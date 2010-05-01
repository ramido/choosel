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

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.biomixer.client.NCBO;
import org.thechiselgroup.biomixer.client.NcboUriHelper;
import org.thechiselgroup.choosel.client.error_handling.ErrorHandler;
import org.thechiselgroup.choosel.client.geometry.Point;
import org.thechiselgroup.choosel.client.resources.DefaultResourceManager;
import org.thechiselgroup.choosel.client.resources.DefaultResourceSet;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceManager;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.ui.widget.graph.GraphDisplay;
import org.thechiselgroup.choosel.client.ui.widget.graph.Node;
import org.thechiselgroup.choosel.client.views.ViewContentDisplayCallback;
import org.thechiselgroup.choosel.client.views.graph.GraphNodeExpansionCallback;
import org.thechiselgroup.choosel.client.views.graph.NeighbourhoodServiceResult;

public class ConceptNeighbourhoodCallbackTest {

    private static final int NODE_LOCATION_X = 20;

    private static final int NODE_LOCATION_Y = 35;

    private static final String TEST_CONCEPT_2_SHORT_ID = "shortid2";

    private static final String TEST_CONCEPT_ONTOLOGY_ID = "ontology_id";

    private static final String TEST_CONCEPT_ONTOLOGY_ID2 = "ontology_id2";

    private static final String TEST_CONCEPT_SHORT_ID = "shortid";

    private ResourceSet automaticResources;

    @Mock
    private ViewContentDisplayCallback callback;

    private Resource concept2;

    @Mock
    private Resource concept2FromResult;

    @Mock
    private Node concept2Node;

    private String concept2Uri;

    @Mock
    private ErrorHandler errorHandler;

    @Mock
    private GraphNodeExpansionCallback expansionCallback;

    @Mock
    private GraphDisplay graphDisplay;

    private Resource inputConcept;

    @Mock
    private Resource inputConceptFromResult;

    private String inputConceptUri;

    @Mock
    private Node inputNode;

    private ConceptNeighbourhoodCallback neighbourhoodCallback;

    private ResourceManager resourceManager;

    private NeighbourhoodServiceResult result;

    @Test
    public void addNodesToDataProvider() {
	neighbourhoodCallback.onSuccess(result);

	verify(automaticResources, times(1)).add(concept2);
	verify(graphDisplay, never()).addNode(any(Node.class));
    }

    @Test
    public void arcsAdded() {
	neighbourhoodCallback.onSuccess(result);

	ArgumentCaptor<String> sourceArgument = ArgumentCaptor
		.forClass(String.class);
	ArgumentCaptor<String> destArgument = ArgumentCaptor
		.forClass(String.class);

	verify(expansionCallback, times(1)).showArc(
		eq(NCBO.CONCEPT_NEIGHBOURHOOD_DESTINATION_CONCEPTS),
		sourceArgument.capture(), destArgument.capture());

	assertEquals(inputConceptUri, sourceArgument.getAllValues().get(0));
	assertEquals(concept2Uri, destArgument.getAllValues().get(0));
    }

    @Test
    public void arcsAdded2() {
	neighbourhoodCallback.onSuccess(result);
	neighbourhoodCallback.onSuccess(result); // call again

	ArgumentCaptor<String> sourceArgument = ArgumentCaptor
		.forClass(String.class);
	ArgumentCaptor<String> destArgument = ArgumentCaptor
		.forClass(String.class);

	verify(expansionCallback, times(2)).showArc(
		eq(NCBO.CONCEPT_NEIGHBOURHOOD_DESTINATION_CONCEPTS),
		sourceArgument.capture(), destArgument.capture());

	assertEquals(inputConceptUri, sourceArgument.getAllValues().get(0));
	assertEquals(concept2Uri, destArgument.getAllValues().get(0));

	assertEquals(inputConceptUri, sourceArgument.getAllValues().get(1));
	assertEquals(concept2Uri, destArgument.getAllValues().get(1));
    }

