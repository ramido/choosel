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
import java.util.List;
import java.util.Set;

import org.thechiselgroup.choosel.client.error_handling.ErrorHandler;
import org.thechiselgroup.choosel.client.geometry.Point;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.ui.widget.graph.GraphDisplay;
import org.thechiselgroup.choosel.client.ui.widget.graph.Node;
import org.thechiselgroup.choosel.client.views.ViewContentDisplayCallback;

import com.allen_sauer.gwt.log.client.Log;

public class MappingNeighbourhoodCallback2 extends
	AbstractNeighbourhoodCallback {

    public MappingNeighbourhoodCallback2(GraphDisplay graph,
	    ViewContentDisplayCallback contentDisplayCallback,
	    ErrorHandler errorHandler,
	    GraphNodeExpansionCallback expansionCallback) {

	super(graph, contentDisplayCallback, errorHandler, expansionCallback);
    }

    private void addRelationshipArcs(List<Relationship> displayableRelationships) {
	for (Relationship relationship : displayableRelationships) {
	    String sourceId = relationship.getSource().getUri();
	    String targetId = relationship.getTarget().getUri();

	    expansionCallback.createArc(
		    GraphViewContentDisplay.ARC_TYPE_MAPPING, sourceId,
		    targetId);
	}
    }

    private List<Relationship> calculateDisplayableRelationships(
	    List<Relationship> relationships) {

	List<Relationship> result = new ArrayList<Relationship>();

	for (Relationship mapping : relationships) {
	    String destinationUri = mapping.getTarget().getUri();
	    String sourceUri = mapping.getSource().getUri();

	    if (contentDisplayCallback.containsResourceWithUri(sourceUri)
		    && contentDisplayCallback
			    .containsResourceWithUri(destinationUri)
		    && !graph.containsArc(expansionCallback.getArcId(
			    GraphViewContentDisplay.ARC_TYPE_MAPPING,
			    sourceUri, destinationUri))) {

		result.add(mapping);
	    }
	}

	return result;
    }

    @Override
    public void onSuccess(NeighbourhoodServiceResult result) {
	List<Resource> newResources = calculateNewResources(result);
	addResources(newResources);
	addRelationshipArcs(calculateDisplayableRelationships(result
		.getRelationships()));
	layoutNodes(newResources, result.getResource());
    }

    protected void layoutNodes(Iterable<Resource> newResources,
	    Resource inputResource) {

	Node inputNode = getNode(inputResource);
	Point inputLocation = graph.getLocation(inputNode);

	Log.debug("ConceptNeighbourhoodCallback.onSuccess - input location "
		+ inputLocation.x + ", " + inputLocation.y);

	List<Node> nodesToLayout = new ArrayList<Node>();
	for (Resource resource : newResources) {
	    Node node = getNode(resource);
	    graph.setLocation(node, inputLocation);
	    nodesToLayout.add(node);
	}

	graph.layOutNodes(nodesToLayout);
    }

    private List<Resource> calculateNewResources(
	    NeighbourhoodServiceResult result) {
	List<Resource> newResources = new ArrayList<Resource>();
	Set<Resource> neighbours = result.getNeighbours();
	for (Resource resource : neighbours) {
	    if (!viewContainsResource(resource)) {
		newResources.add(resource);
	    }
	}
	return newResources;
    }
}