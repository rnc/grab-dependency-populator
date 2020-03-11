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
