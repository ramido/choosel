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
package org.thechiselgroup.choosel.core.client.views;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.thechiselgroup.choosel.core.client.persistence.Memento;
import org.thechiselgroup.choosel.core.client.persistence.Persistable;
import org.thechiselgroup.choosel.core.client.persistence.PersistableRestorationService;
import org.thechiselgroup.choosel.core.client.resources.DefaultResourceSet;
import org.thechiselgroup.choosel.core.client.resources.IntersectionResourceSet;
import org.thechiselgroup.choosel.core.client.resources.Resource;
import org.thechiselgroup.choosel.core.client.resources.ResourceByPropertyMultiCategorizer;
import org.thechiselgroup.choosel.core.client.resources.ResourceByUriMultiCategorizer;
import org.thechiselgroup.choosel.core.client.resources.ResourceGrouping;
import org.thechiselgroup.choosel.core.client.resources.ResourceGroupingChange;
import org.thechiselgroup.choosel.core.client.resources.ResourceGroupingChange.Delta;
import org.thechiselgroup.choosel.core.client.resources.ResourceGroupingChangedEvent;
import org.thechiselgroup.choosel.core.client.resources.ResourceGroupingChangedHandler;
import org.thechiselgroup.choosel.core.client.resources.ResourceMultiCategorizer;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.resources.ResourceSetChangedEvent;
import org.thechiselgroup.choosel.core.client.resources.ResourceSetChangedEventHandler;
import org.thechiselgroup.choosel.core.client.resources.ResourceSetEventForwarder;
import org.thechiselgroup.choosel.core.client.resources.persistence.ResourceSetAccessor;
import org.thechiselgroup.choosel.core.client.resources.persistence.ResourceSetCollector;
import org.thechiselgroup.choosel.core.client.util.DisposeUtil;
import org.thechiselgroup.choosel.core.client.util.HandlerRegistrationSet;
import org.thechiselgroup.choosel.core.client.util.Initializable;
import org.thechiselgroup.choosel.core.client.util.collections.CollectionFactory;
import org.thechiselgroup.choosel.core.client.util.collections.CombinedIterable;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollections;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightList;
import org.thechiselgroup.choosel.core.client.views.slots.Slot;
import org.thechiselgroup.choosel.core.client.views.slots.SlotMappingChangedEvent;
import org.thechiselgroup.choosel.core.client.views.slots.SlotMappingChangedHandler;
import org.thechiselgroup.choosel.core.client.views.slots.SlotMappingConfiguration;
import org.thechiselgroup.choosel.core.client.views.slots.SlotMappingInitializer;

import com.google.gwt.user.client.Timer;

public class DefaultViewModel implements ViewModel {

    private static final String MEMENTO_CONTENT_DISPLAY = "content-display";

    private static final String MEMENTO_SLOT_MAPPINGS = "slot-mappings";

    private static final String MEMENTO_RESOURCE_MODEL = "resource-model";

    private static final String MEMENTO_SELECTION_MODEL = "selection-model";

    private static final String MEMENTO_GROUPING = "grouping";

    private ResourceSetEventForwarder allResourcesToGroupingForwarder;

    /**
     * Maps group ids (representing the resource sets that are calculated by the
     * resource grouping, also used as view item ids) to the
     * {@link DefaultViewItem}s that display the resource sets in the view.
     */
    private Map<String, DefaultViewItem> viewItemsByGroupId = CollectionFactory
            .createStringMap();

    private SlotMappingConfiguration slotMappingConfiguration;

    private ViewContentDisplay contentDisplay;

    private ViewContentDisplayCallback contentDisplayCallback;

    private HoverModel hoverModel;

    private ResourceGrouping resourceGrouping;

    private ResourceModel resourceModel;

    /*
     * Boolean flag that indicates if the configuration part of the view has
     * been created.
     * 
     * XXX This solution breaks down when there is more than one kind of
     * resource (i.e. with different properties)
     */
    private boolean isConfigurationAvailable = false;

    private boolean isInitialized;

    private SelectionModel selectionModel;

