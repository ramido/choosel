package org.thechiselgroup.choosel.client.ui.widget.chart;

import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Dot;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.ProtovisFunctionDouble;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.ProtovisFunctionString;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Scale;
import org.thechiselgroup.choosel.client.util.ArrayUtils;
import org.thechiselgroup.choosel.client.views.SlotResolver;

public class DotChart extends ChartWidget {

    private Dot dot;

    private ProtovisFunctionDouble dotBottom = new ProtovisFunctionDouble() {
        @Override
        public double f(String value, int index) {
            return ((Double.parseDouble(value) - minValue + 0.5) * h / (maxValue
                    - minValue + 1));
        }
    };

    private ProtovisFunctionDouble dotLeft = new ProtovisFunctionDouble() {
        @Override
        public double f(String value, int index) {
            return index * w / chartItemArray.size();
        }
    };

    private String dotStrokeStyle = "rgba(0,0,0,0.35)";

    private ProtovisFunctionString dotFillStyle = new ProtovisFunctionString() {
        @Override
        public String f(String value, int index) {
            return getChartItem(index).getColour();
        }
    };

    protected double minValue;

    protected double maxValue;

    protected double h;

    protected double w;

    @SuppressWarnings("unchecked")
    @Override
    public Dot drawChart() {
        setChartVariables();
        setChartParameters();
        drawScales(Scale.linear(maxValue + 0.5, minValue - 0.5).range(0, h));
        drawDot();

        return dot;
    }

    private void drawDot() {
        dot = chart.add(Dot.createDot()).data(getJsDataArray(dataArray))
                .cursor("pointer").bottom(dotBottom).left(dotLeft)
                .strokeStyle(dotStrokeStyle).fillStyle(dotFillStyle);
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