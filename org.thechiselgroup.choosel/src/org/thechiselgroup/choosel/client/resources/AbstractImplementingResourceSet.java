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

import org.thechiselgroup.choosel.client.label.DefaultHasLabel;
import org.thechiselgroup.choosel.client.label.HasLabel;
import org.thechiselgroup.choosel.client.label.LabelChangedEventHandler;
import org.thechiselgroup.choosel.client.util.SingleItemCollection;

import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;

// TODO move label support into separate class
public abstract class AbstractImplementingResourceSet extends
        AbstractResourceSet {

    protected transient HandlerManager eventBus;

    private HasLabel labelDelegate;

    public AbstractImplementingResourceSet() {
        this.eventBus = new HandlerManager(this);
        this.labelDelegate = new DefaultHasLabel(this);
    }

    @Override
    public boolean add(Resource resource) {
        assert resource != null;

        return addAll(new SingleItemCollection<Resource>(resource));
    }

    @Override
    public HandlerRegistration addEventHandler(
            ResourcesAddedEventHandler handler) {

        return eventBus.addHandler(ResourcesAddedEvent.TYPE, handler);
    }

    @Override
    public HandlerRegistration addEventHandler(
            ResourcesRemovedEventHandler handler) {

        return eventBus.addHandler(ResourcesRemovedEvent.TYPE, handler);
    }

    @Override
    public HandlerRegistration addLabelChangedEventHandler(
            LabelChangedEventHandler eventHandler) {

        return labelDelegate.addLabelChangedEventHandler(eventHandler);
    }

    public int getHandlerCount(Type<?> type) {
        return eventBus.getHandlerCount(type);
    }

    @Override
    public String getLabel() {
        return labelDelegate.getLabel();
    }

    @Override
    public boolean hasLabel() {
        return labelDelegate.hasLabel();
    }

    @Override
    public boolean isModifiable() {
        return true;
    }

    @Override
    public boolean remove(Object o) {
        assert o != null;

        if (!(o instanceof Resource)) {
            return false;
        }

        return removeAll(new SingleItemCollection<Resource>((Resource) o));
    }

    @Override
    public void setLabel(String label) {
        labelDelegate.setLabel(label);
    }

    @Override
    public Object[] toArray() {
        return toList().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return toList().toArray(a);
    }

}