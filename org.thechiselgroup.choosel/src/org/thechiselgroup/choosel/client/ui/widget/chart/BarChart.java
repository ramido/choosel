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

    private static final int BAR_PADDING = 25;

    private static final int BORDER_HEIGHT = 10;

    private static final int BORDER_WIDTH = 20;

    private double[] barCounts = new double[5];

    private double[] highlightedBarCounts = new double[5];

    private Bar bar;

    private ProtovisFunctionDouble barLeft = new ProtovisFunctionDouble() {
        @Override
        public double f(ChartItem value, int index) {
            return BAR_PADDING + (index * chartWidth / chartItems.size());
        }
    };

    private ProtovisFunctionDoubleWithCache barHeight = new ProtovisFunctionDoubleWithCache() {

        @Override
        public void beforeRender() {
            if (chartItems.isEmpty()) {
                return;
            }

            highlightedBarCounts = new double[5];

            for (int i = 0; i < chartItems.size(); i++) {
                highlightedBarCounts[i] = chartItems.get(i).getResourceItem()
                        .getHighlightedResources().size();
            }

        }

        @Override
        public double f(ChartItem value, int index) {
            return highlightedBarCounts[index] * chartHeight / 10;
        }

    };

    private ProtovisFunctionDoubleWithCache barHeight2 = new ProtovisFunctionDoubleWithCache() {

        @Override
        public void beforeRender() {
            if (chartItems.isEmpty()) {
                return;
            }

            barCounts = new double[5];

            for (int i = 0; i < chartItems.size(); i++) {
                barCounts[i] = Integer.parseInt(chartItems.get(i)
                        .getResourceItem()
                        .getResourceValue(SlotResolver.FONT_SIZE_SLOT)
                        .toString())
                        - chartItems.get(i).getResourceItem()
                                .getHighlightedResources().size();
            }

        }

        @Override
        public double f(ChartItem value, int index) {
            return barCounts[index] * chartHeight / 10;
        }

    };

    private ProtovisFunctionDouble barWidth = new ProtovisFunctionDouble() {
        @Override
        public double f(ChartItem value, int index) {
            return chartWidth / (chartItems.size() * 2);
        }
    };

    private ProtovisFunctionDouble barBottom2 = new ProtovisFunctionDouble() {
        @Override
        public double f(ChartItem value, int index) {
            return barHeight.f(value, index) + barBottom;
        }
    };

    private static final String ORANGE = "orange";

    private static final String YELLOW = "yellow";

    private int barBottom = BORDER_HEIGHT + 1;

    private ProtovisFunctionString barFillStyle = new ProtovisFunctionString() {
        @Override
        public String f(ChartItem value, int index) {
            switch (value.getResourceItem().getStatus()) {
            case PARTIALLY_HIGHLIGHTED:
            case PARTIALLY_HIGHLIGHTED_SELECTED:
            case HIGHLIGHTED_SELECTED:
            case HIGHLIGHTED:
                return YELLOW;
            case DEFAULT:
                return Colors.STEELBLUE;
            case SELECTED:
                return ORANGE;
            }
            throw new RuntimeException("No colour available");
        }
    };

    protected double chartHeight;

    protected double chartWidth;

    @Override
    protected void beforeRender() {
        barHeight.beforeRender();
        barHeight2.beforeRender();
    }

    private void calculateChartVariables() {
        chartWidth = width - BORDER_WIDTH * 2;
        chartHeight = height - BORDER_HEIGHT * 2;
    }

    private void drawBar() {
        bar = chart.add(Bar.createBar()).data(chartItemsJSArray)
                .bottom(barBottom).height(barHeight).left(barLeft)
                .width(barWidth).fillStyle(YELLOW)
                .strokeStyle(Colors.STEELBLUE).add(Bar.createBar())
                .bottom(barBottom2).height(barHeight2)
                .fillStyle(Colors.STEELBLUE);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Bar drawChart() {
        assert ArrayUtils.length(chartItemsJSArray) >= 1;

        calculateChartVariables();
        setChartParameters();

        // SlotValues slotValues = getSlotValues(SlotResolver.DESCRIPTION_SLOT);

        Scale scale = Scale.linear(10, 0).range(0, chartHeight);
        drawScales(scale);
        drawBar();

        return bar;
    }

    protected void drawScales(Scale scale) {
        this.scale = scale;
        chart.add(Rule.createRule())
                .data(scale.ticks())
                .top(scale)
                .strokeStyle(new ProtovisFunctionStringToString() {
                    @Override
                    public String f(String value, int index) {
                        return Double.parseDouble(value) == 0 ? "gray"
                                : "lightgray";
                    }
                }).width(chartWidth).anchor("left").add(Label.createLabel())
                .text(labelText);

        chart.add(Rule.createRule()).left(0).bottom(BORDER_HEIGHT)
                .strokeStyle("gray");
    }

    private void setChartParameters() {
        chart.left(BORDER_WIDTH).top(BORDER_HEIGHT);
    }
}