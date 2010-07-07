package org.thechiselgroup.choosel.client.ui.widget.chart;

import org.thechiselgroup.choosel.client.views.SlotResolver;

public class BarChart extends ChartWidget {

    // @formatter:off
    @Override
    public native Chart drawChart(int width, int height) /*-{
        var chart = this.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::chart,
            h = height - 40,
            w = width - 40,
            val = new Array(),
            max,
            min;

        for(var i = 0; i < this.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::chartItemArray.@java.util.ArrayList::size()(); i++) {
            if(max == null && min == null) {
                max = this.@org.thechiselgroup.choosel.client.ui.widget.chart.BarChart::getSlotValue(I)(i);
                min = this.@org.thechiselgroup.choosel.client.ui.widget.chart.BarChart::getSlotValue(I)(i);
            } else {
                if(max < this.@org.thechiselgroup.choosel.client.ui.widget.chart.BarChart::getSlotValue(I)(i)) {
                    max = this.@org.thechiselgroup.choosel.client.ui.widget.chart.BarChart::getSlotValue(I)(i);
                }
                if(min > this.@org.thechiselgroup.choosel.client.ui.widget.chart.BarChart::getSlotValue(I)(i)) {
                    min = this.@org.thechiselgroup.choosel.client.ui.widget.chart.BarChart::getSlotValue(I)(i);
                }
            }

            val[i] = this.@org.thechiselgroup.choosel.client.ui.widget.chart.BarChart::getSlotValue(I)(i);
        }

        chart.width(w)
            .height(h)
            .top(20)
            .left(20)
            .right(20)
            .bottom(20);

        if(val.length == 0) {
            return chart;
        }

        var x = $wnd.pv.Scale.linear(min - .5, max).range(0, w),
            y = $wnd.pv.Scale.linear(max, min - .5).range(0, h);

        if(h / w > 1.5) {
            var bar = chart.add($wnd.pv.Bar)
                .data(val)
                .left(0)
                .bottom(function() {return this.index * h / val.length;})
                .height(h / val.length - 2)
                .width(function(d) {return (d - min + .5) * w / (max - min + .5);});

            chart.add($wnd.pv.Rule)
                .data(function() {return x.ticks();})
                .strokeStyle(function(d) {return d ? "#ccc" : "#999";})
                .left(x)
              .anchor("bottom").add($wnd.pv.Label)
                .text(x.tickFormat);
        } else if(w / h > 1.5) {
            var bar = chart.add($wnd.pv.Bar)
                .data(val)
                .bottom(0)
                .left(function() {return this.index * w / val.length;})
                .width(w / val.length - 2)
                .height(function(d) {return (d - min + .5) * h / (max - min + .5);});

            chart.add($wnd.pv.Rule)
                .data(function() {return y.ticks();})
                .strokeStyle(function(d) {return d ? "#ccc" : "#999";})
                .top(y)
              .anchor("left").add($wnd.pv.Label)
                .text(y.tickFormat);
        } else {
            var bar = chart.add($wnd.pv.Wedge)
                .data(val)
                .bottom(h/2)
                .left(w/2)
                .innerRadius(40)
                .outerRadius(function(d) {return 45 + (d - min) / (max - min + .5) * (h > w ? (w/2 - 50) : (h/2 - 50));})
                .angle((9 * Math.PI / 5) / val.length)
                .startAngle(function() {return (9 * Math.PI / 5) * this.index / val.length + 8 * Math.PI / 5;});

            chart.add($wnd.pv.Dot)
                .data(function() {return y.ticks();})
                .left(w/2)
                .bottom(h/2)
                .fillStyle(null)
                .strokeStyle("#ccc")
                .lineWidth(1)
                .size(function(d) {return Math.pow((45 + (d - min) / (max - min + .5) * (h > w ? (w/2 - 50) : (h/2 - 50))), 2);})
              .anchor("top").add($wnd.pv.Label)
                .textBaseline("middle")
                .text(function(d) {return d;});
        }

        return bar;
    }-*/;
    // @formatter:on

    private Object getSlotValue(int i) {
        return getChartItem(i).getResourceValue(SlotResolver.MAGNITUDE_SLOT);
    }

}