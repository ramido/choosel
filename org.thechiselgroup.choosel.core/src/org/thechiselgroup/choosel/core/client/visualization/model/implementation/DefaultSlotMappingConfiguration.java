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

    /**
     * @return {@link Slot}s that have {@link VisualItemValueResolver}s that
     *         have {@code slot} as one of their target slots.
     * 
     * @see VisualItemValueResolver#getTargetSlots()
     */
    /*
     * XXX find deeper dependencies (several levels, use breadth/depth first
     * search)
     * 
     * XXX make sure slots are only found once (string set, test case)
     * 
     * XXX depth-first search must consider unconfigured slots
     */
    public LightweightList<Slot> getDependentSlots(Slot slot) {
        LightweightList<Slot> dependentSlots = CollectionFactory
                .createLightweightList();
        for (Entry<Slot, VisualItemValueResolver> entry : slotsToResolvers
                .entrySet()) {

            VisualItemValueResolver otherResolver = entry.getValue();
            LightweightCollection<Slot> targetSlots = otherResolver
                    .getTargetSlots();

            assert targetSlots != null : "getTargetSlots() for resolver "
                    + otherResolver.getClass() + " must not be null";

            for (Slot targetSlot : targetSlots) {
                if (targetSlot.equals(slot)) {
                    dependentSlots.add(entry.getKey());
                    break;
                }
            }
        }
        return dependentSlots;
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

        LightweightList<Slot> dependentSlots = getDependentSlots(slot);

        for (Slot dependentSlot : dependentSlots) {
            handlerManager.fireEvent(new SlotMappingChangedEvent(dependentSlot,
                    oldResolver, resolver));
        }

        assertInvariants();
    }

}