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
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Alignment;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Dot;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Label;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.ProtovisEventHandler;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.ProtovisFunctionDoubleWithCache;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.ProtovisFunctionStringToString;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Rule;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Scale;
import org.thechiselgroup.choosel.client.util.collections.ArrayUtils;
import org.thechiselgroup.choosel.client.views.SlotResolver;
import org.thechiselgroup.choosel.client.views.chart.ChartItem;

// TODO right side ticks
public class ScatterPlot extends ChartWidget {

    private static final int BORDER_BOTTOM = 35;

    private static final int BORDER_TOP = 5;

    private static final int BORDER_LEFT = 35;

    private static final int BORDER_RIGHT = 5;

    private static final String GRIDLINE_SCALE_COLOR = Colors.GRAY_1;

    private static final String AXIS_SCALE_COLOR = Colors.GRAY_2;

    private double[] scatterCountsX;

    private double[] scatterCountsY;

    protected double chartHeight;

    protected double chartWidth;

    private double minY;

    private double maxY;

    private double minX;

    private double maxX;

    private Scale scaleX;

    private Scale scaleY;

    private ProtovisFunctionDoubleWithCache scatterBottom = new ProtovisFunctionDoubleWithCache() {

        @Override
        public void beforeRender() {
            if (chartItems.isEmpty()) {
                return;
            }

            scatterCountsY = new double[chartItems.size()];

            for (int i = 0; i < chartItems.size(); i++) {
                scatterCountsY[i] = calculateAllResourcesY(i);
            }

            minY = ArrayUtils.min(scatterCountsY) - 1;
            maxY = ArrayUtils.max(scatterCountsY) + 1;
        }

        @Override
        public double f(ChartItem value, int i) {
            return (scatterCountsY[i] - minY) * chartHeight / (maxY - minY);
        }
    };

    private ProtovisFunctionDoubleWithCache scatterLeft = new ProtovisFunctionDoubleWithCache() {

        @Override
        public void beforeRender() {
            if (chartItems.isEmpty()) {
                return;
            }

            scatterCountsX = new double[chartItems.size()];

            for (int i = 0; i < chartItems.size(); i++) {
                scatterCountsX[i] = calculateAllResourcesX(i);
            }

            minX = ArrayUtils.min(scatterCountsX) - 1;
            maxX = ArrayUtils.max(scatterCountsX) + 1;
        }

        @Override
        public double f(ChartItem value, int i) {
            return (scatterCountsX[i] - minX) * chartWidth / (maxX - minX);
        }
    };

    private String scaleStrokeStyle = GRIDLINE_SCALE_COLOR;

    private Dot scatter;

    protected ProtovisFunctionStringToString scaleLabelTextX = new ProtovisFunctionStringToString() {
        @Override
        public String f(String o, int index) {
            return scaleX.tickFormat(o.toString());
        }
    };

    protected ProtovisFunctionStringToString scaleLabelTextY = new ProtovisFunctionStringToString() {
        @Override
        public String f(String o, int index) {
            return scaleY.tickFormat(o.toString());
        }
    };

    private String xAxisLabel = "X-Axis";

    private String yAxisLabel = "Y-Axis";

    @Override
    protected void beforeRender() {
        scatterBottom.beforeRender();
        scatterLeft.beforeRender();
    }

    // TODO refactor
    protected double calculateAllResourcesX(int i) {
        return Double.parseDouble(chartItems.get(i).getResourceItem()
                .getResourceValue(SlotResolver.X_COORDINATE_SLOT).toString());
    }

    // TODO refactor
    protected double calculateAllResourcesY(int i) {
        return Double.parseDouble(chartItems.get(i).getResourceItem()
                .getResourceValue(SlotResolver.Y_COORDINATE_SLOT).toString());
    }

    private void calculateChartVariables() {
        chartWidth = width - BORDER_LEFT - BORDER_RIGHT;
        chartHeight = height - BORDER_BOTTOM - BORDER_TOP;
    }

    private void drawAxisLabels() {
        chart.add(Label.createLabel()).bottom(-BORDER_BOTTOM + 5)
                .left(chartWidth / 2).text(xAxisLabel)
                .textAlign(Alignment.CENTER);

        chart.add(Label.createLabel()).bottom(chartHeight / 2)
                .left(-BORDER_LEFT + 20).text(yAxisLabel)
                .textAngle(-Math.PI / 2).textAlign(Alignment.CENTER);
    }

    @Override
    public void drawChart() {
        assert chartItems.size() >= 1;

        calculateChartVariables();
        setChartParameters();

        beforeRender();

        scaleX = Scale.linear(minX, maxX).range(0, chartWidth);
        scaleY = Scale.linear(minY, maxY).range(0, chartHeight);
        drawScales(scaleX, scaleY);
        drawScatter();
        drawAxisLabels();
    }

    protected void drawScales(Scale scaleX, Scale scaleY) {
        chart.add(Rule.createRule()).data(scaleX.ticks()).bottom(0)
                .left(scaleX).strokeStyle(scaleStrokeStyle).height(chartHeight)
                .anchor(Alignment.BOTTOM).add(Label.createLabel())
                .text(scaleLabelTextX);

        chart.add(Rule.createRule()).data(scaleY.ticks()).bottom(scaleY)
                .left(0).strokeStyle(scaleStrokeStyle).width(chartWidth)
                .add(Label.createLabel()).text(scaleLabelTextY)
                .textAngle(-Math.PI / 2).textAlign(Alignment.CENTER)
                .textBaseline(Alignment.BOTTOM);

        chart.add(Rule.createRule()).height(chartHeight).bottom(0).left(0)
                .strokeStyle(AXIS_SCALE_COLOR);

        chart.add(Rule.createRule()).width(chartWidth).bottom(0).left(0)
                .strokeStyle(AXIS_SCALE_COLOR);
    }

    private void drawScatter() {
        scatter = chart
                .add(Dot.createDot())
                .data(ArrayUtils.toJsArray(chartItems))
                .bottom(scatterBottom)
                .left(scatterLeft)
                .size(Math.min(chartHeight, chartWidth)
                        / (chartItems.size() * 2)).fillStyle(chartFillStyle)
                .strokeStyle(Colors.STEELBLUE);
    }

    public String getXAxisLabel() {
        return xAxisLabel;
    }

    public String getYAxisLabel() {
        return yAxisLabel;
    }

    @Override
    protected void registerEventHandler(String eventType,
            ProtovisEventHandler handler) {
        scatter.event(eventType, handler);
    }

    private void setChartParameters() {
        chart.left(BORDER_LEFT).bottom(BORDER_BOTTOM);
    }

    public void setXAxisLabel(String xAxisLabel) {
        this.xAxisLabel = xAxisLabel;
    }

    public void setYAxisLabel(String yAxisLabel) {
        this.yAxisLabel = yAxisLabel;
    }
}