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

import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.util.collections.CollectionFactory;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightList;
import org.thechiselgroup.choosel.core.client.views.resolvers.ManagedViewItemValueResolver;
import org.thechiselgroup.choosel.core.client.views.resolvers.SlotMappingUIModel;
import org.thechiselgroup.choosel.core.client.views.resolvers.ViewItemValueResolver;
import org.thechiselgroup.choosel.core.client.views.resolvers.ViewItemValueResolverFactoryProvider;

public class SlotMappingConfigurationUIModel {

    private ViewItemValueResolverFactoryProvider resolverProvider;

    private Map<Slot, SlotMappingUIModel> slotsToSlotMappings = new HashMap<Slot, SlotMappingUIModel>();

    private SlotMappingInitializer slotMappingInitializer;

    private ViewModel viewModel;

    public SlotMappingConfigurationUIModel(
            ViewItemValueResolverFactoryProvider resolverProvider,
            SlotMappingInitializer slotMappingInitializer, ViewModel viewModel) {

        assert resolverProvider != null;
        assert slotMappingInitializer != null;
        assert viewModel != null;

        this.resolverProvider = resolverProvider;
        this.slotMappingInitializer = slotMappingInitializer;
        this.viewModel = viewModel;

        // XXX why do we initialize when there are no resources yet?
        // we definitely need a state that flag slots as invalid / unresolved
        initSlots(viewModel.getSlots());

        viewModel.addHandler(new SlotMappingChangedHandler() {
            @Override
            public void onSlotMappingChanged(SlotMappingChangedEvent e) {
                setResolver(e.getSlot(),
                        (ManagedViewItemValueResolver) e.getCurrentResolver());
            }
        });
        viewModel.addHandler(new ViewItemContainerChangeEventHandler() {
            @Override
            public void onViewItemContainerChanged(
                    ViewItemContainerChangeEvent event) {
                updateVisualMappings();
            }
        });
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

    private void initSlots(Slot[] slots) {
        for (Slot slot : slots) {
            slotsToSlotMappings.put(slot, new SlotMappingUIModel(slot,
                    resolverProvider, viewModel));
        }
    }

    public void setResolver(Slot slot, ManagedViewItemValueResolver resolver) {
        slotsToSlotMappings.get(slot).setCurrentResolver(resolver);
    }

    /**
     * Call this method whenever the model changes (whenever the
     * {@link ViewItem}s change).
     */
    private void updateUIModels(LightweightCollection<ViewItem> viewItems) {
        for (SlotMappingUIModel uiModel : slotsToSlotMappings.values()) {
            uiModel.updateAllowableFactories(viewItems);
        }
    }

    private void updateVisualMappings() {
        ResourceSet viewResources = viewModel.getResourceGrouping()
                .getResourceSet();
        updateVisualMappings(viewModel.getViewItems(), viewResources);
    }

    public void updateVisualMappings(LightweightCollection<ViewItem> viewItems,
            ResourceSet viewResources) {

        // check to see if the configuration is still valid
        updateUIModels(viewItems);

        LightweightList<Slot> slots = getSlotsWithInvalidResolvers();

        if (!slots.isEmpty()) {
            Slot[] slotsAsArray = slots.toArray(new Slot[slots.size()]);
            for (Entry<Slot, ViewItemValueResolver> entry : slotMappingInitializer
                    .getResolvers(viewResources, slotsAsArray).entrySet()) {
                viewModel.setResolver(entry.getKey(), entry.getValue());
            }
        }
    }

}