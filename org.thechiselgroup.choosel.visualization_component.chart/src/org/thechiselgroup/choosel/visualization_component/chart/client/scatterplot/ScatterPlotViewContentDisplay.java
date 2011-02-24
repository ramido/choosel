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

import java.util.Map;

import org.thechiselgroup.choosel.core.client.ui.Colors;
import org.thechiselgroup.choosel.core.client.ui.TextBoundsEstimator;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.views.DragEnablerFactory;
import org.thechiselgroup.choosel.core.client.views.ViewContentDisplayProperty;
import org.thechiselgroup.choosel.core.client.views.ViewItem;
import org.thechiselgroup.choosel.core.client.views.slots.Slot;
import org.thechiselgroup.choosel.protovis.client.PV;
import org.thechiselgroup.choosel.protovis.client.PVAlignment;
import org.thechiselgroup.choosel.protovis.client.PVDot;
import org.thechiselgroup.choosel.protovis.client.PVEventHandler;
import org.thechiselgroup.choosel.protovis.client.PVLabel;
import org.thechiselgroup.choosel.protovis.client.PVLinearScale;
import org.thechiselgroup.choosel.protovis.client.PVMark;
import org.thechiselgroup.choosel.protovis.client.PVPanel;
import org.thechiselgroup.choosel.protovis.client.PVScale;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsArgs;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsDoubleFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsStringFunction;
import org.thechiselgroup.choosel.visualization_component.chart.client.ChartItemColorFunction;
import org.thechiselgroup.choosel.visualization_component.chart.client.ChartItemDoubleSlotAccessor;
import org.thechiselgroup.choosel.visualization_component.chart.client.ChartItemStringSlotAccessor;
import org.thechiselgroup.choosel.visualization_component.chart.client.ChartViewContentDisplay;
import org.thechiselgroup.choosel.visualization_component.chart.client.TickFormatFunction;

import com.google.inject.Inject;

// TODO refactoring: use separate panel for dots that are added to scatter plot
public class ScatterPlotViewContentDisplay extends ChartViewContentDisplay {

    private class ShapeLegendProperty implements
            ViewContentDisplayProperty<Map<String, String>> {

        @Override
        public String getPropertyName() {
            return ScatterPlotVisualization.SHAPE_LEGEND_PROPERTY;
        }

        @Override
        public Map<String, String> getValue() {
            return getShapeLegend();
        }

        @Override
        public void setValue(Map<String, String> value) {
            setShapeLegend(value);
        }
    }

    private static final int SHAPE_LEGEND_PANEL_PADDING = 2;

    private static final int SHAPE_LEGEND_LABEL_SPACING = 20;

    /**
     * Offset that moves the y-axis label closer to the y-axis. This is required
     * because the label is rotate by 90 degrees and the text would only be half
     * visible without the offset.
     */
    private static final int Y_AXIS_LABEL_OFFSET = 10;

    // TODO move
    private static final double ANGLE_90_DEGREES = -Math.PI / 2;

    private static final int SHAPE_SIZE = 15;

    private static final int OUTER_BORDER = 5;

    private static final int AXIS_LEGEND_SPACE = 30;

    /**
     * Height of the shape legend in pixels. This is only relevant if the shape
     * legend is actually displayed. Use {@link #getShapeLegendVerticalSpace()}
     * instead.
     */
    private static final int SHAPE_LEGEND_HEIGHT = 35;

    /**
     * Distance between X axis label and shape legend.
     */
    private static final int SHAPE_LEGEND_OFFSET = 5;

    /**
     * Color of the grid lines.
     */
    private static final String GRIDLINE_COLOR = Colors.GRAY_1;

    /**
     * Color of the axis lines.
     */
    private static final String AXIS_COLOR = Colors.GRAY_2;

    private static final String SHAPE_LEGEND_BACKGROUND_COLOR = "#EEE";

    private static final String FONT_WEIGHT = "normal";

    private static final String FONT_SIZE = "10px";

