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
package org.thechiselgroup.choosel.core.client.visualization.model.implementation;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.thechiselgroup.choosel.core.client.util.collections.CollectionFactory;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightList;
import org.thechiselgroup.choosel.core.client.util.event.PrioritizedHandlerManager;
import org.thechiselgroup.choosel.core.client.visualization.model.NoResolverForSlotException;
import org.thechiselgroup.choosel.core.client.visualization.model.Slot;
import org.thechiselgroup.choosel.core.client.visualization.model.SlotMappingChangedEvent;
import org.thechiselgroup.choosel.core.client.visualization.model.SlotMappingChangedHandler;
import org.thechiselgroup.choosel.core.client.visualization.model.SlotMappingConfiguration;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItemValueResolver;

import com.google.gwt.event.shared.HandlerRegistration;

// TODO rename to DefaultSlotMappingConfiguration
public class DefaultSlotMappingConfiguration implements
        SlotMappingConfiguration {

    // TODO move
    private static <S, T> void assertNoNullValues(Map<S, T> map) {
        for (Entry<S, T> entry : map.entrySet()) {
            assert entry.getValue() != null : "map entry for " + entry.getKey()
                    + " must not be null";
        }
    }

    private transient PrioritizedHandlerManager handlerManager;

    private Map<Slot, VisualItemValueResolver> slotsToResolvers = new HashMap<Slot, VisualItemValueResolver>();

    private Map<String, Slot> slotsByID = CollectionFactory.createStringMap();

    private Slot[] slots;

    public DefaultSlotMappingConfiguration(Slot[] slots) {
        assert slots != null;

        this.handlerManager = new PrioritizedHandlerManager(this);
        this.slots = slots;

        initSlotsById(slots);

        assertInvariants();
    }

    @Override
    public HandlerRegistration addHandler(SlotMappingChangedHandler handler) {
        assert handler != null;
        return handlerManager.addHandler(SlotMappingChangedEvent.TYPE, handler);
    }

    private void assertInvariants() {
        assertNoNullValues(slotsToResolvers);
    }

    @Override
    public VisualItemValueResolver getResolver(Slot slot)
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

    @Override
    public void setResolver(Slot slot, VisualItemValueResolver resolver) {
        assertInvariants();
        assert slot != null : "slot must not be null";
        assert resolver != null : "resolver must not be null";
        assert resolver.getTargetSlots() != null : "resolver "
                + resolver.toString() + " getTargetSlots() must not be null";
        // TODO extract into internal assert method
        assert slotsByID.containsKey(slot.getId()) : "slot " + slot
                + " is not allowed (valid slots: " + slotsByID.values() + ")";

        VisualItemValueResolver oldResolver = slotsToResolvers.get(slot);
        slotsToResolvers.put(slot, resolver);

        handlerManager.fireEvent(new SlotMappingChangedEvent(slot, oldResolver,
                resolver));

        // TODO this stuff down here breaks with fixed delegating resolvers
        // fire events for delegating resolvers that reference this slot
        for (Entry<Slot, VisualItemValueResolver> entry : slotsToResolvers
                .entrySet()) {
            LightweightCollection<Slot> targetSlots = entry.getValue()
                    .getTargetSlots();

            assert targetSlots != null : "getTargetSlots() for resolver "
                    + entry.getValue().getClass() + " must not be null";

            for (Slot targetSlot : targetSlots) {
                if (targetSlot.equals(slot)) {
                    // TODO I'm not sure if this is how target resolvers work,
                    // ask lars

                    // TODO if the target resolver is fixed we probably DO NOT
                    // want to fire an event
                    handlerManager.fireEvent(new SlotMappingChangedEvent(entry
                            .getKey(), oldResolver, resolver));
                }
            }
        }

        assertInvariants();
    }

}