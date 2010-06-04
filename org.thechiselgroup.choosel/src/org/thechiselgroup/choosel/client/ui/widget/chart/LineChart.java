package org.thechiselgroup.choosel.client.ui.widget.chart;

public class LineChart extends ChartWidget {
    
    @Override
    public native Chart drawChart(int width, int height) /*-{

	var val = this.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::val,
	graph = this;
        
        chart.add($wnd.pv.Line)
            .data(val)
            .bottom(function(d) {return d * height / 8;})
            .left(function() {return this.index * width / val.length + 15;})
          .add($wnd.pv.Dot)
            .event("click",function() {return graph.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::onMouseClick(I)(this.index);})
            .event("mousemove",function() {return graph.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::onMouseOver(III)(this.index,chart.mouse().x,chart.mouse().y);})
            .event("mouseout",function() {return graph.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::onMouseOut(III)(this.index,chart.mouse().x,chart.mouse().y);})
          .root.render();
          
        return chart;

    }-*/;
    
}

