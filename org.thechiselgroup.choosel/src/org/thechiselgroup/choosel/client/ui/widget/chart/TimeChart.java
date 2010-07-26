package org.thechiselgroup.choosel.client.ui.widget.chart;

import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Dot;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Line;

public class TimeChart extends ChartWidget {

    private Line line;

    private Dot dot;

    protected double minValue;

    protected double maxValue;

    protected double h;

    protected double w;

    @Override
    public Dot drawChart() {

        return dot;

    }
}