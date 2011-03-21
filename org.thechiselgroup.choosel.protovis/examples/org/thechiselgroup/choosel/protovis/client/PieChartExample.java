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

import static org.thechiselgroup.choosel.protovis.client.PVAlignment.CENTER;

import org.thechiselgroup.choosel.protovis.client.jsutil.JsArgs;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsBooleanFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsDoubleFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsStringFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsUtils;

import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;

/**
 * Protovis/GWT implementation of <a
 * href="http://vis.stanford.edu/protovis/ex/pie.html">Protovis pie chart
 * example</a>.
 * 
 * @author Lars Grammel
 */
public class PieChartExample extends ProtovisWidget implements ProtovisExample {

    @Override
    public Widget asWidget() {
        return this;
    }

    private void createVisualization(JsArrayNumber data) {
        /* Sizing and scales. */
        int w = 400;
        int h = 400;
        final int r = w / 2;
        final PVLinearScale a = PV.Scale.linear(0, PV.sum(data)).range(0,
                2 * Math.PI);

        /* The root panel. */
        PVPanel vis = getPVPanel().width(w).height(h);

        /* The wedge, with centered label. */
        vis.add(PV.Wedge)
                .data(PV.sort(data, PV.reverseOrder()))
                .bottom(w / 2)
                .left(w / 2)
                .innerRadius(r - 40)
                .outerRadius(r)
                .angle(new JsDoubleFunction() {
                    public double f(JsArgs args) {
                        double d = args.getDouble(0);
                        return a.fd(d);
                    }
                })
                .event(PV.Event.MOUSEOVER, new PVEventHandler() {
                    public void onEvent(Event e, String pvEventType, JsArgs args) {
                        PVWedge _this = args.getThis();
                        _this.innerRadius(0);
                        _this.render();
                    }
                })
                .event(PV.Event.MOUSEOUT, new PVEventHandler() {
                    @Override
                    public void onEvent(Event e, String pvEventType, JsArgs args) {
                        PVWedge _this = args.getThis();
                        _this.innerRadius(r - 40);
                        _this.render();
                    }
                }).anchor(CENTER).add(PV.Label)
                .visible(new JsBooleanFunction() {
                    public boolean f(JsArgs args) {
                        double d = args.getDouble(0);
                        return d > .15;
                    }
                }).textAngle(0).text(new JsStringFunction() {
                    public String f(JsArgs args) {
                        double d = args.getDouble(0);
                        return JsUtils.toFixed(d, 2);
                    }
                });
    }

    private JsArrayNumber generateData() {
        JsArrayNumber data = JsUtils.createJsArrayNumber();
        for (int i = 0; i < 10; i++) {
            data.push(Math.random());
        }
        return data;
    }

    public String getProtovisExampleURL() {
        return "http://vis.stanford.edu/protovis/ex/pie.html";
    }

    public String getSourceCodeFile() {
        return "PieChartExample.java";
    }

    protected void onAttach() {
        super.onAttach();
        initPVPanel();
        createVisualization(generateData());
        getPVPanel().render();
    }

    public String toString() {
        return "Pie/Donut Chart";
    }

}