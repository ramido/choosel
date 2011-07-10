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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.thechiselgroup.choosel.core.client.resources.ResourcesTestHelper.createViewItem;
import static org.thechiselgroup.choosel.core.client.resources.ResourcesTestHelper.createViewItems;
import static org.thechiselgroup.choosel.core.client.resources.TestResourceSetFactory.createResource;
import static org.thechiselgroup.choosel.core.client.resources.TestResourceSetFactory.createResources;
import static org.thechiselgroup.choosel.core.client.resources.TestResourceSetFactory.toResourceSet;
import static org.thechiselgroup.choosel.core.shared.test.matchers.collections.CollectionMatchers.containsExactly;

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
import org.thechiselgroup.choosel.core.client.resources.ResourcesTestHelper;
import org.thechiselgroup.choosel.core.client.resources.TestResourceSetFactory;
import org.thechiselgroup.choosel.core.client.test.mockito.MockitoGWTBridge;
import org.thechiselgroup.choosel.core.client.ui.Color;
import org.thechiselgroup.choosel.core.client.util.collections.CollectionUtils;
import org.thechiselgroup.choosel.core.client.util.collections.Delta;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollections;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightList;
import org.thechiselgroup.choosel.core.client.visualization.model.Slot;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItem;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItemContainer;
import org.thechiselgroup.choosel.core.client.visualization.model.implementation.DefaultVisualItem;
import org.thechiselgroup.choosel.core.client.visualization.model.implementation.TestViewContentDisplayCallback;
import org.thechiselgroup.choosel.core.shared.test.matchers.collections.CollectionMatchers;
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

    private Color arcColor;

    private int arcThickness;

    private String arcStyle;

    private Object borderColor;

    private Object backgroundColor;

    @Test
    public void addResourceItemsCallsArcTypeGetArcItems() {
        arcStyleProviderReturnArcType();
        init();
        arcTypeReturnsArcs(any(VisualItem.class));

        LightweightCollection<VisualItem> viewItems = createViewItems(1, 2);

        simulateAddViewItems(viewItems);

        ArgumentCaptor<VisualItem> captor = ArgumentCaptor
                .forClass(VisualItem.class);
        verify(arcType, times(2)).getArcs(captor.capture(),
                any(VisualItemContainer.class));
        assertThat(captor.getAllValues(),
                CollectionMatchers.containsExactly(viewItems.toList()));
    }

    @Test
    public void addResourceItemToAllResource() {
        ResourceSet resourceSet = createResources(1);
        VisualItem viewItem = createViewItem("1", resourceSet);

        init();

        simulateAddViewItems(LightweightCollections.toCollection(viewItem));

        resourceSet.add(createResource(2));

        assertThat(underTest.getAllResources(), containsExactly(resourceSet));
    }

    private void addViewItemToUnderTest(
            LightweightCollection<VisualItem> visualItems) {

        underTest.update(Delta.createAddedDelta(visualItems),
                LightweightCollections.<Slot> emptySet());
    }

    @Test
    public void arcsAreAddedWhenAddingResourceItems() {
        String arcId = "arcid";
        LightweightList<VisualItem> viewItems = createViewItems(1, 2);

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
        LightweightList<VisualItem> viewItems = createViewItems(1, 2);

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

        arcTypeReturnsArcs(any(VisualItem.class), createArc("arcid1", 1, 2));
        underTest.setArcTypeVisible(arcTypeId, false);
        simulateAddViewItems(createViewItems(1, 2));
        underTest.setArcTypeVisible(arcTypeId, true);

        verifyArcShown("arcid1", 1, 2);
    }

    private void arcStyleProviderReturnArcType() {
        when(arcStyleProvider.getArcTypes()).thenReturn(
                LightweightCollections.toCollection(arcType));
    }

    private void arcTypeReturnsArcs(VisualItem viewItem, Arc... arcs) {
        when(arcType.getArcs(viewItem, any(VisualItemContainer.class)))
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
        nodeDragHandler.onDrag(new NodeDragEvent(node, sourceLocation.getX(),
                sourceLocation.getY(), targetLocation.getX(), targetLocation
                        .getY()));

        ArgumentCaptor<UndoableCommand> argument2 = ArgumentCaptor
                .forClass(UndoableCommand.class);

        verify(commandManager, times(1)).execute(argument2.capture());

        UndoableCommand command = argument2.getValue();
        assertThat(command.hasExecuted(), is(true));
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

        arcTypeReturnsArcs(any(VisualItem.class), createArc("arcid1", 1, 2));
        underTest.setArcTypeVisible(arcTypeId, false);
        simulateAddViewItems(createViewItems(1, 2));

        verifyNoArcAdded();
    }

    @Test
    public void doNotShowArcItemsThatRequireUnknownViewItems() {
        arcStyleProviderReturnArcType();
        init();

        arcTypeReturnsArcs(any(VisualItem.class), createArc("arcid1", 1, 2),
                createArc("arcid2", 2, 1));
        simulateAddViewItems(createViewItems(1));

        verifyNoArcAdded();
    }

    @Test
    public void getAllNodes() {
        init();

        VisualItem viewItem1 = createViewItem(1);
        VisualItem viewItem2 = createViewItem(2);

        simulateAddViewItems(LightweightCollections.toCollection(viewItem1,
                viewItem2));

        Node node1 = ((NodeItem) viewItem1.getDisplayObject()).getNode();
        Node node2 = ((NodeItem) viewItem2.getDisplayObject()).getNode();

        assertThat(underTest.getAllNodes(),
                CollectionMatchers.containsExactly(CollectionUtils.toList(
                        node1, node2)));
    }

    private void init() {
        underTest = new Graph(graphDisplay, commandManager, resourceManager,
                resourceCategorizer, arcStyleProvider, registry);
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
        VisualItem viewItem = ResourcesTestHelper.createViewItem("1",
                toResourceSet(resource));

        stubColorSlotValues(viewItem);
        callback.addViewItem(viewItem);
        addViewItemToUnderTest(LightweightCollections.toCollection(viewItem));

        ArgumentCaptor<DefaultVisualItem> argument = ArgumentCaptor
                .forClass(DefaultVisualItem.class);
        verify(automaticExpander, times(1)).expand(argument.capture(),
                any(GraphNodeExpansionCallback.class));

        VisualItem result = argument.getValue();
        assertEquals(1, result.getResources().size());
        assertEquals(resource, result.getResources().getFirstElement());
    }

    @Test
    public void removeResourceItemFromAllResource() {
        init();

        ResourceSet resourceSet = createResources(1);
        VisualItem viewItem = createViewItem("1", resourceSet);

        stubColorSlotValues(viewItem);
        callback.addViewItem(viewItem);
        addViewItemToUnderTest(LightweightCollections.toCollection(viewItem));

        underTest.update(Delta.createRemovedDelta(LightweightCollections
                .toCollection(viewItem)), LightweightCollections
                .<Slot> emptyCollection());

        assertThat(underTest.getAllResources(),
                containsExactly(createResources()));

    }

    @Test
    public void removeSourceResourceItemRemovesArc() {
        String arcId = "arcid";
        String groupId1 = "1";
        String groupId2 = "2";

        ResourceSet resourceSet1 = createResources(1);
        ResourceSet resourceSet2 = createResources(2);

        VisualItem resourceItem1 = createViewItem(groupId1, resourceSet1);
        VisualItem resourceItem2 = createViewItem(groupId2, resourceSet2);

        LightweightCollection<VisualItem> resourceItems = LightweightCollections
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
        underTest.update(Delta.createRemovedDelta(LightweightCollections
                .toCollection(resourceItem1)), LightweightCollections
                .<Slot> emptyCollection());

        verifyArcRemoved(arcId, groupId1, groupId2);
    }

    @Test
    public void removeTargetResourceItemRemovesArc() {
        String arcId = "arcid";
        String groupId1 = "1";
        String groupId2 = "2";

        ResourceSet resourceSet1 = createResources(1);
        ResourceSet resourceSet2 = createResources(2);

        VisualItem viewItem1 = createViewItem(groupId1, resourceSet1);
        VisualItem viewItem2 = createViewItem(groupId2, resourceSet2);

        LightweightCollection<VisualItem> viewItems = LightweightCollections
                .toCollection(viewItem1, viewItem2);

        arcStyleProviderReturnArcType();
        init();
        Arc arc = createArc(arcId, groupId1, groupId2);
        arcTypeReturnsArcs(eq(viewItem1), arc);
        arcTypeReturnsArcs(eq(viewItem2), arc);

        // simulate add
        simulateAddViewItems(viewItems);
        when(graphDisplay.containsArc(arcId)).thenReturn(true);

        // simulate remove
        when(graphDisplay.containsNode(groupId2)).thenReturn(false);
        callback.removeResourceItem(viewItem2);
        underTest.update(Delta.createRemovedDelta(LightweightCollections
                .toCollection(viewItem2)), LightweightCollections
                .<Slot> emptyCollection());

        verifyArcRemoved(arcId, groupId1, groupId2);
    }

    @Test
    public void setArcColorOnContainerChangesColorOfExistingArcs() {
        LightweightList<VisualItem> resourceItems = createViewItems(1, 2);

        arcStyleProviderReturnArcType();
        init();

        Arc arc = createArc("arcid", 1, 2);
        arcTypeReturnsArcs(eq(resourceItems.get(0)), arc);
        arcTypeReturnsArcs(eq(resourceItems.get(1)));

        simulateAddViewItems(resourceItems);

        verify(graphDisplay, times(1)).setArcStyle(eq(arc),
                eq(ArcSettings.ARC_COLOR), eq(arcColor.toHex()));

        Color newColor = new Color("#ff0000");
        underTest.getArcItemContainer(arcTypeId).setArcColor(newColor.toHex());

        verify(graphDisplay, times(1)).setArcStyle(eq(arc),
                eq(ArcSettings.ARC_COLOR), eq(newColor.toHex()));
    }

    @Test
    public void setArcColorOnContainerChangesColorOfNewArcs() {
        String arcId = "arcid";
        String groupId1 = "1";
        String groupId2 = "2";
        LightweightList<VisualItem> resourceItems = createViewItems(1, 2);

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

        LightweightList<VisualItem> viewItems = createViewItems(1, 2);
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

        LightweightList<VisualItem> viewItems = createViewItems(1, 2);
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

        LightweightList<VisualItem> viewItems = createViewItems(1, 2);
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

        LightweightList<VisualItem> viewItems = ResourcesTestHelper
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
        arcColor = new Color("#ffffff");
        arcThickness = 1;
        arcStyle = ArcSettings.ARC_STYLE_SOLID;

        borderColor = new Color("#ff0000");
        backgroundColor = new Color("#ff0000");

        when(arcStyleProvider.getArcTypes()).thenReturn(
                LightweightCollections.<ArcType> emptyCollection());

        when(arcType.getArcTypeID()).thenReturn(arcTypeId);
        when(arcType.getDefaultArcColor()).thenReturn(arcColor.toHex());
        when(arcType.getDefaultArcStyle()).thenReturn(arcStyle);
        when(arcType.getDefaultArcThickness()).thenReturn(arcThickness);

        when(resourceCategorizer.getCategory(any(Resource.class))).thenReturn(
                TestResourceSetFactory.TYPE_1);

        when(registry.getAutomaticExpander(any(String.class))).thenReturn(
                automaticExpander);
    }

    private void simulateAddViewItems(
            LightweightCollection<VisualItem> viewItems) {
        for (VisualItem viewItem : viewItems) {
            when(graphDisplay.containsNode(viewItem.getId())).thenReturn(true);
            stubColorSlotValues(viewItem);
        }
        callback.addViewItems(viewItems);
        addViewItemToUnderTest(viewItems);
    }

    public void stubColorSlotValues(VisualItem viewItem) {
        when(viewItem.getValue(Graph.NODE_BORDER_COLOR))
                .thenReturn(borderColor);
        when(viewItem.getValue(Graph.NODE_BACKGROUND_COLOR)).thenReturn(
                backgroundColor);
    }

    @After
    public void tearDown() {
        MockitoGWTBridge.tearDown();
    }

    @Test
    public void updateArcsForResourceItems() {
        LightweightList<VisualItem> viewItems = createViewItems(1, 2);

        arcStyleProviderReturnArcType();
        init();
        arcTypeReturnsArcs(any(VisualItem.class));
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
