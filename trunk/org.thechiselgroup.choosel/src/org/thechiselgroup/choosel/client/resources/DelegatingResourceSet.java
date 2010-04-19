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

import org.thechiselgroup.choosel.client.label.LabelChangedEventHandler;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.GwtEvent.Type;

public class DelegatingResourceSet extends AbstractResourceSet {

    private final ResourceSet delegate;

    public DelegatingResourceSet(ResourceSet delegate) {
	this.delegate = delegate;
    }

    public void add(Resource resource) {
	delegate.add(resource);
    }

    public <H extends ResourceEventHandler> HandlerRegistration addHandler(
	    Type<H> type, H handler) {
	return delegate.addHandler(type, handler);
    }

    public HandlerRegistration addLabelChangedEventHandler(
	    LabelChangedEventHandler eventHandler) {
	return delegate.addLabelChangedEventHandler(eventHandler);
    }

    public boolean contains(Resource resource) {
	return delegate.contains(resource);
    }

    public Resource getByUri(String uri) {
	return delegate.getByUri(uri);
    }

    public ResourceSet getDelegate() {
	return delegate;
    }

    public String getLabel() {
	return delegate.getLabel();
    }

    public boolean hasLabel() {
	return delegate.hasLabel();
    }

    public boolean isEmpty() {
	return delegate.isEmpty();
    }

    @Override
    public boolean isModifiable() {
	return delegate.isModifiable();
    }

    public Iterator<Resource> iterator() {
	return delegate.iterator();
    }

    public void remove(Resource resource) {
	delegate.remove(resource);
    }

    public void setLabel(String label) {
	delegate.setLabel(label);
    }

    public int size() {
	return delegate.size();
    }

    public List<Resource> toList() {
	return delegate.toList();
    }

}