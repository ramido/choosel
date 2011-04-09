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

import org.thechiselgroup.choosel.protovis.client.jsutil.JsArgs;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsDoubleFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsStringFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsUtils;

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

    private void createVisualization(double[] data) {
        /* Sizing and scales. */
        int w = 400;
        int h = 250;
        final PVLinearScale x = PV.Scale.linear(0, 1.1).range(0, w);
        final PVOrdinalScale y = PV.Scale.ordinal(PV.range(10)).splitBanded(0,
                h, 4d / 5d);

        /* The root panel. */
        PVPanel vis = getPVPanel().width(w).height(h).bottom(20).left(20)
                .right(10).top(5);

        /* The bars. */
        PVBar bar = vis.add(PV.Bar).dataDouble(data)
                .top(new JsDoubleFunction() {
                    public double f(JsArgs args) {
                        PVMark _this = args.getThis();
                        return y.fd(_this.index());
                    }
                }).height(new JsDoubleFunction() {
                    public double f(JsArgs args) {
                        return y.rangeBand();
                    }
                }).left(0).width(new JsDoubleFunction() {
                    public double f(JsArgs args) {
                        double d = args.getDouble();
                        return x.fd(d);
                    }
                });

        /* The value label. */
        bar.anchor(RIGHT).add(PV.Label).textStyle("white")
                .text(new JsStringFunction() {
                    public String f(JsArgs args) {
                        double d = args.getDouble();
                        return JsUtils.toFixed(d, 1);
                    }
                });

        /* The variable label. */
        bar.anchor(LEFT).add(PV.Label).textMargin(5).textAlign(RIGHT)
                .text(new JsStringFunction() {
                    public String f(JsArgs args) {
                        PVMark _this = args.getThis();
                        return "ABCDEFGHIJK".substring(_this.index(),
                                _this.index() + 1);
                    }
                });

        /* X-axis ticks. */
        vis.add(PV.Rule).data(x.ticks(5)).left(x)
                .strokeStyle(new JsStringFunction() {
                    public String f(JsArgs args) {
                        double d = args.getDouble();
                        return d != 0 ? "rgba(255,255,255,.3)" : "#000";
                    }
                }).add(PV.Rule).bottom(0).height(5).strokeStyle("#000")
                .anchor(BOTTOM).add(PV.Label).text(x.tickFormat());
    }

    private double[] generateData() {
        double[] data = new double[10];
        for (int i = 0; i < data.length; i++) {
            data[i] = Math.random() + .1;
        }
        return data;
    }

    @Override
    public String getDescription() {
        return null;
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