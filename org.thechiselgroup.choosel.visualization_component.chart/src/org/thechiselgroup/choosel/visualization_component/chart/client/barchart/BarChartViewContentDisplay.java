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
package org.thechiselgroup.choosel.visualization_component.chart.client.barchart;

import org.thechiselgroup.choosel.core.client.ui.Colors;
import org.thechiselgroup.choosel.core.client.util.StringUtils;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.views.DragEnablerFactory;
import org.thechiselgroup.choosel.core.client.views.SidePanelSection;
import org.thechiselgroup.choosel.core.client.views.ViewContentDisplayProperty;
import org.thechiselgroup.choosel.core.client.views.ViewItem;
import org.thechiselgroup.choosel.core.client.views.ViewItem.Subset;
import org.thechiselgroup.choosel.core.client.views.ViewItem.SubsetStatus;
import org.thechiselgroup.choosel.core.client.views.slots.Slot;
import org.thechiselgroup.choosel.protovis.client.PV;
import org.thechiselgroup.choosel.protovis.client.PVAlignment;
import org.thechiselgroup.choosel.protovis.client.PVBar;
import org.thechiselgroup.choosel.protovis.client.PVEventHandler;
import org.thechiselgroup.choosel.protovis.client.PVLinearScale;
import org.thechiselgroup.choosel.protovis.client.PVMark;
import org.thechiselgroup.choosel.protovis.client.PVScale;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsArgs;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsBooleanFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsDoubleFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsStringFunction;
import org.thechiselgroup.choosel.visualization_component.chart.client.ChartItem;
import org.thechiselgroup.choosel.visualization_component.chart.client.ChartViewContentDisplay;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.inject.Inject;

/* TODO refactor such that the differences between vertical and horizontal bar chart
 * are extracted and the commonalities are kept.
 */
// TODO right side ticks
public class BarChartViewContentDisplay extends ChartViewContentDisplay {

    private class LayoutProperty implements
            ViewContentDisplayProperty<LayoutType> {

        @Override
        public String getPropertyName() {
            return BarChartVisualization.LAYOUT_PROPERTY;
        }

        @Override
        public LayoutType getValue() {
            return layout;
        }

        @Override
        public void setValue(LayoutType value) {
            setLayout(value);
        }
    }

    public static enum LayoutType {

        VERTICAL("Vertical"), HORIZONTAL("Horizontal"), AUTOMATIC("Automatic");

        private String name;

        LayoutType(String name) {
            this.name = name;
        }

        private double getBarLengthSpace(int chartHeight, int chartWidth) {
            return isVerticalBarChart(chartHeight, chartWidth) ? chartHeight
                    : chartWidth;
        }

        private double getBarWidthSpace(int chartHeight, int chartWidth) {
            return isVerticalBarChart(chartHeight, chartWidth) ? chartWidth
                    : chartHeight;
        }

        public String getName() {
            return name;
        }

        private boolean isVerticalBarChart(int chartHeight, int chartWidth) {
            return this == LayoutType.VERTICAL
                    || (this == LayoutType.AUTOMATIC && chartHeight < chartWidth);
        }

    }

    private static final int BORDER_BOTTOM = 35;

    private static final int BORDER_LEFT = 45;

    private static final int BORDER_TOP = 5;

    private static final int BORDER_RIGHT = 5;

    private static final String GRIDLINE_SCALE_COLOR = "rgba(255,255,255,.3)";

    private static final String AXIS_SCALE_COLOR = Colors.GRAY_1;

    private double[] regularValues;

    // TODO semantic meaning (bar length etc) --> makes different settings
    // easier
    protected int chartHeight;

    protected int chartWidth;

    protected JsStringFunction highlightedLabelText = new JsStringFunction() {
        @Override
        public String f(JsArgs args) {
            ChartItem chartItem = args.getObject();
            return StringUtils.formatDecimal(chartItem.getSlotValueAsDouble(
                    BarChartVisualization.BAR_LENGTH_SLOT, Subset.HIGHLIGHTED),
                    2);
        }
    };

    private JsStringFunction fullMarkLabelText = new JsStringFunction() {
        @Override
        public String f(JsArgs args) {
            ChartItem chartItem = args.getObject();
            return StringUtils.formatDecimal(chartItem.getSlotValueAsDouble(
                    BarChartVisualization.BAR_LENGTH_SLOT, Subset.ALL), 2);
        }
    };

