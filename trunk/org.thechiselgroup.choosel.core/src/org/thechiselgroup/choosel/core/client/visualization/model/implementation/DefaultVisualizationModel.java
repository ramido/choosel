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

import static org.thechiselgroup.choosel.core.client.util.DisposeUtil.safelyDispose;
import static org.thechiselgroup.choosel.core.client.util.collections.Delta.*;
import static org.thechiselgroup.choosel.core.client.util.collections.LightweightCollections.*;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.thechiselgroup.choosel.core.client.error_handling.ErrorHandler;
import org.thechiselgroup.choosel.core.client.resources.CategorizableResourceGroupingChange;
import org.thechiselgroup.choosel.core.client.resources.CategorizableResourceGroupingChange.ChangeType;
import org.thechiselgroup.choosel.core.client.resources.DefaultResourceSet;
import org.thechiselgroup.choosel.core.client.resources.IntersectionResourceSet;
import org.thechiselgroup.choosel.core.client.resources.Resource;
import org.thechiselgroup.choosel.core.client.resources.ResourceGrouping;
import org.thechiselgroup.choosel.core.client.resources.ResourceGroupingChangedEvent;
import org.thechiselgroup.choosel.core.client.resources.ResourceGroupingChangedHandler;
import org.thechiselgroup.choosel.core.client.resources.ResourceMultiCategorizer;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.resources.ResourceSetChangedEvent;
import org.thechiselgroup.choosel.core.client.resources.ResourceSetChangedEventHandler;
import org.thechiselgroup.choosel.core.client.resources.ResourceSetFactory;
import org.thechiselgroup.choosel.core.client.util.Disposable;
import org.thechiselgroup.choosel.core.client.util.HandlerRegistrationSet;
import org.thechiselgroup.choosel.core.client.util.Initializable;
import org.thechiselgroup.choosel.core.client.util.collections.CollectionFactory;
import org.thechiselgroup.choosel.core.client.util.collections.CombinedIterable;
import org.thechiselgroup.choosel.core.client.util.collections.Delta;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollections;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightList;
import org.thechiselgroup.choosel.core.client.util.event.PrioritizedHandlerManager;
import org.thechiselgroup.choosel.core.client.visualization.model.Slot;
import org.thechiselgroup.choosel.core.client.visualization.model.SlotMappingChangedHandler;
import org.thechiselgroup.choosel.core.client.visualization.model.ViewContentDisplay;
import org.thechiselgroup.choosel.core.client.visualization.model.ViewContentDisplayCallback;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItem;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItem.Subset;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItemBehavior;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItemContainerChangeEvent;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItemContainerChangeEventHandler;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItemValueResolver;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualizationModel;

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
 * TODO introduce VisualItemContainer decorator that is filtered to the valid
 * view items from the error model
 * 
 * TODO remove highlighted / selected code duplication (similar to
 * DefaultVisualItem)
 */
