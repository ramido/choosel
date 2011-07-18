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

import org.thechiselgroup.choosel.core.client.util.DataType;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.visualization.model.Slot;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItem;
import org.thechiselgroup.choosel.core.client.visualization.model.managed.ManagedVisualItemValueResolver;
import org.thechiselgroup.choosel.core.client.visualization.model.managed.ManagedVisualItemValueResolverDecorator;
import org.thechiselgroup.choosel.core.client.visualization.model.managed.VisualItemValueResolverFactory;
import org.thechiselgroup.choosel.core.client.visualization.resolvers.FixedValueResolver;

public class FixedVisualItemResolverFactory implements
        VisualItemValueResolverFactory {

    private final Object value;

    private final DataType dataType;

    private final String id;

    public FixedVisualItemResolverFactory(Object value, DataType valueType,
            String id) {

        this.value = value;
        this.dataType = valueType;
        this.id = id;
    }

    @Override
    public boolean canCreateApplicableResolver(Slot slot,
            LightweightCollection<VisualItem> viewItems) {

        return slot.getDataType().equals(dataType);
    }

    /**
     * This method does not need to worry about the viewItems because it is
     * fixed value
     */
    public ManagedVisualItemValueResolver create() {
        return new ManagedVisualItemValueResolverDecorator(id,
                new FixedValueResolver(value, dataType));
    }

    @Override
    public ManagedVisualItemValueResolver create(
            LightweightCollection<VisualItem> viewItems) {
        return create();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getLabel() {
        return value.toString();
    }

}