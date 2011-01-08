/*******************************************************************************
 * Copyright 2009, 2010 Lars Grammel 
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0 
 *     
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.  
 *******************************************************************************/
package org.thechiselgroup.choosel.client.views.chart;

import org.thechiselgroup.choosel.client.views.DragEnablerFactory;
import org.thechiselgroup.choosel.client.views.text.TextViewContentDisplay;
import org.thechiselgroup.choosel.client.views.timeline.TimeLineViewContentDisplay;
import org.thechiselgroup.choosel.protovis.client.PVEventHandler;

import com.google.inject.Inject;

public class TimeChartViewContentDisplay extends ChartViewContentDisplay {

    @Inject
    public TimeChartViewContentDisplay(DragEnablerFactory dragEnablerFactory) {

        super(dragEnablerFactory);
    }

    // @formatter:off
    @Override
    public native void drawChart() /*-{
        var chart = this.@org.thechiselgroup.choosel.client.views.chart.ChartViewContentDisplay::getChart(),
        val = new Array();

        this.@org.thechiselgroup.choosel.client.views.chart.TimeChartViewContentDisplay::sortByDate()();

        for(var i = 0; i < this.@org.thechiselgroup.choosel.client.views.chart.ChartViewContentDisplay::chartItemsJsArray.@java.util.ArrayList::size()(); i++) {
            var xCoord = this.@org.thechiselgroup.choosel.client.views.chart.TimeChartViewContentDisplay::getSlotValue(II)(i,0);
            val[i] = {x: new $wnd.Date(xCoord),
                y: this.@org.thechiselgroup.choosel.client.views.chart.TimeChartViewContentDisplay::getSlotValue(II)(i,1)};
        }

        var start = val[0].x;
        var end = val[val.length - 1].x

        var w = this.@org.thechiselgroup.choosel.client.views.chart.ChartViewContentDisplay::width - 25,
            h1 = this.@org.thechiselgroup.choosel.client.views.chart.ChartViewContentDisplay::height - 60,
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

         dot.render();
    }-*/;
    // @formatter:on

    private Object getSlotValue(int i, int coordinate) {
        if (coordinate == 0) {
            return getChartItem(i).getViewItem().getResourceValue(
                    TimeLineViewContentDisplay.DATE_SLOT);
        } else if (coordinate == 1) {
            return getChartItem(i).getViewItem().getResourceValue(
                    TextViewContentDisplay.FONT_SIZE_SLOT);
        }
        throw new RuntimeException("No slot value available");
    }

    @Override
    protected void registerEventHandler(String eventType, PVEventHandler handler) {
    }

    private void sortByDate() {
        // XXX broken while switching to protovis-gwt
        // PV.sort(chartItemsJsArray, new ChartItemComparator(
        // TimeLineViewContentDisplay.DATE_SLOT));
    }
}