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
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.views.model.Slot;
import org.thechiselgroup.choosel.core.client.views.model.ViewItem;
import org.thechiselgroup.choosel.core.client.views.model.ViewItem.Subset;

public class ResourceCountResolverFactory implements
        ViewItemValueResolverFactory {

    public static final String ID = "ResourceCountResolverFactory";

    private final Subset subset;

    public ResourceCountResolverFactory() {
        this(Subset.ALL);
    }

    public ResourceCountResolverFactory(Subset subset) {
        this.subset = subset;
    }

    @Override
    public boolean canCreateApplicableResolver(Slot slot,
            LightweightCollection<ViewItem> viewItems) {
        return slot.getDataType().equals(DataType.NUMBER);
    }

    public ManagedViewItemValueResolver create() {
        return new ManagedViewItemValueResolverDecorator(getId(),
                new ResourceCountResolver(subset));
    }

    @Override
    public ManagedViewItemValueResolver create(
            LightweightCollection<ViewItem> viewItems) {
        return new ManagedViewItemValueResolverDecorator(getId(),
                new ResourceCountResolver(subset));
    }

    // TODO use class name?
    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getLabel() {
        return "Count";
    }

}
