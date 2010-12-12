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
import static org.thechiselgroup.choosel.client.test.ResourcesTestHelper.createResourceItem;
import static org.thechiselgroup.choosel.client.test.ResourcesTestHelper.createResourceItems;
import static org.thechiselgroup.choosel.client.test.TestResourceSetFactory.createResource;
import static org.thechiselgroup.choosel.client.test.TestResourceSetFactory.createResources;
import static org.thechiselgroup.choosel.client.test.TestResourceSetFactory.toLabeledResourceSet;
import static org.thechiselgroup.choosel.client.test.TestResourceSetFactory.toResourceSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.client.command.CommandManager;
import org.thechiselgroup.choosel.client.command.UndoableCommand;
import org.thechiselgroup.choosel.client.geometry.Point;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceCategorizer;
import org.thechiselgroup.choosel.client.resources.ResourceManager;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.test.MockitoGWTBridge;
import org.thechiselgroup.choosel.client.test.ResourcesTestHelper;
import org.thechiselgroup.choosel.client.test.TestResourceSetFactory;
import org.thechiselgroup.choosel.client.ui.widget.graph.Arc;
import org.thechiselgroup.choosel.client.ui.widget.graph.GraphDisplay;
import org.thechiselgroup.choosel.client.ui.widget.graph.GraphWidgetReadyEvent;
import org.thechiselgroup.choosel.client.ui.widget.graph.GraphWidgetReadyHandler;
import org.thechiselgroup.choosel.client.ui.widget.graph.Node;
import org.thechiselgroup.choosel.client.ui.widget.graph.NodeDragEvent;
import org.thechiselgroup.choosel.client.ui.widget.graph.NodeDragHandler;
import org.thechiselgroup.choosel.client.util.collections.CollectionUtils;
import org.thechiselgroup.choosel.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.client.util.collections.LightweightCollections;
import org.thechiselgroup.choosel.client.util.collections.LightweightList;
import org.thechiselgroup.choosel.client.views.DefaultResourceItem;
import org.thechiselgroup.choosel.client.views.DragEnablerFactory;
import org.thechiselgroup.choosel.client.views.ResourceItem;
import org.thechiselgroup.choosel.client.views.Slot;
import org.thechiselgroup.choosel.client.views.TestViewContentDisplayCallback;
import org.thechiselgroup.choosel.client.views.graph.GraphViewContentDisplay.Display;

public class GraphViewContentDisplayTest {

    @Mock
    private ArcStyleProvider arcStyleProvider;

    @Mock
    private GraphNodeExpander automaticExpander;

    private TestViewContentDisplayCallback callback;

    @Mock
    private CommandManager commandManager;

    private GraphViewContentDisplay underTest;

    @Mock
    private Display display;

    @Mock
    private DragEnablerFactory dragEnablerFactory;

    @Mock
    private Node node;

    @Mock
    private GraphExpansionRegistry registry;

    @Mock
    private ResourceCategorizer resourceCategorizer;

    @Mock
    private ResourceManager resourceManager;

    private Point sourceLocation;

    private Point targetLocation;

    @Mock
    private ArcType arcType;

    @Test
    public void addResourceItemsCallsArcTypeGetArcItems() {
        // define variables
        String groupId1 = "1";
        String groupId2 = "2";

        // set up arc type provider
        when(arcStyleProvider.getArcTypes()).thenReturn(
                LightweightCollections.toCollection(arcType));
        when(arcType.getArcItems(any(ResourceItem.class))).thenReturn(
                LightweightCollections.<ArcItem> emptyCollection());

        // simulate add
        LightweightCollection<ResourceItem> resourceItems = createResourceItems(
                toLabeledResourceSet(groupId1, createResource(1)),
                toLabeledResourceSet(groupId2, createResource(2)));
        callback.addResourceItems(resourceItems);
        underTest.update(resourceItems,
                LightweightCollections.<ResourceItem> emptySet(),
                LightweightCollections.<ResourceItem> emptySet(),
                LightweightCollections.<Slot> emptySet());

        // verify that getArcItems called
        ArgumentCaptor<ResourceItem> captor = ArgumentCaptor
                .forClass(ResourceItem.class);
        verify(arcType, times(2)).getArcItems(captor.capture());
        assertContentEquals(resourceItems.toList(), captor.getAllValues());
    }