    private static final String FONT_STYLE = "normal";

    private static final String FONT_FAMILY = "sans-serif";

    private static final String FONT = FONT_SIZE + " " + FONT_FAMILY;

    private Map<String, String> shapeLegend;

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

    private String shapeLegendLabel = "";

    @Inject
    public ScatterPlotViewContentDisplay(DragEnablerFactory dragEnablerFactory) {
        super(dragEnablerFactory);

        registerProperty(new ShapeLegendProperty());
    }

    @Override
    public void buildChart() {
        assert chartItemsJsArray.length() >= 1;

        initChart();
        initScales();

        drawXAxisAndVerticalGridlines();
        drawYAxisAndHorizontalGridlines();
        drawShapeLegend();
        drawDots();
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
                .size(SHAPE_SIZE).fillStyle(new ChartItemColorFunction())
                .strokeStyle(Colors.STEELBLUE);
    }

    private void drawShapeLegend() {
        if (shapeLegend == null) {
            return;
        }

        // calculate widths
        TextBoundsEstimator estimator = new TextBoundsEstimator(FONT_FAMILY,
                FONT_STYLE, FONT_WEIGHT, FONT_SIZE);

        final Map<String, Integer> textWidths = estimator
                .getTextWidths(shapeLegend.values());

        int descriptionsWidth = 0;
        for (Integer integer : textWidths.values()) {
            descriptionsWidth += integer;
        }
        descriptionsWidth += textWidths.size()
                * (SHAPE_SIZE + SHAPE_LEGEND_LABEL_SPACING);
        int legendLabelWidth = estimator.getTextWidth(shapeLegendLabel);
        int shapePanelWidth = legendLabelWidth > descriptionsWidth ? legendLabelWidth
                : descriptionsWidth;

        // panel for legend
        PVPanel shapePanel = getChart()
                .add(PV.Panel)
                .bottom(-AXIS_LEGEND_SPACE - SHAPE_LEGEND_HEIGHT
                        - SHAPE_LEGEND_OFFSET).left(0)
                .fillStyle(SHAPE_LEGEND_BACKGROUND_COLOR)
                .height(SHAPE_LEGEND_HEIGHT).width(shapePanelWidth);

        // shape legend title
        shapePanel.add(PV.Label).bottom(SHAPE_LEGEND_PANEL_PADDING)
                .left(shapePanelWidth / 2).font(FONT).text(shapeLegendLabel)
                .textAlign(PVAlignment.CENTER);

        shapePanel
                .add(PV.Dot)
                .data(shapeLegend.entrySet())
                .top(2 + (SHAPE_SIZE / 2))
                .shape(new JsStringFunction() {
                    @Override
                    public String f(JsArgs args) {
                        Map.Entry<String, String> entry = args.getObject();
                        return entry.getKey();
                    }
                })
                .size(SHAPE_SIZE)
                .left(new JsDoubleFunction() {

                    private int currentWidth = 0;

                    @Override
                    public double f(JsArgs args) {
                        PVMark _this = args.getThis();
                        Map.Entry<String, String> entry = args.getObject();

                        if (_this.index() == 0) {
                            currentWidth = 0;
                        }

                        int left = currentWidth + SHAPE_SIZE;

                        currentWidth += textWidths.get(entry.getValue())
                                + SHAPE_SIZE + SHAPE_LEGEND_LABEL_SPACING;

                        return left;
                    }
                }).anchor(PVAlignment.RIGHT).add(PV.Label).font(FONT)
                .text(new JsStringFunction() {
                    @Override
                    public String f(JsArgs args) {
                        Map.Entry<String, String> entry = args.getObject();
                        return entry.getValue();
                    }
                });

        // TODO leverage knowledge about text widths
        // --> calculation
        // TODO Shapes + attached labels
        // getChart().add(PV.Dot)
        // .bottom(-AXIS_LEGEND_SPACE - getShapeLegendHeight())
        // .left(-AXIS_LEGEND_SPACE);
    }

