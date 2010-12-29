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

import static org.thechiselgroup.choosel.protovis.client.PVAlignment.BOTTOM;
import static org.thechiselgroup.choosel.protovis.client.PVAlignment.LEFT;
import static org.thechiselgroup.choosel.protovis.client.PVAlignment.TOP;

import java.util.Arrays;

import org.thechiselgroup.choosel.protovis.client.functions.PVDoubleFunction;
import org.thechiselgroup.choosel.protovis.client.functions.PVFunction;
import org.thechiselgroup.choosel.protovis.client.functions.PVStringFunction;
import org.thechiselgroup.choosel.protovis.client.functions.PVStringFunctionDoubleArg;
import org.thechiselgroup.choosel.protovis.client.functions.PVStringFunctionWithoutThis;
import org.thechiselgroup.choosel.protovis.client.util.JsArrayGeneric;
import org.thechiselgroup.choosel.protovis.client.util.JsUtils;

import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.user.client.ui.Widget;

/**
 * Protovis/GWT implementation of <a
 * href="http://vis.stanford.edu/protovis/ex/box-and-whisker.html">Protovis
 * box-and-whisker plot example</a>.
 * 
 * @author Lars Grammel
 */
public class BoxAndWhiskerPlotExample extends ProtovisWidget implements
        ProtovisExample {

    public static class Point {

        public double x;

        public double y;

        private Point(double x, double y) {
            this.x = x;
            this.y = y;
        }

    }

    @Override
    public Widget asWidget() {
        return this;
    }

    private void createVisualization(JsArrayGeneric<Experiment> experiments) {
        int w = 860;
        int h = 300;
        final PVOrdinalScale x = PVScale.ordinal(experiments,
                new PVStringFunctionWithoutThis<Experiment>() {
                    public String f(Experiment d) {
                        return d.id;
                    }
                }).splitBanded(0, w, 3d / 5d);
        final PVLinearScale y = PVScale.linear(0, 1).range(0, h);
        double s = x.rangeBand() / 2d;

        PVPanel vis = getPVPanel().width(w).height(h).margin(20);

        /* Add the y-axis rules */
        vis.add(PV.Rule()).data(y.ticks()).bottom(y)
                .strokeStyle(new PVStringFunctionDoubleArg<PVRule>() {
                    public String f(PVRule _this, double d) {
                        return (d == 0 || d == 1) ? "#000" : "#ccc";
                    }
                }).anchor(LEFT).add(PV.Label()).text(y.tickFormat());

        /* Add a panel for each data point */
        PVPanel points = vis.add(PV.Panel()).data(experiments)
                .left(new PVDoubleFunction<PVPanel, Experiment>() {
                    public double f(PVPanel _this, Experiment d) {
                        return x.fd(d.id);
                    }
                }).width(s * 2);

        /* Add the experiment id label */
        points.anchor(BOTTOM).add(PV.Label()).textBaseline(TOP)
                .text(new PVStringFunction<PVLabel, Experiment>() {
                    public String f(PVLabel _this, Experiment d) {
                        return d.id;
                    }
                });

        /* Add the range line */
        points.add(PV.Rule()).left(s)
                .bottom(new PVDoubleFunction<PVRule, Experiment>() {
                    public double f(PVRule _this, Experiment d) {
                        return y.fd(d.min);
                    }
                }).height(new PVDoubleFunction<PVRule, Experiment>() {
                    public double f(PVRule _this, Experiment d) {
                        return y.fd(d.max) - y.fd(d.min);
                    }
                });

        /* Add the min and max indicators */
        points.add(PV.Rule())
                .data(new PVFunction<PVRule, Experiment, JsArrayNumber>() {
                    public JsArrayNumber f(PVRule _this, Experiment d) {
                        return JsUtils.toJsArrayNumber(d.min, d.max);
                    }
                }).bottom(y).left(s / 2).width(s);

        /* Add the upper/lower quartile ranges */
        points.add(PV.Bar()).bottom(new PVDoubleFunction<PVBar, Experiment>() {
            public double f(PVBar _this, Experiment d) {
                return y.fd(d.lq);
            }
        }).height(new PVDoubleFunction<PVBar, Experiment>() {
            public double f(PVBar _this, Experiment d) {
                return y.fd(d.uq) - y.fd(d.lq);
            }
        }).fillStyle(new PVStringFunction<PVBar, Experiment>() {
            public String f(PVBar _this, Experiment d) {
                return d.median > .5 ? "#aec7e8" : "#ffbb78";
            }
        }).strokeStyle("black").lineWidth(1).antialias(false);

        /* Add the median line */
        points.add(PV.Rule()).bottom(
                new PVDoubleFunction<PVRule, Experiment>() {
                    public double f(PVRule _this, Experiment d) {
                        return y.fd(d.median);
                    }
                });
    }

    public static class Experiment {

        public String id;

        public double median;

        public double uq;

        public double lq;

        public double max;

        public double min;

        private Experiment(String id, double median, double uq, double lq,
                double max, double min) {

            this.id = id;
            this.median = median;
            this.uq = uq;
            this.lq = lq;
            this.max = max;
            this.min = min;
        }

    }

    private JsArrayGeneric<Experiment> generateData() {
        JsArrayGeneric<Experiment> experiments = JsUtils.createJsArrayGeneric();
        for (String id : "ABCDEFGHIJKLM".split("")) {
            if (id.length() == 1) {
                double offset = Math.random() * 3d / 4d;
                double[] data = new double[15];
                for (int i = 0; i < data.length; i++) {
                    data[i] = offset + Math.random() / 4d;
                }
                Arrays.sort(data);
                experiments.push(new Experiment(id, data[7], data[11], data[3],
                        data[data.length - 1], data[0]));
            }
        }
        return experiments;
    }

    public String getProtovisExampleURL() {
        return "http://vis.stanford.edu/protovis/ex/box-and-whisker.html";
    }

    public String getSourceCodeFile() {
        return "BoxAndWhiskerPlotExample.java";
    }

    protected void onAttach() {
        super.onAttach();
        initPVPanel();
        createVisualization(generateData());
        getPVPanel().render();
    }

    public String toString() {
        return "Box-and-Whisker Plot";
    }
}