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
import org.thechiselgroup.choosel.core.client.views.filter.GreaterThanSlotValuePredicate;
import org.thechiselgroup.choosel.core.client.views.model.Slot;
import org.thechiselgroup.choosel.core.client.views.model.ViewItem;
import org.thechiselgroup.choosel.protovis.client.PV;
import org.thechiselgroup.choosel.protovis.client.PVAlignment;
import org.thechiselgroup.choosel.protovis.client.PVEventHandler;
import org.thechiselgroup.choosel.protovis.client.PVWedge;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsArgs;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsDoubleFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsStringFunction;
import org.thechiselgroup.choosel.visualization_component.chart.client.ChartViewContentDisplay;
import org.thechiselgroup.choosel.visualization_component.chart.client.functions.ViewItemColorSlotAccessor;
import org.thechiselgroup.choosel.visualization_component.chart.client.functions.ViewItemPredicateJsBooleanFunction;
import org.thechiselgroup.choosel.visualization_component.chart.client.functions.ViewItemStringSlotAccessor;

public class PieChart extends ChartViewContentDisplay {

    public static final Slot ANGLE_VALUE = new Slot("angleValue",
            "Angle Value", DataType.NUMBER);

    public static final Slot LABEL = new Slot("label", "Label", DataType.TEXT);

    public static final Slot PARTIAL_PERCENTAGE = new Slot("partialPercentage",
            "PartialPercentage", DataType.NUMBER);

    public static final Slot COLOR = new Slot("color", "Color", DataType.COLOR);

    public static final Slot BORDER_COLOR = new Slot("borderColor",
            "Border Color", DataType.COLOR);

    public static final Slot PARTIAL_COLOR = new Slot("partialColor",
            "Partial Color", DataType.COLOR);

    public static final Slot PARTIAL_BORDER_COLOR = new Slot(
            "partialBorderColor", "Partial Border Color", DataType.COLOR);

    public static final Slot[] SLOTS = new Slot[] { LABEL, ANGLE_VALUE,
            PARTIAL_PERCENTAGE, COLOR, BORDER_COLOR, PARTIAL_COLOR,
            PARTIAL_BORDER_COLOR };

    private final static int WEDGE_TEXT_ANGLE = 0;

    private JsDoubleFunction partialWedgeRadius = new JsDoubleFunction() {
        @Override
        public double f(JsArgs args) {
            ViewItem viewItem = args.getObject();
            double partialPercentage = viewItem
                    .getValueAsDouble(PARTIAL_PERCENTAGE);
            assert partialPercentage >= 0 && partialPercentage <= 1;

            // performance optimization
            if (partialPercentage == 0) {
                return 0;
            }

            /*
             * This was found to be a visually more accurate solution for both
             * small and large percentages compared to 'partialPercentage *
             * outerRadius' and to 'Math.sqrt(partialPercentage) * outerRadius'
             */
            return ((Math.sqrt(partialPercentage) + partialPercentage) / 2)
                    * outerRadius;
        }
    };

    private double sumOfAngleValues;

    private JsDoubleFunction outRadiusFunction = new JsDoubleFunction() {
        @Override
        public double f(JsArgs args) {
            return outerRadius;
        }
    };

    private int outerRadius;

    private PVWedge mainWedge;

    private PVWedge partialWedge;

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
            ViewItem viewItem = args.getObject();
            return viewItem.getValueAsDouble(ANGLE_VALUE) * 2 * Math.PI
                    / sumOfAngleValues;
        }
    };

    private String wedgeLabelAnchor = PVAlignment.CENTER;

    private JsStringFunction fullMarkLabelText = new ViewItemStringSlotAccessor(
            PieChart.LABEL);

    private JsStringFunction regularMarkLabelText = new JsStringFunction() {
        @Override
        public String f(JsArgs args) {
            ViewItem viewItem = args.getObject();

            double partialPercentage = viewItem
                    .getValueAsDouble(PARTIAL_PERCENTAGE);

            if (partialPercentage == 1) {
                return null;
            }

            return Double.toString(viewItem.getValueAsDouble(ANGLE_VALUE)
                    * (1 - partialPercentage));
        }
    };

    private JsStringFunction highlightedMarkLabelText = new JsStringFunction() {
        @Override
        public String f(JsArgs args) {
            ViewItem viewItem = args.getObject();

            double partialPercentage = viewItem
                    .getValueAsDouble(PARTIAL_PERCENTAGE);

            if (partialPercentage == 0) {
                return null;
            }

            return Double.toString(viewItem.getValueAsDouble(ANGLE_VALUE)
                    * partialPercentage);
        }
    };

    public final static String ID = "org.thechiselgroup.choosel.visualization_component.chart.PieChart";

    @Override
    protected void beforeRender() {
        super.beforeRender();

        calculateAllResourcesSum();
        calculateRegularWedgeOuterRadius();
    }

    @Override
    public void buildChart() {
        assert viewItemsJsArray.length() >= 1;

        mainWedge = getChart().add(PV.Wedge).data(viewItemsJsArray)
                .left(wedgeLeft).bottom(wedgeBottom)
                .innerRadius(partialWedgeRadius).outerRadius(outRadiusFunction)
                .angle(wedgeAngle)
                .fillStyle(new ViewItemColorSlotAccessor(COLOR))
                .strokeStyle(new ViewItemColorSlotAccessor(BORDER_COLOR));

        mainWedge.anchor(wedgeLabelAnchor).add(PV.Label)
                .textAngle(WEDGE_TEXT_ANGLE).text(regularMarkLabelText)
                .textStyle(Colors.WHITE);

        partialWedge = mainWedge
                .add(PV.Wedge)
                .visible(
                        new ViewItemPredicateJsBooleanFunction(
                                new GreaterThanSlotValuePredicate(
                                        PARTIAL_PERCENTAGE, 0)))
                .innerRadius(0)
                .outerRadius(partialWedgeRadius)
                .fillStyle(new ViewItemColorSlotAccessor(PARTIAL_COLOR))
                .strokeStyle(
                        new ViewItemColorSlotAccessor(PARTIAL_BORDER_COLOR));

        partialWedge.anchor(wedgeLabelAnchor).add(PV.Label)
                .textAngle(WEDGE_TEXT_ANGLE).text(highlightedMarkLabelText);

    }

    private void calculateAllResourcesSum() {
        sumOfAngleValues = 0;
        for (int i = 0; i < viewItemsJsArray.length(); i++) {
            sumOfAngleValues += viewItemsJsArray.get(i).getValueAsDouble(
                    ANGLE_VALUE);
        }
    }

    private void calculateRegularWedgeOuterRadius() {
        outerRadius = Math.min(height, width) / 2 - 5;
    }

    @Override
    public String getName() {
        return "Pie Chart";
    }

    @Override
    public Slot[] getSlots() {
        return SLOTS;
    }

    @Override
    protected void registerEventHandler(String eventType, PVEventHandler handler) {
        mainWedge.event(eventType, handler);
    }

}