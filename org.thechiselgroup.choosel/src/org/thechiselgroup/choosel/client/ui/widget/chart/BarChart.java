package org.thechiselgroup.choosel.client.ui.widget.chart;

public class BarChart extends ChartWidget {

    // @formatter:off
    @Override
    public native Chart drawChart(int width, int height) /*-{
        var chart = this.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::chart,
        val = this.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::val,
        max = this.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::getMaxDataValue()(),
        min = this.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::getMinDataValue()(),
        w = width - 40,
        h = height - 40,
        x = $wnd.pv.Scale.linear(max + .5, min - .5).range(w, 0),
        y = $wnd.pv.Scale.linear(max + .5, min - .5).range(0, h);

        chart.width(w)
            .height(h)
            .top(20)
            .left(20)
            .right(20)
            .bottom(20);

        if(val.length == 0) {
            return chart;
        }
        if(h / w > 1.5) {
            var bar = chart.add($wnd.pv.Bar)
                .data(val)
                .left(0)
                .bottom(function() {return this.index * h / val.length;})
                .height(h / val.length - 5)
                .width(function(d) {return (d - min + .5) * w / (max - min + 1);});

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
                .width(w / val.length - 5)
                .height(function(d) {return (d - min + .5) * h / (max - min + 1);});

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
                .outerRadius(function(d) {return 45 + (d - min) / (max + .5 - min) * (h > w ? (w/2 - 50) : (h/2 - 50));})
                .angle((9 * Math.PI / 5) / val.length)
                .startAngle(function() {return (9 * Math.PI / 5) * this.index / val.length + 8 * Math.PI / 5;});

            chart.add($wnd.pv.Dot)
                .data(function() {return y.ticks();})
                .left(w/2)
                .bottom(h/2)
                .fillStyle(null)
                .strokeStyle("#ccc")
                .lineWidth(1)
                .size(function(d) {return Math.pow((45 + (d - min) / (max + .5 - min) * (h > w ? (w/2 - 50) : (h/2 - 50))), 2);})
              .anchor("top").add($wnd.pv.Label)
                .textBaseline("middle")
                .text(function(d) {return d;});
        }

        return bar;
    }-*/;
    // @formatter:on

}