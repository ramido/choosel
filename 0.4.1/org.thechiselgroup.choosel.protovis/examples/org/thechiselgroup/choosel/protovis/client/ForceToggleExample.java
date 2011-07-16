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
import org.thechiselgroup.choosel.protovis.client.jsutil.JsDoubleFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsStringFunction;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;

/**
 * Protovis/GWT implementation of <a href=
 * "http://gitorious.org/protovis/protovis/blobs/e90698ec7a4af638d871d5179e9c391c1f9c5597/tests/layout/force-toggle.html"
 * >Force Toggle Test</a>.
 * 
 * @author Lars Grammel
 */
public class ForceToggleExample extends ProtovisWidget implements
        ProtovisExample {

    @Override
    public Widget asWidget() {
        return this;
    }

    private void createVisualization(FlareData.Unit flare) {
        final PVDomNode root = PVDom.create(flare,
                new FlareData.UnitDomAdapter());

        root.toggle(true);
        root.toggle();

        PVPanel vis = getPVPanel().width(900).height(900).fillStyle("white")
                .event(PV.Event.MOUSEDOWN, PV.Behavior.pan())
                .event(PV.Event.MOUSEWHEEL, PV.Behavior.zoom());

        final PVForceLayout layout = vis.add(PV.Layout.Force())
                .nodes(new JsFunction<JavaScriptObject>() {
                    @Override
                    public JavaScriptObject f(JsArgs args) {
                        return root.nodes();
                    }
                }).links(PV.Layout.HierarchyLinks());

        layout.link().add(PV.Line);
        layout.node()
                .add(PV.Dot)
                .title(new JsStringFunction() {
                    public String f(JsArgs args) {
                        PVDomNode d = args.getObject();
                        return d.nodeName();
                    }
                })
                .size(new JsDoubleFunction() {
                    public double f(JsArgs args) {
                        PVDomNode d = args.getObject();
                        return d.hasNodeValue() ? Math.max(
                                d.nodeValueInt() / 100, 10) : 10;
                    }
                })
                .fillStyle(new JsStringFunction() {
                    public String f(JsArgs args) {
                        PVDomNode n = args.getObject();
                        return n.toggled() ? "#1f77b4"
                                : (n.firstChild() != null ? "#aec7e8"
                                        : "#ff7f0e");
                    }
                }).event(PV.Event.MOUSEDOWN, PV.Behavior.drag())
                .event(PV.Event.DRAG, layout)
                .event(PV.Event.DOUBLE_CLICK, new PVEventHandler() {
                    @Override
                    public void onEvent(Event e, String pvEventType, JsArgs args) {
                        PVDomNode n = args.getObject();
                        n.toggle(e.getAltKey());
                        layout.reset();
                    }
                });
    }

    @Override
    public String getDescription() {
        return "Use double-click to expand/collapse direct children. Use ALT + double-click to expand/collapse all children.";
    }

    public String getProtovisExampleURL() {
        return "http://gitorious.org/protovis/protovis/blobs/e90698ec7a4af638d871d5179e9c391c1f9c5597/tests/layout/force-toggle.html";
    }

    public String getSourceCodeFile() {
        return "ForceToggleExample.java";
    }

    protected void onAttach() {
        super.onAttach();
        initPVPanel();
        createVisualization(FlareData.data());
        getPVPanel().render();
    }

    public String toString() {
        return "Force Toggle Test";
    }

}