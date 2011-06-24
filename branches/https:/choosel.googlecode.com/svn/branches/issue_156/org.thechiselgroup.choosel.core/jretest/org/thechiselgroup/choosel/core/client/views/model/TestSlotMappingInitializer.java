package org.thechiselgroup.choosel.core.client.views.model;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.views.resolvers.ViewItemValueResolver;

/**
 * sets up the configuration based on the Map from Slot to Resolver passed in in
 * the constructor
 */
public class TestSlotMappingInitializer implements SlotMappingInitializer {
    private final Map<Slot, ViewItemValueResolver> initialSlotMapping;

    public TestSlotMappingInitializer(
            Map<Slot, ViewItemValueResolver> initialSlotMapping) {
        this.initialSlotMapping = initialSlotMapping;
    }

    @Override
    public void initializeMappings(ResourceSet resources,
            ViewContentDisplay contentDisplay,
            SlotMappingConfiguration slotMappingConfiguration) {
        for (Entry<Slot, ViewItemValueResolver> entry : initialSlotMapping
                .entrySet()) {
            slotMappingConfiguration.setResolver(entry.getKey(),
                    entry.getValue());
        }
    }

    @Override
    public void updateMappings(ResourceSet resources,
            SlotMappingConfiguration slotMappingConfiguration,
            Slot[] slotsToUpdate) {

        List<Slot> slotList = Arrays.asList(slotsToUpdate);

        for (Entry<Slot, ViewItemValueResolver> entry : initialSlotMapping
                .entrySet()) {
            if (slotList.contains(entry.getKey())) {

                // TODO, this is not checking to see if the intializers resolver
                // is correct
                slotMappingConfiguration.setResolver(entry.getKey(),
                        entry.getValue());
            }
        }
    }
}