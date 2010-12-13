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
import org.thechiselgroup.choosel.client.ui.widget.graph.ArcSettings;
import org.thechiselgroup.choosel.client.ui.widget.graph.GraphDisplay;
import org.thechiselgroup.choosel.client.ui.widget.graph.GraphDisplayReadyEvent;
import org.thechiselgroup.choosel.client.ui.widget.graph.GraphDisplayReadyEventHandler;
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
import org.thechiselgroup.choosel.client.views.TestViewContentDisplayCallback;
import org.thechiselgroup.choosel.client.views.slots.Slot;

public class GraphViewContentDisplayTest {

    @Mock
    private ArcTypeProvider arcStyleProvider;

    @Mock
    private GraphNodeExpander automaticExpander;

    private TestViewContentDisplayCallback callback;

    @Mock
    private CommandManager commandManager;

    private GraphViewContentDisplay underTest;

    @Mock
    private GraphDisplay graphDisplay;

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

    private String arcTypeValue;

    private boolean arcDirected;

    private String arcColor;

    @Test
    public void addResourceItemsCallsArcTypeGetArcItems() {
        String groupId1 = "1";
        String groupId2 = "2";

        arcStyleProviderReturnArcType();
        init();
        arcTypeReturnsNoArcs();

        LightweightCollection<ResourceItem> resourceItems = createResourceItems(
                groupId1, groupId2);

        callback.addResourceItems(resourceItems);
        addResourceItemToUnderTest(resourceItems);

        ArgumentCaptor<ResourceItem> captor = ArgumentCaptor
                .forClass(ResourceItem.class);
        verify(arcType, times(2)).getArcs(captor.capture());
        assertContentEquals(resourceItems.toList(), captor.getAllValues());
    }

    @Test
    public void addResourceItemToAllResource() {
        ResourceSet resourceSet = createResources(1);
        ResourceItem resourceItem = createResourceItem("1", resourceSet);

        init();

        callback.addResourceItem(resourceItem);
        addResourceItemToUnderTest(LightweightCollections
                .toCollection(resourceItem));

        resourceSet.add(createResource(2));

        assertContentEquals(resourceSet, underTest.getAllResources());
    }

    private void addResourceItemToUnderTest(
            LightweightCollection<ResourceItem> resourceItems) {

        underTest.update(resourceItems,
                LightweightCollections.<ResourceItem> emptySet(),
                LightweightCollections.<ResourceItem> emptySet(),
                LightweightCollections.<Slot> emptySet());
    }

    @Test
    public void arcsAreAddedWhenAddingResourceItems() {
        String arcId = "arcid";
        String groupId1 = "1";
        String groupId2 = "2";

        LightweightList<ResourceItem> resourceItems = createResourceItems(
                groupId1, groupId2);

        arcStyleProviderReturnArcType();
        init();

        arcTypeReturnsArcFor(resourceItems.get(0),
                createArc(arcId, groupId1, groupId2));
        arcTypeReturnsNoArcsFor(resourceItems.get(1));

        simulateAddResourceItems(resourceItems);

        verifyArcShown(arcId, groupId1, groupId2);
    }

    @Test
    public void arcsAreAddedWhenRequiredResourceItemsAreAddedLater() {
        String arcId = "arcid";
        String groupId1 = "1";
        String groupId2 = "2";

        ResourceItem resourceItem1 = createResourceItem(groupId1,
                createResources(1));
        ResourceItem resourceItem2 = createResourceItem(groupId2,
                createResources(2));

        arcStyleProviderReturnArcType();
        init();

        arcTypeReturnsArcFor(resourceItem1,
                createArc(arcId, groupId1, groupId2));
        arcTypeReturnsNoArcsFor(resourceItem2);

        // simulate add - arc item gets created but not shown
        when(graphDisplay.containsNode(groupId1)).thenReturn(true);
        callback.addResourceItem(resourceItem1);
        addResourceItemToUnderTest(LightweightCollections
                .toCollection(resourceItem1));

        // verify not shown
        verify(graphDisplay, times(0)).addArc(any(Arc.class));

        // 2nd add - arc item should get shown
        when(graphDisplay.containsNode(groupId2)).thenReturn(true);
        callback.addResourceItem(resourceItem2);
        addResourceItemToUnderTest(LightweightCollections
                .toCollection(resourceItem2));

        verifyArcShown(arcId, groupId1, groupId2);
    }

