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
import org.thechiselgroup.choosel.core.client.views.ViewItem;
import org.thechiselgroup.choosel.core.client.views.ViewItem.Subset;
import org.thechiselgroup.choosel.core.client.views.slots.Slot;

import com.google.gwt.user.client.Event;

public class ChartItem {

    private ViewItem viewItem;

    private ChartViewContentDisplay view;

    public ChartItem(ViewItem viewItem, ChartViewContentDisplay view) {
        this.viewItem = viewItem;
        this.view = view;
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

    public <T> T getSlotValue(Slot slot, Subset subset) {
        return viewItem.getSlotValue(slot, subset);
    }

    public double getSlotValueAsDouble(Slot slot) {
        return getSlotValueAsDouble(slot, Subset.ALL);
    }

    public double getSlotValueAsDouble(Slot slot, Subset subset) {
        return (Double) getSlotValue(slot, subset);
    }

    public ChartViewContentDisplay getView() {
        return view;
    }

    public ViewItem getViewItem() {
        return viewItem;
    }

    public void onEvent(Event e) {
        viewItem.onEvent(e);
    }

}
