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

import org.thechiselgroup.choosel.protovis.client.PVFillPartitionLayout.PVRadialNode;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsArgs;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsBooleanFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsDoubleFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsFunction;

import com.google.gwt.user.client.ui.Widget;

/**
 * Protovis/GWT implementation of <a
 * href="http://vis.stanford.edu/protovis/ex/sunburst.html">Protovis sunburst
 * example</a>.
 * 
 * @author Lars Grammel
 */
public class SunburstExample extends ProtovisWidget implements ProtovisExample {

    @Override
    public Widget asWidget() {
        return this;
    }

    private void createVisualization(FlareData.Unit root) {
        PVPanel vis = getPVPanel().width(900).height(900).bottom(-80);

        final PVOrdinalScale category19 = PVColors.category19();

        PVFillPartitionLayout partition = vis
                .add(PVLayout.PartitionFill())
                .nodes(PVDom.create(root, new FlareData.UnitDomAdapter())
                        .nodes()).size(new JsDoubleFunction() {
                    public double f(JsArgs args) {
                        PVDomNode d = args.getObject();
                        return d.nodeValue();
                    }
                }).order("descending").orient("radial");

        partition.node().add(PV.Wedge).fillStyle(new JsFunction<PVColor>() {
            public PVColor f(JsArgs args) {
                PVDomNode d = args.getObject();
                if (d.parentNode() == null) {
                    return category19.fcolor((String) null);
                }
                return category19.fcolor(d.parentNode().nodeName());
            }
        }).strokeStyle("#fff").lineWidth(.5);

        partition.label().add(PV.Label).visible(new JsBooleanFunction() {
            public boolean f(JsArgs args) {
                PVRadialNode d = args.getObject();
                return d.angle() * d.outerRadius() >= 6;
            }
        });
    }

    public String getProtovisExampleURL() {
        return "http://vis.stanford.edu/protovis/ex/sunburst.html";
    }

    public String getSourceCodeFile() {
        return "SunburstExample.java";
    }

    protected void onAttach() {
        super.onAttach();
        initPVPanel();
        createVisualization(FlareData.data());
        getPVPanel().render();
    }

    public String toString() {
        return "Sunbursts";
    }

}