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

import static org.thechiselgroup.choosel.core.client.util.AdditionalJavaAssertions.assertMapDoesNotContainEmptyLists;

import java.util.Map;

import org.thechiselgroup.choosel.core.client.util.collections.ArrayListToLightweightListAdapter;
import org.thechiselgroup.choosel.core.client.util.collections.CollectionFactory;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollections;

/**
 * <p>
 * Default implementation of {@link ViewItemResolutionErrorModel}.
 * </p>
 * <p>
 * IMPLEMENTATION NOTE: This class assumes that the {@link Slot}s that are
 * passed in have unique slot ids. If two different slots have the same slot id,
 * the functionality in this class will not work.
 * </p>
 * 
 * @author Lars Grammel
 */
public class DefaultViewItemResolutionErrorModel implements
        ViewItemResolutionErrorModel, Cloneable {

    /**
     * Contains ids of all slots that have errors as keys, and the
     * {@link ViewItem}s that could not be resolved for that slot as values.
     * Slots without errors must not be contained.
     * 
     * @see #assertInvariantIntegrity()
     */
    private Map<String, ArrayListToLightweightListAdapter<ViewItem>> errorsBySlotId = CollectionFactory
            .createStringMap();

    /**
     * Contains ids of all {@link ViewItem}s that have errors as keys, and the
     * {@link Slots}s that could not be resolved for that {@link ViewItem} as
     * values. ViewItems without errors must not be contained.
     * 
     * @see #assertInvariantIntegrity()
     */
    private Map<String, ArrayListToLightweightListAdapter<Slot>> errorsByViewItemId = CollectionFactory
            .createStringMap();

    private ArrayListToLightweightListAdapter<Slot> slotsWithErrors = new ArrayListToLightweightListAdapter<Slot>();

    private ArrayListToLightweightListAdapter<ViewItem> viewItemsWithErrors = new ArrayListToLightweightListAdapter<ViewItem>();

    private void addErrorSlotToViewItem(ViewItem viewItem, Slot slot) {
        String viewItemId = viewItem.getViewItemID();

        if (!errorsByViewItemId.containsKey(viewItemId)) {
            errorsByViewItemId.put(viewItemId,
                    new ArrayListToLightweightListAdapter<Slot>());
            viewItemsWithErrors.add(viewItem);
        }

        ArrayListToLightweightListAdapter<Slot> viewItemErrors = errorsByViewItemId
                .get(viewItemId);
        if (!viewItemErrors.contains(slot)) {
            viewItemErrors.add(slot);
        }
    }

    private void addErrorViewItemToSlot(Slot slot, ViewItem viewItem) {
        String slotId = slot.getId();

        if (!errorsBySlotId.containsKey(slotId)) {
            errorsBySlotId.put(slotId,
                    new ArrayListToLightweightListAdapter<ViewItem>());
            slotsWithErrors.add(slot);
        }

        ArrayListToLightweightListAdapter<ViewItem> slotErrors = errorsBySlotId
                .get(slotId);
        if (!slotErrors.contains(viewItem)) {
            slotErrors.add(viewItem);
        }
    }

    private void assertErrorSlotsIntegrity() {
        assert errorsBySlotId.size() == slotsWithErrors.size();
        for (Slot slot : slotsWithErrors) {
            assert errorsBySlotId.containsKey(slot.getId()) : "Slot with id "
                    + slot.getId() + " not contained in slotsWithErrors";
        }
    }

    /**
     * There are the following class invariants:
     * 
     * <ul>
     * <li>errorsBySlotId must not contain empty lists</li>
     * <li>errorsByViewItemId must not contain empty lists</li>
     * <li>errorsBySlotId keys must match Slots in slotsWithErrors</li>
     * <li>errorsByViewItemId keys must match ViewItems in viewItemsWithErrors</li>
     * </ul>
     */
    private void assertInvariantIntegrity() {
        assertMapDoesNotContainEmptyLists(errorsBySlotId);
        assertMapDoesNotContainEmptyLists(errorsByViewItemId);
        assertErrorSlotsIntegrity();
        assertViewItemsIntegrity();
    }

    private void assertViewItemsIntegrity() {
        assert errorsByViewItemId.size() == viewItemsWithErrors.size();
        for (ViewItem viewItem : viewItemsWithErrors) {
            assert errorsByViewItemId.containsKey(viewItem.getViewItemID()) : "ViewItem with id "
                    + viewItem.getViewItemID()
                    + " not contained in viewItemsWithErrors";
        }
    }

    public void clearErrors(LightweightCollection<ViewItem> viewItems) {
        assert viewItems != null;
        assertInvariantIntegrity();

        for (ViewItem viewItem : viewItems) {
            clearErrors(viewItem);
        }

        assertInvariantIntegrity();
    }

    public void clearErrors(Slot slot) {
        assert slot != null;
        assertInvariantIntegrity();

        if (!slotsWithErrors.contains(slot)) {
            return;
        }

        slotsWithErrors.remove(slot);
        ArrayListToLightweightListAdapter<ViewItem> removedViewItems = errorsBySlotId
                .remove(slot.getId());
        for (ViewItem viewItem : removedViewItems) {
            removeSlotFromViewItemErrors(slot, viewItem);
        }

        assertInvariantIntegrity();
        assert !errorsBySlotId.containsKey(slot.getId());
        assert !hasErrors(slot);
    }

    public void clearErrors(ViewItem viewItem) {
        assert viewItem != null;
        assertInvariantIntegrity();

        if (!viewItemsWithErrors.contains(viewItem)) {
            return;
        }

        viewItemsWithErrors.remove(viewItem);
        ArrayListToLightweightListAdapter<Slot> removedSlots = errorsByViewItemId
                .remove(viewItem.getViewItemID());
        for (Slot slot : removedSlots) {
            removeViewItemFromSlotErrors(viewItem, slot);
        }

        assertInvariantIntegrity();
        assert !errorsByViewItemId.containsKey(viewItem.getViewItemID());
        assert !hasErrors(viewItem);
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
        assert viewItem != null;
        assert slot != null;
        assertInvariantIntegrity();

        removeSlotFromViewItemErrors(slot, viewItem);
        removeViewItemFromSlotErrors(viewItem, slot);

        assertInvariantIntegrity();
    }

    public void removeSlotFromViewItemErrors(Slot slot, ViewItem viewItem) {
        assert errorsByViewItemId.containsKey(viewItem.getViewItemID());

        ArrayListToLightweightListAdapter<Slot> slotErrors = errorsByViewItemId
                .get(viewItem.getViewItemID());
        slotErrors.remove(slot);

        // remove slot if no errors left
        if (slotErrors.isEmpty()) {
            errorsByViewItemId.remove(viewItem.getViewItemID());
            viewItemsWithErrors.remove(viewItem);
        }
    }

    private void removeViewItemFromSlotErrors(ViewItem viewItem, Slot slot) {
        assert errorsBySlotId.containsKey(slot.getId());

        ArrayListToLightweightListAdapter<ViewItem> slotErrors = errorsBySlotId
                .get(slot.getId());
        slotErrors.remove(viewItem);

        // remove slot if no errors left
        if (slotErrors.isEmpty()) {
            errorsBySlotId.remove(slot.getId());
            slotsWithErrors.remove(slot);
        }
    }

    public void reportError(Slot slot, ViewItem viewItem) {
        assert slot != null;
        assert viewItem != null;
        assertInvariantIntegrity();

        addErrorViewItemToSlot(slot, viewItem);
        addErrorSlotToViewItem(viewItem, slot);

        assertInvariantIntegrity();
    }

    public void reportErrors(Slot slot,
            LightweightCollection<ViewItem> viewItems) {

        assert slot != null;
        assert viewItems != null;
        assertInvariantIntegrity();

        for (ViewItem viewItem : viewItems) {
            reportError(slot, viewItem);
        }

        assertInvariantIntegrity();
    }

}
