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
package org.thechiselgroup.choosel.protovisexamples.client;

import org.thechiselgroup.choosel.protovis.client.BooleanFunctionDoubleArg;
import org.thechiselgroup.choosel.protovis.client.DoubleFunction;
import org.thechiselgroup.choosel.protovis.client.JsGenericArray;
import org.thechiselgroup.choosel.protovis.client.PVAlignment;
import org.thechiselgroup.choosel.protovis.client.PVArea;
import org.thechiselgroup.choosel.protovis.client.PVLabel;
import org.thechiselgroup.choosel.protovis.client.PVLine;
import org.thechiselgroup.choosel.protovis.client.PVLinearScale;
import org.thechiselgroup.choosel.protovis.client.PVPanel;
import org.thechiselgroup.choosel.protovis.client.PVRule;
import org.thechiselgroup.choosel.protovis.client.PVScale;
import org.thechiselgroup.choosel.protovis.client.ProtovisWidget;
import org.thechiselgroup.choosel.protovis.client.StringFunctionDoubleArg;

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

    public String getProtovisExampleURL() {
        return "http://vis.stanford.edu/protovis/ex/area.html";
    }

    protected void onAttach() {
        super.onAttach();
        initChartPanel();

        JsGenericArray<Point> data = JsGenericArray.createGenericArray();
        for (int i = 0; i < 100; i++) {
            double xValue = i / 10d;
            data.push(new Point(xValue, Math.sin(xValue) + Math.random() * .5
                    + 2));
        }

        int w = 400;
        int h = 200;

        final PVLinearScale x = PVScale.linear()
                .domain(data, new DoubleFunction<Point>() {
                    @Override
                    public double f(Point d, int i) {
                        return d.x;
                    }
                }).range(0, w);
        final PVLinearScale y = PVScale.linear(0, 4).range(0, h);

        /* The root panel. */
        PVPanel vis = getChartPanel().width(w).height(h).bottom(20).left(20)
                .right(10).top(5);

        /* Y-axis and ticks. */
        vis.add(PVRule.createRule()).data(y.ticks(5)).bottom(y)
                .strokeStyle(new StringFunctionDoubleArg() {
                    @Override
                    public String f(double value, int i) {
                        return value != 0 ? "#eee" : "#000";
                    }
                }).anchor(PVAlignment.LEFT).add(PVLabel.createLabel())
                .text(y.tickFormat());

        /* X-axis and ticks. */
        vis.add(PVRule.createRule()).data(x.ticks())
                .visible(new BooleanFunctionDoubleArg() {
                    @Override
                    public boolean f(double value, int i) {
                        return true; // value != 0;
                    }
                }).left(x).bottom(-5).height(5).anchor(PVAlignment.BOTTOM)
                .add(PVLabel.createLabel()).text(x.tickFormat());

        /* The area with top line. */
        vis.add(PVArea.createArea()).data(data).bottom(1)
                .left(new DoubleFunction<Point>() {
                    @Override
                    public double f(Point d, int i) {
                        return x.f(d.x);
                    }
                }).height(new DoubleFunction<Point>() {
                    @Override
                    public double f(Point d, int i) {
                        return y.f(d.y);
                    }
                }).fillStyle("rgb(121,173,210)").anchor(PVAlignment.TOP)
                .add(PVLine.createLine()).lineWidth(3);

        vis.render();

    }

    public String toString() {
        return "Area Chart Example";
    }
}