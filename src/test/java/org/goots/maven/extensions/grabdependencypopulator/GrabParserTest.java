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

    @Test
    public void testParse() throws Exception
    {
        final URL resource = Thread.currentThread().getContextClassLoader().getResource( "" );
        if ( resource == null )
        {
            throw new Exception( "Null resource" );
        }

        final File target = new File( resource.getPath() ).getParentFile();

        GrabParser p = new GrabParser();
        HashMap<ProjectRef, Dependency> dependencies = p.searchGroovyFiles( target );

        assertEquals( 2, dependencies.size() );
        assertTrue( dependencies.containsKey( SimpleProjectRef.parse( "org.yaml:snakeyaml" ) ) );
        System.out.println( "Deps : " + dependencies );
    }
}