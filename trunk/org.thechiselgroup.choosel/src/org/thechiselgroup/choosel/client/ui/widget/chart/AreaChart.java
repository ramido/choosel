package org.thechiselgroup.choosel.client.ui.widget.chart;

import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Area;

public class AreaChart extends ChartWidget {

    private Area area;

    @SuppressWarnings("unchecked")
    @Override
    public Area drawChart() {
        return area;
    }

}