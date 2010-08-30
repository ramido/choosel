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
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Alignments;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Dot;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Label;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.ProtovisEventHandler;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.ProtovisFunctionDouble;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.ProtovisFunctionDoubleToDouble;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.ProtovisFunctionDoubleWithCache;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Scale;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Wedge;
import org.thechiselgroup.choosel.client.util.ArrayUtils;
import org.thechiselgroup.choosel.client.views.chart.ChartItem;

// Version of Pie chart with the average of the area 
// and the radius calculations for proportional highlighting.
// (i.e. ratio + sqrt(ratio) / 2)
public class CircularBarChart extends ChartWidget {

    private double[] highlightedWedgeCounts;

    private double[] regularWedgeCounts;

    private static final int MARGIN_SIZE = 15;

    private ProtovisFunctionDoubleWithCache highlightedWedgeOuterRadius = new ProtovisFunctionDoubleWithCache() {

        @Override
        public void beforeRender() {
            if (chartItems.isEmpty()) {
                return;
            }

            highlightedWedgeCounts = new double[chartItems.size()];

            for (int i = 0; i < chartItems.size(); i++) {
                highlightedWedgeCounts[i] = calculateHighlightedResources(i);
            }

        }

        @Override
        public double f(ChartItem value, int i) {
            return regularWedgeOuterRadius.f(value, i)
                    * highlightedWedgeCounts[i] / regularWedgeCounts[i];
        }
    };

    private double sum;

    private ProtovisFunctionDoubleWithCache regularWedgeOuterRadius = new ProtovisFunctionDoubleWithCache() {
        @Override
        public void beforeRender() {
            if (chartItems.isEmpty()) {
                return;
            }

            regularWedgeCounts = new double[chartItems.size()];

            for (int i = 0; i < chartItems.size(); i++) {
                regularWedgeCounts[i] = calculateAllResources(i);
            }

        }

        @Override
        public double f(ChartItem value, int i) {
            return regularWedgeCounts[i]
                    * (Math.min(height, width) - MARGIN_SIZE)
                    / (ArrayUtils.max(regularWedgeCounts) * 2);
        }
    };

    private Wedge regularWedge;

    private Wedge highlightedWedge;

    private ProtovisFunctionDouble wedgeLeft = new ProtovisFunctionDouble() {
        @Override
        public double f(ChartItem value, int i) {
            return width / 2;
        }
    };

    private ProtovisFunctionDouble wedgeBottom = new ProtovisFunctionDouble() {
        @Override
        public double f(ChartItem value, int i) {
            return height / 2;
        }
    };

    private ProtovisFunctionDouble wedgeAngle = new ProtovisFunctionDouble() {
        @Override
        public double f(ChartItem value, int i) {
            return 2 * Math.PI / chartItems.size();
        }
    };

    private String wedgeLabelAnchor = Alignments.CENTER;

    private int wedgeTextAngle = 0;

    private int highlightedWedgeInnerRadius = 0;

    private ProtovisFunctionDoubleToDouble scaleRadius = new ProtovisFunctionDoubleToDouble() {
        @Override
        public double f(double value, int i) {
            return value * (Math.min(height, width) - MARGIN_SIZE)
                    / (getMaximumChartItemValue() * 2);
        }
    };

    private int scaleLineWidth = 1;

    @Override
    protected void beforeRender() {
        super.beforeRender();
        highlightedWedgeOuterRadius.beforeRender();
        regularWedgeOuterRadius.beforeRender();
    }

    private void calculateAllResourcesSum() {
        sum = 0;
        for (int i = 0; i < chartItems.size(); i++) {
            sum += calculateAllResources(i);
        }
    }

    @Override
    public void drawChart() {
        assert chartItems.size() >= 1;

        drawScale();
        drawWedge();
    }

    private void drawScale() {
        Scale scale = Scale.linear(0, getMaximumChartItemValue()).range(0,
                Math.min(height, width) - MARGIN_SIZE);

        chart.add(Dot.createDot()).data(scale.ticks()).left(width / 2)
                .bottom(height / 2).fillStyle("").strokeStyle(Colors.GRAY_1)
                .lineWidth(scaleLineWidth).radius(scaleRadius);
    }

    private void drawWedge() {
        calculateAllResourcesSum();

        regularWedge = chart.add(Wedge.createWedge())
                .data(ArrayUtils.toJsArray(chartItems)).left(wedgeLeft)
                .bottom(wedgeBottom).outerRadius(regularWedgeOuterRadius)
                .angle(wedgeAngle).strokeStyle(Colors.WHITE);

        if (hasPartiallyHighlightedChartItems()) {
            regularWedge.innerRadius(highlightedWedgeOuterRadius).fillStyle(
                    Colors.STEELBLUE);

            regularWedge.anchor(wedgeLabelAnchor).add(Label.createLabel())
                    .textAngle(wedgeTextAngle).text(regularMarkLabelText)
                    .textStyle(Colors.WHITE);

            highlightedWedge = regularWedge.add(Wedge.createWedge())
                    .innerRadius(highlightedWedgeInnerRadius)
                    .outerRadius(highlightedWedgeOuterRadius)
                    .fillStyle(Colors.YELLOW);

            highlightedWedge.anchor(wedgeLabelAnchor).add(Label.createLabel())
                    .textAngle(wedgeTextAngle).text(highlightedMarkLabelText);

            return;
        }

        regularWedge.innerRadius(0).fillStyle(chartFillStyle);

        regularWedge.anchor(wedgeLabelAnchor).add(Label.createLabel())
                .textAngle(wedgeTextAngle).text(fullMarkLabelText)
                .textStyle(fullMarkTextStyle);

    }

    @Override
    protected void registerEventHandler(String eventType,
            ProtovisEventHandler handler) {
        regularWedge.event(eventType, handler);
    }

}