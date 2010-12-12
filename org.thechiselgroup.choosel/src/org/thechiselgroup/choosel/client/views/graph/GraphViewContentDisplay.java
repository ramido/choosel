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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.thechiselgroup.choosel.client.command.CommandManager;
import org.thechiselgroup.choosel.client.geometry.Point;
import org.thechiselgroup.choosel.client.persistence.Memento;
import org.thechiselgroup.choosel.client.resources.CombinedResourceSet;
import org.thechiselgroup.choosel.client.resources.DefaultResourceSet;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceCategorizer;
import org.thechiselgroup.choosel.client.resources.ResourceManager;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.ui.WidgetAdaptable;
import org.thechiselgroup.choosel.client.ui.widget.graph.Arc;
import org.thechiselgroup.choosel.client.ui.widget.graph.GraphDisplay;
import org.thechiselgroup.choosel.client.ui.widget.graph.GraphLayouts;
import org.thechiselgroup.choosel.client.ui.widget.graph.GraphWidget;
import org.thechiselgroup.choosel.client.ui.widget.graph.GraphWidgetReadyEvent;
import org.thechiselgroup.choosel.client.ui.widget.graph.GraphWidgetReadyHandler;
import org.thechiselgroup.choosel.client.ui.widget.graph.Node;
import org.thechiselgroup.choosel.client.ui.widget.graph.NodeDragEvent;
import org.thechiselgroup.choosel.client.ui.widget.graph.NodeDragHandleMouseDownEvent;
import org.thechiselgroup.choosel.client.ui.widget.graph.NodeDragHandleMouseDownHandler;
import org.thechiselgroup.choosel.client.ui.widget.graph.NodeDragHandleMouseMoveEvent;
import org.thechiselgroup.choosel.client.ui.widget.graph.NodeDragHandleMouseMoveHandler;
import org.thechiselgroup.choosel.client.ui.widget.graph.NodeDragHandler;
import org.thechiselgroup.choosel.client.ui.widget.graph.NodeEvent;
import org.thechiselgroup.choosel.client.ui.widget.graph.NodeMenuItemClickedHandler;
import org.thechiselgroup.choosel.client.ui.widget.graph.NodeMouseClickEvent;
import org.thechiselgroup.choosel.client.ui.widget.graph.NodeMouseClickHandler;
import org.thechiselgroup.choosel.client.ui.widget.graph.NodeMouseOutEvent;
import org.thechiselgroup.choosel.client.ui.widget.graph.NodeMouseOutHandler;
import org.thechiselgroup.choosel.client.ui.widget.graph.NodeMouseOverEvent;
import org.thechiselgroup.choosel.client.ui.widget.graph.NodeMouseOverHandler;
import org.thechiselgroup.choosel.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.client.views.AbstractViewContentDisplay;
import org.thechiselgroup.choosel.client.views.DataType;
import org.thechiselgroup.choosel.client.views.DragEnabler;
import org.thechiselgroup.choosel.client.views.DragEnablerFactory;
import org.thechiselgroup.choosel.client.views.ResourceItem;
import org.thechiselgroup.choosel.client.views.SidePanelSection;
import org.thechiselgroup.choosel.client.views.Slot;
import org.thechiselgroup.choosel.client.views.ViewContentDisplayAction;
import org.thechiselgroup.choosel.client.views.ViewContentDisplayCallback;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

