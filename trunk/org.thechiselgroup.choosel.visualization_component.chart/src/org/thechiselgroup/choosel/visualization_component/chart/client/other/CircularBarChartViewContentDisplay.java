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
package org.thechiselgroup.choosel.visualization_component.chart.client.other;

import org.thechiselgroup.choosel.core.client.ui.Colors;
import org.thechiselgroup.choosel.core.client.util.collections.ArrayUtils;
import org.thechiselgroup.choosel.core.client.views.model.Slot;
import org.thechiselgroup.choosel.core.client.views.model.VisualItem.Subset;
import org.thechiselgroup.choosel.protovis.client.PV;
import org.thechiselgroup.choosel.protovis.client.PVAlignment;
import org.thechiselgroup.choosel.protovis.client.PVEventHandler;
import org.thechiselgroup.choosel.protovis.client.PVLinearScale;
import org.thechiselgroup.choosel.protovis.client.PVMark;
import org.thechiselgroup.choosel.protovis.client.PVWedge;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsArgs;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsDoubleFunction;
import org.thechiselgroup.choosel.visualization_component.chart.client.ChartViewContentDisplay;
import org.thechiselgroup.choosel.visualization_component.chart.client.barchart.BarChart;

//Version of Pie chart with the average of the area 
//and the radius calculations for proportional highlighting.
//(i.e. ratio + sqrt(ratio) / 2)
public class CircularBarChartViewContentDisplay extends ChartViewContentDisplay {

    private double[] highlightedWedgeCounts;

    private double[] regularWedgeCounts;

    private static final int MARGIN_SIZE = 15;

    private JsDoubleFunction highlightedWedgeOuterRadius = new JsDoubleFunction() {
        @Override
        public double f(JsArgs args) {
            int i = args.<PVMark> getThis().index();
            return calculateRegularWedgeOuterRadius(i)
                    * highlightedWedgeCounts[i] / regularWedgeCounts[i];
        }
    };

    protected double maxChartItemValue;

    private double sum;

    private JsDoubleFunction regularWedgeOuterRadius = new JsDoubleFunction() {
        @Override
        public double f(JsArgs args) {
            return calculateRegularWedgeOuterRadius(args.<PVMark> getThis()
                    .index());
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
            return 2 * Math.PI / viewItemsJsArray.length();
        }
    };

    private String wedgeLabelAnchor = PVAlignment.CENTER;

    private int wedgeTextAngle = 0;

    private int highlightedWedgeInnerRadius = 0;

    private JsDoubleFunction scaleRadius = new JsDoubleFunction() {
        @Override
        public double f(JsArgs args) {
            double value = args.getDouble();
            return value * (Math.min(height, width) - MARGIN_SIZE)
                    / (maxChartItemValue * 2);
        }
    };

    private int scaleLineWidth = 1;

    // private JsStringFunction fullMarkTextStyle = new JsStringFunction() {
    // @Override
    // public String f(JsArgs args) {
    // ViewItem viewItem = args.getObject();
    // return viewItem.getValueAsDouble(BarChart.BAR_LENGTH,
    // Subset.HIGHLIGHTED) == 0 ? Colors.WHITE : Colors.BLACK;
    // }
    // };
    //
    // private JsStringFunction fullMarkLabelText = new JsStringFunction() {
    // @Override
    // public String f(JsArgs args) {
    // ChartItem chartItem = args.getObject();
    // return StringUtils.formatDecimal(chartItem.getSlotValueAsDouble(
    // BarChart.BAR_LENGTH, Subset.ALL), 2);
    // }
    //
    // };
    //
    // private JsStringFunction regularMarkLabelText = new JsStringFunction() {
    // @Override
    // public String f(JsArgs args) {
    // ChartItem chartItem = args.getObject();
    // return chartItem.getSlotValueAsDouble(BarChart.BAR_LENGTH,
    // Subset.ALL)
    // - chartItem.getSlotValueAsDouble(BarChart.BAR_LENGTH,
    // Subset.HIGHLIGHTED) < 1 ? null : Double
    // .toString(chartItem.getSlotValueAsDouble(
    // BarChart.BAR_LENGTH, Subset.ALL)
    // - chartItem.getSlotValueAsDouble(
    // BarChart.BAR_LENGTH, Subset.HIGHLIGHTED));
    // }
    // };
    //
    // private JsStringFunction highlightedMarkLabelText = new
    // JsStringFunction() {
    // @Override
    // public String f(JsArgs args) {
    // ChartItem chartItem = args.getObject();
    // return chartItem.getSlotValueAsDouble(BarChart.BAR_LENGTH,
    // Subset.HIGHLIGHTED) <= 0 ? null : Double.toString(chartItem
    // .getSlotValueAsDouble(BarChart.BAR_LENGTH,
    // Subset.HIGHLIGHTED));
    // }
    // };

