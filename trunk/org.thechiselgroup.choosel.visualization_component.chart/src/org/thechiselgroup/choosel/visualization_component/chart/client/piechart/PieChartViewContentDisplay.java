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
package org.thechiselgroup.choosel.visualization_component.chart.client.piechart;

import org.thechiselgroup.choosel.core.client.ui.Colors;
import org.thechiselgroup.choosel.core.client.views.DragEnablerFactory;
import org.thechiselgroup.choosel.core.client.views.ViewItem.Subset;
import org.thechiselgroup.choosel.core.client.views.slots.Slot;
import org.thechiselgroup.choosel.protovis.client.PV;
import org.thechiselgroup.choosel.protovis.client.PVAlignment;
import org.thechiselgroup.choosel.protovis.client.PVEventHandler;
import org.thechiselgroup.choosel.protovis.client.PVMark;
import org.thechiselgroup.choosel.protovis.client.PVWedge;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsArgs;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsDoubleFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsStringFunction;
import org.thechiselgroup.choosel.visualization_component.chart.client.ChartItem;
import org.thechiselgroup.choosel.visualization_component.chart.client.ChartViewContentDisplay;

import com.google.inject.Inject;

//Version of Pie chart with the average of the area 
//and the radius calculations for proportional highlighting.
//(i.e. ratio + sqrt(ratio) / 2)
public class PieChartViewContentDisplay extends ChartViewContentDisplay {

    private double[] highlightedWedgeCounts;

    private double[] regularWedgeCounts;

    private JsDoubleFunction highlightedWedgeOuterRadius = new JsDoubleFunction() {
        @Override
        public double f(JsArgs args) {
            int i = args.<PVMark> getThis().index();
            return (Math
                    .sqrt(highlightedWedgeCounts[i] / regularWedgeCounts[i])
                    * calculateRegularWedgeOuterRadius() + highlightedWedgeCounts[i]
                    / regularWedgeCounts[i]
                    * calculateRegularWedgeOuterRadius()) / 2;
        }
    };

    private double sum;

    private JsDoubleFunction regularWedgeOuterRadius = new JsDoubleFunction() {
        @Override
        public double f(JsArgs args) {
            return calculateRegularWedgeOuterRadius();
        }

    };

    private PVWedge regularWedge;

    private PVWedge highlightedWedge;

    private JsDoubleFunction wedgeLeft = new JsDoubleFunction() {
        @Override
        public double f(JsArgs args) {
            return width / 2;
        }
    };

    private JsDoubleFunction wedgeBottom = new JsDoubleFunction() {
        @Override
        public double f(JsArgs args) {
            return height / 2;
        }
    };

    private JsDoubleFunction wedgeAngle = new JsDoubleFunction() {
        @Override
        public double f(JsArgs args) {
            ChartItem chartItem = args.getObject();
            return calculateChartItemValue(chartItem,
                    PieChartVisualization.PIE_ANGLE_SLOT, Subset.ALL)
                    * 2
                    * Math.PI / sum;
        }
    };

    private String wedgeLabelAnchor = PVAlignment.CENTER;

    private int wedgeTextAngle = 0;

    private JsStringFunction chartFillStyle = new JsStringFunction() {
        @Override
        public String f(JsArgs args) {
            return args.<ChartItem> getObject().getColor();
        }
    };

    private JsStringFunction fullMarkTextStyle = new JsStringFunction() {
        @Override
        public String f(JsArgs args) {
            ChartItem chartItem = args.getObject();
            return calculateChartItemValue(chartItem,
                    PieChartVisualization.PIE_ANGLE_SLOT, Subset.HIGHLIGHTED) == 0 ? Colors.WHITE
                    : Colors.BLACK;
        }
    };

    private JsStringFunction fullMarkLabelText = new JsStringFunction() {
        @Override
        public String f(JsArgs args) {
            ChartItem chartItem = args.getObject();
            return chartItem.getViewItem().getSlotValue(
                    PieChartVisualization.PIE_LABEL_SLOT);
        }

    };

    // XXX fix label
    private JsStringFunction regularMarkLabelText = new JsStringFunction() {
        @Override
        public String f(JsArgs args) {
            ChartItem chartItem = args.getObject();
            return calculateChartItemValue(chartItem,
                    PieChartVisualization.PIE_ANGLE_SLOT, Subset.ALL)
                    - calculateChartItemValue(chartItem,
                            PieChartVisualization.PIE_ANGLE_SLOT,
                            Subset.HIGHLIGHTED) < 1 ? null : Double
                    .toString(calculateChartItemValue(chartItem,
                            PieChartVisualization.PIE_ANGLE_SLOT, Subset.ALL)
                            - calculateChartItemValue(chartItem,
                                    PieChartVisualization.PIE_ANGLE_SLOT,
                                    Subset.HIGHLIGHTED));
        }
    };

