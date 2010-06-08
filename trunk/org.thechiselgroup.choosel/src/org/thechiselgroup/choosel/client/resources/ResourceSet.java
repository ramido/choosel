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

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.GwtEvent.Type;

public interface ResourceSet extends Iterable<Resource>, ResourceContainer,
	HasLabel {

    // TODO more specific addXXXHandler methods
    <H extends ResourceEventHandler> HandlerRegistration addHandler(
	    Type<H> type, H handler);

    void clear();

    boolean contains(Resource resource);

    boolean containsAll(Iterable<Resource> resources);

    boolean containsEqualResources(ResourceSet other);

    boolean containsResourceWithUri(String uri);

    Resource getByUri(String uri);

    boolean isEmpty();

    boolean isModifiable();

    @Override
    Iterator<Resource> iterator();

    int size();

    void switchContainment(Resource resource);
    
    void switchContainment(ResourceSet resources);

    // FIXME toList should be unmodifiable copy
    List<Resource> toList();

    // XXX hack to make changes in resource item work
    // trace and replace with something more sensible,
    // especially in the graph
    Resource getFirstResource();
    
}