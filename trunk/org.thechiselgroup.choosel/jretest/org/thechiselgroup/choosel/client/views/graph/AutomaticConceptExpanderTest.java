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
package org.thechiselgroup.choosel.client.views.graph;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.thechiselgroup.choosel.client.test.ResourcesTestHelper.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.client.domain.ncbo.NCBO;
import org.thechiselgroup.choosel.client.domain.ncbo.NcboUriHelper;
import org.thechiselgroup.choosel.client.error_handling.ErrorHandler;
import org.thechiselgroup.choosel.client.resources.DefaultResourceSet;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.test.MockitoGWTBridge;
import org.thechiselgroup.choosel.client.views.ViewContentDisplayCallback;

public class AutomaticConceptExpanderTest {

    private ResourceSet allResources;

    @Mock
    private ViewContentDisplayCallback callback;

    private Resource concept1;

    private Resource concept2;

    @Mock
    private NeighbourhoodServiceAsync mappingNeighbourhoodService;

    @Mock
    private ErrorHandler errorHandler;

    private AutomaticConceptExpander underTest;

    @Mock
    private GraphNodeExpansionCallback expansionCallback;

    @Test
    public void addNeighbourhoodArcWhenAddingConceptReferedFromCurrentConcepts() {
	concept1 = createResource(NcboUriHelper.NCBO_CONCEPT, 1);
	concept2 = createResource(NcboUriHelper.NCBO_CONCEPT, 2);
	allResources.add(concept1);

	when(callback.containsResourceWithUri(concept1.getUri())).thenReturn(
		true);

	concept1.getUriListValue(
		NCBO.CONCEPT_NEIGHBOURHOOD_DESTINATION_CONCEPTS).add(
		concept2.getUri());

	underTest.expand(concept2, expansionCallback);

	ArgumentCaptor<String> sourceArgument = ArgumentCaptor
		.forClass(String.class);
	ArgumentCaptor<String> destArgument = ArgumentCaptor
		.forClass(String.class);
	verify(expansionCallback, times(1)).createArc(
		eq(NCBO.CONCEPT_NEIGHBOURHOOD_DESTINATION_CONCEPTS),
		sourceArgument.capture(), destArgument.capture());
	assertEquals(concept1.getUri(), sourceArgument.getValue());
	assertEquals(concept2.getUri(), destArgument.getValue());
    }

    @Test
    public void addNeighbourhoodArcWhenAddingConceptReferringCurrentConcepts() {
	concept1 = createResource(NcboUriHelper.NCBO_CONCEPT, 1);
	concept2 = createResource(NcboUriHelper.NCBO_CONCEPT, 2);

	when(callback.containsResourceWithUri(concept1.getUri())).thenReturn(
		true);

	concept2.getUriListValue(
		NCBO.CONCEPT_NEIGHBOURHOOD_DESTINATION_CONCEPTS).add(
		concept1.getUri());

	underTest.expand(concept2, expansionCallback);

	ArgumentCaptor<String> sourceArgument = ArgumentCaptor
		.forClass(String.class);
	ArgumentCaptor<String> destArgument = ArgumentCaptor
		.forClass(String.class);
	verify(expansionCallback, times(1)).createArc(
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

	when(callback.getAllResources()).thenReturn(allResources);
	when(expansionCallback.getCategory(any(Resource.class))).thenReturn(
		NcboUriHelper.NCBO_CONCEPT);
	when(expansionCallback.getCallback()).thenReturn(callback);
    }

    @After
    public void tearDown() {
	MockitoGWTBridge.tearDown();
    }

}
