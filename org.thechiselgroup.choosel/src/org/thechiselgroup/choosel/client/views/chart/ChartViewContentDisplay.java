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

import org.thechiselgroup.choosel.client.persistence.Memento;
import org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget;
import org.thechiselgroup.choosel.client.views.AbstractViewContentDisplay;
import org.thechiselgroup.choosel.client.views.DragEnablerFactory;
import org.thechiselgroup.choosel.client.views.ResourceItem;
import org.thechiselgroup.choosel.client.views.SlotResolver;

import com.google.inject.Inject;

/**
 * An abstract ViewContentDisplay class which any Protovis chart's specific
 * ViewContentDisplay can extend.
 * 
 * @author bblashko
 * 
 */
public abstract class ChartViewContentDisplay extends
        AbstractViewContentDisplay {

    protected ChartWidget chartWidget;

    private DragEnablerFactory dragEnablerFactory;

    public boolean hadPartiallyHighlightedItemsOnLastUpdate = false;

    @Inject
    public ChartViewContentDisplay(DragEnablerFactory dragEnablerFactory) {
        this.dragEnablerFactory = dragEnablerFactory;
    }

    @Override
    public void checkResize() {
        chartWidget.checkResize();
    }

    // TODO push down: the actual chart needs to decide which slots are used
    // XXX currently inaccurate
    // @Override
    // public String[] getSlotIDs() {
    // return new String[] { SlotResolver.DESCRIPTION_SLOT,
    // SlotResolver.LABEL_SLOT, SlotResolver.COLOR_SLOT,
    // SlotResolver.DATE_SLOT, SlotResolver.MAGNITUDE_SLOT,
    // SlotResolver.X_COORDINATE_SLOT, SlotResolver.Y_COORDINATE_SLOT };
    // }
    @Override
    public String[] getSlotIDs() {
        return new String[] { SlotResolver.CHART_LABEL_SLOT,
                SlotResolver.CHART_VALUE_SLOT };
    }

    /**
     * @return whether or not there is one or more newly partially highlighted
     *         items while there was none before (or vice versa)
     */
    public boolean hasPartialHighlightStatusChanged() {
        return (!hadPartiallyHighlightedItemsOnLastUpdate && chartWidget
                .hasPartiallyHighlightedChartItems())
                || (hadPartiallyHighlightedItemsOnLastUpdate && !chartWidget
                        .hasPartiallyHighlightedChartItems());
    }

    // TODO implement
    @Override
    public void restore(Memento state) {
    }

    // TODO implement
    @Override
    public Memento save() {
        Memento state = new Memento();
        return state;
    }

    /**
     * A method that listens for any updates on any resource items relevant to
     * the chart. Chart only will get rendered or updated (depending on the
     * situation) once no matter how many resource items are being affected.
     */
    @Override
    public void update(Set<ResourceItem> addedResourceItems,
            Set<ResourceItem> updatedResourceItems,
            Set<ResourceItem> removedResourceItems) {

        for (ResourceItem resourceItem : addedResourceItems) {
            ChartItem chartItem = new ChartItem(this, dragEnablerFactory,
                    resourceItem);

            chartWidget.addChartItem(chartItem);

            resourceItem.setDisplayObject(chartItem);
        }

        for (ResourceItem resourceItem : removedResourceItems) {
            ChartItem chartItem = (ChartItem) resourceItem.getDisplayObject();

            // TODO remove once dispose is in place
            resourceItem.setDisplayObject(null);

            chartWidget.removeChartItem(chartItem);
        }

        /*
         * The updateChart method only gets called when necessary so as to
         * minimize program overhead.
         */
        // TODO needs improvement, can updates cause structural changes?
        if (!addedResourceItems.isEmpty() || !removedResourceItems.isEmpty()
                || hasPartialHighlightStatusChanged()) {
            chartWidget.updateChart();
        } else {
            chartWidget.renderChart();
        }

        hadPartiallyHighlightedItemsOnLastUpdate = chartWidget
                .hasPartiallyHighlightedChartItems();
    }
}