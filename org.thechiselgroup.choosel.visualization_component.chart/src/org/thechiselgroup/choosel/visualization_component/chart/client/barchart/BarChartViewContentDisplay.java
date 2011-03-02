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

import static org.thechiselgroup.choosel.visualization_component.chart.client.barchart.BarChartVisualization.BAR_LABEL_SLOT;
import static org.thechiselgroup.choosel.visualization_component.chart.client.barchart.BarChartVisualization.BAR_LENGTH_SLOT;

import org.thechiselgroup.choosel.core.client.ui.Colors;
import org.thechiselgroup.choosel.core.client.ui.TextBoundsEstimator;
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
import org.thechiselgroup.choosel.visualization_component.chart.client.ChartItemComparator;
import org.thechiselgroup.choosel.visualization_component.chart.client.ChartItemStringSlotAccessor;
import org.thechiselgroup.choosel.visualization_component.chart.client.ChartViewContentDisplay;
import org.thechiselgroup.choosel.visualization_component.chart.client.TickFormatFunction;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.inject.Inject;

/* TODO refactor such that the differences between vertical and horizontal bar chart
 * are extracted and the commonalities are kept.
 */
// TODO right side ticks
// TODO leverage scales
public class BarChartViewContentDisplay extends ChartViewContentDisplay {

    private class BarSpacingProperty implements
            ViewContentDisplayProperty<Boolean> {

        @Override
        public String getPropertyName() {
            return BarChartVisualization.BAR_SPACING_PROPERTY;
        }

        @Override
        public Boolean getValue() {
            return getBarSpacing();
        }

        @Override
        public void setValue(Boolean value) {
            setBarSpacing(value);
        }
    }

