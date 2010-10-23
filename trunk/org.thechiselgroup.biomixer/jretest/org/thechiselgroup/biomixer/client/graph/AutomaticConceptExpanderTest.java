/*******************************************************************************
 * Copyright 2009, 2010 Lars Grammel 
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0 
 *     
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.  
 *******************************************************************************/
package org.thechiselgroup.biomixer.client.graph;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.thechiselgroup.choosel.client.test.TestResourceSetFactory.createResource;
import static org.thechiselgroup.choosel.client.test.TestResourceSetFactory.toResourceSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.biomixer.client.NCBO;
import org.thechiselgroup.biomixer.client.NcboUriHelper;
import org.thechiselgroup.choosel.client.error_handling.ErrorHandler;
import org.thechiselgroup.choosel.client.resources.DefaultResourceSet;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.test.MockitoGWTBridge;
import org.thechiselgroup.choosel.client.views.ResourceItem;
import org.thechiselgroup.choosel.client.views.graph.GraphNodeExpansionCallback;
import org.thechiselgroup.choosel.client.views.graph.NeighbourhoodServiceAsync;

public class AutomaticConceptExpanderTest {

    private ResourceSet allResources;

    private Resource concept1;

    private Resource concept2;

    @Mock
    private NeighbourhoodServiceAsync mappingNeighbourhoodService;

    @Mock
    private ErrorHandler errorHandler;

    private AutomaticConceptExpander underTest;

    @Mock
    private GraphNodeExpansionCallback expansionCallback;

    @Mock
    private ResourceItem resourceItem;

    @Test
    public void addNeighbourhoodArcWhenAddingConceptReferedFromCurrentConcepts() {
        concept1 = createResource(NcboUriHelper.NCBO_CONCEPT, 1);
        concept2 = createResource(NcboUriHelper.NCBO_CONCEPT, 2);
        allResources.add(concept1);

        when(expansionCallback.containsResourceWithUri(concept1.getUri()))
                .thenReturn(true);

        concept1.getUriListValue(
                NCBO.CONCEPT_NEIGHBOURHOOD_DESTINATION_CONCEPTS).add(
                concept2.getUri());

        when(resourceItem.getResourceSet()).thenReturn(toResourceSet(concept2));

        underTest.expand(resourceItem, expansionCallback);

        ArgumentCaptor<String> sourceArgument = ArgumentCaptor
                .forClass(String.class);
        ArgumentCaptor<String> destArgument = ArgumentCaptor
                .forClass(String.class);
        verify(expansionCallback, times(1)).showArc(
                eq(NCBO.CONCEPT_NEIGHBOURHOOD_DESTINATION_CONCEPTS),
                sourceArgument.capture(), destArgument.capture());
        assertEquals(concept1.getUri(), sourceArgument.getValue());
        assertEquals(concept2.getUri(), destArgument.getValue());
    }

    @Test
    public void addNeighbourhoodArcWhenAddingConceptReferringCurrentConcepts() {
        concept1 = createResource(NcboUriHelper.NCBO_CONCEPT, 1);
        concept2 = createResource(NcboUriHelper.NCBO_CONCEPT, 2);

        when(expansionCallback.containsResourceWithUri(concept1.getUri()))
                .thenReturn(true);

        concept2.getUriListValue(
                NCBO.CONCEPT_NEIGHBOURHOOD_DESTINATION_CONCEPTS).add(
                concept1.getUri());

        when(resourceItem.getResourceSet()).thenReturn(toResourceSet(concept2));

        underTest.expand(resourceItem, expansionCallback);

        ArgumentCaptor<String> sourceArgument = ArgumentCaptor
                .forClass(String.class);
        ArgumentCaptor<String> destArgument = ArgumentCaptor
                .forClass(String.class);
        verify(expansionCallback, times(1)).showArc(
                eq(NCBO.CONCEPT_NEIGHBOURHOOD_DESTINATION_CONCEPTS),
                sourceArgument.capture(), destArgument.capture());
        assertEquals(concept2.getUri(), sourceArgument.getValue());
        assertEquals(concept1.getUri(), destArgument.getValue());
    }

    @Before
    public void setUp() throws Exception {
        MockitoGWTBridge.setUp();
        MockitoAnnotations.initMocks(this);

        underTest = new AutomaticConceptExpander(mappingNeighbourhoodService,
                errorHandler);

        allResources = new DefaultResourceSet();

        when(expansionCallback.getAllResources()).thenReturn(allResources);
        when(expansionCallback.getCategory(any(Resource.class))).thenReturn(
                NcboUriHelper.NCBO_CONCEPT);
    }

    @After
    public void tearDown() {
        MockitoGWTBridge.tearDown();
    }

}