    // TODO convert grid line color into property
    // TODO convert axis color into property
    private void drawXAxisAndVerticalGridlines() {
        // x axis label
        getChart().add(PV.Label).bottom(-AXIS_LEGEND_SPACE)
                .left(chartWidth / 2).text(xAxisLabel)
                .textAlign(PVAlignment.CENTER).textBaseline(PVLabel.BOTTOM);

        // x axis grid labels
        getChart().add(PV.Rule).data(scaleX.ticks()).bottom(0).left(scaleX)
                .strokeStyle(GRIDLINE_COLOR).height(chartHeight)
                .anchor(PVAlignment.BOTTOM).add(PV.Label)
                .text(new TickFormatFunction(scaleX));

        // vertical grid lines
        getChart().add(PV.Rule).height(chartHeight).bottom(0).left(0)
                .strokeStyle(AXIS_COLOR);

    }

    private void drawYAxisAndHorizontalGridlines() {
        // y axis label
        getChart().add(PV.Label).bottom(chartHeight / 2)
                .left(-AXIS_LEGEND_SPACE)
                // + Y_AXIS_LABEL_OFFSET
                .text(yAxisLabel).textAngle(ANGLE_90_DEGREES)
                .textAlign(PVAlignment.CENTER).textBaseline(PVLabel.TOP);

        // y axis grid labels
        getChart().add(PV.Rule).data(scaleY.ticks()).bottom(scaleY).left(0)
                .strokeStyle(GRIDLINE_COLOR).width(chartWidth).add(PV.Label)
                .text(new TickFormatFunction(scaleY))
                .textAngle(ANGLE_90_DEGREES).textAlign(PVAlignment.CENTER)
                .textBaseline(PVAlignment.BOTTOM);

        // horizontal grid lines
        getChart().add(PV.Rule).width(chartWidth).bottom(0).left(0)
                .strokeStyle(AXIS_COLOR);
    }

    @Override
    public String getName() {
        return "Scatter Plot";
    }

    public Map<String, String> getShapeLegend() {
        return shapeLegend;
    }

    /**
     * @return vertical space consumed by the shape legend, or 0, if it is not
     *         displayed.
     */
    private int getShapeLegendVerticalSpace() {
        return shapeLegend == null ? 0 : SHAPE_LEGEND_HEIGHT
                + SHAPE_LEGEND_OFFSET;
    }

    @Override
    public Slot[] getSlots() {
        return new Slot[] { X_POSITION_SLOT, Y_POSITION_SLOT, SHAPE_SLOT };
    }

    private void initChart() {
        chartWidth = width - AXIS_LEGEND_SPACE - 2 * OUTER_BORDER;
        chartHeight = height - AXIS_LEGEND_SPACE - 2 * OUTER_BORDER
                - getShapeLegendVerticalSpace();

        getChart().left(AXIS_LEGEND_SPACE + OUTER_BORDER).bottom(
                AXIS_LEGEND_SPACE + OUTER_BORDER
                        + getShapeLegendVerticalSpace());
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

    public void setShapeLegend(Map<String, String> shapeLegend) {
        this.shapeLegend = shapeLegend;
        updateChart(true);
    }

    @Override
    public void update(LightweightCollection<ViewItem> addedResourceItems,
            LightweightCollection<ViewItem> updatedResourceItems,
            LightweightCollection<ViewItem> removedResourceItems,
            LightweightCollection<Slot> changedSlots) {

        // TODO re-enable
        // if (!changedSlots.isEmpty()) {
        // TODO expose protovis label and change immediately, if possible
        this.yAxisLabel = callback.getSlotResolverDescription(Y_POSITION_SLOT);
        this.xAxisLabel = callback.getSlotResolverDescription(X_POSITION_SLOT);
        this.shapeLegendLabel = callback.getSlotResolverDescription(SHAPE_SLOT);
        // }

        super.update(addedResourceItems, updatedResourceItems,
                removedResourceItems, changedSlots);
    }

}