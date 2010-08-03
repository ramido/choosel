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

import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.UriList;

public abstract class AbstractAutomaticGraphNodeExpander implements
        GraphNodeExpander {

    protected void showArcs(Resource resource,
            GraphNodeExpansionCallback expansionCallback, String property,
            String arcType, boolean inverted) {

        // going through neighbors
        UriList resourceNeighbours = resource.getUriListValue(property);
        for (String neighbourUri : resourceNeighbours) {
            if (expansionCallback.containsResourceWithUri(neighbourUri)) {
                if (inverted) {
                    expansionCallback.showArc(arcType, neighbourUri,
                            resource.getUri());
                } else {
                    expansionCallback.showArc(arcType, resource.getUri(),
                            neighbourUri);
                }
            }
        }

        // going through all existing resource and check if current resource is
        // neighbor
        for (Resource graphResource : expansionCallback.getAllResources()) {
            UriList graphResourceNeighbours = graphResource
                    .getUriListValue(property);
            if (graphResourceNeighbours.contains(resource.getUri())) {
                if (inverted) {
                    expansionCallback.showArc(arcType, resource.getUri(),
                            graphResource.getUri());
                } else {
                    expansionCallback.showArc(arcType, graphResource.getUri(),
                            resource.getUri());
                }
            }
        }
    }
}
