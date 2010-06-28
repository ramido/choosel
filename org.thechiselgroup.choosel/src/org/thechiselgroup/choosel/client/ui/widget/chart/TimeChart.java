package org.thechiselgroup.choosel.client.ui.widget.chart;

public class TimeChart extends ChartWidget {

    // @formatter:off
    @Override
    public native Chart drawChart(int width, int height) /*-{
        var chart = this.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::chart,
        val = this.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::val;

        var start = new $wnd.Date(1990, 0, 1);
        var year = 1000 * 60 * 60 * 24 * 365;
        var data = $wnd.pv.range(0, 20, .02).map(function(x) {
            return {x: new $wnd.Date(start.getTime() + year * x),
                    y: (1 + .1 * (Math.sin(x * 2 * Math.PI))
                        + Math.random() * .1) * Math.pow(1.18, x)
                        + Math.random() * .1};
          });
        var end = data[data.length - 1].x;

        var w = width,
            h1 = height - 55,
            h2 = 30,
            x = $wnd.pv.Scale.linear(start, end).range(0, w),
            y = $wnd.pv.Scale.linear(0, $wnd.pv.max(data, function(d) {return d.y;})).range(0, h2);

        var i = {x:200, dx:100},
            fx = $wnd.pv.Scale.linear().range(0, w),
            fy = $wnd.pv.Scale.linear().range(0, h1);

        var focus = chart.add($wnd.pv.Panel)
            .def("init", function() {
                var d1 = x.invert(i.x),
                    d2 = x.invert(i.x + i.dx),
                    dd = data.slice(
                        Math.max(0, $wnd.pv.search.index(data, d1, function(d) {return d.x;}) - 1),
                        $wnd.pv.search.index(data, d2, function(d) {return d.x;}) + 1);
                fx.domain(d1, d2);
                fy.domain(y.domain());
                return dd;
              })
            .top(0)
            .left(20)
            .height(h1)
            .width(w - 20);

        focus.add($wnd.pv.Rule)
            .data(function() {return fx.ticks();})
            .left(fx)
            .strokeStyle("#eee")
          .anchor("bottom").add($wnd.pv.Label)
            .text(fx.tickFormat);

        focus.add($wnd.pv.Rule)
            .data(function() {return fy.ticks(7);})
            .bottom(fy)
            .strokeStyle(function(d) {return d ? "#aaa" : "#000";})
          .anchor("left").add($wnd.pv.Label)
            .text(fy.tickFormat);

        focus.add($wnd.pv.Panel)
            .overflow("hidden")
          .add($wnd.pv.Area)
            .data(function() {return focus.init();})
            .left(function(d) {return fx(d.x);})
            .bottom(1)
            .height(function(d) {return fy(d.y);})
            .fillStyle("lightsteelblue")
          .anchor("top").add($wnd.pv.Line)
            .fillStyle(null)
            .strokeStyle("steelblue")
            .lineWidth(2);

        var context = chart.add($wnd.pv.Panel)
            .bottom(12)
            .left(20)
            .height(h2)
            .width(w - 20);

        context.add($wnd.pv.Rule)
            .data(x.ticks())
            .left(x)
            .strokeStyle("#eee")
          .anchor("bottom").add($wnd.pv.Label)
            .text(x.tickFormat);

        context.add($wnd.pv.Rule)
            .bottom(0);

        context.add($wnd.pv.Area)
            .data(data)
            .left(function(d) {return x(d.x);})
            .bottom(1)
            .height(function(d) {return y(d.y);})
            .fillStyle("lightsteelblue")
          .anchor("top").add($wnd.pv.Line)
            .strokeStyle("steelblue")
            .lineWidth(2);

        context.add($wnd.pv.Panel)
            .data([i])
            .cursor("crosshair")
            .events("all")
            .event("mousedown", $wnd.pv.Behavior.select())
            .event("select", focus)
          .add($wnd.pv.Bar)
            .left(function(d) {return d.x;})
            .width(function(d) {return d.dx;})
            .fillStyle("rgba(255, 128, 128, .4)")
            .cursor("move")
            .event("mousedown", $wnd.pv.Behavior.drag())
            .event("drag", focus);

        return chart;
    }-*/;
    // @formatter:on

}