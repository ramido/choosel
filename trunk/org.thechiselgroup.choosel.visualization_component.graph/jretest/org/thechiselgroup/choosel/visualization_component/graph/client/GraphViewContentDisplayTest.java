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
package org.thechiselgroup.choosel.visualization_component.graph.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.thechiselgroup.choosel.core.client.test.AdvancedAsserts.assertContentEquals;
import static org.thechiselgroup.choosel.core.client.test.ResourcesMatchers.containsEqualResources;
import static org.thechiselgroup.choosel.core.client.test.ResourcesTestHelper.createViewItem;
import static org.thechiselgroup.choosel.core.client.test.ResourcesTestHelper.createViewItems;
import static org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory.createResource;
import static org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory.createResources;
import static org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory.toResourceSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.core.client.command.CommandManager;
import org.thechiselgroup.choosel.core.client.command.UndoableCommand;
import org.thechiselgroup.choosel.core.client.geometry.Point;
import org.thechiselgroup.choosel.core.client.resources.Resource;
import org.thechiselgroup.choosel.core.client.resources.ResourceCategorizer;
import org.thechiselgroup.choosel.core.client.resources.ResourceManager;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.test.MockitoGWTBridge;
import org.thechiselgroup.choosel.core.client.test.ResourcesTestHelper;
import org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory;
import org.thechiselgroup.choosel.core.client.util.collections.CollectionUtils;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollections;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightList;
import org.thechiselgroup.choosel.core.client.views.DefaultViewItem;
import org.thechiselgroup.choosel.core.client.views.TestViewContentDisplayCallback;
import org.thechiselgroup.choosel.core.client.views.ViewItem;
import org.thechiselgroup.choosel.core.client.views.ViewItemContainer;
import org.thechiselgroup.choosel.core.client.views.slots.Slot;
import org.thechiselgroup.choosel.visualization_component.graph.client.widget.Arc;
import org.thechiselgroup.choosel.visualization_component.graph.client.widget.ArcSettings;
import org.thechiselgroup.choosel.visualization_component.graph.client.widget.GraphDisplay;
import org.thechiselgroup.choosel.visualization_component.graph.client.widget.GraphDisplayReadyEvent;
import org.thechiselgroup.choosel.visualization_component.graph.client.widget.GraphDisplayReadyEventHandler;
import org.thechiselgroup.choosel.visualization_component.graph.client.widget.Node;
import org.thechiselgroup.choosel.visualization_component.graph.client.widget.NodeDragEvent;
import org.thechiselgroup.choosel.visualization_component.graph.client.widget.NodeDragHandler;

public class GraphViewContentDisplayTest {

    @Mock
    private ArcTypeProvider arcStyleProvider;

    @Mock
    private GraphNodeExpander automaticExpander;

    private TestViewContentDisplayCallback callback;

    @Mock
    private CommandManager commandManager;

    private Graph underTest;

    @Mock
    private GraphDisplay graphDisplay;

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

    private String arcTypeId;

    private boolean arcDirected;

    private String arcColor;

    private int arcThickness;

    private String arcStyle;

    @Test
    public void addResourceItemsCallsArcTypeGetArcItems() {
        arcStyleProviderReturnArcType();
        init();
        arcTypeReturnsArcs(any(ViewItem.class));

        LightweightCollection<ViewItem> viewItems = createViewItems(1, 2);

        simulateAddViewItems(viewItems);

        ArgumentCaptor<ViewItem> captor = ArgumentCaptor
                .forClass(ViewItem.class);
        verify(arcType, times(2)).getArcs(captor.capture(),
                any(ViewItemContainer.class));
        assertContentEquals(viewItems.toList(), captor.getAllValues());
    }

    @Test
    public void addResourceItemToAllResource() {
        ResourceSet resourceSet = createResources(1);
        ViewItem viewItem = createViewItem("1", resourceSet);

        init();

        simulateAddViewItems(LightweightCollections.toCollection(viewItem));

        resourceSet.add(createResource(2));

        assertThat(underTest.getAllResources(), containsEqualResources(resourceSet));
    }

    private void addViewItemToUnderTest(
            LightweightCollection<ViewItem> resourceItems) {

        underTest.update(resourceItems,
                LightweightCollections.<ViewItem> emptySet(),
                LightweightCollections.<ViewItem> emptySet(),
                LightweightCollections.<Slot> emptySet());
    }

