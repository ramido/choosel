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
import java.util.HashSet;
import java.util.Set;

import org.thechiselgroup.choosel.protovis.client.MiserablesData.NovelCharacter;
import org.thechiselgroup.choosel.protovis.client.MiserablesData.NovelCharacterNodeAdapter;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsArgs;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsDoubleFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsStringFunction;

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

    private Set<Integer>[] createLinkedNodesArray(NovelCharacter[] nodes,
            final Link[] links) {

        final Set<Integer>[] linkedNodes = new Set[nodes.length];
        for (int i = 0; i < linkedNodes.length; i++) {
            linkedNodes[i] = new HashSet<Integer>();
        }
        for (Link link : links) {
            linkedNodes[link.getSource()].add(link.getTarget());
            linkedNodes[link.getTarget()].add(link.getSource());
        }
        return linkedNodes;
    }

    private void createVisualization(NovelCharacter[] nodes, final Link[] links) {
        final String selectedNodeIndexProperty = "selectedNodeIndex";
        final String selectedArcIndexProperty = "selectedArcIndex";

        final Set<Integer>[] linkedNodes = createLinkedNodesArray(nodes, links);

        final PVPanel vis = getPVPanel().width(880).height(420).bottom(200)
                .def(selectedNodeIndexProperty, -1)
                .def(selectedArcIndexProperty, null);

        final PVArcLayout arc = vis.add(PV.Layout.Arc())
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

        final PVColor arcColor = PV.color("rgba(0,0,0,.2)");
        final PVColor emphasizedArcColor = PV.color("rgba(127,0,0,.3)");
        final PVColor deemphasizedArcColor = PV.color("rgba(0,0,0,.075)");

        arc.link().add(PV.Line).strokeStyle(new JsFunction<PVColor>() {
            public PVColor f(JsArgs args) {
                PVLink d = args.getObject(1); // 0 is PVNode

                PVLink selectedArc = vis.getObject(selectedArcIndexProperty);
                if (selectedArc != null) {
                    return (d == selectedArc) ? emphasizedArcColor
                            : deemphasizedArcColor;
                }

                int selectedNodeIndex = vis.getInt(selectedNodeIndexProperty);
                if (selectedNodeIndex == -1) {
                    return arcColor;
                }

                if (d.source() == selectedNodeIndex
                        || d.target() == selectedNodeIndex) {
                    return emphasizedArcColor;
                }
                return deemphasizedArcColor;
            }
        }).event(PV.Event.MOUSEOVER, new PVEventHandler() {
            public void onEvent(Event e, String pvEventType, JsArgs args) {
                PVLink d = args.getObject(1);
                vis.set(selectedArcIndexProperty, d);
                vis.render();
            }
        }).event(PV.Event.MOUSEOUT, new PVEventHandler() {
            public void onEvent(Event e, String pvEventType, JsArgs args) {
                vis.set(selectedArcIndexProperty, null);
                vis.render();
            }
        });

        PVEventHandler nodeMouseOverHandler = new PVEventHandler() {
            public void onEvent(Event e, String pvEventType, JsArgs args) {
                PVMark _this = args.getThis();
                vis.set(selectedNodeIndexProperty, _this.index());
                vis.render();
            }
        };
        PVEventHandler nodeMouseOutHandler = new PVEventHandler() {
            public void onEvent(Event e, String pvEventType, JsArgs args) {
                vis.set(selectedNodeIndexProperty, -1);
                vis.render();
            }
        };

        final PVOrdinalScale barColorScale = PV.Colors.category19();
        final PVColor nodeLabelColor = PV.color("#000");
        final PVColor selectedNodeColor = PV.color("#A00");
        final PVColor inactiveNodeColor = PV.color("#AAA");

        arc.node()
                .add(PV.Bar)
                .width(10)
                .left(new JsDoubleFunction() {
                    public double f(JsArgs args) {
                        PVNode d = args.getObject();
                        return d.x() - 4;
                    }
                })
                .height(new JsDoubleFunction() {
                    public double f(JsArgs args) {
                        PVNode d = args.getObject();
                        return d.linkDegree();
                    }
                })
                .fillStyle(new JsFunction<PVColor>() {
                    public PVColor f(JsArgs args) {
                        int index = args.<PVMark> getThis().index();
                        PVNode d = args.getObject();
                        PVColor color = barColorScale.fcolor(d
                                .<NovelCharacter> object().getGroup());

                        PVLink arc = vis.getObject(selectedArcIndexProperty);
                        if (arc != null) {
                            return (index == arc.source() || index == arc
                                    .target()) ? color : inactiveNodeColor
                                    .brighter();
                        }

                        int selectedNodeIndex = vis
                                .getInt(selectedNodeIndexProperty);
                        if (selectedNodeIndex == -1) {
                            return color;
                        }

                        if (index == selectedNodeIndex) {
                            return selectedNodeColor;
                        }

                        return (linkedNodes[selectedNodeIndex].contains(index)) ? color
                                : inactiveNodeColor.brighter();
                    }
                }).strokeStyle(new JsFunction<PVColor>() {
                    public PVColor f(JsArgs args) {
                        PVDot _this = args.getThis();
                        return _this.fillStyle().darker();
                    }
                }).event(PV.Event.MOUSEOVER, nodeMouseOverHandler)
                .event(PV.Event.MOUSEOUT, nodeMouseOutHandler)
                .anchor(PVAlignment.BOTTOM).add(PV.Label)
                .text(new JsStringFunction() {
                    public String f(JsArgs args) {
                        PVNode d = args.getObject();
                        return d.nodeName();
                    }
                }).events("all")
                .event(PV.Event.MOUSEOVER, nodeMouseOverHandler)
                .event(PV.Event.MOUSEOUT, nodeMouseOutHandler)
                .textAlign(PVAlignment.RIGHT).textBaseline(PVAlignment.MIDDLE)
                .textAngle(-Math.PI / 2).textStyle(new JsFunction<PVColor>() {
                    public PVColor f(JsArgs args) {
                        int index = args.<PVMark> getThis().index();

                        PVLink arc = vis.getObject(selectedArcIndexProperty);
                        if (arc != null) {
                            return (index == arc.source() || index == arc
                                    .target()) ? nodeLabelColor
                                    : inactiveNodeColor;
                        }

                        int selectedNodeIndex = vis
                                .getInt(selectedNodeIndexProperty);
                        if (selectedNodeIndex == -1) {
                            return nodeLabelColor;
                        }

                        if (index == selectedNodeIndex) {
                            return selectedNodeColor;
                        }

                        return (linkedNodes[selectedNodeIndex].contains(index)) ? nodeLabelColor
                                : inactiveNodeColor;
                    }
                });
    }

    @Override
    public String getDescription() {
        return null;
    }

    public String getProtovisExampleURL() {
        return null;
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
        return "Arc Diagram (highlighting, bars)";
    }

}