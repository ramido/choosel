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
import org.thechiselgroup.choosel.protovis.client.functions.PVFunction;
import org.thechiselgroup.choosel.protovis.client.functions.PVStringFunction;
import org.thechiselgroup.choosel.protovis.client.functions.PVStringFunctionDoubleArg;
import org.thechiselgroup.choosel.protovis.client.util.JsArrayGeneric;
import org.thechiselgroup.choosel.protovis.client.util.JsUtils;

import com.google.gwt.user.client.ui.Widget;

/**
 * Protovis/GWT implementation of <a
 * href="http://vis.stanford.edu/protovis/ex/group.html">Protovis grouped chart
 * example</a>.
 * 
 * @author Lars Grammel
 */
public class GroupedChartExample extends ProtovisWidget implements
        ProtovisExample {

    @Override
    public Widget asWidget() {
        return this;
    }

    private void createVisualization(
            final JsArrayGeneric<JsArrayGeneric<Double>> data) {

        /* Sizing and scales. */
        final int m = 4;
        int w = 400;
        int h = 250;
        final PVLinearScale x = PVScale.linear(0, 1.1).range(0, w);
        final PVOrdinalScale y = PVScale.ordinal(PV.range(data.length()))
                .splitBanded(0, h, 4d / 5d);
        final PVOrdinalScale category20 = PVColors.category20();

        /* The root panel. */
        PVPanel vis = getPVPanel().width(w).height(h).bottom(20).left(20)
                .right(10).top(5);

        /* The bars. */
        PVBar bar = vis
                .add(PV.Panel())
                .data(data)
                .top(new PVDoubleFunction<PVPanel, JsArrayGeneric<Double>>() {
                    public double f(PVPanel _this, JsArrayGeneric<Double> d) {
                        return y.fd(_this.index());
                    }
                })
                .height(new PVDoubleFunction<PVPanel, JsArrayGeneric<Double>>() {
                    public double f(PVPanel _this, JsArrayGeneric<Double> d) {
                        return y.rangeBand();
                    }
                })
                .add(PV.Bar())
                .data(new PVFunction<PVBar, JsArrayGeneric<Double>, JsArrayGeneric<Double>>() {
                    public JsArrayGeneric<Double> f(PVBar _this,
                            JsArrayGeneric<Double> d) {
                        return d;
                    }
                }).top(new PVDoubleFunction<PVBar, Double>() {
                    public double f(PVBar _this, Double d) {
                        return _this.index() * y.rangeBand() / m;
                    }
                }).height(new PVDoubleFunction<PVBar, Double>() {
                    public double f(PVBar _this, Double d) {
                        return y.rangeBand() / m;
                    }
                }).left(0).width(new PVDoubleFunction<PVBar, Double>() {
                    public double f(PVBar _this, Double d) {
                        return x.fd(d);
                    }
                }).fillStyle(new PVFunction<PVBar, Double, PVColor>() {
                    public PVColor f(PVBar _this, Double d) {
                        return category20.fcolor(_this.index());
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
        bar.parent().anchor(LEFT).add(PV.Label()).textAlign(RIGHT)
                .textMargin(5)
                .text(new PVStringFunction<PVLabel, JsArrayGeneric<Double>>() {
                    public String f(PVLabel _this, JsArrayGeneric<Double> d) {
                        int i = _this.parent().index();
                        return "ABCDEFGHIJK".substring(i, i + 1);
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

    private JsArrayGeneric<JsArrayGeneric<Double>> generateData() {
        JsArrayGeneric<JsArrayGeneric<Double>> data = JsUtils
                .createJsArrayGeneric();
        for (int j = 0; j < 3; j++) {
            JsArrayGeneric<Double> series = JsUtils.createJsArrayGeneric();
            for (int i = 0; i < 4; i++) {
                series.push(Math.random() + .1);
            }
            data.push(series);
        }
        return data;
    }

    public String getProtovisExampleURL() {
        return "http://vis.stanford.edu/protovis/ex/group.html";
    }

    public String getSourceCodeFile() {
        return "GroupedChartExample.java";
    }

    protected void onAttach() {
        super.onAttach();
        initPVPanel();
        createVisualization(generateData());
        getPVPanel().render();
    }

    public String toString() {
        return "Grouped Bar Chart";
    }
}