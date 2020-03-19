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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.apache.commons.lang.reflect.FieldUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConfigurationTest
{
    @Test
    public void testXml() throws IOException
    {
        @SuppressWarnings( "ConstantConditions" )
        final File xml = new File( Thread.currentThread()
                                         .getContextClassLoader()
                                         .getResource( "grabDependencyPopulator.xml" )
                                         .getFile() );
        ObjectMapper xmlMapper = new XmlMapper();
        Configuration value = xmlMapper.readValue( xml, Configuration.class );
        assertTrue( value.isErrorOnMismatch() );
        assertFalse( value.isVerifyDependencies() );
        assertEquals( "src", value.getDirectories().get( 0 ) );
    }

    @Test
    public void verifyUserProps() throws IllegalAccessException
    {
        Properties user = new Properties();
        Configuration c = new Configuration();

        user.put( FieldUtils.getDeclaredField( Configuration.class, "ADD_AT_END", true ).get( c ), "false" );
        c.updateConfiguration( new Properties(), user );
        Assert.assertFalse( c.isAtEnd() );
    }

    @Test
    public void testXmlAndProperties() throws IOException, IllegalAccessException
    {
        @SuppressWarnings( "ConstantConditions" )
        final File xml = new File( Thread.currentThread()
                                         .getContextClassLoader()
                                         .getResource( "grabDependencyPopulator.xml" )
                                         .getFile() );
        ObjectMapper xmlMapper = new XmlMapper();
        Configuration config = xmlMapper.readValue( xml, Configuration.class );

        Properties user = new Properties();
        user.put( FieldUtils.getDeclaredField( Configuration.class, "ADD_AT_END", true ).get( config ), "true" );
        config.updateConfiguration( user, new Properties() );

        assertTrue( config.isAtEnd() );
    }
}