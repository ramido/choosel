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

import java.util.List;

import org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants;
import org.thechiselgroup.choosel.client.persistence.Memento;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ui.DetailsWidgetHelper;
import org.thechiselgroup.choosel.client.ui.popup.PopupManager;
import org.thechiselgroup.choosel.client.ui.popup.PopupManagerFactory;
import org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget;
import org.thechiselgroup.choosel.client.views.AbstractViewContentDisplay;
import org.thechiselgroup.choosel.client.views.DragEnablerFactory;
import org.thechiselgroup.choosel.client.views.ResourceItem;
import org.thechiselgroup.choosel.client.views.ResourceItemValueResolver;
import org.thechiselgroup.choosel.client.views.SlotResolver;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public abstract class ChartViewContentDisplay extends
        AbstractViewContentDisplay {

    public static interface Display {

        void addItem(ChartItem chartItem);

        void addStyleName(ChartItem chartItem, String cssClass);

        void removeIndividualItem(ChartItem chartItem);

        void removeStyleName(ChartItem chartItem, String cssClass);

    }

    protected ChartWidget chartWidget;

    private DragEnablerFactory dragEnablerFactory;

    @Inject
    public ChartViewContentDisplay(
            PopupManagerFactory popupManagerFactory,
            DetailsWidgetHelper detailsWidgetHelper,
            @Named(ChooselInjectionConstants.HOVER_MODEL) ResourceSet hoverModel,
            DragEnablerFactory dragEnablerFactory) {

        super(popupManagerFactory, detailsWidgetHelper, hoverModel);

        this.dragEnablerFactory = dragEnablerFactory;
    }

    @Override
    public void checkResize() {
        chartWidget.checkResize();
    }

    @Override
    public ResourceItem createResourceItem(ResourceItemValueResolver resolver,
            String category, ResourceSet resources) {

        PopupManager popupManager = createPopupManager(resolver, resources);

        ChartItem chartItem = new ChartItem(category, resources, this,
                popupManager, hoverModel, resolver, dragEnablerFactory);

        chartWidget.addEvent(chartItem);

        return chartItem;
    }

    public ChartWidget getChartWidget() {
        return chartWidget;
    }

    @Override
    public String[] getSlotIDs() {
        return new String[] { SlotResolver.DESCRIPTION_SLOT,
                SlotResolver.LABEL_SLOT, SlotResolver.COLOR_SLOT,
                SlotResolver.DATE_SLOT, SlotResolver.MAGNITUDE_SLOT,
                SlotResolver.X_COORDINATE_SLOT, SlotResolver.Y_COORDINATE_SLOT };
    }

    @Override
    public void removeResourceItem(ResourceItem resourceItem) {
        // chartWidget.removeEvent(resourceItem);
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
    protected void showHover(List<Resource> resources, boolean showHover) {
        if (resources.isEmpty()) {
            return;
        }
        for (Resource resource : resources) {
            if (!callback.containsResource(resource)) {
                return;
            }

            List<ResourceItem> resourceItems = callback
                    .getResourceItems(resource);
            for (ResourceItem resourceItem : resourceItems) {
                resourceItem.setHighlighted(showHover);
            }
        }
        chartWidget.renderChart();
    }
}