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

public interface ReadableResourceSet extends Iterable<Resource> {

    HandlerRegistration addEventHandler(ResourcesAddedEventHandler handler);

    HandlerRegistration addEventHandler(ResourcesRemovedEventHandler handler);

    boolean contains(Resource resource);

    boolean containsAll(Collection<?> resources);

    boolean containsEqualResources(ResourceSet other);

    boolean containsResourceWithUri(String uri);

    Resource getByUri(String uri);

    boolean isEmpty();

    @Override
    Iterator<Resource> iterator();

    int size();

    // FIXME toList should be unmodifiable copy
    List<Resource> toList();

}