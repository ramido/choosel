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

import org.thechiselgroup.choosel.protovis.client.jsutil.JsArgs;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsStringFunction;

import com.google.gwt.user.client.ui.Widget;

/**
 * Protovis/GWT implementation of <a
 * href="http://vis.stanford.edu/protovis/ex/dendrogram.html">Protovis
 * dendrogram example</a>.
 * 
 * @author Lars Grammel
 */
public class DendrogramExample extends ProtovisWidget implements
        ProtovisExample {

    @Override
    public Widget asWidget() {
        return this;
    }

    private void createVisualization(FlareData.Unit root) {
        PVPanel vis = getPVPanel().width(200).height(2200).left(40).right(160)
                .top(10).bottom(10);

        PVClusterLayout layout = vis
                .add(PVLayout.Cluster())
                .nodes(PVDom.create(root, new FlareData.UnitDomAdapter())
                        .sort(new Comparator<PVDomNode>() {
                            public int compare(PVDomNode o1, PVDomNode o2) {
                                return o2.nodeName().compareTo(o1.nodeName());
                            }
                        }).nodes()).group(true).orient("left");

        layout.link().add(PV.Line).strokeStyle("#ccc").lineWidth(1)
                .antialias(false);

        layout.node().add(PV.Dot).fillStyle(new JsStringFunction() {
            public String f(JsArgs args) {
                PVDomNode n = args.getObject();
                return n.firstChild() != null ? "#aec7e8" : "#ff7f0e";
            }
        });

        layout.label().add(PV.Label);
    }

    public String getProtovisExampleURL() {
        return "http://vis.stanford.edu/protovis/ex/dendrogram.html";
    }

    public String getSourceCodeFile() {
        return "DendrogramExample.java";
    }

    protected void onAttach() {
        super.onAttach();
        initPVPanel();
        createVisualization(FlareData.data());
        getPVPanel().render();
    }

    public String toString() {
        return "Dendrogram";
    }

}