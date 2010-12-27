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
package org.thechiselgroup.choosel.protovis.client;

import static org.thechiselgroup.choosel.protovis.client.PVAlignment.BOTTOM;
import static org.thechiselgroup.choosel.protovis.client.PVAlignment.LEFT;
import static org.thechiselgroup.choosel.protovis.client.PVAlignment.TOP;

import org.thechiselgroup.choosel.protovis.client.functions.PVBooleanFunctionDoubleArg;
import org.thechiselgroup.choosel.protovis.client.functions.PVDoubleFunction;
import org.thechiselgroup.choosel.protovis.client.functions.PVDoubleFunctionNoIndex;
import org.thechiselgroup.choosel.protovis.client.functions.PVStringFunctionDoubleArg;
import org.thechiselgroup.choosel.protovis.client.util.JsArrayGeneric;
import org.thechiselgroup.choosel.protovis.client.util.JsUtils;

/**
 * Protovis/GWT implementation of <a
 * href="http://vis.stanford.edu/protovis/ex/area.html">Protovis area chart
 * example</a>.
 * 
 * @author Lars Grammel
 */
public class AreaChartExample extends ProtovisWidget implements ProtovisExample {

    public static class Point {

        public double x;

        public double y;

        private Point(double x, double y) {
            this.x = x;
            this.y = y;
        }

    }

    private void createVisualization(JsArrayGeneric<Point> data) {
        int w = 400;
        int h = 200;

        final PVLinearScale x = PVScale.linear()
                .domain(data, new PVDoubleFunctionNoIndex<Point>() {
                    @Override
                    public double f(Point d) {
                        return d.x;
                    }
                }).range(0, w);
        final PVLinearScale y = PVScale.linear(0, 4).range(0, h);

        /* The root panel. */
        PVPanel vis = getPVPanel().width(w).height(h).bottom(20).left(20)
                .right(10).top(5);

        /* Y-axis and ticks. */
        vis.add(PV.Rule()).data(y.ticks(5)).bottom(y)
                .strokeStyle(new PVStringFunctionDoubleArg<PVRule>() {
                    @Override
                    public String f(PVRule _this, double d) {
                        return d != 0 ? "#eee" : "#000";
                    }
                }).anchor(LEFT).add(PV.Label()).text(y.tickFormat());

        /* X-axis and ticks. */
        vis.add(PV.Rule()).data(x.ticks())
                .visible(new PVBooleanFunctionDoubleArg<PVRule>() {
                    @Override
                    public boolean f(PVRule _this, double d) {
                        return d != 0;
                    }
                }).left(x).bottom(-5).height(5).anchor(BOTTOM).add(PV.Label())
                .text(x.tickFormat());

        /* The area with top line. */
        vis.add(PV.Area()).data(data).bottom(1)
                .left(new PVDoubleFunction<PVArea, Point>() {
                    @Override
                    public double f(PVArea _this, Point d) {
                        return x.fd(d.x);
                    }
                }).height(new PVDoubleFunction<PVArea, Point>() {
                    @Override
                    public double f(PVArea _this, Point d) {
                        return y.fd(d.y);
                    }
                }).fillStyle("rgb(121,173,210)").anchor(TOP).add(PV.Line())
                .lineWidth(3);
    }

    private JsArrayGeneric<Point> generateData() {
        JsArrayGeneric<Point> data = JsUtils.createJsArrayGeneric();
        for (int i = 0; i < 100; i++) {
            double xValue = i / 10d;
            data.push(new Point(xValue, Math.sin(xValue) + Math.random() * .5
                    + 2));
        }
        return data;
    }

    public String getProtovisExampleURL() {
        return "http://vis.stanford.edu/protovis/ex/area.html";
    }

    protected void onAttach() {
        super.onAttach();
        initPVPanel();
        createVisualization(generateData());
        getPVPanel().render();
    }

    public String toString() {
        return "Area Chart";
    }
}