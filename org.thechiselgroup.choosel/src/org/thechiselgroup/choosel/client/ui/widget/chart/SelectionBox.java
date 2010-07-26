package org.thechiselgroup.choosel.client.ui.widget.chart;

import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark;

public class SelectionBox extends ChartWidget {

    public static native void drawBox() /*-{
        var chart = this.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::chart;

        chart.add($wnd.pv.Label).text("hello").left(30).top(30);
    }-*/;

    protected double minValue;
    protected double maxValue;
    protected double h;
    protected double w;

    @Override
    protected <T extends Mark> T drawChart() {
        return null;
    }

}
