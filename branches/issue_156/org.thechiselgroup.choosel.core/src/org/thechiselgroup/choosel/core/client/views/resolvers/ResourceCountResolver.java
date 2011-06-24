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
import org.thechiselgroup.choosel.core.client.views.model.ViewItem.Subset;
import org.thechiselgroup.choosel.core.client.views.model.ViewItemValueResolverContext;

public class ResourceCountResolver extends SubsetViewItemValueResolver {

    private static final String ID = "ResourceCountResolver";

    public ResourceCountResolver() {
        this(Subset.ALL);
    }

    public ResourceCountResolver(Subset subset) {
        super(subset);
    }

    @Override
    public boolean canResolve(Slot slot,
            LightweightList<ResourceSet> resourceSets,
            ViewItemValueResolverContext context) {
        return DataType.NUMBER.equals(slot.getDataType());
    }

    @Override
    public String getResolverId() {
        return ID;
    }

    @Override
    public Object resolve(ViewItem viewItem,
            ViewItemValueResolverContext context, Subset subset) {
        return new Double(viewItem.getResources(subset).size());
    }

    @Override
    public String toString() {
        return "Count";
    }

}