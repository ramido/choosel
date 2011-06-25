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
import java.util.Set;

import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.util.collections.CollectionFactory;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightList;
import org.thechiselgroup.choosel.core.client.views.resolvers.SlotMappingUIModel;
import org.thechiselgroup.choosel.core.client.views.resolvers.ViewItemValueResolver;
import org.thechiselgroup.choosel.core.client.views.resolvers.ViewItemValueResolverFactoryProvider;

public class SlotMappingConfigurationUIModel {

    private ViewItemValueResolverFactoryProvider resolverProvider;

    private Map<Slot, SlotMappingUIModel> slotsToSlotMappings = new HashMap<Slot, SlotMappingUIModel>();

    private SlotMappingInitializer slotMappingInitializer;

    public SlotMappingConfigurationUIModel(
            ViewItemValueResolverFactoryProvider resolverProvider,
            SlotMappingInitializer slotMappingInitializer) {

        assert resolverProvider != null;
        assert slotMappingInitializer != null;

        this.resolverProvider = resolverProvider;
        this.slotMappingInitializer = slotMappingInitializer;
    }

    public LightweightList<Slot> getSlotsWithInvalidResolvers() {
        LightweightList<Slot> invalidSlots = CollectionFactory
                .createLightweightList();
        for (Entry<Slot, SlotMappingUIModel> entry : slotsToSlotMappings
                .entrySet()) {

            if (!entry.getValue().hasCurrentResolver()) {
                invalidSlots.add(entry.getKey());
            }
        }
        return invalidSlots;
    }

    public void initSlots(Set<Slot> slots) {
        for (Slot slot : slots) {
            slotsToSlotMappings.put(slot, new SlotMappingUIModel(slot,
                    resolverProvider));
        }
    }

    public void setResolver(Slot slot, ViewItemValueResolver resolver) {
        slotsToSlotMappings.get(slot).setCurrentResolver(resolver);
    }

    /**
     * Call this method whenever the model changes (whenever the
     * {@link ViewItem}s change).
     */
    public void updateUIModels(LightweightList<ResourceSet> resourceSets) {
        for (SlotMappingUIModel uiModel : slotsToSlotMappings.values()) {
            uiModel.updateAllowableFactories(resourceSets);
        }
    }

    public void updateVisualMappings(DefaultViewModel defaultViewModel,
            Map<String, ResourceSet> resourceSetMap, ResourceSet viewResources) {

        // check to see if the configuration is still valid
        LightweightList<ResourceSet> resourceSets = CollectionFactory
                .createLightweightList();
        resourceSets.addAll(resourceSetMap.values());
        updateUIModels(resourceSets);

        LightweightList<Slot> slots = getSlotsWithInvalidResolvers();

        if (!slots.isEmpty()) {
            Slot[] slotsAsArray = slots.toArray(new Slot[slots.size()]);
            for (Entry<Slot, ViewItemValueResolver> entry : slotMappingInitializer
                    .getResolvers(viewResources, slotsAsArray).entrySet()) {
                defaultViewModel.setResolver(entry.getKey(), entry.getValue());
            }
        }
    }

}