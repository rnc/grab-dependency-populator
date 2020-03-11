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

import org.apache.maven.model.Dependency;
import org.commonjava.maven.atlas.ident.ref.ProjectRef;
import org.commonjava.maven.atlas.ident.ref.SimpleProjectRef;
import org.commonjava.maven.ext.common.ManipulationUncheckedException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;

import java.io.File;
import java.net.URL;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GrabParserTest
{
    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog().muteForSuccessfulTests();

    private File target;

    @Before
    public void setup() throws Exception
    {
        final URL resource = Thread.currentThread().getContextClassLoader().getResource( "" );
        if ( resource == null )
        {
            throw new Exception( "Null resource" );
        }
        target = new File( resource.getPath() ).getParentFile();
    }

    @Test
    public void testParseGrab() throws Exception
    {
        GrabParser p = new GrabParser();
        p.searchGroovyFiles( target );
        HashMap<ProjectRef, Dependency> dependencies = p.getDependencies();

        assertEquals( 4, dependencies.size() );
        assertTrue( dependencies.containsKey( SimpleProjectRef.parse( "org.yaml:snakeyaml" ) ) );
        assertTrue( systemOutRule.getLog().contains(
                        "Multiple dependencies with different versions detected: Dependency "
                                        + "{groupId=org.yaml, artifactId=snakeyaml, version=1.01, type=jar} versus "
                                        + "Dependency {groupId=org.yaml, artifactId=snakeyaml, version=1.21, type=jar}" ) );
    }

    @Test
    public void testParseGrabResolver() throws Exception
    {
        GrabParser p = new GrabParser();
        p.searchGroovyFiles( target );

        assertEquals( 2, p.getRepositories().size() );
        assertTrue( p.getRepositories().stream().anyMatch( r -> r.getUrl().contains( "https://jitpack.io/" ) ) );
    }

    @Test(expected = ManipulationUncheckedException.class )
    public void testParseGrabErrorOnClash() throws Exception
    {
        GrabParser p = new GrabParser();
        p.setErrorOnMismatch( true );
        p.searchGroovyFiles( target );
    }
}
