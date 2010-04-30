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

import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.ui.popup.PopupClosingEvent;
import org.thechiselgroup.choosel.client.ui.popup.PopupClosingHandler;
import org.thechiselgroup.choosel.client.ui.popup.PopupManager;

import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;

public abstract class ResourceItem {

    public class HighlightingManager implements MouseOverHandler,
	    MouseOutHandler, PopupClosingHandler {

	@Override
	public void onMouseOut(MouseOutEvent e) {
	    setHightlighted(false);
	}

	@Override
	public void onMouseOver(MouseOverEvent e) {
	    setHightlighted(true);
	}

	@Override
	public void onPopupClosing(PopupClosingEvent event) {
	    setHightlighted(false);
	}

    }

    public static enum Status {

	DEFAULT, GRAYED_OUT, HIGHLIGHTED, HIGHLIGHTED_SELECTED, SELECTED

    }

    private boolean highlighted = false;

    private HighlightingManager highlightingManager;

    protected final ResourceSet hoverModel;

    private final Layer layer;

    protected final PopupManager popupManager;

    private final Resource resource;

    private boolean selected;

    private boolean selectionStatusVisible;

    public ResourceItem(Resource resource, ResourceSet hoverModel,
	    PopupManager popupManager, Layer layer) {

	assert resource != null;
	assert hoverModel != null;
	assert popupManager != null;
	assert layer != null;

	this.resource = resource;
	this.popupManager = popupManager;
	this.hoverModel = hoverModel;
	this.layer = layer;

	highlightingManager = new HighlightingManager();

	this.popupManager.addPopupMouseOverHandler(highlightingManager);
	this.popupManager.addPopupMouseOutHandler(highlightingManager);
	this.popupManager.addPopupClosingHandler(highlightingManager);
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

    public final PopupManager getPopupManager() {
	return popupManager;
    }

    public Resource getResource() {
	return resource;
    }

    public Object getResourceValue(String slotID) {
	return layer.getValue(slotID, getResource());
    }

    public boolean isHighlighted() {
	return highlighted;
    }

    public boolean isSelected() {
	return selected;
    }

    public void setHightlighted(boolean highlighted) {
	if (this.highlighted == highlighted) {
	    return;
	}

	this.highlighted = highlighted;

	if (highlighted) {
	    hoverModel.add(resource);
	} else {
	    hoverModel.remove(resource);
	}

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

    protected abstract void setStatusStyling(Status status);

    protected void updateStyling() {
	setStatusStyling(calculateStatus());
    }
}