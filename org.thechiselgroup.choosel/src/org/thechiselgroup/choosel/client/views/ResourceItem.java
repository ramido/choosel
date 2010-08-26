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
import org.thechiselgroup.choosel.client.resources.ResourcesAddedEvent;
import org.thechiselgroup.choosel.client.resources.ResourcesAddedEventHandler;
import org.thechiselgroup.choosel.client.resources.ResourcesRemovedEvent;
import org.thechiselgroup.choosel.client.resources.ResourcesRemovedEventHandler;
import org.thechiselgroup.choosel.client.ui.popup.PopupClosingEvent;
import org.thechiselgroup.choosel.client.ui.popup.PopupClosingHandler;
import org.thechiselgroup.choosel.client.ui.popup.PopupManager;
import org.thechiselgroup.choosel.client.util.Disposable;

import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;

// TODO separate out resource item controller
public class ResourceItem implements Disposable {

    public static enum HighlightStatus {

        NONE, PARTIAL, COMPLETE

    }

    public static enum SelectStatus {

        NONE, PARTIAL, COMPLETE

    }

    public static enum Status {

        DEFAULT, HIGHLIGHTED, HIGHLIGHTED_SELECTED, SELECTED, PARTIALLY_HIGHLIGHTED, PARTIALLY_HIGHLIGHTED_SELECTED, PARTIALLY_SELECTED

    }

    private String category;

    protected final HoverModel hoverModel;

    protected final PopupManager popupManager;

    // TODO update & paint on changes in resources!!!
    private final ResourceSet resources;

    private boolean selected;

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
        this.hoverModel = hoverModel; // TODO separate controller
        this.valueResolver = valueResolver;

        this.highlightedResources = new DefaultResourceSet();
        this.selectedResources = new DefaultResourceSet();

        initHighlighting();
        initPopupHighlighting();
        initResourceEventHandling(resources);

        updateContent();
    }

    public void addHighlightedResources(ResourceSet highlightedResources) {
        this.highlightedResources
                .addAll(calculateAffectedResources(highlightedResources));
    }

    public void addSelectedResources(ResourceSet selectedResources) {
        this.selectedResources
                .addAll(calculateAffectedResources(selectedResources));
    }

    private List<Resource> calculateAffectedResources(ResourceSet resources) {
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

    public Object getDisplayObject() {
        return displayObject;
    }

    /**
     * @return all resources in this resource item that are highlighted.
     *         Resources that are not contained in this resource item are not
     *         included in the result.
     */
    public ResourceSet getHighlightedResources() {
        assert resources.containsAll(highlightedResources);
        return highlightedResources;
    }

    /**
     * @return highlighting manager that manages the hightlighting for this
     *         visual representation of the resource item. For the popup, there
     *         is a separate highlighting manager.
     */
    public HighlightingManager getHighlightingManager() {
        return highlightingManager;
    }

    public HighlightStatus getHighlightStatus() {
        if (highlightedResources.isEmpty()) {
            return HighlightStatus.NONE;
        }

        if (highlightedResources.containsEqualResources(resources)) {
            return HighlightStatus.COMPLETE;
        }

        return HighlightStatus.PARTIAL;
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

    public Collection<Resource> getSelectedResources() {
        assert resources.containsAll(selectedResources);
        return selectedResources;
    }

    public SelectStatus getSelectStatus() {
        if (selectedResources.isEmpty()) {
            return SelectStatus.NONE;
        }

        if (selectedResources.containsEqualResources(resources)) {
            return SelectStatus.COMPLETE;
        }

        return SelectStatus.PARTIAL;
    }

    public Status getStatus() {
        if (getHighlightStatus() == HighlightStatus.COMPLETE
                && getSelectStatus() == SelectStatus.COMPLETE) {
            return Status.HIGHLIGHTED_SELECTED;
        }

        if (getHighlightStatus() == HighlightStatus.COMPLETE) {
            return Status.HIGHLIGHTED;
        }

        if (getSelectStatus() == SelectStatus.COMPLETE) {
            return Status.SELECTED;
        }

        if (getHighlightStatus() == HighlightStatus.PARTIAL
                && getSelectStatus() == SelectStatus.PARTIAL) {
            return Status.PARTIALLY_HIGHLIGHTED_SELECTED;
        }

        if (getHighlightStatus() == HighlightStatus.PARTIAL) {
            return Status.PARTIALLY_HIGHLIGHTED;
        }

        if (getSelectStatus() == SelectStatus.PARTIAL) {
            return Status.PARTIALLY_SELECTED;
        }

        return Status.DEFAULT;
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

    private void initResourceEventHandling(ResourceSet resources) {
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
    }

    public boolean isSelected() {
        return selected;
    }

    public void removeHighlightedResources(ResourceSet highlightedResources) {
        this.highlightedResources
                .removeAll(calculateAffectedResources(highlightedResources));
    }

    public void removeSelectedResources(ResourceSet selectedResources) {
        this.selectedResources
                .removeAll(calculateAffectedResources(selectedResources));
    }

    /**
     * The display object is an arbitrary objects that can be set by a view
     * content display. Usually it would the visual representation of this
     * resource item to facilitate fast lookup operations.
     * 
     * @param displayObject
     * 
     * @see #getDisplayObject()
     */
    public void setDisplayObject(Object displayObject) {
        this.displayObject = displayObject;
    }

    public void setSelected(boolean selected) {
        if (this.selected == selected) {
            return;
        }

        this.selected = selected;
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
        setStatusStyling(getStatus());
    }
}