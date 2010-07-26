package org.thechiselgroup.choosel.client.ui.widget.chart;

import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.ProtovisFunctionDouble;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Wedge;
import org.thechiselgroup.choosel.client.util.ArrayUtils;
import org.thechiselgroup.choosel.client.views.SlotResolver;

public class PieChart extends ChartWidget {

    private Wedge wedge;

    private double sum;

    protected double minValue;

    protected double maxValue;

    protected double h;

    protected double w;

    @SuppressWarnings("unchecked")
    @Override
    public Wedge drawChart() {

        dataArray = getDataArray(SlotResolver.MAGNITUDE_SLOT);

        sum = 0;
        for (Double datum : dataArray) {
            sum += datum;
        }

        wedge = chart.add(Wedge.createWedge()).data(getJsDataArray(dataArray))
                .left(width / 2).bottom(height / 2)
                .outerRadius(width < height ? width / 2 - 5 : height / 2 - 5)
                .angle(new ProtovisFunctionDouble() {
                    @Override
                    public double f(String value, int index) {
                        return Double.parseDouble(value) / sum * 2 * Math.PI;
                    }
                });

        return wedge;
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