    @Test
    public void arcsAreRemovedWhenSettingArcTypeNotInvisible() {
        String arcId = "arcid";
        String groupId1 = "1";
        String groupId2 = "2";
        LightweightList<ResourceItem> resourceItems = createResourceItems(
                groupId1, groupId2);

        arcStyleProviderReturnArcType();
        init();

        arcTypeReturnsArcFor(resourceItems.get(0),
                createArc(arcId, groupId1, groupId2));
        arcTypeReturnsNoArcsFor(resourceItems.get(1));

        simulateAddResourceItems(resourceItems);
        when(graphDisplay.containsArc(arcId)).thenReturn(true);

        underTest.setArcTypeVisible(arcType.getID(), false);

        verifyArcRemoved(arcId, groupId1, groupId2);
    }

    private void arcStyleProviderReturnArcType() {
        when(arcStyleProvider.getArcTypes()).thenReturn(
                LightweightCollections.toCollection(arcType));
    }

    private void arcTypeReturnsArcFor(ResourceItem resourceItem, Arc arc) {
        when(arcType.getArcs(eq(resourceItem))).thenReturn(
                LightweightCollections.toCollection(arc));
    }

    private void arcTypeReturnsNoArcs() {
        when(arcType.getArcs(any(ResourceItem.class))).thenReturn(
                LightweightCollections.<Arc> emptyCollection());
    }

    private void arcTypeReturnsNoArcsFor(ResourceItem resourceItem) {
        when(arcType.getArcs(eq(resourceItem))).thenReturn(
                LightweightCollections.<Arc> emptyCollection());
    }

    private Arc createArc(String arcId, String from, String to) {
        return new Arc(arcId, from, to, arcTypeValue, arcDirected);
    }

