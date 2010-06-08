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

import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.ui.popup.PopupManager;
import org.thechiselgroup.choosel.client.views.DragEnabler;
import org.thechiselgroup.choosel.client.views.DragEnablerFactory;
import org.thechiselgroup.choosel.client.views.Layer;
import org.thechiselgroup.choosel.client.views.ResourceItem;

import com.google.gwt.user.client.Event;

public class ChartItem extends ResourceItem {

    public boolean highlighted = false;
    
    protected String[] colours = {"yellow", "orange", "steelblue"};

    private final ChartViewContentDisplay view;

    private DragEnabler enabler;
    
    public ChartItem(final Resource individual, ChartViewContentDisplay view,
	    PopupManager popupManager, ResourceSet hoverModel,
	    Layer layerModel, DragEnablerFactory dragEnablerFactory) {
	super(individual, hoverModel, popupManager, layerModel);

	this.view = view;
	enabler = dragEnablerFactory.createDragEnabler(this);
    }
    
    public void onEvent(Event e) {
	switch(e.getTypeInt()) {
	case Event.ONMOUSEDOWN: {
	    popupManager.onMouseDown(e.getClientX(), e.getClientY());
	    enabler.forwardMouseDown(e);
	}
	    break;
	case Event.ONMOUSEMOVE: {
	    popupManager.onMouseMove(e.getClientX(), e.getClientY());
	    enabler.forwardMouseMove(e);
	}
	    break;
	case Event.ONMOUSEOUT: {
	    popupManager.onMouseOut(e.getClientX(), e.getClientY());
	    hoverModel.remove(getResource());
	    enabler.forwardMouseOut(e);
	}
	    break;
	case Event.ONMOUSEOVER: {
	    popupManager.onMouseOver(e.getClientX(), e.getClientY());
	    hoverModel.add(getResource());
	}
	    break;
	case Event.ONMOUSEUP: {
	    enabler.forwardMouseUp(e);
	}
	    break;
	}
    }
    
    @Override
    protected void setStatusStyling(Status status) {
	view.getChartWidget().renderChart();
    }
    
    public String getColour() {
	switch(calculateStatus()) {
	case HIGHLIGHTED_SELECTED: 
	case HIGHLIGHTED: 
	    return colours[0];
	case GRAYED_OUT: 
	case DEFAULT:
	    return colours[2];
	case SELECTED:
	    return colours[1];
	}
	throw new RuntimeException("No colour available");
    }

}
