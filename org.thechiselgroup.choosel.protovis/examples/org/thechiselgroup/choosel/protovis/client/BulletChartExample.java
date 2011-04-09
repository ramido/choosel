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
import static org.thechiselgroup.choosel.protovis.client.PVAlignment.RIGHT;
import static org.thechiselgroup.choosel.protovis.client.PVAlignment.TOP;

import org.thechiselgroup.choosel.protovis.client.jsutil.JsArgs;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsArrayGeneric;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsDoubleFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsStringFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsUtils;

import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.user.client.ui.Widget;

/**
 * Protovis/GWT implementation of <a
 * href="http://vis.stanford.edu/protovis/ex/bullet.html">Protovis bullet chart
 * example</a>.
 * 
 * @author Lars Grammel
 */
public class BulletChartExample extends ProtovisWidget implements
        ProtovisExample {

    public static class Bullet {

        public String title;

        public String subtitle;

        public double[] ranges;

        public double[] measures;

        public double[] markers;

        private Bullet(String title, String subtitle, double[] ranges,
                double[] measures, double[] markers) {

            this.title = title;
            this.subtitle = subtitle;
            this.ranges = ranges;
            this.measures = measures;
            this.markers = markers;
        }

    }

    @Override
    public Widget asWidget() {
        return this;
    }

    private void createVisualization(JsArrayGeneric<Bullet> bullets) {
        PVPanel vis = getPVPanel().data(bullets).width(400).height(30)
                .margin(20).left(100).top(new JsDoubleFunction() {
                    public double f(JsArgs args) {
                        PVMark _this = args.getThis();
                        return 10 + _this.index() * 60;
                    }
                });

        PVBulletLayout bullet = vis.add(PV.Layout.Bullet()).orient(LEFT)
                .ranges(new JsFunction<JsArrayNumber>() {
                    public JsArrayNumber f(JsArgs args) {
                        Bullet d = args.getObject();
                        return JsUtils.toJsArrayNumber(d.ranges);
                    }
                }).measures(new JsFunction<JsArrayNumber>() {
                    public JsArrayNumber f(JsArgs args) {
                        Bullet d = args.getObject();
                        return JsUtils.toJsArrayNumber(d.measures);
                    }
                }).markers(new JsFunction<JsArrayNumber>() {
                    public JsArrayNumber f(JsArgs args) {
                        Bullet d = args.getObject();
                        return JsUtils.toJsArrayNumber(d.markers);
                    }
                });

        bullet.range().add(PV.Bar);
        bullet.measure().add(PV.Bar);

        bullet.marker().add(PV.Dot).shape(PVShape.TRIANGLE).fillStyle("white");
        bullet.tick().add(PV.Rule).anchor(BOTTOM).add(PV.Label)
                .text(bullet.x().tickFormat());

        bullet.anchor(LEFT).add(PV.Label).font("bold 12px sans-serif")
                .textAlign(RIGHT).textBaseline(BOTTOM)
                .text(new JsStringFunction() {
                    public String f(JsArgs args) {
                        Bullet d = args.getObject(0);
                        return d.title;
                    }
                });

        bullet.anchor(LEFT).add(PV.Label).textStyle("#666").textAlign(RIGHT)
                .textBaseline(TOP).text(new JsStringFunction() {
                    public String f(JsArgs args) {
                        Bullet d = args.getObject(0);
                        return d.subtitle;
                    }
                });
    }

    private JsArrayGeneric<Bullet> generateData() {
        JsArrayGeneric<Bullet> bullets = JsUtils.createJsArrayGeneric();
        bullets.push(new Bullet("Revenue", "US$, in thousands", new double[] {
                150, 225, 300 }, new double[] { 270d }, new double[] { 250d }));
        bullets.push(new Bullet("Profit", "%", new double[] { 20, 25, 30 },
                new double[] { 23 }, new double[] { 26 }));
        bullets.push(new Bullet("Order Size", "US$, average", new double[] {
                350, 500, 600 }, new double[] { 320 }, new double[] { 550 }));
        bullets.push(new Bullet("New Customers", "count", new double[] { 1400,
                2000, 2500 }, new double[] { 1650 }, new double[] { 2100 }));
        bullets.push(new Bullet("Satisfaction", "out of 5", new double[] { 3.5,
                4.25, 5 }, new double[] { 4.7 }, new double[] { 4.4 }));
        return bullets;
    }

    @Override
    public String getDescription() {
        return null;
    }

    public String getProtovisExampleURL() {
        return "http://vis.stanford.edu/protovis/ex/bullet.html";
    }

    public String getSourceCodeFile() {
        return "BulletChartExample.java";
    }

    protected void onAttach() {
        super.onAttach();
        initPVPanel();
        createVisualization(generateData());
        getPVPanel().render();
    }

    public String toString() {
        return "Bullet Chart";
    }

}