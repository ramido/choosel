/*******************************************************************************
 * Copyright (C) 2011 Lars Grammel 
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
package org.thechiselgroup.choosel.example.workbench.client;

import java.util.ArrayList;
import java.util.List;

import org.thechiselgroup.choosel.core.client.ui.Colors;
import org.thechiselgroup.choosel.core.client.views.model.PreconfiguredViewContentDisplayFactory;
import org.thechiselgroup.choosel.core.client.views.model.ViewContentDisplayFactory;
import org.thechiselgroup.choosel.core.client.views.model.ViewContentDisplaysConfiguration;
import org.thechiselgroup.choosel.core.client.views.model.ViewItem.Subset;
import org.thechiselgroup.choosel.core.client.views.resolvers.FixedValueResolver;
import org.thechiselgroup.choosel.core.client.views.resolvers.SubsetDelegatingValueResolver;
import org.thechiselgroup.choosel.core.client.views.resolvers.ViewItemStatusResolver;
import org.thechiselgroup.choosel.core.client.views.resolvers.ViewItemStatusResolver.StatusRule;
import org.thechiselgroup.choosel.visualization_component.chart.client.barchart.BarChart;
import org.thechiselgroup.choosel.visualization_component.chart.client.barchart.BarChartViewContentDisplayFactory;
import org.thechiselgroup.choosel.visualization_component.chart.client.scatterplot.ScatterPlot;
import org.thechiselgroup.choosel.visualization_component.chart.client.scatterplot.ScatterPlotViewContentDisplayFactory;
import org.thechiselgroup.choosel.visualization_component.map.client.Map;
import org.thechiselgroup.choosel.visualization_component.map.client.MapViewContentDisplayFactory;
import org.thechiselgroup.choosel.visualization_component.text.client.TextViewContentDisplayFactory;
import org.thechiselgroup.choosel.visualization_component.timeline.client.TimeLine;
import org.thechiselgroup.choosel.visualization_component.timeline.client.TimeLineViewContentDisplayFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class ChooselExampleWorkbenchViewContentDisplaysConfigurationProvider
        implements Provider<ViewContentDisplaysConfiguration> {

    private List<ViewContentDisplayFactory> viewContentDisplayFactories = new ArrayList<ViewContentDisplayFactory>();

    @SuppressWarnings("unused")
    @Inject
    private void barChart(BarChartViewContentDisplayFactory originalFactory) {
        PreconfiguredViewContentDisplayFactory factory = new PreconfiguredViewContentDisplayFactory(
                originalFactory);

        factory.setSlotResolver(
                BarChart.BAR_COLOR,
                new ViewItemStatusResolver("barColorResolver",
                        Colors.STEELBLUE_C, StatusRule.fullOrPartial(
                                Colors.ORANGE_C, Subset.SELECTED)));
        factory.setSlotResolver(BarChart.BAR_BORDER_COLOR,
                new FixedValueResolver(Colors.STEELBLUE_C));

        factory.setSlotResolver(BarChart.PARTIAL_BAR_LENGTH,
                new SubsetDelegatingValueResolver("partialBarLength",
                        BarChart.BAR_LENGTH, Subset.HIGHLIGHTED));
        factory.setSlotResolver(BarChart.PARTIAL_BAR_COLOR,
                new FixedValueResolver(Colors.YELLOW_C));
        factory.setSlotResolver(BarChart.PARTIAL_BAR_BORDER_COLOR,
                new FixedValueResolver(Colors.STEELBLUE_C));

        viewContentDisplayFactories.add(factory);
    }

    @Override
    public ViewContentDisplaysConfiguration get() {
        ViewContentDisplaysConfiguration result = new ViewContentDisplaysConfiguration();
        for (ViewContentDisplayFactory factory : viewContentDisplayFactories) {
            result.register(factory);
        }
        return result;
    }

    @SuppressWarnings("unused")
    @Inject
    private void map(MapViewContentDisplayFactory factory) {
        PreconfiguredViewContentDisplayFactory preconfiguredFactory = new PreconfiguredViewContentDisplayFactory(
                factory);

        preconfiguredFactory.setSlotResolver(
                Map.COLOR,
                new ViewItemStatusResolver("mapColorResolver",
                        Colors.STEELBLUE_C.alpha(0.6), StatusRule
                                .fullOrPartial(Colors.YELLOW_C,
                                        Subset.HIGHLIGHTED),
                        StatusRule.fullOrPartial(Colors.ORANGE_C,
                                Subset.SELECTED)));
        preconfiguredFactory.setSlotResolver(Map.BORDER_COLOR,
                new FixedValueResolver(Colors.STEELBLUE_C));

        // TODO fix z-index
        preconfiguredFactory.setSlotResolver(Map.Z_INDEX,
                new FixedValueResolver(1));
        preconfiguredFactory.setSlotResolver(Map.RADIUS,
                new FixedValueResolver(5));

        viewContentDisplayFactories.add(preconfiguredFactory);
    }

    @SuppressWarnings("unused")
    @Inject
    private void scatterPlot(ScatterPlotViewContentDisplayFactory factory) {
        PreconfiguredViewContentDisplayFactory preconfiguredFactory = new PreconfiguredViewContentDisplayFactory(
                factory);

        preconfiguredFactory.setSlotResolver(ScatterPlot.SIZE,
                new FixedValueResolver(20));
        preconfiguredFactory.setSlotResolver(ScatterPlot.BORDER_COLOR,
                new FixedValueResolver(Colors.STEELBLUE_C));
        preconfiguredFactory.setSlotResolver(
                ScatterPlot.COLOR,
                new ViewItemStatusResolver("scatterplotColorResolver",
                        Colors.STEELBLUE_C.alpha(0.6), StatusRule
                                .fullOrPartial(Colors.YELLOW_C,
                                        Subset.HIGHLIGHTED),
                        StatusRule.fullOrPartial(Colors.ORANGE_C,
                                Subset.SELECTED)));

        viewContentDisplayFactories.add(preconfiguredFactory);
    }

    @SuppressWarnings("unused")
    @Inject
    private void text(TextViewContentDisplayFactory factory) {
        viewContentDisplayFactories.add(factory);
    }

    @SuppressWarnings("unused")
    @Inject
    private void timeLine(TimeLineViewContentDisplayFactory factory) {
        PreconfiguredViewContentDisplayFactory preconfiguredFactory = new PreconfiguredViewContentDisplayFactory(
                factory);

        preconfiguredFactory.setSlotResolver(TimeLine.BORDER_COLOR,
                new FixedValueResolver(Colors.STEELBLUE_C));
        preconfiguredFactory.setSlotResolver(
                TimeLine.COLOR,
                new ViewItemStatusResolver("timelineColorResolver",
                        Colors.STEELBLUE_C.alpha(0.6), StatusRule
                                .fullOrPartial(Colors.YELLOW_C,
                                        Subset.HIGHLIGHTED),
                        StatusRule.fullOrPartial(Colors.ORANGE_C,
                                Subset.SELECTED)));

        viewContentDisplayFactories.add(preconfiguredFactory);
    }
}