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
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.thechiselgroup.choosel.core.client.resources.CategorizableResourceGroupingChange;
import org.thechiselgroup.choosel.core.client.resources.CategorizableResourceGroupingChange.DeltaType;
import org.thechiselgroup.choosel.core.client.resources.DefaultResourceSet;
import org.thechiselgroup.choosel.core.client.resources.IntersectionResourceSet;
import org.thechiselgroup.choosel.core.client.resources.Resource;
import org.thechiselgroup.choosel.core.client.resources.ResourceGrouping;
import org.thechiselgroup.choosel.core.client.resources.ResourceGroupingChangedEvent;
import org.thechiselgroup.choosel.core.client.resources.ResourceGroupingChangedHandler;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.resources.ResourceSetChangedEvent;
import org.thechiselgroup.choosel.core.client.resources.ResourceSetChangedEventHandler;
import org.thechiselgroup.choosel.core.client.util.Disposable;
import org.thechiselgroup.choosel.core.client.util.DisposeUtil;
import org.thechiselgroup.choosel.core.client.util.HandlerRegistrationSet;
import org.thechiselgroup.choosel.core.client.util.Initializable;
import org.thechiselgroup.choosel.core.client.util.collections.CollectionFactory;
import org.thechiselgroup.choosel.core.client.util.collections.CombinedIterable;
import org.thechiselgroup.choosel.core.client.util.collections.Delta;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollections;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightList;
import org.thechiselgroup.choosel.core.client.util.event.PrioritizedHandlerManager;
import org.thechiselgroup.choosel.core.client.views.resolvers.ViewItemValueResolver;

import com.google.gwt.event.shared.HandlerRegistration;

/**
 * <p>
 * <b>Robustness</b>: If {@link Exception}s are thrown by the
 * {@link ViewContentDisplay} during <code>update</code> operations, these
 * Exceptions are logged and not forwarded. This is to ensure robustness of the
 * framework against failures in 3rd party components.
 * </p>
 * 
 * @author Lars Grammel
 * @author Patrick Gorman
 */
/*
 * TODO introduce ViewItemContainer decorator that is filtered to the valid view
 * items from the error model
 */
