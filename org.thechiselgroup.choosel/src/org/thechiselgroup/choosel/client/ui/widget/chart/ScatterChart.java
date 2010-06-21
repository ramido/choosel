package org.thechiselgroup.choosel.client.ui.widget.chart;

public class ScatterChart extends ChartWidget {

    @Override
    public native Chart drawChart(int width, int height) /*-{
        var chart = this.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::chart,
        valX = this.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::valX,
        valY = this.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::valY,
        thisChart = this,
        s, val = new Array(),
        isBrushed = new Array();
        
        var i;
        for(i = 0; i < valX.length; i++) {
            val[i] = {x: valX[i], y: valY[i], isBrushed: false};
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
            .left(function(d) {return x(d.x);})
            .top(function(d) {return y(d.y);})
            .radius(function() {return 5 / this.scale;})
            .strokeStyle("rgba(0,0,0,.35)")
            .fillStyle(function(d) {return (s && ((this.index * width / val.length < s.x1) || (this.index * width / val.length > s.x2)
                || (height - (d * height / 10) < s.y1) || (height - (d * height / 10) > s.y2)) || !s
                ? thisChart.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::getChartItem(I)(this.index)
               	    .@org.thechiselgroup.choosel.client.views.chart.ChartItem::getColour()() : "rgba(256,256,0,.6)");});

	return dot;

    }-*/;


}