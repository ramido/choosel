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
package org.thechiselgroup.choosel.client.views.chart;

import static com.google.gwt.query.client.GQuery.*;

import java.util.ArrayList;
import java.util.List;

import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.ui.popup.PopupManager;
import org.thechiselgroup.choosel.client.views.DragEnabler;
import org.thechiselgroup.choosel.client.views.DragEnablerFactory;
import org.thechiselgroup.choosel.client.views.IconResourceItem;
import org.thechiselgroup.choosel.client.views.Layer;
import org.thechiselgroup.choosel.client.views.SlotResolver;
import org.thechiselgroup.choosel.client.views.chart.ChartViewContentDisplay;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.query.client.Function;
import com.google.gwt.query.client.GQuery;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;

public class ChartItem extends IconResourceItem {

    private static final String CSS_GRAYED_OUT_CLASS = "grayedOut";

    // TODO move, combine with listview
    private static final String CSS_HIGHLIGHT_CLASS = "hover";

    private static final String CSS_SELECTED_CLASS = "selected";

    private List<String> elementIDs = new ArrayList<String>();

    private final Function mouseClickListener = new Function() {
	@Override
	public boolean f(Event e) {
	    onMouseClick(e);
	    return true;
	}
    };

    private final Function mouseOutListener = new Function() {
	@Override
	public boolean f(Event e) {
//	    onMouseOut();
	    return true;
	}
    };

    private final Function mouseOverListener = new Function() {
	@Override
	public boolean f(Event e) {
//	    onMouseOver();
	    return true;
	}
    }; 
    
    private class MarkerEventHandler implements ClickHandler, MouseOutHandler,
    MouseOverHandler {

        @Override
        public void onClick(ClickEvent event) {
//            callback.switchSelection(getResource());
        }
        
        @Override
        public void onMouseOut(MouseOutEvent event) {
            getPopupManager()
        	    .onMouseOut(event.getClientX(), event.getClientY());
            setHightlighted(false);
        }
        
        @Override
        public void onMouseOver(MouseOverEvent event) {
            Window.alert(""+event.getClientX());
            Window.alert(""+event.getClientY());
            event.getClientY();
            getPopupManager().onMouseOver(event.getClientX(),
		    event.getClientY());
            setHightlighted(true);
        }
    }
    
    private final ChartViewContentDisplay view;
    

    private DragEnablerFactory dragEnablerFactory;

    private MarkerEventHandler eventHandler;

    public ChartItem(final Resource individual,
	    ChartViewContentDisplay view, PopupManager popupManager,
	    ResourceSet hoverModel, Layer layerModel,
	    DragEnablerFactory dragEnablerFactory) {
	
	super(individual, hoverModel, popupManager, layerModel);
	
	this.view = view;
	this.dragEnablerFactory = dragEnablerFactory;

	this.eventHandler = new MarkerEventHandler();
	
    }

    private void addCssClass(String cssClass) {
	for (String elementID : elementIDs) {
	    if (elementID.startsWith("label")) {
		// TODO get index of element for similar highlighting on
		// overview band
		getGElement(elementID).addClass(cssClass);
	    }
	}
    }

    private void addToElementIDs(String elementID) {
	if (!elementIDs.contains(elementID)) {
	    elementIDs.add(elementID);
	}
    }

    private void applyIcon(String iconUrl) {

	for (String elementID : elementIDs) {
	    if (elementID.startsWith("icon")) {
		// TODO get index of element for similar highlighting on
		// overview band
		getGElement(elementID).children().attr("src", iconUrl);
	    }
	}
    }
    
    private GQuery getGElement(String elementID) {
	return $("#" + elementID);
    }

    private void onMouseClick(Event e) {
	view.getCallback().switchSelection(getResource());
    }

    public void onMouseOut(int x, int y) {
	popupManager.onMouseOut(x,y);
	hoverModel.remove(getResource());
    }

    public void onMouseOver(int x, int y) {
	popupManager.onMouseOver(x,y);
	hoverModel.add(getResource());
    }

    public void onPainted(String labelElementID, String iconElementID) {
	/*
	 * every time the event is repainted, we need to hook up our listeners
	 * again
	 */
	registerListeners(labelElementID);
	registerListeners(iconElementID);

	addToElementIDs(labelElementID);
	addToElementIDs(iconElementID);
    }

    private void registerListeners(String elementID) {
	GQuery element = getGElement(elementID);

	element.mouseover(mouseOverListener);
	element.mouseout(mouseOutListener);
	element.click(mouseClickListener);

	final DragEnabler enabler = dragEnablerFactory.createDragEnabler(this);
	element.mouseup(new Function() {
	    @Override
	    public boolean f(Event e) {
		enabler.forwardMouseUp(e);
		return false;
	    }
	});
	element.mouseout(new Function() {
	    @Override
	    public boolean f(Event e) {
		enabler.forwardMouseOut(e);
		return false;
	    }
	});
	element.mousemove(new Function() {
	    @Override
	    public boolean f(Event e) {
		enabler.forwardMouseMove(e);
		return false;
	    }
	});
	element.mousedown(new Function() {
	    @Override
	    public boolean f(Event e) {
		enabler.forwardMouseDown(e);
		return false;
	    }
	});
    }

    private void removeCssClass(String cssClass) {
	for (String elementID : elementIDs) {
	    if (elementID.startsWith("label")) {
		getGElement(elementID).removeClass(cssClass);
	    }
	}
    }

    @Override
    protected void setStatusStyling(Status status) {
	switch (status) {
	case HIGHLIGHTED_SELECTED: {
	    removeCssClass(CSS_GRAYED_OUT_CLASS);
	    addCssClass(CSS_SELECTED_CLASS);
	    addCssClass(CSS_HIGHLIGHT_CLASS);
	    applyIcon(getHighlightIconURL());
	}
	    break;
	case HIGHLIGHTED: {
	    removeCssClass(CSS_SELECTED_CLASS);
	    removeCssClass(CSS_GRAYED_OUT_CLASS);
	    addCssClass(CSS_HIGHLIGHT_CLASS);
	    applyIcon(getHighlightIconURL());
	}
	    break;
	case DEFAULT: {
	    removeCssClass(CSS_SELECTED_CLASS);
	    removeCssClass(CSS_GRAYED_OUT_CLASS);
	    removeCssClass(CSS_HIGHLIGHT_CLASS);
	    applyIcon(getDefaultIconURL());
	}
	    break;
	case GRAYED_OUT: {
	    removeCssClass(CSS_SELECTED_CLASS);
	    removeCssClass(CSS_HIGHLIGHT_CLASS);
	    addCssClass(CSS_GRAYED_OUT_CLASS);
	    applyIcon(getGrayedOutIconURL());
	}
	    break;
	case SELECTED: {
	    removeCssClass(CSS_GRAYED_OUT_CLASS);
	    removeCssClass(CSS_HIGHLIGHT_CLASS);
	    addCssClass(CSS_SELECTED_CLASS);
	    applyIcon(getSelectedIconURL());
	}
	    break;
	}
    }

}
