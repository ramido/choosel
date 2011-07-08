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

import com.google.gwt.event.shared.HandlerRegistration;

// TODO extract resource item manager?
public class TestViewContentDisplayCallback implements
        ViewContentDisplayCallback, VisualItemContainer {

    private Map<String, VisualItem> viewItemsByGroupId = CollectionFactory
            .createStringMap();

    @Override
    public HandlerRegistration addHandler(
            VisualItemContainerChangeEventHandler handler) {
        // TODO Auto-generated method stub
        return null;
    }

    public void addViewItem(VisualItem viewItem) {
        viewItemsByGroupId.put(viewItem.getId(), viewItem);
    }

    public void addViewItems(Iterable<VisualItem> viewItems) {
        for (VisualItem viewItem : viewItems) {
            addViewItem(viewItem);
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
    public VisualItem getViewItem(String groupId) {
        return viewItemsByGroupId.get(groupId);
    }

    @Override
    public LightweightCollection<VisualItem> getViewItems() {
        LightweightList<VisualItem> result = CollectionFactory
                .createLightweightList();
        result.addAll(viewItemsByGroupId.values());
        return result;
    }

    @Override
    public LightweightCollection<VisualItem> getViewItems(
            Iterable<Resource> resources) {

        return null;
    }

    public void removeResourceItem(VisualItem viewItem) {
        viewItemsByGroupId.remove(viewItem.getId());
    }

}