    private JsBooleanFunction isPartiallyHighlighted = new JsBooleanFunction() {
        @Override
        public boolean f(JsArgs args) {
            ChartItem d = args.getObject();
            return SubsetStatus.PARTIAL.equals(d.getViewItem()
                    .getHighlightStatus());
        }
    };

    private JsDoubleFunction barStart = new JsDoubleFunction() {
        @Override
        public double f(JsArgs args) {
            return calculateBarStart(args.<PVMark> getThis().index());
        }
    };

    // TODO refactor
    private JsDoubleFunction highlightedBarStart = new JsDoubleFunction() {
        @Override
        public double f(JsArgs args) {
            PVMark _this = args.getThis();
            return (0.33 * layout.getBarWidthSpace(chartHeight, chartWidth) / (chartItemsJsArray
                    .length() * 2))
                    + calculateBarWidth()
                    / 2
                    + _this.index()
                    * layout.getBarWidthSpace(chartHeight, chartWidth)
                    / chartItemsJsArray.length();
        }
    };

    /**
     * Calculates the length of the highlighted bar.
     */
    private JsDoubleFunction highlightedBarLength = new JsDoubleFunction() {
        @Override
        public double f(JsArgs args) {
            return calculateHighlightedBarLength(args.<ChartItem> getObject());
        }
    };

    private JsDoubleFunction fullBarLength = new JsDoubleFunction() {
        @Override
        public double f(JsArgs args) {
            PVMark _this = args.getThis();
            return calculateBarLength(regularValues[_this.index()]);
        }
    };

    private JsDoubleFunction barWidth = new JsDoubleFunction() {
        @Override
        public double f(JsArgs args) {
            return calculateBarWidth();
        }

    };

    // TODO barWidth / 3
    private JsDoubleFunction highlightedWidth = new JsDoubleFunction() {
        @Override
        public double f(JsArgs args) {
            return 0.33 * layout.getBarWidthSpace(chartHeight, chartWidth)
                    / (chartItemsJsArray.length() * 2);
        }
    };

    private JsDoubleFunction regularBarBase = new JsDoubleFunction() {
        @Override
        public double f(JsArgs args) {
            return calculateHighlightedBarLength(args.<ChartItem> getObject())
                    + barLineWidth;
        }
    };

    private JsStringFunction scaleStrokeStyle = new JsStringFunction() {
        @Override
        public String f(JsArgs args) {
            int d = args.getInt();
            return d == 0 ? AXIS_SCALE_COLOR : GRIDLINE_SCALE_COLOR;
        }
    };

    private PVBar regularBar;

    private PVBar highlightedBar;

    private JsDoubleFunction baselineLabelStart = new JsDoubleFunction() {
        @Override
        public double f(JsArgs args) {
            PVMark _this = args.getThis();
            return calculateBarStart(_this.index()) + calculateBarWidth() / 2;
        }
    };

    private JsStringFunction baselineLabelText = new JsStringFunction() {
        @Override
        public String f(JsArgs args) {
            ChartItem d = args.getObject();
            return d.getViewItem()
                    .getSlotValue(BarChartVisualization.BAR_LABEL_SLOT)
                    .toString();
        }
    };

    private String barTextBaseline = PVAlignment.TOP;

    protected LayoutType layout = LayoutType.HORIZONTAL;

    private static final int HORIZONTAL_BAR_LABEL_EXTRA_MARGIN = 20;

    private double barLineWidth = 1;

    protected JsStringFunction chartFillStyle = new JsStringFunction() {
        @Override
        public String f(JsArgs args) {
            ChartItem d = args.getObject();
            if (SubsetStatus.COMPLETE.equals(d.getViewItem()
                    .getHighlightStatus())) {
                return Colors.YELLOW; // TODO semantic color constants
            }

            switch (d.getViewItem().getSelectionStatus()) {
            case COMPLETE:
            case PARTIAL:
                return Colors.ORANGE; // TODO semantic color constants
            default:
                return Colors.STEELBLUE; // TODO semantic color constants
            }
        }
    };

