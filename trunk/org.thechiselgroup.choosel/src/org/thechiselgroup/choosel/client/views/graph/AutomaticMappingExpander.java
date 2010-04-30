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
import org.thechiselgroup.choosel.client.views.ViewContentDisplayCallback;

public class AutomaticMappingExpander implements GraphNodeExpander {

    @Override
    public void expand(Resource mapping,
	    GraphNodeExpansionCallback expansionCallback) {

	ViewContentDisplayCallback callback = expansionCallback.getCallback();

	String sourceURI = (String) mapping.getValue(NCBO.MAPPING_SOURCE);

	if (callback.containsResourceWithUri(sourceURI)) {
	    expansionCallback.createArc(
		    GraphViewContentDisplay.ARC_TYPE_MAPPING, sourceURI,
		    mapping.getUri());
	}

	String destinationURI = (String) mapping
		.getValue(NCBO.MAPPING_DESTINATION);

	if (callback.containsResourceWithUri(destinationURI)) {
	    expansionCallback.createArc(
		    GraphViewContentDisplay.ARC_TYPE_MAPPING, mapping.getUri(),
		    destinationURI);
	}
    }
}