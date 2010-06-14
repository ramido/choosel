package org.thechiselgroup.choosel.client.ui.widget.chart;

public class DotChart extends ChartWidget {

    @Override
    public native Chart drawChart(int width, int height) /*-{
        var chart = this.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::chart,
        val = this.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::val,
        thisChart = this, s, isBrushed = new Array();

        var selectBox = chart.add($wnd.pv.Panel)
            .data([{x:20, y:20, dx:100, dy:100}])
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
            .fillStyle("rgba(256,256,0,.3)")
            .strokeStyle("rgba(256,256,0,.6)");

        var dot = chart.add($wnd.pv.Dot)
            .data(val)
            .bottom(function(d) {return d * height / 10;})
            .left(function() {return this.index * width / val.length;})
            .strokeStyle(null)
            .fillStyle(function(d) {return (s && ((this.index * width / val.length < s.x1) || (this.index * width / val.length > s.x2)
                || (height - (d * height / 10) < s.y1) || (height - (d * height / 10) > s.y2)) || !s
                ? "steelblue" : "rgba(256,256,0,.6)");});

        var plusBox = selectBox.add($wnd.pv.Bar)
            .visible(false)
            .left(function(d) {return d.x + d.dx - 30;})
            .top(function(d) {return d.y + d.dy - 15;})
            .width(15)
            .height(15)
            .fillStyle("rgba(256,256,0,.3)")
            .strokeStyle("rgba(256,256,0,.6)")
            .event("mousedown", updatePlus);

        var plus = plusBox.anchor("center").add($wnd.pv.Label)
            .visible(false)
            .text("+")
            .font("bold");

        var minusBox = selectBox.add($wnd.pv.Bar)
            .visible(false)
            .left(function(d) {return d.x + d.dx - 15;})
            .top(function(d) {return d.y + d.dy - 15;})
            .width(15)
            .height(15)
            .fillStyle("rgba(256,256,0,.3)")
            .strokeStyle("rgba(256,256,0,.6)")
            .event("mousedown", updateMinus);

        var minus = minusBox.anchor("center").add($wnd.pv.Label)
            .visible(false)
            .text("–")
            .font("bold");

        function addBoxes(d) {
            plusBox.visible(true);
            plus.visible(true);
            minusBox.visible(true);
            minus.visible(true);
            this.render();
        }

        function removeBoxes(d) {
            plusBox.visible(false);
            plus.visible(false);
            minusBox.visible(false);
            minus.visible(false);
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

        function updatePlus(d) {
            s = d;
            s.x1 = d.x;
            s.x2 = d.x + d.dx;
            s.y1 = d.y;
            s.y2 = d.y + d.dy;
            dot.context(null, 0, function() {return this.render();});

            var i;
            for(i = 0; i < val.length; i++) {
                if((i * width / val.length >= s.x1) && (i * width / val.length <= s.x2)
                && (height - (val[i] * height / 10) >= s.y1) && (height - (val[i] * height / 10) <= s.y2)
                && isBrushed[i] != true) {
            	    thisChart.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::onBrushEvent(I)(i);
            	    isBrushed[i] = true;
                }
            }
        }

        function updateMinus(d) {
            s = d;
            s.x1 = d.x;
            s.x2 = d.x + d.dx;
            s.y1 = d.y;
            s.y2 = d.y + d.dy;
            dot.context(null, 0, function() {return this.render();});

            var i;
            for(i = 0; i < val.length; i++) {
                if((i * width / val.length >= s.x1) && (i * width / val.length <= s.x2)
                && (height - (val[i] * height / 10) >= s.y1) && (height - (val[i] * height / 10) <= s.y2)
                && isBrushed[i] == true) {
            	    thisChart.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::onBrushEvent(I)(i);
            	    isBrushed[i] = false;
                }
            }
        }

        return dot;
    }-*/;


}