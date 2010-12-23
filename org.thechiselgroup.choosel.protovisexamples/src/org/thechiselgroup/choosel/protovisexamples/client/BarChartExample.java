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

import org.thechiselgroup.choosel.protovis.client.DoubleFunction;
import org.thechiselgroup.choosel.protovis.client.JsGenericArray;
import org.thechiselgroup.choosel.protovis.client.JsUtils;
import org.thechiselgroup.choosel.protovis.client.PV;
import org.thechiselgroup.choosel.protovis.client.PVAlignment;
import org.thechiselgroup.choosel.protovis.client.PVBar;
import org.thechiselgroup.choosel.protovis.client.PVLabel;
import org.thechiselgroup.choosel.protovis.client.PVLinearScale;
import org.thechiselgroup.choosel.protovis.client.PVOrdinalScale;
import org.thechiselgroup.choosel.protovis.client.PVPanel;
import org.thechiselgroup.choosel.protovis.client.PVRule;
import org.thechiselgroup.choosel.protovis.client.PVScale;
import org.thechiselgroup.choosel.protovis.client.ProtovisWidget;
import org.thechiselgroup.choosel.protovis.client.StringFunction;
import org.thechiselgroup.choosel.protovis.client.StringFunctionDoubleArg;

/**
 * Protovis/GWT implementation of <a
 * href="http://vis.stanford.edu/protovis/ex/bar.html">Protovis bar chart
 * example</a>.
 * 
 * @author Lars Grammel
 */
public class BarChartExample extends ProtovisWidget implements ProtovisExample {

    public String getProtovisExampleURL() {
        return "http://vis.stanford.edu/protovis/ex/bar.html";
    }

    protected void onAttach() {
        super.onAttach();
        initChartPanel();

        JsGenericArray<Double> data = JsGenericArray.createGenericArray();
        for (int i = 0; i < 10; i++) {
            data.push(Math.random() + .1);
        }

        int w = 400;
        int h = 250;

        final PVLinearScale x = PVScale.linear(0, 1.1).range(0, w);
        final PVOrdinalScale y = PVScale.ordinal().domain(PV.range(10))
                .splitBanded(0, h, 4d / 5d);

        PVPanel vis = getChartPanel().width(w).height(h).bottom(20).left(20)
                .right(10).top(5);

        /* The bars. */
        PVBar bar = vis.add(PVBar.createBar()).data(data)
                .top(new DoubleFunction<Double>() {
                    @Override
                    public double f(Double d, int i) {
                        return y.f(i);
                    }
                }).height(new DoubleFunction<Double>() {
                    @Override
                    public double f(Double d, int i) {
                        return y.rangeBand();
                    }
                }).left(0).width(x);

        /* The value label. */
        bar.anchor(PVAlignment.RIGHT).add(PVLabel.createLabel())
                .textStyle("white").text(new StringFunction<Double>() {
                    @Override
                    public String f(Double d, int i) {
                        return JsUtils.toFixed(d, 1);
                    }
                });

        /* The variable label. */
        bar.anchor(PVAlignment.LEFT).add(PVLabel.createLabel()).textMargin(5)
                .textAlign(PVAlignment.RIGHT)
                .text(new StringFunction<Double>() {
                    @Override
                    public String f(Double d, int i) {
                        return "ABCDEFGHIJK".substring(i, i + 1);
                    }
                });

        /* X-axis ticks. */
        vis.add(PVRule.createRule()).data(x.ticks(5)).left(x)
                .strokeStyle(new StringFunctionDoubleArg() {
                    @Override
                    public String f(double d, int i) {
                        return d != 0 ? "rgba(255,255,255,.3)" : "#000";
                    }
                }).add(PVRule.createRule()).bottom(0).height(5)
                .strokeStyle("#000").anchor(PVAlignment.BOTTOM)
                .add(PVLabel.createLabel()).text(x.tickFormat());

        vis.render();
    }

    public String toString() {
        return "Bar Chart Example";
    }
}