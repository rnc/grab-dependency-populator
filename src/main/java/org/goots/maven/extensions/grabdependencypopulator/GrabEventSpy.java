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

import org.apache.maven.eventspy.AbstractEventSpy;
import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.project.MavenProject;
import org.commonjava.maven.ext.common.ManipulationException;
import org.commonjava.maven.ext.common.ManipulationUncheckedException;
import org.commonjava.maven.ext.common.util.ManifestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.IOException;

import static org.apache.maven.execution.ExecutionEvent.Type.SessionStarted;

@SuppressWarnings( "unused" )
@Named
@Singleton
public class GrabEventSpy
                extends AbstractEventSpy
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    @SuppressWarnings( "FieldCanBeLocal" )
    private final String DISABLE_GRAB_EXTENSION = "grab.extension.disable";

    private final GrabParser grabParser;

    @SuppressWarnings( "unused" )
    @Inject
    public GrabEventSpy( GrabParser lp)
    {
        this.grabParser = lp;
    }

    @Override
    public void onEvent( Object event )
    {
        if ( isEventSpyDisabled() )
        {
            return;
        }

        if ( event instanceof ExecutionEvent )
        {
            final ExecutionEvent ee = (ExecutionEvent) event;
            final ExecutionEvent.Type type = ee.getType();

            if ( type == SessionStarted)
            {
                try
                {
                    logger.info( "Activating GrabDependencyPopulator extension {}", ManifestUtils.getManifestInformation(GrabEventSpy.class) );
                    MavenProject p = ee.getProject();
                    grabParser.setErrorOnMismatch( Boolean.parseBoolean( ee.getSession()
                                                                           .getSystemProperties()
                                                                           .getProperty( "grabPopulatorErrorOnMismatch",
                                                                                         "false" ) ) );
                    grabParser.searchGroovyFiles( p.getBasedir() );

                    if ( grabParser.getDependencies().size() > 0 )
                    {
                        logger.info( "Adding to project the dependencies {} ", grabParser.getDependencies().values() );
                        p.getModel().getDependencies().addAll( grabParser.getDependencies().values() );
                    }
                    if ( grabParser.getRepositories().size() > 0 )
                    {
                        logger.info( "Adding to project the dependencies {} ", grabParser.getRepositories() );
                        p.getModel().getRepositories().addAll( grabParser.getRepositories() );
                    }
                }
                catch ( ManipulationUncheckedException e )
                {
                    ee.getSession().getResult().addException( e.getCause() );
                }
                catch ( IOException | ManipulationException e )
                {
                    ee.getSession().getResult().addException( e );
                }
            }
        }
    }


    private boolean isEventSpyDisabled()
    {
        return "true".equalsIgnoreCase( System.getProperty( DISABLE_GRAB_EXTENSION ) ) || "true".equalsIgnoreCase(
                        System.getenv( DISABLE_GRAB_EXTENSION ) );
    }
}
