package org.thechiselgroup.choosel.client.ui.widget.chart;

import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Bar;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Label;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.ProtovisFunctionDouble;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.ProtovisFunctionDoubleWithCache;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.ProtovisFunctionString;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.ProtovisFunctionStringToString;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Rule;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Scale;
import org.thechiselgroup.choosel.client.util.ArrayUtils;
import org.thechiselgroup.choosel.client.views.SlotResolver;
import org.thechiselgroup.choosel.client.views.chart.ChartItem;

// TODO bar chart should always start at 0
// TODO bottom border line
// TODO right side ticks
public class BarChart extends ChartWidget {

    private static final int BAR_PADDING = 6;

    private static final String STEELBLUE = "steelblue";

    private static final double SCALE_PADDING = 0.5;

    private static final int BORDER_HEIGHT = 10;

    private static final int BORDER_WIDTH = 20;

    private Bar bar;

    private ProtovisFunctionDoubleWithCache barHeight = new ProtovisFunctionDoubleWithCache() {

        private double relativeHeight;

        private double base;

        private SlotValues slotValues;

        @Override
        public void beforeRender() {
            if (chartItems.isEmpty()) {
                return;
            }

            slotValues = getSlotValues(SlotResolver.MAGNITUDE_SLOT);

            double scaleLength = slotValues.max();// - 0 + SCALE_PADDING * 2;

            base = 0;// -SCALE_PADDING;
            relativeHeight = chartHeight / scaleLength;
        }

        @Override
        public double f(ChartItem value, int index) {
            return (slotValues.value(index) + base) * relativeHeight - 1;
        }
    };

    private ProtovisFunctionDouble barLeft = new ProtovisFunctionDouble() {
        @Override
        public double f(ChartItem value, int index) {
            return BAR_PADDING + (index * chartWidth / chartItems.size());
        }
    };

    private ProtovisFunctionDouble barWidth = new ProtovisFunctionDouble() {
        @Override
        public double f(ChartItem value, int index) {
            return chartWidth / (chartItems.size() * 2);
        }
    };

    private static final String ORANGE = "orange";

    private static final String YELLOW = "yellow";

    private int barBottom = BORDER_HEIGHT + 1;

    private ProtovisFunctionString barFillStyle = new ProtovisFunctionString() {
        @Override
        public String f(ChartItem value, int index) {
            switch (value.getResourceItem().getStatus()) {
            case PARTIALLY_HIGHLIGHTED:
            case PARTIALLY_HIGHLIGHTED_SELECTED:
            case HIGHLIGHTED_SELECTED:
            case HIGHLIGHTED:
                return YELLOW;
            case DEFAULT:
                return STEELBLUE;
            case SELECTED:
                return ORANGE;
            }
            throw new RuntimeException("No colour available");
        }
    };

    protected double chartHeight;

    protected double chartWidth;

    @Override
    protected void beforeRender() {
        barHeight.beforeRender();
    }

    private void calculateChartVariables() {
        chartWidth = width - BORDER_WIDTH * 2;
        chartHeight = height - BORDER_HEIGHT * 2;
    }

    private void drawBar() {
        bar = chart.add(Bar.createBar()).data(chartItemsJSArray)
                .bottom(barBottom).height(barHeight).left(barLeft)
                .width(barWidth).fillStyle(barFillStyle).strokeStyle(STEELBLUE);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Bar drawChart() {
        assert ArrayUtils.length(chartItemsJSArray) >= 1;

        calculateChartVariables();
        setChartParameters();

        SlotValues slotValues = getSlotValues(SlotResolver.MAGNITUDE_SLOT);

        drawScales(Scale.linear(slotValues.max(), 0).range(0, chartHeight));
        drawBar();

        return bar;
    }

    protected void drawScales(Scale scale) {
        this.scale = scale;
        // TODO // should // take // double // with // labelText
        chart.add(Rule.createRule())
                .data(scale.ticks())
                .top(scale)
                .strokeStyle(new ProtovisFunctionStringToString() {
                    @Override
                    public String f(String value, int index) {
                        return Double.parseDouble(value) == 0 ? "gray"
                                : "lightgray";
                    }
                }).width(chartWidth).anchor("left").add(Label.createLabel())
                .text(labelText);

        chart.add(Rule.createRule()).left(0).bottom(BORDER_HEIGHT)
                .strokeStyle("gray");
    }

    private void setChartParameters() {
        chart.left(BORDER_WIDTH).top(BORDER_HEIGHT);
    }
}