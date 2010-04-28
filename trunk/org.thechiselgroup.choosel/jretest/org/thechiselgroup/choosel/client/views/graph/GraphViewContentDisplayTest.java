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
import org.thechiselgroup.choosel.client.command.CommandManager;
import org.thechiselgroup.choosel.client.command.UndoableCommand;
import org.thechiselgroup.choosel.client.domain.ncbo.NCBO;
import org.thechiselgroup.choosel.client.domain.ncbo.NcboUriHelper;
import org.thechiselgroup.choosel.client.error_handling.ErrorHandler;
import org.thechiselgroup.choosel.client.geometry.Point;
import org.thechiselgroup.choosel.client.resolver.PropertyValueResolver;
import org.thechiselgroup.choosel.client.resources.DefaultResourceSet;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceManager;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ui.DetailsWidgetHelper;
import org.thechiselgroup.choosel.client.test.MockitoGWTBridge;
import org.thechiselgroup.choosel.client.ui.popup.PopupManager;
import org.thechiselgroup.choosel.client.ui.popup.PopupManagerFactory;
import org.thechiselgroup.choosel.client.ui.widget.graph.Arc;
import org.thechiselgroup.choosel.client.ui.widget.graph.GraphWidgetReadyEvent;
import org.thechiselgroup.choosel.client.ui.widget.graph.GraphWidgetReadyHandler;
import org.thechiselgroup.choosel.client.ui.widget.graph.Node;
import org.thechiselgroup.choosel.client.ui.widget.graph.NodeDragEvent;
import org.thechiselgroup.choosel.client.ui.widget.graph.NodeDragHandler;
import org.thechiselgroup.choosel.client.views.DragEnablerFactory;
import org.thechiselgroup.choosel.client.views.Layer;
import org.thechiselgroup.choosel.client.views.SlotResolver;
import org.thechiselgroup.choosel.client.views.ViewContentDisplayCallback;
import org.thechiselgroup.choosel.client.views.graph.GraphViewContentDisplay.Display;

public class GraphViewContentDisplayTest {

    public class TestGraphViewContentDisplay extends GraphViewContentDisplay {

	public TestGraphViewContentDisplay(Display display,
		ResourceSet hoverModel, SlotResolver slotResolver,
		NeighbourhoodServiceAsync mappingService,
		NeighbourhoodServiceAsync conceptNeighbourhoodService,
		PopupManagerFactory popupManagerFactory,
		DetailsWidgetHelper detailsWidgetHelper,
		CommandManager commandManager, ResourceManager resourceManager,
		ErrorHandler errorHandler, DragEnablerFactory dragEnablerFactory) {

	    super(display, hoverModel, slotResolver, mappingService,
		    conceptNeighbourhoodService, popupManagerFactory,
		    detailsWidgetHelper, commandManager, resourceManager,
		    errorHandler, dragEnablerFactory);
	}

	@Override
	protected PopupManager createPopupManager(Resource concept,
		PropertyValueResolver resolver) {
	    return popupManager;
	}

	@Override
	protected void expandConceptNeighbourhood(Resource resource) {
	}

	@Override
	protected void expandMappingNeighbourhood(Resource resource) {
	}
    }

    private ResourceSet allResources;

    @Mock
    private ViewContentDisplayCallback callback;

    @Mock
    private CommandManager commandManager;

    private Resource concept1;

    private Resource concept2;

    @Mock
    private SlotResolver slotResolver;

    @Mock
    private NeighbourhoodServiceAsync conceptNeighbourhoodService;

    private GraphViewContentDisplay contentDisplay;

    @Mock
    private DetailsWidgetHelper detailsWidgetHelper;

    @Mock
    private Display display;

    @Mock
    private DragEnablerFactory dragEnablerFactory;

    @Mock
    private ErrorHandler errorHandler;

    @Mock
    private ResourceSet hoverModel;

    @Mock
    private Layer layer;

