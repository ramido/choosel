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

import static org.thechiselgroup.choosel.core.client.visualization.resolvers.managed.PreconfiguredVisualItemValueResolverFactoryProvider.SUM_RESOLVER_FACTORY_ID;
import static org.thechiselgroup.choosel.core.client.visualization.resolvers.managed.PreconfiguredVisualItemValueResolverFactoryProvider.TEXT_PROPERTY_RESOLVER_FACTORY_ID;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.thechiselgroup.choosel.core.client.resources.DataTypeToListMap;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.resources.ResourceSetUtils;
import org.thechiselgroup.choosel.core.client.util.DataType;
import org.thechiselgroup.choosel.core.client.visualization.model.Slot;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItemValueResolver;
import org.thechiselgroup.choosel.core.client.visualization.resolvers.managed.PropertyDependantVisualItemValueResolverFactory;

public class DefaultSlotMappingInitializer implements SlotMappingInitializer {

    private Map<DataType, VisualItemValueResolver> fixedResolversByDataType = new HashMap<DataType, VisualItemValueResolver>();

    private VisualItemValueResolverFactoryProvider factoryProvider;

    public DefaultSlotMappingInitializer(
            VisualItemValueResolverFactoryProvider factoryProvider) {
        assert factoryProvider != null;

        this.factoryProvider = factoryProvider;
    }

    private VisualItemValueResolver getFixedResolver(DataType dataType) {
        assert fixedResolversByDataType.containsKey(dataType) : "no fixed resolver for "
                + dataType;
        return fixedResolversByDataType.get(dataType);
    }

    @Override
    public Map<Slot, VisualItemValueResolver> getResolvers(
            ResourceSet viewResources, Slot[] slotsToUpdate) {

        DataTypeToListMap<String> propertiesByDataType = ResourceSetUtils
                .getPropertiesByDataType(viewResources);

        Map<Slot, VisualItemValueResolver> result = new HashMap<Slot, VisualItemValueResolver>();
        for (Slot slot : slotsToUpdate) {
            result.put(slot, getSlotResolver(propertiesByDataType, slot));
        }
        return result;
    }

    private VisualItemValueResolver getSlotResolver(
            DataTypeToListMap<String> propertiesByDataType, Slot slot) {

        DataType dataType = slot.getDataType();
        List<String> properties = propertiesByDataType.get(dataType);

        // fallback to default values if there are no corresponding slots
        if (properties.isEmpty()) {
            return getFixedResolver(dataType);
        }

        assert !properties.isEmpty();

        // dynamic resolution
        String firstProperty = properties.get(0);

        switch (dataType) {
        case TEXT:
            PropertyDependantVisualItemValueResolverFactory textResolverFactory = (PropertyDependantVisualItemValueResolverFactory) factoryProvider
                    .getFactoryById(TEXT_PROPERTY_RESOLVER_FACTORY_ID);
            ManagedVisualItemValueResolver textResolver = textResolverFactory
                    .create(firstProperty);
            assert textResolver != null;
            return textResolver;
        case NUMBER:

            PropertyDependantVisualItemValueResolverFactory sumResolverFactory = (PropertyDependantVisualItemValueResolverFactory) factoryProvider
                    .getFactoryById(SUM_RESOLVER_FACTORY_ID);
            ManagedVisualItemValueResolver sumResolver = sumResolverFactory
                    .create(firstProperty);
            assert sumResolver != null;
            return sumResolver;
        }

        throw new UnableToInitializeSlotException(slot);
    }

    public void putDefaultDataTypeValues(DataType dataType,
            VisualItemValueResolver resolver) {

        assert dataType != null;
        assert resolver != null;

        fixedResolversByDataType.put(dataType, resolver);
    }
}