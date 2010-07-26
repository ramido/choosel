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

import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.ui.popup.PopupManager;
import org.thechiselgroup.choosel.client.views.DragEnabler;
import org.thechiselgroup.choosel.client.views.DragEnablerFactory;
import org.thechiselgroup.choosel.client.views.HoverModel;
import org.thechiselgroup.choosel.client.views.ResourceItem;
import org.thechiselgroup.choosel.client.views.ResourceItemValueResolver;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.Event;

public class ChartItem extends ResourceItem {

    private String[] colours = { "yellow", "orange", "steelblue" };

    private ChartViewContentDisplay view;

    private DragEnabler enabler;

    public ChartItem(String category, ResourceSet resources,
            ChartViewContentDisplay view, PopupManager popupManager,
            HoverModel hoverModel, ResourceItemValueResolver layerModel,
            DragEnablerFactory dragEnablerFactory) {

        super(category, resources, hoverModel, popupManager, layerModel);

        this.view = view;
        enabler = dragEnablerFactory.createDragEnabler(this);
    }

    public String getColour() {
        switch (calculateStatus()) {
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

    public void onEvent(Event e) {
        try {
            switch (e.getTypeInt()) {
            case Event.ONCLICK: {
                if (view != null) {
                    view.getCallback().switchSelection(getResourceSet());
                }
            }
                break;
            case Event.ONMOUSEMOVE: {
                popupManager.onMouseMove(e.getClientX(), e.getClientY());
                enabler.forwardMouseMove(e);
            }
                break;
            case Event.ONMOUSEDOWN: {
                popupManager.onMouseDown(e);
                enabler.forwardMouseDownWithEventPosition(e);
            }
                break;
            case Event.ONMOUSEOUT: {
                popupManager.onMouseOut(e.getClientX(), e.getClientY());
                getHighlightingManager().setHighlighting(false);
                enabler.forwardMouseOut(e);
            }
                break;
            case Event.ONMOUSEOVER: {
                popupManager.onMouseOver(e.getClientX(), e.getClientY());
                getHighlightingManager().setHighlighting(true);

            }
                break;
            case Event.ONMOUSEUP: {
                enabler.forwardMouseUp(e);
            }
                break;
            }
        } catch (RuntimeException ex) {
            Log.error(ex.getMessage(), ex);
            throw ex;
        }
    }

}
