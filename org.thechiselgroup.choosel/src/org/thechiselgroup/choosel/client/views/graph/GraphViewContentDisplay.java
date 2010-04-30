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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.thechiselgroup.choosel.client.command.CommandManager;
import org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants;
import org.thechiselgroup.choosel.client.domain.ncbo.NCBO;
import org.thechiselgroup.choosel.client.domain.ncbo.NcboUriHelper;
import org.thechiselgroup.choosel.client.error_handling.ErrorHandler;
import org.thechiselgroup.choosel.client.geometry.Point;
import org.thechiselgroup.choosel.client.persistence.Memento;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceCategorizer;
import org.thechiselgroup.choosel.client.resources.ResourceManager;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.UriList;
import org.thechiselgroup.choosel.client.resources.ui.DetailsWidgetHelper;
import org.thechiselgroup.choosel.client.ui.WidgetAdaptable;
import org.thechiselgroup.choosel.client.ui.popup.PopupManager;
import org.thechiselgroup.choosel.client.ui.popup.PopupManagerFactory;
import org.thechiselgroup.choosel.client.ui.widget.graph.Arc;
import org.thechiselgroup.choosel.client.ui.widget.graph.GraphDisplay;
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
import org.thechiselgroup.choosel.client.views.Layer;
import org.thechiselgroup.choosel.client.views.ResourceItem;
import org.thechiselgroup.choosel.client.views.SlotResolver;
import org.thechiselgroup.choosel.client.views.ViewContentDisplayCallback;

import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.name.Named;

// TODO separate out ncbo specific stuff and service calls
// TODO register listener for double click on node --> change expansion state
public class GraphViewContentDisplay extends AbstractViewContentDisplay {

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
	    getCallback().switchSelection(getGraphItem(event).getResource());
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
	    Collection<GraphItem> values = nodeIdToGraphItemMap.values();
	    for (GraphItem graphItem : values) {
		// TODO relative to root pane instead of client area
		graphItem.getPopupManager().onMouseMove(event.getClientX(),
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
	    onMouseOut(getGraphItem(event), event.getMouseX(), event
		    .getMouseY());
	}

	@Override
	public void onMouseOver(NodeMouseOverEvent event) {
	    onMouseOver(getGraphItem(event), event.getMouseX(), event
		    .getMouseY());
	}

    }

    public static interface GraphNodeExpander {

	void expand(Resource resource,
		GraphNodeExpansionCallback expansionCallback);

    }

    public static interface GraphNodeExpansionCallback {

	void createMappingArc(String sourceId, String targetId);

	ResourceManager getResourceManager();

	ViewContentDisplayCallback getViewContentDisplayCallback();
    }

    private static final String MEMENTO_X = "x";

    // advanced node class: (incoming, outgoing, expanded: state machine)

    private static final String MEMENTO_Y = "y";

    private final CommandManager commandManager;

    private final NeighbourhoodServiceAsync conceptNeighbourhoodService;

    private final Display display;

    public DragEnablerFactory dragEnablerFactory;

    private ErrorHandler errorHandler;

    private GraphNodeExpansionCallback expansionCallback = new GraphNodeExpansionCallback() {

	@Override
	public void createMappingArc(String sourceId, String targetId) {
	    GraphViewContentDisplay.this.createMappingArc(sourceId, targetId);
	}

	@Override
	public ResourceManager getResourceManager() {
	    return GraphViewContentDisplay.this.resourceManager;
	}

	@Override
	public ViewContentDisplayCallback getViewContentDisplayCallback() {
	    return GraphViewContentDisplay.this.getCallback();
	}
    };

    private final NeighbourhoodServiceAsync mappingNeighbourhoodService;

    private final Map<String, GraphItem> nodeIdToGraphItemMap = new HashMap<String, GraphItem>();

    private boolean ready = false;

    private ResourceCategorizer resourceCategorizer;

    private ResourceManager resourceManager;

