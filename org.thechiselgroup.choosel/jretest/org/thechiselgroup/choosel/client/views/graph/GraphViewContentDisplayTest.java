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

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.thechiselgroup.choosel.client.test.AdvancedAsserts.assertContentEquals;
import static org.thechiselgroup.choosel.client.test.TestResourceSetFactory.createResource;
import static org.thechiselgroup.choosel.client.test.TestResourceSetFactory.createResources;
import static org.thechiselgroup.choosel.client.test.TestResourceSetFactory.toResourceSet;
import static org.thechiselgroup.choosel.client.util.CollectionUtils.toSet;

import java.util.Collections;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.client.command.CommandManager;
import org.thechiselgroup.choosel.client.command.UndoableCommand;
import org.thechiselgroup.choosel.client.geometry.Point;
import org.thechiselgroup.choosel.client.resolver.ResourceSetToValueResolver;
import org.thechiselgroup.choosel.client.resources.DefaultResourceSet;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceCategorizer;
import org.thechiselgroup.choosel.client.resources.ResourceManager;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ui.DetailsWidgetHelper;
import org.thechiselgroup.choosel.client.test.MockitoGWTBridge;
import org.thechiselgroup.choosel.client.test.TestResourceSetFactory;
import org.thechiselgroup.choosel.client.ui.popup.PopupManager;
import org.thechiselgroup.choosel.client.ui.popup.PopupManagerFactory;
import org.thechiselgroup.choosel.client.ui.widget.graph.Arc;
import org.thechiselgroup.choosel.client.ui.widget.graph.GraphWidgetReadyEvent;
import org.thechiselgroup.choosel.client.ui.widget.graph.GraphWidgetReadyHandler;
import org.thechiselgroup.choosel.client.ui.widget.graph.Node;
import org.thechiselgroup.choosel.client.ui.widget.graph.NodeDragEvent;
import org.thechiselgroup.choosel.client.ui.widget.graph.NodeDragHandler;
import org.thechiselgroup.choosel.client.views.DragEnablerFactory;
import org.thechiselgroup.choosel.client.views.HoverModel;
import org.thechiselgroup.choosel.client.views.ResourceItem;
import org.thechiselgroup.choosel.client.views.ResourceItemValueResolver;
import org.thechiselgroup.choosel.client.views.ViewContentDisplayCallback;
import org.thechiselgroup.choosel.client.views.graph.GraphViewContentDisplay.Display;

public class GraphViewContentDisplayTest {

    public class TestGraphViewContentDisplay extends GraphViewContentDisplay {

        public TestGraphViewContentDisplay(Display display,
                PopupManagerFactory popupManagerFactory,
                DetailsWidgetHelper detailsWidgetHelper,
                CommandManager commandManager, ResourceManager resourceManager,
                DragEnablerFactory dragEnablerFactory,
                ResourceCategorizer resourceCategorizer,
                ArcStyleProvider arcStyleProvider,
                GraphExpansionRegistry registry) {

            super(display, popupManagerFactory, detailsWidgetHelper,
                    commandManager, resourceManager, dragEnablerFactory,
                    resourceCategorizer, arcStyleProvider, registry);
        }

        @Override
        protected PopupManager createPopupManager(ResourceSet resources,
                ResourceSetToValueResolver resolver) {
            return popupManager;
        }

    }

    private static final String RESOURCE_ITEM_CATEGORY = "resourceItemCategory";

    private ResourceSet allResources;

    @Mock
    private ArcStyleProvider arcStyleProvider;

    @Mock
    private GraphNodeExpander automaticExpander;

    @Mock
    private ViewContentDisplayCallback callback;

    @Mock
    private CommandManager commandManager;

    private GraphViewContentDisplay underTest;

    @Mock
    private DetailsWidgetHelper detailsWidgetHelper;

    @Mock
    private Display display;

    @Mock
    private DragEnablerFactory dragEnablerFactory;

    @Mock
    private HoverModel hoverModel;

    @Mock
    private ResourceItemValueResolver layer;

    @Mock
    private Node node;

    @Mock
    protected PopupManager popupManager;

    @Mock
    private PopupManagerFactory popupManagerFactory;

    @Mock
    private GraphExpansionRegistry registry;

    @Mock
    private ResourceCategorizer resourceCategorizer;

    @Mock
    private ResourceManager resourceManager;

    private Point sourceLocation;

    private Point targetLocation;

    @Test
    public void addResourceItemsWithRelationshipAddsArc() {
        ResourceSet resourceSet1 = createResources(1);
        ResourceSet resourceSet2 = createResources(2);

        ResourceItem resourceItem1 = underTest.createResourceItem(layer,
                RESOURCE_ITEM_CATEGORY, resourceSet1, hoverModel);
        ResourceItem resourceItem2 = underTest.createResourceItem(layer,
                RESOURCE_ITEM_CATEGORY, resourceSet2, hoverModel);

        underTest.update(toSet(resourceItem1, resourceItem2),
                Collections.<ResourceItem> emptySet(),
                Collections.<ResourceItem> emptySet());

        String arcId = underTest.getArcId("arcType", resourceSet1
                .getFirstResource().getUri(), resourceSet2.getFirstResource()
                .getUri());

        underTest.showArc("arcType", resourceSet1.getFirstResource().getUri(),
                resourceSet2.getFirstResource().getUri());

        ArgumentCaptor<Arc> captor = ArgumentCaptor.forClass(Arc.class);

        verify(display, times(1)).addArc(captor.capture());
        assertEquals(arcId, captor.getValue().getId());
    }

