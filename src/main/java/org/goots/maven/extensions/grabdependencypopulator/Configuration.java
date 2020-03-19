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

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.ToString;

import java.util.Collections;
import java.util.List;
import java.util.Properties;

/*

 <?xml version="1.0" encoding="utf-8"?>
 <configuration>
    <errorOnMismatch>true|false</errorOnMismatch>
    <verifyDependencies>true|false</verifyDependencies>
    <atEnd>true|false</atEnd>

    <!-- Either _all_ from root (excluding target) OR use include -->
    <include>
        <directory>one</directory>
        <directory>two</directory>
    </include>

  </configuration>
 */
@ToString
public class Configuration
{

    @SuppressWarnings( "FieldCanBeLocal" )
    private static final String ERROR_ON_MISMATCH = "grabPopulatorErrorOnMismatch";

    @SuppressWarnings( "FieldCanBeLocal" )
    private static final String ADD_AT_END = "grabPopulatorAddAtEnd";

    @SuppressWarnings( "FieldCanBeLocal" )
    private static final String VERIFY_DEPS = "grabPopulatorVerifyDependencies";

    @Getter
    private boolean errorOnMismatch = true;

    @Getter
    private boolean verifyDependencies = true;

    @Getter
    private boolean atEnd = true;

    @JacksonXmlElementWrapper(localName = "include")
    @JacksonXmlProperty( localName = "directory")
    @Getter
    private final List<String> directories = Collections.emptyList();


    public void updateConfiguration( Properties sProperties, Properties uProperties )
    {
        Properties unified = new Properties();
        unified.putAll( sProperties );
        unified.putAll( uProperties );

        if ( unified.containsKey( ERROR_ON_MISMATCH ) )
        {
            errorOnMismatch = Boolean.parseBoolean( unified.getProperty( ERROR_ON_MISMATCH ) );
        }
        if ( unified.containsKey( ADD_AT_END ) )
        {
            atEnd = Boolean.parseBoolean( unified.getProperty( ADD_AT_END ) );
        }
        if ( unified.containsKey( VERIFY_DEPS ))
        {
            verifyDependencies = Boolean.parseBoolean( unified.getProperty( VERIFY_DEPS ) );
        }
    }
}
