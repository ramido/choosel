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
package org.thechiselgroup.choosel.client.ui.widget.chart;

import org.thechiselgroup.choosel.client.ui.Colors;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Bar;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Label;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.ProtovisEventHandler;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.ProtovisFunctionDouble;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.ProtovisFunctionDoubleWithCache;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.ProtovisFunctionString;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.ProtovisFunctionStringToString;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Rule;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Scale;
import org.thechiselgroup.choosel.client.util.ArrayUtils;
import org.thechiselgroup.choosel.client.views.SlotResolver;
import org.thechiselgroup.choosel.client.views.chart.ChartItem;

// TODO bar chart should always start at 0
// TODO bottom border line
// TODO right side ticks
public class BarChart extends ChartWidget {

    private static final int BORDER_HEIGHT = 20;

    private static final int BORDER_WIDTH = 20;

    private static final String AXIS_SCALE_COLOR = "gray";

    private static final String GRIDLINE_SCALE_COLOR = "lightgray";

    private double[] barCounts;

    private double[] highlightedBarCounts;

    protected double chartHeight;

    protected double chartWidth;

    private double maxBarSize;

    private ProtovisFunctionDouble barLeft = new ProtovisFunctionDouble() {
        @Override
        public double f(ChartItem value, int index) {
            return barWidth.f(value, index) / 2 + index * chartWidth
                    / chartItems.size();
        }
    };

    private ProtovisFunctionDoubleWithCache highlightedBarHeight = new ProtovisFunctionDoubleWithCache() {

        @Override
        public void beforeRender() {
            if (chartItems.isEmpty()) {
                return;
            }

            highlightedBarCounts = new double[chartItems.size()];

            for (int i = 0; i < chartItems.size(); i++) {
                highlightedBarCounts[i] = chartItems.get(i)
                        .getHighlightedResources().size();
            }

        }

        @Override
        public double f(ChartItem value, int index) {
            return highlightedBarCounts[index] * chartHeight / maxBarSize;
        }

    };

    private ProtovisFunctionDoubleWithCache regularBarHeight = new ProtovisFunctionDoubleWithCache() {

        @Override
        public void beforeRender() {
            if (chartItems.isEmpty()) {
                return;
            }

            barCounts = new double[chartItems.size()];

            for (int i = 0; i < chartItems.size(); i++) {
                barCounts[i] = Integer.parseInt(chartItems.get(i)
                        .getResourceValue(SlotResolver.FONT_SIZE_SLOT)
                        .toString())
                        - chartItems.get(i).getHighlightedResources().size();
            }

        }

        @Override
        public double f(ChartItem value, int index) {
            return barCounts[index] * chartHeight / maxBarSize;
        }

    };

    private ProtovisFunctionDouble barWidth = new ProtovisFunctionDouble() {
        @Override
        public double f(ChartItem value, int index) {
            return chartWidth / (chartItems.size() * 2);
        }
    };

    private ProtovisFunctionDouble regularBarBottom = new ProtovisFunctionDouble() {
        @Override
        public double f(ChartItem value, int index) {
            return highlightedBarHeight.f(value, index) + highlightedBarBottom;
        }
    };

    private int highlightedBarBottom = BORDER_HEIGHT + 1;

    private ProtovisFunctionStringToString scaleStrokeStyle = new ProtovisFunctionStringToString() {
        @Override
        public String f(String value, int index) {
            return Double.parseDouble(value) == 0 ? AXIS_SCALE_COLOR
                    : GRIDLINE_SCALE_COLOR;
        }
    };

    private Bar regularBar;

    private Bar highlightedBar;

    private ProtovisFunctionDouble baselineLabelLeft = new ProtovisFunctionDouble() {
        @Override
        public double f(ChartItem value, int index) {
            return barLeft.f(value, index) + barWidth.f(value, index) / 2;
        }
    };

    private String baselineLabelTextAlign = "center";

    private int baselineLabelBottom = 5;

