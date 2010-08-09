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
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ui.DetailsWidgetHelper;
import org.thechiselgroup.choosel.client.ui.popup.PopupManager;
import org.thechiselgroup.choosel.client.ui.popup.PopupManagerFactory;
import org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget;
import org.thechiselgroup.choosel.client.views.AbstractViewContentDisplay;
import org.thechiselgroup.choosel.client.views.DragEnablerFactory;
import org.thechiselgroup.choosel.client.views.HoverModel;
import org.thechiselgroup.choosel.client.views.ResourceItem;
import org.thechiselgroup.choosel.client.views.ResourceItemValueResolver;
import org.thechiselgroup.choosel.client.views.SlotResolver;

import com.google.inject.Inject;

public abstract class ChartViewContentDisplay extends
        AbstractViewContentDisplay {

    protected ChartWidget chartWidget;

    private DragEnablerFactory dragEnablerFactory;

    @Inject
    public ChartViewContentDisplay(PopupManagerFactory popupManagerFactory,
            DetailsWidgetHelper detailsWidgetHelper,
            DragEnablerFactory dragEnablerFactory) {

        super(popupManagerFactory, detailsWidgetHelper);

        this.dragEnablerFactory = dragEnablerFactory;
    }

    @Override
    public void checkResize() {
        chartWidget.checkResize();
    }

    @Override
    public ResourceItem createResourceItem(ResourceItemValueResolver resolver,
            String category, ResourceSet resources, HoverModel hoverModel) {

        PopupManager popupManager = createPopupManager(resolver, resources);

        ChartItem chartItem = new ChartItem(category, resources, this,
                hoverModel, popupManager, resolver, dragEnablerFactory);

        return chartItem;
    }

    // TODO push down: the actual chart needs to decide which slots are used
    @Override
    public String[] getSlotIDs() {
        return new String[] { SlotResolver.DESCRIPTION_SLOT,
                SlotResolver.LABEL_SLOT, SlotResolver.COLOR_SLOT,
                SlotResolver.DATE_SLOT, SlotResolver.MAGNITUDE_SLOT,
                SlotResolver.X_COORDINATE_SLOT, SlotResolver.Y_COORDINATE_SLOT };
    }

    @Override
    public void removeResourceItem(ResourceItem chartItem) {
    }

    @Override
    public void restore(Memento state) {
    }

    @Override
    public Memento save() {
        Memento state = new Memento();
        return state;
    }

    @Override
    public void update(Set<ResourceItem> addedResourceItems,
            Set<ResourceItem> updatedResourceItems,
            Set<ResourceItem> removedResourceItems) {

        for (ResourceItem resourceItem : addedResourceItems) {
            ChartItem chartItem = (ChartItem) resourceItem;
            chartWidget.addChartItem(chartItem);
            resourceItem.setDisplayObject(resourceItem);
        }

        for (ResourceItem resourceItem : removedResourceItems) {
            ChartItem chartItem = (ChartItem) resourceItem.getDisplayObject();
            resourceItem.setDisplayObject(null); // TODO remove once dispose is
                                                 // in place
            chartWidget.removeChartItem(chartItem);
        }

        super.update(addedResourceItems, updatedResourceItems,
                removedResourceItems);

        // TODO needs improvement, can updates cause structural changes?
        if (!addedResourceItems.isEmpty() || !removedResourceItems.isEmpty()) {
            chartWidget.updateChart();
        } else {
            chartWidget.renderChart();
        }
    }
}