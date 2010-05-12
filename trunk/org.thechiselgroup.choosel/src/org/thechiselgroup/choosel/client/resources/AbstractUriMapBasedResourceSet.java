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

public abstract class AbstractUriMapBasedResourceSet extends
	AbstractImplementingResourceSet {

    protected Map<String, Resource> uriToResource = new HashMap<String, Resource>();

    @Override
    public void add(Resource resource) {
	assert resource != null;

	if (contains(resource)) {
	    return;
	}
	doAdd(resource);
	eventBus.fireEvent(new ResourceAddedEvent(resource, this));
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
    public List<Resource> toList() {
	return new ArrayList<Resource>(uriToResource.values());
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
    public void remove(Resource resource) {
	if (!contains(resource)) {
	    return;
	}
	doRemove(resource);
	eventBus.fireEvent(new ResourceRemovedEvent(resource, this));
    }

    protected void removeResourceFromMap(String key) {
	uriToResource.remove(key);
    }

    @Override
    public int size() {
	return uriToResource.size();
    }

}