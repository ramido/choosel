package org.thechiselgroup.choosel.client.ui.widget.chart;

import org.thechiselgroup.choosel.client.ui.Colors;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Alignments;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Dot;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Label;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.ProtovisEventHandler;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.ProtovisFunctionDouble;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.ProtovisFunctionDoubleWithCache;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.ProtovisFunctionString;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.ProtovisFunctionStringToString;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Rule;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Scale;
import org.thechiselgroup.choosel.client.util.ArrayUtils;
import org.thechiselgroup.choosel.client.views.SlotResolver;
import org.thechiselgroup.choosel.client.views.chart.ChartItem;

public class DotChart extends ChartWidget {

    private static final int BORDER_HEIGHT = 20;

    private static final int BORDER_WIDTH = 20;

    private static final String GRIDLINE_SCALE_COLOR = Colors.GRAY_1;

    private static final String AXIS_SCALE_COLOR = Colors.GRAY_2;

    private double[] dotCounts;

    protected double chartHeight;

    protected double chartWidth;

    private ProtovisFunctionDouble dotLeft = new ProtovisFunctionDouble() {

        @Override
        public double f(ChartItem value, int index) {
            return index * chartWidth / chartItems.size() + chartWidth
                    / (chartItems.size() * 2);
        }
    };

    private ProtovisFunctionDoubleWithCache dotBottom = new ProtovisFunctionDoubleWithCache() {

        @Override
        public void beforeRender() {
            if (chartItems.isEmpty()) {
                return;
            }

            dotCounts = new double[chartItems.size()];

            for (int i = 0; i < chartItems.size(); i++) {
                dotCounts[i] = calculateAllResources(i);
            }

        }

        @Override
        public double f(ChartItem value, int i) {
            return dotCounts[i] * chartHeight / getMaximumChartItemValue();
        }

    };

    private int baselineLabelBottom = -15;

    private ProtovisFunctionStringToString scaleStrokeStyle = new ProtovisFunctionStringToString() {
        @Override
        public String f(String value, int i) {
            return Double.parseDouble(value) == 0 ? AXIS_SCALE_COLOR
                    : GRIDLINE_SCALE_COLOR;
        }
    };

    private Dot regularDot;

    private ProtovisFunctionDouble baselineLabelLeft = new ProtovisFunctionDouble() {
        @Override
        public double f(ChartItem value, int i) {
            return dotLeft.f(value, i);
        }
    };

    private String baselineLabelTextAlign = Alignments.CENTER;

    private ProtovisFunctionString baselineLabelText = new ProtovisFunctionString() {
        @Override
        public String f(ChartItem value, int i) {
            return value.getResourceItem()
                    .getResourceValue(SlotResolver.CHART_LABEL_SLOT).toString();
        }
    };

    @Override
    protected void beforeRender() {
        super.beforeRender();
        dotBottom.beforeRender();
    }

    private void calculateChartVariables() {
        chartWidth = width - BORDER_WIDTH * 2;
        chartHeight = height - BORDER_HEIGHT * 2;
    }

    private void dehighlightResources(int i) {
        chartItems
                .get(i)
                .getResourceItem()
                .removeHighlightedResources(
                        chartItems.get(i).getResourceItem().getResourceSet());
    }

    private void deselectResources(int i) {
        chartItems
                .get(i)
                .getResourceItem()
                .removeSelectedResources(
                        chartItems.get(i).getResourceItem().getResourceSet());
    }

    @Override
    public void drawChart() {
        assert chartItems.size() >= 1;

        calculateChartVariables();
        setChartParameters();

        Scale scale = Scale.linear(0, getMaximumChartItemValue()).range(0,
                chartHeight);
        drawScales(scale);
        drawSelectionBox();
        drawDot();

    }

    private void drawDot() {
        regularDot = chart
                .add(Dot.createDot())
                .data(ArrayUtils.toJsArray(chartItems))
                .bottom(dotBottom)
                .left(dotLeft)
                .size(Math.min(chartHeight, chartWidth)
                        / (chartItems.size() * 2)).fillStyle(chartFillStyle)
                .strokeStyle(Colors.STEELBLUE);

        regularDot.add(Label.createLabel()).left(baselineLabelLeft)
                .textAlign(baselineLabelTextAlign).bottom(baselineLabelBottom)
                .text(baselineLabelText);

    }

    protected void drawScales(Scale scale) {
        this.scale = scale;
        chart.add(Rule.createRule()).data(scale.ticks()).bottom(scale)
                .strokeStyle(scaleStrokeStyle).width(chartWidth)
                .anchor(Alignments.LEFT).add(Label.createLabel())
                .text(scaleLabelText);

        chart.add(Rule.createRule()).left(0).bottom(0)
                .strokeStyle(AXIS_SCALE_COLOR).height(chartHeight);
    }

