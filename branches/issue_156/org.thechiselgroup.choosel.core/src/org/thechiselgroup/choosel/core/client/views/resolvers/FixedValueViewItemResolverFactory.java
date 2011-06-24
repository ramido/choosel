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

import org.thechiselgroup.choosel.core.client.resources.DataType;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightList;
import org.thechiselgroup.choosel.core.client.views.model.Slot;

public class FixedValueViewItemResolverFactory implements
        ViewItemValueResolverFactory {

    private final Object value;

    private final DataType dataType;

    private final String id;

    public FixedValueViewItemResolverFactory(Object value, DataType valueType,
            String id) {
        this.value = value;
        this.dataType = valueType;
        this.id = id;
    }

    @Override
    public boolean canCreateApplicableResolver(Slot slot,
            LightweightList<ResourceSet> resourceSets) {
        return slot.getDataType().equals(dataType);
    }

    /**
     * This method does not need to worry about the viewItems because it is
     * fixed value
     */
    @Override
    public ViewItemValueResolver create(
            LightweightList<ResourceSet> resourceSets) {
        return new FixedValueResolver(value, id, dataType);
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
