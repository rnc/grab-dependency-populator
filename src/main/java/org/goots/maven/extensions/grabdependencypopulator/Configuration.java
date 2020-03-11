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

import java.util.Collections;
import java.util.Map;
import java.util.Properties;

public class Configuration
{
    @SuppressWarnings( "FieldCanBeLocal" )
    private final String DISABLE_GRAB_EXTENSION = "grabPopulatorDisable";

    @SuppressWarnings( "FieldCanBeLocal" )
    private final String ERROR_ON_MISMATCH = "grabPopulatorErrorOnMismatch";

    @SuppressWarnings( "FieldCanBeLocal" )
    private final String ADD_AT_END = "grabPopulatorAddAtEnd";

    @SuppressWarnings( "FieldCanBeLocal" )
    private final String VERIFY_DEPS = "grabPopulatorVerifyDependencies";

    @Getter
    private boolean isDisabled;

    @Getter
    private boolean errorOnMismatch;

    @Getter
    private boolean verifyDependencies;

    @Getter
    private boolean atEnd;

    public void init( Properties properties )
    {
        init ( properties, Collections.EMPTY_MAP) ;
    }

    public void init( Map sProperties, Map uProperties )
    {
        Properties unified = new Properties();
        unified.putAll( sProperties );
        unified.putAll( uProperties );

        isDisabled = "true".equalsIgnoreCase( unified.getProperty( DISABLE_GRAB_EXTENSION ) ) || "true".equalsIgnoreCase(
                        System.getenv( DISABLE_GRAB_EXTENSION ) );

        errorOnMismatch = Boolean.parseBoolean( unified.getProperty( ERROR_ON_MISMATCH, "true" ) );
        atEnd = Boolean.parseBoolean( unified.getProperty( ADD_AT_END ) );
        verifyDependencies = Boolean.parseBoolean( unified.getProperty( VERIFY_DEPS, "true" ) );
    }
}
