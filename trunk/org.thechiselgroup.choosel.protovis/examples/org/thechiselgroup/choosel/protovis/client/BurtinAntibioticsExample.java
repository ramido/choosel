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
import static org.thechiselgroup.choosel.protovis.client.PVAlignment.LEFT;
import static org.thechiselgroup.choosel.protovis.client.PVAlignment.MIDDLE;
import static org.thechiselgroup.choosel.protovis.client.PVAlignment.OUTER;
import static org.thechiselgroup.choosel.protovis.client.PVAlignment.RIGHT;
import static org.thechiselgroup.choosel.protovis.client.PVAlignment.TOP;

import java.util.HashMap;
import java.util.Map;

import org.thechiselgroup.choosel.protovis.client.jsutil.JsArgs;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsBooleanFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsDoubleFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsStringFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsUtils;

import com.google.gwt.user.client.ui.Widget;

/**
 * Protovis/GWT implementation of <a
 * href="http://vis.stanford.edu/protovis/ex/antibiotics-burtin.html">Burtin's
 * antibiotics example</a>.
 * 
 * @author Lars Grammel
 */
public class BurtinAntibioticsExample extends ProtovisWidget implements
        ProtovisExample {

    public static class Antibiotics {

        private String bacteria;

        private double penicillin;

        private double streptomycin;

        private double neomycin;

        private String gram;

        private Antibiotics(String bacteria, double penicillin,
                double streptomycin, double neomycin, String gram) {

            this.bacteria = bacteria;
            this.penicillin = penicillin;
            this.streptomycin = streptomycin;
            this.neomycin = neomycin;
            this.gram = gram;
        }
    }

    @Override
    public Widget asWidget() {
        return this;
    }

    private double radius(double mic, double a, double b) {
        return a * Math.sqrt(Math.log(mic * 1E4)) + b;
    }

    private void createVisualization(Antibiotics[] antibiotics) {
        /* Basic dimensions. */
        int width = 700;
        final int height = 700;
        int innerRadius = 90;
        int outerRadius = 300 - 10;

        /* Colors. */
        final Map<String, String> drugColor = new HashMap<String, String>();
        drugColor.put("Penicillin", "rgb(10, 50, 100)");
        drugColor.put("Streptomycin", "rgb(200, 70, 50)");
        drugColor.put("Neomycin", "black");

        final Map<String, String> gramColor = new HashMap<String, String>();
        gramColor.put("positive", "rgba(174, 174, 184, .8)");
        gramColor.put("negative", "rgba(230, 130, 110, .8)");

        /* Burtin's radius encoding is, as far as I can tell, sqrt(log(mic)). */
        double min = Math.sqrt(Math.log(.001 * 1E4));
        double max = Math.sqrt(Math.log(1000 * 1E4));
        final double a = (outerRadius - innerRadius) / (min - max);
        final double b = innerRadius - a * max;

        /*
         * The pie is split into equal sections for each bacteria, with a blank
         * section at the top for the grid labels. Each wedge is further
         * subdivided to make room for the three antibiotics, equispaced.
         */
        final double bigAngle = 2.0 * Math.PI / (antibiotics.length + 1);
        final double smallAngle = bigAngle / 7;

        /* The root panel. */
        PVPanel vis = getPVPanel().width(width).height(height).bottom(100);

        /* Background wedges to indicate gram staining color. */
        PVWedge bg = vis.add(PV.Wedge)
                .data(antibiotics)
                // assumes Burtin's order
                .left(width / 2).top(height / 2).innerRadius(innerRadius)
                .outerRadius(outerRadius).angle(bigAngle)
                .startAngle(new JsDoubleFunction() {
                    public double f(JsArgs args) {
                        PVMark _this = args.getThis();
                        return _this.index() * bigAngle + bigAngle / 2
                                - Math.PI / 2;
                    }
                }).fillStyle(new JsStringFunction() {
                    public String f(JsArgs args) {
                        Antibiotics d = args.getObject(0);
                        return gramColor.get(d.gram);
                    }
                });

        /* Antibiotics. */
        bg.add(PV.Wedge).angle(smallAngle).startAngle(new JsDoubleFunction() {
            public double f(JsArgs args) {
                PVWedge _this = args.getThis();
                return _this.proto().startAngle() + smallAngle;
            }
        }).outerRadius(new JsDoubleFunction() {
            public double f(JsArgs args) {
                Antibiotics d = args.getObject();
                return radius(d.penicillin, a, b);
            }
        }).fillStyle(drugColor.get("Penicillin")).add(PV.Wedge)
                .startAngle(new JsDoubleFunction() {
                    public double f(JsArgs args) {
                        PVWedge _this = args.getThis();
                        return _this.proto().startAngle() + 2 * smallAngle;
                    }
                }).outerRadius(new JsDoubleFunction() {
                    public double f(JsArgs args) {
                        Antibiotics d = args.getObject();
                        return radius(d.streptomycin, a, b);
                    }
                }).fillStyle(drugColor.get("Streptomycin")).add(PV.Wedge)
                .outerRadius(new JsDoubleFunction() {
                    public double f(JsArgs args) {
                        Antibiotics d = args.getObject();
                        return radius(d.neomycin, a, b);
                    }
                }).fillStyle(drugColor.get("Neomycin"));

        /* Circular grid lines. */
        bg.add(PV.Dot).data(PV.range(-3, 4)).fillStyle((String) null)
                .strokeStyle("#eee").lineWidth(1).size(new JsDoubleFunction() {
                    public double f(JsArgs args) {
                        int i = args.getInt();
                        return Math.pow(radius(Math.pow(10, i), a, b), 2);
                    }
                }).anchor(TOP).add(PV.Label).visible(new JsBooleanFunction() {
                    public boolean f(JsArgs args) {
                        int i = args.getInt();
                        return i < 3;
                    }
                }).textBaseline(MIDDLE).text(new JsStringFunction() {
                    public String f(JsArgs args) {
                        int i = args.getInt();
                        return JsUtils.toFixed(Math.pow(10, i), (i > 0) ? 0
                                : -i);
                    }
                });

        /* Radial grid lines. */
        bg.add(PV.Wedge).data(PV.range(antibiotics.length + 1))
                .innerRadius(innerRadius - 10).outerRadius(outerRadius + 10)
                .fillStyle((String) null).strokeStyle("black").angle(0);

        /* Labels. */
        bg.anchor(OUTER).add(PV.Label).textAlign(CENTER)
                .text(new JsStringFunction() {
                    public String f(JsArgs args) {
                        Antibiotics d = args.getObject();
                        return d.bacteria;
                    }
                });

        /* Antibiotic legend. */
        vis.add(PV.Bar).data(drugColor.keySet()).right(width / 2 + 3)
                .top(new JsDoubleFunction() {
                    public double f(JsArgs args) {
                        PVMark _this = args.getThis();
                        return height / 2 - 28 + _this.index() * 18;
                    }
                }).fillStyle(new JsStringFunction() {
                    public String f(JsArgs args) {
                        String d = args.getObject();
                        return drugColor.get(d);
                    }
                }).width(36).height(12).anchor(RIGHT).add(PV.Label)
                .textMargin(6).textAlign(LEFT);

        /* Gram-stain legend. */
        vis.add(PV.Dot).data(gramColor.keySet()).left(width / 2 - 20)
                .bottom(new JsDoubleFunction() {
                    public double f(JsArgs args) {
                        PVMark _this = args.getThis();
                        return -60 + _this.index() * 18;
                    }
                }).fillStyle(new JsStringFunction() {
                    public String f(JsArgs args) {
                        String d = args.getObject();
                        return gramColor.get(d);
                    }
                }).strokeStyle((String) null).size(30).anchor(RIGHT)
                .add(PV.Label).textMargin(6).textAlign(LEFT)
                .text(new JsStringFunction() {
                    public String f(JsArgs args) {
                        String d = args.getObject();
                        return "Gram-" + d;
                    }
                });
    }

    private Antibiotics[] generateData() {
        return new Antibiotics[] {
                new Antibiotics("Mycobacterium tuberculosis", 800, 5, 2,
                        "negative"),
                new Antibiotics("Salmonella schottmuelleri", 10d, 0.8, 0.09,
                        "negative"),
                new Antibiotics("Proteus vulgaris", 3, 0.1, 0.1, "negative"),
                new Antibiotics("Klebsiella pneumoniae", 850, 1.2, 1,
                        "negative"),
                new Antibiotics("Brucella abortus", 1, 2, 0.02, "negative"),
                new Antibiotics("Pseudomonas aeruginosa", 850, 2, 0.4,
                        "negative"),
                new Antibiotics("Escherichia coli", 100, 0.4, 0.1, "negative"),
                new Antibiotics("Salmonella (Eberthella) typhosa", 1, 0.4,
                        0.008, "negative"),
                new Antibiotics("Aerobacter aerogenes", 870, 1, 1.6, "negative"),
                new Antibiotics("Brucella antracis", 0.001, 0.01, 0.007,
                        "positive"),
                new Antibiotics("Streptococcus fecalis", 1, 1, 0.1, "positive"),
                new Antibiotics("Staphylococcus aureus", 0.03, 0.03, 0.001,
                        "positive"),
                new Antibiotics("Staphylococcus albus", 0.007, 0.1, 0.001,
                        "positive"),
                new Antibiotics("Streptococcus hemolyticus", 0.001, 14, 10,
                        "positive"),
                new Antibiotics("Streptococcus viridans", 0.005, 10, 40,
                        "positive"),
                new Antibiotics("Diplococcus pneumoniae", 0.005, 11, 10,
                        "positive") };
    }

    public String getProtovisExampleURL() {
        return "http://vis.stanford.edu/protovis/ex/antibiotics-burtin.html";
    }

    public String getSourceCodeFile() {
        return "BurtinAntibioticsExample.java";
    }

    protected void onAttach() {
        super.onAttach();
        initPVPanel();
        createVisualization(generateData());
        getPVPanel().render();
    }

    public String toString() {
        return "Burtin's Antibiotics";
    }

}