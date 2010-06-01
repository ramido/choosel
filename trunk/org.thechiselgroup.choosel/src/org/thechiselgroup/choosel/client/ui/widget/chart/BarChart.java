package org.thechiselgroup.choosel.client.ui.widget.chart;

import com.google.gwt.core.client.JavaScriptObject;

public class BarChart extends ChartWidget {
    
    @Override
    public native void drawGraph(JavaScriptObject chart, int width, int height) /*-{

	var val = this.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::val,
	colors = ["red","green","blue"],
	graph = this;
        
        var bar = chart.add($wnd.pv.Bar)
            .data(val)
            .bottom(0)
            .width(width / val.length - 5)
            .height(function(d) {return d * height / 10;})
            .left(function() {return this.index * width / val.length;})
            .fillStyle(function() {return colors[this.index % colors.length];})
            .event("click",function() {return graph.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::onClick(III)(this.index,chart.mouse().x,chart.mouse().y);})
            .event("mousemove",function() {return graph.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::onMouseOver(III)(this.index,chart.mouse().x,chart.mouse().y);})
            .event("mouseout",function() {return graph.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::onMouseOut(III)(this.index,chart.mouse().x,chart.mouse().y);});
        
        bar.add($wnd.pv.Label)
            .font(function() {return (width < height ? width/25 : height/25) + "px sans-serif";})
            .top(function() {return bar.top();})
            .left(function() {return bar.left() + bar.width() / 2;})
            .textAlign("center")
            .textBaseline("top")
            .textStyle("white");
        
        bar.root.render();


    }-*/;
    
}

