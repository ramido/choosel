package org.thechiselgroup.choosel.client.ui.widget.chart;

public class LineChart extends ChartWidget {
    
    @Override
    public native Chart drawChart(int width, int height) /*-{
	var chart = this.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::chart,
        val = this.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::val,
	thisChart = this;
        
        var bar = chart.add($wnd.pv.Line)
            .data(val)
            .bottom(function(d) {return d * height / 10;})
            .left(function() {return this.index * width / val.length;});

        return bar;

    }-*/;
    
}

