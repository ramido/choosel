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

import java.util.HashMap;
import java.util.Map;

import org.thechiselgroup.choosel.client.resolver.ResourceSetToValueResolver;
import org.thechiselgroup.choosel.client.resources.ResourceSet;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;

public class SlotMappingConfiguration {

    private transient HandlerManager eventBus;

    private Map<Slot, ResourceSetToValueResolver> slotsToValueResolvers = new HashMap<Slot, ResourceSetToValueResolver>();

    public SlotMappingConfiguration() {
        eventBus = new HandlerManager(this);
    }

    public HandlerRegistration addHandler(SlotMappingChangedHandler handler) {
        assert handler != null;

        return eventBus.addHandler(SlotMappingChangedEvent.TYPE, handler);
    }

    public boolean containsResolver(Slot slot) {
        assert slot != null;

        return slotsToValueResolvers.containsKey(slot);
    }

    // TODO search for calls from outside this class and remove
    public ResourceSetToValueResolver getResolver(Slot slot) {
        assert slot != null;
        assert slotsToValueResolvers.containsKey(slot) : "no resolver for "
                + slot;

        return slotsToValueResolvers.get(slot);
    }

    /*
     * TODO add semantic meta-information as parameter, e.g. expected return
     * type or context (semantic description of slot?)
     */
    public Object resolve(Slot slot, String groupID, ResourceSet resources) {
        return getResolver(slot).resolve(resources, groupID);
    }

    public void setMapping(Slot slot, ResourceSetToValueResolver resolver) {
        assert slot != null;
        assert resolver != null;

        slotsToValueResolvers.put(slot, resolver);
        eventBus.fireEvent(new SlotMappingChangedEvent(slot));
    }

}