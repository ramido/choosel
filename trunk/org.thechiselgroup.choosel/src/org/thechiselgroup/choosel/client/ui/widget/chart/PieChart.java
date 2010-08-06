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
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.ProtovisFunctionDouble;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.ProtovisFunctionDoubleWithCache;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.ProtovisFunctionString;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Wedge;
import org.thechiselgroup.choosel.client.util.ArrayUtils;
import org.thechiselgroup.choosel.client.views.SlotResolver;
import org.thechiselgroup.choosel.client.views.chart.ChartItem;

public class PieChart extends ChartWidget {

    private static final int WEDGE_PADDING = 25;

    private static final int BORDER_HEIGHT = 10;

    private String[] wedgeColors = { "lightblue", "blue", "steelblue" };

    private double[] wedgeCounts;

    private double[] highlightedWedgeCounts;

    private Wedge wedge;

    private ProtovisFunctionDoubleWithCache wedgeRadius = new ProtovisFunctionDoubleWithCache() {

        @Override
        public void beforeRender() {
            if (chartItems.isEmpty()) {
                return;
            }

            highlightedWedgeCounts = new double[chartItems.size()];

            for (int i = 0; i < chartItems.size(); i++) {
                highlightedWedgeCounts[i] = chartItems.get(i).getResourceItem()
                        .getHighlightedResources().size();
            }

        }

        @Override
        public double f(ChartItem value, int index) {
            return highlightedWedgeCounts[index] * chartHeight / maxWedgeSize;
        }

    };

    private ProtovisFunctionDoubleWithCache wedgeRadius2 = new ProtovisFunctionDoubleWithCache() {

        @Override
        public void beforeRender() {
            if (chartItems.isEmpty()) {
                return;
            }

            wedgeCounts = new double[chartItems.size()];

            for (int i = 0; i < chartItems.size(); i++) {
                wedgeCounts[i] = Integer.parseInt(chartItems.get(i)
                        .getResourceItem()
                        .getResourceValue(SlotResolver.FONT_SIZE_SLOT)
                        .toString())
                        - chartItems.get(i).getResourceItem()
                                .getHighlightedResources().size();
            }

        }

        @Override
        public double f(ChartItem value, int index) {
            return wedgeCounts[index] * chartHeight / maxWedgeSize;
        }

    };

    private ProtovisFunctionString wedgeFillStyle = new ProtovisFunctionString() {
        @Override
        public String f(ChartItem value, int index) {
            switch (value.getResourceItem().getStatus()) {
            case PARTIALLY_HIGHLIGHTED:
            case PARTIALLY_HIGHLIGHTED_SELECTED:
            case HIGHLIGHTED_SELECTED:
            case HIGHLIGHTED:
                return Colors.YELLOW;
            case DEFAULT:
                return Colors.STEELBLUE;
            case SELECTED:
                return Colors.ORANGE;
            }
            throw new RuntimeException("No colour available");
        }
    };

    protected double chartHeight;

    protected double chartWidth;

    private double maxWedgeSize = 0;

    private double sum;

    @Override
    protected void beforeRender() {
        wedgeRadius.beforeRender();
        wedgeRadius2.beforeRender();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Wedge drawChart() {
        assert chartItems.size() >= 1;

        drawWedge();

        return wedge;
    }

    private void drawWedge() {
        sum = 0;
        for (ChartItem chartItem : chartItems) {
            sum += Double.parseDouble(chartItem.getResourceItem()
                    .getResourceValue(SlotResolver.FONT_SIZE_SLOT).toString());
        }

        wedge = chart.add(Wedge.createWedge())
                .data(ArrayUtils.toJsArray(chartItems)).left(width / 2)
                .bottom(height / 2)
                .outerRadius(Math.min(height, width) / 2 - 5)
                .angle(new ProtovisFunctionDouble() {
                    @Override
                    public double f(ChartItem value, int index) {
                        System.out.println("a: " + index);
                        return Double.parseDouble(value.getResourceItem()
                                .getResourceValue(SlotResolver.FONT_SIZE_SLOT)
                                .toString())
                                * 2 * Math.PI / sum;
                    }
                }).fillStyle(new ProtovisFunctionString() {
                    @Override
                    public String f(ChartItem value, int index) {
                        System.out.println("b: " + index);
                        return wedgeColors[index % wedgeColors.length];
                    }
                });
    }

}