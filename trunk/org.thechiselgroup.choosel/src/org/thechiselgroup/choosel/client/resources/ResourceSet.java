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

import java.util.List;
import java.util.Set;

import org.thechiselgroup.choosel.client.label.HasLabel;

import com.google.gwt.event.shared.HandlerRegistration;

/**
 * Classes implementing this interface manage sets of resources. They support
 * event notification on changes, and are more heavy-weight than plain lists.
 * Thus, simple ArrayLists should be preferred if the additional functionality
 * (labels, events) are not required.
 */
public interface ResourceSet extends HasLabel, Set<Resource> {

    HandlerRegistration addEventHandler(ResourcesAddedEventHandler handler);

    HandlerRegistration addEventHandler(ResourcesRemovedEventHandler handler);

    boolean containsEqualResources(ResourceSet other);

    boolean containsResourceWithUri(String uri);

    Resource getByUri(String uri);

    // XXX hack to make changes in resource item work
    // trace and replace with something more sensible,
    // especially in the graph
    Resource getFirstResource();

    boolean isModifiable();

    void switchContainment(Resource resource);

    void switchContainment(ResourceSet resources);

    /**
     * @return Unmodifiable List that contains elements from this resource set.
     */
    List<Resource> toList();

}