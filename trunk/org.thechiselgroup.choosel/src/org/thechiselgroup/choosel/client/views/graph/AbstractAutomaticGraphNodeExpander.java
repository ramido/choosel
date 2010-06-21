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
        UriList neighbours = resource.getUriListValue(property);
        for (String uri : neighbours) {
            if (expansionCallback.getCallback().containsResourceWithUri(uri)) {
                if (inverted) {
                    expansionCallback.showArc(arcType, uri, resource.getUri());
                } else {
                    expansionCallback.showArc(arcType, resource.getUri(), uri);
                }
            }
        }

        // going through all existing resource and check if current resource is
        // neighbor
        for (Resource otherResource : expansionCallback.getCallback()
                .getAllResources()) {

            UriList otherNeighbours = otherResource.getUriListValue(property);
            if (otherNeighbours.contains(resource.getUri())) {
                if (inverted) {
                    expansionCallback.showArc(arcType, resource.getUri(),
                            otherResource.getUri());
                } else {
                    expansionCallback.showArc(arcType, otherResource.getUri(),
                            resource.getUri());
                }
            }
        }
    }
}
