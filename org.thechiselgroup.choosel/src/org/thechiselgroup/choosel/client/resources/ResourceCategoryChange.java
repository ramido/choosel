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

import org.thechiselgroup.choosel.client.util.Delta;

/**
 * Represents a delta change to the resource categories in a resource splitter.
 * 
 * @author Lars Grammel
 */
public class ResourceCategoryChange {

    private Delta delta;

    private String category;

    private ResourceSet resourceSet;

    /**
     * @param resourceSet
     *            What is included here depends on the delta: Delta.ADD: new
     *            content (all resources in set); Delta.UPDATE: new content (all
     *            resources in set); Delta.REMOVE: old content
     */
    public ResourceCategoryChange(Delta delta, String category,
            ResourceSet resourceSet) {

        assert delta != null;
        assert category != null;
        assert resourceSet != null;

        this.delta = delta;
        this.category = category;
        this.resourceSet = resourceSet;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        ResourceCategoryChange other = (ResourceCategoryChange) obj;

        if (!category.equals(other.category)) {
            return false;
        }

        if (delta != other.delta) {
            return false;
        }

        if (!resourceSet.containsEqualResources(other.resourceSet)) {
            return false;
        }

        return true;
    }

    public String getCategory() {
        return category;
    }

    public Delta getDelta() {
        return delta;
    }

    public ResourceSet getResourceSet() {
        return resourceSet;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((category == null) ? 0 : category.hashCode());
        result = prime * result + ((delta == null) ? 0 : delta.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "ResourceCategoriesChange [delta=" + delta + ", category="
                + category + ", resourceSet=" + resourceSet + "]";
    }

}