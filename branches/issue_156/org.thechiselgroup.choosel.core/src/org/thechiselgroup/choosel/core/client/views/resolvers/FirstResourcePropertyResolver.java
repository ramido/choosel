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
import org.thechiselgroup.choosel.core.client.resources.Resource;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.resources.ResourceSetUtils;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightList;
import org.thechiselgroup.choosel.core.client.views.model.Slot;
import org.thechiselgroup.choosel.core.client.views.model.ViewItem;
import org.thechiselgroup.choosel.core.client.views.model.ViewItem.Subset;
import org.thechiselgroup.choosel.core.client.views.model.ViewItemValueResolverContext;

public class FirstResourcePropertyResolver extends SubsetViewItemValueResolver
        implements PropertyDependantViewItemValueResolver {

    protected final String property;

    protected final DataType dataType;

    protected final String resolverID;

    public FirstResourcePropertyResolver(String resolverID, String property,
            DataType dataType) {
        this(resolverID, property, dataType, Subset.ALL);
    }

    public FirstResourcePropertyResolver(String resolverID, String property,
            DataType dataType, Subset subset) {
        super(subset);
        assert property != null;
        assert dataType != null;
        assert resolverID != null;
        this.dataType = dataType;
        this.property = property;
        this.resolverID = resolverID;
    }

    @Override
    public boolean canResolve(Slot slot,
            LightweightList<ResourceSet> resourceSets,
            ViewItemValueResolverContext context) {
        if (!slot.getDataType().equals(dataType)) {
            return false;
        }

        for (ResourceSet resourceSet : resourceSets) {
            for (Resource resource : resourceSet) {
                if (!resource.containsProperty(property)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String getProperty() {
        return property;
    }

    @Override
    public String getResolverId() {
        return resolverID;
    }

    @Override
    public Object resolve(ViewItem viewItem,
            ViewItemValueResolverContext context, Subset subset) {
        ResourceSet resources = viewItem.getResources(subset);
        return ResourceSetUtils.firstResource(resources).getValue(property);
    }

    @Override
    public String toString() {
        return property + " (first item)";
    }

}