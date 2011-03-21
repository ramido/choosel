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

import org.thechiselgroup.choosel.protovis.client.jsutil.JsArgs;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsArrayGeneric;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsDoubleFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsUtils;

import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.user.client.ui.Widget;

/**
 * Protovis/GWT implementation of <a
 * href="http://vis.stanford.edu/protovis/ex/stream.html">Protovis streamgraph
 * example</a>.
 * 
 * @author Lars Grammel
 */
public class StreamgraphExample extends ProtovisWidget implements
        ProtovisExample {

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

    private void createVisualization(JsArrayGeneric<JsArrayNumber> data, int n,
            int m) {

        int w = 800;
        int h = 500;
        final PVLinearScale x = PV.Scale.linear(0, m - 1).range(0, w);
        final PVLinearScale y = PV.Scale.linear(0, 2 * n).range(0, h);

        PVPanel vis = getPVPanel().width(w).height(h);

        JsArrayGeneric<JsArrayNumber> datax = JsUtils.createJsArrayGeneric();
        datax.push(JsUtils.toJsArrayNumber(1, 1.2, 1.7, 1.5, 1.7));
        datax.push(JsUtils.toJsArrayNumber(.5, 1, .8, 1.1, 1.3));
        datax.push(JsUtils.toJsArrayNumber(.2, .5, .8, .9, 1));

        vis.add(PV.Layout.Stack()).layers(data).order("inside-out")
                .offset("wiggle").x(new JsDoubleFunction() {
                    public double f(JsArgs args) {
                        PVMark _this = args.getThis();
                        return x.fd(_this.index());
                    }
                }).y(new JsDoubleFunction() {
                    public double f(JsArgs args) {
                        double d = args.getDouble(0);
                        return y.fd(d);
                    }
                }).layer().add(PV.Area).fillStyle(new JsFunction<PVColor>() {
                    public PVColor f(JsArgs args) {
                        return PV.ramp("#aad", "#556").fcolor(Math.random());
                    }
                }).strokeStyle(new JsFunction<PVColor>() {
                    public PVColor f(JsArgs args) {
                        PVArea _this = args.getThis();
                        return _this.fillStyle().alpha(.5);
                    }
                });
    }

    private void bump(JsArrayNumber a, int m) {
        double x = 1d / (.1 + Math.random());
        double y = 2d * Math.random() - .5;
        double z = 10d / (.1 + Math.random());

        for (int i = 0; i < m; i++) {
            double w = (((double) i) / m - y) * z;
            a.set(i, a.get(i) + x * Math.exp(-w * w));
        }
    }

    private JsArrayGeneric<JsArrayNumber> generateData(int n, int m) {
        JsArrayGeneric<JsArrayNumber> result = JsUtils.createJsArrayGeneric();
        for (int i = 0; i < n; i++) {
            JsArrayNumber a = JsUtils.createJsArrayNumber();
            for (int j = 0; j < m; j++) {
                a.set(j, 0);
            }
            for (int j = 0; j < 5; j++) {
                bump(a, m);
            }
            result.push(a);
        }
        return result;
    }

    public String getProtovisExampleURL() {
        return "http://vis.stanford.edu/protovis/ex/stream.html";
    }

    public String getSourceCodeFile() {
        return "StreamgraphExample.java";
    }

    protected void onAttach() {
        super.onAttach();
        initPVPanel();
        createVisualization(generateData(20, 400), 20, 400);
        getPVPanel().render();
    }

    public String toString() {
        return "Streamgraph";
    }

}