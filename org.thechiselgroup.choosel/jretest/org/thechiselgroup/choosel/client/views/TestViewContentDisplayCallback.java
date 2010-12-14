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
import org.thechiselgroup.choosel.client.views.slots.Slot;

// TODO extract resource item manager?
public class TestViewContentDisplayCallback implements
        ViewContentDisplayCallback {

    private Map<String, ViewItem> resourceItemsByGroupId = CollectionFactory
            .createStringMap();

    public void addResourceItem(ViewItem resourceItem) {
        resourceItemsByGroupId.put(resourceItem.getViewItemID(), resourceItem);
    }

    public void addResourceItems(Iterable<ViewItem> resourceItems) {
        for (ViewItem resourceItem : resourceItems) {
            addResourceItem(resourceItem);
        }
    }

    @Override
    public boolean containsViewItem(String groupId) {
        return resourceItemsByGroupId.containsKey(groupId);
    }

    @Override
    public ResourceSet getAutomaticResourceSet() {
        return null;
    }

    @Override
    public ViewItem getViewItem(String groupId) {
        return resourceItemsByGroupId.get(groupId);
    }

    @Override
    public LightweightCollection<ViewItem> getViewItems() {
        LightweightList<ViewItem> result = CollectionFactory
                .createLightweightList();
        result.addAll(resourceItemsByGroupId.values());
        return result;
    }

    @Override
    public LightweightCollection<ViewItem> getViewItems(
            Iterable<Resource> resources) {

        return null;
    }

    @Override
    public String getSlotResolverDescription(Slot slot) {
        return null;
    }

    public void removeResourceItem(ViewItem resourceItem) {
        resourceItemsByGroupId.remove(resourceItem.getViewItemID());
    }

    @Override
    public void switchSelection(ResourceSet resources) {
    }

}