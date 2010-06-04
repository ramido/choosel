package org.thechiselgroup.choosel.client.ui.widget.chart;

public class BarChart extends ChartWidget {

    @Override
    public native Chart drawChart(int width, int height) /*-{
        var chart = this.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::chart,
        thisChart = this,
        val = this.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::val;

        var bar = chart.add($wnd.pv.Bar)
            .data(val)
            .bottom(0)
            .width(width / val.length - 5)
            .height(function(d) {return d * height / 10;})
            .left(function() {return this.index * width / val.length;});

        return bar;
    }-*/;

}
