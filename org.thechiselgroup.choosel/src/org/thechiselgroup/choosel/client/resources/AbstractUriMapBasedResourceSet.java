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
package org.thechiselgroup.choosel.client.resources;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

// TODO move label support into separate class
public abstract class AbstractUriMapBasedResourceSet extends
        AbstractResourceSet {

    protected Map<String, Resource> uriToResource = new HashMap<String, Resource>();

    @Override
    public boolean addAll(Collection<? extends Resource> resources) {
        assert resources != null;

        List<Resource> addedResources = new ArrayList<Resource>();

        for (Resource resource : resources) {
            if (!contains(resource)) {
                doAdd(resource, addedResources);
            }
        }

        if (!addedResources.isEmpty()) {
            eventBus.fireEvent(new ResourcesAddedEvent(this, addedResources));
        }

        return !addedResources.isEmpty();
    }

    protected Resource addResourceToMap(Resource resource) {
        return uriToResource.put(resource.getUri(), resource);
    }

    @Override
    public boolean contains(Resource resource) {
        assert resource != null;
        return uriToResource.containsKey(resource.getUri());
    }

    protected abstract void doAdd(Resource resource, List<Resource> resources);

    protected abstract void doRemove(Resource resource,
            List<Resource> removedResources);

    @Override
    public Resource getByUri(String uri) {
        return uriToResource.get(uri);
    }

    @Override
    public boolean isEmpty() {
        return uriToResource.isEmpty();
    }

    @Override
    public Iterator<Resource> iterator() {
        // FIXME should be unmodifiable
        return uriToResource.values().iterator();
    }

    @Override
    public boolean removeAll(Collection<?> resources) {
        assert resources != null;

        List<Resource> removedResources = new ArrayList<Resource>();
        for (Object o : resources) {
            if (contains(o)) {
                assert o instanceof Resource;
                Resource resource = (Resource) o;
                doRemove(resource, removedResources);
            }
        }

        if (!removedResources.isEmpty()) {
            eventBus.fireEvent(new ResourcesRemovedEvent(this, removedResources));
        }

        return !removedResources.isEmpty();
    }

    protected void removeResourceFromMap(String key) {
        uriToResource.remove(key);
    }

    // TODO implement faster retains if both are default resource sets
    @Override
    public boolean retainAll(Collection<?> resources) {
        assert resources != null;

        List<Resource> removedResources = new ArrayList<Resource>();
        for (Resource resource : toList()) {
            if (!resources.contains(resource)) {
                doRemove(resource, removedResources);
            }
        }

        if (!removedResources.isEmpty()) {
            eventBus.fireEvent(new ResourcesRemovedEvent(this, removedResources));
        }

        return !removedResources.isEmpty();
    }

    @Override
    public int size() {
        return uriToResource.size();
    }

    @Override
    public List<Resource> toList() {
        return new ArrayList<Resource>(uriToResource.values());
    }

    @Override
    public String toString() {
        return uriToResource.values().toString();
    }

}