    @Test
    public void addResourceItemToAllResource() {
        ResourceSet resourceSet = createResources(1);

        ResourceItem resourceItem = createResourceItem("1", resourceSet);

        callback.addResourceItem(resourceItem);
        underTest.update(LightweightCollections.toCollection(resourceItem),
                LightweightCollections.<ResourceItem> emptySet(),
                LightweightCollections.<ResourceItem> emptySet(),
                LightweightCollections.<Slot> emptySet());

        resourceSet.add(createResource(2));

        assertContentEquals(resourceSet, underTest.getAllResources());
    }

    @Test
    public void arcItemGetsShownWhenAddingResourceItems() {
        // define values
        String arcId = "arcid";
        String arcStyle = GraphDisplay.ARC_STYLE_SOLID;
        String arcColor = "#ffffff";
        String arcTypeValue = "arcType";
        String groupId1 = "1";
        String groupId2 = "2";
        LightweightList<ResourceItem> resourceItems = createResourceItems(
                toLabeledResourceSet(groupId1, createResource(1)),
                toLabeledResourceSet(groupId2, createResource(2)));

        // set up arc item response
        when(arcStyleProvider.getArcTypes()).thenReturn(
                LightweightCollections.toCollection(arcType));
        ArcItem arcItem = new ArcItem(arcTypeValue, arcId, groupId1, groupId2,
                arcColor, arcStyle);
        when(arcType.getArcItems(eq(resourceItems.get(0)))).thenReturn(
                LightweightCollections.toCollection(arcItem));
        when(arcType.getArcItems(eq(resourceItems.get(1)))).thenReturn(
                LightweightCollections.<ArcItem> emptyCollection());

        // simulate add
        callback.addResourceItems(resourceItems);
        underTest.update(resourceItems,
                LightweightCollections.<ResourceItem> emptySet(),
                LightweightCollections.<ResourceItem> emptySet(),
                LightweightCollections.<Slot> emptySet());

        // verify that arc was shown and settings were correctly made
        ArgumentCaptor<Arc> captor = ArgumentCaptor.forClass(Arc.class);
        verify(display, times(1)).addArc(captor.capture());
        Arc result = captor.getValue();
        assertEquals(arcId, result.getId());
        assertEquals(groupId1, result.getSourceNodeId());
        assertEquals(groupId2, result.getTargetNodeId());
        assertEquals(arcTypeValue, result.getType());
        verify(display, times(1)).setArcStyle(eq(result),
                eq(GraphDisplay.ARC_COLOR), eq(arcColor));
        verify(display, times(1)).setArcStyle(eq(result),
                eq(GraphDisplay.ARC_STYLE), eq(arcStyle));
    }

