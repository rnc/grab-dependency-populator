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

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Manifest;

class Utils
{
   /**
     * Retrieves the SHA this was built with.
     *
     * @return the GIT sha of this codebase.
     */
    static String getManifestInformation() throws IOException
    {
        String result = "";

        final Enumeration<URL> resources = Utils.class.getClassLoader().getResources( "META-INF/MANIFEST.MF" );

        while ( resources.hasMoreElements() )
        {
            final URL jarUrl = resources.nextElement();

            if ( jarUrl.getFile().contains( "alt-deploy-" ) )
            {
                final Manifest manifest = new Manifest( jarUrl.openStream() );

                result = manifest.getMainAttributes().getValue( "Implementation-Version" );
                result += " ( SHA: " + manifest.getMainAttributes().getValue( "Scm-Revision" ) + " ) ";
                break;
            }
        }

        return result;
    }
}