    @Inject
    public GraphViewContentDisplay(
	    Display display,
	    @Named(ChooselInjectionConstants.HOVER_MODEL) ResourceSet hoverModel,
	    @Named("mapping") NeighbourhoodServiceAsync mappingService,
	    @Named("concept") NeighbourhoodServiceAsync conceptNeighbourhoodService,
	    PopupManagerFactory popupManagerFactory,
	    DetailsWidgetHelper detailsWidgetHelper,
	    CommandManager commandManager, ResourceManager resourceManager,
	    ErrorHandler errorHandler, DragEnablerFactory dragEnablerFactory,
	    ResourceCategorizer resourceCategorizer) {

	super(popupManagerFactory, detailsWidgetHelper, hoverModel);

	assert display != null;
	assert mappingService != null;
	assert conceptNeighbourhoodService != null;
	assert commandManager != null;
	assert resourceManager != null;
	assert errorHandler != null;
	assert dragEnablerFactory != null;
	assert resourceCategorizer != null;

	this.resourceCategorizer = resourceCategorizer;
	this.display = display;
	this.mappingNeighbourhoodService = mappingService;
	this.conceptNeighbourhoodService = conceptNeighbourhoodService;
	this.commandManager = commandManager;
	this.resourceManager = resourceManager;
	this.errorHandler = errorHandler;
	this.dragEnablerFactory = dragEnablerFactory;
    }

    private void addArcsToRelatedConcepts(final Resource concept) {
	// search neighbourhood uri list for neighbours
	UriList neighbours = concept
		.getUriListValue(NCBO.CONCEPT_NEIGHBOURHOOD_DESTINATION_CONCEPTS);
	for (String uri : neighbours) {
	    if (getCallback().containsResourceWithUri(uri)) {
		createConceptArc(concept.getUri(), uri);
	    }
	}

	// get all concepts and see if the new concept is contained in the uri
	// list
	for (Resource resource : getCallback().getAllResources()) {
	    if (NcboUriHelper.NCBO_CONCEPT.equals(getCategory(resource))) {
		UriList neighbours2 = resource
			.getUriListValue(NCBO.CONCEPT_NEIGHBOURHOOD_DESTINATION_CONCEPTS);
		if (neighbours2.contains(concept.getUri())) {
		    createConceptArc(resource.getUri(), concept.getUri());
		}
	    }
	}
    }

    private void addMappingArcsToConcept(Resource concept) {
	for (Resource resource2 : getCallback().getAllResources()) {
	    if (NcboUriHelper.NCBO_MAPPING.equals(getCategory(resource2))) {
		String sourceURI = (String) resource2
			.getValue(NCBO.MAPPING_SOURCE);

		if (concept.getUri().equals(sourceURI)) {
		    createMappingArc(sourceURI, resource2.getUri());
		}

		String destinationURI = (String) resource2
			.getValue(NCBO.MAPPING_DESTINATION);

		if (concept.getUri().equals(destinationURI)) {
		    createMappingArc(resource2.getUri(), destinationURI);
		}
	    }
	}
    }

    private void addMappingToConceptArcs(Resource mapping) {
	String sourceURI = (String) mapping.getValue(NCBO.MAPPING_SOURCE);

	if (getCallback().containsResourceWithUri(sourceURI)) {
	    createMappingArc(sourceURI, mapping.getUri());
	}

	String destinationURI = (String) mapping
		.getValue(NCBO.MAPPING_DESTINATION);

	if (getCallback().containsResourceWithUri(destinationURI)) {
	    createMappingArc(mapping.getUri(), destinationURI);
	}
    }

    // FIXME remove duplication (callback)
    protected String calculateArcId(String sourceUri, String targetUri) {
	return NCBO.CONCEPT_NEIGHBOURHOOD_DESTINATION_CONCEPTS + ":"
		+ sourceUri + "_" + targetUri;
    }

    @Override
    public void checkResize() {
    }

