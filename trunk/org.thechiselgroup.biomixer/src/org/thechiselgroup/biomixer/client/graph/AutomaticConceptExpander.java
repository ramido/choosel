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

import org.thechiselgroup.biomixer.client.NCBO;
import org.thechiselgroup.biomixer.client.NcboUriHelper;
import org.thechiselgroup.choosel.client.error_handling.ErrorHandler;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.UriList;
import org.thechiselgroup.choosel.client.views.graph.GraphNodeExpander;
import org.thechiselgroup.choosel.client.views.graph.GraphNodeExpansionCallback;
import org.thechiselgroup.choosel.client.views.graph.GraphViewContentDisplay;
import org.thechiselgroup.choosel.client.views.graph.NeighbourhoodServiceAsync;

public class AutomaticConceptExpander implements GraphNodeExpander {

    private NeighbourhoodServiceAsync mappingNeighbourhoodService;

    private ErrorHandler errorHandler;

    public AutomaticConceptExpander(
	    NeighbourhoodServiceAsync mappingNeighbourhoodService,
	    ErrorHandler errorHandler) {

	this.mappingNeighbourhoodService = mappingNeighbourhoodService;
	this.errorHandler = errorHandler;
    }

    @Override
    public void expand(Resource resource,
	    GraphNodeExpansionCallback expansionCallback) {

	if (!expansionCallback.isRestoring()) {
	    // only look automatically for mappings if not restoring
	    expandMappingNeighbourhood(resource, expansionCallback);
	}

	addArcsToRelatedConcepts(resource, expansionCallback);
	addMappingArcsToConcept(resource, expansionCallback);
    }

    protected void expandMappingNeighbourhood(Resource resource,
	    GraphNodeExpansionCallback expansionCallback) {

	mappingNeighbourhoodService
		.getNeighbourhood(resource,
			new MappingNeighbourhoodCallback(expansionCallback
				.getDisplay(), expansionCallback.getCallback(),
				errorHandler, expansionCallback));
    }

    private void addArcsToRelatedConcepts(Resource concept,
	    GraphNodeExpansionCallback expansionCallback) {

	// search neighbourhood uri list for neighbours
	UriList neighbours = concept
		.getUriListValue(NCBO.CONCEPT_NEIGHBOURHOOD_DESTINATION_CONCEPTS);
	for (String uri : neighbours) {
	    if (expansionCallback.getCallback().containsResourceWithUri(uri)) {
		expansionCallback.showArc(
			NCBO.CONCEPT_NEIGHBOURHOOD_DESTINATION_CONCEPTS,
			concept.getUri(), uri);
	    }
	}

	// get all concepts and see if the new concept is contained
	// in the uri
	// list
	for (Resource resource : expansionCallback.getCallback()
		.getAllResources()) {
	    if (NcboUriHelper.NCBO_CONCEPT.equals(expansionCallback
		    .getCategory(resource))) {

		UriList neighbours2 = resource
			.getUriListValue(NCBO.CONCEPT_NEIGHBOURHOOD_DESTINATION_CONCEPTS);
		if (neighbours2.contains(concept.getUri())) {
		    expansionCallback.showArc(
			    NCBO.CONCEPT_NEIGHBOURHOOD_DESTINATION_CONCEPTS,
			    resource.getUri(), concept.getUri());
		}
	    }
	}
    }

    private void addMappingArcsToConcept(Resource concept,
	    GraphNodeExpansionCallback expansionCallback) {

	for (Resource resource2 : expansionCallback.getCallback()
		.getAllResources()) {

	    if (NcboUriHelper.NCBO_MAPPING.equals(expansionCallback
		    .getCategory(resource2))) {
		String sourceURI = (String) resource2
			.getValue(NCBO.MAPPING_SOURCE);

		if (concept.getUri().equals(sourceURI)) {
		    expansionCallback.showArc(
			    GraphViewContentDisplay.ARC_TYPE_MAPPING,
			    sourceURI, resource2.getUri());
		}

		String destinationURI = (String) resource2
			.getValue(NCBO.MAPPING_DESTINATION);

		if (concept.getUri().equals(destinationURI)) {
		    expansionCallback.showArc(
			    GraphViewContentDisplay.ARC_TYPE_MAPPING, resource2
				    .getUri(), destinationURI);
		}
	    }
	}
    }
}