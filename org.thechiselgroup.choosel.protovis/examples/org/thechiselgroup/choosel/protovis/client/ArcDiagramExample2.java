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

import java.util.Comparator;

import org.thechiselgroup.choosel.protovis.client.MiserablesData.NovelCharacter;
import org.thechiselgroup.choosel.protovis.client.MiserablesData.NovelCharacterNodeAdapter;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsArgs;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsDoubleFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsFunction;

import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;

/**
 * Extends version of Protovis/GWT implementation of <a
 * href="http://vis.stanford.edu/protovis/ex/arc.html">Protovis arc diagram
 * example</a>.
 * 
 * @author Lars Grammel
 */
public class ArcDiagramExample2 extends ProtovisWidget implements
        ProtovisExample {

    @Override
    public Widget asWidget() {
        return this;
    }

    private void createVisualization(NovelCharacter[] nodes, Link[] links) {
        final String nodeIndexProperty = "nodeIndex";
        final PVPanel vis = getPVPanel().width(880).height(310).bottom(90)
                .defInt(nodeIndexProperty, -1);

        final PVArcLayout arc = vis.add(PVLayout.Arc())
                .nodes(new NovelCharacterNodeAdapter(), nodes).links(links)
                .sort(new Comparator<PVNode>() {
                    public int compare(PVNode a, PVNode b) {
                        NovelCharacter ac = a.object();
                        NovelCharacter bc = b.object();

                        return ac.getGroup() == bc.getGroup() ? b.linkDegree()
                                - a.linkDegree() : bc.getGroup()
                                - ac.getGroup();
                    }
                });

        arc.link().add(PV.Line).strokeStyle(new JsFunction<PVColor>() {
            public PVColor f(JsArgs args) {
                PVLink d = args.getObject(1); // 0 is PVNode
                int nodeIndex = vis.getInt(nodeIndexProperty);
                if (d.source() == nodeIndex || d.target() == nodeIndex) {
                    return PV.color("rgba(127,0,0,.2)");
                }
                return PV.color("rgba(0,0,0,.2)");
            }
        });

        arc.node().add(PV.Dot).size(new JsDoubleFunction() {
            public double f(JsArgs args) {
                PVNode d = args.getObject();
                return d.linkDegree() + 4;
            }
        }).fillStyle(new JsFunction<PVColor>() {

            private PVOrdinalScale category19 = PVColors.category19();

            public PVColor f(JsArgs args) {
                PVNode d = args.getObject();
                return category19
                        .fcolor(d.<NovelCharacter> object().getGroup());
            }
        }).strokeStyle(new JsFunction<PVColor>() {
            public PVColor f(JsArgs args) {
                PVDot _this = args.getThis();
                return _this.fillStyle().darker();
            }
        }).event(PVEventType.MOUSEOVER, new PVEventHandler() {
            public void onEvent(Event e, String pvEventType, JsArgs args) {
                PVMark _this = args.getThis();
                vis.setInt(nodeIndexProperty, _this.index());
                vis.render();
            }
        }).event(PVEventType.MOUSEOUT, new PVEventHandler() {
            public void onEvent(Event e, String pvEventType, JsArgs args) {
                vis.setInt(nodeIndexProperty, -1);
                vis.render();
            }
        });

        arc.label().add(PV.Label);
    }

    public String getProtovisExampleURL() {
        return "http://vis.stanford.edu/protovis/ex/arc.html";
    }

    public String getSourceCodeFile() {
        return "ArcDiagramExample2.java";
    }

    protected void onAttach() {
        super.onAttach();
        initPVPanel();
        createVisualization(MiserablesData.CHARACTERS, MiserablesData.LINKS);
        getPVPanel().render();
    }

    public String toString() {
        return "Arc Diagram (highlighting)";
    }

}