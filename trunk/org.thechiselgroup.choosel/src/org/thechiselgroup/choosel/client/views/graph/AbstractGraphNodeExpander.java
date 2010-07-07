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

import java.util.ArrayList;
import java.util.List;

import org.thechiselgroup.choosel.client.geometry.Point;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.ui.widget.graph.Node;

public abstract class AbstractGraphNodeExpander implements GraphNodeExpander {

    protected void addResources(GraphNodeExpansionCallback expansionCallback,
            List<String> resourceUrisToAdd, Resource resource) {

        List<String> added = new ArrayList<String>();
        for (String uri : resourceUrisToAdd) {
            if (!expansionCallback.getCallback().containsResourceWithUri(uri)) {
                Resource r2 = expansionCallback.getResourceManager().getByUri(
                        uri);
                expansionCallback.getCallback().getAutomaticResourceSet().add(
                        r2);
                added.add(uri);
            }
        }

        // TODO extract + refactor layout (have method layout on node)
        Node inputNode = expansionCallback.getDisplay().getNode(
                resource.getUri());
        Point inputLocation = expansionCallback.getDisplay().getLocation(
                inputNode);

        List<Node> nodesToLayout = new ArrayList<Node>();
        for (String uri : added) {
            Node node = expansionCallback.getDisplay().getNode(uri);
            expansionCallback.getDisplay().setLocation(node, inputLocation);
            nodesToLayout.add(node);
        }

        expansionCallback.getDisplay().layOutNodes(nodesToLayout);
    }

    // TODO do not ask for a uri list
    protected List<String> calculateUrisToAdd(Resource resource,
            String... properties) {

        List<String> resourceUrisToAdd = new ArrayList<String>();

        for (String property : properties) {

            if (resource.isUriList(property)) {
                for (String uri : resource.getUriListValue(property)) {
                    resourceUrisToAdd.add(uri);
                }
            }

            else {
                resourceUrisToAdd.add((String) resource.getValue(property));
            }
        }

        return resourceUrisToAdd;
    }

    protected void showArcs(Resource resource,
            GraphNodeExpansionCallback expansionCallback, String property,
            String arcType, boolean inverted) {

        for (String uri : resource.getUriListValue(property)) {
            if (inverted) {
                expansionCallback.showArc(arcType, uri, resource.getUri());
            } else {
                expansionCallback.showArc(arcType, resource.getUri(), uri);
            }
        }
    }

}