    @Test
    public void arcsAreAddedWhenAddingResourceItems() {
        String arcId = "arcid";
        LightweightList<ViewItem> viewItems = createViewItems(1, 2);

        arcStyleProviderReturnArcType();
        init();

        arcTypeReturnsArcs(eq(viewItems.get(0)), createArc(arcId, 1, 2));
        arcTypeReturnsArcs(eq(viewItems.get(1)));

        simulateAddViewItems(viewItems);

        verifyArcShown(arcId, "1", "2");
    }

    @Test
    public void arcsAreRemovedWhenSettingArcTypeNotInvisible() {
        String arcId = "arcid";
        LightweightList<ViewItem> viewItems = createViewItems(1, 2);

        arcStyleProviderReturnArcType();
        init();

        arcTypeReturnsArcs(eq(viewItems.get(0)), createArc(arcId, 1, 2));
        arcTypeReturnsArcs(eq(viewItems.get(1)));

        simulateAddViewItems(viewItems);
        when(graphDisplay.containsArc(arcId)).thenReturn(true);

        underTest.setArcTypeVisible(arcType.getArcTypeID(), false);

        verifyArcRemoved(arcId, "1", "2");
    }

    @Test
    public void arcsAreShownWhenContainerVisibleSetTrueAfterBeingCreatedWhileVisibleWasFalse() {
        arcStyleProviderReturnArcType();
        init();

        arcTypeReturnsArcs(any(ViewItem.class), createArc("arcid1", 1, 2));
        underTest.setArcTypeVisible(arcTypeId, false);
        simulateAddViewItems(createViewItems(1, 2));
        underTest.setArcTypeVisible(arcTypeId, true);

        verifyArcShown("arcid1", 1, 2);
    }

    private void arcStyleProviderReturnArcType() {
        when(arcStyleProvider.getArcTypes()).thenReturn(
                LightweightCollections.toCollection(arcType));
    }

    private void arcTypeReturnsArcs(ViewItem viewItem, Arc... arcs) {
        when(arcType.getArcs(viewItem, any(ViewItemContainer.class)))
                .thenReturn(LightweightCollections.toCollection(arcs));
    }

    private Arc createArc(String arcId, int from, int to) {
        return createArc(arcId, "" + from, "" + to);
    }