    private ProtovisFunctionString baselineLabelText = new ProtovisFunctionString() {
        @Override
        public String f(ChartItem value, int index) {
            return value.getResourceValue(SlotResolver.DESCRIPTION_SLOT)
                    .toString();
        }
    };

    private ProtovisFunctionString highlightedBarLabelText = new ProtovisFunctionString() {
        @Override
        public String f(ChartItem value, int index) {
            return chartItems.get(index).getHighlightedResources().size() < 1 ? null
                    : Integer.toString(chartItems.get(index)
                            .getHighlightedResources().size());
        }
    };

    private ProtovisFunctionString regularBarLabelText = new ProtovisFunctionString() {
        @Override
        public String f(ChartItem value, int index) {
            return Integer.parseInt(chartItems.get(index)
                    .getResourceValue(SlotResolver.FONT_SIZE_SLOT).toString())
                    - chartItems.get(index).getHighlightedResources().size() < 1 ? null
                    : Integer.toString(Integer.parseInt(chartItems.get(index)
                            .getResourceValue(SlotResolver.FONT_SIZE_SLOT)
                            .toString())
                            - chartItems.get(index).getHighlightedResources()
                                    .size());
        }
    };

    private String barTextBaseline = "top";

    @Override
    protected void beforeRender() {
        highlightedBarHeight.beforeRender();
        regularBarHeight.beforeRender();
    }

    private void calculateChartVariables() {
        chartWidth = width - BORDER_WIDTH * 2;
        chartHeight = height - BORDER_HEIGHT * 2;
    }

    private void drawBar() {
        highlightedBar = chart.add(Bar.createBar())
                .data(ArrayUtils.toJsArray(chartItems))
                .bottom(highlightedBarBottom).height(highlightedBarHeight)
                .left(barLeft).width(barWidth).fillStyle(Colors.YELLOW)
                .strokeStyle(Colors.STEELBLUE);

        highlightedBar.anchor("top").add(Label.createLabel())
                .textBaseline(barTextBaseline).text(highlightedBarLabelText);

        highlightedBar.add(Label.createLabel()).left(baselineLabelLeft)
                .textAlign(baselineLabelTextAlign).bottom(baselineLabelBottom)
                .text(baselineLabelText);

        regularBar = highlightedBar.add(Bar.createBar())
                .bottom(regularBarBottom).height(regularBarHeight)
                .fillStyle(Colors.STEELBLUE);

        regularBar.anchor("top").add(Label.createLabel())
                .textBaseline(barTextBaseline).text(regularBarLabelText)
                .textStyle("white");
    }

    @Override
    public void drawChart() {
        assert chartItems.size() >= 1;

        calculateChartVariables();
        setChartParameters();

        maxBarSize = 0;
        for (int i = 0; i < chartItems.size(); i++) {
            int currentItem = Integer.parseInt(chartItems.get(i)
                    .getResourceValue(SlotResolver.FONT_SIZE_SLOT).toString());
            if (maxBarSize < currentItem) {
                maxBarSize = currentItem;
            }
        }

        Scale scale = Scale.linear(maxBarSize, 0).range(0, chartHeight);
        drawScales(scale);
        drawBar();
    }

    protected void drawScales(Scale scale) {
        this.scale = scale;
        chart.add(Rule.createRule()).data(scale.ticks()).top(scale)
                .strokeStyle(scaleStrokeStyle).width(chartWidth).anchor("left")
                .add(Label.createLabel()).text(scaleLabelText);

        chart.add(Rule.createRule()).left(0).bottom(BORDER_HEIGHT)
                .strokeStyle(AXIS_SCALE_COLOR);

    }

    @Override
    protected void registerEventHandler(String eventType,
            ProtovisEventHandler handler) {

        // XXX this is a problematic solution to the problem that events get
        // automatically
        // fired (asynchronously) when the bar heights
        if (EVENT_TYPE_MOUSEOVER.equals(eventType)) {
            regularBar.event(eventType, handler);
        } else {
            highlightedBar.event(eventType, handler);
        }
    }

    private void setChartParameters() {
        chart.left(BORDER_WIDTH).top(BORDER_HEIGHT);
    }
}