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

import java.util.Map;

import org.thechiselgroup.choosel.core.client.util.collections.ArrayListToLightweightListAdapter;
import org.thechiselgroup.choosel.core.client.util.collections.CollectionFactory;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollections;

public class DefaultViewItemResolutionErrorModel implements
        ViewItemResolutionErrorModel {

    /**
     * Contains ids of all slots that have errors as keys, and the
     * {@link ViewItem}s that could not be resolved for that slot as values.
     * Slots without errors must not be contained.
     */
    private Map<String, ArrayListToLightweightListAdapter<ViewItem>> errorsBySlotId = CollectionFactory
            .createStringMap();

    /**
     * Contains ids of all {@link ViewItem}s that have errors as keys, and the
     * {@link Slots}s that could not be resolved for that {@link ViewItem} as
     * values. ViewItems without errors must not be contained.
     */
    private Map<String, ArrayListToLightweightListAdapter<Slot>> errorsByViewItemId = CollectionFactory
            .createStringMap();

    private ArrayListToLightweightListAdapter<Slot> slotsWithErrors = new ArrayListToLightweightListAdapter<Slot>();

    private ArrayListToLightweightListAdapter<ViewItem> viewItemsWithErrors = new ArrayListToLightweightListAdapter<ViewItem>();

    public void addError(Slot slot, ViewItem viewItem) {
        assert slot != null;
        assert viewItem != null;

        addErrorViewItemToSlot(slot, viewItem);
        addErrorSlotToViewItem(viewItem, slot);
    }

    private void addErrorSlotToViewItem(ViewItem viewItem, Slot slot) {
        String viewItemId = viewItem.getViewItemID();

        if (!errorsByViewItemId.containsKey(viewItemId)) {
            errorsByViewItemId.put(viewItemId,
                    new ArrayListToLightweightListAdapter<Slot>());
        }

        // TODO test for non double add
        errorsByViewItemId.get(viewItemId).add(slot);
        viewItemsWithErrors.add(viewItem);
    }

    private void addErrorViewItemToSlot(Slot slot, ViewItem viewItem) {
        String slotId = slot.getId();

        if (!errorsBySlotId.containsKey(slotId)) {
            errorsBySlotId.put(slotId,
                    new ArrayListToLightweightListAdapter<ViewItem>());
        }

        // TODO test for non double add
        errorsBySlotId.get(slotId).add(viewItem);
        slotsWithErrors.add(slot);
    }

    public void clearErrors(Slot slot) {
    }

    public void clearErrors(ViewItem viewItem) {
    }

    @Override
    public LightweightCollection<Slot> getSlotsWithErrors() {
        return slotsWithErrors;
    }

    @Override
    public LightweightCollection<Slot> getSlotsWithErrors(ViewItem viewItem) {
        assert viewItem != null;

        if (!hasErrors(viewItem)) {
            return LightweightCollections.emptyCollection();
        }

        return errorsByViewItemId.get(viewItem.getViewItemID());
    }

    @Override
    public LightweightCollection<ViewItem> getViewItemsWithErrors() {
        return viewItemsWithErrors;
    }

    @Override
    public LightweightCollection<ViewItem> getViewItemsWithErrors(Slot slot) {
        assert slot != null;

        if (!hasErrors(slot)) {
            return LightweightCollections.emptyCollection();
        }

        return errorsBySlotId.get(slot.getId());
    }

    @Override
    public boolean hasErrors() {
        return !slotsWithErrors.isEmpty();
    }

    @Override
    public boolean hasErrors(Slot slot) {
        assert slot != null;

        return errorsBySlotId.containsKey(slot.getId());
    }

    @Override
    public boolean hasErrors(ViewItem viewItem) {
        assert viewItem != null;

        return errorsByViewItemId.containsKey(viewItem.getViewItemID());
    }

    public void removeError(Slot slot, ViewItem viewItem) {

    }

}