    private SlotMappingInitializer slotMappingInitializer;

    private IntersectionResourceSet highlightedResourcesIntersection;

    private final ViewItemBehavior viewItemBehavior;

    private HandlerRegistrationSet handlerRegistrations = new HandlerRegistrationSet();

    public DefaultViewModel(ResourceGrouping resourceGrouping,
            ViewContentDisplay contentDisplay,
            SlotMappingConfiguration slotMappingConfiguration,
            SelectionModel selectionModel, ResourceModel resourceModel,
            HoverModel hoverModel,
            SlotMappingInitializer slotMappingInitializer,
            ViewItemBehavior viewItemBehavior) {

        assert slotMappingConfiguration != null;
        assert resourceGrouping != null;
        assert contentDisplay != null;
        assert selectionModel != null;
        assert resourceModel != null;
        assert hoverModel != null;
        assert slotMappingInitializer != null;
        assert viewItemBehavior != null;

        this.slotMappingInitializer = slotMappingInitializer;
        this.slotMappingConfiguration = slotMappingConfiguration;
        this.resourceGrouping = resourceGrouping;
        this.contentDisplay = contentDisplay;
        this.selectionModel = selectionModel;
        this.resourceModel = resourceModel;
        this.hoverModel = hoverModel;
        this.viewItemBehavior = viewItemBehavior;
    }

    private DefaultViewItem createViewItem(String groupID,
            ResourceSet resources,
            LightweightCollection<Resource> highlightedResources,
            LightweightCollection<Resource> selectedResources) {

        DefaultViewItem viewItem = new DefaultViewItem(groupID, resources,
                slotMappingConfiguration, viewItemBehavior);

        viewItemBehavior.onViewItemCreated(viewItem);

        viewItem.updateHighlightedResources(highlightedResources,
                LightweightCollections.<Resource> emptyCollection());

        viewItem.updateSelectedResources(selectedResources,
                LightweightCollections.<Resource> emptyCollection());

        assert !viewItemsByGroupId.containsKey(groupID) : "groupsToViewItems already contains "
                + groupID;
        viewItemsByGroupId.put(groupID, viewItem);

        return viewItem;
    }

    @Override
    public void dispose() {
        for (DefaultViewItem viewItem : viewItemsByGroupId.values()) {
            viewItemBehavior.onViewItemRemoved(viewItem);
            viewItem.dispose();
        }

        DisposeUtil.dispose(resourceModel);
        resourceModel = null;

        DisposeUtil.dispose(selectionModel);
        selectionModel = null;

        allResourcesToGroupingForwarder.dispose();
        allResourcesToGroupingForwarder = null;
        contentDisplay.dispose();
        contentDisplay = null;

        hoverModel = null;

        handlerRegistrations.dispose();
        handlerRegistrations = null;
    }

    // protected for test access
    protected void doRestore(Memento state,
            PersistableRestorationService restorationService,
            ResourceSetAccessor accessor) {

        assert isInitialized : "view has to be initialized before restoring it";

        contentDisplay.startRestore();

        restoreGrouping(state.getChild(MEMENTO_GROUPING));
        restore(resourceModel, state, MEMENTO_RESOURCE_MODEL,
                restorationService, accessor);
        restore(selectionModel, state, MEMENTO_SELECTION_MODEL,
                restorationService, accessor);
        contentDisplay.restore(state.getChild(MEMENTO_CONTENT_DISPLAY),
                restorationService, accessor);
        restore(slotMappingConfiguration, state, MEMENTO_SLOT_MAPPINGS,
                restorationService, accessor);

        contentDisplay.endRestore();
    }

    public Map<String, ResourceSet> getCategorizedResourceSets() {
        return resourceGrouping.getCategorizedResourceSets();
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
        resourcesInThisView.addAll(resourceModel.getIntersection(resources));
        return resourcesInThisView;
    }

    public ResourceGrouping getResourceGrouping() {
        return resourceGrouping;
    }

    @Override
    public ResourceModel getResourceModel() {
        return resourceModel;
    }

