package org.thechiselgroup.choosel.client.ui.widget.chart;

import org.thechiselgroup.choosel.client.views.SlotResolver;

public class ScatterChart extends ChartWidget {

    // @formatter:off
    @Override
    public native Chart drawChart(int width, int height) /*-{
        var chart = this.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::chart,
        s,
        thisChart = this,
        val = new Array();

        for(var i = 0; i < this.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::chartItemArray.@java.util.ArrayList::size()(); i++) {
            val[i] = {x: this.@org.thechiselgroup.choosel.client.ui.widget.chart.ScatterChart::getSlotValue(II)(i,0),
                y: this.@org.thechiselgroup.choosel.client.ui.widget.chart.ScatterChart::getSlotValue(II)(i,1)};
        }

        var minX = this.@org.thechiselgroup.choosel.client.ui.widget.chart.ScatterChart::min(I)(0) - 0.5;
        var maxX = this.@org.thechiselgroup.choosel.client.ui.widget.chart.ScatterChart::max(I)(0) + 0.5;

        var minY = this.@org.thechiselgroup.choosel.client.ui.widget.chart.ScatterChart::min(I)(1) - 0.5;
        var maxY = this.@org.thechiselgroup.choosel.client.ui.widget.chart.ScatterChart::max(I)(1) + 0.5;

        var x = $wnd.pv.Scale.linear(minX, maxX).range(0, width - 40),
            y = $wnd.pv.Scale.linear(maxY, minY).range(0, height - 40);

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
            .cursor("pointer")
            .left(function(d) {return x(d.x);})
            .top(function(d) {return y(d.y);})
            .radius(function() {return 5 / this.scale;})
            .strokeStyle("rgba(0,0,0,.35)")
            .fillStyle(function() {return thisChart.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::getChartItem(I)(this.index)
               	    .@org.thechiselgroup.choosel.client.views.chart.ChartItem::getColour()();});

        return dot;
    }-*/;
    // @formatter:on

    public double getDoubleSlotValue(int i, int coordinate) {
        Object value = getSlotValue(i, coordinate);

        if (value instanceof String) {
            return Double.parseDouble((String) value);
        }

        return ((Number) value).doubleValue();
    }

    protected Object getSlotValue(int i, int coordinate) {
        if (coordinate == 0) {
            return getChartItem(i).getResourceValue(
                    SlotResolver.X_COORDINATE_SLOT);
        } else if (coordinate == 1) {
            return getChartItem(i).getResourceValue(
                    SlotResolver.Y_COORDINATE_SLOT);
        }
        return null;
    }

    private double max(int coordinate) {
        double max = Double.MIN_VALUE;
        if (chartItemArray.size() == 0) {
            return 0;
        }
        for (int i = 0; i < chartItemArray.size(); i++) {
            double slotValue = getDoubleSlotValue(i, coordinate);
            if (max < slotValue) {
                max = slotValue;
            }
        }
        return max;
    }

    private double min(int coordinate) {
        double min = Double.MAX_VALUE;
        if (chartItemArray.size() == 0) {
            return 0;
        }
        for (int i = 0; i < chartItemArray.size(); i++) {
            double slotValue = getDoubleSlotValue(i, coordinate);
            if (min > slotValue) {
                min = slotValue;
            }
        }
        return min;
    }

}