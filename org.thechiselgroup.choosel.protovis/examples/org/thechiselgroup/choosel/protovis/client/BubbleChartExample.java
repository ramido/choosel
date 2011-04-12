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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.thechiselgroup.choosel.protovis.client.jsutil.JsArgs;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsBooleanFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsDoubleFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsStringFunction;

import com.google.gwt.user.client.ui.Widget;

/**
 * Protovis/GWT implementation of <a
 * href="http://vis.stanford.edu/protovis/ex/bubble.html">Protovis bubble chart
 * example</a>.
 * 
 * @author Lars Grammel
 */
public class BubbleChartExample extends ProtovisWidget implements
        ProtovisExample {

    public static class ClassData {

        public String packageName;

        public String className;

        public int value;

        private ClassData(String packageName, String className, int value) {
            this.packageName = packageName;
            this.className = className;
            this.value = value;
        }

    }

    @Override
    public Widget asWidget() {
        return this;
    }

    private void createVisualization(FlareData.Unit root) {
        final PVOrdinalScale category20 = PV.Colors.category20();
        final PVNumberFormat format = PV.Format.number();

        PVPanel vis = getPVPanel().width(800).height(600);

        vis.add(PV.Layout.Pack())
                .top(-50)
                .bottom(-50)
                .nodes(PVDom.create(flatten(root),
                        new PVDomAdapter<ClassData>() {
                            public ClassData[] getChildren(ClassData t) {
                                return new ClassData[0];
                            }

                            public String getNodeName(ClassData t) {
                                return t.packageName + "." + t.className;
                            }

                            public double getNodeValue(ClassData t) {
                                return t.value;
                            }
                        }).nodes()).size(new JsDoubleFunction() {
                    public double f(JsArgs args) {
                        PVDomNode d = args.getObject();
                        return d.nodeValueDouble();
                    }
                }).spacing(0).order(null).node().add(PV.Dot)
                .fillStyle(new JsFunction<PVColor>() {
                    public PVColor f(JsArgs args) {
                        PVDomNode d = args.getObject();
                        return category20.fcolor(d.<ClassData> nodeObject().packageName);
                    }
                }).strokeStyle(new JsFunction<PVColor>() {
                    public PVColor f(JsArgs args) {
                        PVDot _this = args.getThis();
                        return _this.fillStyle().darker();
                    }
                }).visible(new JsBooleanFunction() {
                    public boolean f(JsArgs args) {
                        PVDomNode d = args.getObject();
                        return d.parentNode() != null;
                    }
                }).title(new JsStringFunction() {
                    public String f(JsArgs args) {
                        PVDomNode d = args.getObject();
                        return d.nodeName() + ": "
                                + format.format(d.nodeValueDouble());
                    }
                }).anchor(CENTER).add(PV.Label).text(new JsStringFunction() {
                    public String f(JsArgs args) {
                        PVDomNode d = args.getObject();
                        ClassData nodeObject = d.nodeObject();
                        return nodeObject.className.substring(0, Math.min(
                                ((int) Math.sqrt(d.nodeValueDouble())) >> 4,
                                nodeObject.className.length()));
                    }
                });

        vis.render();
    }

    private List<ClassData> flatten(FlareData.Unit root) {
        List<ClassData> result = new ArrayList<ClassData>();
        LinkedList<String> nameStack = new LinkedList<String>();

        flatten(root, nameStack, result);

        return result;
    }

    private void flatten(FlareData.Unit unit, LinkedList<String> nameStack,
            List<ClassData> result) {

        String separator = ".";

        if (isLeaf(unit)) {
            boolean first = true;
            String packageName = "";
            for (String s : nameStack) {
                if (!first) {
                    packageName += separator;
                } else {
                    first = false;
                }
                packageName += s;
            }
            result.add(new ClassData(packageName, unit.name, unit.value));
            return;
        }

        nameStack.addLast(unit.name);
        for (FlareData.Unit child : unit.children) {
            flatten(child, nameStack, result);
        }
        nameStack.removeLast();
    }

    @Override
    public String getDescription() {
        return null;
    }

    public String getProtovisExampleURL() {
        return "http://vis.stanford.edu/protovis/ex/bubble.html";
    }

    public String getSourceCodeFile() {
        return "BubbleChartExample.java";
    }

    private boolean isLeaf(FlareData.Unit unit) {
        return unit.children == null;
    }

    protected void onAttach() {
        super.onAttach();
        initPVPanel();
        createVisualization(FlareData.data());
        getPVPanel().render();
    }

    public String toString() {
        return "Bubble Chart";
    }

}