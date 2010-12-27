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

import org.thechiselgroup.choosel.protovis.client.functions.PVBooleanFunctionDoubleArg;
import org.thechiselgroup.choosel.protovis.client.functions.PVDoubleFunction;
import org.thechiselgroup.choosel.protovis.client.functions.PVFunction;
import org.thechiselgroup.choosel.protovis.client.functions.PVStringFunction;
import org.thechiselgroup.choosel.protovis.client.functions.PVStringFunctionDoubleArg;
import org.thechiselgroup.choosel.protovis.client.util.JsArrayGeneric;
import org.thechiselgroup.choosel.protovis.client.util.JsUtils;

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

    public static class Triple {

        public double x;

        public double y;

        public double z;

        private Triple(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

    }

    @Override
    public Widget asWidget() {
        return this;
    }

    private void createVisualization(JsArrayGeneric<Triple> data) {
        /* Sizing and scales. */
        int w = 400;
        int h = 400;
        final PVLinearScale x = PVScale.linear(0, 99).range(0, w);
        final PVLinearScale y = PVScale.linear(0, 1).range(0, h);
        final PVLogScale c = PVScale.log(1, 100).range("orange", "brown");

        /* The root panel. */
        PVPanel vis = getPVPanel().width(w).height(h).bottom(20).left(20)
                .right(10).top(5);

        /* Y-axis and ticks. */
        vis.add(PV.Rule()).data(y.ticks()).bottom(y)
                .strokeStyle(new PVStringFunctionDoubleArg<PVRule>() {
                    @Override
                    public String f(PVRule _this, double d) {
                        return d != 0 ? "#eee" : "#000";
                    }
                }).anchor(LEFT).add(PV.Label())
                .visible(new PVBooleanFunctionDoubleArg<PVLabel>() {
                    @Override
                    public boolean f(PVLabel _this, double d) {
                        return d > 0 && d < 1;
                    }
                }).text(y.tickFormat());

        /* X-axis and ticks. */
        vis.add(PV.Rule()).data(x.ticks()).left(x)
                .strokeStyle(new PVStringFunctionDoubleArg<PVRule>() {
                    @Override
                    public String f(PVRule _this, double d) {
                        return d != 0 ? "#eee" : "#000";
                    }
                }).anchor(BOTTOM).add(PV.Label())
                .visible(new PVBooleanFunctionDoubleArg<PVLabel>() {
                    @Override
                    public boolean f(PVLabel _this, double d) {
                        return d > 0 && d < 100;
                    }
                }).text(x.tickFormat());

        /* The dot plot! */
        vis.add(PV.Panel()).data(data).add(PV.Dot())
                .left(new PVDoubleFunction<PVDot, Triple>() {
                    @Override
                    public double f(PVDot _this, Triple d) {
                        return x.fd(d.x);
                    }
                }).bottom(new PVDoubleFunction<PVDot, Triple>() {
                    @Override
                    public double f(PVDot _this, Triple d) {
                        return y.fd(d.y);
                    }
                }).strokeStyle(new PVFunction<PVDot, Triple, PVColor>() {
                    @Override
                    public PVColor f(PVDot _this, Triple d) {
                        return c.fcolor(d.z);
                    }
                }).fillStyle(new PVFunction<PVDot, Triple, PVColor>() {
                    @Override
                    public PVColor f(PVDot _this, Triple d) {
                        return c.fcolor(d.z).alpha(0.2d);
                    }
                }).size(new PVDoubleFunction<PVDot, Triple>() {
                    @Override
                    public double f(PVDot _this, Triple d) {
                        return d.z;
                    }
                }).title(new PVStringFunction<PVDot, Triple>() {
                    @Override
                    public String f(PVDot _this, Triple d) {
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