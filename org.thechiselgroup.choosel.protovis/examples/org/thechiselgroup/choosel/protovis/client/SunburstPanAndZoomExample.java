/*******************************************************************************
 * Copyright (C) 2011 Lars Grammel 
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

import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;

/**
 * Protovis/GWT implementation of <a
 * href="http://vis.stanford.edu/protovis/ex/sunburst.html">Protovis sunburst
 * example</a> with Pan and Zoom.
 * 
 * @author Lars Grammel
 */
public class SunburstPanAndZoomExample extends ProtovisWidget implements
        ProtovisExample {

    @Override
    public Widget asWidget() {
        return this;
    }

    private void createVisualization(FlareData.Unit root) {
        final int w = 900;
        final int h = 900;
        final double kx = w / h;
        final double ky = 1;
        final PVLinearScale x = PV.Scale.linear(-kx, kx).range(0, w);
        final PVLinearScale y = PV.Scale.linear(-ky, ky).range(0, h);

        PVPanel vis = getPVPanel().width(w).height(h).bottom(-80);

        final PVOrdinalScale category19 = PV.Colors.category19();

        PVFillPartitionLayout partition = vis
                .add(PV.Layout.PartitionFill())
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

        /** Update the x- and y-scale domains per the new transform. */
        PVEventHandler transform = new PVEventHandler() {
            public void onEvent(Event e, String pvEventType, JsArgs args) {
                PVPanel _this = args.getThis();
                PVTransform t = _this.transform().invert();
                // to do scale on the layout

                x.domain(t.x() / w * 2 * kx - kx, (t.k() + t.x() / w) * 2 * kx
                        - kx);
                y.domain(t.y() / h * 2 * ky - ky, (t.k() + t.y() / h) * 2 * ky
                        - ky);
                getPVPanel().render();
            }
        };

        /* Use an invisible panel to capture pan & zoom events. */
        getPVPanel().events(PV.Events.ALL)
                .event(PV.Event.MOUSEDOWN, PV.Behavior.pan())
                .event(PV.Event.MOUSEWHEEL, PV.Behavior.zoom())
                .event(PV.Behavior.PAN, transform)
                .event(PV.Behavior.ZOOM, transform);
    }

    public String getProtovisExampleURL() {
        return null;
    }

    public String getSourceCodeFile() {
        return "SunburstPanAndZoomExample.java";
    }

    protected void onAttach() {
        super.onAttach();
        initPVPanel();
        createVisualization(FlareData.data());
        getPVPanel().render();
    }

    public String toString() {
        return "Sunbursts (Pan + Zoom)";
    }

}