    @Override
    public SelectionModel getSelectionModel() {
        return selectionModel;
    }

    @Override
    public SlotMappingConfiguration getSlotMappingConfiguration() {
        return slotMappingConfiguration;
    }

    @Override
    public Slot[] getSlots() {
        return contentDisplay.getSlots();
    }

    @Override
    public ViewContentDisplay getViewContentDisplay() {
        return contentDisplay;
    }

    public List<ViewItem> getViewItems() {
        List<ViewItem> result = new ArrayList<ViewItem>();
        for (DefaultViewItem resourceItem : viewItemsByGroupId.values()) {
            result.add(resourceItem);
        }
        return result;
    }

    /**
     * @return list of resource items that contain at least one of the
     *         resources.
     */
    private LightweightList<ViewItem> getViewItems(Iterable<Resource> resources) {

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
    public void init() {
        assert !isInitialized : "view has already been initialized";

        slotMappingConfiguration.initSlots(contentDisplay.getSlots());

        init(resourceModel);

        init(selectionModel);
        initSelectionModelEventHandlers();

        initResourceGrouping();
        initAllResourcesToResourceGroupingLink();
        initHighlightingModel();

        initContentDisplay();
        initSlotMappingChangeHandler();

        isInitialized = true;
    }

    private void init(Object target) {
        if (target instanceof Initializable) {
            ((Initializable) target).init();
        }
    }

    private void initAllResourcesToResourceGroupingLink() {
        allResourcesToGroupingForwarder = new ResourceSetEventForwarder(
                resourceModel.getResources(), resourceGrouping) {

            @Override
            public void onResourceSetChanged(ResourceSetChangedEvent event) {
                initializeVisualMappings(event.getTarget());
                super.onResourceSetChanged(event);
            }

        };
        allResourcesToGroupingForwarder.init();
    }

    // TODO eliminate inner class, implement methods in DefaultView & test them
    private void initContentDisplay() {
        contentDisplayCallback = new ViewContentDisplayCallback() {

            @Override
            public boolean containsViewItem(String groupId) {
                return viewItemsByGroupId.containsKey(groupId);
            }

            @Override
            public ResourceSet getAutomaticResourceSet() {
                return resourceModel.getAutomaticResourceSet();
            }

            @Override
            public String getSlotResolverDescription(Slot slot) {
                if (!slotMappingConfiguration.containsResolver(slot)) {
                    return "N/A";
                }

                return slotMappingConfiguration.getResolver(slot).toString();
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

                return DefaultViewModel.this.getViewItems(resources);
            }

        };
        contentDisplay.init(contentDisplayCallback);
    }

    /**
     * Creates an intersection of contained resources with highlighted resources
     * and registers for changes.
     */
    private void initHighlightingModel() {
        highlightedResourcesIntersection = new IntersectionResourceSet(
                new DefaultResourceSet());
        highlightedResourcesIntersection.addResourceSet(resourceModel
                .getResources());
        highlightedResourcesIntersection.addResourceSet(hoverModel
                .getResources());

        handlerRegistrations.addHandlerRegistration(hoverModel
                .addEventHandler(new ResourceSetChangedEventHandler() {
                    @Override
                    public void onResourceSetChanged(
                            ResourceSetChangedEvent event) {
                        updateHighlighting(event.getAddedResources(),
                                event.getRemovedResources());
                    }
                }));
    }

    private void initializeVisualMappings(ResourceSet resources) {
        /*
         * TODO check if there are changes when adding / adjust each slot -->
         * stable per slot --> initialize early for the slots & map to object
         * that has corresponding update method
         * 
         * XXX for now: just add a flag if a configuration has been created, and
         * if that's the case, don't rebuild the configuration.
         * 
         * XXX this also fails with redo / undo
         */
        // TODO check the validity of the configuration instead
        if (!isConfigurationAvailable) {
            slotMappingInitializer.initializeMappings(resources,
                    contentDisplay, slotMappingConfiguration);
            isConfigurationAvailable = true;
        }
    }

    private void initResourceGrouping() {
        resourceGrouping.addHandler(new ResourceGroupingChangedHandler() {

            @Override
            public void onResourceCategoriesChanged(
                    ResourceGroupingChangedEvent e) {
                updateViewItemsOnModelChange(e);
            }
        });
    }

    private void initSelectionModelEventHandlers() {
        handlerRegistrations.addHandlerRegistration(selectionModel
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
            public void onResourceCategoriesChanged(SlotMappingChangedEvent e) {
                contentDisplay.update(
                        LightweightCollections.<ViewItem> emptyCollection(),
                        LightweightCollections.<ViewItem> emptyCollection(),
                        LightweightCollections.<ViewItem> emptyCollection(),
                        LightweightCollections.toCollection(e.getSlot()));
            }
        });
    }

