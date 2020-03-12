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
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    private final Configuration config;

    private final GrabParser grabParser;

    @SuppressWarnings( "unused" )
    @Inject
    public GrabEventSpy( GrabParser lp, Configuration c)
    {
        this.grabParser = lp;
        this.config = c;

        config.init( System.getProperties() );
    }

    @Override
    public void onEvent( Object event )
    {
        if ( config.isDisabled() )
        {
            return;
        }

        if ( event instanceof ExecutionEvent )
        {
            final ExecutionEvent ee = (ExecutionEvent) event;
            final ExecutionEvent.Type type = ee.getType();

            if ( type == SessionStarted)
            {
                config.init (ee.getSession().getSystemProperties(), ee.getSession().getUserProperties());
                try
                {
                    logger.info( "Activating GrabDependencyPopulator extension {}", ManifestUtils.getManifestInformation() );
                    MavenProject p = ee.getProject();

                    grabParser.setErrorOnMismatch( config.isErrorOnMismatch() );
                    grabParser.searchGroovyFiles( p.getBasedir() );

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
