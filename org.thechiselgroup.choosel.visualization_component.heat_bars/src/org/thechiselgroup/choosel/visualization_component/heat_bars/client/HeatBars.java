package org.thechiselgroup.choosel.visualization_component.heat_bars.client;

import java.util.Date;

import org.thechiselgroup.choosel.core.client.ui.Color;
import org.thechiselgroup.choosel.core.client.ui.Colors;
import org.thechiselgroup.choosel.core.client.util.DataType;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightList;
import org.thechiselgroup.choosel.core.client.visualization.model.Slot;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItem;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItem.Subset;
import org.thechiselgroup.choosel.protovis.client.PV;
import org.thechiselgroup.choosel.protovis.client.PVAlignment;
import org.thechiselgroup.choosel.protovis.client.PVColor;
import org.thechiselgroup.choosel.protovis.client.PVEventHandler;
import org.thechiselgroup.choosel.protovis.client.PVLinearScale;
import org.thechiselgroup.choosel.protovis.client.PVMark;
import org.thechiselgroup.choosel.protovis.client.PVOrdinalScale;
import org.thechiselgroup.choosel.protovis.client.PVPanel;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsArgs;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsArrayGeneric;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsDoubleFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsStringFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsUtils;
import org.thechiselgroup.choosel.visualization_component.chart.client.ChartViewContentDisplay;

import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.i18n.client.DateTimeFormat;

//TODO each bar is represented by a visualItem.  Ideally this would change some time in the future.
public class HeatBars extends ChartViewContentDisplay {

    private static final int MAX_BAR_LABEL_LENGTH = 15;

    private PVPanel invisibleInteractionBar;

    // TODO move to protovis (events)
    public static final String ALL = "all";

    // TODO move to protovis (cursor)
    public static final String POINTER = "pointer";

    public final static Slot LABEL = new Slot("label", "Label", DataType.TEXT);

    public final static Slot ZERO_COLOR = new Slot("zeroColor", "0 Color",
            DataType.COLOR);

    public final static Slot LOW_COLOR = new Slot("lowColor", "Low Color",
            DataType.COLOR);

    public final static Slot MIDDLE_COLOR = new Slot("middleColor",
            "Middle Color", DataType.COLOR);

    public final static Slot HIGH_COLOR = new Slot("highColor", "High Color",
            DataType.COLOR);

    // this is always done by count, and is not really a slot right now
    public final static Slot BINNING_VALUE = new Slot("slice intensity",
            "Slice Intensity", DataType.NUMBER);

    public static final Slot[] SLOTS = new Slot[] { LABEL, ZERO_COLOR,
            LOW_COLOR, MIDDLE_COLOR, HIGH_COLOR, BINNING_VALUE };

    public final static String ID = "org.thechiselgroup.choosel.visualization_component.heat_bars.client.HeatBars";

    public int calculateNumDataItemsPerBar() {
        return totalBarWidth;
    }

    private final int SLICE_WIDTH = 1;

    private final int LEFT_LABEL_PADDING = 50;

    private JsArrayGeneric<JsArrayNumber> data;

    private JsFunction<PVColor> colorFunction = new JsFunction<PVColor>() {
        @Override
        public PVColor f(JsArgs args) {
            // figure out which visualItem this data point maps to
            PVMark _this = args.getThis();
            VisualItem visualItem = visualItemsJsArray.get(_this.parent()
                    .index());

            // calculate the color based on the resolvers and the binCount
            double value = args.getDouble();
            return PV.color(calculateColor(
                    (Color) visualItem.getValue(ZERO_COLOR),
                    (Color) visualItem.getValue(LOW_COLOR),
                    (Color) visualItem.getValue(MIDDLE_COLOR),
                    (Color) visualItem.getValue(HIGH_COLOR), value));
        }
    };

    private JsFunction<PVColor> barBorderColorFunction = new JsFunction<PVColor>() {
        @Override
        public PVColor f(JsArgs args) {
            int index = args.<PVMark> getThis().index();
            VisualItem visualItem = getVisualItem(index);

            if (!visualItem.getResources(Subset.HIGHLIGHTED).isEmpty()) {
                return PV.color(Colors.YELLOW);
            } else if (!visualItem.getResources(Subset.SELECTED).isEmpty()) {
                return PV.color(Colors.ORANGE);
            } else {
                return PV.color(Colors.WHITE);
            }
        }
    };

    int totalBarWidth;

    int chartHeight;

    JsDoubleFunction totalBarWidthFunction = new JsDoubleFunction() {
        public double f(JsArgs args) {
            return totalBarWidth;
        }
    };

    public PVOrdinalScale barBinScale;

    JsDoubleFunction panelTopFunction = new JsDoubleFunction() {
        public double f(JsArgs args) {
            return barBinScale.fd(args.<PVMark> getThis().index());
        }
    };

    JsDoubleFunction panelHeightFunction = new JsDoubleFunction() {
        public double f(JsArgs args) {
            return barBinScale.rangeBand();
        }
    };

