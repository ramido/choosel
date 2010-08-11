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
import java.util.Iterator;
import java.util.List;

import com.google.gwt.event.shared.HandlerRegistration;

public class DelegatingReadableResourceSet implements ReadableResourceSet {

    protected ReadableResourceSet delegate;

    public DelegatingReadableResourceSet(ReadableResourceSet delegate) {
        this.delegate = delegate;
    }

    @Override
    public HandlerRegistration addEventHandler(
            ResourcesAddedEventHandler handler) {
        return delegate.addEventHandler(handler);
    }

    @Override
    public HandlerRegistration addEventHandler(
            ResourcesRemovedEventHandler handler) {
        return delegate.addEventHandler(handler);
    }

    @Override
    public boolean contains(Resource resource) {
        return delegate.contains(resource);
    }

    @Override
    public boolean containsAll(Collection<?> resources) {
        return delegate.containsAll(resources);
    }

    @Override
    public boolean containsEqualResources(ResourceSet other) {
        return delegate.containsEqualResources(other);
    }

    @Override
    public boolean containsResourceWithUri(String uri) {
        return delegate.containsResourceWithUri(uri);
    }

    @Override
    public Resource getByUri(String uri) {
        return delegate.getByUri(uri);
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public Iterator<Resource> iterator() {
        return delegate.iterator();
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public List<Resource> toList() {
        return delegate.toList();
    }

}
