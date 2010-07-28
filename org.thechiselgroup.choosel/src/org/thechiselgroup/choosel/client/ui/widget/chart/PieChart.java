package org.thechiselgroup.choosel.client.ui.widget.chart;

import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.ProtovisFunctionDouble;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Wedge;
import org.thechiselgroup.choosel.client.views.SlotResolver;
import org.thechiselgroup.choosel.client.views.chart.ChartItem;

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

        SlotValues slotValues = getSlotValues(SlotResolver.MAGNITUDE_SLOT);

        sum = 0;
        for (Double datum : slotValues.values()) {
            sum += datum;
        }

        wedge = chart.add(Wedge.createWedge()).data(chartItemsJSArray)
                .left(width / 2).bottom(height / 2)
                .outerRadius(width < height ? width / 2 - 5 : height / 2 - 5)
                .angle(new ProtovisFunctionDouble() {
                    @Override
                    public double f(ChartItem value, int index) {
                        double resolvedValue = Double
                                .parseDouble((String) value.getResourceItem()
                                        .getResourceValue(
                                                SlotResolver.MAGNITUDE_SLOT));

                        return resolvedValue / sum * 2 * Math.PI;
                    }
                });

        return wedge;
    }

    protected void setChartParameters() {
        chart.width(w).height(h).left(20).top(20);
    }

    protected void setChartVariables() {
        SlotValues dataArray = getSlotValues(SlotResolver.MAGNITUDE_SLOT);
        minValue = dataArray.min();
        maxValue = dataArray.max();
        w = width - 40;
        h = height - 40;
    }

}
