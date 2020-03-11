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