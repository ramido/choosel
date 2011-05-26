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

public class PieChart extends ChartViewContentDisplay {

    public static final Slot VALUE = new Slot("value", "Value", DataType.NUMBER);

    public static final Slot LABEL = new Slot("label", "Label", DataType.TEXT);

    public static final Slot PARTIAL_VALUE = new Slot("partialValue",
            "Partial Value", DataType.NUMBER);

    public static final Slot COLOR = new Slot("color", "Color", DataType.COLOR);

    public static final Slot BORDER_COLOR = new Slot("borderColor",
            "Border Color", DataType.COLOR);

    public static final Slot PARTIAL_COLOR = new Slot("partialColor",
            "Partial Color", DataType.COLOR);

    public static final Slot PARTIAL_BORDER_COLOR = new Slot(
            "partialBorderColor", "Partial Border Color", DataType.COLOR);

    public static final Slot[] SLOTS = new Slot[] { LABEL, VALUE,
            PARTIAL_VALUE, COLOR, BORDER_COLOR, PARTIAL_COLOR,
            PARTIAL_BORDER_COLOR };

    private final static int WEDGE_TEXT_ANGLE = 0;

    private JsDoubleFunction partialWedgeRadius = new JsDoubleFunction() {
        @Override
        public double f(JsArgs args) {
            ViewItem viewItem = args.getObject();

            double partialValue = viewItem.getValueAsDouble(PARTIAL_VALUE);
            double value = viewItem.getValueAsDouble(VALUE);

            // cannot divide by zero
            if (value == 0) {
                return 0;
            }

            assert 0 <= partialValue && partialValue <= value;

            double partialPercentage = partialValue / value;

            assert 0 <= partialPercentage && partialPercentage <= 1 : "0 <= partialPercentage <= 1 (was: "
                    + partialPercentage + ")";

            /*
             * This was found to be a visually more accurate solution for both
             * small and large percentages compared to 'partialPercentage *
             * outerRadius' and to 'Math.sqrt(partialPercentage) * outerRadius'
             */
            return ((Math.sqrt(partialPercentage) + partialPercentage) / 2)
                    * outerRadius / 2;
        }
    };

    private double sumOfAngleValues;

    private JsDoubleFunction outerRadiusFunction = new JsDoubleFunction() {
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
            return viewItem.getValueAsDouble(VALUE) * 2 * Math.PI
                    / sumOfAngleValues;
        }
    };

    private String wedgeLabelAnchor = PVAlignment.CENTER;

    private JsStringFunction regularMarkLabelText = new JsStringFunction() {
        @Override
        public String f(JsArgs args) {
            ViewItem viewItem = args.getObject();
            return Double.toString(viewItem.getValueAsDouble(VALUE)
                    - viewItem.getValueAsDouble(PARTIAL_VALUE));
        }
    };

    private JsStringFunction highlightedMarkLabelText = new JsStringFunction() {
        @Override
        public String f(JsArgs args) {
            ViewItem viewItem = args.getObject();
            return Double.toString(viewItem.getValueAsDouble(PARTIAL_VALUE));
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
                .innerRadius(partialWedgeRadius)
                .outerRadius(outerRadiusFunction).angle(wedgeAngle)
                .fillStyle(new ViewItemColorSlotAccessor(COLOR))
                .strokeStyle(new ViewItemColorSlotAccessor(BORDER_COLOR));

        mainWedge.anchor(wedgeLabelAnchor).add(PV.Label)
                .textAngle(WEDGE_TEXT_ANGLE).text(regularMarkLabelText)
                .textStyle(Colors.WHITE);

        partialWedge = mainWedge
                .add(PV.Wedge)
                .startAngle(new JsDoubleFunction() {
                    /*
                     * NOTE: The wedge position calculation using angle()
                     * requires the sibling wedges to be visible, which is not
                     * the case for partial wedges. We thus need to use
                     * startAngle to specify the position.
                     */
                    @Override
                    public double f(JsArgs args) {
                        // TODO Auto-generated method stub
                        return 0;
                    }
                })
                .visible(
                        new ViewItemPredicateJsBooleanFunction(
                                new GreaterThanSlotValuePredicate(
                                        PARTIAL_VALUE, 0)))
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
            sumOfAngleValues += viewItemsJsArray.get(i).getValueAsDouble(VALUE);
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