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
import org.thechiselgroup.choosel.protovis.client.jsutil.JsArrayGeneric;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsDoubleFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsUtils;

import com.google.gwt.user.client.ui.Widget;

/**
 * @author Lars Grammel
 * @author Guillaume Godin
 */
public class EllipseExample extends ProtovisWidget implements ProtovisExample {

    public final static int[][] EXAMPLE_DATA = new int[][] {
            new int[] { 200, 100, 25, 25, 0 },
            new int[] { 200, 250, 50, 25, 45 },
            new int[] { 400, 250, 25, 50, 0 },
            new int[] { 400, 100, 20, 40, 175 },
            new int[] { 400, 120, 70, 35, 0 } };

    @Override
    public Widget asWidget() {
        return this;
    }

    private void createVisualization(JsArrayGeneric<int[]> data) {
        /* The root panel. */
        PVPanel vis = getPVPanel().width(600).height(400).fillStyle("white");

        /* The ellipse plot! */
        vis.add(PV.Panel).data(data).add(PV.Ellipse)
                .left(new JsDoubleFunction() {
                    public double f(JsArgs args) {
                        return args.<int[]> getObject()[0];
                    }
                }).top(new JsDoubleFunction() {
                    public double f(JsArgs args) {
                        return args.<int[]> getObject()[1];
                    }
                }).horizontalRadius(new JsDoubleFunction() {
                    public double f(JsArgs args) {
                        return args.<int[]> getObject()[2];
                    }
                }).verticalRadius(new JsDoubleFunction() {
                    public double f(JsArgs args) {
                        return args.<int[]> getObject()[3];
                    }
                }).angle(new JsDoubleFunction() {
                    public double f(JsArgs args) {
                        return (args.<int[]> getObject()[4] * Math.PI) / 180d;
                    }
                }).fillStyle(new JsFunction<PVColor>() {
                    private PVOrdinalScale colors = PV.Colors.category10();

                    public PVColor f(JsArgs args) {
                        return colors.fcolor(args.getObject()).alpha(0.6);
                    }
                }).strokeStyle("#222").lineWidth(0.5);
    }

    public JsArrayGeneric<int[]> generateData() {
        JsArrayGeneric<int[]> data = JsUtils.createJsArrayGeneric();
        for (int i = 0; i < EXAMPLE_DATA.length; i++) {
            data.push(EXAMPLE_DATA[i]);
        }
        return data;
    }

    @Override
    public String getDescription() {
        return null;
    }

    public String getProtovisExampleURL() {
        return null;
    }

    public String getSourceCodeFile() {
        return "EllipseExample.java";
    }

    protected void onAttach() {
        super.onAttach();
        initPVPanel();
        createVisualization(generateData());
        getPVPanel().render();
    }

    public String toString() {
        return "New: Ellipse (Protovis Extension)";
    }

}