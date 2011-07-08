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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.views.resolvers.ViewItemValueResolver;

/**
 * Sets up the configuration based on the Map from Slot to Resolver passed in in
 * the constructor.
 */
public class TestSlotMappingInitializer implements SlotMappingInitializer {

    private final Map<Slot, ViewItemValueResolver> initialSlotMapping;

    public TestSlotMappingInitializer(
            Map<Slot, ViewItemValueResolver> initialSlotMapping) {
        this.initialSlotMapping = initialSlotMapping;
    }

    @Override
    public Map<Slot, ViewItemValueResolver> getResolvers(
            ResourceSet viewResources, Slot[] slotsToUpdate) {

        Map<Slot, ViewItemValueResolver> result = new HashMap<Slot, ViewItemValueResolver>();

        List<Slot> slotList = Arrays.asList(slotsToUpdate);
        for (Entry<Slot, ViewItemValueResolver> entry : initialSlotMapping
                .entrySet()) {
            if (slotList.contains(entry.getKey())) {
                // TODO, this is not checking to see if the intializers resolver
                // is correct
                result.put(entry.getKey(), entry.getValue());
            }
        }

        return result;
    }
}