    /*
     * Test case: node drag event gets fired, test that correct move command is
     * added to the command manager
     */
    @Test
    public void createNodeMoveCommandWhenNodeDragged() {
        init();

        ArgumentCaptor<NodeDragHandler> argument1 = ArgumentCaptor
                .forClass(NodeDragHandler.class);

        verify(graphDisplay, times(1)).addEventHandler(eq(NodeDragEvent.TYPE),
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
        assertEquals(graphDisplay, command2.getGraphDisplay());
    }

    @Test
    public void doNotShowArcItemForUnknownResourceItems() {
        init();

        String arcId1 = "arcid1";
        String arcId2 = "arcid2";
        String groupId1 = "1";
        String groupId2 = "2";

        // set up arc item response
        Arc arcItem1 = createArc(arcId1, groupId1, groupId2);
        Arc arcItem2 = createArc(arcId2, groupId2, groupId1);
        when(arcType.getArcs(any(ResourceItem.class))).thenReturn(
                LightweightCollections.toCollection(arcItem1, arcItem2));

        // simulate add
        LightweightCollection<ResourceItem> resourceItems = ResourcesTestHelper
                .createResourceItems(groupId1);
        when(callback.getResourceItems()).thenReturn(resourceItems);
        when(callback.containsResourceItem(groupId1)).thenReturn(true);
        addResourceItemToUnderTest(resourceItems);

        // verify that no arc was created
        verify(graphDisplay, times(0)).addArc(any(Arc.class));
    }

    @Test
    public void getAllNodes() {
        init();

        ResourceItem resourceItem1 = ResourcesTestHelper.createResourceItem(
                "1", createResources(1));
        ResourceItem resourceItem2 = ResourcesTestHelper.createResourceItem(
                "2", createResources(2));

        LightweightCollection<ResourceItem> resourceItems = LightweightCollections
                .toCollection(resourceItem1, resourceItem2);

        callback.addResourceItems(resourceItems);
        addResourceItemToUnderTest(resourceItems);

        Node node1 = ((GraphItem) resourceItem1.getDisplayObject()).getNode();
        Node node2 = ((GraphItem) resourceItem2.getDisplayObject()).getNode();

        assertContentEquals(CollectionUtils.toList(node1, node2),
                underTest.getAllNodes());
    }

    private void init() {
        underTest = spy(new GraphViewContentDisplay(graphDisplay,
                commandManager, resourceManager, dragEnablerFactory,
                resourceCategorizer, arcStyleProvider, registry));
        underTest.init(callback);
        ArgumentCaptor<GraphDisplayReadyEventHandler> argument = ArgumentCaptor
                .forClass(GraphDisplayReadyEventHandler.class);
        verify(graphDisplay).addGraphDisplayReadyHandler(argument.capture());
        argument.getValue().onWidgetReady(new GraphDisplayReadyEvent(null));
    }

    @Test
    public void loadNeighbourhoodWhenAddingConcept() {
        init();

        Resource resource = createResource(1);
        ResourceItem resourceItem = ResourcesTestHelper.createResourceItem("1",
                toResourceSet(resource));

        callback.addResourceItem(resourceItem);
        addResourceItemToUnderTest(LightweightCollections
                .toCollection(resourceItem));

        ArgumentCaptor<DefaultResourceItem> argument = ArgumentCaptor
                .forClass(DefaultResourceItem.class);
        verify(automaticExpander, times(1)).expand(argument.capture(),
                any(GraphNodeExpansionCallback.class));

        ResourceItem result = argument.getValue();
        assertEquals(1, result.getResourceSet().size());
        assertEquals(resource, result.getResourceSet().getFirstResource());
    }

    @Test
    public void removeResourceItemFromAllResource() {
        init();

        ResourceSet resourceSet = createResources(1);
        ResourceItem resourceItem = createResourceItem("1", resourceSet);

        callback.addResourceItem(resourceItem);
        addResourceItemToUnderTest(LightweightCollections
                .toCollection(resourceItem));

        underTest.update(
                LightweightCollections.<ResourceItem> emptyCollection(),
                LightweightCollections.<ResourceItem> emptyCollection(),
                LightweightCollections.toCollection(resourceItem),
                LightweightCollections.<Slot> emptyCollection());

        assertContentEquals(createResources(), underTest.getAllResources());

    }

    @Ignore
    @Test
    public void removeSourceResourceItemRemovesArc() {
        String arcId = "arcid";
        String groupId1 = "1";
        String groupId2 = "2";

        ResourceSet resourceSet1 = createResources(1);
        ResourceSet resourceSet2 = createResources(2);

        ResourceItem resourceItem1 = createResourceItem(groupId1, resourceSet1);
        ResourceItem resourceItem2 = createResourceItem(groupId2, resourceSet2);

        LightweightCollection<ResourceItem> resourceItems = LightweightCollections
                .toCollection(resourceItem1, resourceItem2);

        arcStyleProviderReturnArcType();
        init();
        Arc arc = createArc(arcId, groupId1, groupId2);
        arcTypeReturnsArcFor(resourceItem1, arc);
        arcTypeReturnsNoArcsFor(resourceItem2);

        // simulate add
        when(graphDisplay.containsNode(groupId1)).thenReturn(true);
        when(graphDisplay.containsNode(groupId2)).thenReturn(true);
        callback.addResourceItems(resourceItems);
        addResourceItemToUnderTest(resourceItems);
        when(graphDisplay.containsArc(arcId)).thenReturn(true);

        // simulate remove
        when(graphDisplay.containsNode(groupId1)).thenReturn(false);
        callback.removeResourceItem(resourceItem1);
        underTest.update(
                LightweightCollections.<ResourceItem> emptyCollection(),
                LightweightCollections.<ResourceItem> emptyCollection(),
                LightweightCollections.toCollection(resourceItem1),
                LightweightCollections.<Slot> emptyCollection());

        verifyArcRemoved(arcId, groupId1, groupId2);
    }

    @Ignore
    @Test
    public void removeTargetResourceItemRemovesArc() {
        String arcId = "arcid";
        String groupId1 = "1";
        String groupId2 = "2";

        ResourceSet resourceSet1 = createResources(1);
        ResourceSet resourceSet2 = createResources(2);

        ResourceItem resourceItem1 = createResourceItem(groupId1, resourceSet1);
        ResourceItem resourceItem2 = createResourceItem(groupId2, resourceSet2);

        LightweightCollection<ResourceItem> resourceItems = LightweightCollections
                .toCollection(resourceItem1, resourceItem2);

        arcStyleProviderReturnArcType();
        init();
        Arc arc = createArc(arcId, groupId1, groupId2);
        arcTypeReturnsArcFor(resourceItem1, arc);
        arcTypeReturnsNoArcsFor(resourceItem2);

        // simulate add
        when(graphDisplay.containsNode(groupId1)).thenReturn(true);
        when(graphDisplay.containsNode(groupId2)).thenReturn(true);
        callback.addResourceItems(resourceItems);
        addResourceItemToUnderTest(resourceItems);
        when(graphDisplay.containsArc(arcId)).thenReturn(true);

        // simulate remove
        when(graphDisplay.containsNode(groupId2)).thenReturn(false);
        callback.removeResourceItem(resourceItem2);
        underTest.update(
                LightweightCollections.<ResourceItem> emptyCollection(),
                LightweightCollections.<ResourceItem> emptyCollection(),
                LightweightCollections.toCollection(resourceItem2),
                LightweightCollections.<Slot> emptyCollection());

        verifyArcRemoved(arcId, groupId1, groupId2);
    }

    @Test
    public void setArcColorOnContainerChangesColorOfExistingArcs() {
        String arcId = "arcid";
        String groupId1 = "1";
        String groupId2 = "2";
        LightweightList<ResourceItem> resourceItems = ResourcesTestHelper
                .createResourceItems(groupId1, groupId2);

        arcStyleProviderReturnArcType();
        init();

        Arc arc = createArc(arcId, groupId1, groupId2);
        arcTypeReturnsArcFor(resourceItems.get(0), arc);
        arcTypeReturnsNoArcsFor(resourceItems.get(1));

        simulateAddResourceItems(resourceItems);

        verify(graphDisplay, times(1)).setArcStyle(eq(arc),
                eq(ArcSettings.ARC_COLOR), eq(arcColor));

        String newColor = "#ff0000";
        underTest.getArcItemContainer(arcTypeValue).setArcColor(newColor);

        verify(graphDisplay, times(1)).setArcStyle(eq(arc),
                eq(ArcSettings.ARC_COLOR), eq(newColor));
    }

    @Test
    public void setArcColorOnContainerChangesColorOfNewArcs() {
        String arcId = "arcid";
        String groupId1 = "1";
        String groupId2 = "2";
        LightweightList<ResourceItem> resourceItems = ResourcesTestHelper
                .createResourceItems(groupId1, groupId2);

        arcStyleProviderReturnArcType();
        init();

        String newColor = "#ff0000";
        underTest.getArcItemContainer(arcTypeValue).setArcColor(newColor);

        Arc arc = createArc(arcId, groupId1, groupId2);
        arcTypeReturnsArcFor(resourceItems.get(0), arc);
        arcTypeReturnsNoArcsFor(resourceItems.get(1));

        simulateAddResourceItems(resourceItems);

        verify(graphDisplay, times(1)).setArcStyle(eq(arc),
                eq(ArcSettings.ARC_COLOR), eq(newColor));
    }

    @Before
    public void setUp() throws Exception {
        MockitoGWTBridge.setUp();
        MockitoAnnotations.initMocks(this);

        callback = spy(new TestViewContentDisplayCallback());

        sourceLocation = new Point(10, 15);
        targetLocation = new Point(20, 25);

        arcTypeValue = "arcType";
        arcDirected = true;
        arcColor = "#ffffff";

        when(arcStyleProvider.getArcTypes()).thenReturn(
                LightweightCollections.<ArcType> emptyCollection());

        when(arcType.getID()).thenReturn(arcTypeValue);
        when(arcType.getDefaultArcColor()).thenReturn(arcColor);
        when(arcType.getDefaultArcStyle()).thenReturn(
                ArcSettings.ARC_STYLE_SOLID);
        when(arcType.getDefaultArcThickness()).thenReturn(1);

        when(resourceCategorizer.getCategory(any(Resource.class))).thenReturn(
                TestResourceSetFactory.TYPE_1);

        when(registry.getAutomaticExpander(any(String.class))).thenReturn(
                automaticExpander);
    }

    private void simulateAddResourceItems(
            LightweightList<ResourceItem> resourceItems) {
        for (ResourceItem resourceItem : resourceItems) {
            when(graphDisplay.containsNode(resourceItem.getGroupID()))
                    .thenReturn(true);
        }
        callback.addResourceItems(resourceItems);
        addResourceItemToUnderTest(resourceItems);
    }

    @After
    public void tearDown() {
        MockitoGWTBridge.tearDown();
    }

    @Test
    public void updateArcsForResourceItems() {
        String arcId = "arcid";
        String groupId1 = "1";
        String groupId2 = "2";
        LightweightList<ResourceItem> resourceItems = ResourcesTestHelper
                .createResourceItems(groupId1, groupId2);

        arcStyleProviderReturnArcType();
        init();
        arcTypeReturnsNoArcs();
        simulateAddResourceItems(resourceItems);

        arcTypeReturnsArcFor(resourceItems.get(0),
                createArc(arcId, groupId1, groupId2));

        underTest.updateArcsForResourceItems(resourceItems);

        verifyArcShown(arcId, groupId1, groupId2);

    }

    private void verifyArcRemoved(String arcId, String sourceNodeId,
            String targetNodeId) {

        ArgumentCaptor<Arc> captor = ArgumentCaptor.forClass(Arc.class);
        verify(graphDisplay, times(1)).removeArc(captor.capture());
        Arc result = captor.getValue();
        assertEquals(arcId, result.getId());
        assertEquals(sourceNodeId, result.getSourceNodeId());
        assertEquals(targetNodeId, result.getTargetNodeId());
        assertEquals(arcTypeValue, result.getType());
    }

    private void verifyArcShown(String arcId, String sourceNodeId,
            String targetNodeId) {

        ArgumentCaptor<Arc> captor = ArgumentCaptor.forClass(Arc.class);
        verify(graphDisplay, times(1)).addArc(captor.capture());
        Arc result = captor.getValue();
        assertEquals(arcId, result.getId());
        assertEquals(sourceNodeId, result.getSourceNodeId());
        assertEquals(targetNodeId, result.getTargetNodeId());
        assertEquals(arcTypeValue, result.getType());
    }

}
