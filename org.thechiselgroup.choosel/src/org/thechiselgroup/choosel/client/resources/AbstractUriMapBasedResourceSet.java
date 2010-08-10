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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.thechiselgroup.choosel.client.util.CollectionUtils;

public abstract class AbstractUriMapBasedResourceSet extends
        AbstractImplementingResourceSet {

    protected Map<String, Resource> uriToResource = new HashMap<String, Resource>();

    @Override
    public void addAll(Iterable<Resource> resources) {
        assert resources != null;

        List<Resource> addedResources = new ArrayList<Resource>();

        for (Resource resource : resources) {
            if (!contains(resource)) {
                doAdd(resource);
                addedResources.add(resource);
            }
        }

        // TODO test event is not fire if no changes
        eventBus.fireEvent(new ResourcesAddedEvent(this, addedResources));
    }

    protected Resource addResourceToMap(Resource resource) {
        return uriToResource.put(resource.getUri(), resource);
    }

    @Override
    public boolean contains(Resource resource) {
        assert resource != null;
        return uriToResource.containsKey(resource.getUri());
    }

    protected abstract void doAdd(Resource resource);

    protected abstract void doRemove(Resource resource);

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

    // TODO refer to remove all, pull up
    @Override
    public void remove(Resource resource) {
        if (!contains(resource)) {
            return;
        }
        doRemove(resource);
        eventBus.fireEvent(new ResourcesRemovedEvent(this, CollectionUtils
                .toList(resource)));
    }

    @Override
    public void removeAll(Iterable<Resource> resources) {
        assert resources != null;

        List<Resource> removedResources = new ArrayList<Resource>();

        for (Resource resource : resources) {
            if (contains(resource)) {
                doRemove(resource);
                removedResources.add(resource);
            }
        }

        // TODO test event is not fire if no changes
        eventBus.fireEvent(new ResourcesRemovedEvent(this, removedResources));
    }

    protected void removeResourceFromMap(String key) {
        uriToResource.remove(key);
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