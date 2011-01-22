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
import org.thechiselgroup.choosel.protovis.client.jsutil.JsStringFunction;

import com.google.gwt.user.client.ui.Widget;

/**
 * Protovis/GWT implementation of <a
 * href="http://vis.stanford.edu/protovis/ex/tree.html">Protovis node-link tree
 * example</a>.
 * 
 * @author Lars Grammel
 */
public class TreeExample extends ProtovisWidget implements ProtovisExample {

    @Override
    public Widget asWidget() {
        return this;
    }

    private void createVisualization(FlareData.Unit root) {
        PVPanel vis = getPVPanel().width(800).height(800).left(75).right(-75)
                .top(-30).bottom(-80);

        PVTreeLayout tree = vis
                .add(PVLayout.Tree())
                .nodes(PVDom.create(root, new FlareData.UnitDomAdapter())
                        .nodes()).depth(85).breadth(7.25).orient("radial");

        tree.link().add(PV.Line);

        tree.node().add(PV.Dot).fillStyle(new JsStringFunction() {
            public String f(JsArgs args) {
                PVDomNode n = args.getObject();
                return n.firstChild() != null ? "#aec7e8" : "#ff7f0e";
            }
        });

        tree.label().add(PV.Label);
    }

    public String getProtovisExampleURL() {
        return "http://vis.stanford.edu/protovis/ex/tree.html";
    }

    public String getSourceCodeFile() {
        return "TreeExample.java";
    }

    protected void onAttach() {
        super.onAttach();
        initPVPanel();
        createVisualization(FlareData.data());
        getPVPanel().render();
    }

    public String toString() {
        return "Node-Link Trees";
    }

}