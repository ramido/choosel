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

import java.util.Collections;

import org.thechiselgroup.choosel.client.ui.Colors;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Alignment;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Bar;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Label;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.ProtovisEventHandler;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.ProtovisFunctionBoolean;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.ProtovisFunctionDouble;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.ProtovisFunctionDoubleWithCache;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.ProtovisFunctionString;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.ProtovisFunctionStringToString;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Rule;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Scale;
import org.thechiselgroup.choosel.client.util.collections.ArrayUtils;
import org.thechiselgroup.choosel.client.views.ResourceItem.SubsetStatus;
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

    private static final int BORDER_BOTTOM = 35;

    private static final int BORDER_LEFT = 45;

    private static final int BORDER_TOP = 5;

    private static final int BORDER_RIGHT = 5;

    private static final String GRIDLINE_SCALE_COLOR = "rgba(255,255,255,.3)";

    private static final String AXIS_SCALE_COLOR = Colors.GRAY_1;

    private double[] highlightedBarCounts;

    private double[] regularValues;

    // TODO semantic meaning (bar length etc) --> makes different settings
    // easier
    protected int chartHeight;

    protected int chartWidth;

    private ProtovisFunctionBoolean isPartiallyHighlighted = new ProtovisFunctionBoolean() {
        @Override
        public boolean f(ChartItem chartValue, int i) {
            return SubsetStatus.PARTIAL.equals(chartValue.getResourceItem()
                    .getHighlightStatus());
        }
    };

    // TODO refactor
    private ProtovisFunctionDouble barStart = new ProtovisFunctionDouble() {
        @Override
        public double f(ChartItem value, int i) {
            return barWidth.f(value, i) / 2 + i
                    * layout.getBarWidthSpace(chartHeight, chartWidth)
                    / chartItems.size();
        }
    };

    // TODO refactor
    private ProtovisFunctionDouble highlightedBarStart = new ProtovisFunctionDouble() {
        @Override
        public double f(ChartItem value, int i) {
            // TODO extract first part (reuse other function)
            return (0.33 * layout.getBarWidthSpace(chartHeight, chartWidth) / (chartItems
                    .size() * 2))
                    + barWidth.f(value, i)
                    / 2
                    + i
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
        public double f(ChartItem value, int i) {
            return calculateLength(highlightedBarCounts[i]);
        }
    };

    private ProtovisFunctionDoubleWithCache fullBarLength = new ProtovisFunctionDoubleWithCache() {
        @Override
        public void beforeRender() {
            if (chartItems.isEmpty()) {
                return;
            }

            regularValues = new double[chartItems.size()];

            for (int i = 0; i < chartItems.size(); i++) {
                regularValues[i] = calculateAllResources(i);
            }
        }

        @Override
        public double f(ChartItem value, int i) {
            return calculateLength(regularValues[i]);
        }
    };

    private ProtovisFunctionDouble barWidth = new ProtovisFunctionDouble() {
        @Override
        public double f(ChartItem value, int i) {
            return layout.getBarWidthSpace(chartHeight, chartWidth)
                    / (chartItems.size() * 2);
        }
    };

    // TODO barWidth / 3
    private ProtovisFunctionDouble highlightedWidth = new ProtovisFunctionDouble() {
        @Override
        public double f(ChartItem value, int i) {
            return 0.33 * layout.getBarWidthSpace(chartHeight, chartWidth)
                    / (chartItems.size() * 2);
        }
    };

    private ProtovisFunctionDouble regularBarBase = new ProtovisFunctionDouble() {
        @Override
        public double f(ChartItem value, int i) {
            return highlightedBarLength.f(value, i) + barLineWidth;
        }
    };

    private ProtovisFunctionStringToString scaleStrokeStyle = new ProtovisFunctionStringToString() {
        @Override
        public String f(String value, int i) {
            return Double.parseDouble(value) == 0 ? AXIS_SCALE_COLOR
                    : GRIDLINE_SCALE_COLOR;
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

    private ProtovisFunctionString baselineLabelText = new ProtovisFunctionString() {
        @Override
        public String f(ChartItem value, int i) {
            return value.getResourceItem()
                    .getResourceValue(SlotResolver.CHART_LABEL_SLOT).toString();
        }
    };

    private String barTextBaseline = Alignment.TOP;

    protected LayoutType layout = LayoutType.HORIZONTAL;

    private static final int HORIZONTAL_BAR_LABEL_EXTRA_MARGIN = 20;

    private double barLineWidth = 1;

    protected ProtovisFunctionString chartFillStyle = new ProtovisFunctionString() {
        @Override
        public String f(ChartItem chartItem, int i) {
            if (SubsetStatus.COMPLETE.equals(chartItem.getResourceItem()
                    .getHighlightStatus())) {
                return Colors.YELLOW; // TODO semantic color constants
            }

            switch (chartItem.getResourceItem().getSelectionStatus()) {
            case COMPLETE:
            case PARTIAL:
                return Colors.ORANGE; // TODO semantic color constants
            default:
                return Colors.STEELBLUE; // TODO semantic color constants
            }
        }
    };

    protected ProtovisFunctionString fullMarkTextStyle = new ProtovisFunctionString() {
        @Override
        public String f(ChartItem value, int i) {
            if (SubsetStatus.COMPLETE.equals(value.getResourceItem()
                    .getHighlightStatus())) {
                return Colors.BLACK;
            }

            // XXX calculate label size instead of using 60px
            if (calculateLength(regularValues[i]) < 60) {
                return Colors.GRAY_2;
            }

            return Colors.WHITE;
        }
    };

    private String measurementLabel;

    private ProtovisFunctionString valueLabelAlignment = new ProtovisFunctionString() {

        @Override
        public String f(ChartItem value, int i) {
            // XXX pre-calculation should be done by methods in
            // chart..
            // XXX calculate label size instead of taking 60px
            if (calculateLength(regularValues[i]) < 60) {
                return Alignment.LEFT;
            }
            return Alignment.RIGHT;
        }
    };

    @Override
    protected void beforeRender() {
        super.beforeRender();
        highlightedBarLength.beforeRender();
        fullBarLength.beforeRender();
    }

    private void calculateChartVariables() {
        if (layout.isVerticalBarChart(chartHeight, chartWidth)) {
            chartWidth = width - BORDER_LEFT - BORDER_RIGHT;
        } else {
            chartWidth = width - BORDER_LEFT - BORDER_RIGHT
                    - HORIZONTAL_BAR_LABEL_EXTRA_MARGIN;
        }

        chartHeight = height - BORDER_BOTTOM - BORDER_TOP;
    }

    private double calculateLength(double value) {
        return value * layout.getBarLengthSpace(chartHeight, chartWidth)
                / getMaximumChartItemValue();
    }

    @Override
    public void drawChart() {
        assert chartItems.size() >= 1;

        Collections.sort(chartItems, new ChartItemComparator(
                SlotResolver.CHART_LABEL_SLOT));

        calculateChartVariables();
        calculateMaximumChartItemValue();

        if (layout.isVerticalBarChart(chartHeight, chartWidth)) {
            chart.left(BORDER_LEFT).bottom(BORDER_BOTTOM);
            Scale scale = Scale.linear(0, getMaximumChartItemValue()).range(0,
                    chartHeight);
            // TODO axis label
            drawVerticalBarChart();
            drawVerticalBarScales(scale);
        } else {
            chart.left(BORDER_LEFT + HORIZONTAL_BAR_LABEL_EXTRA_MARGIN).bottom(
                    BORDER_BOTTOM);
            Scale scale = Scale.linear(0, getMaximumChartItemValue()).range(0,
                    chartWidth);
            drawHorizontalBarMeasurementAxisLabel();
            drawHorizontalBarChart();
            drawHorizontalBarScales(scale);

        }
        chart.add(Rule.createRule()).bottom(0).left(0).width(chartWidth)
                .strokeStyle(AXIS_SCALE_COLOR).lineWidth(barLineWidth);
        chart.add(Rule.createRule()).left(0).bottom(0).height(chartHeight)
                .strokeStyle(AXIS_SCALE_COLOR).lineWidth(barLineWidth);
    }

    private void drawHorizontalBarChart() {
        regularBar = chart.add(Bar.createBar())
                .data(ArrayUtils.toJsArray(chartItems)).left(barLineWidth)
                .width(fullBarLength).bottom(barStart).height(barWidth)
                .fillStyle(chartFillStyle).strokeStyle(Colors.STEELBLUE)
                .lineWidth(barLineWidth);

        regularBar.add(Label.createLabel()).bottom(baselineLabelStart)
                .textAlign(Alignment.RIGHT).left(0).text(baselineLabelText)
                .textBaseline(Alignment.MIDDLE);

        regularBar.anchor(Alignment.RIGHT).add(Label.createLabel())
                .textBaseline(Alignment.MIDDLE).text(fullMarkLabelText)
                .textStyle(fullMarkTextStyle).textAlign(valueLabelAlignment);

        // TODO negative bars (in opposite direction)
        highlightedBar = chart.add(Bar.createBar())
                .data(ArrayUtils.toJsArray(chartItems)).left(barLineWidth)
                .width(highlightedBarLength).bottom(highlightedBarStart)
                .height(highlightedWidth).fillStyle(Colors.YELLOW)
                .strokeStyle(Colors.STEELBLUE).lineWidth(barLineWidth)
                .visible(isPartiallyHighlighted);

        highlightedBar.anchor(Alignment.RIGHT).add(Label.createLabel())
                .textBaseline(barTextBaseline).text(highlightedLabelText)
                .textStyle(Colors.BLACK).textBaseline(Alignment.MIDDLE);
    }

    private void drawHorizontalBarMeasurementAxisLabel() {
        chart.add(Label.createLabel()).bottom(-BORDER_BOTTOM + 5)
                .left(chartWidth / 2).text(measurementLabel)
                .textAlign(Alignment.CENTER);
    }

    protected void drawHorizontalBarScales(Scale scale) {
        this.scale = scale;
        chart.add(Rule.createRule()).data(scale.ticks(5)).left(scale).bottom(0)
                .strokeStyle(scaleStrokeStyle).height(chartHeight)
                .anchor(Alignment.BOTTOM).add(Label.createLabel())
                .text(scaleLabelText);
    }

    private void drawVerticalBarChart() {
        regularBar = chart.add(Bar.createBar())
                .data(ArrayUtils.toJsArray(chartItems))
                .bottom(barLineWidth - 1).height(fullBarLength).left(barStart)
                .width(barWidth).fillStyle(chartFillStyle)
                .strokeStyle(Colors.STEELBLUE).lineWidth(barLineWidth);

        regularBar.add(Label.createLabel()).left(baselineLabelStart)
                .textAlign(Alignment.CENTER)
                .bottom(new ProtovisFunctionDouble() {
                    @Override
                    public double f(ChartItem value, int i) {
                        // TODO dynamic positioning depending on label size
                        if (chartWidth / regularValues.length > 60) {
                            return -10;
                        }
                        return i % 2 == 0 ? -10 : -25;
                    }
                }).text(baselineLabelText).textBaseline(Alignment.MIDDLE);

        regularBar.anchor(Alignment.TOP).add(Label.createLabel())
                .textAngle(-Math.PI / 2).textBaseline(Alignment.MIDDLE)
                .textAlign(valueLabelAlignment).textStyle(fullMarkTextStyle)
                .text(fullMarkLabelText);

        highlightedBar = chart.add(Bar.createBar())
                .data(ArrayUtils.toJsArray(chartItems)).bottom(barLineWidth)
                .height(highlightedBarLength).left(highlightedBarStart)
                .width(highlightedWidth).fillStyle(Colors.YELLOW)
                .strokeStyle(Colors.STEELBLUE).lineWidth(barLineWidth)
                .visible(isPartiallyHighlighted);

        highlightedBar.anchor(Alignment.TOP).add(Label.createLabel())
                .textBaseline(Alignment.MIDDLE).textAlign(Alignment.RIGHT)
                .text(highlightedMarkLabelText).textAngle(-Math.PI / 2);
    }

    // TODO extract scale ticks # as property
    protected void drawVerticalBarScales(Scale scale) {
        this.scale = scale;
        chart.add(Rule.createRule()).data(scale.ticks(5)).left(0).bottom(scale)
                .strokeStyle(scaleStrokeStyle).width(chartWidth)
                .anchor(Alignment.LEFT).add(Label.createLabel())
                .text(scaleLabelText);
    }

    public String getMeasurementLabel() {
        return measurementLabel;
    }

    @Override
    protected void registerEventHandler(String eventType,
            ProtovisEventHandler handler) {
        regularBar.event(eventType, handler);
    }

    public void setLayout(LayoutType layout) {
        this.layout = layout;
    }

    public void setMeasurementLabel(String measurementLabel) {
        this.measurementLabel = measurementLabel;
    }

}