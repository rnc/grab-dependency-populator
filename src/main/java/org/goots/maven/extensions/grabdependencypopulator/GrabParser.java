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

import lombok.SneakyThrows;
import org.apache.maven.model.Dependency;
import org.commonjava.maven.atlas.ident.ref.ProjectRef;
import org.commonjava.maven.atlas.ident.ref.SimpleProjectRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GrabParser
{
    private static final Pattern GRAB_PATTERN_SINGLE = Pattern.compile( "@Grab.*[\"'](.+):(.+):(.+)[\"'].*" );

    private static final Pattern GRAB_PATTERN_MULTIPLE = Pattern.compile( "@Grab.*group=[\"'](.+)[\"'].*module=[\"'](.+)[\"'].*version=[\"'](.+)[\"'].*" );

    private final Logger logger = LoggerFactory.getLogger( getClass() );

    @SneakyThrows
    private HashMap<ProjectRef, Dependency> searchFile ( Path target)
    {
        HashMap<ProjectRef, Dependency> result = new HashMap<>(  );

        Files.readAllLines( target ).stream().filter( s -> s.contains( "@Grab" ) ).forEach( s -> {
            // Strip all whitespace as it makes the matching simpler.
            s = s.replaceAll( "\\s+", "" );
            Matcher gs = GRAB_PATTERN_SINGLE.matcher( s );
            Matcher gm = GRAB_PATTERN_MULTIPLE.matcher( s );
            if ( gs.matches() )
            {
                Dependency d = processDependency( result, gs.group( 1 ), gs.group( 2 ), gs.group( 3 ) );
                logger.debug( "Matched {} and got version {}", s, d );
            }
            else if ( gm.matches() )
            {
                Dependency d = processDependency( result, gm.group( 1 ), gm.group( 2 ), gm.group( 3 ) );
                logger.debug( "Matched {} and got version {}", s, d );
            }
            else
            {
                logger.debug( "No match for {}", s );
            }
        } );
        return result;
    }

    private Dependency processDependency( HashMap<ProjectRef, Dependency> result,
                                          String group, String artifact, String version )
    {
        Dependency d = new Dependency();
        d.setGroupId( group );
        d.setArtifactId( artifact );
        d.setVersion( version );
        ProjectRef pr = new SimpleProjectRef(group, artifact);

        if ( result.containsKey( pr ) )
        {
            logger.warn ("Multiple dependencies with different versions detected: {} versus {}",
                         d, result.get( pr ));
        }
        result.put( pr, d );
        return d;
    }

    public HashMap<ProjectRef, Dependency> searchGroovyFiles( File root) throws IOException {
        HashMap<ProjectRef, Dependency> results = new HashMap<>(  );
        Files.walk(root.toPath()).
                filter(Files::isRegularFile).
                filter(f -> f.toString().endsWith(".groovy")).
                forEach(f -> results.putAll(searchFile(f)));
        return results;
    }
}
