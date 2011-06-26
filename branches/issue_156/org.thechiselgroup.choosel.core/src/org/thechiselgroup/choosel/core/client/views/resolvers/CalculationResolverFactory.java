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
package org.thechiselgroup.choosel.core.client.views.resolvers;

import java.util.ArrayList;
import java.util.List;

import org.thechiselgroup.choosel.core.client.resources.DataType;
import org.thechiselgroup.choosel.core.client.resources.Resource;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightList;
import org.thechiselgroup.choosel.core.client.util.math.Calculation;
import org.thechiselgroup.choosel.core.client.views.model.Slot;
import org.thechiselgroup.choosel.core.client.views.model.ViewItem;

public class CalculationResolverFactory implements ViewItemValueResolverFactory {

    private final Calculation calculation;

    private final String id;

    public CalculationResolverFactory(String id, Calculation calculation) {
        this.id = id;
        assert calculation != null;
        assert id != null;

        this.calculation = calculation;
    }

    @Override
    public boolean canCreateApplicableResolver(Slot slot,
            LightweightList<ViewItem> viewItems) {

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
    public ManagedViewItemValueResolver create(
            LightweightList<ViewItem> viewItems) {

        List<String> properties = getSharedProperties(viewItems);

        assert !properties.isEmpty();

        return new ManagedViewItemValueResolverAdapter(id,
                new CalculationResolver(properties.get(0), calculation));
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getLabel() {
        return calculation.toString();
    }

    // TODO move somewhere else
    private List<String> getSharedProperties(LightweightList<ViewItem> viewItems) {

        List<String> properties = new ArrayList<String>();
        // intialize properties to be the ones in the first resource
        Resource firstResource = viewItems.get(0).getResources().iterator()
                .next();
        properties.addAll(firstResource.getProperties().keySet());

        // only keep properties that are shared by all of the resource
        for (ViewItem viewItem : viewItems) {
            for (Resource resource : viewItem.getResources()) {
                properties.retainAll(resource.getProperties().keySet());
            }
        }
        return properties;
    }
}
