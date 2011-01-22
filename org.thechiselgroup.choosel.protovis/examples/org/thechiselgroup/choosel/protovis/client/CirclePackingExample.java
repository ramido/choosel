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

import org.thechiselgroup.choosel.protovis.client.jsutil.JsArgs;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsBooleanFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsDoubleFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsStringFunction;

import com.google.gwt.user.client.ui.Widget;

/**
 * Protovis/GWT implementation of <a
 * href="http://vis.stanford.edu/protovis/ex/pack.html">Protovis circle packing
 * example</a>.
 * 
 * @author Lars Grammel
 */
public class CirclePackingExample extends ProtovisWidget implements
        ProtovisExample {

    @Override
    public Widget asWidget() {
        return this;
    }

    private void createVisualization(FlareData.Unit root) {
        final PVNumberFormat format = PVFormat.number();

        PVPanel vis = getPVPanel().width(796).height(796).margin(2);

        PVPackLayout pack = vis
                .add(PVLayout.Pack())
                .nodes(PVDom.create(root, new FlareData.UnitDomAdapter())
                        .nodes()).size(new JsDoubleFunction() {
                    public double f(JsArgs args) {
                        PVDomNode d = args.getObject();
                        return d.nodeValue();
                    }
                });

        pack.node().add(PV.Dot).fillStyle(new JsStringFunction() {
            public String f(JsArgs args) {
                PVDomNode d = args.getObject();
                return d.firstChild() != null ? "rgba(31, 119, 180, .25)"
                        : "#ff7f0e";
            }
        }).title(new JsStringFunction() {
            public String f(JsArgs args) {
                PVDomNode d = args.getObject();
                return d.nodeName()
                        + (d.firstChild() != null ? "" : ": "
                                + format.format(d.nodeValue()));
            }
        }).lineWidth(1);

        pack.label().add(PV.Label).visible(new JsBooleanFunction() {
            public boolean f(JsArgs args) {
                PVDomNode d = args.getObject();
                return d.firstChild() == null;
            }
        }).text(new JsStringFunction() {
            public String f(JsArgs args) {
                PVDomNode d = args.getObject();
                int length = (int) Math.sqrt(d.nodeValue()) / 20;
                return d.nodeName().substring(0,
                        Math.min(d.nodeName().length(), length));
            }
        });
    }

    public String getProtovisExampleURL() {
        return "http://vis.stanford.edu/protovis/ex/pack.html";
    }

    public String getSourceCodeFile() {
        return "CirclePackingExample.java";
    }

    protected void onAttach() {
        super.onAttach();
        initPVPanel();
        createVisualization(FlareData.data());
        getPVPanel().render();
    }

    public String toString() {
        return "Circle Packing";
    }

}