    private Arc createArc(String arcId, String from, String to) {
        return new Arc(arcId, from, to, arcTypeId, arcDirected);
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
    public void doNotShowArcItemOnCreationIfContainerVisibleSetFalse() {
        arcStyleProviderReturnArcType();
        init();

        arcTypeReturnsArcs(any(ViewItem.class), createArc("arcid1", 1, 2));
        underTest.setArcTypeVisible(arcTypeId, false);
        simulateAddViewItems(createViewItems(1, 2));

        verifyNoArcAdded();
    }

    @Test
    public void doNotShowArcItemsThatRequireUnknownViewItems() {
        arcStyleProviderReturnArcType();
        init();

        arcTypeReturnsArcs(any(ViewItem.class), createArc("arcid1", 1, 2),
                createArc("arcid2", 2, 1));
        simulateAddViewItems(createViewItems(1));

        verifyNoArcAdded();
    }

    @Test
    public void getAllNodes() {
        init();

        ViewItem viewItem1 = createViewItem(1);
        ViewItem viewItem2 = createViewItem(2);

        simulateAddViewItems(LightweightCollections.toCollection(viewItem1,
                viewItem2));

        Node node1 = ((GraphItem) viewItem1.getDisplayObject()).getNode();
        Node node2 = ((GraphItem) viewItem2.getDisplayObject()).getNode();

        assertContentEquals(CollectionUtils.toList(node1, node2),
                underTest.getAllNodes());
    }

    private void init() {
        underTest = new Graph(graphDisplay, commandManager,
                resourceManager, resourceCategorizer, arcStyleProvider,
                registry);
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
        ViewItem resourceItem = ResourcesTestHelper.createViewItem("1",
                toResourceSet(resource));

        callback.addResourceItem(resourceItem);
        addViewItemToUnderTest(LightweightCollections
                .toCollection(resourceItem));

        ArgumentCaptor<DefaultViewItem> argument = ArgumentCaptor
                .forClass(DefaultViewItem.class);
        verify(automaticExpander, times(1)).expand(argument.capture(),
                any(GraphNodeExpansionCallback.class));

        ViewItem result = argument.getValue();
        assertEquals(1, result.getResourceSet().size());
        assertEquals(resource, result.getResourceSet().getFirstResource());
    }

    @Test
    public void removeResourceItemFromAllResource() {
        init();

        ResourceSet resourceSet = createResources(1);
        ViewItem resourceItem = createViewItem("1", resourceSet);

        callback.addResourceItem(resourceItem);
        addViewItemToUnderTest(LightweightCollections
                .toCollection(resourceItem));

        underTest.update(LightweightCollections.<ViewItem> emptyCollection(),
                LightweightCollections.<ViewItem> emptyCollection(),
                LightweightCollections.toCollection(resourceItem),
                LightweightCollections.<Slot> emptyCollection());

        assertThat(underTest.getAllResources(), containsEqualResources(createResources()));

    }

    @Test
    public void removeSourceResourceItemRemovesArc() {
        String arcId = "arcid";
        String groupId1 = "1";
        String groupId2 = "2";

        ResourceSet resourceSet1 = createResources(1);
        ResourceSet resourceSet2 = createResources(2);

        ViewItem resourceItem1 = createViewItem(groupId1, resourceSet1);
        ViewItem resourceItem2 = createViewItem(groupId2, resourceSet2);

        LightweightCollection<ViewItem> resourceItems = LightweightCollections
                .toCollection(resourceItem1, resourceItem2);

        arcStyleProviderReturnArcType();
        init();
        Arc arc = createArc(arcId, groupId1, groupId2);
        arcTypeReturnsArcs(eq(resourceItem1), arc);
        arcTypeReturnsArcs(eq(resourceItem2), arc);

        // simulate add
        // when(graphDisplay.containsNode(groupId1)).thenReturn(true);
        // when(graphDisplay.containsNode(groupId2)).thenReturn(true);
        // callback.addResourceItems(resourceItems);
        // addResourceItemToUnderTest(resourceItems);
        simulateAddViewItems(resourceItems);
        when(graphDisplay.containsArc(arcId)).thenReturn(true);

        // simulate remove
        when(graphDisplay.containsNode(groupId1)).thenReturn(false);
        callback.removeResourceItem(resourceItem1);
        underTest.update(LightweightCollections.<ViewItem> emptyCollection(),
                LightweightCollections.<ViewItem> emptyCollection(),
                LightweightCollections.toCollection(resourceItem1),
                LightweightCollections.<Slot> emptyCollection());

        verifyArcRemoved(arcId, groupId1, groupId2);
    }

    @Test
    public void removeTargetResourceItemRemovesArc() {
        String arcId = "arcid";
        String groupId1 = "1";
        String groupId2 = "2";

        ResourceSet resourceSet1 = createResources(1);
        ResourceSet resourceSet2 = createResources(2);

        ViewItem resourceItem1 = createViewItem(groupId1, resourceSet1);
        ViewItem resourceItem2 = createViewItem(groupId2, resourceSet2);

        LightweightCollection<ViewItem> resourceItems = LightweightCollections
                .toCollection(resourceItem1, resourceItem2);

        arcStyleProviderReturnArcType();
        init();
        Arc arc = createArc(arcId, groupId1, groupId2);
        arcTypeReturnsArcs(eq(resourceItem1), arc);
        arcTypeReturnsArcs(eq(resourceItem2), arc);

        // simulate add
        when(graphDisplay.containsNode(groupId1)).thenReturn(true);
        when(graphDisplay.containsNode(groupId2)).thenReturn(true);
        callback.addResourceItems(resourceItems);
        addViewItemToUnderTest(resourceItems);
        when(graphDisplay.containsArc(arcId)).thenReturn(true);

        // simulate remove
        when(graphDisplay.containsNode(groupId2)).thenReturn(false);
        callback.removeResourceItem(resourceItem2);
        underTest.update(LightweightCollections.<ViewItem> emptyCollection(),
                LightweightCollections.<ViewItem> emptyCollection(),
                LightweightCollections.toCollection(resourceItem2),
                LightweightCollections.<Slot> emptyCollection());

        verifyArcRemoved(arcId, groupId1, groupId2);
    }

    @Test
    public void setArcColorOnContainerChangesColorOfExistingArcs() {
        LightweightList<ViewItem> resourceItems = createViewItems(1, 2);

        arcStyleProviderReturnArcType();
        init();

        Arc arc = createArc("arcid", 1, 2);
        arcTypeReturnsArcs(eq(resourceItems.get(0)), arc);
        arcTypeReturnsArcs(eq(resourceItems.get(1)));

        simulateAddViewItems(resourceItems);

        verify(graphDisplay, times(1)).setArcStyle(eq(arc),
                eq(ArcSettings.ARC_COLOR), eq(arcColor));

        String newColor = "#ff0000";
        underTest.getArcItemContainer(arcTypeId).setArcColor(newColor);

        verify(graphDisplay, times(1)).setArcStyle(eq(arc),
                eq(ArcSettings.ARC_COLOR), eq(newColor));
    }

    @Test
    public void setArcColorOnContainerChangesColorOfNewArcs() {
        String arcId = "arcid";
        String groupId1 = "1";
        String groupId2 = "2";
        LightweightList<ViewItem> resourceItems = createViewItems(1, 2);

        arcStyleProviderReturnArcType();
        init();

        String newColor = "#ff0000";
        underTest.getArcItemContainer(arcTypeId).setArcColor(newColor);

        Arc arc = createArc(arcId, groupId1, groupId2);
        arcTypeReturnsArcs(eq(resourceItems.get(0)), arc);
        arcTypeReturnsArcs(eq(resourceItems.get(1)));

        simulateAddViewItems(resourceItems);

        verify(graphDisplay, times(1)).setArcStyle(eq(arc),
                eq(ArcSettings.ARC_COLOR), eq(newColor));
    }

    @Test
    public void setArcStyleOnContainerChangesStyleOfExistingArcs() {
        arcStyleProviderReturnArcType();
        init();

        LightweightList<ViewItem> viewItems = createViewItems(1, 2);
        Arc arc = createArc("arcid", 1, 2);
        arcTypeReturnsArcs(eq(viewItems.get(0)), arc);
        arcTypeReturnsArcs(eq(viewItems.get(1)));

        simulateAddViewItems(viewItems);

        verify(graphDisplay, times(1)).setArcStyle(eq(arc),
                eq(ArcSettings.ARC_STYLE), eq(arcStyle));

        String newStyle = ArcSettings.ARC_STYLE_DASHED;
        underTest.getArcItemContainer(arcTypeId).setArcStyle(newStyle);

        verify(graphDisplay, times(1)).setArcStyle(eq(arc),
                eq(ArcSettings.ARC_STYLE), eq(newStyle));
    }

    @Test
    public void setArcStyleOnContainerChangesStyleOfNewArcs() {
        arcStyleProviderReturnArcType();
        init();

        LightweightList<ViewItem> viewItems = createViewItems(1, 2);
        String newStyle = ArcSettings.ARC_STYLE_DASHED;
        underTest.getArcItemContainer(arcTypeId).setArcStyle(newStyle);

        Arc arc = createArc("arcid", 1, 2);
        arcTypeReturnsArcs(eq(viewItems.get(0)), arc);
        arcTypeReturnsArcs(eq(viewItems.get(1)));

        simulateAddViewItems(viewItems);

        verify(graphDisplay, times(1)).setArcStyle(eq(arc),
                eq(ArcSettings.ARC_STYLE), eq(newStyle));
    }

    @Test
    public void setArcThicknessOnContainerChangesThicknessOfExistingArcs() {
        arcStyleProviderReturnArcType();
        init();

        LightweightList<ViewItem> viewItems = createViewItems(1, 2);
        Arc arc = createArc("arcid", 1, 2);
        arcTypeReturnsArcs(eq(viewItems.get(0)), arc);
        arcTypeReturnsArcs(eq(viewItems.get(1)));

        simulateAddViewItems(viewItems);

        verify(graphDisplay, times(1)).setArcStyle(eq(arc),
                eq(ArcSettings.ARC_THICKNESS), eq("" + arcThickness));

        int newThickness = 4;
        underTest.getArcItemContainer(arcTypeId).setArcThickness(newThickness);

        verify(graphDisplay, times(1)).setArcStyle(eq(arc),
                eq(ArcSettings.ARC_THICKNESS), eq("" + newThickness));
    }

    @Test
    public void setArcThicknessOnContainerChangesThicknessOfNewArcs() {
        arcStyleProviderReturnArcType();
        init();

        LightweightList<ViewItem> viewItems = ResourcesTestHelper
                .createViewItems(1, 2);
        int newThickness = 4;
        underTest.getArcItemContainer(arcTypeId).setArcThickness(newThickness);

        Arc arc = createArc("arcid", 1, 2);
        arcTypeReturnsArcs(eq(viewItems.get(0)), arc);
        arcTypeReturnsArcs(eq(viewItems.get(1)));

        simulateAddViewItems(viewItems);

        verify(graphDisplay, times(1)).setArcStyle(eq(arc),
                eq(ArcSettings.ARC_THICKNESS), eq("" + newThickness));
    }

    @Before
    public void setUp() throws Exception {
        MockitoGWTBridge.setUp();
        MockitoAnnotations.initMocks(this);

        callback = spy(new TestViewContentDisplayCallback());

        sourceLocation = new Point(10, 15);
        targetLocation = new Point(20, 25);

        arcTypeId = "arcType";
        arcDirected = true;
        arcColor = "#ffffff";
        arcThickness = 1;
        arcStyle = ArcSettings.ARC_STYLE_SOLID;

        when(arcStyleProvider.getArcTypes()).thenReturn(
                LightweightCollections.<ArcType> emptyCollection());

        when(arcType.getArcTypeID()).thenReturn(arcTypeId);
        when(arcType.getDefaultArcColor()).thenReturn(arcColor);
        when(arcType.getDefaultArcStyle()).thenReturn(arcStyle);
        when(arcType.getDefaultArcThickness()).thenReturn(arcThickness);

        when(resourceCategorizer.getCategory(any(Resource.class))).thenReturn(
                TestResourceSetFactory.TYPE_1);

        when(registry.getAutomaticExpander(any(String.class))).thenReturn(
                automaticExpander);
    }

    private void simulateAddViewItems(LightweightCollection<ViewItem> viewItems) {
        for (ViewItem viewItem : viewItems) {
            when(graphDisplay.containsNode(viewItem.getViewItemID()))
                    .thenReturn(true);
        }
        callback.addResourceItems(viewItems);
        addViewItemToUnderTest(viewItems);
    }

    @After
    public void tearDown() {
        MockitoGWTBridge.tearDown();
    }

    @Test
    public void updateArcsForResourceItems() {
        LightweightList<ViewItem> viewItems = createViewItems(1, 2);

        arcStyleProviderReturnArcType();
        init();
        arcTypeReturnsArcs(any(ViewItem.class));
        simulateAddViewItems(viewItems);

        arcTypeReturnsArcs(eq(viewItems.get(0)), createArc("arcid", 1, 2));

        underTest.updateArcsForViewItems(viewItems);

        verifyArcShown("arcid", "1", "2");

    }

    private void verifyArcRemoved(String arcId, String sourceNodeId,
            String targetNodeId) {

        ArgumentCaptor<Arc> captor = ArgumentCaptor.forClass(Arc.class);
        verify(graphDisplay, times(1)).removeArc(captor.capture());
        Arc result = captor.getValue();
        assertEquals(arcId, result.getId());
        assertEquals(sourceNodeId, result.getSourceNodeId());
        assertEquals(targetNodeId, result.getTargetNodeId());
        assertEquals(arcTypeId, result.getType());
    }

    private void verifyArcShown(String arcId, int sourceNodeId, int targetNodeId) {
        verifyArcShown(arcId, "" + sourceNodeId, "" + targetNodeId);
    }

    private void verifyArcShown(String arcId, String sourceNodeId,
            String targetNodeId) {

        ArgumentCaptor<Arc> captor = ArgumentCaptor.forClass(Arc.class);
        verify(graphDisplay, times(1)).addArc(captor.capture());
        Arc result = captor.getValue();
        assertEquals(arcId, result.getId());
        assertEquals(sourceNodeId, result.getSourceNodeId());
        assertEquals(targetNodeId, result.getTargetNodeId());
        assertEquals(arcTypeId, result.getType());
    }

    private void verifyNoArcAdded() {
        verify(graphDisplay, times(0)).addArc(any(Arc.class));
    }

}
