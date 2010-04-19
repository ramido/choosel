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

public abstract class AbstractResourceSet extends AbstractResourceContainer
	implements ResourceSet {

    @Override
    public void clear() {
	for (Resource resource : toList()) {
	    remove(resource);
	}
    }

    @Override
    public final boolean containsAll(Iterable<Resource> resources) {
	for (Resource resource : resources) {
	    if (!contains(resource)) {
		return false;
	    }
	}

	return true;
    }

    @Override
    public final boolean containsEqualResources(ResourceSet other) {
	if (size() != other.size()) {
	    return false;
	}

	return containsAll(other);
    }

    @Override
    public boolean containsResourceWithUri(String uri) {
	return getByUri(uri) != null;
    }

    @Override
    public final void switchContainment(Resource resource) {
	if (contains(resource)) {
	    remove(resource);
	} else {
	    add(resource);
	}
    }

}
