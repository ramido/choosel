package org.thechiselgroup.choosel.client.ui.widget.chart;

public class LineChart extends ChartWidget {
    
    @Override
    public native Chart drawChart(int width, int height) /*-{

	var chart = this.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::chart,
        val = this.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::val,
	graph = this;
        
        
        chart.add($wnd.pv.Bar)
            .data([1, 1.2, 1.7, 1.5, .7])
            .bottom(0)
            .width(20)
            .height(function(d) {return d * 80;})
            .left(function() {return this.index * 25;})
	.root.render();
        
        return chart;

    }-*/;
    
}

