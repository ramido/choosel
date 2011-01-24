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
package org.thechiselgroup.choosel.visualization_component.chart.client.scatterplot;

import org.thechiselgroup.choosel.core.client.ui.Colors;
import org.thechiselgroup.choosel.core.client.util.collections.ArrayUtils;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.views.DragEnablerFactory;
import org.thechiselgroup.choosel.core.client.views.ViewItem;
import org.thechiselgroup.choosel.core.client.views.slots.Slot;
import org.thechiselgroup.choosel.protovis.client.PV;
import org.thechiselgroup.choosel.protovis.client.PVAlignment;
import org.thechiselgroup.choosel.protovis.client.PVDot;
import org.thechiselgroup.choosel.protovis.client.PVEventHandler;
import org.thechiselgroup.choosel.protovis.client.PVLinearScale;
import org.thechiselgroup.choosel.protovis.client.PVMark;
import org.thechiselgroup.choosel.protovis.client.PVScale;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsArgs;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsDoubleFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsStringFunction;
import org.thechiselgroup.choosel.visualization_component.chart.client.ChartItem;
import org.thechiselgroup.choosel.visualization_component.chart.client.ChartViewContentDisplay;

import com.google.inject.Inject;

public class ScatterPlotViewContentDisplay extends ChartViewContentDisplay {

    private static final int BORDER_BOTTOM = 35;

    private static final int BORDER_TOP = 5;

    private static final int BORDER_LEFT = 35;

    private static final int BORDER_RIGHT = 5;

    private static final String GRIDLINE_SCALE_COLOR = Colors.GRAY_1;

    private static final String AXIS_SCALE_COLOR = Colors.GRAY_2;

    private double[] scatterCountsX;

    private double[] scatterCountsY;

    protected int chartHeight;

    protected int chartWidth;

    private double minY;

    private double maxY;

    private double minX;

    private double maxX;

    private PVLinearScale scaleX;

    private PVLinearScale scaleY;

    private JsDoubleFunction scatterBottom = new JsDoubleFunction() {
        @Override
        public double f(JsArgs args) {
            PVMark _this = args.getThis();
            return (scatterCountsY[_this.index()] - minY) * chartHeight
                    / (maxY - minY);
        }
    };

    private JsDoubleFunction scatterLeft = new JsDoubleFunction() {
        @Override
        public double f(JsArgs args) {
            PVMark _this = args.getThis();
            return (scatterCountsX[_this.index()] - minX) * chartWidth
                    / (maxX - minX);
        }
    };

    private String scaleStrokeStyle = GRIDLINE_SCALE_COLOR;

    private PVDot scatter;

    private JsStringFunction scaleLabelTextX = new JsStringFunction() {
        @Override
        public String f(JsArgs args) {
            return scaleX.tickFormatInt(args.getInt());
        }
    };

    private JsStringFunction scaleLabelTextY = new JsStringFunction() {
        @Override
        public String f(JsArgs args) {
            return scaleY.tickFormatInt(args.getInt());
        }
    };

    private String xAxisLabel = "X-Axis";

    private String yAxisLabel = "Y-Axis";

    private JsStringFunction chartFillStyle = new JsStringFunction() {
        @Override
        public String f(JsArgs args) {
            return args.<ChartItem> getObject().getColor();
        }
    };

    @Inject
    public ScatterPlotViewContentDisplay(DragEnablerFactory dragEnablerFactory) {
        super(dragEnablerFactory);
    }

    @Override
    protected void beforeRender() {
        if (chartItemsJsArray.length() == 0) {
            return;
        }

        scatterCountsY = new double[chartItemsJsArray.length()];

        for (int i = 0; i < chartItemsJsArray.length(); i++) {
            scatterCountsY[i] = calculateAllResourcesY(i);
        }

        minY = ArrayUtils.min(scatterCountsY) - 1;
        maxY = ArrayUtils.max(scatterCountsY) + 1;

        scatterCountsX = new double[chartItemsJsArray.length()];

        for (int i = 0; i < chartItemsJsArray.length(); i++) {
            scatterCountsX[i] = calculateAllResourcesX(i);
        }

        minX = ArrayUtils.min(scatterCountsX) - 1;
        maxX = ArrayUtils.max(scatterCountsX) + 1;
    }

    // TODO refactor
    protected double calculateAllResourcesX(int i) {
        return Double.parseDouble(chartItemsJsArray.get(i).getViewItem()
                .getSlotValue(ScatterPlotVisualization.X_POSITION_SLOT)
                .toString());
    }

