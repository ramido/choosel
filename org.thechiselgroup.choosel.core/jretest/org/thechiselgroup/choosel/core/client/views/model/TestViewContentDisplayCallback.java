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
package org.thechiselgroup.choosel.core.client.views.model;

import java.util.Map;

import org.thechiselgroup.choosel.core.client.resources.Resource;
import org.thechiselgroup.choosel.core.client.util.collections.CollectionFactory;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightList;
import org.thechiselgroup.choosel.core.client.views.resolvers.ViewItemValueResolver;

// TODO extract resource item manager?
public class TestViewContentDisplayCallback implements
        ViewContentDisplayCallback, ViewItemContainer {

    private Map<String, ViewItem> viewItemsByGroupId = CollectionFactory
            .createStringMap();

    public void addResourceItem(ViewItem viewItem) {
        viewItemsByGroupId.put(viewItem.getViewItemID(), viewItem);
    }

    public void addResourceItems(Iterable<ViewItem> viewItems) {
        for (ViewItem viewItem : viewItems) {
            addResourceItem(viewItem);
        }
    }

    @Override
    public boolean containsViewItem(String viewItemId) {
        return viewItemsByGroupId.containsKey(viewItemId);
    }

    @Override
    public ViewItemValueResolver getResolver(Slot slot) {
        return null;
    }

    @Override
    public String getSlotResolverDescription(Slot slot) {
        return null;
    }

    @Override
    public ViewItem getViewItem(String groupId) {
        return viewItemsByGroupId.get(groupId);
    }

    @Override
    public LightweightCollection<ViewItem> getViewItems() {
        LightweightList<ViewItem> result = CollectionFactory
                .createLightweightList();
        result.addAll(viewItemsByGroupId.values());
        return result;
    }

    @Override
    public LightweightCollection<ViewItem> getViewItems(
            Iterable<Resource> resources) {

        return null;
    }

    public void removeResourceItem(ViewItem viewItem) {
        viewItemsByGroupId.remove(viewItem.getViewItemID());
    }

}