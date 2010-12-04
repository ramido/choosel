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

import java.util.Iterator;
import java.util.List;

import org.thechiselgroup.choosel.client.label.HasLabel;
import org.thechiselgroup.choosel.client.util.collections.CollectionFactory;
import org.thechiselgroup.choosel.client.util.collections.LightweightList;

import com.google.gwt.event.shared.HandlerRegistration;

/**
 * <p>
 * Classes implementing this interface manage sets of resources. They support
 * event notification on changes and are more heavy-weight than plain arrays or
 * sets. For intermediate calculations that do not require events etc, consider
 * using a {@link LightweightList} via {@link CollectionFactory}.
 * </p>
 * <p>
 * <b>IMPLEMENTATION NOTE</b>: This interface originally extended
 * java.util.Set<Resource>. However, the collection classes in java.util require
 * supporting subclasses of Resource, as well as supporting generic objects in
 * contains and remove. In GWT, this lead to a performance penalty, as
 * instanceof checks and class casting are fairly expensive (at least according
 * to profiling in Chrome 8). Therefore, this interface now provides methods
 * that resemble the Java collections API, but are specific to Resources.
 * </p>
 * 
 * @see Resource
 */
public interface ResourceSet extends HasLabel, Iterable<Resource> {

    boolean add(Resource resource);

    boolean addAll(Iterable<Resource> resources);

    HandlerRegistration addEventHandler(ResourcesAddedEventHandler handler);

    HandlerRegistration addEventHandler(ResourcesRemovedEventHandler handler);

    /**
     * Removes all resources from this resource set.
     */
    void clear();

    boolean contains(Resource resource);

    boolean containsAll(Iterable<Resource> resources);

    boolean containsEqualResources(ResourceSet other);

    boolean containsResourceWithUri(String uri);

    Resource getByUri(String uri);

    // XXX hack to make changes in resource item work
    // trace and replace with something more sensible,
    // especially in the graph
    Resource getFirstResource();

    /**
     * Calculates the intersection of this resource set and the resources in the
     * parameter. <b>Assumes that resources are sorted by URIs String natural
     * order</b>.
     * 
     * @param sortedResources
     *            Sorted Iterable over resources that are in both this resource
     *            set and in the param. Resources can appear multiple times.
     * 
     * @return Sorted intersection (resources are sorted by the natural order of
     *         their URIs). Each resource is contained at most once.
     */
    LightweightList<Resource> getIntersection(Iterable<Resource> sortedResources);

    boolean isEmpty();

    boolean isModifiable();

    /**
     * @return Iterator over the resources in this resource set. Resource sets
     *         are sorted by the natural order of the URIs of the contained
     *         resources.
     */
    @Override
    Iterator<Resource> iterator();

    boolean remove(Resource resource);

    boolean removeAll(Iterable<Resource> resources);

    boolean removeAll(ResourceSet resources);

    /**
     * Removes all resources from this resource set that are not contained in
     * the parameter resource set.
     * 
     * @param resources
     *            resource set with resource that will remain in this resource
     *            set
     * 
     * @return true if this resource set changed as a result of this operation
     */
    boolean retainAll(ResourceSet resources);

    /**
     * Returns the number of resources in this resource set.
     * 
     * @return number of resources in this set.
     */
    int size();

    void switchContainment(Resource resource);

    void switchContainment(ResourceSet resources);

    /**
     * @return Unmodifiable List that contains elements from this resource set.
     */
    List<Resource> toList();

}