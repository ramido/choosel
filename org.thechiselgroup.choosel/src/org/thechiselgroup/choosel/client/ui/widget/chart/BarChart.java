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
import org.thechiselgroup.choosel.client.views.ResourceItem.Status;
import org.thechiselgroup.choosel.client.views.SlotResolver;
import org.thechiselgroup.choosel.client.views.chart.ChartItem;

// TODO right side ticks
public class BarChart extends ChartWidget {

    private static final int BORDER_HEIGHT = 20;

    private static final int BORDER_WIDTH = 20;

    private static final String GRIDLINE_SCALE_COLOR = Colors.GRAY_1;

    private static final String AXIS_SCALE_COLOR = Colors.GRAY_2;

    private double[] regularBarCounts;

    private double[] highlightedBarCounts;

    private double[] barCounts;

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
                highlightedBarCounts[i] = calculateHighlightedResources(i);
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

            regularBarCounts = new double[chartItems.size()];

            for (int i = 0; i < chartItems.size(); i++) {
                regularBarCounts[i] = calculateAllResources(i)
                        - calculateHighlightedResources(i);
            }

        }

        @Override
        public double f(ChartItem value, int i) {
            return regularBarCounts[i] * chartHeight / maxBarSize;
        }

    };

    private ProtovisFunctionDoubleWithCache fullBarHeight = new ProtovisFunctionDoubleWithCache() {

        @Override
        public void beforeRender() {
            if (chartItems.isEmpty()) {
                return;
            }

            barCounts = new double[chartItems.size()];

            for (int i = 0; i < chartItems.size(); i++) {
                barCounts[i] = calculateAllResources(i);
            }

        }

        @Override
        public double f(ChartItem value, int i) {
            return barCounts[i] * chartHeight / maxBarSize;
        }

    };

    private ProtovisFunctionDouble barWidth = new ProtovisFunctionDouble() {
        @Override
        public double f(ChartItem value, int i) {
            return chartWidth / (chartItems.size() * 2);
        }
    };

    private ProtovisFunctionDouble regularBarBottom = new ProtovisFunctionDouble() {
        @Override
        public double f(ChartItem value, int i) {
            return highlightedBarHeight.f(value, i) + highlightedBarBottom;
        }
    };

    private int highlightedBarBottom = BORDER_HEIGHT + 1;

    private ProtovisFunctionStringToString scaleStrokeStyle = new ProtovisFunctionStringToString() {
        @Override
        public String f(String value, int i) {
            return Double.parseDouble(value) == 0 ? AXIS_SCALE_COLOR
                    : GRIDLINE_SCALE_COLOR;
        }
    };

    private Bar regularBar;

    private Bar highlightedBar;

    private ProtovisFunctionDouble baselineLabelLeft = new ProtovisFunctionDouble() {
        @Override
        public double f(ChartItem value, int i) {
            return barLeft.f(value, i) + barWidth.f(value, i) / 2;
        }
    };

    private String baselineLabelTextAlign = "center";

    private int baselineLabelBottom = 5;

    private ProtovisFunctionString baselineLabelText = new ProtovisFunctionString() {
        @Override
        public String f(ChartItem value, int i) {
            return value.getResourceItem()
                    .getResourceValue(SlotResolver.DESCRIPTION_SLOT).toString();
        }
    };

    private ProtovisFunctionString highlightedBarLabelText = new ProtovisFunctionString() {
        @Override
        public String f(ChartItem value, int i) {
            return calculateHighlightedResources(i) < 1 ? null : Integer
                    .toString(calculateHighlightedResources(i));
        }
    };

    private ProtovisFunctionString regularBarLabelText = new ProtovisFunctionString() {
        @Override
        public String f(ChartItem value, int i) {
            return calculateAllResources(i) - calculateHighlightedResources(i) < 1 ? null
                    : Integer.toString(calculateAllResources(i)
                            - calculateHighlightedResources(i));
        }
    };

    private ProtovisFunctionString fullBarLabelText = new ProtovisFunctionString() {
        @Override
        public String f(ChartItem value, int i) {
            return Integer.toString(Math.max(calculateAllResources(i),
                    calculateHighlightedResources(i)));
        }
    };

    private String barTextBaseline = "top";

    private ProtovisFunctionString fullBarTextStyle = new ProtovisFunctionString() {
        @Override
        public String f(ChartItem value, int i) {
            return calculateHighlightedResources(i) == 0 ? "white" : "black";
        }
    };

    @Override
    protected void beforeRender() {
        highlightedBarHeight.beforeRender();
        regularBarHeight.beforeRender();
        fullBarHeight.beforeRender();
    }

    private void calculateChartVariables() {
        chartWidth = width - BORDER_WIDTH * 2;
        chartHeight = height - BORDER_HEIGHT * 2;
    }

    private void drawBar() {
        for (ChartItem chartItem : chartItems) {
            if (chartItem.getResourceItem().getStatus() == Status.PARTIALLY_HIGHLIGHTED
                    || chartItem.getResourceItem().getStatus() == Status.PARTIALLY_HIGHLIGHTED_SELECTED) {
                highlightedBar = chart.add(Bar.createBar())
                        .data(ArrayUtils.toJsArray(chartItems))
                        .bottom(highlightedBarBottom)
                        .height(highlightedBarHeight).left(barLeft)
                        .width(barWidth).fillStyle(Colors.YELLOW)
                        .strokeStyle(Colors.STEELBLUE);

                highlightedBar.anchor("top").add(Label.createLabel())
                        .textBaseline(barTextBaseline)
                        .text(highlightedBarLabelText);

                highlightedBar.add(Label.createLabel()).left(baselineLabelLeft)
                        .textAlign(baselineLabelTextAlign)
                        .bottom(baselineLabelBottom).text(baselineLabelText);

                regularBar = highlightedBar.add(Bar.createBar())
                        .bottom(regularBarBottom).height(regularBarHeight)
                        .fillStyle(partialHighlightingChartFillStyle);

                regularBar.anchor("top").add(Label.createLabel())
                        .textBaseline(barTextBaseline)
                        .text(regularBarLabelText).textStyle("white");
                return;
            }
        }

        regularBar = chart.add(Bar.createBar())
                .data(ArrayUtils.toJsArray(chartItems))
                .bottom(highlightedBarBottom).height(fullBarHeight)
                .left(barLeft).width(barWidth).fillStyle(chartFillStyle)
                .strokeStyle(Colors.STEELBLUE);

        regularBar.add(Label.createLabel()).left(baselineLabelLeft)
                .textAlign(baselineLabelTextAlign).bottom(baselineLabelBottom)
                .text(baselineLabelText);

        regularBar.anchor("top").add(Label.createLabel())
                .textBaseline(barTextBaseline).text(fullBarLabelText)
                .textStyle(fullBarTextStyle);

    }

    @Override
    public void drawChart() {
        assert chartItems.size() >= 1;

        calculateChartVariables();
        setChartParameters();

        maxBarSize = 0;
        for (int i = 0; i < chartItems.size(); i++) {
            int currentItem = Integer.parseInt(chartItems.get(i)
                    .getResourceItem()
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
        regularBar.event(eventType, handler);
    }

    private void setChartParameters() {
        chart.left(BORDER_WIDTH).top(BORDER_HEIGHT);
    }
}