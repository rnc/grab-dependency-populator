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
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

class ManifestUtils
{
    /**
     * Retrieves the SHA this was built with.
     *
     * @return the GIT sha of this codebase.
     */
    static String getManifestInformation() throws IOException
    {
        String result;

        final URL jarUrl = ManifestUtils.class.getProtectionDomain().getCodeSource().getLocation();

        try (JarInputStream jarStream = new JarInputStream( jarUrl.openStream() ))
        {
            final Manifest manifest = jarStream.getManifest();
            result = manifest.getMainAttributes().getValue( "Implementation-Version" );
            result += " ( SHA: " + manifest.getMainAttributes().getValue( "Scm-Revision" ) + " ) ";
        }

        return result;
    }
}
