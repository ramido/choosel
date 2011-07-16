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

import org.thechiselgroup.choosel.protovis.client.jsutil.JsArgs;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsArrayGeneric;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsBooleanFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsDoubleFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsStringFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsUtils;

import com.google.gwt.user.client.ui.Widget;

/**
 * Protovis/GWT implementation of <a
 * href="http://vis.stanford.edu/protovis/ex/dot.html">Protovis scatterplot
 * example</a>.
 * 
 * @author Lars Grammel
 */
public class ScatterplotExample extends ProtovisWidget implements
        ProtovisExample {

    @Override
    public Widget asWidget() {
        return this;
    }

    private void createVisualization(JsArrayGeneric<Triple> data) {
        /* Sizing and scales. */
        int w = 400;
        int h = 400;
        final PVLinearScale x = PV.Scale.linear(0, 99).range(0, w);
        final PVLinearScale y = PV.Scale.linear(0, 1).range(0, h);
        final PVLogScale c = PV.Scale.log(1, 100).range("orange", "brown");

        /* The root panel. */
        PVPanel vis = getPVPanel().width(w).height(h).bottom(20).left(20)
                .right(10).top(5);

        /* Y-axis and ticks. */
        vis.add(PV.Rule).data(y.ticks()).bottom(y)
                .strokeStyle(new JsStringFunction() {
                    public String f(JsArgs args) {
                        double d = args.getDouble(0);
                        return d != 0 ? "#eee" : "#000";
                    }
                }).anchor(LEFT).add(PV.Label).visible(new JsBooleanFunction() {
                    public boolean f(JsArgs args) {
                        double d = args.getDouble(0);
                        return d > 0 && d < 1;
                    }
                }).text(y.tickFormat());

        /* X-axis and ticks. */
        vis.add(PV.Rule).data(x.ticks()).left(x)
                .strokeStyle(new JsStringFunction() {
                    public String f(JsArgs args) {
                        double d = args.getDouble(0);
                        return d != 0 ? "#eee" : "#000";
                    }
                }).anchor(BOTTOM).add(PV.Label)
                .visible(new JsBooleanFunction() {
                    public boolean f(JsArgs args) {
                        double d = args.getDouble(0);
                        return d > 0 && d < 100;
                    }
                }).text(x.tickFormat());

        /* The dot plot! */
        vis.add(PV.Panel).data(data).add(PV.Dot).left(new JsDoubleFunction() {
            public double f(JsArgs args) {
                Triple d = args.getObject();
                return x.fd(d.x);
            }
        }).bottom(new JsDoubleFunction() {
            public double f(JsArgs args) {
                Triple d = args.getObject();
                return y.fd(d.y);
            }
        }).strokeStyle(new JsFunction<PVColor>() {
            public PVColor f(JsArgs args) {
                Triple d = args.getObject();
                return c.fcolor(d.z);
            }
        }).fillStyle(new JsFunction<PVColor>() {
            public PVColor f(JsArgs args) {
                Triple d = args.getObject();
                return c.fcolor(d.z).alpha(0.2d);
            }
        }).size(new JsDoubleFunction() {
            public double f(JsArgs args) {
                Triple d = args.getObject();
                return d.z;
            }
        }).title(new JsStringFunction() {
            public String f(JsArgs args) {
                Triple d = args.getObject();
                return JsUtils.toFixed(d.z, 1);
            }
        });
    }

    private JsArrayGeneric<Triple> generateData() {
        JsArrayGeneric<Triple> data = JsUtils.createJsArrayGeneric();
        for (int i = 0; i < 100; i++) {
            data.push(new Triple(i / 1d, Math.random(), Math.pow(10,
                    2 * Math.random())));
        }
        return data;
    }

    @Override
    public String getDescription() {
        return null;
    }

    public String getProtovisExampleURL() {
        return "http://vis.stanford.edu/protovis/ex/dot.html";
    }

    public String getSourceCodeFile() {
        return "ScatterplotExample.java";
    }

    protected void onAttach() {
        super.onAttach();
        initPVPanel();
        createVisualization(generateData());
        getPVPanel().render();
    }

    public String toString() {
        return "Scatter Plot";
    }

}