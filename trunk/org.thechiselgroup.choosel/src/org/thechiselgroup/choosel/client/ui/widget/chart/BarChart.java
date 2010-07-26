package org.thechiselgroup.choosel.client.ui.widget.chart;

import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Bar;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.ProtovisFunctionDouble;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.ProtovisFunctionString;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Scale;
import org.thechiselgroup.choosel.client.util.ArrayUtils;
import org.thechiselgroup.choosel.client.views.SlotResolver;

public class BarChart extends ChartWidget {

    private Bar bar;

    private ProtovisFunctionDouble barHeight = new ProtovisFunctionDouble() {
        @Override
        public double f(String value, int index) {
            return ((Double.parseDouble(value) - minValue + 0.5) * h / (maxValue
                    - minValue + 1));
        }
    };

    private ProtovisFunctionDouble barLeft = new ProtovisFunctionDouble() {
        @Override
        public double f(String value, int index) {
            return index * w / chartItemArray.size();
        }
    };

    private ProtovisFunctionDouble barWidth = new ProtovisFunctionDouble() {
        @Override
        public double f(String value, int index) {
            return w / chartItemArray.size() - 2;
        }
    };

    private int barBottom = 0;

    private ProtovisFunctionString barFillStyle = new ProtovisFunctionString() {
        @Override
        public String f(String value, int index) {
            return getChartItem(index).getColour();
        }
    };

    protected double minValue;

    protected double maxValue;

    protected double h;

    protected double w;

    private void drawBar() {
        bar = chart.add(Bar.createBar()).data(getJsDataArray(dataArray))
                .bottom(barBottom).height(barHeight).left(barLeft)
                .width(barWidth).fillStyle(barFillStyle);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Bar drawChart() {
        setChartVariables();
        setChartParameters();
        drawBar();
        drawScales(Scale.linear(maxValue + 0.5, minValue - 0.5).range(0, h));

        return bar;
    }

    protected void setChartParameters() {
        chart.width(w).height(h).left(20).top(20);
    }

    protected void setChartVariables() {
        dataArray = getDataArray(SlotResolver.MAGNITUDE_SLOT);
        if (dataArray.size() > 0) {
            minValue = ArrayUtils.min(dataArray);
            maxValue = ArrayUtils.max(dataArray);
        }
        w = width - 40;
        h = height - 40;
    }
}