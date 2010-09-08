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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
import org.thechiselgroup.choosel.client.views.AbstractViewContentDisplay;
import org.thechiselgroup.choosel.client.views.DragEnabler;
import org.thechiselgroup.choosel.client.views.DragEnablerFactory;
import org.thechiselgroup.choosel.client.views.ResourceItem;
import org.thechiselgroup.choosel.client.views.SlotResolver;
import org.thechiselgroup.choosel.client.views.ViewContentDisplayAction;
import org.thechiselgroup.choosel.client.views.ViewContentDisplayCallback;

import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
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
            Collection<ResourceItem> values = nodeIdToResourceItemMap.values();
            for (ResourceItem item : values) {
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

    // TODO move to ncbo stuff
    public static final String ARC_TYPE_MAPPING = "mapping";

    private static final String MEMENTO_X = "x";

    private static final String MEMENTO_Y = "y";

    // advanced node class: (incoming, outgoing, expanded: state machine)

    private ArcStyleProvider arcStyleProvider;

    private final CommandManager commandManager;

    private final Display display;

    public DragEnablerFactory dragEnablerFactory;

    private List<Arc> arcList = new ArrayList<Arc>();

    private final Map<String, ResourceItem> nodeIdToResourceItemMap = new HashMap<String, ResourceItem>();

    private boolean ready = false;

    private GraphExpansionRegistry registry;

    private ResourceCategorizer resourceCategorizer;

    private ResourceManager resourceManager;

    private CombinedResourceSet nodeResources = new CombinedResourceSet(
            new DefaultResourceSet());

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

    private void createDisplayObjectForResourceItem(ResourceItem resourceItem) {
        String label = (String) resourceItem
                .getResourceValue(SlotResolver.GRAPH_LABEL_SLOT);

        String category = getCategory(resourceItem.getResourceSet()
                .getFirstResource());

        String backgroundColor = (String) resourceItem
                .getResourceValue(SlotResolver.GRAPH_NODE_BACKGROUND_COLOR_SLOT);

        String borderColor = (String) resourceItem
                .getResourceValue(SlotResolver.GRAPH_NODE_BORDER_COLOR_SLOT);

        GraphItem gItem = new GraphItem(resourceItem.getResourceSet(), label,
                category, display);

        nodeIdToResourceItemMap.put(gItem.getNode().getId(), resourceItem);

        display.addNode(gItem.getNode());
        positionNode(gItem.getNode());

        // needs to be done after node is added to graph
        gItem.setDefaultColors(backgroundColor, borderColor);

        // TODO remove once new drag and drop mechanism works...
        display.setNodeStyle(gItem.getNode(), "showDragImage", "true");

        display.setNodeStyle(gItem.getNode(), "showArrow", registry
                .getNodeMenuEntries(category).isEmpty() ? "false" : "true");

        registry.getAutomaticExpander(category).expand(resourceItem, this);

        nodeResources.addResourceSet(resourceItem.getResourceSet());

        resourceItem.setDisplayObject(gItem);
    }

    // TODO encapsulate in display, use dependency injection
    @Override
    protected Widget createWidget() {
        return display.asWidget();
    }

    @Override
    public List<ViewContentDisplayAction> getActions() {
        List<ViewContentDisplayAction> actions = new ArrayList<ViewContentDisplayAction>();

        actions.add(new GraphLayoutAction(GraphLayouts.CIRCLE_LAYOUT));
        // actions.add(new
        // GraphLayoutAction(GraphLayouts.HORIZONTAL_TREE_LAYOUT));
        // actions.add(new
        // GraphLayoutAction(GraphLayouts.VERTICAL_TREE_LAYOUT));
        // actions.add(new GraphLayoutAction(GraphLayouts.RADIAL_LAYOUT));
        // actions.add(new GraphLayoutAction(GraphLayouts.SPRING_LAYOUT));
        actions.add(new GraphLayoutAction(GraphLayouts.INDENTED_TREE_LAYOUT));
        actions.add(new GraphLayoutAction(GraphLayouts.GRID_LAYOUT_BY_NODE_ID));
        actions.add(new GraphLayoutAction(GraphLayouts.GRID_LAYOUT_BY_NODE_TYPE));
        actions.add(new GraphLayoutAction(GraphLayouts.GRID_LAYOUT_ALPHABETICAL));
        actions.add(new GraphLayoutAction(GraphLayouts.GRID_LAYOUT_BY_ARC_COUNT));
        // actions.add(new GraphLayoutAction(GraphLayouts.HORIZONTAL_LAYOUT));
        // actions.add(new GraphLayoutAction(GraphLayouts.VERTICAL_LAYOUT));
        actions.add(new GraphLayoutAction(GraphLayouts.FORCE_DIRECTED_LAYOUT));

        return actions;
    }

    // default visibility for test case use
    List<Node> getAllNodes() {
        List<Node> result = new ArrayList<Node>();
        for (ResourceItem resourceItem : nodeIdToResourceItemMap.values()) {
            result.add(getNodeFromResourceItem(resourceItem));
        }
        return result;
    }

    @Override
    public ResourceSet getAllResources() {
        return nodeResources;
    }

    public String getArcId(String arcType, String sourceId, String targetId) {
        // FIXME this needs escaping of special characters to work properly
        return arcType + ":" + sourceId + "_" + targetId;
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
        return nodeIdToResourceItemMap.get(node.getId());
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

    @Override
    public String[] getSlotIDs() {
        return new String[] { SlotResolver.DESCRIPTION_SLOT,
                SlotResolver.GRAPH_LABEL_SLOT,
                SlotResolver.GRAPH_NODE_BORDER_COLOR_SLOT,
                SlotResolver.GRAPH_NODE_BACKGROUND_COLOR_SLOT };
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

        assert nodeIdToResourceItemMap.containsKey(node.getId());
        if (nodeIdToResourceItemMap.size() > 1) {
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
        assert nodeIdToResourceItemMap.containsValue(resourceItem);

        Node node = getNodeFromResourceItem(resourceItem);

        nodeResources.removeResourceSet(resourceItem.getResourceSet());
        nodeIdToResourceItemMap.remove(node.getId());
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
        Iterable<Resource> allResources = getCallback().getAllResources();
        for (Resource resource : allResources) {
            // XXX broken
            // GraphItem item = (GraphItem) getCallback()
            // .getResourceItem(resource);
            // Memento nodeMemento = state.getChild(resource.getUri());
            // Point location = new Point(
            // (Integer) nodeMemento.getValue(MEMENTO_X),
            // (Integer) nodeMemento.getValue(MEMENTO_Y));
            //
            // display.setLocation(item.getNode(), location);
        }
    }

    @Override
    public Memento save() {
        Memento state = new Memento();
        Iterable<Resource> allResources = getCallback().getAllResources();
        for (Resource resource : allResources) {
            // XXX broken
            // GraphItem item = (GraphItem) getCallback()
            // .getResourceItem(resource);
            // Point location = display.getLocation(item.getNode());
            //
            // Memento nodeMemento = new Memento();
            // nodeMemento.setValue(MEMENTO_X, location.x);
            // nodeMemento.setValue(MEMENTO_Y, location.y);
            //
            // state.addChild(resource.getUri(), nodeMemento);
        }
        return state;
    }

    @Override
    public void showArc(String arcType, String sourceId, String targetId) {
        assert arcType != null;
        assert sourceId != null;
        assert targetId != null;

        String arcId = getArcId(arcType, sourceId, targetId);

        if (display.containsArc(arcId)) {
            return;
        }

        Arc arc = new Arc(arcId, sourceId, targetId, arcType);

        display.addArc(arc);
        arcList.add(arc);

        display.setArcStyle(arc, GraphDisplay.ARC_COLOR,
                arcStyleProvider.getArcColor(arcType));
        display.setArcStyle(arc, GraphDisplay.ARC_STYLE,
                arcStyleProvider.getArcStyle(arcType));
    }

    @Override
    public void update(Set<ResourceItem> addedResourceItems,
            Set<ResourceItem> updatedResourceItems,
            Set<ResourceItem> removedResourceItems) {

        for (ResourceItem addedItem : addedResourceItems) {
            createDisplayObjectForResourceItem(addedItem);
            updateNode(addedItem);
        }

        for (ResourceItem updatedItem : updatedResourceItems) {
            updateNode(updatedItem);
        }

        for (ResourceItem resourceItem : removedResourceItems) {
            removeNode(resourceItem);
        }
    }

    private void updateNode(ResourceItem resourceItem) {
        ((GraphItem) resourceItem.getDisplayObject()).updateNode(resourceItem
                .getStatus());
    }
}
