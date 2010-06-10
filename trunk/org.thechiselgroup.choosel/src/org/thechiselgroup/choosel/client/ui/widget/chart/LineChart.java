package org.thechiselgroup.choosel.client.ui.widget.chart;

public class LineChart extends ChartWidget {

    @Override
    public native Chart drawChart(int width, int height) /*-{
        var chart = this.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::chart,
        val = this.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::val,
        thisChart = this;

        var line = chart.add($wnd.pv.Dot)
            .data(val)
            .bottom(function(d) {return d * height / 10;})
            .left(function() {return this.index * width / val.length;});
            
//        line.add($wnd.pv.Panel)
//   .data([{x:20, y:20, dx:100, dy:100}])
//   .cursor("crosshair")
//   .events("all")
//   .event("mousedown", $wnd.pv.Behavior.select())
//   .event("selectstart", function() {return (s = null, vis);})
// .add($wnd.pv.Bar)
////   .visible(function(d, k, t) s && s.px == t.px && s.py == t.py)
//   .left(function(d) {return d.x;})
//   .top(function(d) {return d.y;})
//   .width(function(d) {return d.dx;})
//   .height(function(d) {return d.dy;})
////   .fillStyle("rgba(0,0,0,.15)")
//   .strokeStyle("white")
//   .cursor("move")
//   .event("mousedown", $wnd.pv.Behavior.drag());
            
        return line;
    }-*/;

}