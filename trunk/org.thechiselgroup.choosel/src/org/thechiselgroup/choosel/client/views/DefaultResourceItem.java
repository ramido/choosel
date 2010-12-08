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
package org.thechiselgroup.choosel.client.views;

import java.util.Map;

import org.thechiselgroup.choosel.client.resources.DefaultResourceSet;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ResourcesAddedEvent;
import org.thechiselgroup.choosel.client.resources.ResourcesAddedEventHandler;
import org.thechiselgroup.choosel.client.resources.ResourcesRemovedEvent;
import org.thechiselgroup.choosel.client.resources.ResourcesRemovedEventHandler;
import org.thechiselgroup.choosel.client.ui.popup.PopupClosingEvent;
import org.thechiselgroup.choosel.client.ui.popup.PopupClosingHandler;
import org.thechiselgroup.choosel.client.ui.popup.PopupManager;
import org.thechiselgroup.choosel.client.util.Disposable;
import org.thechiselgroup.choosel.client.util.collections.CollectionFactory;
import org.thechiselgroup.choosel.client.util.collections.LightweightCollection;

import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;

/**
 * Default implementation of {@link ResourceItem}. <b>PERFORMANCE NOTE</b>:
 * Provides caching for calculated slot values and for highlighting and
 * selection status.
 * 
 * @author Lars Grammel
 */
// TODO separate out resource item controller part
public class DefaultResourceItem implements Disposable, ResourceItem {

    private String groupID;

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
     * 
     * TODO dispose
     */
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

    public DefaultResourceItem(String groupID, ResourceSet resources,
            HoverModel hoverModel, PopupManager popupManager,
            SlotMappingConfiguration slotMappingConfiguration) {

        assert groupID != null;
        assert resources != null;
        assert hoverModel != null;
        assert popupManager != null;
        assert slotMappingConfiguration != null;

        this.groupID = groupID;
        this.resources = resources;
        this.popupManager = popupManager;
        this.hoverModel = hoverModel; // TODO separate controller
        this.slotMappingConfiguration = slotMappingConfiguration;

        highlightedResources = new DefaultResourceSet();
        selectedResources = new DefaultResourceSet();

        initCacheCleaning();
        initHighlighting();
        initPopupHighlighting();
    }

    public void addHighlightedResources(
            LightweightCollection<Resource> highlightedResources) {

        assert highlightedResources != null;

        cachedHighlightStatus = null;
        highlightedSubsetSlotValueCache.clear();
        this.highlightedResources.addAll(resources
                .getIntersection(highlightedResources));
    }

    public void addSelectedResources(
            LightweightCollection<Resource> selectedResources) {

        assert selectedResources != null;

        cachedSelectedStatus = null;
        selectedSubsetSlotValueCache.clear();
        this.selectedResources.addAll(resources
                .getIntersection(selectedResources));
    }

    @Override
    public void dispose() {
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

        Object value = slotMappingConfiguration.resolve(slot, groupID,
                resources);

        cache.put(slotId, value);

        return value;
    }

    @Override
    public Object getDisplayObject() {
        return displayObject;
    }

    @Override
    public String getGroupID() {
        return groupID;
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
    public Object getResourceValue(Slot slot) {
        return getResourceValue(slot, Subset.ALL);
    }

    @Override
    public Object getResourceValue(Slot slot, Subset subset) {
        assert slot != null;
        assert subset != null;

        switch (subset) {
        case ALL:
            return doResolve(slot, resources, allSubsetSlotValueCache);
        case SELECTED:
            return doResolve(slot, selectedResources,
                    selectedSubsetSlotValueCache);
        case HIGHLIGHTED:
            return doResolve(slot, highlightedResources,
                    highlightedSubsetSlotValueCache);
        }

        throw new IllegalArgumentException("invalid slot / sub combination "
                + slot + " " + subset);
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

    private void initCacheCleaning() {
        resources.addEventHandler(new ResourcesAddedEventHandler() {
            @Override
            public void onResourcesAdded(ResourcesAddedEvent e) {
                allSubsetSlotValueCache.clear();
            }
        });
        resources.addEventHandler(new ResourcesRemovedEventHandler() {
            @Override
            public void onResourcesRemoved(ResourcesRemovedEvent e) {
                allSubsetSlotValueCache.clear();
            }
        });
        slotMappingConfiguration.addHandler(new SlotMappingChangedHandler() {
            @Override
            public void onResourceCategoriesChanged(SlotMappingChangedEvent e) {
                String slotId = e.getSlot().getId();

                allSubsetSlotValueCache.remove(slotId);
                selectedSubsetSlotValueCache.remove(slotId);
                highlightedSubsetSlotValueCache.remove(slotId);
            }
        });
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

    public void removeHighlightedResources(
            LightweightCollection<Resource> highlightedResources) {

        assert highlightedResources != null;

        cachedHighlightStatus = null;
        highlightedSubsetSlotValueCache.clear();
        this.highlightedResources.removeAll(resources
                .getIntersection(highlightedResources));
    }

    public void removeSelectedResources(
            LightweightCollection<Resource> selectedResources) {
        assert selectedResources != null;

        cachedSelectedStatus = null;
        selectedSubsetSlotValueCache.clear();
        this.selectedResources.removeAll(resources
                .getIntersection(selectedResources));
    }

    @Override
    public void setDisplayObject(Object displayObject) {
        this.displayObject = displayObject;
    }

    @Override
    public String toString() {
        return "ResourceItem[" + resources.toString() + "]";
    }

}