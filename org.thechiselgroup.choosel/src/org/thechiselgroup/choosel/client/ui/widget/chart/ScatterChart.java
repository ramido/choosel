package org.thechiselgroup.choosel.client.ui.widget.chart;

import org.thechiselgroup.choosel.client.views.SlotResolver;

public class ScatterChart extends ChartWidget {

    // @formatter:off
    @Override
    public native Chart drawChart(int width, int height) /*-{
        var chart = this.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::chart,
        s,
        thisChart = this,
        val = new Array();

        for(var i = 0; i < this.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::chartItemArray.@java.util.ArrayList::size()(); i++) {
            val[i] = {x: this.@org.thechiselgroup.choosel.client.ui.widget.chart.ScatterChart::getSlotValue(II)(i,0),
                y: this.@org.thechiselgroup.choosel.client.ui.widget.chart.ScatterChart::getSlotValue(II)(i,1)};
        }

        var kx = 10,
            ky = 10,
            x = $wnd.pv.Scale.linear(0, kx).range(0, width - 40),
            y = $wnd.pv.Scale.linear(ky, 0).range(0, height - 40);

        chart.width(width - 40)
            .height(height - 40)
            .top(20)
            .left(20)
            .right(20)
            .bottom(20);

        chart.add($wnd.pv.Rule)
            .data(function() {return x.ticks();})
            .strokeStyle(function(d) {return d ? "#ccc" : "#999";})
            .left(x)
          .anchor("bottom").add($wnd.pv.Label)
            .text(x.tickFormat);

        chart.add($wnd.pv.Rule)
            .data(function() {return y.ticks();})
            .strokeStyle(function(d) {return d ? "#ccc" : "#999";})
            .top(y)
          .anchor("left").add($wnd.pv.Label)
            .text(y.tickFormat);

        var dot = chart.add($wnd.pv.Panel)
            .overflow("hidden")
          .add($wnd.pv.Dot)
            .data(val)
            .cursor("pointer")
            .left(function(d) {return x(d.x);})
            .top(function(d) {return y(d.y);})
            .radius(function() {return 5 / this.scale;})
            .strokeStyle("rgba(0,0,0,.35)")
            .fillStyle(function() {return thisChart.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::getChartItem(I)(this.index)
               	    .@org.thechiselgroup.choosel.client.views.chart.ChartItem::getColour()();});

        return dot;
    }-*/;
    // @formatter:on

    private Object getSlotValue(int i, int coordinate) {
        if (coordinate == 0) {
            return getChartItem(i).getResourceValue(
                    SlotResolver.X_COORDINATE_SLOT);
        } else if (coordinate == 1) {
            return getChartItem(i).getResourceValue(
                    SlotResolver.Y_COORDINATE_SLOT);
        }
        return null;
    }

}