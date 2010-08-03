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
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.views.ResourceItem;
import org.thechiselgroup.choosel.client.views.graph.GraphNodeExpander;
import org.thechiselgroup.choosel.client.views.graph.GraphNodeExpansionCallback;
import org.thechiselgroup.choosel.client.views.graph.GraphViewContentDisplay;

public class AutomaticMappingExpander implements GraphNodeExpander {

	@Override
	public void expand(ResourceItem resourceItem,
			GraphNodeExpansionCallback expansionCallback) {

		// TODO better resource item handling
		Resource mapping = resourceItem.getResourceSet().getFirstResource();

		String sourceURI = (String) mapping.getValue(NCBO.MAPPING_SOURCE);

		if (expansionCallback.containsResourceWithUri(sourceURI)) {
			expansionCallback.showArc(GraphViewContentDisplay.ARC_TYPE_MAPPING,
					sourceURI, mapping.getUri());
		}

		String destinationURI = (String) mapping
				.getValue(NCBO.MAPPING_DESTINATION);

		if (expansionCallback.containsResourceWithUri(destinationURI)) {
			expansionCallback.showArc(GraphViewContentDisplay.ARC_TYPE_MAPPING,
					mapping.getUri(), destinationURI);
		}
	}
}