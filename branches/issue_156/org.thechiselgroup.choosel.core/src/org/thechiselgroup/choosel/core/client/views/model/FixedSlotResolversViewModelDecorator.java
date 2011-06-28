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
package org.thechiselgroup.choosel.core.client.views.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;

import org.thechiselgroup.choosel.core.client.resources.Resource;
import org.thechiselgroup.choosel.core.client.resources.ResourceGrouping;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.views.resolvers.ViewItemValueResolver;

import com.google.gwt.event.shared.HandlerRegistration;

/**
 * Decorator for {@link ViewModel} that sets fixed {@link ViewItemValueResolver}
 * s for one or more {@link Slot}s. The fixed slots are not exposed by this
 * decorator, only the configurable ones.
 * 
 * @author Lars Grammel
 */
// TODO needs more tests & features, e.g. for the error model decoration
public class FixedSlotResolversViewModelDecorator implements ViewModel {

    private ViewModel delegate;

    private Map<Slot, ViewItemValueResolver> fixedSlotResolvers;

    private Slot[] slots;

    public FixedSlotResolversViewModelDecorator(ViewModel delegate,
            Map<Slot, ViewItemValueResolver> fixedSlotResolvers) {

        assert delegate != null;
        assert fixedSlotResolvers != null;

        this.delegate = delegate;
        this.fixedSlotResolvers = fixedSlotResolvers;

        initFixedSlots();
        initAvailableSlots(delegate, fixedSlotResolvers);
    }

    @Override
    public HandlerRegistration addHandler(SlotMappingChangedHandler handler) {
        return delegate.addHandler(handler);
    }

    @Override
    public HandlerRegistration addHandler(
            ViewItemContainerChangeEventHandler handler) {

        return delegate.addHandler(handler);
    }

    @Override
    public boolean containsViewItem(String viewItemId) {
        return delegate.containsViewItem(viewItemId);
    }

    @Override
    public ResourceSet getContentResourceSet() {
        return delegate.getContentResourceSet();
    }

    @Override
    public ResourceSet getHighlightedResources() {
        return delegate.getHighlightedResources();
    }

    @Override
    public ViewItemValueResolver getResolver(Slot slot) {
        return delegate.getResolver(slot);
    }

    @Override
    public ResourceGrouping getResourceGrouping() {
        return delegate.getResourceGrouping();
    }

    @Override
    public ResourceSet getSelectedResources() {
        return delegate.getSelectedResources();
    }

    @Override
    public Slot getSlotById(String slotId) {
        return delegate.getSlotById(slotId);
    }

    @Override
    public Slot[] getSlots() {
        return slots;
    }

    @Override
    public LightweightCollection<Slot> getSlotsWithErrors() {
        return delegate.getSlotsWithErrors();
    }

    @Override
    public LightweightCollection<Slot> getSlotsWithErrors(ViewItem viewItem) {
        return delegate.getSlotsWithErrors(viewItem);
    }

    @Override
    public LightweightCollection<Slot> getUnconfiguredSlots() {
        return delegate.getUnconfiguredSlots();
    }

    @Override
    public ViewContentDisplay getViewContentDisplay() {
        return delegate.getViewContentDisplay();
    }

    @Override
    public ViewItem getViewItem(String viewItemId) {
        return delegate.getViewItem(viewItemId);
    }

    @Override
    public LightweightCollection<ViewItem> getViewItems() {
        return delegate.getViewItems();
    }

    @Override
    public LightweightCollection<ViewItem> getViewItems(
            Iterable<Resource> resources) {
        return delegate.getViewItems(resources);
    }

    @Override
    public LightweightCollection<ViewItem> getViewItemsWithErrors() {
        return delegate.getViewItemsWithErrors();
    }

    @Override
    public LightweightCollection<ViewItem> getViewItemsWithErrors(Slot slot) {
        return delegate.getViewItemsWithErrors(slot);
    }

    @Override
    public boolean hasErrors() {
        return delegate.hasErrors();
    }

    @Override
    public boolean hasErrors(Slot slot) {
        return delegate.hasErrors(slot);
    }

    @Override
    public boolean hasErrors(ViewItem viewItem) {
        return delegate.hasErrors(viewItem);
    }

    /**
     * This method calculates and initialized the slots field to the non-fixed
     * slots that are in the slotMappingConfiguration of the delegate. These
     * slots represent slots that the user is able to configure in the UI
     */
    private void initAvailableSlots(ViewModel delegate,
            Map<Slot, ViewItemValueResolver> fixedSlotResolvers) {
        ArrayList<Slot> slotList = new ArrayList<Slot>();
        slotList.addAll(Arrays.asList(delegate.getSlots()));
        slotList.removeAll(fixedSlotResolvers.keySet());
        this.slots = slotList.toArray(new Slot[slotList.size()]);
    }

    private void initFixedSlots() {
        for (Entry<Slot, ViewItemValueResolver> entry : fixedSlotResolvers
                .entrySet()) {
            delegate.setResolver(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public boolean isConfigured(Slot slot) {
        return delegate.isConfigured(slot);
    }

    @Override
    public void setResolver(Slot slot, ViewItemValueResolver resolver) {
        delegate.setResolver(slot, resolver);
    }

    @Override
    public void setResourceGrouping(ResourceGrouping resourceGrouping) {
        delegate.setResourceGrouping(resourceGrouping);
    }

}