public class DefaultVisualizationModel implements VisualizationModel,
        Disposable {

    /**
     * Maps group ids (representing the resource sets that are calculated by the
     * resource grouping, also used as view item ids) to the
     * {@link DefaultVisualItem}s that display the resource sets in the view.
     */
    private Map<String, DefaultVisualItem> viewItemsByGroupId = CollectionFactory
            .createStringMap();

    private SlotMappingConfiguration slotMappingConfiguration;

    private ViewContentDisplay contentDisplay;

    private ResourceGrouping resourceGrouping;

    private ResourceSet selectedResources;

    private ResourceSet highlightedResources;

    private IntersectionResourceSet highlightedResourcesIntersection;

    private final VisualItemBehavior viewItemBehavior;

    private HandlerRegistrationSet handlerRegistrations = new HandlerRegistrationSet();

    private final Logger logger;

    private DefaultVisualItemResolutionErrorModel errorModel = new DefaultVisualItemResolutionErrorModel();

    private transient PrioritizedHandlerManager handlerManager;

    public DefaultVisualizationModel(ViewContentDisplay contentDisplay,
            ResourceSet selectedResources, ResourceSet highlightedResources,
            VisualItemBehavior viewItemBehavior,
            ResourceGrouping resourceGrouping, Logger logger) {

        assert contentDisplay != null;
        assert selectedResources != null;
        assert highlightedResources != null;
        assert viewItemBehavior != null;
        assert resourceGrouping != null;
        assert logger != null;

        this.contentDisplay = contentDisplay;
        this.selectedResources = selectedResources;
        this.highlightedResources = highlightedResources;
        this.viewItemBehavior = viewItemBehavior;
        this.resourceGrouping = resourceGrouping;
        this.logger = logger;

        this.slotMappingConfiguration = new SlotMappingConfiguration(
                contentDisplay.getSlots());
        this.handlerManager = new PrioritizedHandlerManager(this);

        addHandler(viewItemBehavior);

        init(selectedResources);
        initSelectionModelEventHandlers();
        initResourceGrouping();
        initHighlightingModel();
        initContentDisplay();
    }

    @Override
    public HandlerRegistration addHandler(SlotMappingChangedHandler handler) {
        return slotMappingConfiguration.addHandler(handler);
    }

    @Override
    public HandlerRegistration addHandler(
            VisualItemContainerChangeEventHandler handler) {

        assert handler != null;
        return handlerManager.addHandler(VisualItemContainerChangeEvent.TYPE,
                handler);
    }

    private Delta<VisualItem> calculateDeltaThatConsidersErrors(
            Delta<VisualItem> delta,
            LightweightCollection<VisualItem> viewItemsThatHadErrors) {

        LightweightList<VisualItem> addedViewItems = CollectionFactory
                .createLightweightList();
        LightweightList<VisualItem> removedViewItems = CollectionFactory
                .createLightweightList();
        LightweightList<VisualItem> updatedViewItems = CollectionFactory
                .createLightweightList();

        // check if added view items should be added or ignored
        for (VisualItem added : delta.getAddedElements()) {
            boolean hasErrorsNow = errorModel.hasErrors(added);

            if (!hasErrorsNow) {
                addedViewItems.add(added);
            }
        }

        // check if removed resources should to be removed or ignored
        for (VisualItem removed : delta.getRemovedElements()) {
            boolean hadErrorsBefore = viewItemsThatHadErrors.contains(removed);

            if (!hadErrorsBefore) {
                removedViewItems.add(removed);
            }
        }

        // check if updated resources should to be added, removed, updated, or
        // ignored
        for (VisualItem updated : delta.getUpdatedElements()) {
            boolean hasErrorsNow = errorModel.hasErrors(updated);
            boolean hadErrorsBefore = viewItemsThatHadErrors.contains(updated);

            if (!hasErrorsNow && !hadErrorsBefore) {
                updatedViewItems.add(updated);
            } else if (!hasErrorsNow && hadErrorsBefore) {
                addedViewItems.add(updated);
            } else if (!hadErrorsBefore && hasErrorsNow) {
                removedViewItems.add(updated);
            }
        }

        return Delta.createDelta(addedViewItems, updatedViewItems, removedViewItems);
    }

    private void clearViewItemValueCache(Slot slot) {
        for (DefaultVisualItem viewItem : viewItemsByGroupId.values()) {
            viewItem.clearValueCache(slot);
        }
    };

    @Override
    public boolean containsViewItem(String groupId) {
        return viewItemsByGroupId.containsKey(groupId);
    }

    private DefaultVisualItem createViewItem(String groupID,
            ResourceSet resources,
            LightweightCollection<Resource> highlightedResources,
            LightweightCollection<Resource> selectedResources) {

        assert !viewItemsByGroupId.containsKey(groupID) : "viewItemsByGroupId ( "
                + viewItemsByGroupId + " ) already contains " + groupID;

        DefaultVisualItem viewItem = new DefaultVisualItem(groupID, resources,
                slotMappingConfiguration, viewItemBehavior);

        viewItem.updateHighlightedResources(highlightedResources,
                LightweightCollections.<Resource> emptyCollection());

        viewItem.updateSelectedResources(selectedResources,
                LightweightCollections.<Resource> emptyCollection());

        viewItemsByGroupId.put(groupID, viewItem);

        return viewItem;
    }

    @Override
    public void dispose() {
        for (DefaultVisualItem viewItem : viewItemsByGroupId.values()) {
            // fire event that all view items were removed
            fireViewItemContainerChangeEvent(Delta.createDelta(LightweightCollections.<VisualItem> emptyCollection(), LightweightCollections.<VisualItem> emptyCollection(), getViewItems()));

            viewItem.dispose();
        }

        /*
         * XXX Shared objects should not be disposed. Instead, our event
         * handlers should get removed and references should be set to null.
         */

        DisposeUtil.dispose(selectedResources);
        selectedResources = null;

        DisposeUtil.dispose(resourceGrouping);
        resourceGrouping = null;

        contentDisplay.dispose();
        contentDisplay = null;

        highlightedResources = null;

        handlerRegistrations.dispose();
        handlerRegistrations = null;
    }

    private void fireViewItemContainerChangeEvent(Delta<VisualItem> delta) {
        assert delta != null;

        // TODO check that delta does contain actual changes (method on delta)
        handlerManager
                .fireEvent(new VisualItemContainerChangeEvent(this, delta));
    }

    @Override
    public ResourceSet getContentResourceSet() {
        return resourceGrouping.getResourceSet();
    }

    @Override
    public ResourceSet getHighlightedResources() {
        return highlightedResources;
    }

    /**
     * Calculates the intersection with the resources that are displayed in this
     * view.
     * <p>
     * <b>PERFORMANCE</b>: returns ResourceSet to enable fast containment checks
     * in DefaultResourceItem.
     * </p>
     */
    private ResourceSet getIntersectionWithViewResources(
            LightweightCollection<Resource> resources) {

        ResourceSet resourcesInThisView = new DefaultResourceSet();
        resourcesInThisView.addAll(resourceGrouping.getResourceSet()
                .getIntersection(resources));
        return resourcesInThisView;
    }

    @Override
    public ViewItemValueResolver getResolver(Slot slot) {
        return slotMappingConfiguration.getResolver(slot);
    }

    @Override
    public ResourceGrouping getResourceGrouping() {
        return resourceGrouping;
    }

    @Override
    public ResourceSet getSelectedResources() {
        return selectedResources;
    }

    @Override
    public Slot getSlotById(String slotId) {
        return slotMappingConfiguration.getSlotById(slotId);
    }

    @Override
    public Slot[] getSlots() {
        return slotMappingConfiguration.getSlots();
    }

    @Override
    public LightweightCollection<Slot> getSlotsWithErrors() {
        return errorModel.getSlotsWithErrors();
    }

    @Override
    public LightweightCollection<Slot> getSlotsWithErrors(VisualItem viewItem) {
        return errorModel.getSlotsWithErrors(viewItem);
    }

    @Override
    public LightweightCollection<Slot> getUnconfiguredSlots() {
        return slotMappingConfiguration.getUnconfiguredSlots();
    }

    // TODO cache
    private LightweightCollection<VisualItem> getValidViewItems() {
        LightweightList<VisualItem> viewItemsThatWereValid = CollectionFactory
                .createLightweightList();
        for (VisualItem viewItem : getViewItems()) {
            if (!hasErrors(viewItem)) {
                viewItemsThatWereValid.add(viewItem);
            }
        }
        return viewItemsThatWereValid;
    }

    @Override
    public ViewContentDisplay getViewContentDisplay() {
        return contentDisplay;
    }

    @Override
    public VisualItem getViewItem(String viewItemId) {
        return viewItemsByGroupId.get(viewItemId);
    }

    // TODO these view items should be cached & the cache should be updated
    @Override
    public LightweightCollection<VisualItem> getViewItems() {
        return LightweightCollections
                .<VisualItem> toCollection(viewItemsByGroupId.values());
    }

    /**
     * @return List of {@link VisualItem}s that contain at least one of the
     *         {@link Resource}s.
     */
    @Override
    public LightweightList<VisualItem> getViewItems(Iterable<Resource> resources) {
        assert resources != null;

        LightweightList<VisualItem> result = CollectionFactory
                .createLightweightList();
        Set<String> groupIds = resourceGrouping.getGroupIds(resources);
        for (String groupId : groupIds) {
            assert viewItemsByGroupId.containsKey(groupId) : "view item with id "
                    + groupId + " not found";
            result.add(viewItemsByGroupId.get(groupId));
        }
        return result;
    }

    @Override
    public LightweightCollection<VisualItem> getViewItemsWithErrors() {
        return errorModel.getViewItemsWithErrors();
    }

    @Override
    public LightweightCollection<VisualItem> getViewItemsWithErrors(Slot slot) {
        return errorModel.getViewItemsWithErrors(slot);
    }

    @Override
    public boolean hasErrors() {
        return errorModel.hasErrors();
    }

    @Override
    public boolean hasErrors(Slot slot) {
        return errorModel.hasErrors(slot);
    }

    @Override
    public boolean hasErrors(VisualItem viewItem) {
        return errorModel.hasErrors(viewItem);
    }

    private void init(Object target) {
        if (target instanceof Initializable) {
            ((Initializable) target).init();
        }
    }

    private void initContentDisplay() {
        contentDisplay.init(new ViewContentDisplayCallback() {
            @Override
            public HandlerRegistration addHandler(
                    VisualItemContainerChangeEventHandler handler) {

                return null;
            }

            @Override
            public boolean containsViewItem(String viewItemId) {
                return DefaultVisualizationModel.this
                        .containsViewItem(viewItemId)
                        && !hasErrors(viewItemsByGroupId.get(viewItemId));
            }

            @Override
            public ViewItemValueResolver getResolver(Slot slot) {
                return DefaultVisualizationModel.this.getResolver(slot);
            }

            @Override
            public String getSlotResolverDescription(Slot slot) {
                if (!slotMappingConfiguration.isConfigured(slot)) {
                    return "N/A";
                }

                return slotMappingConfiguration.getResolver(slot).toString();
            }

            @Override
            public VisualItem getViewItem(String viewItemId) {

                VisualItem viewItem = DefaultVisualizationModel.this
                        .getViewItem(viewItemId);

                if (viewItem == null) {
                    throw new NoSuchElementException("View Item with id "
                            + viewItemId + " is not contained.");
                }
                if (DefaultVisualizationModel.this.hasErrors(viewItem)) {
                    throw new NoSuchElementException("View Item with id "
                            + viewItemId
                            + " contains errors and cannot be retrieved.");
                }
                return viewItem;
            }

            @Override
            public LightweightCollection<VisualItem> getViewItems() {
                return getValidViewItems();
            }

            @Override
            public LightweightCollection<VisualItem> getViewItems(
                    Iterable<Resource> resources) {
                return LightweightCollections.getRelativeComplement(
                        DefaultVisualizationModel.this.getViewItems(resources),
                        getViewItemsWithErrors());
            }
        });
    }

    /**
     * Creates an intersection of contained resources with highlighted resources
     * and registers for changes.
     */
    private void initHighlightingModel() {
        highlightedResourcesIntersection = new IntersectionResourceSet(
                new DefaultResourceSet());
        highlightedResourcesIntersection
                .addResourceSet(getContentResourceSet());
        highlightedResourcesIntersection.addResourceSet(highlightedResources);

        handlerRegistrations.addHandlerRegistration(highlightedResources
                .addEventHandler(new ResourceSetChangedEventHandler() {
                    @Override
                    public void onResourceSetChanged(
                            ResourceSetChangedEvent event) {
                        updateHighlighting(event.getAddedResources(),
                                event.getRemovedResources());
                    }
                }));
    }

    private void initResourceGrouping() {
        resourceGrouping.addHandler(new ResourceGroupingChangedHandler() {
            @Override
            public void onResourceCategoriesChanged(
                    ResourceGroupingChangedEvent e) {
                processResourceGroupingUpdate(e);
            }
        });
    }

    private void initSelectionModelEventHandlers() {
        handlerRegistrations.addHandlerRegistration(selectedResources
                .addEventHandler(new ResourceSetChangedEventHandler() {
                    @Override
                    public void onResourceSetChanged(
                            ResourceSetChangedEvent event) {
                        updateSelection(event.getAddedResources(),
                                event.getRemovedResources());
                    }
                }));
    }

    @Override
    public boolean isConfigured(Slot slot) {
        return slotMappingConfiguration.isConfigured(slot);
    }

    /**
     * Processes the added resource groups when the grouping changes.
     */
    private LightweightCollection<VisualItem> processAddChanges(
            LightweightList<CategorizableResourceGroupingChange> changes) {

        if (changes.isEmpty()) {
            return LightweightCollections.emptyCollection();
        }

        LightweightList<VisualItem> addedViewItems = CollectionFactory
                .createLightweightList();

        /*
         * PERFORMANCE: cache highlighted resources and use resource set to
         * enable fast containment checks in DefaultResourceItem
         * 
         * TODO refactor: use intersection resource sets instead.
         * 
         * TODO extract
         */
        LightweightList<Resource> highlightedResources = getContentResourceSet()
                .getIntersection(this.highlightedResources);
        LightweightList<Resource> selectedResources = getContentResourceSet()
                .getIntersection(this.selectedResources);

        for (CategorizableResourceGroupingChange change : changes) {
            assert change.getDelta() == DeltaType.GROUP_CREATED;

            addedViewItems.add(createViewItem(change.getGroupID(),
                    change.getResourceSet(), highlightedResources,
                    selectedResources));
        }

        return addedViewItems;
    }

    private LightweightCollection<VisualItem> processRemoveChanges(
            LightweightList<CategorizableResourceGroupingChange> changes) {

        if (changes.isEmpty()) {
            return LightweightCollections.emptyCollection();
        }

        LightweightList<VisualItem> removedViewItems = CollectionFactory
                .createLightweightList();
        for (CategorizableResourceGroupingChange change : changes) {
            assert change.getDelta() == DeltaType.GROUP_REMOVED;

            // XXX dispose should be done after method call...
            removedViewItems.add(removeViewItem(change.getGroupID()));
        }
        return removedViewItems;
    }

    private void processResourceGroupingUpdate(
            ResourceGroupingChangedEvent event) {

        assert event != null;

        Delta<VisualItem> delta = updateViewItemsOnModelChange(event);
        // create a copy of the view items with errors
        LightweightCollection<VisualItem> viewItemsThatHadErrors = LightweightCollections
                .copy(errorModel.getViewItemsWithErrors());
        updateErrorModel(delta);
        Delta<VisualItem> deltaThatConsidersErrors = calculateDeltaThatConsidersErrors(
                delta, viewItemsThatHadErrors);
        updateViewContentDisplay(deltaThatConsidersErrors,
                LightweightCollections.<Slot> emptyCollection());
        fireViewItemContainerChangeEvent(delta);
    }

    private LightweightCollection<VisualItem> processUpdates(
            LightweightList<CategorizableResourceGroupingChange> changes) {

        if (changes.isEmpty()) {
            return LightweightCollections.emptyCollection();
        }

        LightweightList<VisualItem> updatedViewItems = CollectionFactory
                .createLightweightList();
        for (CategorizableResourceGroupingChange change : changes) {
            assert change.getDelta() == DeltaType.GROUP_CHANGED;
            DefaultVisualItem viewItem = viewItemsByGroupId.get(change
                    .getGroupID());

            updateViewItemHighlightingSet(viewItem, change);
            updateViewItemSelectionSet(change, viewItem);

            updatedViewItems.add(viewItem);
        }

        return updatedViewItems;
    }

    private DefaultVisualItem removeViewItem(String groupID) {
        assert groupID != null : "groupIDs must not be null";
        assert viewItemsByGroupId.containsKey(groupID) : "no view item for "
                + groupID;

        DefaultVisualItem viewItem = viewItemsByGroupId.remove(groupID);
        viewItem.dispose();

        assert !viewItemsByGroupId.containsKey(groupID);

        return viewItem;
    }

    /*
     * IMPLEMENTATION NOTE: We updated the error model and view content display
     * directly after the resolver has been set, because this keeps the
     * algorithm simpler (compared to an event based approach, which would
     * require prioritizing event handlers etc.)
     */
    @Override
    public void setResolver(Slot slot, ViewItemValueResolver resolver) {
        assert slot != null;
        assert resolver != null;

        // keep information (currently valid / invalid stuff)
        LightweightCollection<VisualItem> viewItemsThatHadErrors = LightweightCollections
                .copy(getViewItemsWithErrors());
        LightweightCollection<VisualItem> viewItemsThatWereValid = LightweightCollections
                .copy(getValidViewItems());

        clearViewItemValueCache(slot);

        // actually change the slot mapping
        slotMappingConfiguration.setResolver(slot, resolver);

        updateErrorModel(slot);

        // CASE 1: stuff gets removed
        // valid --> invalid (for view items)
        LightweightCollection<VisualItem> viewItemsToAdd = LightweightCollections
                .getRelativeComplement(viewItemsThatHadErrors,
                        getViewItemsWithErrors());
        // CASE 2: stuff gets added
        // invalid --> valid (for view items)
        LightweightCollection<VisualItem> viewItemsToRemove = LightweightCollections
                .getRelativeComplement(viewItemsThatWereValid,
                        getValidViewItems());

        updateViewContentDisplay(Delta.createDelta(viewItemsToAdd, LightweightCollections.<VisualItem> emptyCollection(), viewItemsToRemove), LightweightCollections.toCollection(slot));
    }

    @Override
    public void setResourceGrouping(ResourceGrouping resourceGrouping) {
        // TODO Auto-generated method stub
        // XXX test --> remove handler, add handler, change grouping, update
        // view items(remove,add)
    }

    private void updateErrorModel(Delta<VisualItem> delta) {
        assert delta != null;

        updateErrorModel(delta.getAddedElements());
        updateErrorModel(delta.getUpdatedElements());
        errorModel.clearErrors(delta.getRemovedElements());
    }

    private void updateErrorModel(LightweightCollection<VisualItem> viewItems) {
        assert viewItems != null;

        errorModel.clearErrors(viewItems);
        for (Slot slot : getSlots()) {
            updateErrorModel(slot, viewItems);
        }
    }

    private void updateErrorModel(Slot changedSlot) {
        assert changedSlot != null;

        errorModel.clearErrors(changedSlot);
        updateErrorModel(changedSlot, getViewItems());
    }

    private void updateErrorModel(Slot slot,
            LightweightCollection<VisualItem> viewItems) {

        if (!slotMappingConfiguration.isConfigured(slot)) {
            /*
             * TODO potential optimization: have all invalid state for slot in
             * error model (would return errors for all view items)
             */
            errorModel.reportErrors(slot, viewItems);
            return;
        }

        ViewItemValueResolver resolver = slotMappingConfiguration
                .getResolver(slot);

        for (VisualItem viewItem : viewItems) {
            if (!resolver.canResolve(viewItem, this)) {
                /*
                 * TODO potential optimization: only change error model if state
                 * for view item has changed (delta update).
                 */
                errorModel.reportError(slot, viewItem);
            }
        }
    }

    private void updateHighlighting(
            LightweightCollection<Resource> addedResources,
            LightweightCollection<Resource> removedResources) {

        assert addedResources != null;
        assert removedResources != null;

        ResourceSet addedResourcesInThisView = getIntersectionWithViewResources(addedResources);
        ResourceSet removedResourcesInThisView = getIntersectionWithViewResources(removedResources);

        if (addedResourcesInThisView.isEmpty()
                && removedResourcesInThisView.isEmpty()) {
            return;
        }

        LightweightList<VisualItem> affectedViewItems = getViewItems(new CombinedIterable<Resource>(
                addedResources, removedResources));

        for (VisualItem viewItem : affectedViewItems) {
            ((DefaultVisualItem) viewItem).updateHighlightedResources(
                    addedResourcesInThisView, removedResourcesInThisView);
        }

        updateViewContentDisplay(
                Delta.createDelta(LightweightCollections.<VisualItem> emptyCollection(), affectedViewItems, LightweightCollections.<VisualItem> emptyCollection()),
                LightweightCollections.<Slot> emptyCollection());
    }

    /*
     * TODO refactor: updateHighlighting is similar (and the whole highlighting
     * vs selection in ViewItem)
     */
    private void updateSelection(
            LightweightCollection<Resource> addedResources,
            LightweightCollection<Resource> removedResources) {

        ResourceSet addedResourcesInThisView = getIntersectionWithViewResources(addedResources);
        ResourceSet removedResourcesInThisView = getIntersectionWithViewResources(removedResources);

        if (addedResourcesInThisView.isEmpty()
                && removedResourcesInThisView.isEmpty()) {
            return;
        }

        LightweightList<VisualItem> affectedViewItems = getViewItems(new CombinedIterable<Resource>(
                addedResources, removedResources));

        for (VisualItem viewItem : affectedViewItems) {
            ((DefaultVisualItem) viewItem).updateSelectedResources(
                    addedResourcesInThisView, removedResourcesInThisView);
        }

        updateViewContentDisplay(
                Delta.createDelta(LightweightCollections.<VisualItem> emptyCollection(), affectedViewItems, LightweightCollections.<VisualItem> emptyCollection()),
                LightweightCollections.<Slot> emptyCollection());
    }

    /**
     * NOTE: Exceptions are logged and not thrown to ensure robustness.
     */
    private void updateViewContentDisplay(Delta<VisualItem> delta,
            LightweightCollection<Slot> changedSlots) {

        if (delta.isEmpty() && changedSlots.isEmpty()) {
            return;
        }

        try {
            // TODO switch to delta in view content display interface
            contentDisplay.update(delta, changedSlots);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "ViewContentDisplay.update failed", ex);
        }
    }

    private void updateViewItemHighlightingSet(DefaultVisualItem viewItem,
            CategorizableResourceGroupingChange change) {

        LightweightList<Resource> highlightedAdded = highlightedResources
                .getIntersection(change.getAddedResources());
        LightweightList<Resource> highlightedRemoved = highlightedResources
                .getIntersection(change.getRemovedResources());

        if (!highlightedAdded.isEmpty() || !highlightedRemoved.isEmpty()) {
            viewItem.updateHighlightedResources(highlightedAdded,
                    highlightedRemoved);
        }
    }

    private void updateViewItemSelectionSet(
            CategorizableResourceGroupingChange change,
            DefaultVisualItem viewItem) {

        LightweightList<Resource> selectedAdded = selectedResources
                .getIntersection(change.getAddedResources());
        LightweightList<Resource> selectedRemoved = selectedResources
                .getIntersection(change.getRemovedResources());

        if (!selectedAdded.isEmpty() || !selectedRemoved.isEmpty()) {
            viewItem.updateSelectedResources(selectedAdded, selectedRemoved);
        }
    }

    private Delta<VisualItem> updateViewItemsOnModelChange(
            ResourceGroupingChangedEvent event) {

        assert event != null;

        /*
         * IMPORTANT: remove old items before adding new once (there might be
         * conflicts, i.e. groups with the same id)
         */
        LightweightCollection<VisualItem> removedViewItems = processRemoveChanges(event
                .getChanges(DeltaType.GROUP_REMOVED));
        LightweightCollection<VisualItem> addedViewItems = processAddChanges(event
                .getChanges(DeltaType.GROUP_CREATED));
        LightweightCollection<VisualItem> updatedViewItems = processUpdates(event
                .getChanges(DeltaType.GROUP_CHANGED));

        return Delta.createDelta(addedViewItems, updatedViewItems, removedViewItems);
    }

}