    // XXX fix label
    private JsStringFunction highlightedMarkLabelText = new JsStringFunction() {
        @Override
        public String f(JsArgs args) {
            ChartItem chartItem = args.getObject();
            return calculateChartItemValue(chartItem,
                    PieChartVisualization.PIE_ANGLE_SLOT, Subset.HIGHLIGHTED) <= 0 ? null
                    : Double.toString(calculateChartItemValue(chartItem,
                            PieChartVisualization.PIE_ANGLE_SLOT,
                            Subset.HIGHLIGHTED));
        }
    };

    @Inject
    public PieChartViewContentDisplay(DragEnablerFactory dragEnablerFactory) {
        super(dragEnablerFactory);
    }

    @Override
    protected void beforeRender() {
        super.beforeRender();

        calculateMaximumChartItemValue();

        if (chartItemsJsArray.length() == 0) {
            return;
        }

        highlightedWedgeCounts = new double[chartItemsJsArray.length()];
        regularWedgeCounts = new double[chartItemsJsArray.length()];

        for (int i = 0; i < chartItemsJsArray.length(); i++) {
            ChartItem chartItem = chartItemsJsArray.get(i);
            highlightedWedgeCounts[i] = calculateChartItemValue(chartItem,
                    PieChartVisualization.PIE_ANGLE_SLOT, Subset.HIGHLIGHTED);
            regularWedgeCounts[i] = calculateChartItemValue(chartItem,
                    PieChartVisualization.PIE_ANGLE_SLOT, Subset.ALL);
        }
    }

    private void calculateAllResourcesSum() {
        sum = 0;
        for (int i = 0; i < chartItemsJsArray.length(); i++) {
            sum += calculateChartItemValue(chartItemsJsArray.get(i),
                    PieChartVisualization.PIE_ANGLE_SLOT, Subset.ALL);
        }
    }

    protected void calculateMaximumChartItemValue() {
        maxChartItemValue = 0;
        for (int i = 0; i < chartItemsJsArray.length(); i++) {
            double currentItemValue = calculateChartItemValue(
                    chartItemsJsArray.get(i),
                    PieChartVisualization.PIE_ANGLE_SLOT, Subset.ALL);
            if (maxChartItemValue < currentItemValue) {
                maxChartItemValue = currentItemValue;
            }
        }
    }

    private int calculateRegularWedgeOuterRadius() {
        return Math.min(height, width) / 2 - 5;
    }

    @Override
    public void drawChart() {
        assert chartItemsJsArray.length() >= 1;

        drawWedge();
    }

    private void drawWedge() {
        calculateAllResourcesSum();

        if (hasPartiallyHighlightedChartItems()) {
            regularWedge = getChart().add(PV.Wedge).data(chartItemsJsArray)
                    .left(wedgeLeft).bottom(wedgeBottom)
                    .innerRadius(highlightedWedgeOuterRadius)
                    .outerRadius(regularWedgeOuterRadius).angle(wedgeAngle)
                    .fillStyle(partialHighlightingChartFillStyle)
                    .strokeStyle(Colors.WHITE);

            regularWedge.anchor(wedgeLabelAnchor).add(PV.Label)
                    .textAngle(wedgeTextAngle).text(regularMarkLabelText)
                    .textStyle(Colors.WHITE);

            highlightedWedge = regularWedge.add(PV.Wedge).innerRadius(0)
                    .outerRadius(highlightedWedgeOuterRadius)
                    .fillStyle(Colors.YELLOW);

            highlightedWedge.anchor(wedgeLabelAnchor).add(PV.Label)
                    .textAngle(wedgeTextAngle).text(highlightedMarkLabelText);
            return;
        }

        regularWedge = getChart().add(PV.Wedge).data(chartItemsJsArray)
                .left(wedgeLeft).bottom(wedgeBottom)
                .outerRadius(regularWedgeOuterRadius).angle(wedgeAngle)
                .fillStyle(chartFillStyle).strokeStyle(Colors.WHITE);

        regularWedge.anchor(wedgeLabelAnchor).add(PV.Label)
                .textAngle(wedgeTextAngle).text(fullMarkLabelText)
                .textStyle(fullMarkTextStyle);

    }

    @Override
    public String getName() {
        return "Pie Chart";
    }

    @Override
    public Slot[] getSlots() {
        return new Slot[] { PieChartVisualization.PIE_LABEL_SLOT,
                PieChartVisualization.PIE_ANGLE_SLOT };
    }

    @Override
    protected void registerEventHandler(String eventType, PVEventHandler handler) {
        regularWedge.event(eventType, handler);
    }

}