package org.thechiselgroup.choosel.client.ui.widget.chart;

import java.util.ArrayList;

import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Dot;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.ProtovisFunctionString;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Scale;
import org.thechiselgroup.choosel.client.util.ArrayUtils;
import org.thechiselgroup.choosel.client.views.chart.ChartItem;

import com.google.gwt.core.client.JavaScriptObject;

public class ScatterChart extends ChartWidget {

    private double minValueX;

    private double maxValueX;

    private double minValueY;

    private double maxValueY;

    private Scale scaleX;

    private Scale scaleY;

    private Dot dot;

    private ArrayList<Double> scatterDataX = null; // getDataArray(SlotResolver.X_COORDINATE_SLOT);

    private ArrayList<Double> scatterDataY = null; // getDataArray(SlotResolver.Y_COORDINATE_SLOT);

    // @formatter:off
    private native JavaScriptObject createCoordinateJsArray(
            JavaScriptObject xCoords, JavaScriptObject yCoords) /*-{
        var newArray = new Array();
        for(var i = 0; i < xCoords.length; i++) {
            newArray[i] = {x: xCoords[i], y: yCoords[i]};
        }
        return newArray;
    }-*/;
    // @formatter:on

    @SuppressWarnings("unchecked")
    @Override
    public Dot drawChart() {
        minValueX = ArrayUtils.min(scatterDataX);
        maxValueX = ArrayUtils.max(scatterDataX);
        minValueY = ArrayUtils.min(scatterDataY);
        maxValueY = ArrayUtils.max(scatterDataY);

        // w = width - 40;
        // h = height - 40;
        //
        // scaleX = Scale.linear(minValueX - 0.5, maxValueX + 0.5).range(0, w);
        // scaleY = Scale.linear(maxValueY + 0.5, minValueY - 0.5).range(0, h);

        drawScatter();

        return dot;

    }

    private void drawScatter() {
        dot = chart
                .add(Dot.createDot())
                .data(createCoordinateJsArray(getJsDataArray(scatterDataX),
                        getJsDataArray(scatterDataY)))
                .cursor("pointer")
                // .left(new ProtovisFunctionDouble() {
                // @Override
                // public double f(String value, int index) {
                // return scatterDataX.get(index)
                // / (maxValueX - minValueX + 1) * w;
                // }
                // })
                // .top(new ProtovisFunctionDouble() {
                // @Override
                // public double f(String value, int index) {
                // return scatterDataY.get(index)
                // / (maxValueY - minValueY + 1) * h;
                // }
                .radius(3).strokeStyle("rgba(0,0,0,0.35)")
                .fillStyle(new ProtovisFunctionString() {
                    @Override
                    public String f(ChartItem value, int index) {
                        return value.getColour();
                    }
                });
    }

}