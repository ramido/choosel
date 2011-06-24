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

import org.thechiselgroup.choosel.core.client.resources.DefaultResourceSet;
import org.thechiselgroup.choosel.core.client.resources.Resource;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.resources.ResourceSetChangedEvent;
import org.thechiselgroup.choosel.core.client.resources.ResourceSetChangedEventHandler;
import org.thechiselgroup.choosel.core.client.util.Disposable;
import org.thechiselgroup.choosel.core.client.util.collections.CollectionFactory;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.util.event.EventHandlerPriority;
import org.thechiselgroup.choosel.core.client.util.event.PrioritizedEventHandler;

/**
 * Default implementation of {@link ViewItem}.
 * <p>
 * <b>PERFORMANCE NOTE</b>: Provides caching for calculated slot values and for
 * highlighting and selection status.
 * </p>
 * 
 * @author Lars Grammel
 */
// TODO separate out resource item controller part
public class DefaultViewItem implements Disposable, ViewItem {

    private final class CacheUpdateOnResourceSetChange implements
            ResourceSetChangedEventHandler, PrioritizedEventHandler {

        @Override
        public EventHandlerPriority getPriority() {
            return EventHandlerPriority.FIRST;
        }

        @Override
        public void onResourceSetChanged(ResourceSetChangedEvent event) {
            cache.clear();
        }
    }

    private final class CacheUpdateOnSlotChange implements
            SlotMappingChangedHandler, PrioritizedEventHandler {

        @Override
        public EventHandlerPriority getPriority() {
            return EventHandlerPriority.FIRST;
        }

        @Override
        public void onResourceCategoriesChanged(SlotMappingChangedEvent e) {
            String slotId = e.getSlot().getId();

            cache.remove(slotId);
        }
    }

    private String viewItemID;

    // TODO update & paint on changes in resources!!!
    private final ResourceSet resources;

    private final SlotMappingConfiguration slotMappingConfiguration;

    /**
     * The representation of this resource item in the specific display. This is
     * set by the display to enable fast reference to this display element, and
     * should be casted into the specific type.
     */
    // TODO dispose
    private Object displayObject;

    private ResourceSet highlightedResources;

    private ResourceSet selectedResources;

    private Status cachedHighlightStatus = null;

    private Status cachedSelectedStatus = null;

    /**
     * PERFORMANCE: Cache for the resolved slot values of ALL subset. Maps the
     * slot id to the value.
     */
    private Map<String, Object> cache = CollectionFactory.createStringMap();

    private final ViewItemInteractionHandler interactionHandler;

    public DefaultViewItem(String viewItemID, ResourceSet resources,
            SlotMappingConfiguration slotMappingConfiguration,
            ViewItemInteractionHandler interactionHandler) {

        assert viewItemID != null;
        assert resources != null;
        assert slotMappingConfiguration != null;
        assert interactionHandler != null;

        this.viewItemID = viewItemID;
        this.resources = resources;
        this.slotMappingConfiguration = slotMappingConfiguration;
        this.interactionHandler = interactionHandler;

        highlightedResources = new DefaultResourceSet();
        selectedResources = new DefaultResourceSet();

        initCacheCleaning(resources, slotMappingConfiguration);

        resources.addEventHandler(new ResourceSetChangedEventHandler() {
            @Override
            public void onResourceSetChanged(ResourceSetChangedEvent event) {
                LightweightCollection<Resource> removedResources = event
                        .getRemovedResources();

                highlightedResources.removeAll(removedResources);
                selectedResources.removeAll(removedResources);
            }
        });
    }

    @Override
    public void dispose() {
        // XXX deregister event listeners
        // XXX dispose intersection sets

    }

    @Override
    public Object getDisplayObject() {
        return displayObject;
    }

    private Status getHighlightStatus() {
        if (cachedHighlightStatus == null) {
            cachedHighlightStatus = getSubsetStatus(highlightedResources);
        }

        return cachedHighlightStatus;
    }

    @Override
    public ResourceSet getResources() {
        return resources;
    }

    @Override
    public ResourceSet getResources(Subset subset) {
        assert subset != null;
        switch (subset) {
        case ALL:
            return getResources();
        case HIGHLIGHTED:
            return highlightedResources;
        case SELECTED:
            return selectedResources;
        default:
            throw new RuntimeException("should not be reached");
        }
    }

    private Status getSelectionStatus() {
        if (cachedSelectedStatus == null) {
            cachedSelectedStatus = getSubsetStatus(selectedResources);
        }

        return cachedSelectedStatus;
    }

    // TODO move, refactor
    @Override
    public Slot[] getSlots() {
        return slotMappingConfiguration.getSlots().toArray(
                new Slot[slotMappingConfiguration.getSlots().size()]);
    }

    @Override
    public Status getStatus(Subset subset) {
        assert subset != null;
        switch (subset) {
        case ALL:
            // always containing all contained resources
            return Status.FULL;
        case HIGHLIGHTED:
            return getHighlightStatus();
        case SELECTED:
            return getSelectionStatus();
        default:
            throw new RuntimeException("should not be reached");
        }
    }

    private Status getSubsetStatus(ResourceSet subset) {
        if (subset.isEmpty()) {
            return Status.NONE;
        }

        if (subset.containsEqualResources(resources)) {
            return Status.FULL;
        }

        return Status.PARTIAL;
    }

    @Override
    public <T> T getValue(Slot slot) {
        assert slot != null : "slot must not be null";

        String slotId = slot.getId();
        if (cache.containsKey(slotId)) {
            return (T) cache.get(slotId);
        }

        Object value = slotMappingConfiguration.resolve(slot, this);

        cache.put(slotId, value);

        return (T) value;
    }

    @Override
    public double getValueAsDouble(Slot slot) {
        return this.<Number> getValue(slot).doubleValue();
    }

    @Override
    public String getViewItemID() {
        return viewItemID;
    }

    public void initCacheCleaning(ResourceSet resources,
            SlotMappingConfiguration slotMappingConfiguration) {
        resources.addEventHandler(new CacheUpdateOnResourceSetChange());
        slotMappingConfiguration.addHandler(new CacheUpdateOnSlotChange());
    }

    @Override
    public boolean isStatus(Subset subset, Status... status) {
        Status realStatus = getStatus(subset);
        for (Status expectedStatus : status) {
            if (realStatus.equals(expectedStatus)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void reportInteraction(ViewItemInteraction interaction) {
        assert interaction != null;
        interactionHandler.onInteraction(this, interaction);
    }

    @Override
    public void setDisplayObject(Object displayObject) {
        this.displayObject = displayObject;
    }

    @Override
    public String toString() {
        return "ViewItem[" + resources.toString() + "]";
    }

    public void updateHighlightedResources(
            LightweightCollection<Resource> addedHighlightedResources,
            LightweightCollection<Resource> removedHighlightedResources) {

        assert addedHighlightedResources != null;
        assert removedHighlightedResources != null;

        cachedHighlightStatus = null;
        cache.clear();

        highlightedResources.addAll(resources
                .getIntersection(addedHighlightedResources));
        highlightedResources.removeAll(resources
                .getIntersection(removedHighlightedResources));
    }

    public void updateSelectedResources(
            LightweightCollection<Resource> addedSelectedResources,
            LightweightCollection<Resource> removedSelectedResources) {

        assert addedSelectedResources != null;
        assert removedSelectedResources != null;

        cachedSelectedStatus = null;
        cache.clear();

        selectedResources.addAll(resources
                .getIntersection(addedSelectedResources));
        selectedResources.removeAll(resources
                .getIntersection(removedSelectedResources));
    }

}