    protected JsStringFunction fullMarkTextStyle = new JsStringFunction() {
        @Override
        public String f(JsArgs args) {
            ChartItem d = args.getObject();
            PVMark _this = args.getThis();

            if (SubsetStatus.COMPLETE.equals(d.getViewItem()
                    .getHighlightStatus())) {
                return Colors.BLACK;
            }

            // XXX calculate label size instead of using 60px
            if (calculateBarLength(regularValues[_this.index()]) < 60) {
                return Colors.GRAY_2;
            }

            return Colors.WHITE;
        }
    };

    private String valueAxisLabel;

    private JsStringFunction valueAxisLabelFunction = new JsStringFunction() {
        @Override
        public String f(JsArgs args) {
            return valueAxisLabel;
        }
    };

    private JsStringFunction valueLabelAlignment = new JsStringFunction() {
        @Override
        public String f(JsArgs args) {
            // XXX pre-calculation should be done by methods in
            // chart..
            // XXX calculate label size instead of taking 60px
            PVMark _this = args.getThis();
            if (calculateBarLength(regularValues[_this.index()]) < 60) {
                return PVAlignment.LEFT;
            }
            return PVAlignment.RIGHT;
        }
    };

    protected JsStringFunction regularMarkLabelText = new JsStringFunction() {
        @Override
        public String f(JsArgs args) {
            ChartItem chartItem = args.getObject();
            return chartItem.getSlotValueAsDouble(
                    BarChartVisualization.BAR_LENGTH_SLOT, Subset.ALL)
                    - chartItem.getSlotValueAsDouble(
                            BarChartVisualization.BAR_LENGTH_SLOT,
                            Subset.HIGHLIGHTED) < 1 ? null : Double
                    .toString(chartItem.getSlotValueAsDouble(
                            BarChartVisualization.BAR_LENGTH_SLOT, Subset.ALL)
                            - chartItem.getSlotValueAsDouble(
                                    BarChartVisualization.BAR_LENGTH_SLOT,
                                    Subset.HIGHLIGHTED));
        }
    };

    protected JsStringFunction highlightedMarkLabelText = new JsStringFunction() {
        @Override
        public String f(JsArgs args) {
            ChartItem chartItem = args.getObject();
            return chartItem.getSlotValueAsDouble(
                    BarChartVisualization.BAR_LENGTH_SLOT, Subset.HIGHLIGHTED) <= 0 ? null
                    : Double.toString(chartItem.getSlotValueAsDouble(
                            BarChartVisualization.BAR_LENGTH_SLOT,
                            Subset.HIGHLIGHTED));
        }
    };

    @Inject
    public BarChartViewContentDisplay(DragEnablerFactory dragEnablerFactory) {
        super(dragEnablerFactory);

        registerProperty(new LayoutProperty());
    }

    @Override
    protected void beforeRender() {
        super.beforeRender();

        calculateMaximumChartItemValue();

        if (chartItemsJsArray.length() == 0) {
            return;
        }

        regularValues = new double[chartItemsJsArray.length()];
        for (int i = 0; i < chartItemsJsArray.length(); i++) {
            regularValues[i] = chartItemsJsArray.get(i).getSlotValueAsDouble(
                    BarChartVisualization.BAR_LENGTH_SLOT, Subset.ALL);
        }
    }

    private double calculateBarLength(double value) {
        return value * layout.getBarLengthSpace(chartHeight, chartWidth)
                / getMaximumChartItemValue();
    }

    private double calculateBarStart(int index) {
        return calculateBarWidth() / 2 + index
                * layout.getBarWidthSpace(chartHeight, chartWidth)
                / chartItemsJsArray.length();
    }

    private double calculateBarWidth() {
        return layout.getBarWidthSpace(chartHeight, chartWidth)
                / (chartItemsJsArray.length() * 2);
    }

    private void calculateChartVariables() {
        if (layout.isVerticalBarChart(chartHeight, chartWidth)) {
            chartWidth = width - BORDER_LEFT - BORDER_RIGHT;
        } else {
            chartWidth = width - BORDER_LEFT - BORDER_RIGHT
                    - HORIZONTAL_BAR_LABEL_EXTRA_MARGIN;
        }

        chartHeight = height - BORDER_BOTTOM - BORDER_TOP;
    }

