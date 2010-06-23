package org.thechiselgroup.choosel.client.ui.widget.chart;

public class DotChart extends ChartWidget {

    // @formatter:off
    @Override
    public native Chart drawChart(int width, int height) /*-{
        var chart = this.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::chart,
        val = this.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::val,
        thisChart = this, fade, s, selectBoxAlpha = .42;

        var selectBox = chart.add($wnd.pv.Panel)
            .data([{x:0, y:0, dx:0, dy:0}])
            .events("all")
            .event("mousedown", $wnd.pv.Behavior.select())
            .event("selectstart", removeBoxes)
            .event("select", update)
            .event("selectend", addBoxes)
          .add($wnd.pv.Bar)
            .visible(function() {return s;})
            .left(function(d) {return d.x;})
            .top(function(d) {return d.y;})
            .width(function(d) {return d.dx;})
            .height(function(d) {return d.dy;})
        //            .event("mouseout", fadeOut)
            .fillStyle("rgba(193,217,241,"+selectBoxAlpha+")")
            .strokeStyle("rgba(0,0,0,"+selectBoxAlpha+")")
            .lineWidth(.5);

        var minusBox = selectBox.add($wnd.pv.Bar)
            .left(function(d) {return d.x + d.dx - 15;})
            .top(function(d) {return d.y + d.dy - 15;})
            .width(15)
            .height(15)
            .strokeStyle("rgba(0,0,0,"+selectBoxAlpha+")")
            .lineWidth(.5)
            .events("all")
            .event("mousedown", function(d) {
                var doReturn;
                for(var i = 0; i < thisChart.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::chartItemArray.@java.util.ArrayList::size()(); i++) {
                    if(isInSelectBox(d,i) && thisChart.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::getChartItem(I)(i)
                            .@org.thechiselgroup.choosel.client.views.chart.ChartItem::isSelected()()) {
                	updateMinusPlus(d,i);
                	doReturn = true;
                    }
                }
                if(doReturn == true) {
                    removeBoxes(d);
                    return (s = null, chart);
                }})
            .event("mouseover", function(d) {
                for(var i = 0; i < thisChart.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::chartItemArray.@java.util.ArrayList::size()(); i++) {
                    if(isInSelectBox(d,i) && thisChart.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::getChartItem(I)(i)
                            .@org.thechiselgroup.choosel.client.views.chart.ChartItem::isSelected()()) {
                        return this.fillStyle("FFFFE1");
                    }
                }})
            .event("mouseout", function() {return this.fillStyle("rgba(193,217,241,"+selectBoxAlpha+")");});

        var minus = minusBox.anchor("center").add($wnd.pv.Label)
            .visible(false)
            .text("–")
            .textStyle("rgba(0,0,0,.25)")
            .font("bold");

        var plusBox = selectBox.add($wnd.pv.Bar)
            .left(function(d) {return d.x + d.dx - 30;})
            .top(function(d) {return d.y + d.dy - 15;})
            .width(15)
            .height(15)
            .strokeStyle("rgba(0,0,0,.35)")
            .lineWidth(.5)
            .events("all")
            .event("mousedown", function(d) {
                var doReturn;
                for(var i = 0; i < thisChart.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::chartItemArray.@java.util.ArrayList::size()(); i++) {
                    if(isInSelectBox(d,i) && !thisChart.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::getChartItem(I)(i)
                            .@org.thechiselgroup.choosel.client.views.chart.ChartItem::isSelected()()) {
                	updateMinusPlus(d,i);
                	doReturn = true;
                    }
                }
                if(doReturn == true) {
                    removeBoxes(d);
                    return (s = null, chart);
                }})
            .event("mouseover", function(d) {
                for(var i = 0; i < thisChart.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::chartItemArray.@java.util.ArrayList::size()(); i++) {
                    if(isInSelectBox(d,i) && !thisChart.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::getChartItem(I)(i)
                            .@org.thechiselgroup.choosel.client.views.chart.ChartItem::isSelected()()) {
                        return this.fillStyle("FFFFE1");
                    }
                }})
            .event("mouseout", function() {return this.fillStyle("rgba(193,217,241,"+selectBoxAlpha+")");});

        var plus = plusBox.anchor("center").add($wnd.pv.Label)
            .visible(false)
            .text("+")
            .textStyle("rgba(0,0,0,.25)")
            .font("bold");

        var dot = chart.add($wnd.pv.Dot)
            .data(val)
            .cursor("pointer")
            .bottom(function(d) {return d * height / 10;})
            .left(function() {return this.index * width / val.length;})
            .strokeStyle("rgba(0,0,0,.35)");

        function addBoxes(d) {
            for(var i = 0; i < val.length; i++) {
                if(isInSelectBox(d,i)) {
                    if(!thisChart.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::getChartItem(I)(i)
                            .@org.thechiselgroup.choosel.client.views.chart.ChartItem::isSelected()()) {
                        plus.textStyle("black");
                    }
                    if(thisChart.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::getChartItem(I)(i)
                            .@org.thechiselgroup.choosel.client.views.chart.ChartItem::isSelected()()) {
                        minus.textStyle("black");
                    }
                    plusBox.visible(true);
            	    plus.visible(true);
            	    minusBox.visible(true);
           	    minus.visible(true);
            	    update(d);
            	    this.render();
                }
            }
        }

        //        function fadeOut() {
        //            fade = setInterval(function() {
        //            	selectBoxAlpha = selectBoxAlpha - .01;
        //            	if(selectBoxAlpha <= 0.005) {
        //                    return (s = null, chart, selectBoxAlpha = .42);
        //            	}
        //            	selectBox.fillStyle("rgba(193,217,241,"+selectBoxAlpha+")");
        //            	selectBox.strokeStyle("rgba(0,0,0,"+selectBoxAlpha+")");
        //            	selectBox.render();
        //            }, 100); 
        //        }

        function isInSelectBox(d,i) {
            return (i * width / val.length >= d.x) && (i * width / val.length <= d.x + d.dx)
                    && (height - (val[i] * height / 10) >= d.y) && (height - (val[i] * height / 10) <= d.y + d.dy);
        }

        function removeBoxes(d) {
            plusBox.visible(false);
            plus.visible(false);
            plus.textStyle("rgba(0,0,0,.25)");
            minusBox.visible(false);
            minus.visible(false);
            minus.textStyle("rgba(0,0,0,.25)");

            update(d);
        }

        function update(d) {
            s = d;
            s.x1 = d.x;
            s.x2 = d.x + d.dx;
            s.y1 = d.y;
            s.y2 = d.y + d.dy;

            for(var i = 0; i < thisChart.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::chartItemArray.@java.util.ArrayList::size()(); i++) {
                if(isInSelectBox(d,i)) {
                    thisChart.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::getChartItem(I)(i)
                        .@org.thechiselgroup.choosel.client.views.chart.ChartItem::setHighlighted(Z)(true);
                } else {
                    thisChart.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::getChartItem(I)(i)
                        .@org.thechiselgroup.choosel.client.views.chart.ChartItem::setHighlighted(Z)(false);
                }
            }

            dot.context(null, 0, function() {return this.render();});
        }

        function updateMinusPlus(d,i) {
            update(d);
            thisChart.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::onEvent(I)(i);
        }

        return dot;
    }-*/;
    // @formatter:on        

}