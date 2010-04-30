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

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.thechiselgroup.choosel.client.domain.ncbo.NCBO;
import org.thechiselgroup.choosel.client.domain.ncbo.NcboUriHelper;
import org.thechiselgroup.choosel.client.error_handling.ErrorHandler;
import org.thechiselgroup.choosel.client.resources.DefaultResourceSet;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.ui.widget.graph.Node;
import org.thechiselgroup.choosel.client.views.ViewContentDisplayCallback;
import org.thechiselgroup.choosel.client.views.graph.GraphViewContentDisplay.Display;

public class MappingNeighbourhoodCallbackTest {

    private static final String TEST_MAPPING_ID = "mapping-id";

    private static final String TEST_CONCEPT_ONTOLOGY_ID = "ontology_id";

    private static final String TEST_CONCEPT_ONTOLOGY_ID2 = "ontology_id2";

    private static final String TEST_CONCEPT_2_SHORT_ID = "shortid2";

    private static final String TEST_CONCEPT_SHORT_ID = "shortid";

    @Mock
    private ViewContentDisplayCallback callback;

    @Mock
    private Display graphDisplay;

    @Mock
    private GraphItem concept2GraphItem;

    @Mock
    private GraphItem mappingGraphItem;

    @Mock
    private Node concept2Node;

    @Mock
    private Node mappingNode;

    private Resource inputConcept;

    private Resource concept2;

    private ResourceSet availableResources;

    @Mock
    private GraphViewContentDisplay view;

    private MappingNeighbourhoodCallback underTest;

    private NeighbourhoodServiceResult result;

    private Resource mapping;

    private String inputConceptUri;

    private String concept2Uri;

    private String mappingUri;

    @Mock
    private ErrorHandler errorHandler;

    @Mock
    private GraphNodeExpansionCallback expansionCallback;

    @Test
    public void addMappingArcs() {
	underTest.onSuccess(result);

	ArgumentCaptor<String> sourceArgument = ArgumentCaptor
		.forClass(String.class);
	ArgumentCaptor<String> destArgument = ArgumentCaptor
		.forClass(String.class);

	verify(expansionCallback, times(2)).createArc(
		eq(GraphViewContentDisplay.ARC_TYPE_MAPPING),
		sourceArgument.capture(), destArgument.capture());

	assertEquals(inputConceptUri, sourceArgument.getAllValues().get(0));
	assertEquals(mappingUri, destArgument.getAllValues().get(0));

	assertEquals(mappingUri, sourceArgument.getAllValues().get(1));
	assertEquals(concept2Uri, destArgument.getAllValues().get(1));
    }

    @Test
    public void doNotAddDuplicateArcs() {
	when(
		graphDisplay.containsArc(expansionCallback.getArcId(
			GraphViewContentDisplay.ARC_TYPE_MAPPING, mappingUri,
			concept2Uri))).thenReturn(true);

	underTest.onSuccess(result);

	ArgumentCaptor<String> sourceArgument = ArgumentCaptor
		.forClass(String.class);
	ArgumentCaptor<String> destArgument = ArgumentCaptor
		.forClass(String.class);

	verify(expansionCallback, times(1)).createArc(
		eq(GraphViewContentDisplay.ARC_TYPE_MAPPING),
		sourceArgument.capture(), destArgument.capture());

	assertEquals(inputConceptUri, sourceArgument.getAllValues().get(0));
	assertEquals(mappingUri, destArgument.getAllValues().get(0));
    }

    @Before
    public void setUp() {
	MockitoAnnotations.initMocks(this);

	view.init(callback);

	availableResources = spy(new DefaultResourceSet());

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

	mappingUri = NcboUriHelper.toMappingURI(TEST_MAPPING_ID);
	mapping = new Resource(mappingUri);
	mapping.putValue(NCBO.MAPPING_ID, TEST_MAPPING_ID);
	mapping.putValue(NCBO.MAPPING_SOURCE, inputConceptUri);
	mapping.putValue(NCBO.MAPPING_DESTINATION, concept2Uri);

	when(view.getCallback()).thenReturn(callback);
	when(availableResources.getByUri(any(String.class))).thenReturn(
		inputConcept);
	when(callback.getResourceItem(concept2)).thenReturn(concept2GraphItem);
	when(callback.getResourceItem(mapping)).thenReturn(mappingGraphItem);
	when(concept2GraphItem.getNode()).thenReturn(concept2Node);
	when(mappingGraphItem.getNode()).thenReturn(mappingNode);

	result = new NeighbourhoodServiceResult(inputConcept);
	result.addRelationship(inputConcept, mapping);
	result.addRelationship(mapping, concept2);

	availableResources.add(inputConcept);
	availableResources.add(concept2);

	when(callback.getAutomaticResourceSet()).thenReturn(availableResources);

	when(callback.containsResourceWithUri(any(String.class))).thenAnswer(
		new Answer<Boolean>() {
		    @Override
		    public Boolean answer(InvocationOnMock invocation)
			    throws Throwable {
			return availableResources
				.containsResourceWithUri((String) invocation
					.getArguments()[0]);
		    }
		});

	when(
		expansionCallback.getArcId(any(String.class),
			any(String.class), any(String.class))).thenAnswer(
		new Answer<String>() {
		    @Override
		    public String answer(InvocationOnMock invocation)
			    throws Throwable {

			return invocation.getArguments()[0] + ":"
				+ invocation.getArguments()[1] + "_"
				+ invocation.getArguments()[2];
		    }
		});

	underTest = new MappingNeighbourhoodCallback(graphDisplay, callback,
		errorHandler, expansionCallback);
    }
}