    private double calculateHighlightedBarLength(ChartItem d) {
        return calculateBarLength(d.getSlotValueAsDouble(BarChartVisualization.BAR_LENGTH_SLOT, Subset.HIGHLIGHTED));
    }

    protected void calculateMaximumChartItemValue() {
        maxChartItemValue = 0;
        for (int i = 0; i < chartItemsJsArray.length(); i++) {
            double currentItemValue = chartItemsJsArray.get(i)
                    .getSlotValueAsDouble(
                            BarChartVisualization.BAR_LENGTH_SLOT, Subset.ALL);
            if (maxChartItemValue < currentItemValue) {
                maxChartItemValue = currentItemValue;
            }
        }
    }

    @Override
    public void drawChart() {
        assert chartItemsJsArray.length() >= 1;

        // TODO do we need sorting?
        // Collections.sort(chartItems, new ChartItemComparator(
        // SlotResolver.CHART_LABEL_SLOT));

        calculateChartVariables();
        calculateMaximumChartItemValue();

        if (layout.isVerticalBarChart(chartHeight, chartWidth)) {
            getChart().left(BORDER_LEFT).bottom(BORDER_BOTTOM);
            PVLinearScale scale = PVScale.linear(0, getMaximumChartItemValue())
                    .range(0, chartHeight);
            // TODO axis label
            drawVerticalBarChart();
            drawVerticalBarScales(scale);
        } else {
            getChart().left(BORDER_LEFT + HORIZONTAL_BAR_LABEL_EXTRA_MARGIN)
                    .bottom(BORDER_BOTTOM);
            PVLinearScale scale = PVScale.linear(0, getMaximumChartItemValue())
                    .range(0, chartWidth);
            drawHorizontalBarMeasurementAxisLabel();
            drawHorizontalBarChart();
            drawHorizontalBarScales(scale);

        }
        getChart().add(PV.Rule).bottom(0).left(0).width(chartWidth)
                .strokeStyle(AXIS_SCALE_COLOR).lineWidth(barLineWidth);
        getChart().add(PV.Rule).left(0).bottom(0).height(chartHeight)
                .strokeStyle(AXIS_SCALE_COLOR).lineWidth(barLineWidth);
    }

    private void drawHorizontalBarChart() {
        regularBar = getChart().add(PV.Bar).data(chartItemsJsArray)
                .left(barLineWidth).width(fullBarLength).bottom(barStart)
                .height(barWidth).fillStyle(chartFillStyle)
                .strokeStyle(Colors.STEELBLUE).lineWidth(barLineWidth);

        regularBar.add(PV.Label).bottom(baselineLabelStart)
                .textAlign(PVAlignment.RIGHT).left(0).text(baselineLabelText)
                .textBaseline(PVAlignment.MIDDLE);

        regularBar.anchor(PVAlignment.RIGHT).add(PV.Label)
                .textBaseline(PVAlignment.MIDDLE).text(fullMarkLabelText)
                .textStyle(fullMarkTextStyle).textAlign(valueLabelAlignment);

        // TODO negative bars (in opposite direction)
        highlightedBar = getChart().add(PV.Bar).data(chartItemsJsArray)
                .left(barLineWidth).width(highlightedBarLength)
                .bottom(highlightedBarStart).height(highlightedWidth)
                .fillStyle(Colors.YELLOW).strokeStyle(Colors.STEELBLUE)
                .lineWidth(barLineWidth).visible(isPartiallyHighlighted);

        highlightedBar.anchor(PVAlignment.RIGHT).add(PV.Label)
                .textBaseline(barTextBaseline).text(highlightedLabelText)
                .textStyle(Colors.BLACK).textBaseline(PVAlignment.MIDDLE);
    }

    private void drawHorizontalBarMeasurementAxisLabel() {
        getChart().add(PV.Label).bottom(-BORDER_BOTTOM + 5)
                .left(chartWidth / 2).text(valueAxisLabelFunction)
                .textAlign(PVAlignment.CENTER);
    }

    protected void drawHorizontalBarScales(PVLinearScale scale) {
        this.scale = scale;
        getChart().add(PV.Rule).data(scale.ticks(5)).left(scale).bottom(0)
                .strokeStyle(scaleStrokeStyle).height(chartHeight)
                .anchor(PVAlignment.BOTTOM).add(PV.Label).text(scaleLabelText);
    }

