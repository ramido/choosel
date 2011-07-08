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
package org.thechiselgroup.choosel.core.client.views.model;

import org.thechiselgroup.choosel.core.client.views.resolvers.ViewItemValueResolver;

import com.google.gwt.event.shared.GwtEvent;

public class SlotMappingChangedEvent extends
        GwtEvent<SlotMappingChangedHandler> {

    public static final GwtEvent.Type<SlotMappingChangedHandler> TYPE = new GwtEvent.Type<SlotMappingChangedHandler>();

    private final Slot slot;

    private final ViewItemValueResolver oldResolver;

    private final ViewItemValueResolver currentResolver;

    public SlotMappingChangedEvent(Slot slot,
            ViewItemValueResolver oldResolver,
            ViewItemValueResolver currentResolver) {

        assert slot != null;
        assert currentResolver != null;

        this.slot = slot;
        this.currentResolver = currentResolver;
        this.oldResolver = oldResolver;
    }

    @Override
    protected void dispatch(SlotMappingChangedHandler handler) {
        handler.onSlotMappingChanged(this);
    }

    @Override
    public GwtEvent.Type<SlotMappingChangedHandler> getAssociatedType() {
        return TYPE;
    }

    public ViewItemValueResolver getCurrentResolver() {
        return currentResolver;
    }

    public ViewItemValueResolver getOldResolver() {
        return oldResolver;
    }

    public Slot getSlot() {
        return slot;
    }

    @Override
    public String toString() {
        return "SlotMappingChangedEvent [slot=" + slot + ", oldResolver="
                + oldResolver + ", currentResolver=" + currentResolver + "]";
    }

}