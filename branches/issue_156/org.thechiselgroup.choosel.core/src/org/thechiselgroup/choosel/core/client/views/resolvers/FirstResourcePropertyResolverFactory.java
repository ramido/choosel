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
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightList;
import org.thechiselgroup.choosel.core.client.views.model.Slot;

public class FirstResourcePropertyResolverFactory implements
        ViewItemValueResolverFactory {

    private final DataType dataType;

    private final String resolverID;

    public FirstResourcePropertyResolverFactory(DataType dataType,
            String resolverID) {
        this.dataType = dataType;
        this.resolverID = resolverID;
    }

    @Override
    public boolean canCreateApplicableResolver(Slot slot,
            LightweightList<ResourceSet> resourceSets) {

        if (!slot.getDataType().equals(dataType)) {
            return false;
        }

        // for now, all resolvers should be applicable when there are no view
        // items
        if (resourceSets.isEmpty()) {
            return true;
        }

        return !getSharedProperties(resourceSets).isEmpty();
    }

    @Override
    public ViewItemValueResolver create(
            LightweightList<ResourceSet> resourceSets) {
        List<String> properties = getSharedProperties(resourceSets);
        assert !properties.isEmpty();
        return new FirstResourcePropertyResolver(resolverID, properties.get(0),
                dataType);
    }

    @Override
    public String getId() {
        return resolverID;
    }

    // TODO Perhaps a better value for this
    @Override
    public String getLabel() {
        return "Property Selector";
    }

    private List<String> getSharedProperties(
            LightweightList<ResourceSet> resourceSets) {
        List<String> properties = new ArrayList<String>();
        // intialize properties to be the ones in the first resource
        if (resourceSets.isEmpty()) {
            return properties;
        }

        Resource firstResource = resourceSets.get(0).iterator().next();
        properties.addAll(firstResource.getProperties().keySet());

        // only keep properties that are shared by all of the resource
        for (ResourceSet resourceSet : resourceSets) {
            for (Resource resource : resourceSet) {
                properties.retainAll(resource.getProperties().keySet());
            }
        }
        return properties;
    }

}