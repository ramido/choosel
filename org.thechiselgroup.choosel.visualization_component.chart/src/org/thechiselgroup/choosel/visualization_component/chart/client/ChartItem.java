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
package org.thechiselgroup.choosel.visualization_component.chart.client;

import org.thechiselgroup.choosel.core.client.ui.Colors;
import org.thechiselgroup.choosel.core.client.views.DragEnabler;
import org.thechiselgroup.choosel.core.client.views.DragEnablerFactory;
import org.thechiselgroup.choosel.core.client.views.ViewItem;
import org.thechiselgroup.choosel.core.client.views.ViewItem.Subset;
import org.thechiselgroup.choosel.core.client.views.slots.Slot;

import com.google.gwt.user.client.Event;

public class ChartItem {

    private ChartViewContentDisplay view;

    private DragEnabler enabler;

    private ViewItem viewItem;

    public ChartItem(ChartViewContentDisplay view,
            DragEnablerFactory dragEnablerFactory, ViewItem viewItem) {

        this.view = view;
        this.viewItem = viewItem;
        enabler = dragEnablerFactory.createDragEnabler(viewItem);
    }

    public String getColor() {
        switch (viewItem.getStatus()) {
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

    public ViewItem getViewItem() {
        return viewItem;
    }

    public double getViewItemValueAsNumber(Slot slot, Subset subset) {
        return (Double) viewItem.getResourceValue(slot, subset);
    }

    public ChartViewContentDisplay getView() {
        return view;
    }

    // TODO find better way to separate controller
    public void onEvent(Event e) {
        switch (e.getTypeInt()) {
        case Event.ONCLICK: {
            if (view != null) {
                view.getCallback().switchSelection(viewItem.getResourceSet());
            }
        }
            break;
        case Event.ONMOUSEMOVE: {
            viewItem.getPopupManager().onMouseMove(e.getClientX(),
                    e.getClientY());
            enabler.forwardMouseMove(e);
        }
            break;
        case Event.ONMOUSEDOWN: {
            viewItem.getPopupManager().onMouseDown(e);
            enabler.forwardMouseDownWithEventPosition(e);
        }
            break;
        case Event.ONMOUSEOUT: {
            viewItem.getPopupManager().onMouseOut(e.getClientX(),
                    e.getClientY());
            viewItem.getHighlightingManager().setHighlighting(false);
            enabler.forwardMouseOut(e);
        }
            break;
        case Event.ONMOUSEOVER: {
            viewItem.getPopupManager().onMouseOver(e.getClientX(),
                    e.getClientY());
            viewItem.getHighlightingManager().setHighlighting(true);
        }
            break;
        case Event.ONMOUSEUP: {
            enabler.forwardMouseUp(e);
        }
            break;
        }
    }

}
