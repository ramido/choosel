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
import org.thechiselgroup.choosel.client.views.DragEnablerFactory;
import org.thechiselgroup.choosel.client.views.Layer;
import org.thechiselgroup.choosel.client.views.ResourceItem;

public class ChartItem extends ResourceItem {

    private DragEnablerFactory dragEnablerFactory;

    public boolean highlighted = false;
    
    protected String[] colours = new String[]{"yellow", "orange","brown","blue"};

    private final ChartViewContentDisplay view;

    public ChartItem(final Resource individual, ChartViewContentDisplay view,
	    PopupManager popupManager, ResourceSet hoverModel,
	    Layer layerModel, DragEnablerFactory dragEnablerFactory) {
	super(individual, hoverModel, popupManager, layerModel);

	this.view = view;
	this.dragEnablerFactory = dragEnablerFactory;
    }

    public void onMouseClick() {
	view.getCallback().switchSelection(getResource());
    }

    public void onMouseDown() {
//	final DragEnabler enabler = dragEnablerFactory.createDragEnabler(this);
    }

    public void onMouseOut(int x, int y) {
	popupManager.onMouseOut(x, y);
	hoverModel.remove(getResource());
    }

    public void onMouseOver(int x, int y) {
	popupManager.onMouseOver(x, y);
	hoverModel.add(getResource());
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
