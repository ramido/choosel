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
package org.thechiselgroup.choosel.core.client.visualization.model.implementation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;

import org.thechiselgroup.choosel.core.client.resources.Resource;
import org.thechiselgroup.choosel.core.client.resources.ResourceMultiCategorizer;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.visualization.model.Slot;
import org.thechiselgroup.choosel.core.client.visualization.model.SlotMappingChangedHandler;
import org.thechiselgroup.choosel.core.client.visualization.model.ViewContentDisplay;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItem;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItemContainerChangeEventHandler;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItemValueResolver;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualizationModel;

import com.google.gwt.event.shared.HandlerRegistration;

/**
 * Decorator for {@link VisualizationModel} that sets fixed
 * {@link VisualItemValueResolver} s for one or more {@link Slot}s. The fixed
 * slots are not exposed by this decorator, only the configurable ones.
 * 
 * @author Lars Grammel
 */
// TODO needs more tests & features, e.g. for the error model decoration
public class FixedSlotResolversVisualizationModelDecorator implements
        VisualizationModel {

    private VisualizationModel delegate;

    private Map<Slot, VisualItemValueResolver> fixedSlotResolvers;

    private Slot[] slots;

    public FixedSlotResolversVisualizationModelDecorator(
            VisualizationModel delegate,
            Map<Slot, VisualItemValueResolver> fixedSlotResolvers) {

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
            VisualItemContainerChangeEventHandler handler) {

        return delegate.addHandler(handler);
    }

    @Override
    public boolean containsVisualItem(String viewItemId) {
        return delegate.containsVisualItem(viewItemId);
    }

    @Override
    public ResourceMultiCategorizer getCategorizer() {
        return delegate.getCategorizer();
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
    public VisualItemValueResolver getResolver(Slot slot) {
        return delegate.getResolver(slot);
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
    public LightweightCollection<Slot> getSlotsWithErrors(VisualItem viewItem) {
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
    public VisualItem getVisualItem(String viewItemId) {
        return delegate.getVisualItem(viewItemId);
    }

    @Override
    public LightweightCollection<VisualItem> getVisualItems() {
        return delegate.getVisualItems();
    }

    @Override
    public LightweightCollection<VisualItem> getVisualItems(
            Iterable<Resource> resources) {
        return delegate.getVisualItems(resources);
    }

    @Override
    public LightweightCollection<VisualItem> getVisualItemsWithErrors() {
        return delegate.getVisualItemsWithErrors();
    }

    @Override
    public LightweightCollection<VisualItem> getVisualItemsWithErrors(Slot slot) {
        return delegate.getVisualItemsWithErrors(slot);
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
    public boolean hasErrors(VisualItem viewItem) {
        return delegate.hasErrors(viewItem);
    }

    /**
     * This method calculates and initialized the slots field to the non-fixed
     * slots that are in the slotMappingConfiguration of the delegate. These
     * slots represent slots that the user is able to configure in the UI
     */
    private void initAvailableSlots(VisualizationModel delegate,
            Map<Slot, VisualItemValueResolver> fixedSlotResolvers) {
        ArrayList<Slot> slotList = new ArrayList<Slot>();
        slotList.addAll(Arrays.asList(delegate.getSlots()));
        slotList.removeAll(fixedSlotResolvers.keySet());
        this.slots = slotList.toArray(new Slot[slotList.size()]);
    }

    private void initFixedSlots() {
        for (Entry<Slot, VisualItemValueResolver> entry : fixedSlotResolvers
                .entrySet()) {
            delegate.setResolver(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public boolean isConfigured(Slot slot) {
        return delegate.isConfigured(slot);
    }

    @Override
    public void setCategorizer(ResourceMultiCategorizer newCategorizer) {
        delegate.setCategorizer(newCategorizer);
    }

    @Override
    public void setContentResourceSet(ResourceSet resources) {
        delegate.setContentResourceSet(resources);
    }

    @Override
    public void setResolver(Slot slot, VisualItemValueResolver resolver) {
        delegate.setResolver(slot, resolver);
    }

}