    // FIXME remove duplication (callback)
    private void createConceptArc(String sourceUri, String targetUri) {
	Arc arc = new Arc(calculateArcId(sourceUri, targetUri), sourceUri,
		targetUri, NCBO.CONCEPT_NEIGHBOURHOOD_DESTINATION_CONCEPTS);
	display.addArc(arc);
	display.setArcStyle(arc, GraphDisplay.ARC_COLOR, "#AFC6E5");
    }

    private GraphItem createGraphItem(Layer layer, Resource resource) {
	String label = layer.getValue(SlotResolver.GRAPH_LABEL_SLOT, resource);
	String category = getCategory(resource);
	PopupManager popupManager = createPopupManager(resource, layer
		.getResolver(SlotResolver.DESCRIPTION_SLOT));

	GraphItem item = new GraphItem(resource, hoverModel, popupManager,
		label, category, display, layer);

	nodeIdToGraphItemMap.put(item.getNode().getId(), item);

	display.addNode(item.getNode());
	positionNode(item.getNode());

	// TODO remove once new drag and drop mechanism works...
	display.setNodeStyle(item.getNode(), "showDragImage", "true");

	setGraphItemColors(resource, item);

	if (category.equals(NcboUriHelper.NCBO_CONCEPT)) {
	    // TODO this should be false if set of available neighbourhoods
	    // equals 0
	    display.setNodeStyle(item.getNode(), "showArrow", "true");
	}
	if (category.equals(NcboUriHelper.NCBO_MAPPING)) {
	    // TODO this should be false if set of available neighbourhoods
	    // equals 0
	    display.setNodeStyle(item.getNode(), "showArrow", "true");
	}

	return item;
    }

    // TODO eliminate duplicate (callback)
    private void createMappingArc(String sourceId, String targetId) {
	Arc arc = new Arc(getArcId(sourceId, targetId), sourceId, targetId,
		"mapping");

	display.addArc(arc);
	display.setArcStyle(arc, GraphDisplay.ARC_COLOR, "#D4D4D4");
	display.setArcStyle(arc, GraphDisplay.ARC_STYLE,
		GraphDisplay.ARC_STYLE_DASHED);
    }

    @Override
    public GraphItem createResourceItem(Layer layer, Resource resource) {
	GraphItem item = createGraphItem(layer, resource);

	if (NcboUriHelper.NCBO_CONCEPT.equals(getCategory(resource))) {
	    if (!isRestoring()) {
		// only look automatically for mappings if not restoring
		expandMappingNeighbourhood(resource);
	    }

	    addArcsToRelatedConcepts(resource);
	    addMappingArcsToConcept(resource);
	}

	if (NcboUriHelper.NCBO_MAPPING.equals(getCategory(resource))) {
	    addMappingToConceptArcs(resource);
	}

	return item;
    }

    // TODO encapsulate in display, use dependency injection
    @Override
    protected Widget createWidget() {
	return display.asWidget();
    }

    protected void expandConceptNeighbourhood(Resource resource) {
	// TODO introduce factories for callbacks that return
	// service + callback + name
	conceptNeighbourhoodService.getNeighbourhood(resource,
		new ConceptNeighbourhoodCallback(display, getCallback(),
			resourceManager, errorHandler));
    }

    protected void expandMappingNeighbourhood(Resource resource) {
	mappingNeighbourhoodService.getNeighbourhood(resource,
		new MappingNeighbourhoodCallback(display, getCallback(),
			errorHandler));
    }

    protected void expandMappingNeighbourhood2(Resource resource) {
	mappingNeighbourhoodService.getNeighbourhood(resource,
		new MappingNeighbourhoodCallback2(display, getCallback(),
			errorHandler));
    }

    // TODO eliminate duplicate (callback)
    protected String getArcId(String sourceId, String targetId) {
	return sourceId + "_" + targetId;
    }

    private String getCategory(Resource resource) {
	return resourceCategorizer.getCategory(resource);
    }

    private GraphItem getGraphItem(Node node) {
	return nodeIdToGraphItemMap.get(node.getId());
    }

    private GraphItem getGraphItem(NodeEvent<?> event) {
	return getGraphItem(event.getNode());
    }

