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
import static org.thechiselgroup.choosel.protovis.client.PVEventTypes.MOUSEOUT;
import static org.thechiselgroup.choosel.protovis.client.PVEventTypes.MOUSEOVER;

import org.thechiselgroup.choosel.protovis.client.functions.PVBooleanFunctionDoubleArg;
import org.thechiselgroup.choosel.protovis.client.functions.PVDoubleFunctionDoubleArg;
import org.thechiselgroup.choosel.protovis.client.functions.PVEventHandler;
import org.thechiselgroup.choosel.protovis.client.functions.PVStringFunctionDoubleArg;
import org.thechiselgroup.choosel.protovis.client.util.JsUtils;

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
        final PVLinearScale a = PVScale.linear(0, PV.sum(data)).range(0,
                2 * Math.PI);

        /* The root panel. */
        PVPanel vis = getPVPanel().width(w).height(h);

        /* The wedge, with centered label. */
        vis.add(PV.Wedge()).data(PV.sort(data, PV.reverseOrder()))
                .bottom(w / 2).left(w / 2).innerRadius(r - 40).outerRadius(r)
                .angle(new PVDoubleFunctionDoubleArg<PVWedge>() {
                    public double f(PVWedge _this, double d) {
                        return a.fd(d);
                    }
                }).event(MOUSEOVER, new PVEventHandler<PVWedge>() {
                    public void onEvent(PVWedge _this, Event e) {
                        _this.innerRadius(0);
                        _this.render();
                    }
                }).event(MOUSEOUT, new PVEventHandler<PVWedge>() {
                    public void onEvent(PVWedge _this, Event e) {
                        _this.innerRadius(r - 40);
                        _this.render();
                    }
                }).anchor(CENTER).add(PV.Label())
                .visible(new PVBooleanFunctionDoubleArg<PVLabel>() {
                    public boolean f(PVLabel _this, double d) {
                        return d > .15;
                    }
                }).textAngle(0).text(new PVStringFunctionDoubleArg<PVLabel>() {
                    public String f(PVLabel _this, double d) {
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