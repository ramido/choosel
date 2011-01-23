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
package org.thechiselgroup.choosel.core.client.resources;

import org.thechiselgroup.choosel.core.client.util.Delta;

/**
 * Delta change to the resource groupings.
 * 
 * @author Lars Grammel
 */
public class ResourceGroupingChange {

    private Delta delta;

    private String groupID;

    private ResourceSet resourceSet;

    /**
     * @param delta
     *            kind of change (ADD / UPDATE / REMOVE )
     * @param groupID
     *            identifier of the changed group. The identifier is local to
     *            the grouping.
     * @param resourceSet
     *            What is included here depends on the delta: Delta.ADD: new
     *            content (all resources in set); Delta.UPDATE: new content (all
     *            resources in set); Delta.REMOVE: old content
     */
    public ResourceGroupingChange(Delta delta, String groupID,
            ResourceSet resourceSet) {

        assert delta != null;
        assert groupID != null;
        assert resourceSet != null;

        this.delta = delta;
        this.groupID = groupID;
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

        ResourceGroupingChange other = (ResourceGroupingChange) obj;

        if (!groupID.equals(other.groupID)) {
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

    /**
     * @return kind of change (ADD / UPDATE / REMOVE )
     */
    public Delta getDelta() {
        return delta;
    }

    /**
     * @return identifier of the changed group
     */
    public String getGroupID() {
        return groupID;
    }

    /**
     * @return What is included here depends on the delta: Delta.ADD: new
     *         content (all resources in set); Delta.UPDATE: new content (all
     *         resources in set); Delta.REMOVE: old content
     */
    public ResourceSet getResourceSet() {
        return resourceSet;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((groupID == null) ? 0 : groupID.hashCode());
        result = prime * result + ((delta == null) ? 0 : delta.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "ResourceGroupingChange [delta=" + delta + ", groupID="
                + groupID + ", resourceSet=" + resourceSet + "]";
    }

}