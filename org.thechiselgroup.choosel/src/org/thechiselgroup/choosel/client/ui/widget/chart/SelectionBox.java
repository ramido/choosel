package org.thechiselgroup.choosel.client.ui.widget.chart;

import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Label;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Rule;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Scale;

public class SelectionBox extends ChartWidget {

    public static native void drawBox() /*-{
        var chart = this.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::chart;

        chart.add($wnd.pv.Label).text("hello").left(30).top(30);
    }-*/;

    protected double minValue;
    protected double maxValue;
    protected double h;
    protected double w;

    @Override
    protected <T extends Mark> T drawChart() {
        return null;
    }

    protected void drawScales(Scale scale) {
        this.scale = scale;
        // TODO // should // take // double // with // labelText
        chart.add(Rule.createRule()).data(scale.ticks())
                .strokeStyle("lightGray").top(scale).bottom(4.5).anchor("left")
                .add(Label.createLabel()).text(labelText);
    }

}
