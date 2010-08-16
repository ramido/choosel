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
import org.thechiselgroup.biomixer.client.NcboUriHelper;
import org.thechiselgroup.choosel.client.error_handling.ErrorHandler;
import org.thechiselgroup.choosel.client.geometry.Point;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.ui.widget.graph.GraphDisplay;
import org.thechiselgroup.choosel.client.ui.widget.graph.Node;
import org.thechiselgroup.choosel.client.views.graph.AbstractNeighbourhoodCallback;
import org.thechiselgroup.choosel.client.views.graph.GraphNodeExpansionCallback;
import org.thechiselgroup.choosel.client.views.graph.GraphViewContentDisplay;
import org.thechiselgroup.choosel.client.views.graph.NeighbourhoodServiceResult;
import org.thechiselgroup.choosel.client.views.graph.Relationship;

// TODO check for existing references, existing mappings
// TODO Also automatically load neighbourhood to display links
// TODO 2 modes of callbacks: show // not show
public class MappingNeighbourhoodCallback extends AbstractNeighbourhoodCallback {

	public MappingNeighbourhoodCallback(GraphDisplay graph,
			ErrorHandler errorHandler,
			GraphNodeExpansionCallback expansionCallback) {

		super(graph, errorHandler, expansionCallback);
	}

	private void addRelationshipArcs(List<Relationship> displayableRelationships) {
		for (Relationship relationship : displayableRelationships) {
			String sourceId = relationship.getSource().getUri();
			String targetId = relationship.getTarget().getUri();

			expansionCallback.showArc(GraphViewContentDisplay.ARC_TYPE_MAPPING,
					sourceId, targetId);
		}
	}

	// filter mappings: check if source & target node are contained
	private List<Resource> calculateDisplayableMappings(
			NeighbourhoodServiceResult result) {

		Set<Resource> neighbours = result.getNeighbours();
		List<Resource> displayableMappings = new ArrayList<Resource>();
		for (Resource resource : neighbours) {
			if (resource.getUri().startsWith(NcboUriHelper.NCBO_MAPPING)) {
				// we have a mapping --> check if source and target are
				// contained
				String sourceUri = (String) resource
						.getValue(NCBO.MAPPING_SOURCE);
				String destinationUri = (String) resource
						.getValue(NCBO.MAPPING_DESTINATION);

				if (containsUri(sourceUri) && containsUri(destinationUri)
						&& !contains(resource)) {
					displayableMappings.add(resource);
				}
			}
		}
		return displayableMappings;
	}

	private List<Relationship> calculateDisplayableRelationships(
			List<Relationship> relationships) {

		List<Relationship> result = new ArrayList<Relationship>();

		for (Relationship mapping : relationships) {
			String destinationUri = mapping.getTarget().getUri();
			String sourceUri = mapping.getSource().getUri();

			if (containsUri(sourceUri) && containsUri(destinationUri)) {
				result.add(mapping);
			}
		}

		return result;
	}

	private Node getRelatedNode(Resource resource, String key) {
		return getNode(expansionCallback.getResourceByUri((String) resource
				.getValue(key)));
	}

	protected void layoutNodes(List<Resource> displayableMappings) {
		// for displayable mappings: find connected node position, calculate,
		// intermediate distance, position, layout

		List<Node> nodesToLayout = new ArrayList<Node>();
		for (Resource resource : displayableMappings) {
			Node sourceNode = getRelatedNode(resource, NCBO.MAPPING_SOURCE);
			Point sourceLocation = graph.getLocation(sourceNode);

			Node targetNode = getRelatedNode(resource, NCBO.MAPPING_DESTINATION);
			Point targetLocation = graph.getLocation(targetNode);

			Point intermediateLocation = new Point(
					(sourceLocation.x + targetLocation.x) / 2,
					(sourceLocation.y + targetLocation.y) / 2);

			Node node = getNode(resource);
			graph.setLocation(node, intermediateLocation);
			nodesToLayout.add(node);
		}

		graph.runLayoutOnNodes(nodesToLayout);
	}

	@Override
	public void onSuccess(NeighbourhoodServiceResult result) {
		List<Resource> displayableMappings = calculateDisplayableMappings(result);
		addResources(displayableMappings);
		addRelationshipArcs(calculateDisplayableRelationships(result
				.getRelationships()));
		layoutNodes(displayableMappings);
	}
}