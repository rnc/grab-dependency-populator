package org.goots.maven.extensions.grabdependencypopulator;

import org.apache.commons.lang.reflect.FieldUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.Properties;

public class ConfigurationTest
{
    @Test
    public void verifyUserProps() throws IllegalAccessException
    {
        Properties user = new Properties();
        Configuration c = new Configuration();

        user.put( FieldUtils.getDeclaredField( Configuration.class, "ADD_AT_END", true ).get( c ), "true" );
        c.init( Collections.EMPTY_MAP, user );
        Assert.assertTrue ( c.isAtEnd());
    }

}