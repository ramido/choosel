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

import static org.thechiselgroup.choosel.visualization_component.chart.client.scatterplot.ScatterPlotVisualization.SHAPE_SLOT;
import static org.thechiselgroup.choosel.visualization_component.chart.client.scatterplot.ScatterPlotVisualization.X_POSITION_SLOT;
import static org.thechiselgroup.choosel.visualization_component.chart.client.scatterplot.ScatterPlotVisualization.Y_POSITION_SLOT;

import org.thechiselgroup.choosel.core.client.ui.Colors;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.views.DragEnablerFactory;
import org.thechiselgroup.choosel.core.client.views.ViewItem;
import org.thechiselgroup.choosel.core.client.views.slots.Slot;
import org.thechiselgroup.choosel.protovis.client.PV;
import org.thechiselgroup.choosel.protovis.client.PVAlignment;
import org.thechiselgroup.choosel.protovis.client.PVDot;
import org.thechiselgroup.choosel.protovis.client.PVEventHandler;
import org.thechiselgroup.choosel.protovis.client.PVLinearScale;
import org.thechiselgroup.choosel.protovis.client.PVScale;
import org.thechiselgroup.choosel.visualization_component.chart.client.ChartItemColorFunction;
import org.thechiselgroup.choosel.visualization_component.chart.client.ChartItemDoubleSlotAccessor;
import org.thechiselgroup.choosel.visualization_component.chart.client.ChartItemStringSlotAccessor;
import org.thechiselgroup.choosel.visualization_component.chart.client.ChartViewContentDisplay;
import org.thechiselgroup.choosel.visualization_component.chart.client.TickFormatFunction;

import com.google.inject.Inject;

public class ScatterPlotViewContentDisplay extends ChartViewContentDisplay {

    private static final int BORDER_BOTTOM = 35;

    private static final int BORDER_TOP = 5;

    private static final int BORDER_LEFT = 35;

    private static final int BORDER_RIGHT = 5;

    /**
     * Color of the grid lines.
     */
    private static final String GRIDLINE_COLOR = Colors.GRAY_1;

    /**
     * Color of the axis lines.
     */
    private static final String AXIS_COLOR = Colors.GRAY_2;

    protected int chartHeight;

    protected int chartWidth;

    private PVLinearScale scaleX;

    private PVLinearScale scaleY;

    /**
     * Configuration of the dots that get painted in the scatter plot.
     */
    private PVDot dots;

    private String xAxisLabel = "";

    private String yAxisLabel = "";

    @Inject
    public ScatterPlotViewContentDisplay(DragEnablerFactory dragEnablerFactory) {
        super(dragEnablerFactory);
    }

    @Override
    public void buildChart() {
        assert chartItemsJsArray.length() >= 1;

        initChart();
        initScales();

        drawAxesAndGrid();
        drawAxesLabels();
        drawDots();
    }

    // TODO convert grid line color into property
    // TODO convert axis color into property
    void drawAxesAndGrid() {
        // vertical grid lines and labels on x axis
        getChart().add(PV.Rule).data(scaleX.ticks()).bottom(0).left(scaleX)
                .strokeStyle(GRIDLINE_COLOR).height(chartHeight)
                .anchor(PVAlignment.BOTTOM).add(PV.Label)
                .text(new TickFormatFunction(scaleX));
        getChart().add(PV.Rule).height(chartHeight).bottom(0).left(0)
                .strokeStyle(AXIS_COLOR);

        // horizontal grid lines and labels on y axis
        getChart().add(PV.Rule).data(scaleY.ticks()).bottom(scaleY).left(0)
                .strokeStyle(GRIDLINE_COLOR).width(chartWidth).add(PV.Label)
                .text(new TickFormatFunction(scaleY)).textAngle(-Math.PI / 2)
                .textAlign(PVAlignment.CENTER).textBaseline(PVAlignment.BOTTOM);
        getChart().add(PV.Rule).width(chartWidth).bottom(0).left(0)
                .strokeStyle(AXIS_COLOR);
    }

    // TODO remove magic constants
    private void drawAxesLabels() {
        getChart().add(PV.Label).bottom(-BORDER_BOTTOM + 5)
                .left(chartWidth / 2).text(xAxisLabel)
                .textAlign(PVAlignment.CENTER);

        getChart().add(PV.Label).bottom(chartHeight / 2)
                .left(-BORDER_LEFT + 20).text(yAxisLabel)
                .textAngle(-Math.PI / 2).textAlign(PVAlignment.CENTER);
    }

    private void drawDots() {
        dots = getChart()
                .add(PV.Dot)
                .data(chartItemsJsArray)
                .shape(new ChartItemStringSlotAccessor(SHAPE_SLOT))
                .bottom(scaleY.fd(new ChartItemDoubleSlotAccessor(
                        Y_POSITION_SLOT)))
                .left(scaleX
                        .fd(new ChartItemDoubleSlotAccessor(X_POSITION_SLOT)))
                // TODO extract size function
                .size(Math.min(chartHeight, chartWidth)
                        / (chartItemsJsArray.length() * 2))
                .fillStyle(new ChartItemColorFunction())
                .strokeStyle(Colors.STEELBLUE);
    }

    @Override
    public String getName() {
        return "Scatter Plot";
    }

    @Override
    public Slot[] getSlots() {
        return new Slot[] { X_POSITION_SLOT, Y_POSITION_SLOT, SHAPE_SLOT };
    }

    private void initChart() {
        chartWidth = width - BORDER_LEFT - BORDER_RIGHT;
        chartHeight = height - BORDER_BOTTOM - BORDER_TOP;

        getChart().left(BORDER_LEFT).bottom(BORDER_BOTTOM);
    }

    private void initScales() {
        scaleX = PVScale.linear(chartItemsJsArray,
                new ChartItemDoubleSlotAccessor(X_POSITION_SLOT)).range(0,
                chartWidth);
        scaleY = PVScale.linear(chartItemsJsArray,
                new ChartItemDoubleSlotAccessor(Y_POSITION_SLOT)).range(0,
                chartHeight);
    }

    @Override
    protected void registerEventHandler(String eventType, PVEventHandler handler) {
        dots.event(eventType, handler);
    }

    @Override
    public void update(LightweightCollection<ViewItem> addedResourceItems,
            LightweightCollection<ViewItem> updatedResourceItems,
            LightweightCollection<ViewItem> removedResourceItems,
            LightweightCollection<Slot> changedSlots) {

        // TODO re-enable
        // if (!changedSlots.isEmpty()) {
        // TODO expose protovis label and change immediately, if possible
        this.yAxisLabel = callback
                .getSlotResolverDescription(ScatterPlotVisualization.Y_POSITION_SLOT);
        this.xAxisLabel = callback
                .getSlotResolverDescription(ScatterPlotVisualization.X_POSITION_SLOT);
        // }

        super.update(addedResourceItems, updatedResourceItems,
                removedResourceItems, changedSlots);
    }

}