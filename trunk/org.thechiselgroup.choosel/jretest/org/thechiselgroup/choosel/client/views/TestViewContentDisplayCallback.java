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
package org.thechiselgroup.choosel.client.views;

import java.util.Map;

import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.util.collections.CollectionFactory;
import org.thechiselgroup.choosel.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.client.util.collections.LightweightList;

// TODO extract resource item manager?
public class TestViewContentDisplayCallback implements
        ViewContentDisplayCallback {

    private Map<String, ResourceItem> resourceItemsByGroupId = CollectionFactory
            .createStringMap();

    public void addResourceItem(ResourceItem resourceItem) {
        resourceItemsByGroupId.put(resourceItem.getGroupID(), resourceItem);
    }

    public void addResourceItems(Iterable<ResourceItem> resourceItems) {
        for (ResourceItem resourceItem : resourceItems) {
            addResourceItem(resourceItem);
        }
    }

    @Override
    public boolean containsResourceItem(String groupId) {
        return resourceItemsByGroupId.containsKey(groupId);
    }

    @Override
    public ResourceSet getAutomaticResourceSet() {
        return null;
    }

    @Override
    public ResourceItem getResourceItemByGroupID(String groupId) {
        return resourceItemsByGroupId.get(groupId);
    }

    @Override
    public LightweightCollection<ResourceItem> getResourceItems() {
        LightweightList<ResourceItem> result = CollectionFactory
                .createLightweightList();
        result.addAll(resourceItemsByGroupId.values());
        return result;
    }

    @Override
    public LightweightCollection<ResourceItem> getResourceItems(
            Iterable<Resource> resources) {

        return null;
    }

    @Override
    public String getSlotResolverDescription(Slot slot) {
        return null;
    }

    public void removeResourceItem(ResourceItem resourceItem) {
        resourceItemsByGroupId.remove(resourceItem.getGroupID());
    }

    @Override
    public void switchSelection(ResourceSet resources) {
    }

}