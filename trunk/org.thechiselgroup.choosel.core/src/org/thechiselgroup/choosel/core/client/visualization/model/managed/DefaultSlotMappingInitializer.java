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

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.thechiselgroup.choosel.core.client.resources.DataTypeToListMap;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.resources.ResourceSetUtils;
import org.thechiselgroup.choosel.core.client.util.DataType;
import org.thechiselgroup.choosel.core.client.visualization.model.Slot;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItemValueResolver;
import org.thechiselgroup.choosel.core.client.visualization.resolvers.managed.FixedVisualItemResolverFactory;
import org.thechiselgroup.choosel.core.client.visualization.resolvers.managed.PropertyDependantVisualItemValueResolverFactory;

public class DefaultSlotMappingInitializer implements SlotMappingInitializer {

    private Map<DataType, VisualItemValueResolver> fixedResolversByDataType = new EnumMap<DataType, VisualItemValueResolver>(
            DataType.class);

    private Map<DataType, String> propertyResolverIdsByDataType = new EnumMap<DataType, String>(
            DataType.class);

    private VisualItemValueResolverFactoryProvider factoryProvider;

    public DefaultSlotMappingInitializer(
            VisualItemValueResolverFactoryProvider factoryProvider) {

        assert factoryProvider != null;
        this.factoryProvider = factoryProvider;
    }

    private VisualItemValueResolver createPropertyResolver(DataType dataType,
            String firstProperty) {

        VisualItemValueResolverFactory factory = factoryProvider
                .getFactoryById(propertyResolverIdsByDataType.get(dataType));
        PropertyDependantVisualItemValueResolverFactory resolverFactory = (PropertyDependantVisualItemValueResolverFactory) factory;
        ManagedVisualItemValueResolver resolver = resolverFactory
                .create(firstProperty);
        assert resolver != null;
        return resolver;
    }

    private VisualItemValueResolver getFixedResolver(DataType dataType) {
        assert fixedResolversByDataType.containsKey(dataType) : "no fixed resolver for "
                + dataType;
        return fixedResolversByDataType.get(dataType);
    }

    @Override
    public Map<Slot, VisualItemValueResolver> getResolvers(
            ResourceSet viewResources, Map<Slot, ManagedSlotMappingState> states) {

        // TODO make this more intelligent
        return getResolvers(viewResources, states.keySet().toArray(new Slot[0]));
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

        if (!propertyResolverIdsByDataType.containsKey(dataType)) {
            throw new UnableToInitializeSlotException(slot);
        }

        return createPropertyResolver(dataType, firstProperty);
    }

    public void setFixedDataTypeResolverId(DataType dataType, String resolverId) {

        assert dataType != null;
        assert resolverId != null;
        assert factoryProvider.getFactoryById(resolverId) != null;
        assert factoryProvider.getFactoryById(resolverId) instanceof FixedVisualItemResolverFactory : "resolver factory with id '"
                + resolverId + "' is not a FixedVisualItemResolverFactory";

        VisualItemValueResolver resolver = ((FixedVisualItemResolverFactory) factoryProvider
                .getFactoryById(resolverId)).create();

        fixedResolversByDataType.put(dataType, resolver);
    }

    public String setPropertyDataTypeResolverId(DataType dataType,
            String resolverId) {
        return propertyResolverIdsByDataType.put(dataType, resolverId);
    }
}