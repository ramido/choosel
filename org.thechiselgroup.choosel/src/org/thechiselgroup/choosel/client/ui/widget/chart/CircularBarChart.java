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
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.ProtovisFunctionString;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Scale;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Wedge;
import org.thechiselgroup.choosel.client.util.ArrayUtils;
import org.thechiselgroup.choosel.client.views.ResourceItem.Status;
import org.thechiselgroup.choosel.client.views.SlotResolver;
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

    private ProtovisFunctionDouble wedgeStartAngle = new ProtovisFunctionDouble() {
        @Override
        public double f(ChartItem value, int i) {
            return 2 * Math.PI * i / chartItems.size();
        }
    };

    private ProtovisFunctionString highlightedWedgeLabelText = new ProtovisFunctionString() {
        @Override
        public String f(ChartItem value, int i) {
            return calculateHighlightedResources(i) < 1 ? null : Integer
                    .toString(calculateHighlightedResources(i));
        }
    };

    private ProtovisFunctionString regularWedgeLabelText = new ProtovisFunctionString() {
        @Override
        public String f(ChartItem value, int i) {
            return calculateAllResources(i) - calculateHighlightedResources(i) < 1 ? null
                    : Integer.toString(calculateAllResources(i)
                            - calculateHighlightedResources(i));
        }
    };

    private ProtovisFunctionString fullWedgeLabelText = new ProtovisFunctionString() {
        @Override
        public String f(ChartItem value, int i) {
            return Integer.toString(Math.max(calculateAllResources(i),
                    calculateHighlightedResources(i)));
        }
    };

    private ProtovisFunctionString fullWedgeTextStyle = new ProtovisFunctionString() {
        @Override
        public String f(ChartItem value, int i) {
            return calculateHighlightedResources(i) == 0 ? Colors.WHITE
                    : Colors.BLACK;
        }
    };

    private String wedgeLabelAnchor = Alignments.CENTER;

    private int wedgeTextAngle = 0;

    private double maxWedgeSize;

    @Override
    protected void beforeRender() {
        highlightedWedgeOuterRadius.beforeRender();
        regularWedgeOuterRadius.beforeRender();
    }

    private void calculateMaxWedgeSize() {
        maxWedgeSize = 0;
        for (int i = 0; i < chartItems.size(); i++) {
            int currentItem = Integer
                    .parseInt(chartItems.get(i).getResourceItem()
                            .getResourceValue(SlotResolver.CHART_VALUE_SLOT)
                            .toString());
            if (maxWedgeSize < currentItem) {
                maxWedgeSize = currentItem;
            }
        }
    }

    @Override
    public void drawChart() {
        assert chartItems.size() >= 1;

        calculateMaxWedgeSize();

        drawScale();
        drawWedge();
    }

    private void drawScale() {
        Scale scale = Scale.linear(0, maxWedgeSize).range(0,
                Math.min(height, width) - MARGIN_SIZE);

        chart.add(Dot.createDot()).data(scale.ticks()).left(width / 2)
                .bottom(height / 2).fillStyle("").strokeStyle(Colors.GRAY_1)
                .lineWidth(1).radius(new ProtovisFunctionDoubleToDouble() {
                    @Override
                    public double f(double value, int i) {
                        return value * (Math.min(height, width) - MARGIN_SIZE)
                                / (maxWedgeSize * 2);
                    }
                });
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
                        .fillStyle(Colors.STEELBLUE).strokeStyle(Colors.WHITE);

                regularWedge.anchor(wedgeLabelAnchor).add(Label.createLabel())
                        .textAngle(wedgeTextAngle).text(regularWedgeLabelText)
                        .textStyle(Colors.WHITE);

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
                .bottom(wedgeBottom).innerRadius(0)
                .outerRadius(regularWedgeOuterRadius).angle(wedgeAngle)
                .fillStyle(chartFillStyle).strokeStyle(Colors.WHITE);

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