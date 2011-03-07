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
package org.thechiselgroup.choosel.core.client.views;

import java.util.Map;

import org.thechiselgroup.choosel.core.client.resources.DefaultResourceSet;
import org.thechiselgroup.choosel.core.client.resources.Resource;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.resources.ResourceSetChangedEvent;
import org.thechiselgroup.choosel.core.client.resources.ResourceSetChangedEventHandler;
import org.thechiselgroup.choosel.core.client.ui.popup.PopupClosingEvent;
import org.thechiselgroup.choosel.core.client.ui.popup.PopupClosingHandler;
import org.thechiselgroup.choosel.core.client.ui.popup.PopupManager;
import org.thechiselgroup.choosel.core.client.util.Disposable;
import org.thechiselgroup.choosel.core.client.util.collections.CollectionFactory;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.util.event.EventHandlerPriority;
import org.thechiselgroup.choosel.core.client.util.event.PrioritizedEventHandler;
import org.thechiselgroup.choosel.core.client.views.slots.Slot;
import org.thechiselgroup.choosel.core.client.views.slots.SlotMappingChangedEvent;
import org.thechiselgroup.choosel.core.client.views.slots.SlotMappingChangedHandler;
import org.thechiselgroup.choosel.core.client.views.slots.SlotMappingConfiguration;

