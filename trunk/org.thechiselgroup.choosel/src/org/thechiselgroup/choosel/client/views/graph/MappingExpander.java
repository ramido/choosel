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

import org.thechiselgroup.choosel.client.domain.ncbo.NCBO;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceManager;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.views.ViewContentDisplayCallback;
import org.thechiselgroup.choosel.client.views.graph.GraphViewContentDisplay.GraphNodeExpander;
import org.thechiselgroup.choosel.client.views.graph.GraphViewContentDisplay.GraphNodeExpansionCallback;

public class MappingExpander implements GraphNodeExpander {

    @Override
    public void expand(Resource mapping,
	    GraphNodeExpansionCallback expansionCallback) {

	ViewContentDisplayCallback displayCallback = expansionCallback
		.getViewContentDisplayCallback();
	ResourceManager resourceManager2 = expansionCallback
		.getResourceManager();

	ResourceSet automaticSet = displayCallback.getAutomaticResourceSet();

	String sourceUri = (String) mapping.getValue(NCBO.MAPPING_SOURCE);
	if (!displayCallback.containsResourceWithUri(sourceUri)) {
	    if (!resourceManager2.contains(sourceUri)) {
		Resource concept = new Resource(sourceUri);

		concept.putValue(NCBO.CONCEPT_SHORT_ID, (String) mapping
			.getValue(NCBO.MAPPING_SOURCE_CONCEPT_ID));
		concept.putValue(NCBO.CONCEPT_NAME, (String) mapping
			.getValue(NCBO.MAPPING_SOURCE_CONCEPT_NAME));
		concept.putValue(NCBO.CONCEPT_ONTOLOGY_ID, (String) mapping
			.getValue(NCBO.MAPPING_SOURCE_ONTOLOGY_ID));
		concept.putValue(NCBO.CONCEPT_ONTOLOGY_NAME, (String) mapping
			.getValue(NCBO.MAPPING_SOURCE_ONTOLOGY_NAME));

		resourceManager2.add(concept);
	    }

	    Resource concept = resourceManager2.getByUri(sourceUri);

	    automaticSet.add(concept);

	    expansionCallback.createMappingArc(sourceUri, mapping.getUri());
	}

	String destinationUri = (String) mapping
		.getValue(NCBO.MAPPING_DESTINATION);

	if (!displayCallback.containsResourceWithUri(destinationUri)) {
	    if (!resourceManager2.contains(destinationUri)) {
		Resource concept = new Resource(destinationUri);

		concept.putValue(NCBO.CONCEPT_SHORT_ID, (String) mapping
			.getValue(NCBO.MAPPING_DESTINATION_CONCEPT_ID));
		concept.putValue(NCBO.CONCEPT_NAME, (String) mapping
			.getValue(NCBO.MAPPING_DESTINATION_CONCEPT_NAME));
		concept.putValue(NCBO.CONCEPT_ONTOLOGY_ID, (String) mapping
			.getValue(NCBO.MAPPING_DESTINATION_ONTOLOGY_ID));
		concept.putValue(NCBO.CONCEPT_ONTOLOGY_NAME, (String) mapping
			.getValue(NCBO.MAPPING_DESTINATION_ONTOLOGY_NAME));

		resourceManager2.add(concept);
	    }

	    Resource concept = resourceManager2.getByUri(destinationUri);

	    automaticSet.add(concept);

	    expansionCallback
		    .createMappingArc(mapping.getUri(), destinationUri);
	}
    }
}