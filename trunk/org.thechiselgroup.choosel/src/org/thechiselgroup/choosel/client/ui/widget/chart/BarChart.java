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

/* TODO refactor such that the differences between vertical and horizontal bar chart
 * are extracted and the commonalities are kept.
 */
// TODO right side ticks
public class BarChart extends ChartWidget {

    public static enum LayoutType {

        VERTICAL("Vertical"), HORIZONTAL("Horizontal"), AUTOMATIC("Automatic");

        private String name;

        LayoutType(String name) {
            this.name = name;
        }

        private double getBarLengthSpace(int chartHeight, int chartWidth) {
            return isVerticalBarChart(chartHeight, chartWidth) ? chartHeight
                    : chartWidth;
        }

        private double getBarWidthSpace(int chartHeight, int chartWidth) {
            return isVerticalBarChart(chartHeight, chartWidth) ? chartWidth
                    : chartHeight;
        }

        public String getName() {
            return name;
        }

        private boolean isVerticalBarChart(int chartHeight, int chartWidth) {
            return this == LayoutType.VERTICAL
                    || (this == LayoutType.AUTOMATIC && chartHeight < chartWidth);
        }

    }

    private static final int BORDER_HEIGHT = 20;

    private static final int BORDER_WIDTH = 20;

    private double[] regularBarCounts;

    private double[] highlightedBarCounts;

    private double[] barCounts;

    protected int chartHeight;

    protected int chartWidth;

    private double maxBarSize;

    private ProtovisFunctionDouble barStart = new ProtovisFunctionDouble() {
        @Override
        public double f(ChartItem value, int index) {
            return barWidth.f(value, index) / 2 + index
                    * layout.getBarWidthSpace(chartHeight, chartWidth)
                    / chartItems.size();
        }
    };

