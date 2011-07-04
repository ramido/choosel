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
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.thechiselgroup.choosel.core.client.resources.CategorizableResourceGroupingChange;
import org.thechiselgroup.choosel.core.client.resources.CategorizableResourceGroupingChange.Delta;
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
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollections;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightList;
import org.thechiselgroup.choosel.core.client.views.resolvers.ViewItemValueResolver;
import org.thechiselgroup.choosel.core.shared.util.ForTest;

import com.google.gwt.event.shared.HandlerManager;
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
public class DefaultViewModel implements ViewModel, Disposable,
        ViewContentDisplayCallback {

    /**
     * Maps group ids (representing the resource sets that are calculated by the
     * resource grouping, also used as view item ids) to the
     * {@link DefaultViewItem}s that display the resource sets in the view.
     */
    private Map<String, DefaultViewItem> viewItemsByGroupId = CollectionFactory
            .createStringMap();

    private SlotMappingConfiguration slotMappingConfiguration;

    private ViewContentDisplay contentDisplay;

    private ResourceGrouping resourceGrouping;

    private ResourceSet selectedResources;

    private ResourceSet highlightedResources;

    private IntersectionResourceSet highlightedResourcesIntersection;

    private final ViewItemBehavior viewItemBehavior;

    private HandlerRegistrationSet handlerRegistrations = new HandlerRegistrationSet();

    private final Logger logger;

    private DefaultViewItemResolutionErrorModel errorModel = new DefaultViewItemResolutionErrorModel();

    private transient HandlerManager handlerManager;

    public DefaultViewModel(ViewContentDisplay contentDisplay,
            ResourceSet selectedResources, ResourceSet highlightedResources,
            ViewItemBehavior viewItemBehavior,
            ResourceGrouping resourceGrouping, Logger logger) {

        this(contentDisplay, new SlotMappingConfiguration(
                contentDisplay.getSlots()), selectedResources,
                highlightedResources, viewItemBehavior, resourceGrouping,
                logger);
    }

    @ForTest
    public DefaultViewModel(ViewContentDisplay contentDisplay,
            SlotMappingConfiguration slotMappingConfiguration,
            ResourceSet selectedResources, ResourceSet highlightedResources,
            ViewItemBehavior viewItemBehavior,
            ResourceGrouping resourceGrouping, Logger logger) {

        assert slotMappingConfiguration != null;
        assert contentDisplay != null;
        assert selectedResources != null;
        assert highlightedResources != null;
        assert viewItemBehavior != null;
        assert resourceGrouping != null;
        assert logger != null;

        this.slotMappingConfiguration = slotMappingConfiguration;
        this.contentDisplay = contentDisplay;
        this.selectedResources = selectedResources;
        this.highlightedResources = highlightedResources;
        this.viewItemBehavior = viewItemBehavior;
        this.resourceGrouping = resourceGrouping;
        this.logger = logger;

        this.handlerManager = new HandlerManager(this);

        init(selectedResources);
        initSelectionModelEventHandlers();
        initResourceGrouping();
        initHighlightingModel();
        initContentDisplay();
        initSlotMappingChangeHandler();
    }

    @Override
    public HandlerRegistration addHandler(SlotMappingChangedHandler handler) {
        return slotMappingConfiguration.addHandler(handler);
    }

    @Override
    public HandlerRegistration addHandler(
            ViewItemContainerChangeEventHandler handler) {

        assert handler != null;
        return handlerManager.addHandler(ViewItemContainerChangeEvent.TYPE,
                handler);
    }

    private ViewItemContainerDelta calculateDeltaThatConsidersErrors(
            ViewItemContainerDelta delta,
            LightweightCollection<ViewItem> viewItemsThatHadErrors) {

        LightweightList<ViewItem> addedViewItems = CollectionFactory
                .createLightweightList();
        LightweightList<ViewItem> removedViewItems = CollectionFactory
                .createLightweightList();
        LightweightList<ViewItem> updatedViewItems = CollectionFactory
                .createLightweightList();

        // check if added view items should be added or ignored
        for (ViewItem added : delta.getAddedViewItems()) {
            boolean hasErrorsNow = errorModel.hasErrors(added);

            if (!hasErrorsNow) {
                addedViewItems.add(added);
            }
        }

        // check if removed resources should to be removed or ignored
        for (ViewItem removed : delta.getRemovedViewItems()) {
            boolean hadErrorsBefore = viewItemsThatHadErrors.contains(removed);

            if (!hadErrorsBefore) {
                removedViewItems.add(removed);
            }
        }

        // check if updated resources should to be added, removed, updated, or
        // ignored
        for (ViewItem updated : delta.getUpdatedViewItems()) {
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

        return new ViewItemContainerDelta(addedViewItems, updatedViewItems,
                removedViewItems);
    }

    @Override
    public boolean containsViewItem(String groupId) {
        return viewItemsByGroupId.containsKey(groupId);
    }

    private DefaultViewItem createViewItem(String groupID,
            ResourceSet resources,
            LightweightCollection<Resource> highlightedResources,
            LightweightCollection<Resource> selectedResources) {

        assert !viewItemsByGroupId.containsKey(groupID) : "viewItemsByGroupId ( "
                + viewItemsByGroupId + " ) already contains " + groupID;

        DefaultViewItem viewItem = new DefaultViewItem(groupID, resources,
                slotMappingConfiguration, viewItemBehavior);

        viewItemBehavior.onViewItemCreated(viewItem);

        viewItem.updateHighlightedResources(highlightedResources,
                LightweightCollections.<Resource> emptyCollection());

        viewItem.updateSelectedResources(selectedResources,
                LightweightCollections.<Resource> emptyCollection());

        viewItemsByGroupId.put(groupID, viewItem);

        return viewItem;
    };

    @Override
    public void dispose() {
        for (DefaultViewItem viewItem : viewItemsByGroupId.values()) {
            viewItemBehavior.onViewItemRemoved(viewItem);
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

    private void fireViewItemContainerChangeEvent(ViewItemContainerDelta delta) {
        assert delta != null;

        // TODO check that delta does contain actual changes (method on delta)
        handlerManager.fireEvent(new ViewItemContainerChangeEvent(this, delta));
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
    public String getSlotResolverDescription(Slot slot) {
        if (!slotMappingConfiguration.isConfigured(slot)) {
            return "N/A";
        }

        return slotMappingConfiguration.getResolver(slot).toString();
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
    public LightweightCollection<Slot> getSlotsWithErrors(ViewItem viewItem) {
        return errorModel.getSlotsWithErrors(viewItem);
    }

    @Override
    public LightweightCollection<Slot> getUnconfiguredSlots() {
        return slotMappingConfiguration.getUnconfiguredSlots();
    }

    @Override
    public ViewContentDisplay getViewContentDisplay() {
        return contentDisplay;
    }

    @Override
    public ViewItem getViewItem(String viewItemId) {
        return viewItemsByGroupId.get(viewItemId);
    }

    // TODO these view items should be cached & the cache should be updated
    @Override
    public LightweightCollection<ViewItem> getViewItems() {
        return LightweightCollections
                .<ViewItem> toCollection(viewItemsByGroupId.values());
    }

    /**
     * @return List of {@link ViewItem}s that contain at least one of the
     *         {@link Resource}s.
     */
    @Override
    public LightweightList<ViewItem> getViewItems(Iterable<Resource> resources) {
        assert resources != null;

        LightweightList<ViewItem> result = CollectionFactory
                .createLightweightList();
        Set<String> groups = resourceGrouping.getGroups(resources);
        for (String group : groups) {
            assert viewItemsByGroupId.containsKey(group);
            result.add(viewItemsByGroupId.get(group));
        }
        return result;
    }

    @Override
    public LightweightCollection<ViewItem> getViewItemsWithErrors() {
        return errorModel.getViewItemsWithErrors();
    }

    @Override
    public LightweightCollection<ViewItem> getViewItemsWithErrors(Slot slot) {
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
    public boolean hasErrors(ViewItem viewItem) {
        return errorModel.hasErrors(viewItem);
    }

    private void init(Object target) {
        if (target instanceof Initializable) {
            ((Initializable) target).init();
        }
    }

    private void initContentDisplay() {
        contentDisplay.init(this);
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

    protected void initSlotMappingChangeHandler() {
        slotMappingConfiguration.addHandler(new SlotMappingChangedHandler() {
            @Override
            public void onSlotMappingChanged(SlotMappingChangedEvent event) {
                Slot slot = event.getSlot();
                // TODO process to get the right delta...
                // --> stuff might get added because its fixed by the slot
                // mapping change

                /*
                 * XXX this might be a problem - we need priorities to make sure
                 * this is called before other things (error model needs to be
                 * updated), but after view item cache is cleared. ==> default
                 * view items should not listen for slot changes, their cache
                 * should be cleared from here, and this handler should have
                 * high priority.
                 */
                updateErrorModel(slot);

                // TODO extract
                for (DefaultViewItem viewItem : viewItemsByGroupId.values()) {
                    viewItem.clearValueCache(slot);
                }

                updateViewContentDisplay(new ViewItemContainerDelta(
                        LightweightCollections.<ViewItem> emptyCollection(),
                        LightweightCollections.<ViewItem> emptyCollection(),
                        LightweightCollections.<ViewItem> emptyCollection()),
                        LightweightCollections.toCollection(slot));
            }
        });
    }

    @Override
    public boolean isConfigured(Slot slot) {
        return slotMappingConfiguration.isConfigured(slot);
    }

    /**
     * Processes the added resource groups when the grouping changes.
     */
    private LightweightCollection<ViewItem> processAddChanges(
            LightweightList<CategorizableResourceGroupingChange> changes) {

        if (changes.isEmpty()) {
            return LightweightCollections.emptyCollection();
        }

        LightweightList<ViewItem> addedViewItems = CollectionFactory
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
            assert change.getDelta() == Delta.GROUP_CREATED;

            addedViewItems.add(createViewItem(change.getGroupID(),
                    change.getResourceSet(), highlightedResources,
                    selectedResources));
        }

        return addedViewItems;
    }

    private LightweightCollection<ViewItem> processRemoveChanges(
            LightweightList<CategorizableResourceGroupingChange> changes) {

        if (changes.isEmpty()) {
            return LightweightCollections.emptyCollection();
        }

        LightweightList<ViewItem> removedViewItems = CollectionFactory
                .createLightweightList();
        for (CategorizableResourceGroupingChange change : changes) {
            assert change.getDelta() == Delta.GROUP_REMOVED;

            // XXX dispose should be done after method call...
            removedViewItems.add(removeViewItem(change.getGroupID()));
        }
        return removedViewItems;
    }

    private void processResourceGroupingUpdate(
            ResourceGroupingChangedEvent event) {
        assert event != null;

        ViewItemContainerDelta delta = updateViewItemsOnModelChange(event);
        // create a copy of the view items with errors
        LightweightCollection<ViewItem> viewItemsThatHadErrors = LightweightCollections
                .toCollection(errorModel.getViewItemsWithErrors());
        updateErrorModel(delta);
        ViewItemContainerDelta deltaThatConsidersErrors = calculateDeltaThatConsidersErrors(
                delta, viewItemsThatHadErrors);
        updateViewContentDisplay(deltaThatConsidersErrors,
                LightweightCollections.<Slot> emptyCollection());
        fireViewItemContainerChangeEvent(delta);
    }

    private LightweightCollection<ViewItem> processUpdates(
            LightweightList<CategorizableResourceGroupingChange> changes) {

        if (changes.isEmpty()) {
            return LightweightCollections.emptyCollection();
        }

        LightweightList<ViewItem> updatedViewItems = CollectionFactory
                .createLightweightList();
        for (CategorizableResourceGroupingChange change : changes) {
            assert change.getDelta() == Delta.GROUP_CHANGED;
            DefaultViewItem viewItem = viewItemsByGroupId.get(change
                    .getGroupID());

            updateViewItemHighlightingSet(viewItem, change);
            updateViewItemSelectionSet(change, viewItem);

            updatedViewItems.add(viewItem);
        }

        return updatedViewItems;
    }

    private DefaultViewItem removeViewItem(String groupID) {
        assert groupID != null : "groupIDs must not be null";
        assert viewItemsByGroupId.containsKey(groupID) : "no view item for "
                + groupID;

        DefaultViewItem viewItem = viewItemsByGroupId.remove(groupID);
        viewItemBehavior.onViewItemRemoved(viewItem);
        viewItem.dispose();

        assert !viewItemsByGroupId.containsKey(groupID);

        return viewItem;
    }

    @Override
    public void setResolver(Slot slot, ViewItemValueResolver resolver) {
        slotMappingConfiguration.setResolver(slot, resolver);
    }

    @Override
    public void setResourceGrouping(ResourceGrouping resourceGrouping) {
        // TODO Auto-generated method stub
        // XXX test --> remove handler, add handler, change grouping, update
        // view items(remove,add)
    }

    private void updateErrorModel(LightweightCollection<ViewItem> viewItems) {
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
            LightweightCollection<ViewItem> viewItems) {

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

        for (ViewItem viewItem : viewItems) {
            if (!resolver.canResolve(viewItem, this)) {
                /*
                 * TODO potential optimization: only change error model if state
                 * for view item has changed (delta update).
                 */
                errorModel.reportError(slot, viewItem);
            }
        }
    }

    private void updateErrorModel(ViewItemContainerDelta delta) {
        assert delta != null;

        updateErrorModel(delta.getAddedViewItems());
        updateErrorModel(delta.getUpdatedViewItems());
        errorModel.clearErrors(delta.getRemovedViewItems());
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

        LightweightList<ViewItem> affectedViewItems = getViewItems(new CombinedIterable<Resource>(
                addedResources, removedResources));

        for (ViewItem viewItem : affectedViewItems) {
            ((DefaultViewItem) viewItem).updateHighlightedResources(
                    addedResourcesInThisView, removedResourcesInThisView);
        }

        updateViewContentDisplay(
                new ViewItemContainerDelta(
                        LightweightCollections.<ViewItem> emptyCollection(),
                        affectedViewItems,
                        LightweightCollections.<ViewItem> emptyCollection()),
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

        LightweightList<ViewItem> affectedViewItems = getViewItems(new CombinedIterable<Resource>(
                addedResources, removedResources));

        for (ViewItem viewItem : affectedViewItems) {
            ((DefaultViewItem) viewItem).updateSelectedResources(
                    addedResourcesInThisView, removedResourcesInThisView);
        }

        updateViewContentDisplay(
                new ViewItemContainerDelta(
                        LightweightCollections.<ViewItem> emptyCollection(),
                        affectedViewItems,
                        LightweightCollections.<ViewItem> emptyCollection()),
                LightweightCollections.<Slot> emptyCollection());
    }

    /**
     * NOTE: Exceptions are logged and not thrown to ensure robustness.
     */
    private void updateViewContentDisplay(ViewItemContainerDelta delta,
            LightweightCollection<Slot> changedSlots) {

        if (delta.isEmpty() && changedSlots.isEmpty()) {
            return;
        }

        try {
            // TODO switch to delta in view content display interface
            contentDisplay.update(delta.getAddedViewItems(),
                    delta.getUpdatedViewItems(), delta.getRemovedViewItems(),
                    changedSlots);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "ViewContentDisplay.update failed", ex);
        }
    }

    private void updateViewItemHighlightingSet(DefaultViewItem viewItem,
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
            CategorizableResourceGroupingChange change, DefaultViewItem viewItem) {

        LightweightList<Resource> selectedAdded = selectedResources
                .getIntersection(change.getAddedResources());
        LightweightList<Resource> selectedRemoved = selectedResources
                .getIntersection(change.getRemovedResources());

        if (!selectedAdded.isEmpty() || !selectedRemoved.isEmpty()) {
            viewItem.updateSelectedResources(selectedAdded, selectedRemoved);
        }
    }

    private ViewItemContainerDelta updateViewItemsOnModelChange(
            ResourceGroupingChangedEvent event) {

        assert event != null;

        /*
         * IMPORTANT: remove old items before adding new once (there might be
         * conflicts, i.e. groups with the same id)
         */
        LightweightCollection<ViewItem> removedViewItems = processRemoveChanges(event
                .getChanges(Delta.GROUP_REMOVED));
        LightweightCollection<ViewItem> addedViewItems = processAddChanges(event
                .getChanges(Delta.GROUP_CREATED));
        LightweightCollection<ViewItem> updatedViewItems = processUpdates(event
                .getChanges(Delta.GROUP_CHANGED));

        return new ViewItemContainerDelta(addedViewItems, updatedViewItems,
                removedViewItems);
    }

}