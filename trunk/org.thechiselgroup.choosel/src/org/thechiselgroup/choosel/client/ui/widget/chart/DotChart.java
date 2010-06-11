package org.thechiselgroup.choosel.client.ui.widget.chart;

public class DotChart extends ChartWidget {

    @Override
    public native Chart drawChart(int width, int height) /*-{
        var chart = this.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::chart,
        val = this.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::val,
        sum = $wnd.pv.sum(val),
        thisChart = this,
        s,
        isBrushed = new Array();
            
        var selectBox = chart.add($wnd.pv.Panel)
            .data([{x:20, y:20, dx:100, dy:100}])
            .events("all")
            .event("mousedown", $wnd.pv.Behavior.select())
            .event("selectstart", update)
            .event("select", update)
            .event("selectend", function() {return (s = null, chart);})
          .add($wnd.pv.Bar)
            .left(function(d) {return d.x;})
            .top(function(d) {return d.y;})
            .width(function(d) {return d.dx;})
            .height(function(d) {return d.dy;})
            .fillStyle("rgba(0,0,0,.15)")
            .event("mousedown", $wnd.pv.Behavior.drag())
            .event("dragstart", update)
            .event("drag", update);

        var dot = selectBox.add($wnd.pv.Dot)
            .data(val)
            .bottom(function(d) {return d * height / 10;})
            .left(function() {return this.index * width / val.length;})
            .fillStyle(function(d) {return (s
                && ((this.index * width / val.length < s.x1) || (this.index * width / val.length > s.x2)
                || (height - (d * height / 10) < s.y1) || (height - (d * height / 10) > s.y2))
                ? "steelblue" : "orange");});
    
        function update(d) {
    	    s = d;
      	    s.x1 = d.x;
      	    s.x2 = d.x + d.dx;
    	    s.y1 = d.y;
    	    s.y2 = d.y + d.dy;
    	    dot.context(null, 0, function() {return this.render();});
    	    
    	    var i;
    	    for(i = 0; i < val.length; i++) {
    	        if((i * width / val.length > s.x1) && (i * width / val.length < s.x2)
                && (height - (val[i] * height / 10) > s.y1) && (height - (val[i] * height / 10) < s.y2)
                && isBrushed[i] != true) {
    	    	    thisChart.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::onBrushEvent(ILcom/google/gwt/user/client/Event;)
            	    (i,$wnd.pv.event);
            	    isBrushed[i] = true;
                } else if(((i * width / val.length < s.x1) || (i * width / val.length > s.x2)
                || (height - (val[i] * height / 10) < s.y1) || (height - (val[i] * height / 10) > s.y2))
                && (isBrushed[i] == true)) {
    	    	    thisChart.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::onBrushEvent(ILcom/google/gwt/user/client/Event;)
            	    (i,$wnd.pv.event);
            	    isBrushed[i] = false;
                }
                     
    	    }
        }
            
        return dot;

    }-*/;


}