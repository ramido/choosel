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
import static org.thechiselgroup.choosel.protovis.client.PVInterpolationMethod.CARDINAL;

import org.thechiselgroup.choosel.protovis.client.jsutil.JsArgs;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsDoubleFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsStringFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsUtils;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Widget;

/**
 * Protovis/GWT implementation of <a
 * href="http://vis.stanford.edu/protovis/ex/driving.html">Protovis gas and
 * driving example</a>.
 * 
 * @author Lars Grammel
 */
public class GasAndDrivingExample extends ProtovisWidgetWithAnnotations
        implements ProtovisExample {

    private static final String CSS_CLASS = "gasAndDrivingExample-caption";

    public static class DrivingStats {

        public String side;

        public int year;

        public int miles;

        public double gas;

        private DrivingStats(String side, int year, int miles, double gas) {
            this.side = side;
            this.year = year;
            this.miles = miles;
            this.gas = gas;
        }

    }

    @Override
    public Widget asWidget() {
        return this;
    }

    private void createVisualization(DrivingStats[] driving) {
        int w = 900;
        int h = 590;
        final PVLinearScale x = PVScale.linear(3380, 10500).range(0, w);
        final PVLinearScale y = PVScale.linear(1.25, 3.49).range(0, h);

        PVPanel vis = getPVPanel().width(w).height(h).top(10);

        vis.add(PV.Rule).data(y.ticks(5)).bottom(y).strokeStyle("#ccc")
                .anchor(LEFT).add(PV.Label).font("bold 12px sans-serif")
                .textMargin(6).textAlign(LEFT).textBaseline(BOTTOM)
                .text(new JsStringFunction() {
                    public String f(JsArgs args) {
                        double d = args.getDouble(0);
                        return "$" + JsUtils.toFixed(d, 2);
                    }
                });

        vis.add(PV.Rule).data(x.ticks(5)).left(x).strokeStyle("#ccc")
                .anchor(TOP).add(PV.Label).font("bold 12px sans-serif")
                .textMargin(6).textAlign(LEFT).textBaseline(TOP)
                .text(new JsStringFunction() {
                    public String f(JsArgs args) {
                        double d = args.getDouble(0);
                        return PVFormat.number().format(d) + " mi";
                    }
                });

        vis.add(PV.Line).data(driving).interpolate(CARDINAL).lineWidth(4)
                .strokeStyle("black").left(new JsDoubleFunction() {
                    public double f(JsArgs args) {
                        DrivingStats d = args.getObject(0);
                        return x.fd(d.miles);
                    }
                }).bottom(new JsDoubleFunction() {
                    public double f(JsArgs args) {
                        DrivingStats d = args.getObject(0);
                        return y.fd(d.gas);
                    }
                }).add(PV.Dot).lineWidth(1).fillStyle("white")
                .anchor(new JsStringFunction() {
                    public String f(JsArgs args) {
                        DrivingStats d = args.getObject(0);
                        return d.side;
                    }
                }).add(PV.Label).text(new JsStringFunction() {
                    public String f(JsArgs args) {
                        DrivingStats d = args.getObject(0);
                        return "" + d.year;
                    }
                });
    }

    private void addDescriptions() {
        addDescriptionElement(320, 150, "Cheap gas,<br/>longer commutes",
                CSS_CLASS);
        addDescriptionElement(350, 350, "The Arab<br/>oil embargo", CSS_CLASS);
        addDescriptionElement(100, 440, "Energy crisis</p>", CSS_CLASS);
        addDescriptionElement(430, 670, "Record low prices", CSS_CLASS);

        Element div = addDescriptionElement(
                86,
                660,
                "The swing backward"
                        + "<p>The average number of miles that Americans drive annually beings to"
                        + "fall, so the chart appears to turn around.</p>",
                CSS_CLASS);
        div.getStyle().setWidth(110, Unit.PX);
        div.getStyle().setPadding(10, Unit.PX);
    }

    /**
     * This data was extracted from the New York Times article "Driving Shifts
     * Into Reverse" by Hannah Fairfield, published on May 2, 2010. The original
     * sources are the Energy Information Administration, the Federal Highway
     * Administration, and the Brookings Institution. Due to error in
     * extraction, these figures may not be accurate.
     * 
     * @see http://www.nytimes.com/imagepages/2010/05/02/business/02metrics.html
     */
    private DrivingStats[] generateData() {
        return new DrivingStats[] { new DrivingStats("left", 1956, 3675, 2.38),
                new DrivingStats("right", 1957, 3706, 2.40),
                new DrivingStats("bottom", 1958, 3766, 2.26),
                new DrivingStats("top", 1959, 3905, 2.31),
                new DrivingStats("right", 1960, 3935, 2.27),
                new DrivingStats("bottom", 1961, 3977, 2.25),
                new DrivingStats("right", 1962, 4085, 2.22),
                new DrivingStats("left", 1963, 4218, 2.12),
                new DrivingStats("bottom", 1964, 4369, 2.11),
                new DrivingStats("bottom", 1965, 4538, 2.14),
                new DrivingStats("top", 1966, 4676, 2.14),
                new DrivingStats("bottom", 1967, 4827, 2.14),
                new DrivingStats("right", 1968, 5038, 2.13),
                new DrivingStats("right", 1969, 5207, 2.07),
                new DrivingStats("right", 1970, 5376, 2.01),
                new DrivingStats("bottom", 1971, 5617, 1.93),
                new DrivingStats("bottom", 1972, 5973, 1.87),
                new DrivingStats("right", 1973, 6154, 1.90),
                new DrivingStats("left", 1974, 5943, 2.34),
                new DrivingStats("bottom", 1975, 6111, 2.31),
                new DrivingStats("bottom", 1976, 6389, 2.32),
                new DrivingStats("top", 1977, 6630, 2.36),
                new DrivingStats("bottom", 1978, 6883, 2.23),
                new DrivingStats("left", 1979, 6744, 2.68),
                new DrivingStats("left", 1980, 6672, 3.30),
                new DrivingStats("right", 1981, 6732, 3.30),
                new DrivingStats("right", 1982, 6835, 2.92),
                new DrivingStats("right", 1983, 6943, 2.66),
                new DrivingStats("right", 1984, 7130, 2.48),
                new DrivingStats("right", 1985, 7323, 2.36),
                new DrivingStats("left", 1986, 7558, 1.76),
                new DrivingStats("top", 1987, 7770, 1.76),
                new DrivingStats("bottom", 1988, 8089, 1.68),
                new DrivingStats("left", 1989, 8397, 1.75),
                new DrivingStats("top", 1990, 8529, 1.88),
                new DrivingStats("right", 1991, 8535, 1.78),
                new DrivingStats("right", 1992, 8662, 1.69),
                new DrivingStats("left", 1993, 8855, 1.60),
                new DrivingStats("bottom", 1994, 8909, 1.59),
                new DrivingStats("bottom", 1995, 9150, 1.60),
                new DrivingStats("top", 1996, 9192, 1.67),
                new DrivingStats("right", 1997, 9416, 1.65),
                new DrivingStats("bottom", 1998, 9590, 1.39),
                new DrivingStats("right", 1999, 9687, 1.50),
                new DrivingStats("top", 2000, 9717, 1.89),
                new DrivingStats("left", 2001, 9699, 1.77),
                new DrivingStats("bottom", 2002, 9814, 1.64),
                new DrivingStats("right", 2003, 9868, 1.86),
                new DrivingStats("left", 2004, 9994, 2.14),
                new DrivingStats("left", 2005, 10067, 2.53),
                new DrivingStats("right", 2006, 10037, 2.79),
                new DrivingStats("right", 2007, 10025, 2.95),
                new DrivingStats("left", 2008, 9880, 3.31),
                new DrivingStats("bottom", 2009, 9657, 2.38),
                new DrivingStats("left", 2010, 9596, 2.61) };
    }

    public String getProtovisExampleURL() {
        return "http://vis.stanford.edu/protovis/ex/driving.html";
    }

    public String getSourceCodeFile() {
        return "GasAndDrivingExample.java";
    }

    protected void onAttach() {
        super.onAttach();
        initPVPanel();
        createVisualization(generateData());
        getPVPanel().render();

        addDescriptions();
    }

    public String toString() {
        return "Gas & Driving";
    }
}