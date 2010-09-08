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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.thechiselgroup.choosel.client.resources.DefaultResourceSet;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.ui.popup.PopupClosingEvent;
import org.thechiselgroup.choosel.client.ui.popup.PopupClosingHandler;
import org.thechiselgroup.choosel.client.ui.popup.PopupManager;
import org.thechiselgroup.choosel.client.util.Disposable;

import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;

// TODO separate out resource item controller
public class DefaultResourceItem implements Disposable, ResourceItem {

    public static enum Status {

        DEFAULT, HIGHLIGHTED, HIGHLIGHTED_SELECTED, SELECTED, PARTIALLY_HIGHLIGHTED, PARTIALLY_HIGHLIGHTED_SELECTED, PARTIALLY_SELECTED

    }

    public static enum SubsetStatus {

        NONE, PARTIAL, COMPLETE

    }

    private String category;

    protected final HoverModel hoverModel;

    protected final PopupManager popupManager;

    // TODO update & paint on changes in resources!!!
    private final ResourceSet resources;

    private final ResourceItemValueResolver valueResolver;

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

    public DefaultResourceItem(String category, ResourceSet resources,
            HoverModel hoverModel, PopupManager popupManager,
            ResourceItemValueResolver valueResolver) {

        assert category != null;
        assert resources != null;
        assert hoverModel != null;
        assert popupManager != null;
        assert valueResolver != null;

        this.category = category;
        this.resources = resources;
        this.popupManager = popupManager;
        this.hoverModel = hoverModel; // TODO separate controller
        this.valueResolver = valueResolver;

        this.highlightedResources = new DefaultResourceSet();
        this.selectedResources = new DefaultResourceSet();

        initHighlighting();
        initPopupHighlighting();
    }

    public void addHighlightedResources(
            Collection<Resource> highlightedResources) {

        this.cachedHighlightStatus = null;
        this.highlightedResources
                .addAll(calculateAffectedResources(highlightedResources));
    }

    public void addSelectedResources(Collection<Resource> selectedResources) {
        this.cachedSelectedStatus = null;
        this.selectedResources
                .addAll(calculateAffectedResources(selectedResources));
    }

    private List<Resource> calculateAffectedResources(
            Collection<Resource> resources) {

        assert resources != null;

        List<Resource> affectedResources = new ArrayList<Resource>();
        affectedResources.addAll(resources);
        affectedResources.retainAll(this.resources);

        return affectedResources;
    }

    @Override
    public void dispose() {
        highlightingManager.dispose();
        popupHighlightingManager.dispose();
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
    public Collection<Resource> getHighlightedSelectedResources() {
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
    public Object getResourceValue(String slotID) {
        return valueResolver.resolve(slotID, category, resources);
    }

    @Override
    public Collection<Resource> getSelectedResources() {
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

    /*
     * (non-Javadoc)
     * 
     * @see org.thechiselgroup.choosel.client.views.ResourceItem#getStatus()
     */
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

    private void initHighlighting() {
        this.highlightingManager = new HighlightingManager(hoverModel,
                resources);
    }

    private void initPopupHighlighting() {
        this.popupHighlightingManager = new HighlightingManager(hoverModel,
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
            Collection<Resource> highlightedResources) {

        this.cachedHighlightStatus = null;
        this.highlightedResources
                .removeAll(calculateAffectedResources(highlightedResources));
    }

    public void removeSelectedResources(Collection<Resource> selectedResources) {
        this.cachedSelectedStatus = null;
        this.selectedResources
                .removeAll(calculateAffectedResources(selectedResources));
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