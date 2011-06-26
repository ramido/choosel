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
import org.thechiselgroup.choosel.core.client.util.event.PrioritizedEventHandler;
import org.thechiselgroup.choosel.core.client.util.event.PrioritizedHandlerManager;
import org.thechiselgroup.choosel.core.client.views.resolvers.DelegatingViewItemValueResolver;
import org.thechiselgroup.choosel.core.client.views.resolvers.NullViewItemValueResolver;
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
    }

    /**
     * Adds an event handler that gets called when mappings change. Supports
     * {@link PrioritizedEventHandler}.
     */
    @Override
    public HandlerRegistration addHandler(SlotMappingChangedHandler handler) {
        assert handler != null;
        return handlerManager.addHandler(SlotMappingChangedEvent.TYPE, handler);
    }

    public Slot[] getRequiredSlots() {
        return slots;
    }

    // TODO search for calls from outside this class and remove
    @Override
    public ViewItemValueResolver getResolver(Slot slot)
            throws NoResolverForSlotException {

        assert slot != null;

        if (!isConfigured(slot)) {
            throw new NoResolverForSlotException(slot,
                    slotsToResolvers.keySet());
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
        // TODO Auto-generated method stub
        return null;
    }

    // TODO why is this required? remove...
    public void initSlots(Slot[] slots) {
        assert slots != null;

        // XXX this is not correct because we have fixed slots...
        for (Slot slot : slots) {
            slotsByID.put(slot.getId(), slot);
            slotsToResolvers.put(slot, null); // XXX
        }
    }

    // TODO this is not how we would check this anymore
    @Override
    public boolean isConfigured(Slot slot) {
        assert slot != null;

        return slotsToResolvers.containsKey(slot);
    }

    // TODO remove
    public boolean isSlotInitialized(Slot slot) {
        ViewItemValueResolver viewItemValueResolver = slotsToResolvers
                .get(slot);
        return viewItemValueResolver != null
                && !(viewItemValueResolver instanceof NullViewItemValueResolver);
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

    /**
     * XXX This note will be outdated once fixed resolvers are separated from
     * the SlotMappingConfiguration.
     * <p>
     * <b>Note:</b> Slot resolvers that are not returned by getSlots() in the
     * {@link ViewContentDisplay} can still be configured to allow view content
     * display decorators to hide and preconfigure slots.
     * </p>
     */
    // TODO the UI Model will throw its own events, we should capture these
    @Override
    public void setResolver(Slot slot, ViewItemValueResolver resolver) {
        assert slot != null : "slot must not be null";
        assert resolver != null : "resolver must not be null";
        assert slotsToResolvers.containsKey(slot) : "slot " + slot
                + " is not available in " + slotsToResolvers.keySet();

        slotsToResolvers.put(slot, resolver);

        // TODO we should not fire this event, we should capture any event fired
        // by the UIModel
        handlerManager.fireEvent(new SlotMappingChangedEvent(slot));

        for (Entry<Slot, ViewItemValueResolver> entry : slotsToResolvers
                .entrySet()) {

            if ((entry.getValue() instanceof DelegatingViewItemValueResolver)
                    && (((DelegatingViewItemValueResolver) entry.getValue())
                            .getTargetSlot().equals(slot))) {
                handlerManager.fireEvent(new SlotMappingChangedEvent(entry
                        .getKey()));
            }
        }
    }
}