package org.thechiselgroup.choosel.client.ui.widget.chart;

import com.google.gwt.core.client.JavaScriptObject;

public class PieChart extends ChartWidget {
    
    @Override
    public native void drawGraph(JavaScriptObject chart, int width, int height) /*-{

	var val = this.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::val,
	sum = $wnd.pv.sum(val),
	graph = this;
        
        var wedge = chart.add($wnd.pv.Wedge)
            .data(val)
            .left(width/2)
            .bottom(height/2)
            .outerRadius(function() {return width < height ? width/2 - 20 : height/2 - 20;})
            .angle(function(d) {return d / sum * 2 * Math.PI;})
            .event("click",function() {return graph.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::onClick(III)(this.index,chart.mouse().x,chart.mouse().y);})
            .event("mousemove",function() {return graph.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::onMouseOver(III)(this.index,chart.mouse().x,chart.mouse().y);})
            .event("mouseout",function() {return graph.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::onMouseOut(III)(this.index,chart.mouse().x,chart.mouse().y);});
	
        wedge.add($wnd.pv.Label)
            .font(function() {return (width < height ? width/25 : height/25) + "px sans-serif";})
            .left(function() {return (width < height ? width/2 - 20 : height/2 - 20)/2 
                * Math.cos(wedge.midAngle()) + width/2;})
            .bottom(function() {return -(width < height ? width/2 - 20 : height/2 - 20)/2 
                * Math.sin(wedge.midAngle()) + height/2;})
            .textAlign("center")
            .textBaseline("middle");
          
        wedge.root.render();

    }-*/;
    
}

