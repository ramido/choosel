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

import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ResourcesAddedEvent;
import org.thechiselgroup.choosel.client.resources.ResourcesAddedEventHandler;
import org.thechiselgroup.choosel.client.resources.ResourcesRemovedEvent;
import org.thechiselgroup.choosel.client.resources.ResourcesRemovedEventHandler;
import org.thechiselgroup.choosel.client.ui.popup.PopupClosingEvent;
import org.thechiselgroup.choosel.client.ui.popup.PopupClosingHandler;
import org.thechiselgroup.choosel.client.ui.popup.PopupManager;

import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;

public class ResourceItem {

    public class HighlightingManager implements MouseOverHandler,
            MouseOutHandler, PopupClosingHandler {

        private boolean highlighted = false;

        @Override
        public void onMouseOut(MouseOutEvent e) {
            if (highlighted) {
                hoverModel.removeHighlightedResources(getResourceSet());
                highlighted = false;
            }
        }

        @Override
        public void onMouseOver(MouseOverEvent e) {
            if (!highlighted) {
                hoverModel.addHighlightedResources(getResourceSet());
                highlighted = true;
            }
        }

        @Override
        public void onPopupClosing(PopupClosingEvent event) {
            if (highlighted) {
                hoverModel.removeHighlightedResources(getResourceSet());
                highlighted = false;
            }
        }

    }

    public static enum Status {

        DEFAULT, GRAYED_OUT, HIGHLIGHTED, HIGHLIGHTED_SELECTED, SELECTED

    }

    private String category;

    private boolean highlighted = false;

    private HighlightingManager highlightingManager;

    protected final HoverModel hoverModel;

    protected final PopupManager popupManager;

    // TODO update & paint on changes in resources!!!
    private final ResourceSet resources;

    private boolean selected;

    private boolean selectionStatusVisible;

    private final ResourceItemValueResolver valueResolver;

    public ResourceItem(String category, ResourceSet resources,
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
        this.hoverModel = hoverModel;
        this.valueResolver = valueResolver;

        highlightingManager = new HighlightingManager();

        this.popupManager.addPopupMouseOverHandler(highlightingManager);
        this.popupManager.addPopupMouseOutHandler(highlightingManager);
        this.popupManager.addPopupClosingHandler(highlightingManager);

        resources.addEventHandler(new ResourcesAddedEventHandler() {
            @Override
            public void onResourcesAdded(ResourcesAddedEvent e) {
                updateContent();
            }
        });
        resources.addEventHandler(new ResourcesRemovedEventHandler() {
            @Override
            public void onResourcesRemoved(ResourcesRemovedEvent e) {
                updateContent();
            }
        });

        updateContent();
    }

    protected Status calculateStatus() {
        if (isHighlighted() && isSelected()) {
            return Status.HIGHLIGHTED_SELECTED;
        }

        if (isHighlighted()) {
            return Status.HIGHLIGHTED;
        }

        if (isSelected()) {
            return Status.SELECTED;
        }

        return Status.DEFAULT;
    }

    protected Status calculateStatusNormalVsGraySelection() {
        if (isHighlighted()) {
            return Status.HIGHLIGHTED;
        }

        if (selectionStatusVisible && isSelected()) {
            return Status.DEFAULT;
        }

        if (selectionStatusVisible) {
            return Status.GRAYED_OUT;
        }

        return Status.DEFAULT;
    }

    // for test only
    HighlightingManager getHighlightingManager() {
        return highlightingManager;
    }

    public HoverModel getHoverModel() {
        return hoverModel;
    }

    public final PopupManager getPopupManager() {
        return popupManager;
    }

    public ResourceSet getResourceSet() {
        return resources;
    }

    public Object getResourceValue(String slotID) {
        return valueResolver.resolve(slotID, category, resources);
    }

    public boolean isHighlighted() {
        return highlighted;
    }

    public boolean isSelected() {
        return selected;
    }

    // TODO introduce partial highlighting
    public final void setHighlighted(boolean highlighted) {
        if (this.highlighted == highlighted) {
            return;
        }

        this.highlighted = highlighted;

        updateStyling();
    }

    public void setSelected(boolean selected) {
        if (this.selected == selected) {
            return;
        }

        this.selected = selected;
        updateStyling();
    }

    public void setSelectionStatusVisible(boolean visible) {
        if (this.selectionStatusVisible == visible) {
            return;
        }

        this.selectionStatusVisible = visible;
        updateStyling();
    }

    /**
     * Should be overridden by subclasses to apply correct styling to the visual
     * representation. Gets called whenever status (highlighting, selecting,
     * etc.) changes.
     * 
     * @param status
     *            Current status of resource item.
     */
    protected void setStatusStyling(Status status) {

    }

    /**
     * Should be overridden by subclasses to update the content of the visual
     * representation. Gets called whenever the underlying resource set changes.
     */
    protected void updateContent() {
    }

    protected void updateStyling() {
        setStatusStyling(calculateStatus());
    }
}