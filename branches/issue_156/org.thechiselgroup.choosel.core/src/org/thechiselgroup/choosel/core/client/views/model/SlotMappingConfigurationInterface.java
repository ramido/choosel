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

import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.util.event.PrioritizedEventHandler;
import org.thechiselgroup.choosel.core.client.views.resolvers.ViewItemValueResolver;

import com.google.gwt.event.shared.HandlerRegistration;

//TODO rename to SlotMappingConfiguration
public interface SlotMappingConfigurationInterface extends
        ViewItemValueResolverContext {

    /**
     * @param handler
     *            {@link SlotMappingChangedHandler} that gets called when a slot
     *            mapping changes. Supports {@link PrioritizedEventHandler}.
     */
    HandlerRegistration addHandler(SlotMappingChangedHandler handler);

    /**
     * @return {code true}, if a {@link ViewItemValueResolver} is configured for
     *         {code slot}.
     */
    // TODO rename to isConfigured
    boolean containsResolver(Slot slot);

    /**
     * @param slotId
     *            id of the slot
     * @return {@link Slot} with the ID {@code slotId}
     */
    // TODO throws NoSuchSlotException
    Slot getSlotById(String slotId);

    /**
     * @return {@link Slot}s that need to be configured.
     */
    // TODO change return type to LightweightCollection<Slot>
    Slot[] getSlots();

    /**
     * @return {@link Slot}s from {@link #getSlots()} for which no
     *         {@link ViewItemValueResolver} are configured.
     * 
     * @see #containsResolver(Slot)
     */
    LightweightCollection<Slot> getUnconfiguredSlots();

    // TODO document
    void setResolver(Slot slot, ViewItemValueResolver resolver);

}