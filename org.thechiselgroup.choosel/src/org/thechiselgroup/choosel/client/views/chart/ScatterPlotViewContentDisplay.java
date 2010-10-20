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

import java.util.Set;

import org.thechiselgroup.choosel.client.ui.widget.chart.ScatterPlot;
import org.thechiselgroup.choosel.client.views.DragEnablerFactory;
import org.thechiselgroup.choosel.client.views.ResourceItem;
import org.thechiselgroup.choosel.client.views.Slot;
import org.thechiselgroup.choosel.client.views.SlotResolver;

import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class ScatterPlotViewContentDisplay extends ChartViewContentDisplay {

    @Inject
    public ScatterPlotViewContentDisplay(DragEnablerFactory dragEnablerFactory) {
        super(dragEnablerFactory);
    }

    @Override
    public Widget createWidget() {
        chartWidget = new ScatterPlot();
        return chartWidget;
    }

    @Override
    public Slot[] getSlots() {
        return new Slot[] { SlotResolver.X_COORDINATE_SLOT,
                SlotResolver.Y_COORDINATE_SLOT, SlotResolver.CHART_LABEL_SLOT };
    }

    @Override
    public void update(Set<ResourceItem> addedResourceItems,
            Set<ResourceItem> updatedResourceItems,
            Set<ResourceItem> removedResourceItems, Set<Slot> changedSlots) {

        // TODO re-enable
        // if (!changedSlots.isEmpty()) {
        ((ScatterPlot) chartWidget).setYAxisLabel(callback
                .getSlotResolverDescription(SlotResolver.Y_COORDINATE_SLOT));
        ((ScatterPlot) chartWidget).setXAxisLabel(callback
                .getSlotResolverDescription(SlotResolver.X_COORDINATE_SLOT));
        // }

        super.update(addedResourceItems, updatedResourceItems,
                removedResourceItems, changedSlots);
    }
}