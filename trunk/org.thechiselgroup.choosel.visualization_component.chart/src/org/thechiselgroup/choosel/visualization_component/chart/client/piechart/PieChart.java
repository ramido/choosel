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

import org.thechiselgroup.choosel.core.client.resources.DataType;
import org.thechiselgroup.choosel.core.client.ui.Colors;
import org.thechiselgroup.choosel.core.client.views.ViewItem.Status;
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
import org.thechiselgroup.choosel.visualization_component.chart.client.ChartItemColorFunction;
import org.thechiselgroup.choosel.visualization_component.chart.client.ChartItemStringSlotAccessor;
import org.thechiselgroup.choosel.visualization_component.chart.client.ChartViewContentDisplay;

//Version of Pie chart with the average of the area 
//and the radius calculations for proportional highlighting.
//(i.e. ratio + sqrt(ratio) / 2)
public class PieChart extends ChartViewContentDisplay {

    private double[] highlightedWedgeCounts;

    private double[] regularWedgeCounts;

    private JsStringFunction partialHighlightingChartFillStyle = new JsStringFunction() {
        @Override
        public String f(JsArgs args) {
            ChartItem value = args.getObject();
            Status status = value.getViewItem().getStatus();
            return status == Status.PARTIALLY_HIGHLIGHTED ? Colors.STEELBLUE
                    : status == Status.PARTIALLY_HIGHLIGHTED_SELECTED ? Colors.ORANGE
                            : value.getColor();
        }
    };

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
            return chartItem.getSlotValueAsDouble(PieChart.PIE_ANGLE_SLOT,
                    Subset.ALL) * 2 * Math.PI / sum;
        }
    };

    private String wedgeLabelAnchor = PVAlignment.CENTER;

    private int wedgeTextAngle = 0;

    private JsStringFunction fullMarkTextStyle = new JsStringFunction() {
        @Override
        public String f(JsArgs args) {
            ChartItem chartItem = args.getObject();
            return chartItem.getSlotValueAsDouble(PieChart.PIE_ANGLE_SLOT,
                    Subset.HIGHLIGHTED) == 0 ? Colors.WHITE : Colors.BLACK;
        }
    };

    private JsStringFunction fullMarkLabelText = new ChartItemStringSlotAccessor(
            PieChart.PIE_LABEL_SLOT);

    // XXX fix label
    private JsStringFunction regularMarkLabelText = new JsStringFunction() {
        @Override
        public String f(JsArgs args) {
            ChartItem chartItem = args.getObject();
            return chartItem.getSlotValueAsDouble(PieChart.PIE_ANGLE_SLOT,
                    Subset.ALL)
                    - chartItem.getSlotValueAsDouble(PieChart.PIE_ANGLE_SLOT,
                            Subset.HIGHLIGHTED) < 1 ? null : Double
                    .toString(chartItem.getSlotValueAsDouble(
                            PieChart.PIE_ANGLE_SLOT, Subset.ALL)
                            - chartItem
                                    .getSlotValueAsDouble(
                                            PieChart.PIE_ANGLE_SLOT,
                                            Subset.HIGHLIGHTED));
        }
    };

    // XXX fix label
    private JsStringFunction highlightedMarkLabelText = new JsStringFunction() {
        @Override
        public String f(JsArgs args) {
            ChartItem chartItem = args.getObject();
            return chartItem.getSlotValueAsDouble(PieChart.PIE_ANGLE_SLOT,
                    Subset.HIGHLIGHTED) <= 0 ? null : Double.toString(chartItem
                    .getSlotValueAsDouble(PieChart.PIE_ANGLE_SLOT,
                            Subset.HIGHLIGHTED));
        }
    };

    private double maxChartItemValue;

    public static final Slot PIE_ANGLE_SLOT = new Slot("pie.angle",
            "Pie Angle", DataType.NUMBER);

    public static final Slot PIE_LABEL_SLOT = new Slot("pie.label", "Label",
            DataType.TEXT);

    public final static String ID = "org.thechiselgroup.choosel.visualization_component.chart.PieChart";

    @Override
    protected void beforeRender() {
        super.beforeRender();

        if (chartItemsJsArray.length() == 0) {
            return;
        }

        calculateMaximumChartItemValue();

        highlightedWedgeCounts = new double[chartItemsJsArray.length()];
        regularWedgeCounts = new double[chartItemsJsArray.length()];

        for (int i = 0; i < chartItemsJsArray.length(); i++) {
            ChartItem chartItem = chartItemsJsArray.get(i);
            highlightedWedgeCounts[i] = chartItem.getSlotValueAsDouble(
                    PieChart.PIE_ANGLE_SLOT, Subset.HIGHLIGHTED);
            regularWedgeCounts[i] = chartItem.getSlotValueAsDouble(
                    PieChart.PIE_ANGLE_SLOT, Subset.ALL);
        }
    }

    @Override
    public void buildChart() {
        assert chartItemsJsArray.length() >= 1;

        drawWedge();
    }

    private void calculateAllResourcesSum() {
        sum = 0;
        for (int i = 0; i < chartItemsJsArray.length(); i++) {
            sum += chartItemsJsArray.get(i).getSlotValueAsDouble(
                    PieChart.PIE_ANGLE_SLOT, Subset.ALL);
        }
    }

    protected void calculateMaximumChartItemValue() {
        maxChartItemValue = 0;
        for (int i = 0; i < chartItemsJsArray.length(); i++) {
            double currentItemValue = chartItemsJsArray.get(i)
                    .getSlotValueAsDouble(PieChart.PIE_ANGLE_SLOT, Subset.ALL);
            if (maxChartItemValue < currentItemValue) {
                maxChartItemValue = currentItemValue;
            }
        }
    }

    private int calculateRegularWedgeOuterRadius() {
        return Math.min(height, width) / 2 - 5;
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
                .fillStyle(new ChartItemColorFunction())
                .strokeStyle(Colors.WHITE);

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
        return new Slot[] { PieChart.PIE_LABEL_SLOT, PieChart.PIE_ANGLE_SLOT };
    }

    @Override
    protected void registerEventHandler(String eventType, PVEventHandler handler) {
        regularWedge.event(eventType, handler);
    }

}