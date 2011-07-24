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
package org.thechiselgroup.choosel.core.client.visualization.model.managed;

import java.util.HashMap;
import java.util.Map;

import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.visualization.model.Slot;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItemValueResolver;
import org.thechiselgroup.choosel.core.client.visualization.resolvers.managed.SingleSlotDependentVisualItemResolverFactory;

public class PresetSlotMappingInitializer implements SlotMappingInitializer {

    private final Map<Slot, VisualItemValueResolver> slotMappings = new HashMap<Slot, VisualItemValueResolver>();

    @Override
    public Map<Slot, VisualItemValueResolver> getResolvers(
            ResourceSet viewResources, Map<Slot, ManagedSlotMappingState> states) {

        return slotMappings;
    }

    @Override
    public Map<Slot, VisualItemValueResolver> getResolvers(
            ResourceSet viewResources, Slot[] slotsToUpdate) {

        return slotMappings;
    }

    public void putSlotMapping(Slot slot,
            SingleSlotDependentVisualItemResolverFactory resolverFactory) {
        slotMappings.put(slot, resolverFactory.create());
    }
}