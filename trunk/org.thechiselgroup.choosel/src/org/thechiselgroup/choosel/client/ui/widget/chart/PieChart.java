package org.thechiselgroup.choosel.client.ui.widget.chart;

import org.thechiselgroup.choosel.client.views.SlotResolver;

public class PieChart extends ChartWidget {

    // FIXME
    // @formatter:off
    @Override
    public native Chart drawChart(int width, int height) /*-{
           var chart = this.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::chart,
           thisChart = this,
           val = new Array(),
           sum = 0;

           for(var i = 0; i < this.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::chartItemArray.@java.util.ArrayList::size()(); i++) {
               val[i] = this.@org.thechiselgroup.choosel.client.ui.widget.chart.PieChart::getSlotValue(I)(i);
               sum += val[i] * 1;
           }

           var wedge = chart.add($wnd.pv.Wedge)
               .data(val)
               .left(width/2)
               .bottom(height/2)
               .outerRadius(function() {return width < height ? width/2 - 20 : height/2 - 20;})
               .angle(function(d) {return d / sum * 2 * Math.PI;})
               .strokeStyle("white")
               .lineWidth(.5);

           return wedge;
       }-*/;

    // @formatter:on

    private Object getSlotValue(int i) {
        return getChartItem(i).getResourceValue(SlotResolver.MAGNITUDE_SLOT);
    }

}
