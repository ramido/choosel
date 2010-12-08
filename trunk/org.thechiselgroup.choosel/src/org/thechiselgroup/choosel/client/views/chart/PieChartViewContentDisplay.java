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
package org.thechiselgroup.choosel.client.views.chart;

import org.thechiselgroup.choosel.client.ui.Colors;
import org.thechiselgroup.choosel.client.ui.widget.protovis.Alignment;
import org.thechiselgroup.choosel.client.ui.widget.protovis.Label;
import org.thechiselgroup.choosel.client.ui.widget.protovis.ProtovisEventHandler;
import org.thechiselgroup.choosel.client.ui.widget.protovis.ProtovisFunctionDouble;
import org.thechiselgroup.choosel.client.ui.widget.protovis.Wedge;
import org.thechiselgroup.choosel.client.util.collections.ArrayUtils;
import org.thechiselgroup.choosel.client.views.DragEnablerFactory;

import com.google.inject.Inject;

//Version of Pie chart with the average of the area 
//and the radius calculations for proportional highlighting.
//(i.e. ratio + sqrt(ratio) / 2)
public class PieChartViewContentDisplay extends ChartViewContentDisplay {

    private double[] highlightedWedgeCounts;

    private double[] regularWedgeCounts;

    private ProtovisFunctionDoubleWithCache<ChartItem> highlightedWedgeOuterRadius = new ProtovisFunctionDoubleWithCache<ChartItem>() {

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

    private ProtovisFunctionDouble<ChartItem> regularWedgeOuterRadius = new ProtovisFunctionDouble<ChartItem>() {
        @Override
        public double f(ChartItem value, int i) {
            return Math.min(height, width) / 2 - 5;
        }
    };

    private Wedge regularWedge;

    private Wedge highlightedWedge;

    private ProtovisFunctionDouble<ChartItem> wedgeLeft = new ProtovisFunctionDouble<ChartItem>() {
        @Override
        public double f(ChartItem value, int i) {
            return width / 2;
        }
    };

    private ProtovisFunctionDouble<ChartItem> wedgeBottom = new ProtovisFunctionDouble<ChartItem>() {
        @Override
        public double f(ChartItem value, int i) {
            return height / 2;
        }
    };

    private ProtovisFunctionDouble<ChartItem> wedgeAngle = new ProtovisFunctionDouble<ChartItem>() {
        @Override
        public double f(ChartItem value, int i) {
            return calculateAllResources(i) * 2 * Math.PI / sum;
        }
    };

    private String wedgeLabelAnchor = Alignment.CENTER;

    private int wedgeTextAngle = 0;

    @Inject
    public PieChartViewContentDisplay(DragEnablerFactory dragEnablerFactory) {

        super(dragEnablerFactory);
    }

    @Override
    protected void beforeRender() {
        super.beforeRender();
        highlightedWedgeOuterRadius.beforeRender();
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

        drawWedge();
    }

    private void drawWedge() {
        calculateAllResourcesSum();

        if (hasPartiallyHighlightedChartItems()) {
            regularWedge = chart.add(Wedge.createWedge())
                    .data(ArrayUtils.toJsArray(chartItems)).left(wedgeLeft)
                    .bottom(wedgeBottom)
                    .innerRadius(highlightedWedgeOuterRadius)
                    .outerRadius(regularWedgeOuterRadius).angle(wedgeAngle)
                    .fillStyle(partialHighlightingChartFillStyle)
                    .strokeStyle(Colors.WHITE);

            regularWedge.anchor(wedgeLabelAnchor).add(Label.createLabel())
                    .textAngle(wedgeTextAngle).text(regularMarkLabelText)
                    .textStyle(Colors.WHITE);

            highlightedWedge = regularWedge.add(Wedge.createWedge())
                    .innerRadius(0).outerRadius(highlightedWedgeOuterRadius)
                    .fillStyle(Colors.YELLOW);

            highlightedWedge.anchor(wedgeLabelAnchor).add(Label.createLabel())
                    .textAngle(wedgeTextAngle).text(highlightedMarkLabelText);
            return;
        }

        regularWedge = chart.add(Wedge.createWedge())
                .data(ArrayUtils.toJsArray(chartItems)).left(wedgeLeft)
                .bottom(wedgeBottom).outerRadius(regularWedgeOuterRadius)
                .angle(wedgeAngle).fillStyle(chartFillStyle)
                .strokeStyle(Colors.WHITE);

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