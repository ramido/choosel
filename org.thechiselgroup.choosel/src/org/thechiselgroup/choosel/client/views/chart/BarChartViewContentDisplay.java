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

import java.util.ArrayList;
import java.util.List;

import org.thechiselgroup.choosel.client.resolver.ResourceSetToValueResolver;
import org.thechiselgroup.choosel.client.resolver.ResourceToValueResolver;
import org.thechiselgroup.choosel.client.resolver.SimplePropertyValueResolver;
import org.thechiselgroup.choosel.client.resources.ResourceCategorizer;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ui.DetailsWidgetHelper;
import org.thechiselgroup.choosel.client.ui.popup.PopupManagerFactory;
import org.thechiselgroup.choosel.client.ui.widget.chart.BarChart;
import org.thechiselgroup.choosel.client.ui.widget.chart.BarChart.LayoutType;
import org.thechiselgroup.choosel.client.views.DragEnablerFactory;
import org.thechiselgroup.choosel.client.views.SlotResolver;
import org.thechiselgroup.choosel.client.views.ViewContentDisplayAction;
import org.thechiselgroup.choosel.client.views.ViewContentDisplayConfiguration;

import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class BarChartViewContentDisplay extends ChartViewContentDisplay {

    public class BarLayoutAction implements ViewContentDisplayAction {

        private LayoutType layout;

        public BarLayoutAction(LayoutType layout) {
            this.layout = layout;
        }

        @Override
        public void execute() {
            ((BarChart) chartWidget).setLayout(layout);
            chartWidget.updateChart();
        }

        @Override
        public String getLabel() {
            return layout.getName();
        }
    }

    private ChartCategorizer[] categorizers;

    @Inject
    public BarChartViewContentDisplay(PopupManagerFactory popupManagerFactory,
            DetailsWidgetHelper detailsWidgetHelper,
            DragEnablerFactory dragEnablerFactory,
            ResourceCategorizer resourceByTypeCategorizer) {

        super(popupManagerFactory, detailsWidgetHelper, dragEnablerFactory);

        categorizers = new ChartCategorizer[] {
                new ChartCategorizer(resourceByTypeCategorizer, "label"),
                new ChartCategorizer(resourceByTypeCategorizer, "description"), };
    }

    @Override
    public Widget createWidget() {
        chartWidget = new BarChart();
        return chartWidget;
    }

    @Override
    public List<ViewContentDisplayAction> getActions() {
        List<ViewContentDisplayAction> actions = new ArrayList<ViewContentDisplayAction>();

        for (LayoutType layout : LayoutType.values()) {
            actions.add(new BarLayoutAction(layout));
        }

        return actions;
    }

    @Override
    public List<ViewContentDisplayConfiguration> getConfigurations() {
        List<ViewContentDisplayConfiguration> actions = new ArrayList<ViewContentDisplayConfiguration>();

        for (final ChartCategorizer categorizer : categorizers) {
            actions.add(new ViewContentDisplayConfiguration() {
                @Override
                public void execute() {
                    // XXX This should be done in a better way
                    final ResourceToValueResolver resolver = new SimplePropertyValueResolver(
                            categorizer.getPropertyName());
                    ResourceSetToValueResolver multiCategorizer = new ResourceSetToValueResolver() {
                        @Override
                        public Object resolve(ResourceSet resources,
                                String category) {
                            return resolver.resolve(resources
                                    .getFirstResource());
                        }
                    };

                    getCallback().putResolver(SlotResolver.CHART_LABEL_SLOT,
                            multiCategorizer);

                    getCallback().setCategorizer(categorizer);
                    /*
                     * XXX this is a workaround for a bug - the labels should be
                     * updated when the resource item changes
                     */
                    // chartWidget.updateChart();
                }

                @Override
                public String getLabel() {
                    return categorizer.getPropertyName();
                }
            });
        }

        return actions;
    }

}