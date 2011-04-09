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
import static org.thechiselgroup.choosel.protovis.client.PVAlignment.CENTER;
import static org.thechiselgroup.choosel.protovis.client.PVAlignment.RIGHT;
import static org.thechiselgroup.choosel.protovis.client.PVAlignment.TOP;
import static org.thechiselgroup.choosel.protovis.client.PVInterpolationMethod.STEP_AFTER;

import org.thechiselgroup.choosel.protovis.client.jsutil.JsArgs;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsArrayGeneric;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsBooleanFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsDoubleFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsStringFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsUtils;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Widget;

/**
 * Protovis/GWT implementation of <a
 * href="http://vis.stanford.edu/protovis/ex/wheat.html">Protovis Playfair's
 * Wheat example</a>.
 * 
 * @author Lars Grammel
 */
public class PlayfairsWheatExample extends ProtovisWidgetWithAnnotations
        implements ProtovisExample {

    public static class Monarch {

        public String name;

        public int start;

        public int end;

        public boolean commonwealth;

        private Monarch(String name, int start, int end) {
            this(name, start, end, false);
        }

        private Monarch(String name, int start, int end, boolean commonwealth) {
            this.name = name;
            this.start = start;
            this.end = end;
            this.commonwealth = commonwealth;
        }

    }

    public static class WheatPrice {

        public int year;

        public double wheat;

        public double wages;

        private WheatPrice(int year, double wheat, double wages) {
            this.year = year;
            this.wheat = wheat;
            this.wages = wages;
        }

    }

    private void addDescriptions() {
        Element div = addDescriptionElement(70, 200,
                "<center><large style='font-size: medium'>"
                        + "<b>CHART</b>,<br>Shewing at One View<br/>"
                        + "The Price of The Quarter of Wheat,"
                        + "</large><br/>"
                        + " &amp; Wages of Labour by the Week,<br/>"
                        + "from The Year 1565 to 1821,<br/>"
                        + "by WILLIAM PLAYFAIR</center>", null);
        div.getStyle().setPadding(10, Unit.PX);
    }

    @Override
    public Widget asWidget() {
        return this;
    }

    private void createVisualization(final WheatPrice[] wheat, Monarch[] monarch) {
        int w = 860 - 60;
        int h = 465 - 20;
        final PVLinearScale x = PV.Scale.linear(1565, 1821).range(0, w);
        final PVLinearScale y = PV.Scale.linear(0, 100).range(0, h);

        PVPanel vis = getPVPanel().width(w).height(h).right(60).bottom(20);

        /* Price of The Quarter of Wheat. */
        vis.add(PV.Area).data(wheat).interpolate(STEP_AFTER).bottom(0)
                .height(new JsDoubleFunction() {
                    public double f(JsArgs args) {
                        WheatPrice d = args.getObject();
                        return y.fd(d.wheat);
                    }
                }).left(new JsDoubleFunction() {
                    public double f(JsArgs args) {
                        WheatPrice d = args.getObject();
                        return x.fd(d.year);
                    }
                }).fillStyle("#aaa").strokeStyle("#999").add(PV.Rule);

        /* Weekly Wages of a Good Mechanic. */
        vis.add(PV.Area)
                .data(new JsFunction<JsArrayGeneric<WheatPrice>>() {
                    public JsArrayGeneric<WheatPrice> f(JsArgs args) {
                        JsArrayGeneric<WheatPrice> array = JsUtils
                                .createJsArrayGeneric();
                        for (WheatPrice w : wheat) {
                            if (!Double.isNaN(w.wages)) {
                                array.push(w);
                            }
                        }
                        return array;
                    }
                }).left(new JsDoubleFunction() {
                    public double f(JsArgs args) {
                        WheatPrice d = args.getObject();
                        return x.fd(d.year);
                    }
                }).bottom(0).height(new JsDoubleFunction() {
                    public double f(JsArgs args) {
                        WheatPrice d = args.getObject();
                        return y.fd(d.wages);
                    }
                }).fillStyle("hsla(195, 50%, 80%, .75)").anchor(TOP)
                .add(PV.Line)
                .fillStyle((String) null).lineWidth(4)
                .strokeStyle("lightcoral").add(PV.Line)
                .top(new JsDoubleFunction() {
                    public double f(JsArgs args) {
                        PVLine _this = args.getThis();
                        return _this.proto().top() + 1.5;
                    }
                }).lineWidth(1.5).strokeStyle("black");

        vis.add(PV.Label).left(130).bottom(31).font("italic 10px serif")
                .text("Weekly Wages of a Good Mechanic");

        /* Y-axis. */
        vis.add(PV.Rule).bottom(-.5).add(PV.Rule).data(PV.range(0, 100, 10))
                .bottom(y).strokeStyle("rgba(255, 255, 255, .2)").anchor(RIGHT)
                .add(PV.Label).visible(new JsBooleanFunction() {
                    public boolean f(JsArgs args) {
                        PVMark _this = args.getThis();
                        return _this.index() % 2 == 0;
                    }
                }).text(new JsStringFunction() {
                    public String f(JsArgs args) {
                        int s = args.getInt();
                        return s + (s != 0 ? "" : " shillings");
                    }
                });

        /* X-axis. */
        vis.add(PV.Rule).data(PV.range(1560, 1830, 10)).bottom(0).left(x)
                .height(-4).add(PV.Rule).data(PV.range(1600, 1850, 50))
                .bottom(0).height(h).strokeStyle("rgba(0, 0, 0, .2)")
                .anchor(BOTTOM).add(PV.Label).textMargin(8);

        /* Monarchs. */
        vis.add(PV.Bar).data(monarch).height(5).top(new JsDoubleFunction() {
            public double f(JsArgs args) {
                PVMark _this = args.getThis();
                Monarch d = args.getObject();
                return (!d.commonwealth && (_this.index() % 2 == 1)) ? 15 : 10;
            }
        }).fillStyle(new JsStringFunction() {
            public String f(JsArgs args) {
                Monarch d = args.getObject();
                return d.commonwealth ? null : "#000";
            }
        }).strokeStyle("#000").left(new JsDoubleFunction() {
            public double f(JsArgs args) {
                Monarch d = args.getObject();
                return x.fd(d.start);
            }
        }).width(new JsDoubleFunction() {
            public double f(JsArgs args) {
                Monarch d = args.getObject();
                return x.fd(d.end) - x.fd(d.start);
            }
        }).anchor(CENTER).add(PV.Label).textBaseline(TOP).textMargin(6)
                .font("italic 10px serif").text(new JsStringFunction() {
                    public String f(JsArgs args) {
                        Monarch d = args.getObject();
                        return d.name;
                    }
                });
    }

    @Override
    public String getDescription() {
        return null;
    }

    public String getProtovisExampleURL() {
        return "http://vis.stanford.edu/protovis/ex/wheat.html";
    }

    public String getSourceCodeFile() {
        return "PlayfairsWheatExample.java";
    }

    private Monarch[] monarchData() {
        return new Monarch[] { new Monarch("Elizabeth", 1565, 1603),
                new Monarch("James I", 1603, 1625),
                new Monarch("Charles I", 1625, 1649),
                new Monarch("Cromwell", 1649, 1660, true),
                new Monarch("Charles II", 1660, 1685),
                new Monarch("James II", 1685, 1689),
                new Monarch("W&M", 1689, 1702),
                new Monarch("Anne", 1702, 1714),
                new Monarch("George I", 1714, 1727),
                new Monarch("George II", 1727, 1760),
                new Monarch("George III", 1760, 1820),
                new Monarch("George IV", 1820, 1821) };
    }

    protected void onAttach() {
        super.onAttach();
        getElement().getStyle()
                .setProperty("font", "oblique small baskerville");
        initPVPanel();
        createVisualization(wheatData(), monarchData());
        getPVPanel().render();
        addDescriptions();
    }

    public String toString() {
        return "Playfair's Wheat";
    }

    private WheatPrice[] wheatData() {
        return new WheatPrice[] { new WheatPrice(1565, 41, 5),
                new WheatPrice(1570, 45, 5.05), new WheatPrice(1575, 42, 5.08),
                new WheatPrice(1580, 49, 5.12),
                new WheatPrice(1585, 41.5, 5.15),
                new WheatPrice(1590, 47, 5.25), new WheatPrice(1595, 64, 5.54),
                new WheatPrice(1600, 27, 5.61), new WheatPrice(1605, 33, 5.69),
                new WheatPrice(1610, 32, 5.78), new WheatPrice(1615, 33, 5.94),
                new WheatPrice(1620, 35, 6.01), new WheatPrice(1625, 33, 6.12),
                new WheatPrice(1630, 45, 6.22), new WheatPrice(1635, 33, 6.3),
                new WheatPrice(1640, 39, 6.37), new WheatPrice(1645, 53, 6.45),
                new WheatPrice(1650, 42, 6.5), new WheatPrice(1655, 40.5, 6.6),
                new WheatPrice(1660, 46.5, 6.75),
                new WheatPrice(1665, 32, 6.8), new WheatPrice(1670, 37, 6.9),
                new WheatPrice(1675, 43, 7), new WheatPrice(1680, 35, 7.3),
                new WheatPrice(1685, 27, 7.6), new WheatPrice(1690, 40, 8),
                new WheatPrice(1695, 50, 8.5), new WheatPrice(1700, 30, 9),
                new WheatPrice(1705, 32, 10), new WheatPrice(1710, 44, 11),
                new WheatPrice(1715, 33, 11.75),
                new WheatPrice(1720, 29, 12.5), new WheatPrice(1725, 39, 13),
                new WheatPrice(1730, 26, 13.3), new WheatPrice(1735, 32, 13.6),
                new WheatPrice(1740, 27, 14), new WheatPrice(1745, 27.5, 14.5),
                new WheatPrice(1750, 31, 15), new WheatPrice(1755, 35.5, 15.7),
                new WheatPrice(1760, 31, 16.5), new WheatPrice(1765, 43, 17.6),
                new WheatPrice(1770, 47, 18.5), new WheatPrice(1775, 44, 19.5),
                new WheatPrice(1780, 46, 21), new WheatPrice(1785, 42, 23),
                new WheatPrice(1790, 47.5, 25.5),
                new WheatPrice(1795, 76, 27.5), new WheatPrice(1800, 79, 28.5),
                new WheatPrice(1805, 81, 29.5), new WheatPrice(1810, 99, 30),
                new WheatPrice(1815, 78, Double.NaN),
                new WheatPrice(1820, 54, Double.NaN),
                new WheatPrice(1821, 54, Double.NaN) };
    }
}