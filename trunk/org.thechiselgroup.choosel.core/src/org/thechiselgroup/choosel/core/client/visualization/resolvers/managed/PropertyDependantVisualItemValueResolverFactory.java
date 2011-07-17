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
package org.thechiselgroup.choosel.core.client.visualization.resolvers.managed;

import java.util.List;

import org.thechiselgroup.choosel.core.client.resources.ResourceSetUtils;
import org.thechiselgroup.choosel.core.client.util.DataType;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.visualization.model.Slot;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItem;
import org.thechiselgroup.choosel.core.client.visualization.model.managed.ManagedVisualItemValueResolver;
import org.thechiselgroup.choosel.core.client.visualization.model.managed.VisualItemValueResolverFactory;
import org.thechiselgroup.choosel.core.client.visualization.resolvers.PropertyDependantViewItemValueResolver;

public abstract class PropertyDependantVisualItemValueResolverFactory implements
        VisualItemValueResolverFactory {

    private final DataType dataType;

    private final String resolverId;

    protected PropertyDependantVisualItemValueResolverFactory(
            String resolverId, DataType dataType) {

        assert dataType != null;
        assert resolverId != null;

        this.dataType = dataType;
        this.resolverId = resolverId;
    }

    @Override
    public boolean canCreateApplicableResolver(Slot slot,
            LightweightCollection<VisualItem> viewItems) {

        assert slot != null;
        assert viewItems != null;

        if (!slot.getDataType().equals(getValidDataType())) {
            return false;
        }

        if (viewItems.isEmpty()) {
            return true;
        }

        return !ResourceSetUtils.getSharedPropertiesOfDataType(viewItems,
                getValidDataType()).isEmpty();
    }

    /**
     * This can fail if you do not first check to see if this factory is
     * applicable. Checking if the factory can create a resolver will set an
     * initial property for the resolver to use
     */
    // XXX this method does some inference - does it belong here??
    @Override
    public ManagedVisualItemValueResolver create(
            LightweightCollection<VisualItem> viewItems) {

        assert viewItems != null;
        List<String> properties = ResourceSetUtils
                .getSharedPropertiesOfDataType(viewItems, getValidDataType());
        assert !properties.isEmpty();
        return create(properties.get(0));
    }

    public ManagedVisualItemValueResolver create(String property) {
        return new PropertyDependantManagedVisualItemValueResolverDecorator(
                getId(), createUnmanagedResolver(property));
    }

    protected abstract PropertyDependantViewItemValueResolver createUnmanagedResolver(
            String property);

    @Override
    public String getId() {
        return resolverId;
    }

    public DataType getValidDataType() {
        return dataType;
    }

}