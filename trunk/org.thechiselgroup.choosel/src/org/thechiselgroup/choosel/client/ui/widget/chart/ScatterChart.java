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
import org.thechiselgroup.choosel.client.views.SlotResolver;
import org.thechiselgroup.choosel.client.views.chart.ChartItem;

//Version of Pie chart with the radius calculation for proportional highlighting.
public class ScatterChart extends ChartWidget {

    private String[] wedgeColors = { Colors.STEELBLUE };

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
                highlightedWedgeCounts[i] = chartItems.get(i).getResourceItem()
                        .getHighlightedResources().size();
                regularWedgeCounts[i] = Integer.parseInt(chartItems.get(i)
                        .getResourceItem()
                        .getResourceValue(SlotResolver.FONT_SIZE_SLOT)
                        .toString());
            }

        }

        @Override
        public double f(ChartItem value, int index) {
            return highlightedWedgeCounts[index] / regularWedgeCounts[index]
                    * regularWedgeOuterRadius.f(value, index);
        }

    };

    private double sum;

    private ProtovisFunctionDouble regularWedgeOuterRadius = new ProtovisFunctionDouble() {
        @Override
        public double f(ChartItem value, int index) {
            return Math.min(height, width) / 2 - 5;
        }
    };

    private Wedge regularWedge;

    private Wedge highlightedWedge;

    private ProtovisFunctionDouble wedgeLeft = new ProtovisFunctionDouble() {
        @Override
        public double f(ChartItem value, int index) {
            return width / 2;
        }
    };

    private ProtovisFunctionDouble wedgeBottom = new ProtovisFunctionDouble() {
        @Override
        public double f(ChartItem value, int index) {
            return height / 2;
        }
    };

    private ProtovisFunctionDouble wedgeAngle = new ProtovisFunctionDouble() {
        @Override
        public double f(ChartItem value, int index) {
            return Double.parseDouble(value.getResourceItem()
                    .getResourceValue(SlotResolver.FONT_SIZE_SLOT).toString())
                    * 2 * Math.PI / sum;
        }
    };

    private ProtovisFunctionString wedgeFillStyle = new ProtovisFunctionString() {
        @Override
        public String f(ChartItem value, int index) {
            return wedgeColors[index % wedgeColors.length];
        }
    };

    private ProtovisFunctionString highlightedWedgeLabelText = new ProtovisFunctionString() {
        @Override
        public String f(ChartItem value, int index) {
            return chartItems.get(index).getResourceItem()
                    .getHighlightedResources().size() < 1 ? null : Integer
                    .toString(chartItems.get(index).getResourceItem()
                            .getHighlightedResources().size());
        }
    };

    private ProtovisFunctionString regularWedgeLabelText = new ProtovisFunctionString() {
        @Override
        public String f(ChartItem value, int index) {
            return Integer.parseInt(chartItems.get(index).getResourceItem()
                    .getResourceValue(SlotResolver.FONT_SIZE_SLOT).toString())
                    - chartItems.get(index).getResourceItem()
                            .getHighlightedResources().size() < 1 ? null
                    : Integer.toString(Integer.parseInt(chartItems.get(index)
                            .getResourceItem()
                            .getResourceValue(SlotResolver.FONT_SIZE_SLOT)
                            .toString())
                            - chartItems.get(index).getResourceItem()
                                    .getHighlightedResources().size());
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
        sum = wedgeTextAngle;
        for (ChartItem chartItem : chartItems) {
            sum += Double.parseDouble(chartItem.getResourceItem()
                    .getResourceValue(SlotResolver.FONT_SIZE_SLOT).toString());
        }

        regularWedge = chart.add(Wedge.createWedge())
                .data(ArrayUtils.toJsArray(chartItems)).left(wedgeLeft)
                .bottom(wedgeBottom).innerRadius(highlightedWedgeOuterRadius)
                .outerRadius(regularWedgeOuterRadius).angle(wedgeAngle)
                .fillStyle(wedgeFillStyle).strokeStyle("white");

        regularWedge.anchor(wedgeLabelAnchor).add(Label.createLabel())
                .textAngle(wedgeTextAngle).text(regularWedgeLabelText);

        highlightedWedge = regularWedge.add(Wedge.createWedge()).innerRadius(0)
                .outerRadius(highlightedWedgeOuterRadius)
                .fillStyle(Colors.YELLOW).strokeStyle("black");

        highlightedWedge.anchor(wedgeLabelAnchor).add(Label.createLabel())
                .textAngle(wedgeTextAngle).text(highlightedWedgeLabelText);
    }

    @Override
    protected void registerEventHandler(String eventType,
            ProtovisEventHandler handler) {

        // XXX this is a problematic solution to the problem that events get
        // automatically
        // fired (asynchronously) when the wedge heights
        if (EVENT_TYPE_MOUSEOVER.equals(eventType)) {
            regularWedge.event(eventType, handler);
        } else {
            highlightedWedge.event(eventType, handler);
        }
    }

}