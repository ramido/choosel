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
import static org.thechiselgroup.choosel.protovis.client.PVAlignment.RIGHT;

import org.thechiselgroup.choosel.protovis.client.functions.PVDoubleFunction;
import org.thechiselgroup.choosel.protovis.client.functions.PVStringFunction;
import org.thechiselgroup.choosel.protovis.client.functions.PVStringFunctionDoubleArg;
import org.thechiselgroup.choosel.protovis.client.util.JsArrayGeneric;
import org.thechiselgroup.choosel.protovis.client.util.JsUtils;

import com.google.gwt.user.client.ui.Widget;

/**
 * Protovis/GWT implementation of <a
 * href="http://vis.stanford.edu/protovis/ex/bar.html">Protovis bar chart
 * example</a>.
 * 
 * @author Lars Grammel
 */
public class BarChartExample extends ProtovisWidget implements ProtovisExample {

    @Override
    public Widget asWidget() {
        return this;
    }

    private void createVisualization(JsArrayGeneric<Double> data) {
        /* Sizing and scales. */
        int w = 400;
        int h = 250;
        final PVLinearScale x = PVScale.linear(0, 1.1).range(0, w);
        final PVOrdinalScale y = PVScale.ordinal(PV.range(10)).splitBanded(0,
                h, 4d / 5d);

        /* The root panel. */
        PVPanel vis = getPVPanel().width(w).height(h).bottom(20).left(20)
                .right(10).top(5);

        /* The bars. */
        PVBar bar = vis.add(PV.Bar()).data(data)
                .top(new PVDoubleFunction<PVBar, Double>() {
                    public double f(PVBar _this, Double d) {
                        return y.fd(_this.index());
                    }
                }).height(new PVDoubleFunction<PVBar, Double>() {
                    public double f(PVBar _this, Double d) {
                        return y.rangeBand();
                    }
                }).left(0).width(new PVDoubleFunction<PVBar, Double>() {
                    public double f(PVBar _this, Double d) {
                        return x.fd(d);
                    }
                });

        /* The value label. */
        bar.anchor(RIGHT).add(PV.Label()).textStyle("white")
                .text(new PVStringFunction<PVLabel, Double>() {
                    public String f(PVLabel _this, Double d) {
                        return JsUtils.toFixed(d, 1);
                    }
                });

        /* The variable label. */
        bar.anchor(LEFT).add(PV.Label()).textMargin(5).textAlign(RIGHT)
                .text(new PVStringFunction<PVLabel, Double>() {
                    public String f(PVLabel _this, Double d) {
                        return "ABCDEFGHIJK".substring(_this.index(),
                                _this.index() + 1);
                    }
                });

        /* X-axis ticks. */
        vis.add(PV.Rule()).data(x.ticks(5)).left(x)
                .strokeStyle(new PVStringFunctionDoubleArg<PVRule>() {
                    public String f(PVRule _this, double d) {
                        return d != 0 ? "rgba(255,255,255,.3)" : "#000";
                    }
                }).add(PV.Rule()).bottom(0).height(5).strokeStyle("#000")
                .anchor(BOTTOM).add(PV.Label()).text(x.tickFormat());
    }

    private JsArrayGeneric<Double> generateData() {
        JsArrayGeneric<Double> data = JsUtils.createJsArrayGeneric();
        for (int i = 0; i < 10; i++) {
            data.push(Math.random() + .1);
        }
        return data;
    }

    public String getProtovisExampleURL() {
        return "http://vis.stanford.edu/protovis/ex/bar.html";
    }

    public String getSourceCodeFile() {
        return "BarChartExample.java";
    }

    protected void onAttach() {
        super.onAttach();
        initPVPanel();
        createVisualization(generateData());
        getPVPanel().render();
    }

    public String toString() {
        return "Bar Chart";
    }
}