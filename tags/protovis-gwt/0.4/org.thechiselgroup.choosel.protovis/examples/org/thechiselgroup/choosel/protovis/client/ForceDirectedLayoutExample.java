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

import org.thechiselgroup.choosel.protovis.client.MiserablesData.NovelCharacter;
import org.thechiselgroup.choosel.protovis.client.MiserablesData.NovelCharacterNodeAdapter;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsArgs;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsDoubleFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsStringFunction;

import com.google.gwt.user.client.ui.Widget;

/**
 * Protovis/GWT implementation of <a
 * href="http://vis.stanford.edu/protovis/ex/force.html">Protovis Force-Directed
 * Layout example</a>.
 * 
 * @author Lars Grammel
 */
public class ForceDirectedLayoutExample extends ProtovisWidget implements
        ProtovisExample {

    private PVForceLayout force;

    @Override
    public Widget asWidget() {
        return this;
    }

    private void createVisualization(NovelCharacter[] nodes, Link[] links) {
        int w = 600;
        int h = 400;

        PVPanel vis = getPVPanel().width(w).height(h).fillStyle("white")
                .event(PV.Event.MOUSEDOWN, PV.Behavior.pan())
                .event(PV.Event.MOUSEWHEEL, PV.Behavior.zoom());

        force = vis.add(PV.Layout.Force())
                .nodes(new NovelCharacterNodeAdapter(), nodes).links(links);

        force.link().add(PV.Line);

        force.node().add(PV.Dot).size(new JsDoubleFunction() {
            public double f(JsArgs args) {
                PVNode d = args.getObject();
                PVMark _this = args.<PVMark> getThis();
                return (d.linkDegree() + 4) * (Math.pow(_this.scale(), -1.5));
            }
        }).fillStyle(new JsFunction<PVColor>() {
            private PVOrdinalScale colors = PV.Colors.category19();

            public PVColor f(JsArgs args) {
                PVNode d = args.getObject();
                if (d.fix()) {
                    return PV.color("brown");
                }
                return colors.fcolor(d.<NovelCharacter> object().getGroup());
            }
        }).strokeStyle(new JsFunction<PVColor>() {
            public PVColor f(JsArgs args) {
                PVDot _this = args.getThis();
                return _this.fillStyle().darker();
            }
        }).lineWidth(1).title(new JsStringFunction() {
            public String f(JsArgs args) {
                PVNode d = args.getObject();
                return d.nodeName();
            }
        }).event(PV.Event.MOUSEDOWN, PV.Behavior.drag())
                .event(PV.Event.DRAG, force);
    }

    @Override
    protected void onDetach() {
        // stop the force directed layout
        if (force != null) {
            force.stop();
        }

        super.onDetach();
    }

    public String getProtovisExampleURL() {
        return "http://vis.stanford.edu/protovis/ex/force.html";
    }

    public String getSourceCodeFile() {
        return "ForceDirectedLayoutExample.java";
    }

    protected void onAttach() {
        super.onAttach();
        initPVPanel();
        createVisualization(MiserablesData.CHARACTERS, MiserablesData.LINKS);
        getPVPanel().render();
        asWidget().getElement().getStyle()
                .setProperty("border", "1px solid #aaa");
    }

    public String toString() {
        return "Force-Directed Layout";
    }

}