public class DefaultVisualizationModel implements VisualizationModel,
        Disposable {

    /**
     * Maps group ids (representing the resource sets that are calculated by the
     * resource grouping, also used as view item ids) to the
     * {@link DefaultVisualItem}s that display the resource sets in the view.
     */
    private Map<String, DefaultVisualItem> visualItemsByGroupId = CollectionFactory
            .createStringMap();

    private DefaultSlotMappingConfiguration slotMappingConfiguration;

    private ViewContentDisplay contentDisplay;

    private ResourceGrouping resourceGrouping;

    private ResourceSet selectedResources;

    private ResourceSet highlightedResources;

    private IntersectionResourceSet highlightedResourcesIntersection;

    private final VisualItemBehavior visualItemBehavior;

    private HandlerRegistrationSet handlerRegistrations = new HandlerRegistrationSet();

    private final ErrorHandler errorHandler;

    private DefaultVisualItemResolutionErrorModel errorModel = new DefaultVisualItemResolutionErrorModel();

    private transient PrioritizedHandlerManager handlerManager;

    /**
     * @param errorHandler
     *            Exceptions that occur in other modules, e.g.
     *            {@link ViewContentDisplay}s or
     *            {@link VisualItemContainerChangeEventHandler}s are caught and
     *            reported to this {@link ErrorHandler}.
     */
    public DefaultVisualizationModel(ViewContentDisplay contentDisplay,
            ResourceSet selectedResources, ResourceSet highlightedResources,
            VisualItemBehavior visualItemBehavior, ErrorHandler errorHandler,
            ResourceSetFactory resourceSetFactory,
            ResourceMultiCategorizer multiCategorizer) {

        assert contentDisplay != null;
        assert selectedResources != null;
        assert highlightedResources != null;
        assert visualItemBehavior != null;
        assert errorHandler != null;
        assert resourceSetFactory != null;
        assert multiCategorizer != null;

        this.contentDisplay = contentDisplay;
        this.selectedResources = selectedResources;
        this.highlightedResources = highlightedResources;
        this.visualItemBehavior = visualItemBehavior;
        this.errorHandler = errorHandler;

        this.resourceGrouping = new ResourceGrouping(multiCategorizer,
                resourceSetFactory);
        this.slotMappingConfiguration = new DefaultSlotMappingConfiguration(
                contentDisplay.getSlots());
        this.handlerManager = new PrioritizedHandlerManager(this);

        // TODO should be external...
        addHandler(visualItemBehavior);

        init(selectedResources);
        initSelectionModelEventHandlers();
        initResourceGrouping();
        initHighlightingModel();
        initContentDisplay();

        assert getContentResourceSet().isEmpty();
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

    private void assertWithoutErrors(
            LightweightCollection<VisualItem> visualItems) {

        for (VisualItem visualItem : visualItems) {
            assert !hasErrors(visualItem) : visualItem + " has errors";
        }
    }

    private Delta<VisualItem> calculateDeltaThatConsidersErrors(
            Delta<VisualItem> delta,
            LightweightCollection<VisualItem> visualItemsThatHadErrors) {

        LightweightList<VisualItem> addedVisualItems = CollectionFactory
                .createLightweightList();
        LightweightList<VisualItem> removedVisualItems = CollectionFactory
                .createLightweightList();
        LightweightList<VisualItem> updatedVisualItems = CollectionFactory
                .createLightweightList();

        // check if added view items should be added or ignored
        for (VisualItem added : delta.getAddedElements()) {
            boolean hasErrorsNow = errorModel.hasErrors(added);

            if (!hasErrorsNow) {
                addedVisualItems.add(added);
            }
        }

        // check if removed resources should to be removed or ignored
        for (VisualItem removed : delta.getRemovedElements()) {
            boolean hadErrorsBefore = visualItemsThatHadErrors
                    .contains(removed);

            if (!hadErrorsBefore) {
                removedVisualItems.add(removed);
            }
        }

        // check if updated resources should to be added, removed, updated, or
        // ignored
        for (VisualItem updated : delta.getUpdatedElements()) {
            boolean hasErrorsNow = errorModel.hasErrors(updated);
            boolean hadErrorsBefore = visualItemsThatHadErrors
                    .contains(updated);

            if (!hasErrorsNow && !hadErrorsBefore) {
                updatedVisualItems.add(updated);
            } else if (!hasErrorsNow && hadErrorsBefore) {
                addedVisualItems.add(updated);
            } else if (!hadErrorsBefore && hasErrorsNow) {
                removedVisualItems.add(updated);
            }
        }

        return createDelta(addedVisualItems, updatedVisualItems,
                removedVisualItems);
    }

    private void clearVisualItemValueCache(Slot slot) {
        for (DefaultVisualItem visualItem : visualItemsByGroupId.values()) {
            visualItem.clearValueCache(slot);
        }
    };

    @Override
    public boolean containsVisualItem(String groupId) {
        return visualItemsByGroupId.containsKey(groupId);
    }

    private DefaultVisualItem createVisualItem(String groupID,
            ResourceSet resources,
            LightweightCollection<Resource> highlightedResources,
            LightweightCollection<Resource> selectedResources) {

        assert !visualItemsByGroupId.containsKey(groupID) : "visualItemsByGroupId ( "
                + visualItemsByGroupId + " ) already contains " + groupID;

        DefaultVisualItem visualItem = new DefaultVisualItem(groupID,
                resources, slotMappingConfiguration, visualItemBehavior);

        visualItem.updateSubset(Subset.HIGHLIGHTED, highlightedResources,
                LightweightCollections.<Resource> emptyCollection());

        visualItem.updateSubset(Subset.SELECTED, selectedResources,
                LightweightCollections.<Resource> emptyCollection());

        visualItemsByGroupId.put(groupID, visualItem);

        return visualItem;
    }

    @Override
    public void dispose() {
        for (DefaultVisualItem visualItem : visualItemsByGroupId.values()) {
            // fire event that all view items were removed
            fireVisualItemContainerChangeEvent(Delta.createDelta(
                    LightweightCollections.<VisualItem> emptyCollection(),
                    LightweightCollections.<VisualItem> emptyCollection(),
                    getVisualItems()));

            safelyDispose(visualItem, errorHandler);
        }

        /*
         * XXX Shared objects should not be disposed. Instead, our event
         * handlers should get removed and references should be set to null.
         */

        selectedResources = safelyDispose(selectedResources, errorHandler);
        resourceGrouping = safelyDispose(resourceGrouping, errorHandler);
        contentDisplay = safelyDispose(contentDisplay, errorHandler);

        highlightedResources = null;

        handlerRegistrations = safelyDispose(handlerRegistrations, errorHandler);
    }

    private void fireVisualItemContainerChangeEvent(Delta<VisualItem> delta) {
        assert delta != null;

        if (delta.isEmpty()) {
            return;
        }

        try {
            handlerManager.fireEvent(new VisualItemContainerChangeEvent(this,
                    delta));
        } catch (Throwable ex) {
            errorHandler.handleError(ex);
        }
    }

    @Override
    public ResourceMultiCategorizer getCategorizer() {
        return resourceGrouping.getCategorizer();
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
    private ResourceSet getIntersectionWithVisualizationResources(
            LightweightCollection<Resource> resources) {

        ResourceSet resourcesInThisView = new DefaultResourceSet();
        resourcesInThisView.addAll(resourceGrouping.getResourceSet()
                .getIntersection(resources));
        return resourcesInThisView;
    }

    @Override
    public VisualItemValueResolver getResolver(Slot slot) {
        return slotMappingConfiguration.getResolver(slot);
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
    public LightweightCollection<Slot> getSlotsWithErrors(VisualItem visualItem) {
        return errorModel.getSlotsWithErrors(visualItem);
    }

    @Override
    public LightweightCollection<Slot> getUnconfiguredSlots() {
        return slotMappingConfiguration.getUnconfiguredSlots();
    }

    // TODO cache
    private LightweightCollection<VisualItem> getValidVisualItems() {
        LightweightList<VisualItem> visualItemsThatWereValid = CollectionFactory
                .createLightweightList();
        for (VisualItem visualItem : getVisualItems()) {
            if (!hasErrors(visualItem)) {
                visualItemsThatWereValid.add(visualItem);
            }
        }
        return visualItemsThatWereValid;
    }

    private LightweightCollection<VisualItem> getValidVisualItems(
            Iterable<Resource> resources) {

        return getRelativeComplement(getVisualItems(resources),
                getVisualItemsWithErrors());
    }

    @Override
    public ViewContentDisplay getViewContentDisplay() {
        return contentDisplay;
    }

    @Override
    public VisualItem getVisualItem(String visualItemId) {
        return visualItemsByGroupId.get(visualItemId);
    }

    // TODO these view items should be cached & the cache should be updated
    @Override
    public LightweightCollection<VisualItem> getVisualItems() {
        return LightweightCollections
                .<VisualItem> toCollection(visualItemsByGroupId.values());
    }

    /**
     * @return List of {@link VisualItem}s that contain at least one of the
     *         {@link Resource}s.
     */
    @Override
    public LightweightList<VisualItem> getVisualItems(
            Iterable<Resource> resources) {

        assert resources != null;

        LightweightList<VisualItem> result = CollectionFactory
                .createLightweightList();
        Set<String> groupIds = resourceGrouping.getGroupIds(resources);
        for (String groupId : groupIds) {
            assert visualItemsByGroupId.containsKey(groupId) : "VisualItem with id "
                    + groupId + " not found";
            result.add(visualItemsByGroupId.get(groupId));
        }
        return result;
    }

    @Override
    public LightweightCollection<VisualItem> getVisualItemsWithErrors() {
        return errorModel.getVisualItemsWithErrors();
    }

    @Override
    public LightweightCollection<VisualItem> getVisualItemsWithErrors(Slot slot) {
        return errorModel.getVisualItemsWithErrors(slot);
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
    public boolean hasErrors(VisualItem visualItem) {
        return errorModel.hasErrors(visualItem);
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
            public boolean containsVisualItem(String visualItemId) {
                return DefaultVisualizationModel.this
                        .containsVisualItem(visualItemId)
                        && !hasErrors(visualItemsByGroupId.get(visualItemId));
            }

            @Override
            public VisualItemValueResolver getResolver(Slot slot) {
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
            public VisualItem getVisualItem(String visualItemId) {

                VisualItem visualItem = DefaultVisualizationModel.this
                        .getVisualItem(visualItemId);

                if (visualItem == null) {
                    throw new NoSuchElementException("VisualItem with id "
                            + visualItemId + " is not contained.");
                }
                if (DefaultVisualizationModel.this.hasErrors(visualItem)) {
                    throw new NoSuchElementException("VisualItem with id "
                            + visualItemId
                            + " contains errors and cannot be retrieved.");
                }
                return visualItem;
            }

            @Override
            public LightweightCollection<VisualItem> getVisualItems() {
                return DefaultVisualizationModel.this.getValidVisualItems();
            }

            @Override
            public LightweightCollection<VisualItem> getVisualItems(
                    Iterable<Resource> resources) {
                return DefaultVisualizationModel.this
                        .getValidVisualItems(resources);
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

                        updateSubset(Subset.HIGHLIGHTED,
                                event.getAddedResources(),
                                event.getRemovedResources());
                    }
                }));
    }

    private void initResourceGrouping() {
        resourceGrouping.addHandler(new ResourceGroupingChangedHandler() {
            @Override
            public void onResourceCategoriesChanged(
                    ResourceGroupingChangedEvent e) {
                processResourceGroupingChange(e);
            }
        });
    }

    private void initSelectionModelEventHandlers() {
        handlerRegistrations.addHandlerRegistration(selectedResources
                .addEventHandler(new ResourceSetChangedEventHandler() {
                    @Override
                    public void onResourceSetChanged(
                            ResourceSetChangedEvent event) {

                        updateSubset(Subset.SELECTED,
                                event.getAddedResources(),
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
            LightweightCollection<CategorizableResourceGroupingChange> changes) {

        if (changes.isEmpty()) {
            return LightweightCollections.emptyCollection();
        }

        LightweightList<VisualItem> addedVisualItems = CollectionFactory
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
            assert change.getDelta() == ChangeType.GROUP_CREATED;

            addedVisualItems.add(createVisualItem(change.getGroupID(),
                    change.getResourceSet(), highlightedResources,
                    selectedResources));
        }

        return addedVisualItems;
    }

    private LightweightCollection<VisualItem> processRemoveChanges(
            LightweightCollection<CategorizableResourceGroupingChange> changes) {

        if (changes.isEmpty()) {
            return LightweightCollections.emptyCollection();
        }

        LightweightList<VisualItem> removedVisualItems = CollectionFactory
                .createLightweightList();
        for (CategorizableResourceGroupingChange change : changes) {
            assert change.getDelta() == ChangeType.GROUP_REMOVED;

            // XXX dispose should be done after method call / event firing etc.
            removedVisualItems.add(removeVisualItem(change.getGroupID()));
        }
        return removedVisualItems;
    }

    private void processResourceGroupingChange(
            ResourceGroupingChangedEvent event) {

        assert event != null;

        LightweightCollection<VisualItem> visualItemsThatHadErrors = copy(getVisualItemsWithErrors());

        Delta<VisualItem> delta = updateVisualItemsOnGroupingChange(event);

        updateErrorModel(delta);

        Delta<VisualItem> deltaThatConsidersErrors = calculateDeltaThatConsidersErrors(
                delta, visualItemsThatHadErrors);

        updateViewContentDisplay(deltaThatConsidersErrors,
                LightweightCollections.<Slot> emptyCollection());

        fireVisualItemContainerChangeEvent(delta);
    }

    private LightweightCollection<VisualItem> processUpdates(
            LightweightCollection<CategorizableResourceGroupingChange> changes) {

        if (changes.isEmpty()) {
            return LightweightCollections.emptyCollection();
        }

        LightweightList<VisualItem> updatedVisualItems = CollectionFactory
                .createLightweightList();
        for (CategorizableResourceGroupingChange change : changes) {
            assert change.getDelta() == ChangeType.GROUP_CHANGED;
            DefaultVisualItem visualItem = visualItemsByGroupId.get(change
                    .getGroupID());

            updateVisualItemHighlightingSet(visualItem, change);
            updateVisualItemSelectionSet(change, visualItem);

            updatedVisualItems.add(visualItem);
        }

        return updatedVisualItems;
    }

    private DefaultVisualItem removeVisualItem(String groupID) {
        assert groupID != null : "groupIDs must not be null";
        assert visualItemsByGroupId.containsKey(groupID) : "no VisualItem for "
                + groupID;

        DefaultVisualItem visualItem = visualItemsByGroupId.remove(groupID);
        visualItem.dispose();

        assert !visualItemsByGroupId.containsKey(groupID);

        return visualItem;
    }

    @Override
    public void setCategorizer(ResourceMultiCategorizer newCategorizer) {
        resourceGrouping.setCategorizer(newCategorizer);
    }

    @Override
    public void setContentResourceSet(ResourceSet resources) {
        resourceGrouping.setResourceSet(resources);
    }

    /*
     * IMPLEMENTATION NOTE: We updated the error model and view content display
     * directly after the resolver has been set, because this keeps the
     * algorithm simpler (compared to an event based approach, which would
     * require prioritizing event handlers etc.)
     * 
     * TODO this can cause problems, if you actively try to set a fixed resolver
     */
    @Override
    public void setResolver(Slot slot, VisualItemValueResolver resolver) {
        assert slot != null;
        assert resolver != null;

        // keep information (currently valid / invalid stuff)
        LightweightCollection<VisualItem> visualItemsThatHadErrors = copy(getVisualItemsWithErrors());
        LightweightCollection<VisualItem> visualItemsThatWereValid = copy(getValidVisualItems());

        clearVisualItemValueCache(slot);

        // actually change the slot mapping
        slotMappingConfiguration.setResolver(slot, resolver);

        updateErrorModel(slot);

        // CASE 1: VisualItems that went from invalid to valid get added
        LightweightCollection<VisualItem> visualItemsToAdd = getRelativeComplement(
                visualItemsThatHadErrors, getVisualItemsWithErrors());

        // CASE 2: VisualItems that went from valid to invalid get removed
        LightweightCollection<VisualItem> visualItemsToRemove = getRelativeComplement(
                visualItemsThatWereValid, getValidVisualItems());

        updateViewContentDisplay(
                createAddedRemovedDelta(visualItemsToAdd, visualItemsToRemove),
                toCollection(slot));
    }

    private void updateErrorModel(Delta<VisualItem> delta) {
        assert delta != null;

        updateErrorModel(delta.getAddedElements());
        updateErrorModel(delta.getUpdatedElements());
        errorModel.clearErrors(delta.getRemovedElements());
    }

    private void updateErrorModel(LightweightCollection<VisualItem> visualItems) {
        assert visualItems != null;

        errorModel.clearErrors(visualItems);
        for (Slot slot : getSlots()) {
            updateErrorModel(slot, visualItems);
        }
    }

    private void updateErrorModel(Slot changedSlot) {
        assert changedSlot != null;

        errorModel.clearErrors(changedSlot);
        updateErrorModel(changedSlot, getVisualItems());
    }

    private void updateErrorModel(Slot slot,
            LightweightCollection<VisualItem> visualItems) {

        if (!slotMappingConfiguration.isConfigured(slot)) {
            /*
             * TODO potential optimization: have all invalid state for slot in
             * error model (would return errors for all view items)
             */
            errorModel.reportErrors(slot, visualItems);
            return;
        }

        VisualItemValueResolver resolver = slotMappingConfiguration
                .getResolver(slot);

        for (VisualItem visualItem : visualItems) {
            // TODO subset delegating resolvers have to be done last, because
            // their errors are dependant on the other resolvers being set
            if (!resolver.canResolve(visualItem, this)) {
                /*
                 * TODO potential optimization: only change error model if state
                 * for view item has changed (delta update).
                 */
                errorModel.reportError(slot, visualItem);
            }
        }
    }

    /*
     * TODO introduce Delta/Change object with added/removed --> just use full
     * delta?
     */
    private void updateSubset(Subset subset,
            LightweightCollection<Resource> addedResources,
            LightweightCollection<Resource> removedResources) {

        ResourceSet addedResourcesInThisVisualization = getIntersectionWithVisualizationResources(addedResources);
        ResourceSet removedResourcesInThisVisualization = getIntersectionWithVisualizationResources(removedResources);

        if (addedResourcesInThisVisualization.isEmpty()
                && removedResourcesInThisVisualization.isEmpty()) {
            return;
        }

        /*
         * XXX What if VisualItem subsets are not updated and then it becomes
         * valid again?
         */

        LightweightCollection<VisualItem> affectedVisualItems = getValidVisualItems(new CombinedIterable<Resource>(
                addedResources, removedResources));

        for (VisualItem visualItem : affectedVisualItems) {
            ((DefaultVisualItem) visualItem).updateSubset(subset,
                    addedResourcesInThisVisualization,
                    removedResourcesInThisVisualization);
        }

        updateViewContentDisplay(createUpdatedDelta(affectedVisualItems),
                LightweightCollections.<Slot> emptyCollection());
    }

    /**
     * NOTE: Exceptions are reported and not thrown to ensure robustness.
     */
    private void updateViewContentDisplay(Delta<VisualItem> delta,
            LightweightCollection<Slot> changedSlots) {

        if (delta.isEmpty() && changedSlots.isEmpty()) {
            return;
        }

        assertWithoutErrors(delta.getAddedElements());
        assertWithoutErrors(delta.getUpdatedElements());

        try {
            contentDisplay.update(delta, changedSlots);
        } catch (Throwable ex) {
            errorHandler.handleError(ex);
        }
    }

    // TODO refactor: updateVisualItemSelectionSet
    private void updateVisualItemHighlightingSet(DefaultVisualItem visualItem,
            CategorizableResourceGroupingChange change) {

        LightweightList<Resource> highlightedAdded = highlightedResources
                .getIntersection(change.getAddedResources());
        LightweightList<Resource> highlightedRemoved = highlightedResources
                .getIntersection(change.getRemovedResources());

        if (!highlightedAdded.isEmpty() || !highlightedRemoved.isEmpty()) {
            visualItem.updateSubset(Subset.HIGHLIGHTED, highlightedAdded,
                    highlightedRemoved);
        }
    }

    // TODO refactor: updateVisualItemHighlightingSet
    private void updateVisualItemSelectionSet(
            CategorizableResourceGroupingChange change,
            DefaultVisualItem visualItem) {

        LightweightList<Resource> selectedAdded = selectedResources
                .getIntersection(change.getAddedResources());
        LightweightList<Resource> selectedRemoved = selectedResources
                .getIntersection(change.getRemovedResources());

        if (!selectedAdded.isEmpty() || !selectedRemoved.isEmpty()) {
            visualItem.updateSubset(Subset.SELECTED, selectedAdded,
                    selectedRemoved);
        }
    }

    private Delta<VisualItem> updateVisualItemsOnGroupingChange(
            ResourceGroupingChangedEvent event) {

        assert event != null;

        /*
         * IMPORTANT: remove old items before adding new once (there might be
         * conflicts, i.e. groups with the same id)
         */
        LightweightCollection<VisualItem> removedVisualItems = processRemoveChanges(event
                .getChanges(ChangeType.GROUP_REMOVED));
        LightweightCollection<VisualItem> addedVisualItems = processAddChanges(event
                .getChanges(ChangeType.GROUP_CREATED));
        LightweightCollection<VisualItem> updatedVisualItems = processUpdates(event
                .getChanges(ChangeType.GROUP_CHANGED));

        return createDelta(addedVisualItems, updatedVisualItems,
                removedVisualItems);
    }

}