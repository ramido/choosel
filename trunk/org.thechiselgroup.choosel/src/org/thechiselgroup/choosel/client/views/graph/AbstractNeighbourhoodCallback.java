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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.thechiselgroup.choosel.client.error_handling.ErrorHandler;
import org.thechiselgroup.choosel.client.error_handling.ErrorHandlingAsyncCallback;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.ui.widget.graph.GraphDisplay;
import org.thechiselgroup.choosel.client.ui.widget.graph.Node;

public abstract class AbstractNeighbourhoodCallback extends
        ErrorHandlingAsyncCallback<NeighbourhoodServiceResult> {

    protected final GraphNodeExpansionCallback expansionCallback;

    protected final GraphDisplay graph;

    public AbstractNeighbourhoodCallback(GraphDisplay graph,
            ErrorHandler errorHandler,
            GraphNodeExpansionCallback expansionCallback) {

        super(errorHandler);

        assert expansionCallback != null;
        assert graph != null;

        this.expansionCallback = expansionCallback;
        this.graph = graph;
    }

    protected void addResource(Resource resource) {
        expansionCallback.addAutomaticResource(resource);
    }

    protected void addResources(Iterable<Resource> newResources) {
        for (Resource resource : newResources) {
            addResource(resource);
        }
    }

    protected boolean contains(Resource resource) {
        return containsUri(resource.getUri());
    }

    protected boolean containsUri(String resourceUri) {
        return expansionCallback.containsResourceWithUri(resourceUri);
    }

    protected Set<Resource> getNewResources(Iterable<Resource> neighbours) {
        Set<Resource> newResources = new HashSet<Resource>();
        for (Resource resource : neighbours) {
            if (!contains(resource)) {
                newResources.add(resource);
            }
        }
        return newResources;
    }

    protected Node getNode(Resource resource) {
        assert resource != null;
        return graph.getNode(resource.getUri());
    }

    private void updateUriLists(String property,
            List<Relationship> relationships) {

        // FIXME use events on resource instead of hard-coded link?
        for (Relationship relationship : relationships) {
            relationship.getSource().getUriListValue(property)
                    .add(relationship.getTarget().getUri());
        }
    }

    protected void updateUriLists(String property,
            List<Relationship> relationships, Resource inputResource) {
        updateUriLists(property, relationships);
        inputResource.getUriListValue(property).setLoaded(true);
    }

}