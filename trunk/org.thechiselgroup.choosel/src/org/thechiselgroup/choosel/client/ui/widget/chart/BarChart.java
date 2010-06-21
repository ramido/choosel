package org.thechiselgroup.choosel.client.ui.widget.chart;

public class BarChart extends ChartWidget {

    // @formatter:off
    @Override
    public native Chart drawChart(int width, int height) /*-{
        var chart = this.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::chart,
        val = this.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::val,
        maxY = this.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::getMaxDataValue()(),
        minY = this.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::getMinDataValue()(),
        y = $wnd.pv.Scale.linear(maxY + .5, minY - .5).range(0, height - 40);

        chart.width(width - 40)
            .height(height - 40)
            .top(20)
            .left(20)
            .right(20)
            .bottom(20);

        chart.add($wnd.pv.Rule)
            .data(function() {return y.ticks();})
            .strokeStyle(function(d) {return d ? "#ccc" : "#999";})
            .top(y)
          .anchor("left").add($wnd.pv.Label)
            .text(y.tickFormat);

        var bar = chart.add($wnd.pv.Bar)
            .data(val)
            .bottom(0)
            .left(function() {return this.index * (width - 40) / val.length;})
            .width((width - 40) / val.length - 5)
            .height(function(d) {return (d - minY + .5) * (height - 40) / (maxY - minY + 1);});

        return bar;
    }-*/;
    // @formatter:on

}