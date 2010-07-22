package org.thechiselgroup.choosel.client.ui.widget.chart;

import java.util.Collections;
import java.util.Comparator;

import org.thechiselgroup.choosel.client.views.SlotResolver;
import org.thechiselgroup.choosel.client.views.chart.ChartItem;

public class TimeChart extends ChartWidget {

    // @formatter:off
    @Override
    public native Chart drawChart(int width, int height) /*-{
           var chart = this.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::chart,
           val = new Array();

           for(var i = 0; i < this.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::chartItemArray.@java.util.ArrayList::size()(); i++) {
               var xCoord = this.@org.thechiselgroup.choosel.client.ui.widget.chart.TimeChart::getSlotValue(II)(i,0);
               val[i] = {x: new $wnd.Date(xCoord),
                   y: this.@org.thechiselgroup.choosel.client.ui.widget.chart.TimeChart::getSlotValue(II)(i,1)};
           }

           function sortByDate(a,b) {
               return a.x - b.x; 
           }

           val.sort(sortByDate);

           //        this.@org.thechiselgroup.choosel.client.ui.widget.chart.TimeChart::sortArray()();

           if(val.length == 0) {
               return chart;
           }

           var start = val[0].x;
           var end = val[val.length - 1].x;

           var w = width - 25,
               h1 = height - 60,
               h2 = 30,
               x = $wnd.pv.Scale.linear(start, end).range(0, w),
               y = $wnd.pv.Scale.linear(0, $wnd.pv.max(val, function(d) {return d.y;})).range(0, h2);

           var i = {x:200, dx:100},
               fx = $wnd.pv.Scale.linear().range(0, w),
               fy = $wnd.pv.Scale.linear().range(0, h1);

           var focus = chart.add($wnd.pv.Panel)
               .def("init", function() {
                   var d1 = x.invert(i.x),
                       d2 = x.invert(i.x + i.dx),
                       dd = val.slice(
                           Math.max(0, $wnd.pv.search.index(val, d1, function(d) {return d.x;}) - 1),
                           $wnd.pv.search.index(val, d2, function(d) {return d.x;}) + 1);
                   fx.domain(d1, d2);
                   fy.domain(y.domain());
                   return dd;
                 })
               .top(5)
               .left(20)
               .height(h1)
               .width(w)
               .events("none");

           focus.add($wnd.pv.Rule)
               .data(function() {return fx.ticks();})
               .left(fx)
               .strokeStyle("#eee")
             .anchor("bottom").add($wnd.pv.Label)
               .text(fx.tickFormat);

           focus.add($wnd.pv.Rule)
               .data(function() {return fy.ticks(7);})
               .bottom(fy)
               .strokeStyle(function(d) {return d ? "#aaa" : "#000";})
             .anchor("left").add($wnd.pv.Label)
               .text(fy.tickFormat);

           focus.add($wnd.pv.Panel)
               .overflow("hidden")
             .add($wnd.pv.Area)
               .data(function() {return focus.init();})
               .left(function(d) {return fx(d.x);})
               .bottom(1)
               .height(function(d) {return fy(d.y);})
               .fillStyle("lightgreen")
             .anchor("top").add($wnd.pv.Line)
               .fillStyle(null)
               .strokeStyle("green")
               .lineWidth(2);

           var dot = focus.add($wnd.pv.Dot)
               .data(function() {return focus.init();})
               .left(function(d) {return fx(d.x);})
               .bottom(function(d) {return fy(d.y);})
               .fillStyle("steelblue")
               .size(3);

           var context = chart.add($wnd.pv.Panel)
               .bottom(12)
               .left(20)
               .height(h2)
               .width(w);

           context.add($wnd.pv.Rule)
               .data(x.ticks())
               .left(x)
               .strokeStyle("#eee")
             .anchor("bottom").add($wnd.pv.Label)
               .text(x.tickFormat);

           context.add($wnd.pv.Rule)
               .bottom(0);

           context.add($wnd.pv.Area)
               .data(val)
               .left(function(d) {return x(d.x);})
               .bottom(1)
               .height(function(d) {return y(d.y);})
               .fillStyle("lightgreen")
             .anchor("top").add($wnd.pv.Line)
               .strokeStyle("green")
               .lineWidth(2);

           context.add($wnd.pv.Panel)
               .data([i])
               .cursor("crosshair")
               .events("all")
               .event("mousedown", $wnd.pv.Behavior.select())
               .event("select", focus)
             .add($wnd.pv.Bar)
               .left(function(d) {return d.x;})
               .width(function(d) {return d.dx;})
               .fillStyle("rgba(255, 128, 128, .4)")
               .cursor("move")
               .event("mousedown", $wnd.pv.Behavior.drag())
               .event("drag", focus);

           return dot;
       }-*/;

    // @formatter:on

    private Object getSlotValue(int i, int slot) {
        if (slot == 0) {
            return getChartItem(i).getResourceValue(SlotResolver.DATE_SLOT);
        } else if (slot == 1) {
            return getChartItem(i)
                    .getResourceValue(SlotResolver.MAGNITUDE_SLOT);
        }
        return null;
    }

    private void sortArray() {
        Collections.sort(chartItemArray, new Comparator() {

            @Override
            public int compare(Object o1, Object o2) {
                ChartItem c1 = (ChartItem) o1;
                ChartItem c2 = (ChartItem) o2;
                return ((String) c1.getResourceValue(SlotResolver.DATE_SLOT))
                        .compareToIgnoreCase((String) c2
                                .getResourceValue(SlotResolver.DATE_SLOT));
            }
        });
    }

}