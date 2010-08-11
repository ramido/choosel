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

/**
 * Allows adding the same resource multiple times and stores the count. The
 * resource has to be removed the same number of times before being removed from
 * the set. Size and the returned element are not affected by double adding,
 * every resource is counted once for size and returned once in the iterator.
 */
public class CountingResourceSet extends AbstractResourceSet {

    private static class ResourceElement {

        private int counter;

        private Resource resource;

        public ResourceElement(Resource resource) {
            this.resource = resource;
            this.counter = 1;
        }

    }

    private Map<String, ResourceElement> uriToResourceElementMap = new HashMap<String, ResourceElement>();

    // TODO refactor / extract to superclass
    @Override
    public boolean addAll(Collection<? extends Resource> resources) {
        assert resources != null;

        List<Resource> addedResources = new ArrayList<Resource>();
        for (Resource resource : resources) {
            String uri = resource.getUri();

            if (uriToResourceElementMap.containsKey(uri)) {
                uriToResourceElementMap.get(uri).counter++;
            } else {
                uriToResourceElementMap.put(uri, new ResourceElement(resource));
                addedResources.add(resource);
            }
        }

        if (!addedResources.isEmpty()) {
            eventBus.fireEvent(new ResourcesAddedEvent(this, addedResources));
        }

        return !addedResources.isEmpty();
    }

    @Override
    public boolean contains(Resource resource) {
        return uriToResourceElementMap.containsKey(resource.getUri());
    }

    @Override
    public Resource getByUri(String uri) {
        assert uri != null;
        assert uriToResourceElementMap.containsKey(uri);

        return uriToResourceElementMap.get(uri).resource;
    }

    @Override
    public boolean isEmpty() {
        return uriToResourceElementMap.isEmpty();
    }

    @Override
    public Iterator<Resource> iterator() {
        return toList().iterator();
    }

    // TODO refactor / extract to superclass
    @Override
    public boolean removeAll(Collection<?> resources) {
        assert resources != null;

        List<Resource> removedResources = new ArrayList<Resource>();
        for (Object o : resources) {
            if (contains(o)) {
                assert o instanceof Resource;
                Resource resource = (Resource) o;
                String uri = resource.getUri();
                assert uriToResourceElementMap.containsKey(uri);

                uriToResourceElementMap.get(uri).counter--;

                if (uriToResourceElementMap.get(uri).counter == 0) {
                    uriToResourceElementMap.remove(uri);
                    removedResources.add(resource);
                }
            }
        }

        if (!removedResources.isEmpty()) {
            eventBus.fireEvent(new ResourcesRemovedEvent(this, removedResources));
        }

        return !removedResources.isEmpty();
    }

    // TODO refactor / extract to superclass
    @Override
    public boolean retainAll(Collection<?> resources) {
        assert resources != null;

        List<Resource> removedResources = new ArrayList<Resource>();
        for (Resource resource : toList()) {
            if (!resources.contains(resource)) {
                String uri = resource.getUri();
                assert uriToResourceElementMap.containsKey(uri);

                uriToResourceElementMap.get(uri).counter--;

                if (uriToResourceElementMap.get(uri).counter == 0) {
                    uriToResourceElementMap.remove(uri);
                    removedResources.add(resource);
                }
            }
        }

        if (!removedResources.isEmpty()) {
            eventBus.fireEvent(new ResourcesRemovedEvent(this, removedResources));
        }

        return !removedResources.isEmpty();
    }

    @Override
    public int size() {
        return uriToResourceElementMap.size();
    }

    @Override
    public List<Resource> toList() {
        List<Resource> resources = new ArrayList<Resource>();

        for (ResourceElement element : uriToResourceElementMap.values()) {
            resources.add(element.resource);
        }

        return resources;
    }
}
