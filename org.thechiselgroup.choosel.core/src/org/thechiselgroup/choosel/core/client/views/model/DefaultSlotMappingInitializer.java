/*******************************************************************************
 * Copyright 2009, 2010 Lars Grammel 
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
import java.util.List;
import java.util.Map;

import org.thechiselgroup.choosel.core.client.resources.DataType;
import org.thechiselgroup.choosel.core.client.resources.DataTypeToListMap;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.resources.ResourceSetUtils;
import org.thechiselgroup.choosel.core.client.util.math.SumCalculation;
import org.thechiselgroup.choosel.core.client.views.resolvers.CalculationResolver;
import org.thechiselgroup.choosel.core.client.views.resolvers.FirstResourcePropertyResolver;
import org.thechiselgroup.choosel.core.client.views.resolvers.TextPropertyResolver;
import org.thechiselgroup.choosel.core.client.views.resolvers.ViewItemValueResolver;

public class DefaultSlotMappingInitializer implements SlotMappingInitializer {

    private Map<DataType, ViewItemValueResolver> defaultDataTypeResolvers = new HashMap<DataType, ViewItemValueResolver>();

    private ViewItemValueResolver getSlotResolver(
            DataTypeToListMap<String> propertiesByDataType, Slot slot) {

        DataType dataType = slot.getDataType();
        List<String> properties = propertiesByDataType.get(dataType);

        // fallback to default values if there are no corresponding slots
        if (properties.isEmpty()) {
            return defaultDataTypeResolvers.get(dataType);
        }

        assert !properties.isEmpty();

        // dynamic resolution
        String firstProperty = properties.get(0);

        switch (dataType) {
        case TEXT:
            return new TextPropertyResolver(firstProperty);
        case NUMBER:
            return new CalculationResolver(firstProperty, new SumCalculation());
        }

        return new FirstResourcePropertyResolver(firstProperty, dataType);
    }

    @Override
    public void initializeMappings(ResourceSet resources,
            ViewContentDisplay contentDisplay,
            SlotMappingConfiguration slotMappingConfiguration) {

        DataTypeToListMap<String> propertiesByDataType = ResourceSetUtils
                .getPropertiesByDataType(resources);

        for (Slot slot : contentDisplay.getSlots()) {
            if (slotMappingConfiguration.isSlotInitialized(slot)) {
                continue;
            }

            slotMappingConfiguration.setResolver(slot,
                    getSlotResolver(propertiesByDataType, slot));
        }
    }

    public void putDefaultDataTypeValues(ViewItemValueResolver resolver) {

        defaultDataTypeResolvers.put(resolver.getVisualDimensionDataType(),
                resolver);
    }
}