    @Override
    protected void beforeRender() {
        super.beforeRender();

        // calculateMaximumChartItemValue();

        if (viewItemsJsArray.length() == 0) {
            return;
        }

        highlightedWedgeCounts = new double[viewItemsJsArray.length()];
        //
        // for (int i = 0; i < viewItemsJsArray.length(); i++) {
        // highlightedWedgeCounts[i] = viewItemsJsArray.get(i)
        // .getSlotValueAsDouble(BarChart.BAR_LENGTH,
        // Subset.HIGHLIGHTED);
        // }
        //
        // regularWedgeCounts = new double[viewItemsJsArray.length()];
        //
        // for (int i = 0; i < viewItemsJsArray.length(); i++) {
        // regularWedgeCounts[i] = viewItemsJsArray.get(i)
        // .getSlotValueAsDouble(BarChart.BAR_LENGTH, Subset.ALL);
        // }
    }

    @Override
    public void buildChart() {
        assert viewItemsJsArray.length() > 0;

        // calculateMaximumChartItemValue();
        drawScale();
        drawWedge();
    }

    // private void calculateAllResourcesSum() {
    // sum = 0;
    // for (int i = 0; i < viewItemsJsArray.length(); i++) {
    // sum += viewItemsJsArray.get(i).getSlotValueAsDouble(
    // BarChart.BAR_LENGTH, Subset.ALL);
    // }
    // }
    //
    // protected void calculateMaximumChartItemValue() {
    // maxChartItemValue = 0;
    // for (int i = 0; i < viewItemsJsArray.length(); i++) {
    // double currentItemValue = viewItemsJsArray.get(i)
    // .getSlotValueAsDouble(BarChart.BAR_LENGTH, Subset.ALL);
    // if (maxChartItemValue < currentItemValue) {
    // maxChartItemValue = currentItemValue;
    // }
    // }
    // }

    private double calculateRegularWedgeOuterRadius(int i) {
        return regularWedgeCounts[i] * (Math.min(height, width) - MARGIN_SIZE)
                / (ArrayUtils.max(regularWedgeCounts) * 2);
    }

    private void drawScale() {
        PVLinearScale scale = PV.Scale.linear(0, maxChartItemValue).range(0,
                Math.min(height, width) - MARGIN_SIZE);

        getChart().add(PV.Dot).data(scale.ticks()).left(width / 2)
                .bottom(height / 2).fillStyle("").strokeStyle(Colors.GRAY_1)
                .lineWidth(scaleLineWidth).radius(scaleRadius);
    }

    private void drawWedge() {
        // calculateAllResourcesSum();

        regularWedge = getChart().add(PV.Wedge).data(viewItemsJsArray)
                .left(wedgeLeft).bottom(wedgeBottom)
                .outerRadius(regularWedgeOuterRadius).angle(wedgeAngle)
                .strokeStyle(Colors.WHITE);

        if (hasViewItemsWithPartialSubset(Subset.HIGHLIGHTED)) {
            regularWedge.innerRadius(highlightedWedgeOuterRadius).fillStyle(
                    Colors.STEELBLUE);

            regularWedge.anchor(wedgeLabelAnchor).add(PV.Label)
                    .textAngle(wedgeTextAngle)
                    // .text(regularMarkLabelText)
                    .textStyle(Colors.WHITE);

            highlightedWedge = regularWedge.add(PV.Wedge)
                    .innerRadius(highlightedWedgeInnerRadius)
                    .outerRadius(highlightedWedgeOuterRadius)
                    .fillStyle(Colors.YELLOW);

            highlightedWedge.anchor(wedgeLabelAnchor).add(PV.Label)
                    .textAngle(wedgeTextAngle);
            // .text(highlightedMarkLabelText);

            return;
        }

        // regularWedge.innerRadius(0).fillStyle(new ChartItemColorFunction());
        //
        // regularWedge.anchor(wedgeLabelAnchor).add(PV.Label)
        // .textAngle(wedgeTextAngle).text(fullMarkLabelText)
        // .textStyle(fullMarkTextStyle);

    }

    @Override
    public String getName() {
        return "Circular Bar Chart";
    }

    @Override
    public Slot[] getSlots() {
        return new Slot[] { BarChart.BAR_LABEL, BarChart.BAR_LENGTH };
    }

    @Override
    protected void registerEventHandler(String eventType, PVEventHandler handler) {
        regularWedge.event(eventType, handler);
    }

}