    JsDoubleFunction sliceWidthFunction = new JsDoubleFunction() {
        public double f(JsArgs args) {
            return SLICE_WIDTH;
        }
    };

    JsDoubleFunction sliceLeftFunction = new JsDoubleFunction() {
        public double f(JsArgs args) {
            return slicePositionScale.fd(args.<PVMark> getThis().index());
        }
    };

    JsStringFunction tickLabelFunction = new JsStringFunction() {
        @Override
        public String f(JsArgs args) {
            DateTimeFormat format = DateTimeFormat
                    .getFormat("MMMM dd yyyy HH aa");

            Date date = new Date(new Double(tickLabelValueScale.fd(args
                    .getDouble())).longValue());

            return format.format(date);
        }
    };

    JsStringFunction panelLabelFunction = new JsStringFunction() {
        @Override
        public String f(JsArgs args) {

            PVMark _this = args.getThis();
            int index = _this.parent().index();

            String labelValue = visualItemsJsArray.get(index).getValue(LABEL);

            if (labelValue.length() > MAX_BAR_LABEL_LENGTH) {
                labelValue = labelValue.substring(0, MAX_BAR_LABEL_LENGTH);
            }

            return labelValue;
        }
    };

    JsFunction<JsArrayNumber> childDataFunction = new JsFunction<JsArrayNumber>() {
        public JsArrayNumber f(JsArgs args) {
            return args.getObject();
        }
    };

    private final int CHART_BOTTOM_PADDING = 10;

    private final int CHART_RIGHT_PADDING = 25;

    private PVOrdinalScale slicePositionScale;

    private double barScaleStart;

    private double barScaleEnd;

    private double maxBinCount;

    private PVLinearScale tickLabelValueScale;

    private int calculateNumBars() {
        return visualItemsJsArray.length();
    }

    @Override
    protected void buildChart() {

        assert visualItemsJsArray.length() >= 1;

        /* Initialize the chart dimension variables */
        initChartDimensions(height, width);

        /* calculate the data values for each bin before we build */
        calculateDataArrayFromVisualItems();

        /* Scale that maps bar indexes onto bar positions */
        barBinScale = PV.Scale.ordinal(PV.range(calculateNumBars()))
                .splitBanded(0, chartHeight, .8);

        /* Scale that maps slice index onto slice position */
        slicePositionScale = PV.Scale.ordinal(PV.range(totalBarWidth))
                .splitBanded(LEFT_LABEL_PADDING,
                        totalBarWidth + LEFT_LABEL_PADDING);

        /* This maps an index to a value that it represents in the graph range */
        tickLabelValueScale = PV.Scale.linear(0, totalBarWidth).range(
                barScaleStart, barScaleEnd);

        /* A scale that maps 0 - 1000 to 50 - 1050 */
        PVLinearScale tickPositionScale = PV.Scale.linear(0, totalBarWidth)
                .range(LEFT_LABEL_PADDING, totalBarWidth + LEFT_LABEL_PADDING);

        /* Root panel */
        PVPanel vis = chartWidget
                .getPVPanel()
                .width(totalBarWidth + LEFT_LABEL_PADDING + CHART_RIGHT_PADDING)
                .height(chartHeight + CHART_BOTTOM_PADDING);

        /* Add one panel for each horizontal bar */
        PVPanel panel = vis.add(PV.Panel).data(data).top(panelTopFunction)
                .height(panelHeightFunction);

        /* an invisible panel that will handle mouse events */
        invisibleInteractionBar = vis.add(PV.Panel).data(visualItemsJsArray)
                .left(LEFT_LABEL_PADDING).width(totalBarWidthFunction)
                .top(panelTopFunction).height(panelHeightFunction)
                .cursor(POINTER).events(ALL)
                .strokeStyle(barBorderColorFunction);

        /* Add a label to each panel */
        panel.anchor(PVAlignment.LEFT).add(PV.Label).text(panelLabelFunction);

        /* Add many vertical bars (slices) to each horizontal Panel (bar) */
        panel.add(PV.Bar).data(childDataFunction).top(0)
                .left(sliceLeftFunction).width(sliceWidthFunction)
                .fillStyle(colorFunction);

        /* Add X axis ticks with number labels */
        vis.add(PV.Rule).data(tickPositionScale.ticks(calculateNumTicks()))
                .left(tickPositionScale).bottom(CHART_BOTTOM_PADDING).height(5)
                .strokeStyle("#000").anchor(PVAlignment.BOTTOM).add(PV.Label)
                .text(tickLabelFunction);
    }

    /**
     * Each tick should be spaced enough to include one full date label
     */
    private int calculateNumTicks() {
        return new Double(Math.ceil(totalBarWidth / 200.0)).intValue();
    }

    /**
     * Calculates certain variables based on the height and width provided for
     * the chart
     */
    public void initChartDimensions(int totalHeight, int totalWidth) {
        chartHeight = totalHeight - CHART_BOTTOM_PADDING;
        totalBarWidth = totalWidth - LEFT_LABEL_PADDING - CHART_RIGHT_PADDING;
    }

