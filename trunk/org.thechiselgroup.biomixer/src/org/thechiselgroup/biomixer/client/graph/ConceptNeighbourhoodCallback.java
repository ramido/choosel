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
package org.thechiselgroup.biomixer.client.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.thechiselgroup.biomixer.client.NCBO;
import org.thechiselgroup.choosel.client.error_handling.ErrorHandler;
import org.thechiselgroup.choosel.client.geometry.Point;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceManager;
import org.thechiselgroup.choosel.client.ui.widget.graph.GraphDisplay;
import org.thechiselgroup.choosel.client.ui.widget.graph.Node;
import org.thechiselgroup.choosel.client.views.graph.AbstractNeighbourhoodCallback;
import org.thechiselgroup.choosel.client.views.graph.GraphNodeExpansionCallback;
import org.thechiselgroup.choosel.client.views.graph.NeighbourhoodServiceResult;
import org.thechiselgroup.choosel.client.views.graph.Relationship;

// TODO extract superclass
public class ConceptNeighbourhoodCallback extends AbstractNeighbourhoodCallback {

	private final ResourceManager resourceManager;

	public ConceptNeighbourhoodCallback(GraphDisplay graph,
			ResourceManager resourceManager, ErrorHandler errorHandler,
			GraphNodeExpansionCallback expansionCallback) {

		super(graph, errorHandler, expansionCallback);

		this.resourceManager = resourceManager;
	}

	private void createArcs(List<Relationship> relationships) {
		for (Relationship relationship : relationships) {
			String targetUri = relationship.getTarget().getUri();
			String sourceUri = relationship.getSource().getUri();

			expansionCallback.showArc(
					NCBO.CONCEPT_NEIGHBOURHOOD_DESTINATION_CONCEPTS, sourceUri,
					targetUri);
		}
	}

	protected void layoutNodes(Set<Resource> newResources,
			Resource inputResource) {

		Node inputNode = getNode(inputResource);
		Point inputLocation = graph.getLocation(inputNode);

		List<Node> nodesToLayout = new ArrayList<Node>();
		for (Resource resource : newResources) {
			Node node = getNode(resource);
			graph.setLocation(node, inputLocation);
			nodesToLayout.add(node);
		}

		graph.runLayoutOnNodes(nodesToLayout);
	}

	@Override
	public void onSuccess(NeighbourhoodServiceResult result) {
		// FIXME add to global storage // convert result to use objects
		// from global storage

		Resource inputResource = resourceManager.add(result.getResource());
		List<Resource> neighbours = new ArrayList<Resource>();
		for (Resource resource : result.getNeighbours()) {
			neighbours.add(resourceManager.add(resource));
		}
		List<Relationship> relationships = new ArrayList<Relationship>();
		for (Relationship relationship : result.getRelationships()) {
			relationships.add(new Relationship(resourceManager.add(relationship
					.getSource()),
					resourceManager.add(relationship.getTarget())));
		}

		Set<Resource> newResources = getNewResources(neighbours);

		// TODO filter to arcs/node where both concepts are contained

		addResources(newResources);
		updateUriLists(NCBO.CONCEPT_NEIGHBOURHOOD_DESTINATION_CONCEPTS,
				relationships, inputResource);

		createArcs(relationships);

		layoutNodes(newResources, inputResource);
	}

}