    private Resource getResource(Node node) {
	return getGraphItem(node).getResource();
    }

    @Override
    public String[] getSlotIDs() {
	return new String[] { SlotResolver.DESCRIPTION_SLOT,
		SlotResolver.GRAPH_LABEL_SLOT };
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

		display.addNodeMenuItemHandler("Concepts",
			new NodeMenuItemClickedHandler() {
			    @Override
			    public void onNodeMenuItemClicked(Node node) {
				expandConceptNeighbourhood(getResource(node));
			    }
			}, NcboUriHelper.NCBO_CONCEPT);
		display.addNodeMenuItemHandler("Mappings",
			new NodeMenuItemClickedHandler() {
			    @Override
			    public void onNodeMenuItemClicked(Node node) {
				expandMappingNeighbourhood2(getResource(node));
			    }
			}, NcboUriHelper.NCBO_CONCEPT);

		registerNodeMenuItem(new MappingExpander(), "Concepts",
			NcboUriHelper.NCBO_MAPPING);
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
	Iterator<Resource> it = getCallback().getAllResources().iterator();

	if (!it.hasNext()) {
	    return; // for tests
	}

	it.next(); // there are already some nodes (this one is already added)
	if (it.hasNext()) {
	    return; // there are already some nodes (this one is already added)
	}

	Widget displayWidget = display.asWidget();

	if (displayWidget == null) {
	    return; // for tests
	}

	int height = displayWidget.getOffsetHeight();
	int width = displayWidget.getOffsetWidth();

	display.setLocation(node, new Point(width / 2, height / 2));
    }

    private void registerNodeMenuItem(final GraphNodeExpander nodeExpander,
	    String menuLabel, String category) {

	display.addNodeMenuItemHandler(menuLabel,
		new NodeMenuItemClickedHandler() {
		    @Override
		    public void onNodeMenuItemClicked(Node node) {
			nodeExpander.expand(getResource(node),
				expansionCallback);
		    }
		}, category);
    }

    @Override
    public void removeResourceItem(ResourceItem resourceItem) {
	nodeIdToGraphItemMap.remove(((GraphItem) resourceItem).getNode()
		.getId());
	display.removeNode(((GraphItem) resourceItem).getNode());
    }

    @Override
    public void restore(Memento state) {
	Iterable<Resource> allResources = getCallback().getAllResources();
	for (Resource resource : allResources) {
	    GraphItem item = (GraphItem) getCallback()
		    .getResourceItem(resource);
	    Memento nodeMemento = state.getChild(resource.getUri());
	    Point location = new Point((Integer) nodeMemento
		    .getValue(MEMENTO_X), (Integer) nodeMemento
		    .getValue(MEMENTO_Y));

	    display.setLocation(item.getNode(), location);
	}
    }

    @Override
    public Memento save() {
	Memento state = new Memento();
	Iterable<Resource> allResources = getCallback().getAllResources();
	for (Resource resource : allResources) {
	    GraphItem item = (GraphItem) getCallback()
		    .getResourceItem(resource);
	    Point location = display.getLocation(item.getNode());

	    Memento nodeMemento = new Memento();
	    nodeMemento.setValue(MEMENTO_X, location.x);
	    nodeMemento.setValue(MEMENTO_Y, location.y);

	    state.addChild(resource.getUri(), nodeMemento);
	}
	return state;
    }

    private void setGraphItemColors(Resource resource, GraphItem item) {
	// TODO use dependency injection
	String category = getCategory(resource);

	if (category.equals(NcboUriHelper.NCBO_CONCEPT)) {
	    String backgroundColor = "#DAE5F3";
	    String borderColor = "#AFC6E5";
	    item.setDefaultColors(backgroundColor, borderColor);
	}

	if (category.equals(NcboUriHelper.NCBO_MAPPING)) {
	    String backgroundColor = "#E4E4E4";
	    String borderColor = "#D4D4D4";
	    item.setDefaultColors(backgroundColor, borderColor);
	}
    }
}