    public String calculateColor(Color zeroColor, Color lowColor,
            Color middleColor, Color highColor, double value) {

        double lowCutOff = calculateLowCutOff();
        double middleCutOff = calculateMiddleCutOff();
        double highCutOff = calculateHighCutOff();

        if (value == 0) {
            return zeroColor.toHex();
        } else if (value > 0 && value <= lowCutOff) {
            double blendFactor = value / (lowCutOff - 0.0);
            return middleColor.interpolateWith(lowColor, blendFactor).toHex();
        } else if (value > lowCutOff && value <= middleCutOff) {
            double blendFactor = (value - lowCutOff)
                    / (middleCutOff - lowCutOff);
            return lowColor.interpolateWith(middleColor, blendFactor).toHex();
        } else if (value > middleCutOff && value < highCutOff) {
            double blendFactor = (value - middleCutOff)
                    / (highCutOff - middleCutOff);
            return middleColor.interpolateWith(highColor, blendFactor).toHex();
        } else {
            return highColor.toHex();
        }
    }

    // TODO this should be done with resolvers
    public double calculateHighCutOff() {
        return maxBinCount;
    }

    // TODO this should be done with resolvers
    public double calculateMiddleCutOff() {
        return (2.0 / 3.0) * maxBinCount;
    }

    // TODO this should be done with resolvers
    public double calculateLowCutOff() {
        return (1.0 / 3.0) * maxBinCount;
    }

    @Override
    public String getName() {
        return "HeatBars";
    }

    @Override
    public Slot[] getSlots() {
        return SLOTS;
    }

    /**
     * This method defines what pieces of the visualization should trigger
     * events
     */
    @Override
    protected void registerEventHandler(String eventType, PVEventHandler handler) {
        invisibleInteractionBar.event(eventType, handler);
    }

    @Override
    protected void afterRender() {
        super.afterRender();
    }

    @Override
    protected void beforeRender() {
        super.beforeRender();
    }

    /**
     * This method converts the visualItems that are in the view, which
     * representBars, into a two dimensional JavaScript friendly double array
     * 
     * It also calculates the minimum and maximum bar scale, and the
     * maximumBinCount
     */
    private void calculateDataArrayFromVisualItems() {
        // reset data

        calculateStartAndEndOfBarScale();
        resetData();
        maxBinCount = 0;

        for (int i = 0; i < visualItemsJsArray.length(); i++) {
            // each visualItem represents one bar
            VisualItem visualItem = visualItemsJsArray.get(i);

            // This is the date of the work item
            for (Double binValue : visualItem
                    .<LightweightList<Double>> getValue(BINNING_VALUE)) {

                if (barScaleEnd == barScaleStart) {
                    // all of the elements are the same, and there can not rly
                    // be a graph
                    return;
                } else {
                    int index = (int) ((binValue - barScaleStart)
                            / (barScaleEnd - barScaleStart) * (totalBarWidth - 1));
                    JsArrayNumber barArray = data.get(i);

                    double val = barArray.get(index) + 1.0;
                    maxBinCount = Math.max(maxBinCount, val);
                    barArray.set(index, val);
                }
            }
        }
    }

    /**
     * This method resets the data array to an array of all zeros
     */
    // XXX this method is REAAAAAAAAAALLY slow in dev mode
    public void resetData() {
        data = JsUtils.createJsArrayGeneric();
        for (int i = 0; i < calculateNumBars(); i++) {
            JsArrayNumber jsArrayNumber = intializeJsArray();
            data.set(i, jsArrayNumber);
        }
    }

    public JsArrayNumber intializeJsArray() {
        JsArrayNumber jsArrayNumber = JsUtils.createJsArrayNumber();
        for (int i = 0; i < totalBarWidth; i++) {
            jsArrayNumber.set(i, 0.0);
        }
        return jsArrayNumber;
    }

    private void calculateStartAndEndOfBarScale() {

        if (visualItemsJsArray.length() == 0) {
            barScaleStart = 0.0;
            barScaleEnd = 0.0;
            return;
        }

        barScaleEnd = getFirstBinValueFromVisualItems();
        barScaleStart = barScaleEnd;

        for (int i = 0; i < visualItemsJsArray.length(); i++) {
            VisualItem visualItem = visualItemsJsArray.get(i);
            LightweightList<Double> values = visualItem.getValue(BINNING_VALUE);
            assert values.size() > 0;

            for (double binVal : values) {
                if (binVal > barScaleEnd) {
                    barScaleEnd = binVal;
                }
                if (binVal < barScaleStart) {
                    barScaleStart = binVal;
                }
            }
        }
    }

    public Double getFirstBinValueFromVisualItems() {
        LightweightList<Double> values = visualItemsJsArray.get(0).getValue(
                BINNING_VALUE);
        return (values).get(0);
    }
}