    @Test
    public void addResourceItemToAllResource() {
        ResourceSet resourceSet = createResources(1);

        ResourceItem resourceItem = underTest.createResourceItem(layer,
                RESOURCE_ITEM_CATEGORY, resourceSet, hoverModel);

        underTest.update(toSet(resourceItem),
                Collections.<ResourceItem> emptySet(),
                Collections.<ResourceItem> emptySet());

        resourceSet.add(createResource(2));

        assertContentEquals(resourceSet, underTest.getAllResources());
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
    public void loadNeighbourhoodWhenAddingConcept() {
        Resource concept1 = createResource(1);

        ResourceItem resourceItem = underTest.createResourceItem(layer,
                RESOURCE_ITEM_CATEGORY, toResourceSet(concept1), hoverModel);

        underTest.update(toSet(resourceItem),
                Collections.<ResourceItem> emptySet(),
                Collections.<ResourceItem> emptySet());

        ArgumentCaptor<ResourceItem> argument = ArgumentCaptor
                .forClass(ResourceItem.class);
        verify(automaticExpander, times(1)).expand(argument.capture(),
                any(GraphNodeExpansionCallback.class));

        ResourceItem result = argument.getValue();
        assertEquals(1, result.getResourceSet().size());
        assertEquals(concept1, result.getResourceSet().getFirstResource());
    }

    @Test
    public void removeResourceItemFromAllResource() {
        ResourceSet resourceSet = createResources(1);

        ResourceItem resourceItem = underTest.createResourceItem(layer,
                RESOURCE_ITEM_CATEGORY, resourceSet, hoverModel);

        underTest.update(toSet(resourceItem),
                Collections.<ResourceItem> emptySet(),
                Collections.<ResourceItem> emptySet());

        underTest.removeResourceItem(resourceItem);

        assertContentEquals(createResources(), underTest.getAllResources());

    }

    @Test
    public void removeResourceItemsWithRelationshipRemovesArc1() {
        ResourceSet resourceSet1 = createResources(1);
        ResourceSet resourceSet2 = createResources(2);

        ResourceItem resourceItem1 = underTest.createResourceItem(layer,
                RESOURCE_ITEM_CATEGORY, resourceSet1, hoverModel);
        ResourceItem resourceItem2 = underTest.createResourceItem(layer,
                RESOURCE_ITEM_CATEGORY, resourceSet2, hoverModel);

        underTest.update(toSet(resourceItem1, resourceItem2),
                Collections.<ResourceItem> emptySet(),
                Collections.<ResourceItem> emptySet());

        underTest.showArc("arcType", resourceSet1.getFirstResource().getUri(),
                resourceSet2.getFirstResource().getUri());

        ArgumentCaptor<Arc> captor = ArgumentCaptor.forClass(Arc.class);

        underTest.removeResourceItem(resourceItem1);

        verify(display, times(1)).removeArc(captor.capture());
    }

    @Test
    public void removeResourceItemsWithRelationshipRemovesArc2() {
        ResourceSet resourceSet1 = createResources(1);
        ResourceSet resourceSet2 = createResources(2);

        ResourceItem resourceItem1 = underTest.createResourceItem(layer,
                RESOURCE_ITEM_CATEGORY, resourceSet1, hoverModel);
        ResourceItem resourceItem2 = underTest.createResourceItem(layer,
                RESOURCE_ITEM_CATEGORY, resourceSet2, hoverModel);

        underTest.update(toSet(resourceItem1, resourceItem2),
                Collections.<ResourceItem> emptySet(),
                Collections.<ResourceItem> emptySet());

        underTest.showArc("arcType", resourceSet1.getFirstResource().getUri(),
                resourceSet2.getFirstResource().getUri());

        ArgumentCaptor<Arc> captor = ArgumentCaptor.forClass(Arc.class);

        underTest.removeResourceItem(resourceItem2);

        verify(display, times(1)).removeArc(captor.capture());
    }

    @Before
    public void setUp() throws Exception {
        MockitoGWTBridge.setUp();
        MockitoAnnotations.initMocks(this);

        sourceLocation = new Point(10, 15);
        targetLocation = new Point(20, 25);

        allResources = new DefaultResourceSet();

        when(callback.getAllResources()).thenReturn(allResources);

        underTest = spy(new TestGraphViewContentDisplay(display,
                popupManagerFactory, detailsWidgetHelper, commandManager,
                resourceManager, dragEnablerFactory, resourceCategorizer,
                arcStyleProvider, registry));

        underTest.init(callback);

        when(resourceCategorizer.getCategory(any(Resource.class))).thenReturn(
                TestResourceSetFactory.DEFAULT_TYPE);

        when(registry.getAutomaticExpander(any(String.class))).thenReturn(
                automaticExpander);

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
