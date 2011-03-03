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
import org.thechiselgroup.choosel.protovis.client.jsutil.JsFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsStringFunction;

import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;

/**
 * Additional tree map example that emphasizes how to use the UnitDomAdapter.
 * 
 * @author Lars Grammel
 */
public class TreemapExample2 extends ProtovisWidget implements ProtovisExample {

    public static class MyDataClass {

        public MyDataClass[] children;

        public int value;

        public String name;

        private String additionalProperty = null;

        public MyDataClass(String name, int value) {
            this.value = value;
            this.name = name;
        }

        public MyDataClass(String name, int value, String addionalProperty) {
            this.value = value;
            this.name = name;
            this.additionalProperty = addionalProperty;
        }

        public MyDataClass(String name, MyDataClass... children) {
            this.children = children;
            this.name = name;
        }

        public String getAdditionalProperty() {
            return additionalProperty;
        }
    }

    public static class MyDomAdapter implements PVDomAdapter<MyDataClass> {

        public MyDataClass[] getChildren(MyDataClass t) {
            return t.children == null ? new MyDataClass[0] : t.children;
        }

        public String getNodeName(MyDataClass t) {
            return t.name;
        }

        public double getNodeValue(MyDataClass t) {
            return t.value;
        }

    }

    private static MyDataClass data() {
        return new MyDataClass("flare", new MyDataClass("analytics",
                new MyDataClass("cluster", new MyDataClass(
                        "AgglomerativeCluster", 3938, "my additional text"),
                        new MyDataClass("CommunityStructure", 3812),
                        new MyDataClass("HierarchicalCluster", 6714)),
                new MyDataClass("ISchedulable", 1041, "my additional text"),
                new MyDataClass("Parallel", 5176, "other additional text"),
                new MyDataClass("Pause", 449), new MyDataClass("Scheduler",
                        5593), new MyDataClass("Sequence", 5534),
                new MyDataClass("Tween", 6006)));
    }

    @Override
    public Widget asWidget() {
        return this;
    }

    private void createVisualization(MyDataClass root) {
        final PVOrdinalScale category19 = PVColors.category19();
        final PVPanel vis = getPVPanel().width(860).height(568);
        vis.defInt("i", -1);

        PVTreemapLayout treemap = vis.add(PVLayout.Treemap())
                .nodes(PVDom.create(root, new MyDomAdapter()).nodes())
                .round(true);

        treemap.leaf().add(PV.Panel).fillStyle(new JsFunction<PVColor>() {
            public PVColor f(JsArgs args) {
                PVDomNode d = args.getObject();
                PVMark _this = args.getThis();

                if (vis.getInt("i") == _this.index()) {
                    return PV.color("orange");
                }

                if (d.parentNode() == null) {
                    return category19.fcolor((String) null);
                }
                return category19.fcolor(d.parentNode().nodeName());
            }
        }).strokeStyle("#fff").lineWidth(1d).antialias(false)
                .event(PVEventType.MOUSEOVER, new PVEventHandler() {
                    public void onEvent(Event e, String pvEventType, JsArgs args) {
                        PVMark _this = args.getThis();
                        vis.setInt("i", _this.index());
                        _this.render();
                    }
                }).event(PVEventType.MOUSEOUT, new PVEventHandler() {
                    public void onEvent(Event e, String pvEventType, JsArgs args) {
                        PVMark _this = args.getThis();
                        vis.setInt("i", -1);
                        _this.render();
                    }
                });

        treemap.label().add(PV.Label).font(new JsStringFunction() {
            public String f(JsArgs args) {
                PVDomNode d = args.getObject();
                MyDataClass myData = d.nodeObject();
                return myData.getAdditionalProperty() != null ? "bold 20px serif"
                        : "italic 10px serif";
            }
        });
    }

    public String getProtovisExampleURL() {
        return null;
    }

    public String getSourceCodeFile() {
        return "TreemapExample2.java";
    }

    protected void onAttach() {
        super.onAttach();
        initPVPanel();
        createVisualization(data());
        getPVPanel().render();
    }

    public String toString() {
        return "Treemap  Example 2";
    }

}