    private class LayoutProperty implements
            ViewContentDisplayProperty<LayoutType> {

        @Override
        public String getPropertyName() {
            return BarChartVisualization.LAYOUT_PROPERTY;
        }

        @Override
        public LayoutType getValue() {
            return getLayout();
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

        /**
         * @return space that is available for the bar height.
         */
        private double getBarLengthSpace(int chartHeight, int chartWidth) {
            return isVerticalBarChart(chartHeight, chartWidth) ? chartHeight
                    : chartWidth;
        }

        /**
         * @return space that is available for the bar width.
         */
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

    private static final int BORDER_LEFT = 5;

    private static final int BORDER_TOP = 5;

    private static final int BORDER_RIGHT = 5;

    private static final String GRIDLINE_SCALE_COLOR = "rgba(255,255,255,.3)";

    private static final String AXIS_SCALE_COLOR = Colors.GRAY_1;

    private static final double BAR_STROKE_WIDTH = 0.5d;

    private static final String FONT_WEIGHT = "normal";

    private static final String FONT_SIZE = "10px";

    private static final String FONT_STYLE = "normal";

    private static final String FONT_FAMILY = "sans-serif";

    private static final String FONT = FONT_SIZE + " " + FONT_FAMILY;

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
                    BAR_LENGTH_SLOT, Subset.HIGHLIGHTED), 2);
        }
    };

    private JsStringFunction fullMarkLabelText = new JsStringFunction() {
        @Override
        public String f(JsArgs args) {
            ChartItem chartItem = args.getObject();
            return StringUtils
                    .formatDecimal(chartItem.getSlotValueAsDouble(
                            BAR_LENGTH_SLOT, Subset.ALL), 2);
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
            return (0.33 * getBarWidthSpace() / (chartItemsJsArray.length() * 2))
                    + calculateBarWidth()
                    / 2
                    + _this.index()
                    * getBarWidthSpace() / chartItemsJsArray.length();
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
            return 0.33 * getBarWidthSpace() / (chartItemsJsArray.length() * 2);
        }
    };

    private JsDoubleFunction regularBarBase = new JsDoubleFunction() {
        @Override
        public double f(JsArgs args) {
            return calculateHighlightedBarLength(args.<ChartItem> getObject())
                    + BAR_STROKE_WIDTH;
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

    private String barTextBaseline = PVAlignment.TOP;

    private LayoutType layout = LayoutType.HORIZONTAL;

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
            return chartItem.getSlotValueAsDouble(BAR_LENGTH_SLOT, Subset.ALL)
                    - chartItem.getSlotValueAsDouble(BAR_LENGTH_SLOT,
                            Subset.HIGHLIGHTED) < 1 ? null : Double
                    .toString(chartItem.getSlotValueAsDouble(BAR_LENGTH_SLOT,
                            Subset.ALL)
                            - chartItem.getSlotValueAsDouble(BAR_LENGTH_SLOT,
                                    Subset.HIGHLIGHTED));
        }
    };

    protected JsStringFunction highlightedMarkLabelText = new JsStringFunction() {
        @Override
        public String f(JsArgs args) {
            ChartItem chartItem = args.getObject();
            return chartItem.getSlotValueAsDouble(BAR_LENGTH_SLOT,
                    Subset.HIGHLIGHTED) <= 0 ? null : Double.toString(chartItem
                    .getSlotValueAsDouble(BAR_LENGTH_SLOT, Subset.HIGHLIGHTED));
        }
    };

    protected double maxChartItemValue;

    private boolean barSpacing = true;

    @Inject
    public BarChartViewContentDisplay(DragEnablerFactory dragEnablerFactory) {
        super(dragEnablerFactory);

        registerProperty(new LayoutProperty());
    }

    @Override
    protected void beforeRender() {
        super.beforeRender();

        chartItemsJsArray.sortStable(new ChartItemComparator(BAR_LENGTH_SLOT));

        calculateMaximumChartItemValue();

        if (chartItemsJsArray.length() == 0) {
            return;
        }

        regularValues = new double[chartItemsJsArray.length()];
        for (int i = 0; i < chartItemsJsArray.length(); i++) {
            regularValues[i] = chartItemsJsArray.get(i).getSlotValueAsDouble(
                    BAR_LENGTH_SLOT, Subset.ALL);
        }
    }

    @Override
    public void buildChart() {
        assert chartItemsJsArray.length() >= 1;

        // TODO do we need sorting?
        // Collections.sort(chartItems, new ChartItemComparator(
        // SlotResolver.CHART_LABEL_SLOT));

        calculateChartVariables();
        calculateMaximumChartItemValue();

        if (layout.isVerticalBarChart(chartHeight, chartWidth)) {
            getChart().left(BORDER_LEFT + 40).bottom(BORDER_BOTTOM);
            // TODO axis label
            drawVerticalBarChart();
            drawVerticalBarScales();
        } else {
            getChart().left(BORDER_LEFT + calculateHorizontalLabelSpace())
                    .bottom(BORDER_BOTTOM);
            drawHorizontalBarMeasurementAxisLabel();
            drawHorizontalBarChart();
            drawHorizontalBarScales();

        }
        getChart().add(PV.Rule).bottom(0).left(0).width(chartWidth)
                .strokeStyle(AXIS_SCALE_COLOR).lineWidth(BAR_STROKE_WIDTH);
        getChart().add(PV.Rule).left(0).bottom(0).height(chartHeight)
                .strokeStyle(AXIS_SCALE_COLOR).lineWidth(BAR_STROKE_WIDTH);
    }

    private double calculateBarLength(double value) {
        return value * getBarLengthSpace() / maxChartItemValue;
    }

    private double calculateBarStart(int index) {
        double barAreaStart = index * getBarWidthSpace()
                / chartItemsJsArray.length();

        double barOffset = barSpacing ? calculateBarWidth() / 2 : 0;

        return barAreaStart + barOffset;
    }

    private double calculateBarWidth() {
        double spacePerBar = getBarWidthSpace() / chartItemsJsArray.length();

        if (barSpacing) {
            spacePerBar /= 2;
        }

        return spacePerBar;
    }

    private void calculateChartVariables() {
        if (layout.isVerticalBarChart(chartHeight, chartWidth)) {
            chartWidth = width - BORDER_LEFT - 40 - BORDER_RIGHT;
        } else {
            chartWidth = width - BORDER_LEFT - BORDER_RIGHT
                    - calculateHorizontalLabelSpace();
        }

        chartHeight = height - BORDER_BOTTOM - BORDER_TOP;
    }

    private double calculateHighlightedBarLength(ChartItem d) {
        return calculateBarLength(d.getSlotValueAsDouble(BAR_LENGTH_SLOT,
                Subset.HIGHLIGHTED));
    }

    private int calculateHorizontalLabelSpace() {
        TextBoundsEstimator estimator = new TextBoundsEstimator();
        estimator.applyFontSettings(FONT_FAMILY, FONT_STYLE, FONT_WEIGHT,
                FONT_SIZE);

        // max over widths for labels
        int maxWidth = 0;
        for (int i = 0; i < chartItemsJsArray.length(); i++) {
            String label = chartItemsJsArray.get(i).getSlotValue(
                    BAR_LABEL_SLOT, Subset.ALL);
            estimator.setText(label);
            int width = estimator.getTextWidth();

            if (maxWidth < width) {
                maxWidth = width;
            }
        }

        return maxWidth;
    }

    protected void calculateMaximumChartItemValue() {
        maxChartItemValue = 0;
        for (int i = 0; i < chartItemsJsArray.length(); i++) {
            double currentItemValue = chartItemsJsArray.get(i)
                    .getSlotValueAsDouble(BAR_LENGTH_SLOT, Subset.ALL);
            if (maxChartItemValue < currentItemValue) {
                maxChartItemValue = currentItemValue;
            }
        }
    }

    private void drawHorizontalBarChart() {
        regularBar = getChart().add(PV.Bar).data(chartItemsJsArray).left(0)
                .width(fullBarLength).bottom(barStart).height(barWidth)
                .fillStyle(chartFillStyle).strokeStyle(Colors.STEELBLUE)
                .lineWidth(BAR_STROKE_WIDTH);

        regularBar.add(PV.Label).bottom(baselineLabelStart)
                .textAlign(PVAlignment.RIGHT).left(0)
                .text(new ChartItemStringSlotAccessor(BAR_LABEL_SLOT))
                .textBaseline(PVAlignment.MIDDLE);

        regularBar.anchor(PVAlignment.RIGHT).add(PV.Label)
                .textBaseline(PVAlignment.MIDDLE).text(fullMarkLabelText)
                .textStyle(fullMarkTextStyle).textAlign(valueLabelAlignment);

        // TODO negative bars (in opposite direction)
        highlightedBar = getChart().add(PV.Bar).data(chartItemsJsArray).left(0)
                .width(highlightedBarLength).bottom(highlightedBarStart)
                .height(highlightedWidth).fillStyle(Colors.YELLOW)
                .strokeStyle(Colors.STEELBLUE).lineWidth(BAR_STROKE_WIDTH)
                .visible(isPartiallyHighlighted);

        highlightedBar.anchor(PVAlignment.RIGHT).add(PV.Label)
                .textBaseline(barTextBaseline).text(highlightedLabelText)
                .textStyle(Colors.BLACK).textBaseline(PVAlignment.MIDDLE);
    }

    private void drawHorizontalBarMeasurementAxisLabel() {
        getChart().add(PV.Label).bottom(-BORDER_BOTTOM + 5)
                .left(chartWidth / 2).text(valueAxisLabelFunction)
                .textAlign(PVAlignment.CENTER);
    }

    private void drawHorizontalBarScales() {
        PVLinearScale scale = PVScale.linear(0, maxChartItemValue).range(0,
                chartWidth);
        getChart().add(PV.Rule).data(scale.ticks(5)).left(scale).bottom(0)
                .strokeStyle(scaleStrokeStyle).height(chartHeight)
                .anchor(PVAlignment.BOTTOM).add(PV.Label)
                .text(new TickFormatFunction(scale));
    }

    private void drawVerticalBarChart() {
        regularBar = getChart().add(PV.Bar).data(chartItemsJsArray).bottom(0)
                .height(fullBarLength).left(barStart).width(barWidth)
                .fillStyle(chartFillStyle).strokeStyle(Colors.STEELBLUE)
                .lineWidth(BAR_STROKE_WIDTH);

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
                }).text(new ChartItemStringSlotAccessor(BAR_LABEL_SLOT))
                .textBaseline(PVAlignment.MIDDLE);

        regularBar.anchor(PVAlignment.TOP).add(PV.Label)
                .textAngle(-Math.PI / 2).textBaseline(PVAlignment.MIDDLE)
                .textAlign(valueLabelAlignment).textStyle(fullMarkTextStyle)
                .text(fullMarkLabelText);

        highlightedBar = getChart().add(PV.Bar).data(chartItemsJsArray)
                .bottom(0).height(highlightedBarLength)
                .left(highlightedBarStart).width(highlightedWidth)
                .fillStyle(Colors.YELLOW).strokeStyle(Colors.STEELBLUE)
                .lineWidth(BAR_STROKE_WIDTH).visible(isPartiallyHighlighted);

        highlightedBar.anchor(PVAlignment.TOP).add(PV.Label)
                .textBaseline(PVAlignment.MIDDLE).textAlign(PVAlignment.RIGHT)
                .text(highlightedMarkLabelText).textAngle(-Math.PI / 2);
    }

    // TODO extract scale ticks # as property
    protected void drawVerticalBarScales() {
        PVLinearScale scale = PVScale.linear(0, maxChartItemValue).range(0,
                chartHeight);
        getChart().add(PV.Rule).data(scale.ticks(5)).left(0).bottom(scale)
                .strokeStyle(scaleStrokeStyle).width(chartWidth)
                .anchor(PVAlignment.LEFT).add(PV.Label)
                .text(new TickFormatFunction(scale));
    }

    private double getBarLengthSpace() {
        return layout.getBarLengthSpace(chartHeight, chartWidth);
    }

    private boolean getBarSpacing() {
        return barSpacing;
    }

    private double getBarWidthSpace() {
        return layout.getBarWidthSpace(chartHeight, chartWidth);
    }

    public LayoutType getLayout() {
        return layout;
    }

    @Override
    public String getName() {
        return "Bar Chart";
    }

    @Override
    public SidePanelSection[] getSidePanelSections() {
        FlowPanel settingsPanel = new FlowPanel();

        {
            settingsPanel.add(new Label("Chart orientation"));
            final ListBox layoutBox = new ListBox(false);
            layoutBox.setVisibleItemCount(1);
            for (LayoutType layout : LayoutType.values()) {
                layoutBox.addItem(layout.getName(), layout.toString());
            }
            layoutBox.setSelectedIndex(1);
            layoutBox.addChangeHandler(new ChangeHandler() {
                @Override
                public void onChange(ChangeEvent event) {
                    setLayout(LayoutType.valueOf(layoutBox.getValue(layoutBox
                            .getSelectedIndex())));
                }
            });
            settingsPanel.add(layoutBox);
        }

        {
            settingsPanel.add(new Label("Bar spacing"));

            CheckBox barSpacingCheckbox = new CheckBox();
            barSpacingCheckbox.setText("separate");
            barSpacingCheckbox.setValue(barSpacing);
            barSpacingCheckbox
                    .addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                        @Override
                        public void onValueChange(
                                ValueChangeEvent<Boolean> event) {
                            setBarSpacing(event.getValue());
                        }
                    });
            settingsPanel.add(barSpacingCheckbox);
        }

        return new SidePanelSection[] { new SidePanelSection("Settings",
                settingsPanel), };
    }

    @Override
    public Slot[] getSlots() {
        return new Slot[] { BAR_LABEL_SLOT, BAR_LENGTH_SLOT };
    }

    @Override
    protected void registerEventHandler(String eventType, PVEventHandler handler) {
        regularBar.event(eventType, handler);
    }

    public void setBarSpacing(boolean barSpacing) {
        if (this.barSpacing == barSpacing) {
            return;
        }

        this.barSpacing = barSpacing;
        updateChart(true);
    }

    public void setLayout(LayoutType layout) {
        assert layout != null;

        if (this.layout.equals(layout)) {
            return;
        }

        this.layout = layout;
        updateChart(true);
    }

    @Override
    public void update(LightweightCollection<ViewItem> addedResourceItems,
            LightweightCollection<ViewItem> updatedResourceItems,
            LightweightCollection<ViewItem> removedResourceItems,
            LightweightCollection<Slot> changedSlots) {

        // TODO re-enable - might be wrong for initial configuration...
        // if (!changedSlots.isEmpty()) {
        valueAxisLabel = callback.getSlotResolverDescription(BAR_LENGTH_SLOT);
        // }

        super.update(addedResourceItems, updatedResourceItems,
                removedResourceItems, changedSlots);
    }

}