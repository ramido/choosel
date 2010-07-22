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

import org.thechiselgroup.biomixer.client.NcboUriHelper;
import org.thechiselgroup.choosel.client.error_handling.ErrorHandler;
import org.thechiselgroup.choosel.client.views.graph.GraphExpansionRegistry;
import org.thechiselgroup.choosel.client.views.graph.NeighbourhoodServiceAsync;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class BioMixerGraphExpansionRegistry extends GraphExpansionRegistry {

	@Inject
	public BioMixerGraphExpansionRegistry(
			@Named("mapping") NeighbourhoodServiceAsync mappingNeighbourhoodService,
			@Named("concept") NeighbourhoodServiceAsync conceptNeighbourhoodService,
			ErrorHandler errorHandler) {

		putAutomaticExpander(NcboUriHelper.NCBO_CONCEPT,
				new AutomaticConceptExpander(mappingNeighbourhoodService,
						errorHandler));
		putAutomaticExpander(NcboUriHelper.NCBO_MAPPING,
				new AutomaticMappingExpander());

		putNodeMenuEntry(NcboUriHelper.NCBO_CONCEPT, "Concepts",
				new ConceptConceptNeighbourhoodExpander(
						conceptNeighbourhoodService, errorHandler));
		putNodeMenuEntry(NcboUriHelper.NCBO_CONCEPT, "Mappings",
				new ConceptMappingNeighbourhoodExpander(
						mappingNeighbourhoodService, errorHandler));
		putNodeMenuEntry(NcboUriHelper.NCBO_MAPPING, "Concepts",
				new MappingExpander());
	}
}