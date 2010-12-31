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

import org.thechiselgroup.choosel.protovis.client.jsutil.JsArgs;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsBooleanFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsDoubleFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsStringFunction;

import com.google.gwt.user.client.ui.Widget;

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

    @Override
    public Widget asWidget() {
        return this;
    }

    private void createVisualization(Point[] points) {
        int w = 400;
        int h = 200;

        final PVLinearScale x = PVScale.linear(points, new JsDoubleFunction() {
            public double f(JsArgs args) {
                Point d = args.getObject();
                return d.x;
            }
        }).range(0, w);
        final PVLinearScale y = PVScale.linear(0, 4).range(0, h);

        /* The root panel. */
        PVPanel vis = getPVPanel().width(w).height(h).bottom(20).left(20)
                .right(10).top(5);

        /* Y-axis and ticks. */
        vis.add(PV.Rule).data(y.ticks(5)).bottom(y)
                .strokeStyle(new JsStringFunction() {
                    public String f(JsArgs args) {
                        double d = args.getDouble();
                        return d != 0 ? "#eee" : "#000";
                    }
                }).anchor(LEFT).add(PV.Label).text(y.tickFormat());

        /* X-axis and ticks. */
        vis.add(PV.Rule).data(x.ticks()).visible(new JsBooleanFunction() {
            public boolean f(JsArgs args) {
                double d = args.getDouble();
                return d != 0;
            }
        }).left(x).bottom(-5).height(5).anchor(BOTTOM).add(PV.Label)
                .text(x.tickFormat());

        /* The area with top line. */
        vis.add(PV.Area).data(points).bottom(1).left(new JsDoubleFunction() {
            public double f(JsArgs args) {
                Point d = args.getObject();
                return x.fd(d.x);
            }
        }).height(new JsDoubleFunction() {
            public double f(JsArgs args) {
                Point d = args.getObject();
                return y.fd(d.y);
            }
        }).fillStyle("rgb(121,173,210)").anchor(TOP).add(PV.Line).lineWidth(3);
    }

    private Point[] generateData() {
        Point[] data = new Point[100];
        for (int i = 0; i < data.length; i++) {
            double xValue = i / 10d;
            data[i] = new Point(xValue, Math.sin(xValue) + Math.random() * .5
                    + 2);
        }
        return data;
    }

    public String getProtovisExampleURL() {
        return "http://vis.stanford.edu/protovis/ex/area.html";
    }

    public String getSourceCodeFile() {
        return "AreaChartExample.java";
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