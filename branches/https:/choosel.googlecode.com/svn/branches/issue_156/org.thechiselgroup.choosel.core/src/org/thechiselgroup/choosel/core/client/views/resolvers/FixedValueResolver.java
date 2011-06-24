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
package org.thechiselgroup.choosel.core.client.views.resolvers;

import org.thechiselgroup.choosel.core.client.resources.DataType;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightList;
import org.thechiselgroup.choosel.core.client.views.model.Slot;
import org.thechiselgroup.choosel.core.client.views.model.ViewItem;
import org.thechiselgroup.choosel.core.client.views.model.ViewItemValueResolverContext;

public class FixedValueResolver extends
        AbstractEventHandlingViewItemValueResolver implements
        ViewItemValueResolver {

    private final DataType dataType;

    private final Object value;

    private final String id;

    public FixedValueResolver(Object value, String id, DataType dataType) {
        this.value = value;
        this.id = id;
        this.dataType = dataType;
    }

    @Override
    public boolean canResolve(Slot slot,
            LightweightList<ResourceSet> resourceSets,
            ViewItemValueResolverContext context) {
        return slot.getDataType().equals(dataType);
    }

    @Override
    public String getResolverId() {
        return id;
    }

    @Override
    public Object resolve(ViewItem viewItem,
            ViewItemValueResolverContext context) {
        return value;
    }

    @Override
    public String toString() {
        return "Constant: " + value;
    }

}