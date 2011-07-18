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
import java.util.Map.Entry;

import org.thechiselgroup.choosel.core.client.util.collections.CollectionFactory;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightList;
import org.thechiselgroup.choosel.core.client.util.event.EventHandlerPriority;
import org.thechiselgroup.choosel.core.client.util.event.PrioritizedEventHandler;
import org.thechiselgroup.choosel.core.client.visualization.model.Slot;
import org.thechiselgroup.choosel.core.client.visualization.model.SlotMappingChangedEvent;
import org.thechiselgroup.choosel.core.client.visualization.model.SlotMappingChangedHandler;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItem;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItemContainerChangeEvent;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItemContainerChangeEventHandler;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItemResolutionErrorModel;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItemValueResolver;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualizationModel;

public class ManagedSlotMappingConfiguration {

    private final class VisualMappingUpdaterForViewItemChanges implements
            VisualItemContainerChangeEventHandler, PrioritizedEventHandler {

        @Override
        public EventHandlerPriority getPriority() {
            return EventHandlerPriority.FIRST;
        }

        @Override
        public void onVisualItemContainerChanged(
                VisualItemContainerChangeEvent event) {

            updateVisualMappings();
        }
    }

    private VisualItemValueResolverFactoryProvider resolverProvider;

    private Map<Slot, ManagedSlotMapping> slotsToSlotMappings = new HashMap<Slot, ManagedSlotMapping>();

    private SlotMappingInitializer slotMappingInitializer;

    private VisualizationModel visualizationModel;

    private final VisualItemResolutionErrorModel errorModel;

    public ManagedSlotMappingConfiguration(
            VisualItemValueResolverFactoryProvider resolverProvider,
            SlotMappingInitializer slotMappingInitializer,
            VisualizationModel visualizationModel,
            VisualItemResolutionErrorModel errorModel) {

        assert resolverProvider != null;
        assert slotMappingInitializer != null;
        assert visualizationModel != null;
        assert errorModel != null;

        this.resolverProvider = resolverProvider;
        this.slotMappingInitializer = slotMappingInitializer;
        this.visualizationModel = visualizationModel;
        this.errorModel = errorModel;

        // this does not set up a mapping
        initSlotModels(visualizationModel.getSlots());

        visualizationModel.addHandler(new SlotMappingChangedHandler() {
            @Override
            public void onSlotMappingChanged(SlotMappingChangedEvent e) {
                handleResolverChange(e.getSlot(), e.getOldResolver(),
                        e.getCurrentResolver());
            }
        });
        visualizationModel.getFullVisualItemContainer().addHandler(
                new VisualMappingUpdaterForViewItemChanges());
    }

    public ManagedVisualItemValueResolver getCurrentResolver(Slot slot) {
        assert slotsToSlotMappings.containsKey(slot);

        return slotsToSlotMappings.get(slot).getCurrentResolver();
    }

    public ManagedSlotMapping getManagedSlotMapping(Slot slot) {
        return slotsToSlotMappings.get(slot);
    }

    public LightweightList<ManagedSlotMapping> getManagedSlotMappings() {
        LightweightList<ManagedSlotMapping> managedMappings = CollectionFactory
                .createLightweightList();
        managedMappings.addAll(slotsToSlotMappings.values());
        return managedMappings;
    }

    public Slot[] getSlots() {
        return visualizationModel.getSlots();
    }

    public LightweightList<Slot> getSlotsWithInvalidResolvers() {
        LightweightList<Slot> invalidSlots = CollectionFactory
                .createLightweightList();
        for (Entry<Slot, ManagedSlotMapping> entry : slotsToSlotMappings
                .entrySet()) {

            if (!slotHasInvalidResolver(entry.getKey())) {
                invalidSlots.add(entry.getKey());
            }
        }
        return invalidSlots;
    }

    public LightweightCollection<VisualItem> getVisualItems() {
        return visualizationModel.getFullVisualItemContainer().getVisualItems();
    }

    public void handleResolverChange(Slot slot,
            VisualItemValueResolver oldResolver,
            VisualItemValueResolver resolver) {

        assert visualizationModel.containsSlot(slot) : "slot " + slot
                + " is not a part of the " + "visualization model";

        slotsToSlotMappings.get(slot).currentResolverWasSet(oldResolver,
                resolver, getVisualItems());
    }

    private void initSlotModels(Slot[] slots) {
        for (Slot slot : slots) {
            slotsToSlotMappings.put(slot, new ManagedSlotMapping(slot,
                    resolverProvider, visualizationModel, errorModel));
        }
    }

    private void resetMappingsFromInitializer(
            LightweightCollection<Slot> unconfiguredSlots,
            LightweightCollection<VisualItem> viewItems) {

        assert !unconfiguredSlots.isEmpty();

        // TODO this is not a good way to convert to an array, find a better way
        // to do this
        Slot[] slotsAsArray = unconfiguredSlots.toList().toArray(
                new Slot[unconfiguredSlots.size()]);

        for (Entry<Slot, VisualItemValueResolver> entry : slotMappingInitializer
                .getResolvers(visualizationModel.getContentResourceSet(),
                        slotsAsArray).entrySet()) {

            Slot slot = entry.getKey();
            VisualItemValueResolver resolver = entry.getValue();

            assert resolver != null;

            if (getManagedSlotMapping(slot).isAllowableResolver(resolver,
                    viewItems)) {
                visualizationModel.setResolver(slot, resolver);
            } else {
                // Oh god, even the initializer was wrong
                // TODO throw an exception or something
            }
        }
    }

    // TODO shouldn't this be pushed to the SlotMappingUIModel
    public void setCurrentResolver(Slot slot,
            ManagedVisualItemValueResolver resolver) {
        slotsToSlotMappings.get(slot).setResolver(resolver);
    }

    public boolean slotHasInvalidResolver(Slot slot) {
        assert slotsToSlotMappings.containsKey(slot);
        return slotsToSlotMappings.get(slot).inValidState(getVisualItems());
    }

    /**
     * Call this method whenever the model changes (whenever the
     * {@link VisualItem}s change).
     */
    private void updateManagedMappings(
            LightweightCollection<VisualItem> viewItems) {
        for (ManagedSlotMapping managedMapping : slotsToSlotMappings.values()) {
            managedMapping.updateAllowableFactories(viewItems);
        }
    }

    private void updateVisualMappings() {
        updateVisualMappings(getVisualItems());
    }

    // TODO handle view items with errors in here
    private void updateVisualMappings(
            LightweightCollection<VisualItem> viewItems) {

        // check to see if the configuration is still valid
        updateManagedMappings(viewItems);

        // reset the unconfigured slots
        LightweightCollection<Slot> slots = visualizationModel
                .getUnconfiguredSlots();

        if (!slots.isEmpty()) {
            resetMappingsFromInitializer(slots, viewItems);
        }
        // TODO assert that all of the slots now have valid resolvers
    }
}