    // TODO refactor
    protected double calculateAllResourcesY(int i) {
        return Double.parseDouble(chartItemsJsArray.get(i).getViewItem()
                .getSlotValue(ScatterPlotVisualization.Y_POSITION_SLOT)
                .toString());
    }

    private void calculateChartVariables() {
        chartWidth = width - BORDER_LEFT - BORDER_RIGHT;
        chartHeight = height - BORDER_BOTTOM - BORDER_TOP;
    }

    private void drawAxisLabels() {
        getChart().add(PV.Label).bottom(-BORDER_BOTTOM + 5)
                .left(chartWidth / 2).text(xAxisLabel)
                .textAlign(PVAlignment.CENTER);

        getChart().add(PV.Label).bottom(chartHeight / 2)
                .left(-BORDER_LEFT + 20).text(yAxisLabel)
                .textAngle(-Math.PI / 2).textAlign(PVAlignment.CENTER);
    }

    @Override
    public void drawChart() {
        assert chartItemsJsArray.length() >= 1;

        calculateChartVariables();
        setChartParameters();

        beforeRender();

        scaleX = PVScale.linear(minX, maxX).range(0, chartWidth);
        scaleY = PVScale.linear(minY, maxY).range(0, chartHeight);
        drawScales(scaleX, scaleY);
        drawScatter();
        drawAxisLabels();
    }

    protected void drawScales(PVLinearScale scaleX, PVLinearScale scaleY) {
        getChart().add(PV.Rule).data(scaleX.ticks()).bottom(0).left(scaleX)
                .strokeStyle(scaleStrokeStyle).height(chartHeight)
                .anchor(PVAlignment.BOTTOM).add(PV.Label).text(scaleLabelTextX);

        getChart().add(PV.Rule).data(scaleY.ticks()).bottom(scaleY).left(0)
                .strokeStyle(scaleStrokeStyle).width(chartWidth).add(PV.Label)
                .text(scaleLabelTextY).textAngle(-Math.PI / 2)
                .textAlign(PVAlignment.CENTER).textBaseline(PVAlignment.BOTTOM);

        getChart().add(PV.Rule).height(chartHeight).bottom(0).left(0)
                .strokeStyle(AXIS_SCALE_COLOR);

        getChart().add(PV.Rule).width(chartWidth).bottom(0).left(0)
                .strokeStyle(AXIS_SCALE_COLOR);
    }

    private void drawScatter() {
        scatter = getChart()
                .add(PV.Dot)
                .data(chartItemsJsArray)
                .bottom(scatterBottom)
                .left(scatterLeft)
                .size(Math.min(chartHeight, chartWidth)
                        / (chartItemsJsArray.length() * 2))
                .fillStyle(chartFillStyle).strokeStyle(Colors.STEELBLUE);
    }

    @Override
    public Slot[] getSlots() {
        return new Slot[] { ScatterPlotVisualization.X_POSITION_SLOT,
                ScatterPlotVisualization.Y_POSITION_SLOT };
    }

    public String getXAxisLabel() {
        return xAxisLabel;
    }

    public String getYAxisLabel() {
        return yAxisLabel;
    }

    @Override
    protected void registerEventHandler(String eventType, PVEventHandler handler) {
        scatter.event(eventType, handler);
    }

    private void setChartParameters() {
        getChart().left(BORDER_LEFT).bottom(BORDER_BOTTOM);
    }

    public void setXAxisLabel(String xAxisLabel) {
        this.xAxisLabel = xAxisLabel;
    }

    public void setYAxisLabel(String yAxisLabel) {
        this.yAxisLabel = yAxisLabel;
    }

    @Override
    public void update(LightweightCollection<ViewItem> addedResourceItems,
            LightweightCollection<ViewItem> updatedResourceItems,
            LightweightCollection<ViewItem> removedResourceItems,
            LightweightCollection<Slot> changedSlots) {

        // TODO re-enable
        // if (!changedSlots.isEmpty()) {
        setYAxisLabel(callback
                .getSlotResolverDescription(ScatterPlotVisualization.Y_POSITION_SLOT));
        setXAxisLabel(callback
                .getSlotResolverDescription(ScatterPlotVisualization.X_POSITION_SLOT));
        // }

        super.update(addedResourceItems, updatedResourceItems,
                removedResourceItems, changedSlots);
    }

}