import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;

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
            allSubsetSlotValueCache.clear();
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

            allSubsetSlotValueCache.remove(slotId);
            selectedSubsetSlotValueCache.remove(slotId);
            highlightedSubsetSlotValueCache.remove(slotId);
        }
    }

    private String viewItemID;

    protected final HoverModel hoverModel;

    protected final PopupManager popupManager;

    // TODO update & paint on changes in resources!!!
    private final ResourceSet resources;

    private final SlotMappingConfiguration slotMappingConfiguration;

    private HighlightingManager highlightingManager;

    /**
     * The representation of this resource item in the specific display. This is
     * set by the display to enable fast reference to this display element, and
     * should be casted into the specific type.
     */
    // TODO dispose
    private Object displayObject;

    private ResourceSet highlightedResources;

    private ResourceSet selectedResources;

    private HighlightingManager popupHighlightingManager;

    private SubsetStatus cachedHighlightStatus = null;

    private SubsetStatus cachedSelectedStatus = null;

    /**
     * PERFORMANCE: Cache for the resolved slot values of ALL subset. Maps the
     * slot id to the value.
     */
    private Map<String, Object> allSubsetSlotValueCache = CollectionFactory
            .createStringMap();

    /**
     * PERFORMANCE: Cache for the resolved slot values of SELECTED subset. Maps
     * the slot id to the value.
     */
    private Map<String, Object> highlightedSubsetSlotValueCache = CollectionFactory
            .createStringMap();

    /**
     * PERFORMANCE: Cache for the resolved slot values of HIGHLIGHTED subset.
     * Maps the slot id to the value.
     */
    private Map<String, Object> selectedSubsetSlotValueCache = CollectionFactory
            .createStringMap();

    public DefaultViewItem(String viewItemID, ResourceSet resources,
            HoverModel hoverModel, PopupManager popupManager,
            SlotMappingConfiguration slotMappingConfiguration) {

        assert viewItemID != null;
        assert resources != null;
        assert hoverModel != null;
        assert popupManager != null;
        assert slotMappingConfiguration != null;

        this.viewItemID = viewItemID;
        this.resources = resources;
        this.popupManager = popupManager;
        this.hoverModel = hoverModel; // TODO separate controller
        this.slotMappingConfiguration = slotMappingConfiguration;

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

        initHighlighting();
        initPopupHighlighting();
    }

    @Override
    public void dispose() {
        // XXX deregister event listeners
        // XXX dispose intersection sets

        highlightingManager.dispose();
        popupHighlightingManager.dispose();
    }

    /**
     * Performs the slot value resolution for given resources. Uses caching.
     * 
     * @param cache
     *            Map of slot id to cached values
     */
    private Object doResolve(Slot slot, ResourceSet resources,
            Map<String, Object> cache) {

        assert slot != null;
        assert resources != null;
        assert cache != null;
        assert this.resources.containsAll(resources);

        String slotId = slot.getId();
        if (cache.containsKey(slotId)) {
            return cache.get(slotId);
        }

        Object value = slotMappingConfiguration.resolve(slot, viewItemID,
                resources);

        cache.put(slotId, value);

        return value;
    }

    @Override
    public Object getDisplayObject() {
        return displayObject;
    }

    @Override
    public ResourceSet getHighlightedResources() {
        assert resources.containsAll(highlightedResources);
        return highlightedResources;
    }

    @Override
    public ResourceSet getHighlightedSelectedResources() {
        assert resources.containsAll(highlightedResources);
        assert resources.containsAll(selectedResources);

        ResourceSet highlightedSelectedResources = new DefaultResourceSet();
        highlightedSelectedResources.addAll(highlightedResources);
        highlightedSelectedResources.retainAll(selectedResources);

        return highlightedSelectedResources;
    }

    @Override
    public HighlightingManager getHighlightingManager() {
        return highlightingManager;
    }

    @Override
    public SubsetStatus getHighlightStatus() {
        if (cachedHighlightStatus == null) {
            cachedHighlightStatus = getSubsetStatus(highlightedResources);
        }

        return cachedHighlightStatus;
    }

    @Override
    public final PopupManager getPopupManager() {
        return popupManager;
    }

    @Override
    public ResourceSet getResourceSet() {
        return resources;
    }

    @Override
    public ResourceSet getSelectedResources() {
        assert resources.containsAll(selectedResources);
        return selectedResources;
    }

    @Override
    public SubsetStatus getSelectionStatus() {
        if (cachedSelectedStatus == null) {
            cachedSelectedStatus = getSubsetStatus(selectedResources);
        }

        return cachedSelectedStatus;
    }

    @Override
    public <T> T getSlotValue(Slot slot) {
        return getSlotValue(slot, Subset.ALL);
    }

    @Override
    public <T> T getSlotValue(Slot slot, Subset subset) {
        assert slot != null : "slot must not be null";
        assert subset != null : "subset must not be null";

        switch (subset) {
        case ALL:
            return (T) doResolve(slot, resources, allSubsetSlotValueCache);
        case SELECTED:
            return (T) doResolve(slot, selectedResources,
                    selectedSubsetSlotValueCache);
        case HIGHLIGHTED:
            return (T) doResolve(slot, highlightedResources,
                    highlightedSubsetSlotValueCache);
        }

        throw new IllegalArgumentException("invalid slot / sub combination "
                + slot + " " + subset);
    }

    @Override
    public Status getStatus() {
        if (getHighlightStatus() == SubsetStatus.COMPLETE
                && getSelectionStatus() == SubsetStatus.COMPLETE) {
            return Status.HIGHLIGHTED_SELECTED;
        }

        if (getHighlightStatus() != SubsetStatus.NONE
                && getSelectionStatus() != SubsetStatus.NONE) {
            return Status.PARTIALLY_HIGHLIGHTED_SELECTED;
        }

        if (getHighlightStatus() == SubsetStatus.COMPLETE) {
            return Status.HIGHLIGHTED;
        }

        if (getSelectionStatus() == SubsetStatus.COMPLETE) {
            return Status.SELECTED;
        }

        if (getHighlightStatus() == SubsetStatus.PARTIAL) {
            return Status.PARTIALLY_HIGHLIGHTED;
        }

        if (getSelectionStatus() == SubsetStatus.PARTIAL) {
            return Status.PARTIALLY_SELECTED;
        }

        return Status.DEFAULT;
    }

    private SubsetStatus getSubsetStatus(ResourceSet subset) {
        if (subset.isEmpty()) {
            return SubsetStatus.NONE;
        }

        if (subset.containsEqualResources(resources)) {
            return SubsetStatus.COMPLETE;
        }

        return SubsetStatus.PARTIAL;
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

    private void initHighlighting() {
        highlightingManager = new HighlightingManager(hoverModel, resources);
    }

    private void initPopupHighlighting() {
        popupHighlightingManager = new HighlightingManager(hoverModel,
                resources);

        popupManager.addPopupMouseOverHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent e) {
                popupHighlightingManager.setHighlighting(true);
            }
        });
        popupManager.addPopupMouseOutHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                popupHighlightingManager.setHighlighting(false);
            }
        });
        popupManager.addPopupClosingHandler(new PopupClosingHandler() {
            @Override
            public void onPopupClosing(PopupClosingEvent event) {
                popupHighlightingManager.setHighlighting(false);
            }
        });
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
        highlightedSubsetSlotValueCache.clear();

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
        selectedSubsetSlotValueCache.clear();

        selectedResources.addAll(resources
                .getIntersection(addedSelectedResources));
        selectedResources.removeAll(resources
                .getIntersection(removedSelectedResources));
    }

}