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
import org.apache.maven.eventspy.AbstractEventSpy;
import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Repository;
import org.apache.maven.project.MavenProject;
import org.commonjava.maven.ext.common.ManipulationException;
import org.commonjava.maven.ext.common.ManipulationUncheckedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.maven.execution.ExecutionEvent.Type.SessionStarted;

@SuppressWarnings( "unused" )
@Named
@Singleton
public class GrabEventSpy
                extends AbstractEventSpy
{
    @SuppressWarnings( "FieldCanBeLocal" )
    private static final String DISABLE_GRAB_EXTENSION = "grabPopulatorDisable";

    private final Logger logger = LoggerFactory.getLogger( getClass() );

    private final ObjectMapper mapper = new XmlMapper();

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
        if ( "true".equalsIgnoreCase( System.getProperty( DISABLE_GRAB_EXTENSION ) ) || "true".equalsIgnoreCase(
                        System.getenv( DISABLE_GRAB_EXTENSION ) ) )
        {
            return;
        }

        if ( event instanceof ExecutionEvent )
        {
            final ExecutionEvent ee = (ExecutionEvent) event;
            final ExecutionEvent.Type type = ee.getType();
            final Configuration config;

            if ( type == SessionStarted)
            {
                try
                {
                    File configurationFle = new File ( ee.getSession().getRequest().getBaseDirectory(), ".mvn" + File.separator + "grabDependencyPopulator.xml");
                    logger.debug ("Checking for {} ", configurationFle);
                    if ( configurationFle.exists() )
                    {
                        config = mapper.readValue(configurationFle , Configuration.class );
                    }
                    else
                    {
                        config = new Configuration();
                    }
                    config.updateConfiguration (ee.getSession().getSystemProperties(), ee.getSession().getUserProperties());

                    logger.info( "Activating GrabDependencyPopulator extension {}", ManifestUtils.getManifestInformation() );

                    MavenProject p = ee.getProject();

                    grabParser.setErrorOnMismatch( config.isErrorOnMismatch() );
                    grabParser.searchGroovyFiles( p.getBasedir(), config.getDirectories() );

                    if ( grabParser.getDependencies().size() > 0 )
                    {
                        if ( config.isVerifyDependencies() )
                        {
                            if ( p.getModel().getDependencyManagement() != null )
                            {
                                verifyDeps( false, grabParser.getDependencies().values(), p.getModel().getDependencyManagement().getDependencies() );
                            }
                            verifyDeps( true, grabParser.getDependencies().values(), p.getModel().getDependencies() );
                        }

                        if ( logger.isInfoEnabled() )
                        {
                            String repoList = grabParser.getDependencies()
                                                        .values()
                                                        .stream()
                                                        .sorted( Comparator.comparing( Dependency::getGroupId ))
                                                        .map( v -> "\t" + v )
                                                        .collect( Collectors.joining( System.lineSeparator() ) );
                            logger.info( "Adding to project the dependencies{}{}", System.lineSeparator(), repoList );
                        }
                        p.getModel().getDependencies().addAll(
                                        config.isAtEnd() ? p.getModel().getDependencies().size() : 0,
                                        grabParser.getDependencies().values() );
                    }
                    if ( grabParser.getRepositories().size() > 0 )
                    {
                        String repoList = grabParser.getRepositories()
                                                    .stream()
                                                    .sorted( Comparator.comparing( Repository::getId ))
                                                    .map( r -> "\t" + r.getId() + " -> " + r.getUrl() )
                                                    .collect( Collectors.joining( System.lineSeparator() ) );
                        logger.info( "Adding to project the repositories{}{}", System.lineSeparator(), repoList );
                    }
                }
                catch ( ManipulationUncheckedException e )
                {
                    ee.getSession().getResult().addException( e.getCause() );
                }
                catch ( IOException e )
                {
                    logger.error( "Caught ", e );
                    ee.getSession().getResult().addException( new ManipulationException( "Error searching groovy files" , e ) );
                }
            }
        }
    }

    private void verifyDeps( boolean warnOnMatch, Collection<Dependency> grabbedDeps, List<Dependency> targetDeps )
    {
        grabbedDeps.forEach( d -> targetDeps.forEach( t -> {
            if ( t.getGroupId().equals( d.getGroupId() ) &&
                            t.getArtifactId().equals( d.getArtifactId() ) )
            {
                if ( !d.getVersion().equals( t.getVersion() ) )
                {
                    logger.error( "Mismatched version between Grab ({}) and native dependency ({})", d, t );
                    throw new ManipulationUncheckedException( new ManipulationException(
                                                              "Mismatched version between Grab and native dependencies." + d + " and " + t ) );
                }
                else if ( warnOnMatch )
                {
                    logger.warn( "Duplicate dependency definition between Grab ({}) and native dependency ({})", d, t );
                }
            }
        } ) );
    }
}
