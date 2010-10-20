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

import org.thechiselgroup.choosel.client.ui.widget.chart.BarChart;
import org.thechiselgroup.choosel.client.ui.widget.chart.BarChart.LayoutType;
import org.thechiselgroup.choosel.client.views.DragEnablerFactory;
import org.thechiselgroup.choosel.client.views.ResourceItem;
import org.thechiselgroup.choosel.client.views.Slot;
import org.thechiselgroup.choosel.client.views.SlotResolver;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class BarChartViewContentDisplay extends ChartViewContentDisplay {

    @Inject
    public BarChartViewContentDisplay(DragEnablerFactory dragEnablerFactory) {
        super(dragEnablerFactory);
    }

    @Override
    public Widget createWidget() {
        chartWidget = new BarChart();
        return chartWidget;
    }

    @Override
    public Widget getConfigurationWidget() {
        FlowPanel panel = new FlowPanel();

        final ListBox layoutBox = new ListBox(false);
        layoutBox.setVisibleItemCount(1);
        for (LayoutType layout : LayoutType.values()) {
            layoutBox.addItem(layout.getName(), layout.toString());
        }
        layoutBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                LayoutType layout = LayoutType.valueOf(layoutBox
                        .getValue(layoutBox.getSelectedIndex()));
                ((BarChart) chartWidget).setLayout(layout);
                chartWidget.updateChart();
            }
        });
        panel.add(layoutBox);

        return panel;
    }

    @Override
    public Slot[] getSlots() {
        return new Slot[] { SlotResolver.CHART_LABEL_SLOT,
                SlotResolver.CHART_VALUE_SLOT };
    }

    @Override
    public void update(Set<ResourceItem> addedResourceItems,
            Set<ResourceItem> updatedResourceItems,
            Set<ResourceItem> removedResourceItems, Set<Slot> changedSlots) {

        // TODO re-enable
        // if (!changedSlots.isEmpty()) {
        ((BarChart) chartWidget).setMeasurementLabel(callback
                .getSlotResolverDescription(SlotResolver.CHART_VALUE_SLOT));
        // }

        super.update(addedResourceItems, updatedResourceItems,
                removedResourceItems, changedSlots);
    }
}