    @Mock
    private NeighbourhoodServiceAsync mappingService;

    @Mock
    private Node node;

    @Mock
    protected PopupManager popupManager;

    @Mock
    private PopupManagerFactory popupManagerFactory;

    @Mock
    private ResourceManager resourceManager;

    private Point sourceLocation;

    private Point targetLocation;

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

	contentDisplay.createResourceItem(layer, concept1);
	contentDisplay.createResourceItem(layer, concept2);

	ArgumentCaptor<Arc> argument = ArgumentCaptor.forClass(Arc.class);
	verify(display, times(1)).addArc(argument.capture());
	assertEquals(concept1.getUri(), argument.getValue().getSourceNodeId());
	assertEquals(concept2.getUri(), argument.getValue().getTargetNodeId());
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

	contentDisplay.createResourceItem(layer, concept1);
	contentDisplay.createResourceItem(layer, concept2);

	ArgumentCaptor<Arc> argument = ArgumentCaptor.forClass(Arc.class);
	verify(display, times(1)).addArc(argument.capture());
	assertEquals(concept2.getUri(), argument.getValue().getSourceNodeId());
	assertEquals(concept1.getUri(), argument.getValue().getTargetNodeId());
    }

    /*
     * Test case: node drag event gets fired, test that correct move command is
     * added to the command manager
     */
    @Test
    public void createNodeMoveCommandWhenNodeDragged() {
	ArgumentCaptor<NodeDragHandler> argument1 = ArgumentCaptor
		.forClass(NodeDragHandler.class);

	verify(display, times(1)).addEventHandler(eq(NodeDragEvent.TYPE),
		argument1.capture());

	NodeDragHandler nodeDragHandler = argument1.getValue();
	nodeDragHandler.onDrag(new NodeDragEvent(node, sourceLocation.x,
		sourceLocation.y, targetLocation.x, targetLocation.y));

	ArgumentCaptor<UndoableCommand> argument2 = ArgumentCaptor
		.forClass(UndoableCommand.class);

	verify(commandManager, times(1))
		.addExecutedCommand(argument2.capture());

	UndoableCommand command = argument2.getValue();

	assertEquals(true, command instanceof MoveNodeCommand);

	MoveNodeCommand command2 = (MoveNodeCommand) command;

	assertEquals(node, command2.getNode());
	assertEquals(sourceLocation, command2.getSourceLocation());
	assertEquals(targetLocation, command2.getTargetLocation());
	assertEquals(display, command2.getGraphDisplay());
    }

    @Test
    public void doNotLoadNeighbourhoodWhenAddingConceptWithLoadedNeighbourhood() {

    }

    // TODO current work
    @Test
    public void loadNeighbourhoodWhenAddingConcept() {
	concept1 = createResource(1);

	contentDisplay.createResourceItem(layer, concept1);

	// verify(contentDisplay, times(1)).expandNeighbourhood(eq(concept1));
    }

    @Before
    public void setUp() throws Exception {
	MockitoGWTBridge.setUp();
	MockitoAnnotations.initMocks(this);

	sourceLocation = new Point(10, 15);
	targetLocation = new Point(20, 25);

	allResources = new DefaultResourceSet();

	when(callback.getAllResources()).thenReturn(allResources);

	contentDisplay = spy(new TestGraphViewContentDisplay(display,
		hoverModel, slotResolver, mappingService,
		conceptNeighbourhoodService, popupManagerFactory,
		detailsWidgetHelper, commandManager, resourceManager,
		errorHandler, dragEnablerFactory));

	contentDisplay.init(callback);

	ArgumentCaptor<GraphWidgetReadyHandler> argument = ArgumentCaptor
		.forClass(GraphWidgetReadyHandler.class);
	verify(display).addGraphWidgetReadyHandler(argument.capture());
	argument.getValue().onWidgetReady(new GraphWidgetReadyEvent(null));
    }

    @After
    public void tearDown() {
	MockitoGWTBridge.tearDown();
    }

}
