package org.thechiselgroup.choosel.client.ui.widget.chart;

public class LineChart extends ChartWidget {
    
    @Override
    public native Chart drawChart(int width, int height) /*-{
	var chart = this.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::chart,
        val = this.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::val,
	thisChart = this;
        
        chart.add($wnd.pv.Line)
            .data(val)
            .bottom(function(d) {return d * 80;})
            .left(function() {return this.index * 20 + 15;})
          .add($wnd.pv.Dot);

        return chart;

    }-*/;
    
}