    /**
     * Processes the added resource groups when the grouping changes.
     */
    private LightweightCollection<ViewItem> processAddChanges(
            LightweightList<ResourceGroupingChange> changes) {

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
        LightweightList<Resource> highlightedResources = resourceModel
                .getIntersection(hoverModel.getResources());
        LightweightList<Resource> selectedResources = resourceModel
                .getIntersection(selectionModel.getSelection());

        for (ResourceGroupingChange change : changes) {
            assert change.getDelta() == Delta.GROUP_CREATED;

            addedViewItems.add(createViewItem(change.getGroupID(),
                    change.getResourceSet(), highlightedResources,
                    selectedResources));
        }

        return addedViewItems;
    }

    private LightweightCollection<ViewItem> processRemoveChanges(
            LightweightList<ResourceGroupingChange> changes) {

        if (changes.isEmpty()) {
            return LightweightCollections.emptyCollection();
        }

        LightweightList<ViewItem> removedViewItems = CollectionFactory
                .createLightweightList();
        for (ResourceGroupingChange change : changes) {
            assert change.getDelta() == Delta.GROUP_REMOVED;

            // XXX dispose should be done after method call...
            removedViewItems.add(removeViewItem(change.getGroupID()));
        }
        return removedViewItems;
    }

    // TODO implement
    private LightweightCollection<ViewItem> processUpdates(
            LightweightList<ResourceGroupingChange> changes) {

        if (changes.isEmpty()) {
            return LightweightCollections.emptyCollection();
        }

        LightweightList<ViewItem> updatedViewItems = CollectionFactory
                .createLightweightList();
        for (ResourceGroupingChange change : changes) {
            assert change.getDelta() == Delta.GROUP_CHANGED;
            DefaultViewItem viewItem = viewItemsByGroupId.get(change
                    .getGroupID());

            // intersect added with highlighting set - TODO extract
            LightweightList<Resource> highlightedAdded = hoverModel
                    .getResources().getIntersection(change.getAddedResources());
            LightweightList<Resource> highlightedRemoved = hoverModel
                    .getResources().getIntersection(
                            change.getRemovedResources());

            boolean add = false;

            if (!highlightedAdded.isEmpty() || !highlightedRemoved.isEmpty()) {
                viewItem.updateHighlightedResources(highlightedAdded,
                        highlightedRemoved);
                add = true;
            }

            LightweightList<Resource> selectedAdded = selectionModel
                    .getSelection().getIntersection(change.getAddedResources());
            LightweightList<Resource> selectedRemoved = selectionModel
                    .getSelection().getIntersection(
                            change.getRemovedResources());
            if (!selectedAdded.isEmpty() || !selectedRemoved.isEmpty()) {
                viewItem.updateSelectedResources(selectedAdded, selectedRemoved);
                add = true;
            }

            if (add) {
                updatedViewItems.add(viewItem);
            }
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
    public void restore(final Memento state,
            final PersistableRestorationService restorationService,
            final ResourceSetAccessor accessor) {

        /*
         * wait for content to be ready (needed for graph view swf loading on
         * restore)
         */
        // XXX this might be the cause for issue 25
        if (contentDisplay.isReady()) {
            doRestore(state, restorationService, accessor);
        } else {
            new Timer() {
                @Override
                public void run() {
                    restore(state, restorationService, accessor);
                }
            }.schedule(200);
        }
    }

    private void restore(Object target, Memento parentMemento,
            String targetMementoKey,
            PersistableRestorationService restorationService,
            ResourceSetAccessor accessor) {

        if (target instanceof Persistable) {
            ((Persistable) target).restore(
                    parentMemento.getChild(targetMementoKey),
                    restorationService, accessor);
        }
    }

    // TODO extract constants
    private void restoreGrouping(Memento groupingMemento) {
        assert groupingMemento != null : "The grouping memento you're trying to get a result from wasn't set";

        String categorizerType = (String) groupingMemento.getValue("type");

        if ("byProperty".equals(categorizerType)) {
            String property = (String) groupingMemento.getValue("property");
            resourceGrouping
                    .setCategorizer(new ResourceByPropertyMultiCategorizer(
                            property));
        } else if ("byUri".equals(categorizerType)) {
            resourceGrouping
                    .setCategorizer(new ResourceByUriMultiCategorizer());
        }
    }

    private void save(Object target, Memento parentMemento,
            String targetMementoKey, ResourceSetCollector resourceSetCollector) {

        if (target instanceof Persistable) {
            parentMemento.addChild(targetMementoKey,
                    ((Persistable) target).save(resourceSetCollector));
        }
    }

    @Override
    public Memento save(ResourceSetCollector resourceSetCollector) {
        Memento memento = new Memento();

        saveGrouping(memento);
        save(selectionModel, memento, MEMENTO_SELECTION_MODEL,
                resourceSetCollector);
        save(resourceModel, memento, MEMENTO_RESOURCE_MODEL,
                resourceSetCollector);
        memento.addChild(MEMENTO_CONTENT_DISPLAY,
                contentDisplay.save(resourceSetCollector));
        save(slotMappingConfiguration, memento, MEMENTO_SLOT_MAPPINGS,
                resourceSetCollector);

        return memento;
    }

    // TODO extract constants
    private void saveGrouping(Memento memento) {
        Memento groupingMemento = new Memento();

        ResourceMultiCategorizer categorizer = resourceGrouping
                .getCategorizer();
        if (categorizer instanceof ResourceByPropertyMultiCategorizer) {
            groupingMemento.setValue("type", "byProperty");
            groupingMemento.setValue("property",
                    ((ResourceByPropertyMultiCategorizer) categorizer)
                            .getProperty());
        } else if (categorizer instanceof ResourceByUriMultiCategorizer) {
            groupingMemento.setValue("type", "byUri");
        }

        memento.addChild(MEMENTO_GROUPING, groupingMemento);
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

        contentDisplay.update(
                LightweightCollections.<ViewItem> emptyCollection(),
                affectedViewItems,
                LightweightCollections.<ViewItem> emptyCollection(),
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

        contentDisplay.update(
                LightweightCollections.<ViewItem> emptyCollection(),
                affectedViewItems,
                LightweightCollections.<ViewItem> emptyCollection(),
                LightweightCollections.<Slot> emptyCollection());
    }

    // TODO use viewContentDisplay.update to perform single update
    // TODO test update gets called with the right sets
    // (a) add
    // (b) remove
    // (c) update
    // (d) add + update
    // (e) remove + update
    private void updateViewItemsOnModelChange(ResourceGroupingChangedEvent e) {
        assert e != null;

        /*
         * IMPORTANT: remove old items before adding new once (there might be
         * conflicts, i.e. groups with the same id)
         */
        LightweightCollection<ViewItem> removedResourceItems = processRemoveChanges(e
                .getChanges(Delta.GROUP_REMOVED));
        LightweightCollection<ViewItem> addedResourceItems = processAddChanges(e
                .getChanges(Delta.GROUP_CREATED));
        LightweightCollection<ViewItem> updatedResourceItems = processUpdates(e
                .getChanges(Delta.GROUP_CHANGED));

        contentDisplay.update(addedResourceItems, updatedResourceItems,
                removedResourceItems,
                LightweightCollections.<Slot> emptyCollection());
    }
}