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

import org.thechiselgroup.choosel.core.client.resources.DataType;
import org.thechiselgroup.choosel.core.client.ui.Colors;
import org.thechiselgroup.choosel.core.client.views.DefaultViewContentDisplaysConfigurationProvider;
import org.thechiselgroup.choosel.core.client.views.model.ViewContentDisplayConfiguration;
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

public class ChooselExampleWorkbenchViewContentDisplaysConfigurationProvider
        extends DefaultViewContentDisplaysConfigurationProvider {

    @SuppressWarnings("unused")
    @Inject
    private void barChart(BarChartViewContentDisplayFactory originalFactory) {
        ViewContentDisplayConfiguration configuration = new ViewContentDisplayConfiguration(
                originalFactory);

        configuration.setSlotResolver(
                BarChart.BAR_COLOR,
                new ViewItemStatusResolver(Colors.STEELBLUE_C, StatusRule
                        .fullOrPartial(Colors.ORANGE_C, Subset.SELECTED)));
        configuration.setSlotResolver(BarChart.BAR_BORDER_COLOR,
                new FixedValueResolver(Colors.STEELBLUE_C, DataType.COLOR));

        configuration.setSlotResolver(BarChart.PARTIAL_BAR_LENGTH,
                new SubsetDelegatingValueResolver(BarChart.BAR_LENGTH,
                        Subset.HIGHLIGHTED));
        configuration.setSlotResolver(BarChart.PARTIAL_BAR_COLOR,
                new FixedValueResolver(Colors.YELLOW_C, DataType.COLOR));
        configuration.setSlotResolver(BarChart.PARTIAL_BAR_BORDER_COLOR,
                new FixedValueResolver(Colors.STEELBLUE_C, DataType.COLOR));

        add(configuration);
    }

    @SuppressWarnings("unused")
    @Inject
    private void map(MapViewContentDisplayFactory factory) {
        ViewContentDisplayConfiguration configuration = new ViewContentDisplayConfiguration(
                factory);

        configuration.setSlotResolver(
                Map.COLOR,
                new ViewItemStatusResolver(Colors.STEELBLUE_C.alpha(0.6),
                        StatusRule.fullOrPartial(Colors.YELLOW_C,
                                Subset.HIGHLIGHTED), StatusRule.fullOrPartial(
                                Colors.ORANGE_C, Subset.SELECTED)));
        configuration.setSlotResolver(Map.BORDER_COLOR, new FixedValueResolver(
                Colors.STEELBLUE_C, DataType.COLOR));

        // TODO fix z-index
        configuration.setSlotResolver(Map.Z_INDEX, new FixedValueResolver(1,
                DataType.NUMBER));
        configuration.setSlotResolver(Map.RADIUS, new FixedValueResolver(5,
                DataType.NUMBER));

        add(configuration);
    }

    @SuppressWarnings("unused")
    @Inject
    private void scatterPlot(ScatterPlotViewContentDisplayFactory factory) {
        ViewContentDisplayConfiguration configuration = new ViewContentDisplayConfiguration(
                factory);

        configuration.setSlotResolver(ScatterPlot.SIZE, new FixedValueResolver(
                20, DataType.NUMBER));
        configuration.setSlotResolver(ScatterPlot.BORDER_COLOR,
                new FixedValueResolver(Colors.STEELBLUE_C, DataType.COLOR));
        configuration.setSlotResolver(
                ScatterPlot.COLOR,
                new ViewItemStatusResolver(Colors.STEELBLUE_C.alpha(0.6),
                        StatusRule.fullOrPartial(Colors.YELLOW_C,
                                Subset.HIGHLIGHTED), StatusRule.fullOrPartial(
                                Colors.ORANGE_C, Subset.SELECTED)));

        add(configuration);
    }

    @SuppressWarnings("unused")
    @Inject
    private void text(TextViewContentDisplayFactory factory) {
        add(factory);
    }

    @SuppressWarnings("unused")
    @Inject
    private void timeLine(TimeLineViewContentDisplayFactory factory) {
        ViewContentDisplayConfiguration configuration = new ViewContentDisplayConfiguration(
                factory);

        configuration.setSlotResolver(TimeLine.BORDER_COLOR,
                new FixedValueResolver(Colors.STEELBLUE_C, DataType.COLOR));
        configuration.setSlotResolver(
                TimeLine.COLOR,
                new ViewItemStatusResolver(Colors.STEELBLUE_C.alpha(0.6),
                        StatusRule.fullOrPartial(Colors.YELLOW_C,
                                Subset.HIGHLIGHTED), StatusRule.fullOrPartial(
                                Colors.ORANGE_C, Subset.SELECTED)));

        add(configuration);
    }
}