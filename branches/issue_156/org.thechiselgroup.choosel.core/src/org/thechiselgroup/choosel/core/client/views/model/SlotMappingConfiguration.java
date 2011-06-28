/*******************************************************************************
 * Copyright (C) 2011 Lars Grammel 
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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.thechiselgroup.choosel.core.client.util.collections.CollectionFactory;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightList;
import org.thechiselgroup.choosel.core.client.util.event.PrioritizedHandlerManager;
import org.thechiselgroup.choosel.core.client.views.resolvers.ViewItemValueResolver;

import com.google.gwt.event.shared.HandlerRegistration;

// TODO rename to DefaultSlotMappingConfiguration
public class SlotMappingConfiguration implements
        SlotMappingConfigurationInterface {

    private transient PrioritizedHandlerManager handlerManager;

    private Map<Slot, ViewItemValueResolver> slotsToResolvers = new HashMap<Slot, ViewItemValueResolver>();

    private Map<String, Slot> slotsByID = CollectionFactory.createStringMap();

    private Slot[] slots;

    public SlotMappingConfiguration(Slot[] slots) {
        assert slots != null;

        this.handlerManager = new PrioritizedHandlerManager(this);
        this.slots = slots;

        initSlotsById(slots);
    }

    @Override
    public HandlerRegistration addHandler(SlotMappingChangedHandler handler) {
        assert handler != null;
        return handlerManager.addHandler(SlotMappingChangedEvent.TYPE, handler);
    }

    @Override
    public ViewItemValueResolver getResolver(Slot slot)
            throws NoResolverForSlotException {

        assert slot != null;

        if (!isConfigured(slot)) {
            throw new NoResolverForSlotException(slot, slotsToResolvers);
        }

        assert slotsToResolvers.containsKey(slot);

        return slotsToResolvers.get(slot);
    }

    @Override
    public Slot getSlotById(String slotId) {
        assert slotId != null;
        return slotsByID.get(slotId);
    }

    @Override
    public Slot[] getSlots() {
        return slots;
    }

    @Override
    public LightweightCollection<Slot> getUnconfiguredSlots() {
        LightweightList<Slot> unconfiguredSlots = CollectionFactory
                .createLightweightList();
        for (Slot slot : slots) {
            if (!isConfigured(slot)) {
                unconfiguredSlots.add(slot);
            }
        }
        return unconfiguredSlots;
    }

    private void initSlotsById(Slot[] slots) {
        for (Slot slot : slots) {
            slotsByID.put(slot.getId(), slot);
        }
    }

    @Override
    public boolean isConfigured(Slot slot) {
        assert slot != null;

        return slotsToResolvers.containsKey(slot);
    }

    /**
     * @throws SlotMappingResolutionException
     *             Exception occurred while trying to resolve slot value
     */
    /*
     * TODO add semantic meta-information as parameter, e.g. expected return
     * type or context (semantic description of slot?)
     */
    public Object resolve(Slot slot, ViewItem viewItem)
            throws SlotMappingResolutionException {

        try {
            assert getResolver(slot) != null;
            return getResolver(slot).resolve(viewItem, this);
        } catch (Exception ex) {
            throw new SlotMappingResolutionException(slot, viewItem, ex);
        }
    }

    @Override
    public void setResolver(Slot slot, ViewItemValueResolver resolver) {
        assert slot != null : "slot must not be null";
        assert resolver != null : "resolver must not be null";
        // TODO extract into internal assert method
        assert slotsByID.containsKey(slot.getId()) : "slot " + slot
                + " is not allowed (valid slots: " + slotsByID.values() + ")";

        slotsToResolvers.put(slot, resolver);

        handlerManager.fireEvent(new SlotMappingChangedEvent(slot, resolver));

        // fire events for delegating resolvers that reference this slot
        for (Entry<Slot, ViewItemValueResolver> entry : slotsToResolvers
                .entrySet()) {
            for (Slot targetSlot : entry.getValue().getTargetSlots()) {
                if (targetSlot.equals(slot)) {
                    handlerManager.fireEvent(new SlotMappingChangedEvent(entry
                            .getKey(), resolver));
                }
            }
        }
    }
}