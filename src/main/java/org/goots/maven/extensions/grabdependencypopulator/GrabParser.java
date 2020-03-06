/*
 * Copyright (C) 2020 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.goots.maven.extensions.grabdependencypopulator;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Repository;
import org.commonjava.maven.atlas.ident.ref.ProjectRef;
import org.commonjava.maven.atlas.ident.ref.SimpleProjectRef;
import org.commonjava.maven.ext.common.ManipulationUncheckedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GrabParser
{
    private static final Pattern GRAB_PATTERN_SINGLE = Pattern.compile( "@Grab.*[\"'](.+):(.+):(.+)[\"'].*" );

    private static final Pattern GRAB_PATTERN_MULTIPLE = Pattern.compile( "@Grab.*group=[\"'](.+)[\"'].*module=[\"'](.+)[\"'].*version=[\"'](.+)[\"'].*" );

    private static final Pattern GRAB_RESOLVER_PATTERN_MULTIPLE = Pattern.compile( "@GrabResolver.*name=[\"'](.+)[\"'].*root=[\"'](.+)[\"'].*" );

    private final Logger logger = LoggerFactory.getLogger( getClass() );

    /**
     * Determines whether to throw an error or print a warning if multiple dependencies have mismatched versions.
     */
    @Setter
    private boolean errorOnMismatch;

    @Getter
    private final HashMap<ProjectRef, Dependency> dependencies = new HashMap<>(  );

    @Getter
    private final Set<Repository> repositories = new HashSet<>( );

    @SneakyThrows
    private void searchFile ( Path target)
    {
        logger.debug( "Processing {}", target );
        Files.readAllLines( target ).stream().filter( s -> s.contains( "@Grab" ) ).forEach( s -> {
            // Strip all whitespace as it makes the matching simpler.
            s = s.replaceAll( "\\s+", "" );
            Matcher gs = GRAB_PATTERN_SINGLE.matcher( s );
            Matcher gm = GRAB_PATTERN_MULTIPLE.matcher( s );
            Matcher gr = GRAB_RESOLVER_PATTERN_MULTIPLE.matcher( s );
            if ( gs.matches() )
            {
                Dependency d = processDependency( dependencies, gs.group( 1 ), gs.group( 2 ), gs.group( 3 ) );
                logger.debug( "Matched {} and got version {}", s, d );
            }
            else if ( gm.matches() )
            {
                Dependency d = processDependency( dependencies, gm.group( 1 ), gm.group( 2 ), gm.group( 3 ) );
                logger.debug( "Matched {} and got version {}", s, d );
            }
            else if ( gr.matches() )
            {
                Repository r = new Repository();
                r.setId( gr.group( 1 ) );
                r.setUrl( gr.group( 2 ) );
                repositories.add( r );
            }
            else
            {
                logger.debug( "No match for {}", s );
            }
        } );
    }

    private Dependency processDependency( HashMap<ProjectRef, Dependency> result,
                                          String group, String artifact, String version )
    {
        Dependency d = new Dependency();
        d.setGroupId( group );
        d.setArtifactId( artifact );
        d.setVersion( version );
        ProjectRef pr = new SimpleProjectRef(group, artifact);

        Dependency existing = result.get( pr );
        if ( existing != null && ! version.equals( existing.getVersion() ) )
        {
            if ( errorOnMismatch )
            {
                logger.error( "Multiple dependencies with different versions detected: {} versus {}", d,
                             result.get( pr ) );
                throw new ManipulationUncheckedException(
                    "GrabDependencyPopulator failed due to a version clash (" + d + " versus " + result.get( pr ) + ")");
            }
            else
            {
                logger.warn( "Multiple dependencies with different versions detected: {} versus {}", d,
                             result.get( pr ) );
            }
        }

        result.put( pr, d );
        return d;
    }

    public void searchGroovyFiles( File root) throws IOException {
        Files.walk(root.toPath()).
                filter(Files::isRegularFile).
                filter(f -> f.toString().endsWith(".groovy")).
                forEach( this::searchFile );
    }
}
