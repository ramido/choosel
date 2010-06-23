package org.thechiselgroup.choosel.client.ui.widget.chart;

public class PieChart extends ChartWidget {

    // @formatter:off
    @Override
    public native Chart drawChart(int width, int height) /*-{
        var val = this.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::val,
        chart = this.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::chart,
        sum = $wnd.pv.sum(val),
        thisChart = this;

        var wedge = chart.add($wnd.pv.Wedge)
            .data(val)
            .left(width/2)
            .bottom(height/2)
            .outerRadius(function() {return width < height ? width/2 - 20 : height/2 - 20;})
            .angle(function(d) {return d / sum * 2 * Math.PI;})
            .strokeStyle("white")
            .lineWidth(.5);

        return wedge;
    }-*/;
    // @formatter:on

}
