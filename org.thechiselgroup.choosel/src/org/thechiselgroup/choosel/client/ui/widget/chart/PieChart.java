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
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Label;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.ProtovisEventHandler;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.ProtovisFunctionDouble;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.ProtovisFunctionDoubleWithCache;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.ProtovisFunctionString;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Wedge;
import org.thechiselgroup.choosel.client.util.ArrayUtils;
import org.thechiselgroup.choosel.client.views.ResourceItem.Status;
import org.thechiselgroup.choosel.client.views.chart.ChartItem;

// Version of Pie chart with the average of the area 
// and the radius calculations for proportional highlighting.
// (i.e. ratio + sqrt(ratio) / 2)
public class PieChart extends ChartWidget {

    private double[] highlightedWedgeCounts;

    private double[] regularWedgeCounts;

    private ProtovisFunctionDoubleWithCache highlightedWedgeOuterRadius = new ProtovisFunctionDoubleWithCache() {

        @Override
        public void beforeRender() {
            if (chartItems.isEmpty()) {
                return;
            }

            highlightedWedgeCounts = new double[chartItems.size()];
            regularWedgeCounts = new double[chartItems.size()];

            for (int i = 0; i < chartItems.size(); i++) {
                highlightedWedgeCounts[i] = calculateHighlightedResources(i);
                regularWedgeCounts[i] = calculateAllResources(i);
            }

        }

        @Override
        public double f(ChartItem value, int i) {
            return (Math
                    .sqrt(highlightedWedgeCounts[i] / regularWedgeCounts[i])
                    * regularWedgeOuterRadius.f(value, i) + highlightedWedgeCounts[i]
                    / regularWedgeCounts[i]
                    * regularWedgeOuterRadius.f(value, i)) / 2;
        }
    };

    private double sum;

    private ProtovisFunctionDouble regularWedgeOuterRadius = new ProtovisFunctionDouble() {
        @Override
        public double f(ChartItem value, int i) {
            return Math.min(height, width) / 2 - 5;
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
            return calculateAllResources(i) * 2 * Math.PI / sum;
        }
    };

    private ProtovisFunctionString highlightedWedgeLabelText = new ProtovisFunctionString() {
        @Override
        public String f(ChartItem value, int i) {
            return calculateHighlightedResources(i) < 1 ? null : Double
                    .toString(calculateHighlightedResources(i));
        }
    };

    private ProtovisFunctionString regularWedgeLabelText = new ProtovisFunctionString() {
        @Override
        public String f(ChartItem value, int i) {
            return calculateAllResources(i) - calculateHighlightedResources(i) < 1 ? null
                    : Double.toString(calculateAllResources(i)
                            - calculateHighlightedResources(i));
        }
    };

    private ProtovisFunctionString fullWedgeLabelText = new ProtovisFunctionString() {
        @Override
        public String f(ChartItem value, int i) {
            return Double.toString(Math.max(calculateAllResources(i),
                    calculateHighlightedResources(i)));
        }
    };

    private ProtovisFunctionString fullWedgeTextStyle = new ProtovisFunctionString() {
        @Override
        public String f(ChartItem value, int i) {
            return calculateHighlightedResources(i) == 0 ? "white" : "black";
        }
    };

    private String wedgeLabelAnchor = "center";

    private int wedgeTextAngle = 0;

    @Override
    protected void beforeRender() {
        highlightedWedgeOuterRadius.beforeRender();
    }

    @Override
    public void drawChart() {
        assert chartItems.size() >= 1;

        drawWedge();
    }

    private void drawWedge() {
        sum = 0;
        for (int i = 0; i < chartItems.size(); i++) {
            sum += calculateAllResources(i);
        }

        for (ChartItem chartItem : chartItems) {
            if (chartItem.getResourceItem().getStatus() == Status.PARTIALLY_HIGHLIGHTED
                    || chartItem.getResourceItem().getStatus() == Status.PARTIALLY_HIGHLIGHTED_SELECTED) {

                regularWedge = chart.add(Wedge.createWedge())
                        .data(ArrayUtils.toJsArray(chartItems)).left(wedgeLeft)
                        .bottom(wedgeBottom)
                        .innerRadius(highlightedWedgeOuterRadius)
                        .outerRadius(regularWedgeOuterRadius).angle(wedgeAngle)
                        .fillStyle(partialHighlightingChartFillStyle)
                        .strokeStyle("white");

                regularWedge.anchor(wedgeLabelAnchor).add(Label.createLabel())
                        .textAngle(wedgeTextAngle).text(regularWedgeLabelText)
                        .textStyle("white");

                highlightedWedge = regularWedge.add(Wedge.createWedge())
                        .innerRadius(0)
                        .outerRadius(highlightedWedgeOuterRadius)
                        .fillStyle(Colors.YELLOW);

                highlightedWedge.anchor(wedgeLabelAnchor)
                        .add(Label.createLabel()).textAngle(wedgeTextAngle)
                        .text(highlightedWedgeLabelText);
                return;
            }
        }

        regularWedge = chart.add(Wedge.createWedge())
                .data(ArrayUtils.toJsArray(chartItems)).left(wedgeLeft)
                .bottom(wedgeBottom).outerRadius(regularWedgeOuterRadius)
                .angle(wedgeAngle).fillStyle(chartFillStyle)
                .strokeStyle("white");

        regularWedge.anchor(wedgeLabelAnchor).add(Label.createLabel())
                .textAngle(wedgeTextAngle).text(fullWedgeLabelText)
                .textStyle(fullWedgeTextStyle);

    }

    @Override
    protected void registerEventHandler(String eventType,
            ProtovisEventHandler handler) {
        regularWedge.event(eventType, handler);
    }

}