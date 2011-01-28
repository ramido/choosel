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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.thechiselgroup.choosel.core.client.command.CommandManager;
import org.thechiselgroup.choosel.core.client.geometry.Point;
import org.thechiselgroup.choosel.core.client.persistence.Memento;
import org.thechiselgroup.choosel.core.client.persistence.PersistableRestorationService;
import org.thechiselgroup.choosel.core.client.resources.CombinedResourceSet;
import org.thechiselgroup.choosel.core.client.resources.DefaultResourceSet;
import org.thechiselgroup.choosel.core.client.resources.Resource;
import org.thechiselgroup.choosel.core.client.resources.ResourceCategorizer;
import org.thechiselgroup.choosel.core.client.resources.ResourceManager;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.resources.persistence.ResourceSetAccessor;
import org.thechiselgroup.choosel.core.client.resources.persistence.ResourceSetCollector;
import org.thechiselgroup.choosel.core.client.util.collections.CollectionFactory;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.views.AbstractViewContentDisplay;
import org.thechiselgroup.choosel.core.client.views.DragEnabler;
import org.thechiselgroup.choosel.core.client.views.DragEnablerFactory;
import org.thechiselgroup.choosel.core.client.views.SidePanelSection;
import org.thechiselgroup.choosel.core.client.views.ViewContentDisplayAction;
import org.thechiselgroup.choosel.core.client.views.ViewContentDisplayCallback;
import org.thechiselgroup.choosel.core.client.views.ViewItem;
import org.thechiselgroup.choosel.core.client.views.ViewItemContainer;
import org.thechiselgroup.choosel.core.client.views.slots.Slot;
import org.thechiselgroup.choosel.visualization_component.graph.client.widget.GraphDisplay;
import org.thechiselgroup.choosel.visualization_component.graph.client.widget.GraphDisplayLoadingFailureEvent;
import org.thechiselgroup.choosel.visualization_component.graph.client.widget.GraphDisplayLoadingFailureEventHandler;
import org.thechiselgroup.choosel.visualization_component.graph.client.widget.GraphDisplayReadyEvent;
import org.thechiselgroup.choosel.visualization_component.graph.client.widget.GraphDisplayReadyEventHandler;
import org.thechiselgroup.choosel.visualization_component.graph.client.widget.GraphLayouts;
import org.thechiselgroup.choosel.visualization_component.graph.client.widget.GraphWidget;
import org.thechiselgroup.choosel.visualization_component.graph.client.widget.Node;
import org.thechiselgroup.choosel.visualization_component.graph.client.widget.NodeDragEvent;
import org.thechiselgroup.choosel.visualization_component.graph.client.widget.NodeDragHandleMouseDownEvent;
import org.thechiselgroup.choosel.visualization_component.graph.client.widget.NodeDragHandleMouseDownHandler;
import org.thechiselgroup.choosel.visualization_component.graph.client.widget.NodeDragHandleMouseMoveEvent;
import org.thechiselgroup.choosel.visualization_component.graph.client.widget.NodeDragHandleMouseMoveHandler;
import org.thechiselgroup.choosel.visualization_component.graph.client.widget.NodeDragHandler;
import org.thechiselgroup.choosel.visualization_component.graph.client.widget.NodeEvent;
import org.thechiselgroup.choosel.visualization_component.graph.client.widget.NodeMenuItemClickedHandler;
import org.thechiselgroup.choosel.visualization_component.graph.client.widget.NodeMouseClickEvent;
import org.thechiselgroup.choosel.visualization_component.graph.client.widget.NodeMouseClickHandler;
import org.thechiselgroup.choosel.visualization_component.graph.client.widget.NodeMouseOutEvent;
import org.thechiselgroup.choosel.visualization_component.graph.client.widget.NodeMouseOutHandler;
import org.thechiselgroup.choosel.visualization_component.graph.client.widget.NodeMouseOverEvent;
import org.thechiselgroup.choosel.visualization_component.graph.client.widget.NodeMouseOverHandler;

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

    public static class DefaultDisplay extends GraphWidget implements
            GraphDisplay {

        // TODO why is size needed in the first place??
        public DefaultDisplay() {
            this(400, 300);
        }

        public DefaultDisplay(int width, int height) {
            super(width, height);
        }

    }

    private class GraphEventHandler extends ViewToIndividualItemEventForwarder
            implements NodeMouseOverHandler, NodeMouseOutHandler,
            NodeMouseClickHandler, MouseMoveHandler, NodeDragHandler,
            NodeDragHandleMouseDownHandler, NodeDragHandleMouseMoveHandler {

        // XXX find cleaner solution that maps to nodes
        private DragEnabler dragEnabler;

        @Override
        public void onDrag(NodeDragEvent event) {
            commandManager.addExecutedCommand(new MoveNodeCommand(graphDisplay,
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
            for (ViewItem item : getCallback().getViewItems()) {
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
            commandManager.execute(new GraphLayoutCommand(graphDisplay, layout,
                    getAllNodes()));
        }

        @Override
        public String getLabel() {
            return layout;
        }
    }

    private static final String MEMENTO_ARC_ITEM_CONTAINERS_CHILD = "arcItemContainers";

    private static final String MEMENTO_NODE_LOCATIONS_CHILD = "nodeLocations";

    private static final String MEMENTO_X = "x";

    private static final String MEMENTO_Y = "y";

    // TODO move
    public static String getArcId(String arcType, String sourceId,
            String targetId) {
        // FIXME this needs escaping of special characters to work properly
        return arcType + ":" + sourceId + "_" + targetId;
    }

    // advanced node class: (incoming, outgoing, expanded: state machine)

    private ArcTypeProvider arcStyleProvider;

    private final CommandManager commandManager;

    private final GraphDisplay graphDisplay;

    public DragEnablerFactory dragEnablerFactory;

    private boolean ready = false;

    private GraphExpansionRegistry registry;

    private ResourceCategorizer resourceCategorizer;

    private ResourceManager resourceManager;

    private CombinedResourceSet nodeResources = new CombinedResourceSet(
            new DefaultResourceSet());

    private Map<String, ArcItemContainer> arcItemContainersByArcTypeID = CollectionFactory
            .createStringMap();

    @Inject
    public GraphViewContentDisplay(GraphDisplay display,
            CommandManager commandManager, ResourceManager resourceManager,
            DragEnablerFactory dragEnablerFactory,
            ResourceCategorizer resourceCategorizer,
            ArcTypeProvider arcStyleProvider, GraphExpansionRegistry registry) {

        assert display != null;
        assert commandManager != null;
        assert resourceManager != null;
        assert dragEnablerFactory != null;
        assert resourceCategorizer != null;
        assert arcStyleProvider != null;
        assert registry != null;

        this.arcStyleProvider = arcStyleProvider;
        this.resourceCategorizer = resourceCategorizer;
        graphDisplay = display;
        this.commandManager = commandManager;
        this.resourceManager = resourceManager;
        this.dragEnablerFactory = dragEnablerFactory;
        this.registry = registry;

        /*
         * we init the arc type containers early so they are available for UI
         * customization in Choosel applications.
         */
        initArcTypeContainers();
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

    private GraphItem createGraphNodeItem(ViewItem viewItem) {
        // TODO get from group id
        String type = getCategory(viewItem.getResourceSet().getFirstResource());

        GraphItem graphItem = new GraphItem(viewItem, type, graphDisplay);

        graphDisplay.addNode(graphItem.getNode());
        positionNode(graphItem.getNode());

        // TODO re-enable
        // TODO remove once new drag and drop mechanism works...
        graphDisplay
                .setNodeStyle(graphItem.getNode(), "showDragImage", "false");

        graphDisplay.setNodeStyle(graphItem.getNode(), "showArrow", registry
                .getNodeMenuEntries(type).isEmpty() ? "false" : "true");

        registry.getAutomaticExpander(type).expand(viewItem, this);

        nodeResources.addResourceSet(viewItem.getResourceSet());

        viewItem.setDisplayObject(graphItem);

        return graphItem;
    }

    // TODO encapsulate in display, use dependency injection
    @Override
    protected Widget createWidget() {
        return graphDisplay.asWidget();
    }

    // TODO better caching?
    // default visibility for test case use
    List<Node> getAllNodes() {
        List<Node> result = new ArrayList<Node>();
        for (ViewItem viewItem : getCallback().getViewItems()) {
            result.add(getNodeFromResourceItem(viewItem));
        }
        return result;
    }

    @Override
    public ResourceSet getAllResources() {
        return nodeResources;
    }

    public ArcItemContainer getArcItemContainer(String arcTypeID) {
        assert arcTypeID != null;
        assert arcItemContainersByArcTypeID.containsKey(arcTypeID);

        return arcItemContainersByArcTypeID.get(arcTypeID);
    }

    public Iterable<ArcItemContainer> getArcItemContainers() {
        return arcItemContainersByArcTypeID.values();
    }

    @Override
    public String getCategory(Resource resource) {
        return resourceCategorizer.getCategory(resource);
    }

    @Override
    public GraphDisplay getDisplay() {
        return graphDisplay;
    }

    private ViewItem getGraphItem(Node node) {
        return getCallback().getViewItem(node.getId());
    }

    private ViewItem getGraphItem(NodeEvent<?> event) {
        return getGraphItem(event.getNode());
    }

    private Node getNodeFromResourceItem(ViewItem resourceItem) {
        return ((GraphItem) resourceItem.getDisplayObject()).getNode();
    }

    @Override
    public Resource getResourceByUri(String value) {
        return nodeResources.getByUri(value);
    }

    private ViewItem getResourceItem(Node node) {
        return getGraphItem(node);
    }

    @Override
    public LightweightCollection<ViewItem> getResourceItems(
            Iterable<Resource> resources) {

        return getCallback().getViewItems(resources);
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
        return new Slot[] { GraphVisualization.NODE_LABEL_SLOT,
                GraphVisualization.NODE_BORDER_COLOR_SLOT,
                GraphVisualization.NODE_BACKGROUND_COLOR_SLOT };
    }

    @Override
    public void init(ViewContentDisplayCallback callback) {
        super.init(callback);

        initStateChangeHandlers();
    }

    private void initArcTypeContainers() {
        ViewItemContainer context = new ViewItemContainer() {

            @Override
            public boolean containsViewItem(String viewItemId) {
                return getCallback().containsViewItem(viewItemId);
            }

            @Override
            public ViewItem getViewItem(String viewItemId) {
                return getCallback().getViewItem(viewItemId);
            }

            @Override
            public LightweightCollection<ViewItem> getViewItems() {
                return getCallback().getViewItems();
            }

            @Override
            public LightweightCollection<ViewItem> getViewItems(
                    Iterable<Resource> resources) {
                return getCallback().getViewItems(resources);
            }
        };

        for (ArcType arcType : arcStyleProvider.getArcTypes()) {
            arcItemContainersByArcTypeID.put(arcType.getArcTypeID(),
                    new ArcItemContainer(arcType, graphDisplay, context));
        }
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

    private void initStateChangeHandlers() {
        graphDisplay
                .addGraphDisplayReadyHandler(new GraphDisplayReadyEventHandler() {
                    @Override
                    public void onWidgetReady(GraphDisplayReadyEvent event) {
                        ready = true;

                        GraphEventHandler handler = new GraphEventHandler();

                        graphDisplay.addEventHandler(
                                NodeDragHandleMouseDownEvent.TYPE, handler);
                        graphDisplay.addEventHandler(NodeMouseOverEvent.TYPE,
                                handler);
                        graphDisplay.addEventHandler(NodeMouseOutEvent.TYPE,
                                handler);
                        graphDisplay.addEventHandler(NodeMouseClickEvent.TYPE,
                                handler);
                        graphDisplay.addEventHandler(NodeDragEvent.TYPE,
                                handler);
                        graphDisplay.addEventHandler(MouseMoveEvent.getType(),
                                handler);

                        initNodeMenuItems();
                    }
                });
        graphDisplay
                .addGraphDisplayLoadingFailureHandler(new GraphDisplayLoadingFailureEventHandler() {
                    @Override
                    public void onLoadingFailure(
                            GraphDisplayLoadingFailureEvent event) {
                        // TODO handle loading failures
                    }
                });
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

        if (getCallback().getViewItems().size() > 1) {
            return;
        }

        Widget displayWidget = graphDisplay.asWidget();
        if (displayWidget == null) {
            return; // for tests
        }

        int height = displayWidget.getOffsetHeight();
        int width = displayWidget.getOffsetWidth();

        graphDisplay.setLocation(node, new Point(width / 2, height / 2));
    }

    private void registerNodeMenuItem(String category, String menuLabel,
            final GraphNodeExpander nodeExpander) {

        graphDisplay.addNodeMenuItemHandler(menuLabel,
                new NodeMenuItemClickedHandler() {
                    @Override
                    public void onNodeMenuItemClicked(Node node) {
                        nodeExpander.expand(getResourceItem(node),
                                GraphViewContentDisplay.this);
                    }
                }, category);
    }

    private void removeViewItem(ViewItem viewItem) {
        assert viewItem != null;

        nodeResources.removeResourceSet(viewItem.getResourceSet());
        for (ArcItemContainer arcItemContainer : arcItemContainersByArcTypeID
                .values()) {
            arcItemContainer.removeViewItem(viewItem);
        }
        graphDisplay.removeNode(getNodeFromResourceItem(viewItem));
    }

    @Override
    public void restore(Memento state,
            PersistableRestorationService restorationService,
            ResourceSetAccessor accessor) {

        restoreArcItemContainers(restorationService, accessor,
                state.getChild(MEMENTO_ARC_ITEM_CONTAINERS_CHILD));
        restoreNodeLocations(state.getChild(MEMENTO_NODE_LOCATIONS_CHILD));
    }

    private void restoreArcItemContainers(
            PersistableRestorationService restorationService,
            ResourceSetAccessor accessor, Memento child) {
        for (Entry<String, Memento> entry : child.getChildren().entrySet()) {
            arcItemContainersByArcTypeID.get(entry.getKey()).restore(
                    entry.getValue(), restorationService, accessor);
        }
    }

    private void restoreNodeLocations(Memento state) {
        LightweightCollection<ViewItem> resourceItems = getCallback()
                .getViewItems();
        for (ViewItem resourceItem : resourceItems) {
            GraphItem item = (GraphItem) resourceItem.getDisplayObject();
            Memento nodeMemento = state.getChild(resourceItem.getViewItemID());
            Point location = new Point(
                    (Integer) nodeMemento.getValue(MEMENTO_X),
                    (Integer) nodeMemento.getValue(MEMENTO_Y));

            graphDisplay.setLocation(item.getNode(), location);
        }
    }

    @Override
    public Memento save(ResourceSetCollector resourceSetCollector) {
        Memento result = new Memento();

        result.addChild(MEMENTO_NODE_LOCATIONS_CHILD, saveNodeLocations());
        result.addChild(MEMENTO_ARC_ITEM_CONTAINERS_CHILD,
                saveArcTypeContainers(resourceSetCollector));

        return result;
    }

    private Memento saveArcTypeContainers(
            ResourceSetCollector resourceSetCollector) {

        Memento memento = new Memento();
        for (Entry<String, ArcItemContainer> entry : arcItemContainersByArcTypeID
                .entrySet()) {
            memento.addChild(entry.getKey(),
                    entry.getValue().save(resourceSetCollector));
        }
        return memento;
    }

    private Memento saveNodeLocations() {
        Memento state = new Memento();

        LightweightCollection<ViewItem> viewItems = getCallback()
                .getViewItems();
        for (ViewItem viewItem : viewItems) {
            GraphItem item = (GraphItem) viewItem.getDisplayObject();

            Point location = graphDisplay.getLocation(item.getNode());

            Memento nodeMemento = new Memento();
            nodeMemento.setValue(MEMENTO_X, location.x);
            nodeMemento.setValue(MEMENTO_Y, location.y);

            state.addChild(viewItem.getViewItemID(), nodeMemento);
        }
        return state;
    }

    /**
     * If the arc type becomes invisible, all arcs of this arcType from the view
     * and arcs of this arc type are not shown any more. If the arc types
     * becomes visible, all arcs of this type are added.
     */
    // TODO expose arc type configurations and use listener mechanism
    public void setArcTypeVisible(String arcTypeId, boolean visible) {
        assert arcTypeId != null;
        assert arcItemContainersByArcTypeID.containsKey(arcTypeId);

        arcItemContainersByArcTypeID.get(arcTypeId).setVisible(visible);
    }

    @Override
    public void update(LightweightCollection<ViewItem> addedViewItems,
            LightweightCollection<ViewItem> updatedViewItems,
            LightweightCollection<ViewItem> removedViewItems,
            LightweightCollection<Slot> changedSlots) {

        for (ViewItem addedItem : addedViewItems) {
            createGraphNodeItem(addedItem);
            updateNode(addedItem);
        }

        updateArcsForViewItems(addedViewItems);

        for (ViewItem updatedItem : updatedViewItems) {
            updateNode(updatedItem);
        }

        for (ViewItem viewItem : removedViewItems) {
            removeViewItem(viewItem);
        }

        if (!changedSlots.isEmpty()) {
            LightweightCollection<ViewItem> viewItems = getCallback()
                    .getViewItems();
            for (ViewItem viewItem : viewItems) {
                updateNode(viewItem);
            }
        }
    }

    /**
     * Updates the arc items and arcs for the given view items. The view items
     * must already be contained in the view content display (i.e. they have
     * been added already and their nodes must be visible).
     */
    @Override
    public void updateArcsForViewItems(LightweightCollection<ViewItem> viewItems) {
        assert viewItems != null;
        for (ArcItemContainer container : arcItemContainersByArcTypeID.values()) {
            container.update(viewItems);
        }
    }

    private void updateNode(ViewItem viewItem) {
        ((GraphItem) viewItem.getDisplayObject()).updateNode();
    }
}