    private void drawVerticalBarChart() {
        regularBar = getChart().add(PV.Bar).data(chartItemsJsArray)
                .bottom(barLineWidth - 1).height(fullBarLength).left(barStart)
                .width(barWidth).fillStyle(chartFillStyle)
                .strokeStyle(Colors.STEELBLUE).lineWidth(barLineWidth);

        regularBar.add(PV.Label).left(baselineLabelStart)
                .textAlign(PVAlignment.CENTER).bottom(new JsDoubleFunction() {
                    @Override
                    public double f(JsArgs args) {
                        ChartItem d = args.getObject();
                        PVMark _this = args.getThis();
                        // TODO dynamic positioning depending on label size
                        if (chartWidth / regularValues.length > 60) {
                            return -10;
                        }
                        return _this.index() % 2 == 0 ? -10 : -25;
                    }
                }).text(baselineLabelText).textBaseline(PVAlignment.MIDDLE);

        regularBar.anchor(PVAlignment.TOP).add(PV.Label)
                .textAngle(-Math.PI / 2).textBaseline(PVAlignment.MIDDLE)
                .textAlign(valueLabelAlignment).textStyle(fullMarkTextStyle)
                .text(fullMarkLabelText);

        highlightedBar = getChart().add(PV.Bar).data(chartItemsJsArray)
                .bottom(barLineWidth).height(highlightedBarLength)
                .left(highlightedBarStart).width(highlightedWidth)
                .fillStyle(Colors.YELLOW).strokeStyle(Colors.STEELBLUE)
                .lineWidth(barLineWidth).visible(isPartiallyHighlighted);

        highlightedBar.anchor(PVAlignment.TOP).add(PV.Label)
                .textBaseline(PVAlignment.MIDDLE).textAlign(PVAlignment.RIGHT)
                .text(highlightedMarkLabelText).textAngle(-Math.PI / 2);
    }

    // TODO extract scale ticks # as property
    protected void drawVerticalBarScales(PVLinearScale scale) {
        this.scale = scale;
        getChart().add(PV.Rule).data(scale.ticks(5)).left(0).bottom(scale)
                .strokeStyle(scaleStrokeStyle).width(chartWidth)
                .anchor(PVAlignment.LEFT).add(PV.Label).text(scaleLabelText);
    }

    @Override
    public String getName() {
        return "Bar Chart";
    }

    @Override
    public SidePanelSection[] getSidePanelSections() {
        FlowPanel settingsPanel = new FlowPanel();

        final ListBox layoutBox = new ListBox(false);
        layoutBox.setVisibleItemCount(1);
        for (LayoutType layout : LayoutType.values()) {
            layoutBox.addItem(layout.getName(), layout.toString());
        }
        layoutBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                setLayout(LayoutType.valueOf(layoutBox.getValue(layoutBox
                        .getSelectedIndex())));
            }
        });
        settingsPanel.add(layoutBox);

        return new SidePanelSection[] { new SidePanelSection("Settings",
                settingsPanel), };
    }

    @Override
    public Slot[] getSlots() {
        return new Slot[] { BarChartVisualization.BAR_LABEL_SLOT,
                BarChartVisualization.BAR_LENGTH_SLOT };
    }

    @Override
    protected void registerEventHandler(String eventType, PVEventHandler handler) {
        regularBar.event(eventType, handler);
    }

    private void setLayout(LayoutType layout) {
        assert layout != null;

        this.layout = layout;
        buildChart();
    }

    @Override
    public void update(LightweightCollection<ViewItem> addedResourceItems,
            LightweightCollection<ViewItem> updatedResourceItems,
            LightweightCollection<ViewItem> removedResourceItems,
            LightweightCollection<Slot> changedSlots) {

        // TODO re-enable - might be wrong for initial configuration...
        // if (!changedSlots.isEmpty()) {
        valueAxisLabel = callback
                .getSlotResolverDescription(BarChartVisualization.BAR_LENGTH_SLOT);
        // }

        super.update(addedResourceItems, updatedResourceItems,
                removedResourceItems, changedSlots);
    }

}