    // @formatter:off
    public native void drawSelectionBox() /*-{
        var chart = this.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::chart,
        eventToggle = true,
        fade,
        s, 
        selectBoxAlpha = .42,
        selectBoxData = [{x:0, y:0, dx:0, dy:0}],
        thisChart = this;

        var selectBox = chart.add($wnd.pv.Panel)
            .data(selectBoxData)
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
                for(var i = 0; i < thisChart.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::chartItems.@java.util.ArrayList::size()(); i++) {
                    if(isInSelectionBox(d,i)) {
                        updateMinusPlus(d,i);
                        thisChart.@org.thechiselgroup.choosel.client.ui.widget.chart.DotChart::deselectResources(I)(i);
                        doReturn = true;
                    }
                }
                if(doReturn == true) {
                    removeBoxes(d);
                    return (s = null, chart);
                }
            })
            .event("mouseover", function(d) {
                for(var i = 0; i < thisChart.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::chartItems.@java.util.ArrayList::size()(); i++) {
                    if(isInSelectionBox(d,i)) {
                        return this.fillStyle("FFFFE1");
                    }
                }
            })
            .event("mouseout", function() {return this.fillStyle("rgba(193,217,241,"+selectBoxAlpha+")");});

        var minus = minusBox.anchor("center").add($wnd.pv.Label)
            .visible(false)
            .text("-")
            .textStyle("black")
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
                for(var i = 0; i < thisChart.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::chartItems.@java.util.ArrayList::size()(); i++) {
                    if(isInSelectionBox(d,i)) {
                        updateMinusPlus(d,i);
                        thisChart.@org.thechiselgroup.choosel.client.ui.widget.chart.DotChart::selectResources(I)(i);
                        doReturn = true;
                    }
                }
                if(doReturn == true) {
                    removeBoxes(d);
                    return (s = null, chart);
                }
            })
            .event("mouseover", function(d) {
                for(var i = 0; i < thisChart.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::chartItems.@java.util.ArrayList::size()(); i++) {
                    if(isInSelectionBox(d,i)) {
                        return this.fillStyle("FFFFE1");
                    }
                }
            })
            .event("mouseout", function() {return this.fillStyle("rgba(193,217,241,"+selectBoxAlpha+")");});

        var plus = plusBox.anchor("center").add($wnd.pv.Label)
            .visible(false)
            .text("+")
            .textStyle("black")
            .font("bold");

        function addBoxes(d) {
            for(var i = 0; i < thisChart.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::chartItems.@java.util.ArrayList::size()(); i++) {
                if(isInSelectionBox(d,i)) {
                    plusBox.visible(true);
                    plus.visible(true);
                    minusBox.visible(true);
                    minus.visible(true);
                    eventToggle = false;
                    update(d);
                    this.render();
                }
            }
        }

        function isInSelectionBox(d,i) {
            return thisChart.@org.thechiselgroup.choosel.client.ui.widget.chart.DotChart::isInSelectionBox(IIIII)(d.x, d.y, d.dx, d.dy, i);
        }

        function removeBoxes(d) {
            plusBox.visible(false);
            plus.visible(false);
            plus.textStyle("black");
            minusBox.visible(false);
            minus.visible(false);
            minus.textStyle("black");

            d.dx = 0;
            d.dy = 0;
            eventToggle = true;

            update(d);
        }

        function update(d) {
            s = d;
            s.x1 = d.x;
            s.x2 = d.x + d.dx;
            s.y1 = d.y;
            s.y2 = d.y + d.dy;

            for(var i = 0; i < thisChart.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::chartItems.@java.util.ArrayList::size()(); i++) {
                if(isInSelectionBox(d,i)) {
                    thisChart.@org.thechiselgroup.choosel.client.ui.widget.chart.DotChart::highlightResources(I)(i);
                } else {
                    thisChart.@org.thechiselgroup.choosel.client.ui.widget.chart.DotChart::dehighlightResources(I)(i);
                }
            }

            chart.context(null, 0, function() {return this.render();});
        }

        function updateMinusPlus(d,i) {
            update(d);
            thisChart.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::onEvent(Lcom/google/gwt/user/client/Event;I)($wnd.pv.event,i);
        }
    }-*/;
    // @formatter:on

    private void highlightResources(int i) {
        chartItems
                .get(i)
                .getResourceItem()
                .addHighlightedResources(
                        chartItems.get(i).getResourceItem().getResourceSet());
    }

    private boolean isInSelectionBox(int x, int y, int dx, int dy, int i) {
        return dotLeft.f(chartItems.get(i), i) >= x
                && dotLeft.f(chartItems.get(i), i) <= x + dx
                && chartHeight - dotBottom.f(chartItems.get(i), i) + 20 >= y
                && chartHeight - dotBottom.f(chartItems.get(i), i) + 20 <= y
                        + dy;
    }

    private boolean isSelected(int i) {
        return chartItems.get(i).getResourceItem().getSelectedResources()
                .contains(chartItems.get(i));
    }

    @Override
    protected void registerEventHandler(String eventType,
            ProtovisEventHandler handler) {
        regularDot.event(eventType, handler);
    }

    private void selectResources(int i) {
        chartItems
                .get(i)
                .getResourceItem()
                .addSelectedResources(
                        chartItems.get(i).getResourceItem().getResourceSet());
    }

    private void setChartParameters() {
        chart.left(BORDER_WIDTH).bottom(BORDER_HEIGHT);
    }
}