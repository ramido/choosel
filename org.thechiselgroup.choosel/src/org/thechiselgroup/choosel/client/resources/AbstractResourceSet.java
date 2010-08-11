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

import java.util.Collection;

public abstract class AbstractResourceSet implements ResourceSet {

    @Override
    public void clear() {
        // TODO fix: this fires several events
        for (Resource resource : toList()) {
            remove(resource);
        }
    }

    @Override
    public boolean contains(Object o) {
        if (!(o instanceof Resource)) {
            return false;
        }

        return contains((Resource) o);
    }

    @Override
    public boolean containsAll(Collection<?> resources) {
        for (Object o : resources) {
            if (!contains(o)) {
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
    public Resource getFirstResource() {
        assert !isEmpty();
        return toList().get(0);
    }

    @Override
    public final void switchContainment(Resource resource) {
        assert resource != null;

        if (contains(resource)) {
            remove(resource);
            assert !contains(resource);
        } else {
            add(resource);
            assert contains(resource);
        }
    }

    @Override
    public void switchContainment(ResourceSet resources) {
        // TODO fix: this fires several events
        assert resources != null;

        for (Resource resource : resources) {
            switchContainment(resource);
        }
    }

}