    @Test
    public void arcItemsGetsShownWhenRequiredResourceItemsAreAddedLater() {
        // define values
        String arcId = "arcid";
        String arcStyle = GraphDisplay.ARC_STYLE_SOLID;
        String arcColor = "#ffffff";
        String arcTypeValue = "arcType";
        String groupId1 = "1";
        String groupId2 = "2";

        ResourceItem resourceItem1 = createResourceItem(groupId1,
                createResources(1));
        ResourceItem resourceItem2 = createResourceItem(groupId2,
                createResources(2));

        // set up arc item response
        when(arcStyleProvider.getArcTypes()).thenReturn(
                LightweightCollections.toCollection(arcType));
        ArcItem arcItem = new ArcItem(arcTypeValue, arcId, groupId1, groupId2,
                arcColor, arcStyle);
        when(arcType.getArcItems(eq(resourceItem1))).thenReturn(
                LightweightCollections.toCollection(arcItem));
        when(arcType.getArcItems(eq(resourceItem2))).thenReturn(
                LightweightCollections.<ArcItem> emptyCollection());

        // simulate add - arc item gets created but not shown
        callback.addResourceItem(resourceItem1);
        underTest.update(LightweightCollections.toCollection(resourceItem1),
                LightweightCollections.<ResourceItem> emptySet(),
                LightweightCollections.<ResourceItem> emptySet(),
                LightweightCollections.<Slot> emptySet());

        // verify not shown
        verify(display, times(0)).addArc(any(Arc.class));

        // 2nd add - arc item should get shown
        callback.addResourceItem(resourceItem2);
        underTest.update(LightweightCollections.toCollection(resourceItem2),
                LightweightCollections.<ResourceItem> emptySet(),
                LightweightCollections.<ResourceItem> emptySet(),
                LightweightCollections.<Slot> emptySet());

        // verify that arc was shown and settings were correctly made
        ArgumentCaptor<Arc> captor = ArgumentCaptor.forClass(Arc.class);
        verify(display, times(1)).addArc(captor.capture());
        Arc result = captor.getValue();
        assertEquals(arcId, result.getId());
        assertEquals(groupId1, result.getSourceNodeId());
        assertEquals(groupId2, result.getTargetNodeId());
        assertEquals(arcTypeValue, result.getType());
        verify(display, times(1)).setArcStyle(eq(result),
                eq(GraphDisplay.ARC_COLOR), eq(arcColor));
        verify(display, times(1)).setArcStyle(eq(result),
                eq(GraphDisplay.ARC_STYLE), eq(arcStyle));
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
    public void doNotShowArcItemForUnknownResourceItems() {
        // define values
        String arcId1 = "arcid1";
        String arcId2 = "arcid2";
        String arcStyle = GraphDisplay.ARC_STYLE_SOLID;
        String arcColor = "#ffffff";
        String arcTypeValue = "arcType";
        String groupId1 = "1";
        String groupId2 = "2";

        // set up arc item response
        ArcItem arcItem1 = new ArcItem(arcTypeValue, arcId1, groupId1,
                groupId2, arcColor, arcStyle);
        ArcItem arcItem2 = new ArcItem(arcTypeValue, arcId2, groupId2,
                groupId1, arcColor, arcStyle);
        when(arcType.getArcItems(any(ResourceItem.class))).thenReturn(
                LightweightCollections.toCollection(arcItem1, arcItem2));

        // simulate add
        LightweightCollection<ResourceItem> resourceItems = createResourceItems(toLabeledResourceSet(
                groupId1, createResource(1)));
        when(callback.getResourceItems()).thenReturn(resourceItems);
        when(callback.containsResourceItem(groupId1)).thenReturn(true);
        underTest.update(resourceItems,
                LightweightCollections.<ResourceItem> emptySet(),
                LightweightCollections.<ResourceItem> emptySet(),
                LightweightCollections.<Slot> emptySet());

        // verify that no arc was created
        verify(display, times(0)).addArc(any(Arc.class));
    }

    @Test
    public void getAllNodes() {
        ResourceItem resourceItem1 = ResourcesTestHelper.createResourceItem(
                "1", createResources(1));
        ResourceItem resourceItem2 = ResourcesTestHelper.createResourceItem(
                "2", createResources(2));

        LightweightCollection<ResourceItem> resourceItems = LightweightCollections
                .toCollection(resourceItem1, resourceItem2);

        callback.addResourceItems(resourceItems);
        underTest.update(resourceItems,
                LightweightCollections.<ResourceItem> emptySet(),
                LightweightCollections.<ResourceItem> emptySet(),
                LightweightCollections.<Slot> emptySet());

        Node node1 = ((GraphItem) resourceItem1.getDisplayObject()).getNode();
        Node node2 = ((GraphItem) resourceItem2.getDisplayObject()).getNode();

        assertContentEquals(CollectionUtils.toList(node1, node2),
                underTest.getAllNodes());
    }

    @Test
    public void loadNeighbourhoodWhenAddingConcept() {
        Resource concept1 = createResource(1);

        ResourceItem resourceItem = ResourcesTestHelper.createResourceItem("1",
                toResourceSet(concept1));

        callback.addResourceItem(resourceItem);
        underTest.update(LightweightCollections.toCollection(resourceItem),
                LightweightCollections.<ResourceItem> emptySet(),
                LightweightCollections.<ResourceItem> emptySet(),
                LightweightCollections.<Slot> emptySet());

        ArgumentCaptor<DefaultResourceItem> argument = ArgumentCaptor
                .forClass(DefaultResourceItem.class);
        verify(automaticExpander, times(1)).expand(argument.capture(),
                any(GraphNodeExpansionCallback.class));

        ResourceItem result = argument.getValue();
        assertEquals(1, result.getResourceSet().size());
        assertEquals(concept1, result.getResourceSet().getFirstResource());
    }

    @Test
    public void removeResourceItemFromAllResource() {
        ResourceSet resourceSet = createResources(1);

        ResourceItem resourceItem = ResourcesTestHelper.createResourceItem("1",
                resourceSet);

        callback.addResourceItem(resourceItem);
        underTest.update(LightweightCollections.toCollection(resourceItem),
                LightweightCollections.<ResourceItem> emptyCollection(),
                LightweightCollections.<ResourceItem> emptyCollection(),
                LightweightCollections.<Slot> emptyCollection());

        underTest.update(
                LightweightCollections.<ResourceItem> emptyCollection(),
                LightweightCollections.<ResourceItem> emptyCollection(),
                LightweightCollections.toCollection(resourceItem),
                LightweightCollections.<Slot> emptyCollection());

        assertContentEquals(createResources(), underTest.getAllResources());

    }

    // TODO restore
    @Ignore
    @Test
    public void removeSourceResourceItemRemovesArc() {
        String groupId1 = "1";
        String groupId2 = "2";

        ResourceSet resourceSet1 = createResources(1);
        ResourceSet resourceSet2 = createResources(2);

        ResourceItem resourceItem1 = ResourcesTestHelper.createResourceItem(
                groupId1, resourceSet1);
        ResourceItem resourceItem2 = ResourcesTestHelper.createResourceItem(
                groupId2, resourceSet2);

        LightweightCollection<ResourceItem> resourceItems = LightweightCollections
                .toCollection(resourceItem1, resourceItem2);

        callback.addResourceItems(resourceItems);
        underTest.update(resourceItems,
                LightweightCollections.<ResourceItem> emptyCollection(),
                LightweightCollections.<ResourceItem> emptyCollection(),
                LightweightCollections.<Slot> emptyCollection());

        // underTest.showArc("arcType", groupId1, groupId2);
        //
        // ArgumentCaptor<Arc> captor = ArgumentCaptor.forClass(Arc.class);
        //
        // underTest.update(
        // LightweightCollections.<ResourceItem> emptyCollection(),
        // LightweightCollections.<ResourceItem> emptyCollection(),
        // LightweightCollections.toCollection(resourceItem1),
        // LightweightCollections.<Slot> emptyCollection());
        //
        // verify(display, times(1)).removeArc(captor.capture());
    }

    // TODO restore
    @Ignore
    @Test
    public void removeTargetResourceItemRemovesArc() {
        String groupId1 = "1";
        String groupId2 = "2";

        ResourceSet resourceSet1 = createResources(1);
        ResourceSet resourceSet2 = createResources(2);

        ResourceItem resourceItem1 = ResourcesTestHelper.createResourceItem(
                groupId1, resourceSet1);
        ResourceItem resourceItem2 = ResourcesTestHelper.createResourceItem(
                groupId2, resourceSet2);

        LightweightCollection<ResourceItem> resourceItems = LightweightCollections
                .toCollection(resourceItem1, resourceItem2);

        callback.addResourceItems(resourceItems);

        underTest.update(resourceItems,
                LightweightCollections.<ResourceItem> emptyCollection(),
                LightweightCollections.<ResourceItem> emptyCollection(),
                LightweightCollections.<Slot> emptyCollection());

        // underTest.showArc("arcType", groupId1, groupId2);
        //
        // ArgumentCaptor<Arc> captor = ArgumentCaptor.forClass(Arc.class);
        //
        // underTest.update(
        // LightweightCollections.<ResourceItem> emptyCollection(),
        // LightweightCollections.<ResourceItem> emptyCollection(),
        // LightweightCollections.toCollection(resourceItem2),
        // LightweightCollections.<Slot> emptyCollection());
        //
        // verify(display, times(1)).removeArc(captor.capture());
    }

    @Before
    public void setUp() throws Exception {
        MockitoGWTBridge.setUp();
        MockitoAnnotations.initMocks(this);

        callback = spy(new TestViewContentDisplayCallback());

        sourceLocation = new Point(10, 15);
        targetLocation = new Point(20, 25);

        underTest = spy(new GraphViewContentDisplay(display, commandManager,
                resourceManager, dragEnablerFactory, resourceCategorizer,
                arcStyleProvider, registry));

        underTest.init(callback);

        when(resourceCategorizer.getCategory(any(Resource.class))).thenReturn(
                TestResourceSetFactory.TYPE_1);

        when(registry.getAutomaticExpander(any(String.class))).thenReturn(
                automaticExpander);

        when(arcStyleProvider.getArcTypes()).thenReturn(
                LightweightCollections.<ArcType> emptyCollection());

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
