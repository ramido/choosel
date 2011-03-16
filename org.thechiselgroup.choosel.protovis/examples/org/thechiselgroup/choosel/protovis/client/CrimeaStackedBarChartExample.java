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
import static org.thechiselgroup.choosel.protovis.client.PVAlignment.RIGHT;
import static org.thechiselgroup.choosel.protovis.client.PVAlignment.TOP;

import org.thechiselgroup.choosel.protovis.client.jsutil.JsArgs;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsBooleanFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsDoubleFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsStringFunction;

import com.google.gwt.core.client.JsDate;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Widget;

/**
 * Protovis/GWT implementation of <a href=
 * "http://vis.stanford.edu/protovis/ex/crimea-stacked-bar.html" >Crimean war
 * stacked bar chart example</a>.
 * 
 * @author Lars Grammel
 */
public class CrimeaStackedBarChartExample extends ProtovisWidget implements
        ProtovisExample {

    @Override
    public Widget asWidget() {
        return this;
    }

    public static enum Cause {

        WOUNDS, OTHER, DISEASE;

        public int getValue(CrimeanWarData d) {
            switch (this) {
            case WOUNDS:
                return d.getWounds();
            case OTHER:
                return d.getOther();
            case DISEASE:
                return d.getDisease();
            }

            throw new RuntimeException("cannot be reached");
        }
    }

    private void createVisualization(CrimeanWarData[] crimea) {
        final PVOrdinalScale fill = PV.colors("lightpink", "darkgray",
                "lightblue");
        final DateTimeFormat format = DateTimeFormat.getFormat("MMM");

        int w = 545;
        int h = 280;
        final PVOrdinalScale x = PVScale.ordinal(crimea,
                new JsFunction<JsDate>() {
                    @Override
                    public JsDate f(JsArgs args) {
                        CrimeanWarData d = args.getObject();
                        return JsDate.create(d.getDate().getTime());
                    }
                }).splitBanded(0, w);
        final PVLinearScale y = PVScale.linear(0, 2200).range(0, h);

        PVPanel vis = getPVPanel().width(w).height(h).margin(19.5).right(40);

        vis.add(PVLayout.Stack()).layers(Cause.values()).values(crimea)
                .x(new JsDoubleFunction() {
                    public double f(JsArgs args) {
                        CrimeanWarData d = args.getObject();
                        return x.fd(d.getDate());
                    }
                }).y(new JsDoubleFunction() {
                    public double f(JsArgs args) {
                        CrimeanWarData d = args.getObject(0);
                        Cause t = args.getObject(1);
                        return y.fd(t.getValue(d));
                    }
                }).layer().add(PV.Bar).antialias(false).width(x.rangeBand())
                .fillStyle(new JsFunction<PVColor>() {
                    public PVColor f(JsArgs args) {
                        Cause d = args.getObject(1);
                        return fill.fcolor(d);
                    }
                }).strokeStyle(new JsFunction<PVColor>() {
                    public PVColor f(JsArgs args) {
                        PVBar _this = args.getThis();
                        return _this.fillStyle().darker();
                    }
                }).lineWidth(1).anchor(BOTTOM).add(PV.Label)
                .visible(new JsBooleanFunction() {
                    public boolean f(JsArgs args) {
                        PVLabel _this = args.getThis();
                        return _this.parent().index() == 0
                                && _this.index() % 3 == 0;
                    }
                }).textBaseline(TOP).textMargin(5).text(new JsStringFunction() {
                    public String f(JsArgs args) {
                        CrimeanWarData d = args.getObject();
                        return format.format(d.getDate());
                    }
                });

        vis.add(PV.Rule).data(y.ticks(5)).bottom(y)
                .strokeStyle(new JsStringFunction() {
                    public String f(JsArgs args) {
                        double d = args.getDouble();
                        return d != 0 ? "rgba(255, 255, 255, .7)" : "black";
                    }
                }).anchor(RIGHT).add(PV.Label).textMargin(6);
    }

    public String getProtovisExampleURL() {
        return "http://vis.stanford.edu/protovis/ex/crimea-stacked-bar.html";
    }

    public String getSourceCodeFile() {
        return "CrimeaStackedBarChartExample.java";
    }

    protected void onAttach() {
        super.onAttach();
        initPVPanel();
        createVisualization(CrimeanWarData.getData());
        getPVPanel().render();
    }

    public String toString() {
        return "Crimean War Stacked Bar Chart";
    }

}