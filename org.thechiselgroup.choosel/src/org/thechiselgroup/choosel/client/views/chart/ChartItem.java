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

import org.thechiselgroup.choosel.client.ui.Colors;
import org.thechiselgroup.choosel.client.views.DragEnabler;
import org.thechiselgroup.choosel.client.views.DragEnablerFactory;
import org.thechiselgroup.choosel.client.views.ResourceItem;

import com.google.gwt.user.client.Event;

public class ChartItem {

    private ChartViewContentDisplay view;

    private DragEnabler enabler;

    private ResourceItem resourceItem;

    public ChartItem(ChartViewContentDisplay view,
            DragEnablerFactory dragEnablerFactory, ResourceItem resourceItem) {

        this.view = view;
        this.resourceItem = resourceItem;
        this.enabler = dragEnablerFactory.createDragEnabler(resourceItem);
    }

    public String getColour() {
        switch (resourceItem.getStatus()) {
        case PARTIALLY_HIGHLIGHTED:
        case PARTIALLY_HIGHLIGHTED_SELECTED:
        case HIGHLIGHTED_SELECTED:
        case HIGHLIGHTED:
            return Colors.YELLOW;
        case DEFAULT:
            return Colors.STEELBLUE;
        case PARTIALLY_SELECTED:
        case SELECTED:
            return Colors.ORANGE;
        }
        throw new RuntimeException("No colour available");
    }

    public ResourceItem getResourceItem() {
        return resourceItem;
    }

    public ChartViewContentDisplay getView() {
        return view;
    }

    // TODO find better way to separate controller
    public void onEvent(Event e) {
        switch (e.getTypeInt()) {
        case Event.ONCLICK: {
            if (view != null) {
                view.getCallback().switchSelection(
                        resourceItem.getResourceSet());
            }
        }
            break;
        case Event.ONMOUSEMOVE: {
            resourceItem.getPopupManager().onMouseMove(e.getClientX(),
                    e.getClientY());
            enabler.forwardMouseMove(e);
        }
            break;
        case Event.ONMOUSEDOWN: {
            resourceItem.getPopupManager().onMouseDown(e);
            enabler.forwardMouseDownWithEventPosition(e);
        }
            break;
        case Event.ONMOUSEOUT: {
            resourceItem.getPopupManager().onMouseOut(e.getClientX(),
                    e.getClientY());
            resourceItem.getHighlightingManager().setHighlighting(false);
            enabler.forwardMouseOut(e);
        }
            break;
        case Event.ONMOUSEOVER: {
            resourceItem.getPopupManager().onMouseOver(e.getClientX(),
                    e.getClientY());
            resourceItem.getHighlightingManager().setHighlighting(true);

        }
            break;
        case Event.ONMOUSEUP: {
            enabler.forwardMouseUp(e);
        }
            break;
        }
    }

}
