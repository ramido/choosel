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

import org.thechiselgroup.choosel.protovis.client.jsutil.JsArgs;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsArrayGeneric;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsDoubleFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsStringFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsUtils;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;

/**
 * Protovis/GWT implementation of <a
 * href="http://vis.stanford.edu/protovis/ex/transform.html">Protovis Pan + Zoom
 * example</a>.
 * 
 * @author Lars Grammel
 */
public class PanAndZoomExample extends ProtovisWidget implements
        ProtovisExample {

    @Override
    public Widget asWidget() {
        return this;
    }

    private void createVisualization(JsArrayGeneric<Pair> data) {
        /* Sizing and scales. */
        final int w = 800;
        final int h = 400;
        final double kx = w / h;
        final double ky = 1;
        final PVLinearScale x = PV.Scale.linear(-kx, kx).range(0, w);
        final PVLinearScale y = PV.Scale.linear(-ky, ky).range(0, h);

        /* The root panel. */
        PVPanel vis = getPVPanel().width(w).height(h).top(30).left(40)
                .right(20).bottom(20).strokeStyle("#aaa");

        /* X-axis and ticks. */
        vis.add(PV.Rule).data(new JsFunction<JavaScriptObject>() {
            @Override
            public JavaScriptObject f(JsArgs args) {
                return x.ticks();
            }
        }).strokeStyle(new JsStringFunction() {
            public String f(JsArgs args) {
                double d = args.getDouble(0);
                return d != 0 ? "#ccc" : "#999";
            }
        }).left(x).anchor(BOTTOM).add(PV.Label).text(x.tickFormat());

        /* Y-axis and ticks. */
        vis.add(PV.Rule).data(new JsFunction<JavaScriptObject>() {
            @Override
            public JavaScriptObject f(JsArgs args) {
                return y.ticks();
            }
        }).strokeStyle(new JsStringFunction() {
            public String f(JsArgs args) {
                double d = args.getDouble(0);
                return d != 0 ? "#ccc" : "#999";
            }
        }).top(y).anchor(LEFT).add(PV.Label).text(y.tickFormat());

        /* The dot plot. */
        vis.add(PV.Panel).overflow("hidden").add(PV.Dot).data(data)
                .left(new JsDoubleFunction() {
                    public double f(JsArgs args) {
                        Pair d = args.getObject();
                        return x.fd(d.x);
                    }
                }).top(new JsDoubleFunction() {
                    public double f(JsArgs args) {
                        Pair d = args.getObject();
                        return y.fd(d.y);
                    }
                }).fillStyle(PV.rgb(121, 173, 210, .5))
                .radius(new JsDoubleFunction() {
                    public double f(JsArgs args) {
                        return 5 / args.<PVDot> getThis().scale();
                    }
                });

        /** Update the x- and y-scale domains per the new transform. */
        PVEventHandler transform = new PVEventHandler() {
            public void onEvent(Event e, String pvEventType, JsArgs args) {
                PVPanel _this = args.getThis();
                PVTransform t = _this.transform().invert();
                x.domain(t.x() / w * 2 * kx - kx, (t.k() + t.x() / w) * 2 * kx
                        - kx);
                y.domain(t.y() / h * 2 * ky - ky, (t.k() + t.y() / h) * 2 * ky
                        - ky);
                getPVPanel().render();
            }
        };

        /* Use an invisible panel to capture pan & zoom events. */
        vis.add(PV.Panel).events(PV.Events.ALL)
                .event(PV.Event.MOUSEDOWN, PV.Behavior.pan())
                .event(PV.Event.MOUSEWHEEL, PV.Behavior.zoom())
                .event(PV.Behavior.PAN, transform)
                .event(PV.Behavior.ZOOM, transform);
    }

    private JsArrayGeneric<Pair> generateData() {
        JsArrayGeneric<Pair> data = JsUtils.createJsArrayGeneric();

        // circle
        for (int i = 0; i < 100; i++) {
            double r = .5 + .15 * Math.random();
            double a = Math.PI * i / 50d;
            data.push(new Pair(r * Math.cos(a), r * Math.sin(a)));
        }

        // eyes
        for (int i = 0; i < 10; i++) {
            double x = -.25 + .1 * Math.random();
            double y = -.2 + .1 * Math.random();
            data.push(new Pair(x, y));
        }
        for (int i = 0; i < 10; i++) {
            double x = .25 - .1 * Math.random();
            double y = -.2 + .1 * Math.random();
            data.push(new Pair(x, y));
        }

        // nose
        for (int i = 0; i < 10; i++) {
            double x = 0.05 - .1 * Math.random();
            double y = .1 * Math.random();
            data.push(new Pair(x, y));
        }

        // mouth
        for (int i = 0; i < 25; i++) {
            double r = .3 + 0.05 * Math.random();
            double a = 2 * Math.PI / 8 + Math.PI * i / (4 * 12.5d);
            data.push(new Pair(r * Math.cos(a), r * Math.sin(a)));
        }

        return data;
    }

    public String getProtovisExampleURL() {
        return "http://vis.stanford.edu/protovis/ex/transform.html";
    }

    public String getSourceCodeFile() {
        return "PanAndZoomExample.java";
    }

    protected void onAttach() {
        super.onAttach();
        initPVPanel();
        createVisualization(generateData());
        getPVPanel().render();
    }

    public String toString() {
        return "Pan + Zoom";
    }

}