    @Test
    public void doNotAddDuplicatedNodesToDataProvider() {
	when(callback.containsResourceWithUri(concept2.getUri())).thenReturn(
		Boolean.TRUE);

	neighbourhoodCallback.onSuccess(result);

	verify(automaticResources, never()).add(concept2);
	verify(graphDisplay, never()).addNode(any(Node.class));
    }

    @Test
    public void inputConceptNeighbourhoodLoaded() {
	neighbourhoodCallback.onSuccess(result);

	assertEquals(true, inputConcept.getUriListValue(
		NCBO.CONCEPT_NEIGHBOURHOOD_DESTINATION_CONCEPTS).isLoaded());
    }

    @Test
    public void layOutNewNode() {
	neighbourhoodCallback.onSuccess(result);

	ArgumentCaptor<Collection> argument = ArgumentCaptor
		.forClass(Collection.class);
	verify(graphDisplay, times(1)).layOutNodes(argument.capture());
	assertEquals(1, argument.getValue().size());
	assertEquals(true, argument.getValue().contains(concept2Node));

	verify(graphDisplay, never()).layOut();
    }

    @Test
    public void positionNewNodeOnTopOfSourceNodeBeforeLayout() {
	neighbourhoodCallback.onSuccess(result);

	ArgumentCaptor<Point> locationArgument = ArgumentCaptor
		.forClass(Point.class);
	verify(graphDisplay, times(1)).setLocation(eq(concept2Node),
		locationArgument.capture());
	assertEquals(NODE_LOCATION_X, locationArgument.getValue().x);
	assertEquals(NODE_LOCATION_Y, locationArgument.getValue().y);
    }

    @Before
    public void setUp() {
	MockitoAnnotations.initMocks(this);

	automaticResources = spy(new DefaultResourceSet());
	resourceManager = spy(new DefaultResourceManager());

	inputConceptUri = NcboUriHelper.toConceptURI(TEST_CONCEPT_ONTOLOGY_ID,
		TEST_CONCEPT_SHORT_ID);
	inputConcept = new Resource(inputConceptUri);
	inputConcept.putValue(NCBO.CONCEPT_SHORT_ID, TEST_CONCEPT_SHORT_ID);
	inputConcept.putValue(NCBO.CONCEPT_ONTOLOGY_ID,
		TEST_CONCEPT_ONTOLOGY_ID);

	concept2Uri = NcboUriHelper.toConceptURI(TEST_CONCEPT_ONTOLOGY_ID2,
		TEST_CONCEPT_2_SHORT_ID);
	concept2 = new Resource(concept2Uri);
	concept2.putValue(NCBO.CONCEPT_SHORT_ID, TEST_CONCEPT_2_SHORT_ID);
	concept2.putValue(NCBO.CONCEPT_ONTOLOGY_ID, TEST_CONCEPT_ONTOLOGY_ID2);

	when(automaticResources.getByUri(any(String.class))).thenReturn(
		inputConcept);
	when(graphDisplay.getNode(concept2Uri)).thenReturn(concept2Node);
	when(graphDisplay.getNode(inputConceptUri)).thenReturn(inputNode);
	when(graphDisplay.getLocation(inputNode)).thenReturn(
		new Point(NODE_LOCATION_X, NODE_LOCATION_Y));

	when(inputConceptFromResult.getUri()).thenReturn(inputConceptUri);

	result = new NeighbourhoodServiceResult(inputConceptFromResult);
	result.addRelationship(inputConceptFromResult, concept2FromResult);

	when(resourceManager.add(inputConceptFromResult)).thenReturn(
		inputConcept);
	when(resourceManager.add(concept2FromResult)).thenReturn(concept2);

	automaticResources.add(inputConcept);

	when(callback.getAutomaticResourceSet()).thenReturn(automaticResources);

	neighbourhoodCallback = new ConceptNeighbourhoodCallback(graphDisplay,
		callback, resourceManager, errorHandler, expansionCallback);
    }
}
