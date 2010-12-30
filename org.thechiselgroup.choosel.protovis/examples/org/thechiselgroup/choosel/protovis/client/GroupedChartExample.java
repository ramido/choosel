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
import org.thechiselgroup.choosel.protovis.client.util.JsArrayGeneric;
import org.thechiselgroup.choosel.protovis.client.util.JsUtils;

import com.google.gwt.core.client.JsArrayNumber;
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

    private void createVisualization(final JsArrayGeneric<JsArrayNumber> data) {
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
        PVBar bar = vis.add(PV.Panel()).data(data)
                .top(new PVDoubleFunction<PVPanel>() {
                    public double f(PVPanel _this, PVArgs args) {
                        return y.fd(_this.index());
                    }
                }).height(new PVDoubleFunction<PVPanel>() {
                    public double f(PVPanel _this, PVArgs args) {
                        return y.rangeBand();
                    }
                }).add(PV.Bar()).data(new PVFunction<PVBar, JsArrayNumber>() {
                    public JsArrayNumber f(PVBar _this, PVArgs args) {
                        return args.getObject(0);
                    }
                }).top(new PVDoubleFunction<PVBar>() {
                    public double f(PVBar _this, PVArgs args) {
                        return _this.index() * y.rangeBand() / m;
                    }
                }).height(new PVDoubleFunction<PVBar>() {
                    public double f(PVBar _this, PVArgs args) {
                        return y.rangeBand() / m;
                    }
                }).left(0).width(new PVDoubleFunction<PVBar>() {
                    public double f(PVBar _this, PVArgs args) {
                        double d = args.getDouble(0);
                        return x.fd(d);
                    }
                }).fillStyle(new PVFunction<PVBar, PVColor>() {
                    public PVColor f(PVBar _this, PVArgs args) {
                        return category20.fcolor(_this.index());
                    }
                });

        /* The value label. */
        bar.anchor(RIGHT).add(PV.Label()).textStyle("white")
                .text(new PVStringFunction<PVLabel>() {
                    public String f(PVLabel _this, PVArgs args) {
                        double d = args.getDouble(0);
                        return JsUtils.toFixed(d, 1);
                    }
                });

        /* The variable label. */
        bar.parent().anchor(LEFT).add(PV.Label()).textAlign(RIGHT)
                .textMargin(5).text(new PVStringFunction<PVLabel>() {
                    public String f(PVLabel _this, PVArgs args) {
                        int i = _this.parent().index();
                        return "ABCDEFGHIJK".substring(i, i + 1);
                    }
                });

        /* X-axis ticks. */
        vis.add(PV.Rule()).data(x.ticks(5)).left(x)
                .strokeStyle(new PVStringFunction<PVRule>() {
                    public String f(PVRule _this, PVArgs args) {
                        double d = args.getDouble(0);
                        return d != 0 ? "rgba(255,255,255,.3)" : "#000";
                    }
                }).add(PV.Rule()).bottom(0).height(5).strokeStyle("#000")
                .anchor(BOTTOM).add(PV.Label()).text(x.tickFormat());
    }

    private JsArrayGeneric<JsArrayNumber> generateData() {
        JsArrayGeneric<JsArrayNumber> data = JsUtils.createJsArrayGeneric();
        for (int j = 0; j < 3; j++) {
            JsArrayNumber series = JsUtils.createJsArrayNumber();
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