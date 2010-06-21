package org.thechiselgroup.choosel.client.ui.widget.chart;

public class DotChart extends ChartWidget {

    @Override
    public native Chart drawChart(int width, int height) /*-{
        var chart = this.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::chart,
        val = this.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::val,
        thisChart = this,
        s,
        isBrushed = new Array(),
        selectBoxAlpha = .42;
        
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
//	    .event("mouseout", fadeOut)
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
            .event("mousedown", function(d) {
                var doReturn;
                for(var i = 0; i < val.length; i++) {
                    if(isInSelectBox(d,i) && isBrushed[i]) {
                	updateMinus(d,i);
                	doReturn = true;
                    }
                }
                if(doReturn == true) {
                    removeBoxes(d);
                    return (s = null, chart);
                }})
            .event("mouseover", function(d) {
                for(var i = 0; i < val.length; i++) {
                    if(isInSelectBox(d,i) && isBrushed[i]) {
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
            .event("mousedown", function(d) {
                var doReturn;
                for(var i = 0; i < val.length; i++) {
                    if(isInSelectBox(d,i) && !isBrushed[i]) {
                	updatePlus(d,i);
                	doReturn = true;
                    }
                }
                if(doReturn == true) {
                    removeBoxes(d);
                    return (s = null, chart);
                }})
            .event("mouseover", function(d) {
                for(var i = 0; i < val.length; i++) {
                    if(isInSelectBox(d,i) && !isBrushed[i]) {
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
            .strokeStyle("rgba(0,0,0,.35)")
            .fillStyle(function(d) {return (s && ((this.index * width / val.length < s.x1) || (this.index * width / val.length > s.x2)
                || (height - (d * height / 10) < s.y1) || (height - (d * height / 10) > s.y2)) || !s
                ? thisChart.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::getChartItem(I)(this.index)
               	    .@org.thechiselgroup.choosel.client.views.chart.ChartItem::getColour()() : "rgba(256,256,0,.6)");});

        function addBoxes(d) {
            for(var i = 0; i < val.length; i++) {
                if(isInSelectBox(d,i)) {
                    if(!isBrushed[i]) {
                        plus.textStyle("black");
                    }
                    if(isBrushed[i]) {
                        minus.textStyle("black");
                    }
                    plusBox.visible(true);
            	    plus.visible(true);
            	    minusBox.visible(true);
           	    minus.visible(true);
            	    update(d);
                }
            }
        }
            
        function fadeOut() {
            fade = setInterval(function() {
            	selectBoxAlpha = selectBoxAlpha - .01;
            	if(selectBoxAlpha <= 0.005) {
            	    clearInterval(fade);
            	    fade = null;
                    return (s = null, chart, selectBoxAlpha = .42);
            	}
            	selectBox.fillStyle("rgba(193,217,241,"+selectBoxAlpha+")");
            	selectBox.strokeStyle("rgba(0,0,0,"+selectBoxAlpha+")");
            	selectBox.render();
            }, 100); 
        }

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
            this.render();
        }

        function update(d) {
            s = d;
            s.x1 = d.x;
            s.x2 = d.x + d.dx;
            s.y1 = d.y;
            s.y2 = d.y + d.dy;
            dot.context(null, 0, function() {return this.render();});
        }

        function updateMinus(d,i) {
            update(d);
            thisChart.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::onBrushEvent(IZ)(i,false);
            isBrushed[i] = false;
        }

        function updatePlus(d,i) {
            update(d);
    	    thisChart.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::onBrushEvent(IZ)(i,true);
       	    isBrushed[i] = true;
        }

        return dot;
    }-*/;

}