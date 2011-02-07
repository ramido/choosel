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
package org.thechiselgroup.choosel.visualization_component.chart.client;

import org.thechiselgroup.choosel.core.client.ui.Colors;
import org.thechiselgroup.choosel.core.client.util.StringUtils;
import org.thechiselgroup.choosel.core.client.util.collections.ArrayUtils;
import org.thechiselgroup.choosel.core.client.views.DragEnablerFactory;
import org.thechiselgroup.choosel.core.client.views.ViewItem.Subset;
import org.thechiselgroup.choosel.core.client.views.slots.Slot;
import org.thechiselgroup.choosel.protovis.client.PV;
import org.thechiselgroup.choosel.protovis.client.PVAlignment;
import org.thechiselgroup.choosel.protovis.client.PVEventHandler;
import org.thechiselgroup.choosel.protovis.client.PVLinearScale;
import org.thechiselgroup.choosel.protovis.client.PVMark;
import org.thechiselgroup.choosel.protovis.client.PVScale;
import org.thechiselgroup.choosel.protovis.client.PVWedge;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsArgs;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsDoubleFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsStringFunction;
import org.thechiselgroup.choosel.visualization_component.chart.client.barchart.BarChartVisualization;

import com.google.inject.Inject;

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
            return 2 * Math.PI / chartItemsJsArray.length();
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
                    / (getMaximumChartItemValue() * 2);
        }
    };

    private int scaleLineWidth = 1;

    private JsStringFunction fullMarkTextStyle = new JsStringFunction() {
        @Override
        public String f(JsArgs args) {
            ChartItem chartItem = args.getObject();
            return chartItem.getSlotValueAsDouble(BarChartVisualization.BAR_LENGTH_SLOT, Subset.HIGHLIGHTED) == 0 ? Colors.WHITE
                    : Colors.BLACK;
        }
    };

    private JsStringFunction fullMarkLabelText = new JsStringFunction() {
        @Override
        public String f(JsArgs args) {
            ChartItem chartItem = args.getObject();
            return StringUtils.formatDecimal(
                    chartItem.getSlotValueAsDouble(BarChartVisualization.BAR_LENGTH_SLOT, Subset.ALL),
                    2);
        }

    };

    private JsStringFunction regularMarkLabelText = new JsStringFunction() {
        @Override
        public String f(JsArgs args) {
            ChartItem chartItem = args.getObject();
            return chartItem.getSlotValueAsDouble(BarChartVisualization.BAR_LENGTH_SLOT, Subset.ALL)
                    - chartItem.getSlotValueAsDouble(BarChartVisualization.BAR_LENGTH_SLOT,
                            Subset.HIGHLIGHTED) < 1 ? null : Double
                    .toString(chartItem.getSlotValueAsDouble(BarChartVisualization.BAR_LENGTH_SLOT, Subset.ALL)
                            - chartItem.getSlotValueAsDouble(BarChartVisualization.BAR_LENGTH_SLOT,
                                    Subset.HIGHLIGHTED));
        }
    };

    private JsStringFunction highlightedMarkLabelText = new JsStringFunction() {
        @Override
        public String f(JsArgs args) {
            ChartItem chartItem = args.getObject();
            return chartItem.getSlotValueAsDouble(BarChartVisualization.BAR_LENGTH_SLOT, Subset.HIGHLIGHTED) <= 0 ? null
                    : Double.toString(chartItem.getSlotValueAsDouble(BarChartVisualization.BAR_LENGTH_SLOT,
                            Subset.HIGHLIGHTED));
        }
    };

    @Inject
    public CircularBarChartViewContentDisplay(
            DragEnablerFactory dragEnablerFactory) {

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

        for (int i = 0; i < chartItemsJsArray.length(); i++) {
            highlightedWedgeCounts[i] = chartItemsJsArray.get(i).getSlotValueAsDouble(
                    BarChartVisualization.BAR_LENGTH_SLOT, Subset.HIGHLIGHTED);
        }

        regularWedgeCounts = new double[chartItemsJsArray.length()];

        for (int i = 0; i < chartItemsJsArray.length(); i++) {
            regularWedgeCounts[i] = chartItemsJsArray.get(i).getSlotValueAsDouble(
                    BarChartVisualization.BAR_LENGTH_SLOT, Subset.ALL);
        }
    }

    private void calculateAllResourcesSum() {
        sum = 0;
        for (int i = 0; i < chartItemsJsArray.length(); i++) {
            sum += chartItemsJsArray.get(i).getSlotValueAsDouble(BarChartVisualization.BAR_LENGTH_SLOT, Subset.ALL);
        }
    }

    protected void calculateMaximumChartItemValue() {
        maxChartItemValue = 0;
        for (int i = 0; i < chartItemsJsArray.length(); i++) {
            double currentItemValue = chartItemsJsArray.get(i).getSlotValueAsDouble(
                    BarChartVisualization.BAR_LENGTH_SLOT, Subset.ALL);
            if (maxChartItemValue < currentItemValue) {
                maxChartItemValue = currentItemValue;
            }
        }
    }

    private double calculateRegularWedgeOuterRadius(int i) {
        return regularWedgeCounts[i] * (Math.min(height, width) - MARGIN_SIZE)
                / (ArrayUtils.max(regularWedgeCounts) * 2);
    }

    @Override
    public void drawChart() {
        assert chartItemsJsArray.length() > 0;

        calculateMaximumChartItemValue();
        drawScale();
        drawWedge();
    }

    private void drawScale() {
        PVLinearScale scale = PVScale.linear(0, getMaximumChartItemValue())
                .range(0, Math.min(height, width) - MARGIN_SIZE);

        getChart().add(PV.Dot).data(scale.ticks()).left(width / 2)
                .bottom(height / 2).fillStyle("").strokeStyle(Colors.GRAY_1)
                .lineWidth(scaleLineWidth).radius(scaleRadius);
    }

    private void drawWedge() {
        calculateAllResourcesSum();

        regularWedge = getChart().add(PV.Wedge).data(chartItemsJsArray)
                .left(wedgeLeft).bottom(wedgeBottom)
                .outerRadius(regularWedgeOuterRadius).angle(wedgeAngle)
                .strokeStyle(Colors.WHITE);

        if (hasPartiallyHighlightedChartItems()) {
            regularWedge.innerRadius(highlightedWedgeOuterRadius).fillStyle(
                    Colors.STEELBLUE);

            regularWedge.anchor(wedgeLabelAnchor).add(PV.Label)
                    .textAngle(wedgeTextAngle).text(regularMarkLabelText)
                    .textStyle(Colors.WHITE);

            highlightedWedge = regularWedge.add(PV.Wedge)
                    .innerRadius(highlightedWedgeInnerRadius)
                    .outerRadius(highlightedWedgeOuterRadius)
                    .fillStyle(Colors.YELLOW);

            highlightedWedge.anchor(wedgeLabelAnchor).add(PV.Label)
                    .textAngle(wedgeTextAngle).text(highlightedMarkLabelText);

            return;
        }

        regularWedge.innerRadius(0).fillStyle(chartFillStyle);

        regularWedge.anchor(wedgeLabelAnchor).add(PV.Label)
                .textAngle(wedgeTextAngle).text(fullMarkLabelText)
                .textStyle(fullMarkTextStyle);

    }

    @Override
    public String getName() {
        return "Circular Bar Chart";
    }

    @Override
    public Slot[] getSlots() {
        return new Slot[] { BarChartVisualization.BAR_LABEL_SLOT,
                BarChartVisualization.BAR_LENGTH_SLOT };
    }

    @Override
    protected void registerEventHandler(String eventType, PVEventHandler handler) {
        regularWedge.event(eventType, handler);
    }

}