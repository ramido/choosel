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

import org.thechiselgroup.choosel.client.error_handling.ErrorHandler;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.views.ResourceItem;
import org.thechiselgroup.choosel.client.views.graph.GraphNodeExpander;
import org.thechiselgroup.choosel.client.views.graph.GraphNodeExpansionCallback;
import org.thechiselgroup.choosel.client.views.graph.NeighbourhoodServiceAsync;

public class ConceptConceptNeighbourhoodExpander implements GraphNodeExpander {

    private NeighbourhoodServiceAsync neighbourhoodService;

    private ErrorHandler errorHandler;

    public ConceptConceptNeighbourhoodExpander(
            NeighbourhoodServiceAsync neighbourhoodService,
            ErrorHandler errorHandler) {

        this.neighbourhoodService = neighbourhoodService;
        this.errorHandler = errorHandler;
    }

    @Override
    public void expand(ResourceItem resourceItem,
            GraphNodeExpansionCallback expansionCallback) {

        Resource resource = resourceItem.getResourceSet().getFirstResource();

        // TODO reuse methods exposed by expansionCallback within
        // ConceptNeighbourhoodCallback
        neighbourhoodService.getNeighbourhood(
                resource,
                new ConceptNeighbourhoodCallback(
                        expansionCallback.getDisplay(), expansionCallback
                                .getResourceManager(), errorHandler,
                        expansionCallback));
    }
}