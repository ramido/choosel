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

import com.google.gwt.user.client.ui.Widget;

/**
 * Protovis/GWT implementation of <a
 * href="http://vis.stanford.edu/protovis/ex/treemap.html">Protovis treemap
 * example</a>.
 * 
 * @author Lars Grammel
 */
public class TreemapExample extends ProtovisWidget implements ProtovisExample {

    @Override
    public Widget asWidget() {
        return this;
    }

    private void createVisualization(FlareData.Unit root) {
        final PVOrdinalScale category19 = PV.Colors.category19();
        PVPanel vis = getPVPanel().width(860).height(568);

        PVTreemapLayout treemap = vis
                .add(PV.Layout.Treemap())
                .nodes(PVDom.create(root, new FlareData.UnitDomAdapter())
                        .nodes()).round(true);

        treemap.leaf().add(PV.Panel).fillStyle(new JsFunction<PVColor>() {
            public PVColor f(JsArgs args) {
                PVDomNode d = args.getObject();
                if (d.parentNode() == null) {
                    return category19.fcolor((String) null);
                }
                return category19.fcolor(d.parentNode().nodeName());
            }
        }).strokeStyle("#fff").lineWidth(1d).antialias(false);

        treemap.label().add(PV.Label);
    }

    @Override
    public String getDescription() {
        return null;
    }

    public String getProtovisExampleURL() {
        return "http://vis.stanford.edu/protovis/ex/treemap.html";
    }

    public String getSourceCodeFile() {
        return "TreemapExample.java";
    }

    protected void onAttach() {
        super.onAttach();
        initPVPanel();
        createVisualization(FlareData.data());
        getPVPanel().render();
    }

    public String toString() {
        return "Treemaps";
    }

}