// TODO separate out ncbo specific stuff and service calls
// TODO register listener for double click on node --> change expansion state
public class GraphViewContentDisplay extends AbstractViewContentDisplay
        implements GraphNodeExpansionCallback {

    public static class DefaultDisplay extends GraphWidget implements Display {

        // TODO why is size needed in the first place??
        public DefaultDisplay() {
            this(400, 300);
        }

        public DefaultDisplay(int width, int height) {
            super(width, height);
        }

        @Override
        public Widget asWidget() {
            return this;
        }

    }

    public static interface Display extends WidgetAdaptable, GraphDisplay {
    }

    private class GraphEventHandler extends ViewToIndividualItemEventForwarder
            implements NodeMouseOverHandler, NodeMouseOutHandler,
            NodeMouseClickHandler, MouseMoveHandler, NodeDragHandler,
            NodeDragHandleMouseDownHandler, NodeDragHandleMouseMoveHandler {

        // XXX find cleaner solution that maps to nodes
        private DragEnabler dragEnabler;

        @Override
        public void onDrag(NodeDragEvent event) {
            commandManager.addExecutedCommand(new MoveNodeCommand(display,
                    event.getNode(), new Point(event.getStartX(), event
                            .getStartY()), new Point(event.getEndX(), event
                            .getEndY())));
        }

        @Override
        public void onMouseClick(NodeMouseClickEvent event) {
            getCallback().switchSelection(getGraphItem(event).getResourceSet());
        }

        @Override
        public void onMouseDown(NodeDragHandleMouseDownEvent event) {
            dragEnabler = dragEnablerFactory
                    .createDragEnabler(getGraphItem(event.getNode()));

            dragEnabler.createTransparentDragProxy(event.getMouseX()
                    + asWidget().getAbsoluteLeft(), event.getMouseY()
                    + asWidget().getAbsoluteTop());
        }

        @Override
        public void onMouseMove(MouseMoveEvent event) {
            // TODO restrict to mouse move for current graph item
            for (ResourceItem item : getCallback().getResourceItems()) {
                // TODO relative to root pane instead of client area
                item.getPopupManager().onMouseMove(event.getClientX(),
                        event.getClientY());
            }
        }

        @Override
        public void onMouseMove(NodeDragHandleMouseMoveEvent event) {
            dragEnabler.forwardMouseMove(event.getMouseX()
                    + asWidget().getAbsoluteLeft(), event.getMouseY()
                    + asWidget().getAbsoluteTop());
        }

        @Override
        public void onMouseOut(NodeMouseOutEvent event) {
            onMouseOut(getGraphItem(event), event.getMouseX(),
                    event.getMouseY());
        }

        @Override
        public void onMouseOver(NodeMouseOverEvent event) {
            onMouseOver(getGraphItem(event), event.getMouseX(),
                    event.getMouseY());
        }

    }

    public class GraphLayoutAction implements ViewContentDisplayAction {

        private String layout;

        public GraphLayoutAction(String layout) {
            this.layout = layout;
        }

        @Override
        public void execute() {
            commandManager.execute(new GraphLayoutCommand(display, layout,
                    getAllNodes()));
        }

        @Override
        public String getLabel() {
            return layout;
        }
    }

    private static final String MEMENTO_X = "x";

    private static final String MEMENTO_Y = "y";

    // advanced node class: (incoming, outgoing, expanded: state machine)

    // TODO move
    public static String getArcId(String arcType, String sourceId,
            String targetId) {
        // FIXME this needs escaping of special characters to work properly
        return arcType + ":" + sourceId + "_" + targetId;
    }

    private ArcStyleProvider arcStyleProvider;

    private final CommandManager commandManager;

    private final Display display;

    public DragEnablerFactory dragEnablerFactory;

    private List<Arc> arcList = new ArrayList<Arc>();

    private boolean ready = false;

    private GraphExpansionRegistry registry;

    private ResourceCategorizer resourceCategorizer;

    private ResourceManager resourceManager;

    private CombinedResourceSet nodeResources = new CombinedResourceSet(
            new DefaultResourceSet());

    public static final Slot NODE_BORDER_COLOR_SLOT = new Slot(
            "nodeBorderColor", "Node Border Color", DataType.COLOR);

    public static final Slot NODE_BACKGROUND_COLOR_SLOT = new Slot(
            "nodeBackgroundColor", "Node Color", DataType.COLOR);

    public static final Slot NODE_LABEL_SLOT = new Slot("nodeLabel",
            "Node Label", DataType.TEXT);

    /**
     * <b>IMPLEMENTATION NOTE</b>: lots of items are added & removed so we use a
     * linked list.
     */
    private List<ArcItem> arcItems = new LinkedList<ArcItem>();

    @Inject
    public GraphViewContentDisplay(Display display,
            CommandManager commandManager, ResourceManager resourceManager,
            DragEnablerFactory dragEnablerFactory,
            ResourceCategorizer resourceCategorizer,
            ArcStyleProvider arcStyleProvider, GraphExpansionRegistry registry) {

        assert display != null;
        assert commandManager != null;
        assert resourceManager != null;
        assert dragEnablerFactory != null;
        assert resourceCategorizer != null;
        assert arcStyleProvider != null;
        assert registry != null;

        this.arcStyleProvider = arcStyleProvider;
        this.resourceCategorizer = resourceCategorizer;
        this.display = display;
        this.commandManager = commandManager;
        this.resourceManager = resourceManager;
        this.dragEnablerFactory = dragEnablerFactory;
        this.registry = registry;
    }

    @Override
    public void addAutomaticResource(Resource resource) {
        getCallback().getAutomaticResourceSet().add(resource);
    }

    @Override
    public void checkResize() {
    }

    @Override
    public boolean containsResourceWithUri(String resourceUri) {
        return nodeResources.containsResourceWithUri(resourceUri);
    }

    private GraphItem createGraphNodeItem(ResourceItem resourceItem) {
        // TODO get from group id
        String type = getCategory(resourceItem.getResourceSet()
                .getFirstResource());

        GraphItem graphItem = new GraphItem(resourceItem, type, display);

        display.addNode(graphItem.getNode());
        positionNode(graphItem.getNode());

        // TODO re-enable
        // TODO remove once new drag and drop mechanism works...
        display.setNodeStyle(graphItem.getNode(), "showDragImage", "false");

        display.setNodeStyle(graphItem.getNode(), "showArrow", registry
                .getNodeMenuEntries(type).isEmpty() ? "false" : "true");

        registry.getAutomaticExpander(type).expand(resourceItem, this);

        nodeResources.addResourceSet(resourceItem.getResourceSet());

        resourceItem.setDisplayObject(graphItem);

        return graphItem;
    }

    // TODO encapsulate in display, use dependency injection
    @Override
    protected Widget createWidget() {
        return display.asWidget();
    }

    // TODO better caching?
    // default visibility for test case use
    List<Node> getAllNodes() {
        List<Node> result = new ArrayList<Node>();
        for (ResourceItem resourceItem : getCallback().getResourceItems()) {
            result.add(getNodeFromResourceItem(resourceItem));
        }
        return result;
    }

    @Override
    public ResourceSet getAllResources() {
        return nodeResources;
    }

    @Override
    public String getCategory(Resource resource) {
        return resourceCategorizer.getCategory(resource);
    }

    @Override
    public Display getDisplay() {
        return display;
    }

    private ResourceItem getGraphItem(Node node) {
        return getCallback().getResourceItemByGroupID(node.getId());
    }

    private ResourceItem getGraphItem(NodeEvent<?> event) {
        return getGraphItem(event.getNode());
    }

    private Node getNodeFromResourceItem(ResourceItem resourceItem) {
        return ((GraphItem) resourceItem.getDisplayObject()).getNode();
    }

    @Override
    public Resource getResourceByUri(String value) {
        return nodeResources.getByUri(value);
    }

    private ResourceItem getResourceItem(Node node) {
        return getGraphItem(node);
    }

    @Override
    public ResourceManager getResourceManager() {
        return resourceManager;
    }

    // TODO cleanup
    @Override
    public SidePanelSection[] getSidePanelSections() {
        List<ViewContentDisplayAction> actions = new ArrayList<ViewContentDisplayAction>();

        actions.add(new GraphLayoutAction(GraphLayouts.CIRCLE_LAYOUT));
        actions.add(new GraphLayoutAction(GraphLayouts.HORIZONTAL_TREE_LAYOUT));
        actions.add(new GraphLayoutAction(GraphLayouts.VERTICAL_TREE_LAYOUT));
        actions.add(new GraphLayoutAction(GraphLayouts.RADIAL_LAYOUT));
        actions.add(new GraphLayoutAction(GraphLayouts.SPRING_LAYOUT));
        actions.add(new GraphLayoutAction(GraphLayouts.INDENTED_TREE_LAYOUT));
        actions.add(new GraphLayoutAction(GraphLayouts.GRID_LAYOUT_BY_NODE_ID));
        actions.add(new GraphLayoutAction(GraphLayouts.GRID_LAYOUT_BY_NODE_TYPE));
        actions.add(new GraphLayoutAction(GraphLayouts.GRID_LAYOUT_ALPHABETICAL));
        actions.add(new GraphLayoutAction(GraphLayouts.GRID_LAYOUT_BY_ARC_COUNT));
        actions.add(new GraphLayoutAction(GraphLayouts.HORIZONTAL_LAYOUT));
        actions.add(new GraphLayoutAction(GraphLayouts.VERTICAL_LAYOUT));
        actions.add(new GraphLayoutAction(GraphLayouts.FORCE_DIRECTED_LAYOUT));

        VerticalPanel layoutPanel = new VerticalPanel();
        for (final ViewContentDisplayAction action : actions) {
            Button w = new Button(action.getLabel());
            w.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    action.execute();
                }
            });
            layoutPanel.add(w);
        }

        return new SidePanelSection[] { new SidePanelSection("Layouts",
                layoutPanel), };
    }

    @Override
    public Slot[] getSlots() {
        return new Slot[] { GraphViewContentDisplay.NODE_LABEL_SLOT,
                GraphViewContentDisplay.NODE_BORDER_COLOR_SLOT,
                GraphViewContentDisplay.NODE_BACKGROUND_COLOR_SLOT };
    }

    @Override
    public void init(ViewContentDisplayCallback callback) {
        super.init(callback);

        display.addGraphWidgetReadyHandler(new GraphWidgetReadyHandler() {
            @Override
            public void onWidgetReady(GraphWidgetReadyEvent event) {
                ready = true;

                GraphEventHandler handler = new GraphEventHandler();

                display.addEventHandler(NodeDragHandleMouseDownEvent.TYPE,
                        handler);
                display.addEventHandler(NodeMouseOverEvent.TYPE, handler);
                display.addEventHandler(NodeMouseOutEvent.TYPE, handler);
                display.addEventHandler(NodeMouseClickEvent.TYPE, handler);
                display.addEventHandler(NodeDragEvent.TYPE, handler);
                display.addEventHandler(MouseMoveEvent.getType(), handler);

                initNodeMenuItems();
            }

        });
    }

    private void initNodeMenuItems() {
        for (Entry<String, List<NodeMenuEntry>> entry : registry
                .getNodeMenuEntriesByCategory()) {

            String category = entry.getKey();
            for (NodeMenuEntry nodeMenuEntry : entry.getValue()) {
                registerNodeMenuItem(category, nodeMenuEntry.getLabel(),
                        nodeMenuEntry.getExpander());
            }
        }
    }

    @Override
    public boolean isReady() {
        return ready;
    }

    private void positionNode(Node node) {
        // FIXME positioning: FlexVis takes care of positioning nodes into empty
        // space except for first node - if the node is the first node, we put
        // it in the center
        // TODO improve interface to access all resources

        assert node != null;

        if (getCallback().getResourceItems().size() > 1) {
            return;
        }

        Widget displayWidget = display.asWidget();
        if (displayWidget == null) {
            return; // for tests
        }

        int height = displayWidget.getOffsetHeight();
        int width = displayWidget.getOffsetWidth();

        display.setLocation(node, new Point(width / 2, height / 2));
    }

    private void registerNodeMenuItem(String category, String menuLabel,
            final GraphNodeExpander nodeExpander) {

        display.addNodeMenuItemHandler(menuLabel,
                new NodeMenuItemClickedHandler() {
                    @Override
                    public void onNodeMenuItemClicked(Node node) {
                        nodeExpander.expand(getResourceItem(node),
                                GraphViewContentDisplay.this);
                    }
                }, category);
    }

    private void removeNode(ResourceItem resourceItem) {
        Node node = getNodeFromResourceItem(resourceItem);

        nodeResources.removeResourceSet(resourceItem.getResourceSet());
        removeNodeArcs(node);
        display.removeNode(node);
    }

    private void removeNodeArcs(Node node) {
        assert node != null;

        for (Iterator<Arc> it = arcList.iterator(); it.hasNext();) {
            Arc arc = it.next();

            if (arc.getSourceNodeId() == node.getId()
                    || arc.getTargetNodeId() == node.getId()) {

                display.removeArc(arc);
                it.remove();
            }
        }
    }

    @Override
    public void restore(Memento state) {
        LightweightCollection<ResourceItem> resourceItems = getCallback()
                .getResourceItems();
        for (ResourceItem resourceItem : resourceItems) {
            GraphItem item = (GraphItem) resourceItem.getDisplayObject();
            Memento nodeMemento = state.getChild(resourceItem.getGroupID());
            Point location = new Point(
                    (Integer) nodeMemento.getValue(MEMENTO_X),
                    (Integer) nodeMemento.getValue(MEMENTO_Y));

            display.setLocation(item.getNode(), location);
        }
    }

    @Override
    public Memento save() {
        Memento state = new Memento();
        LightweightCollection<ResourceItem> resourceItems = getCallback()
                .getResourceItems();
        for (ResourceItem resourceItem : resourceItems) {
            GraphItem item = (GraphItem) resourceItem.getDisplayObject();

            Point location = display.getLocation(item.getNode());

            Memento nodeMemento = new Memento();
            nodeMemento.setValue(MEMENTO_X, location.x);
            nodeMemento.setValue(MEMENTO_Y, location.y);

            state.addChild(resourceItem.getGroupID(), nodeMemento);
        }
        return state;
    }

    // TODO encapsulate into arc item
    private void showArc(ArcItem arcItem) {
        if (display.containsArc(arcItem.getId())) {
            return;
        }

        Arc arc = new Arc(arcItem.getId(), arcItem.getSourceNodeItemId(),
                arcItem.getTargetNodeItemId(), arcItem.getType());

        display.addArc(arc);
        arcList.add(arc);

        display.setArcStyle(arc, GraphDisplay.ARC_COLOR, arcItem.getColor());
        display.setArcStyle(arc, GraphDisplay.ARC_STYLE, arcItem.getStyle());
    }

    @Override
    public void update(LightweightCollection<ResourceItem> addedResourceItems,
            LightweightCollection<ResourceItem> updatedResourceItems,
            LightweightCollection<ResourceItem> removedResourceItems,
            LightweightCollection<Slot> changedSlots) {

        LightweightCollection<ArcType> arcTypes = arcStyleProvider
                .getArcTypes();

        for (ResourceItem addedItem : addedResourceItems) {
            createGraphNodeItem(addedItem);
            updateNode(addedItem);

            for (ArcType arcType : arcTypes) {
                for (ArcItem arcItem : arcType.getArcItems(addedItem)) {
                    arcItems.add(arcItem);
                }
            }
        }

        // TODO go through arcs to show
        for (Iterator<ArcItem> it = arcItems.iterator(); it.hasNext();) {
            ArcItem arcItem = it.next();

            // TODO also check for target...
            String sourceGroupId = arcItem.getSourceNodeItemId();
            String targetGroupId = arcItem.getTargetNodeItemId();
            if (getCallback().containsResourceItem(sourceGroupId)
                    && getCallback().containsResourceItem(targetGroupId)) {
                showArc(arcItem);
                it.remove();
            }
        }

        for (ResourceItem updatedItem : updatedResourceItems) {
            updateNode(updatedItem);
        }

        for (ResourceItem resourceItem : removedResourceItems) {
            removeNode(resourceItem);
        }
    }

    private void updateNode(ResourceItem resourceItem) {
        ((GraphItem) resourceItem.getDisplayObject()).updateNode();
    }
}