    private ProtovisFunctionDoubleWithCache highlightedBarLength = new ProtovisFunctionDoubleWithCache() {

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
            return highlightedBarCounts[index]
                    * layout.getBarLengthSpace(chartHeight, chartWidth)
                    / maxBarSize;
        }

    };

    private ProtovisFunctionDoubleWithCache regularBarLength = new ProtovisFunctionDoubleWithCache() {

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
            return regularBarCounts[i]
                    * layout.getBarLengthSpace(chartHeight, chartWidth)
                    / maxBarSize;
        }

    };

    private ProtovisFunctionDoubleWithCache fullBarLength = new ProtovisFunctionDoubleWithCache() {

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
            return barCounts[i]
                    * layout.getBarLengthSpace(chartHeight, chartWidth)
                    / maxBarSize;
        }

    };

    private ProtovisFunctionDouble barWidth = new ProtovisFunctionDouble() {
        @Override
        public double f(ChartItem value, int i) {
            return layout.getBarWidthSpace(chartHeight, chartWidth)
                    / (chartItems.size() * 2);
        }
    };

    private ProtovisFunctionDouble regularBarBase = new ProtovisFunctionDouble() {
        @Override
        public double f(ChartItem value, int i) {
            return highlightedBarLength.f(value, i) + highlightedBarBase;
        }
    };

    private int highlightedBarBase = 0;

    private ProtovisFunctionStringToString scaleStrokeStyle = new ProtovisFunctionStringToString() {
        @Override
        public String f(String value, int i) {
            return Double.parseDouble(value) == 0 ? Colors.GRAY_2
                    : Colors.GRAY_1;
        }
    };

    private Bar regularBar;

    private Bar highlightedBar;

    private ProtovisFunctionDouble baselineLabelStart = new ProtovisFunctionDouble() {
        @Override
        public double f(ChartItem value, int i) {
            return barStart.f(value, i) + barWidth.f(value, i) / 2;
        }
    };

    private String baselineLabelTextAlign = "center";

    private int baselineLabelLength = -15;

    private ProtovisFunctionString baselineLabelText = new ProtovisFunctionString() {
        @Override
        public String f(ChartItem value, int i) {
            return value.getResourceItem()
                    .getResourceValue(SlotResolver.CHART_LABEL_SLOT).toString();
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

    protected LayoutType layout = LayoutType.HORIZONTAL;

    private static final int HORIZONTAL_BAR_LABEL_EXTRA_MARGIN = 20;

    @Override
    protected void beforeRender() {
        highlightedBarLength.beforeRender();
        regularBarLength.beforeRender();
        fullBarLength.beforeRender();
    }

    private void calculateChartVariables() {
        if (layout.isVerticalBarChart(chartHeight, chartWidth)) {
            chartWidth = width - BORDER_WIDTH * 2;
        } else {
            chartWidth = width - BORDER_WIDTH * 2
                    - HORIZONTAL_BAR_LABEL_EXTRA_MARGIN;
        }
        chartHeight = height - BORDER_HEIGHT * 2;
    }

    private void calculateMaxBarSize() {
        maxBarSize = 0;
        for (int i = 0; i < chartItems.size(); i++) {
            int currentItem = Integer
                    .parseInt(chartItems.get(i).getResourceItem()
                            .getResourceValue(SlotResolver.CHART_VALUE_SLOT)
                            .toString());
            if (maxBarSize < currentItem) {
                maxBarSize = currentItem;
            }
        }
    }

    @Override
    public void drawChart() {
        assert chartItems.size() >= 1;

        calculateChartVariables();
        setChartParameters();

        calculateMaxBarSize();

        if (layout.isVerticalBarChart(chartHeight, chartWidth)) {
            Scale scale = Scale.linear(0, maxBarSize).range(0, chartHeight);
            drawVerticalBarScales(scale);
            drawVerticalBarChart();
        } else {
            Scale scale = Scale.linear(0, maxBarSize).range(0, chartWidth);
            drawHorizontalBarScales(scale);
            drawHorizontalBarChart();
        }
    }

    private void drawHorizontalBarChart() {
        for (ChartItem chartItem : chartItems) {
            if (isPartiallyHighlighted(chartItem)) {
                highlightedBar = chart.add(Bar.createBar())
                        .data(ArrayUtils.toJsArray(chartItems))
                        .left(highlightedBarBase).width(highlightedBarLength)
                        .bottom(barStart).height(barWidth)
                        .fillStyle(Colors.YELLOW).strokeStyle(Colors.STEELBLUE);

                highlightedBar.anchor("right").add(Label.createLabel())
                        .textBaseline(barTextBaseline)
                        .text(highlightedBarLabelText).textBaseline("middle");

                highlightedBar.add(Label.createLabel())
                        .bottom(baselineLabelStart)
                        .textAlign(baselineLabelTextAlign)
                        .left(baselineLabelLength).text(baselineLabelText)
                        .textBaseline("middle");

                regularBar = highlightedBar.add(Bar.createBar())
                        .left(regularBarBase).width(regularBarLength)
                        .fillStyle(partialHighlightingChartFillStyle);

                regularBar.anchor("right").add(Label.createLabel())
                        .textBaseline(barTextBaseline)
                        .text(regularBarLabelText).textStyle("white")
                        .textBaseline("middle");
                return;
            }
        }

        regularBar = chart.add(Bar.createBar())
                .data(ArrayUtils.toJsArray(chartItems))
                .left(highlightedBarBase).width(fullBarLength).bottom(barStart)
                .height(barWidth).fillStyle(chartFillStyle)
                .strokeStyle(Colors.STEELBLUE);

        regularBar.add(Label.createLabel()).bottom(baselineLabelStart)
                .textAlign(baselineLabelTextAlign).left(baselineLabelLength)
                .text(baselineLabelText).textBaseline("middle");

        regularBar.anchor("right").add(Label.createLabel())
                .textBaseline(barTextBaseline).text(fullBarLabelText)
                .textStyle(fullBarTextStyle).textBaseline("middle");
    }

    protected void drawHorizontalBarScales(Scale scale) {
        this.scale = scale;
        chart.add(Rule.createRule()).data(scale.ticks()).left(scale).bottom(0)
                .strokeStyle(scaleStrokeStyle).height(chartHeight)
                .anchor("bottom").add(Label.createLabel()).text(scaleLabelText);

        chart.add(Rule.createRule()).bottom(0).left(0).width(chartWidth)
                .strokeStyle(Colors.GRAY_2);
    }

    private void drawVerticalBarChart() {
        for (ChartItem chartItem : chartItems) {
            if (isPartiallyHighlighted(chartItem)) {
                highlightedBar = chart.add(Bar.createBar())
                        .data(ArrayUtils.toJsArray(chartItems))
                        .bottom(highlightedBarBase)
                        .height(highlightedBarLength).left(barStart)
                        .width(barWidth).fillStyle(Colors.YELLOW)
                        .strokeStyle(Colors.STEELBLUE);

                highlightedBar.anchor("top").add(Label.createLabel())
                        .textBaseline(barTextBaseline)
                        .text(highlightedBarLabelText);

                highlightedBar.add(Label.createLabel())
                        .left(baselineLabelStart)
                        .textAlign(baselineLabelTextAlign)
                        .bottom(baselineLabelLength).text(baselineLabelText);

                regularBar = highlightedBar.add(Bar.createBar())
                        .bottom(regularBarBase).height(regularBarLength)
                        .fillStyle(partialHighlightingChartFillStyle);

                regularBar.anchor("top").add(Label.createLabel())
                        .textBaseline(barTextBaseline)
                        .text(regularBarLabelText).textStyle("white");
                return;
            }
        }

        regularBar = chart.add(Bar.createBar())
                .data(ArrayUtils.toJsArray(chartItems))
                .bottom(highlightedBarBase).height(fullBarLength)
                .left(barStart).width(barWidth).fillStyle(chartFillStyle)
                .strokeStyle(Colors.STEELBLUE);

        regularBar.add(Label.createLabel()).left(baselineLabelStart)
                .textAlign(baselineLabelTextAlign).bottom(baselineLabelLength)
                .text(baselineLabelText);

        regularBar.anchor("top").add(Label.createLabel())
                .textBaseline(barTextBaseline).text(fullBarLabelText)
                .textStyle(fullBarTextStyle);
    }

    protected void drawVerticalBarScales(Scale scale) {
        this.scale = scale;
        chart.add(Rule.createRule()).data(scale.ticks()).bottom(scale)
                .strokeStyle(scaleStrokeStyle).width(chartWidth).anchor("left")
                .add(Label.createLabel()).text(scaleLabelText);

        chart.add(Rule.createRule()).left(0).bottom(0).height(chartHeight)
                .strokeStyle(Colors.GRAY_2);
    }

    private boolean isPartiallyHighlighted(ChartItem chartItem) {
        return chartItem.getResourceItem().getStatus() == Status.PARTIALLY_HIGHLIGHTED
                || chartItem.getResourceItem().getStatus() == Status.PARTIALLY_HIGHLIGHTED_SELECTED;
    }

    @Override
    protected void registerEventHandler(String eventType,
            ProtovisEventHandler handler) {
        regularBar.event(eventType, handler);
    }

    private void setChartParameters() {
        if (layout.isVerticalBarChart(chartHeight, chartWidth)) {
            chart.left(BORDER_WIDTH).bottom(BORDER_HEIGHT);
        } else {
            chart.left(BORDER_WIDTH + HORIZONTAL_BAR_LABEL_EXTRA_MARGIN)
                    .bottom(BORDER_HEIGHT);
        }
    }

    public void setLayout(LayoutType layout) {
        this.layout = layout;
    }
}