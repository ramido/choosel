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

import org.thechiselgroup.choosel.core.client.util.DataType;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.util.math.Calculation;
import org.thechiselgroup.choosel.core.client.visualization.model.Slot;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItem;
import org.thechiselgroup.choosel.core.client.visualization.model.managed.ManagedVisualItemValueResolver;
import org.thechiselgroup.choosel.core.client.visualization.resolvers.CalculationResolver;

public class CalculationResolverFactory extends
        PropertyDependantVisualItemValueResolverFactory {

    private final Calculation calculation;

    private final String id;

    public CalculationResolverFactory(String id, Calculation calculation) {
        assert calculation != null;
        assert id != null;

        this.id = id;
        this.calculation = calculation;
    }

    @Override
    public boolean canCreateApplicableResolver(Slot slot,
            LightweightCollection<VisualItem> viewItems) {

        if (!slot.getDataType().equals(DataType.NUMBER)) {
            return false;
        }

        return !getSharedProperties(viewItems).isEmpty();
    }

    /**
     * This can fail if you do not first check to see if this factory is
     * applicable. Checking if the factory can create a resolver will set an
     * initial property for the resolver to use
     */
    @Override
    public ManagedVisualItemValueResolver create(
            LightweightCollection<VisualItem> viewItems) {

        List<String> properties = getSharedProperties(viewItems);
        assert !properties.isEmpty();
        return create(properties.get(0));
    }

    @Override
    public ManagedVisualItemValueResolver create(String property) {
        return new PropertyDependantManagedVisualItemValueResolverDecorator(id,
                new CalculationResolver(property, calculation));
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getLabel() {
        return calculation.toString();
    }
}
