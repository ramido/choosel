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
package org.thechiselgroup.choosel.client.views;

import java.util.Collection;
import java.util.Set;

import org.thechiselgroup.choosel.client.resources.CombinedResourceSet;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceSet;

public interface ResourceModel {

    /**
     * Add the resources to the contents of this view without displaying the
     * resource set explicitly.
     */
    void addResources(Collection<Resource> resources);

    /**
     * Explicit adding of the resource set as a new, displayed resource set.
     */
    void addResourceSet(ResourceSet resourceSet);

    void clear();

    /**
     * Checks if the resources are displayed in this view.
     */
    boolean containsResources(Collection<Resource> resources);

    /**
     * Checks if this labeled resource set is explicitly displayed in this view.
     */
    boolean containsResourceSet(ResourceSet resourceSet);

    ResourceSet getAutomaticResourceSet();

    CombinedResourceSet getCombinedUserResourceSets();

    /**
     * Returns an unmodifiable resource set containing all resources displayed
     * in this view.
     */
    ResourceSet getResources();

    /**
     * Removes resources that are <b>not</b> contained in any explicitly added
     * resource set.
     */
    void removeResources(Collection<Resource> resources);

    /**
     * Removes a resource set that was explicitly added via
     * {@link #addResourceSet(ResourceSet)}. We assert that the resource set has
     * a label.
     */
    void removeResourceSet(ResourceSet resourceSet);

    /**
     * @return new <code>Set</code> that contains only the subset of
     *         <code>resources</code> that is contained in this resource model
     */
    Set<Resource> retain(Set<Resource> resources);

}