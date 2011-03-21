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

import org.thechiselgroup.choosel.protovis.client.jsutil.JsArgs;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsDoubleFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsStringFunction;

import com.google.gwt.core.client.JsDate;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Widget;

/**
 * Protovis/GWT implementation of <a
 * href="http://vis.stanford.edu/protovis/ex/candlestick.html">Protovis
 * candlestick chart example</a>.
 * 
 * @author Lars Grammel
 */
public class CandlestickChartExample extends ProtovisWidget implements
        ProtovisExample {

    public static class DaySummary {

        public String date;

        public JsDate jsDate;

        public double open;

        public double high;

        public double low;

        public double close;

        private DaySummary(String date, double open, double high, double low,
                double close) {

            this.date = date;
            this.open = open;
            this.high = high;
            this.low = low;
            this.close = close;
        }
    }

    @Override
    public Widget asWidget() {
        return this;
    }

    private void createVisualization(DaySummary[] days) {
        /* Parse dates. */
        DateTimeFormat format = DateTimeFormat.getFormat("dd-MMM-yyyy");
        for (DaySummary d : days) {
            d.jsDate = JsDate.create(format.parse(d.date).getTime());
        }

        /* Scales. */
        int w = 840;
        int h = 200;
        final PVLinearScale x = PV.Scale.linear(days, new JsFunction<JsDate>() {
            public JsDate f(JsArgs args) {
                DaySummary d = args.getObject();
                return d.jsDate;
            }
        }).range(0, w);
        final PVLinearScale y = PV.Scale.linear(days, new JsDoubleFunction() {
            public double f(JsArgs args) {
                DaySummary d = args.getObject();
                return d.low;
            }
        }, new JsDoubleFunction() {
            public double f(JsArgs args) {
                DaySummary d = args.getObject();
                return d.high;
            }
        }).range(0, h).nice();

        PVPanel vis = getPVPanel().width(w).height(h).margin(10).left(30);

        /* Dates. */
        vis.add(PV.Rule).data(x.ticks()).left(x).strokeStyle("#eee")
                .anchor(BOTTOM).add(PV.Label).text(x.tickFormat());

        /* Prices. */
        vis.add(PV.Rule).data(y.ticks(7)).bottom(y).left(-10).right(-10)
                .strokeStyle(new JsStringFunction() {
                    public String f(JsArgs args) {
                        double d = args.getDouble();
                        return d % 10 != 0 ? "#ccc" : "#333";
                    }
                }).anchor(LEFT).add(PV.Label).textStyle(new JsStringFunction() {
                    public String f(JsArgs args) {
                        double d = args.getDouble();
                        return d % 10 != 0 ? "#999" : "#333";
                    }
                }).text(y.tickFormat());

        /* Candlestick. */
        vis.add(PV.Rule).data(days).left(new JsDoubleFunction() {
            public double f(JsArgs args) {
                DaySummary d = args.getObject();
                return x.fd(d.jsDate);
            }
        }).bottom(new JsDoubleFunction() {
            public double f(JsArgs args) {
                DaySummary d = args.getObject();
                return y.fd(Math.min(d.high, d.low));
            }
        }).height(new JsDoubleFunction() {
            public double f(JsArgs args) {
                DaySummary d = args.getObject();
                return Math.abs(y.fd(d.high) - y.fd(d.low));
            }
        }).strokeStyle(new JsStringFunction() {
            public String f(JsArgs args) {
                DaySummary d = args.getObject();
                return d.open < d.close ? "#ae1325" : "#06982d";
            }
        }).add(PV.Rule).bottom(new JsDoubleFunction() {
            public double f(JsArgs args) {
                DaySummary d = args.getObject();
                return y.fd(Math.min(d.open, d.close));
            }
        }).height(new JsDoubleFunction() {
            public double f(JsArgs args) {
                DaySummary d = args.getObject();
                return Math.abs(y.fd(d.open) - y.fd(d.close));
            }
        }).lineWidth(10);
    }

    private DaySummary[] generateData() {
        return new DaySummary[] {
                new DaySummary("01-Jun-2009", 28.7, 30.05, 28.45, 30.04),
                new DaySummary("02-Jun-2009", 30.4, 30.13, 28.3, 29.63),
                new DaySummary("03-Jun-2009", 29.62, 31.79, 29.62, 31.02),
                new DaySummary("04-Jun-2009", 31.02, 31.02, 29.92, 30.18),
                new DaySummary("05-Jun-2009", 29.39, 30.81, 28.85, 29.62),
                new DaySummary("08-Jun-2009", 30.84, 31.82, 26.41, 29.77),
                new DaySummary("09-Jun-2009", 29.77, 29.77, 27.79, 28.27),
                new DaySummary("10-Jun-2009", 26.9, 29.74, 26.9, 28.46),
                new DaySummary("11-Jun-2009", 27.36, 28.11, 26.81, 28.11),
                new DaySummary("12-Jun-2009", 28.08, 28.5, 27.73, 28.15),
                new DaySummary("15-Jun-2009", 29.7, 31.09, 29.64, 30.81),
                new DaySummary("16-Jun-2009", 30.81, 32.75, 30.07, 32.68),
                new DaySummary("17-Jun-2009", 31.19, 32.77, 30.64, 31.54),
                new DaySummary("18-Jun-2009", 31.54, 31.54, 29.6, 30.03),
                new DaySummary("19-Jun-2009", 29.16, 29.32, 27.56, 27.99),
                new DaySummary("22-Jun-2009", 30.4, 32.05, 30.3, 31.17),
                new DaySummary("23-Jun-2009", 31.3, 31.54, 27.83, 30.58),
                new DaySummary("24-Jun-2009", 30.58, 30.58, 28.79, 29.05),
                new DaySummary("25-Jun-2009", 29.45, 29.56, 26.3, 26.36),
                new DaySummary("26-Jun-2009", 27.09, 27.22, 25.76, 25.93),
                new DaySummary("29-Jun-2009", 25.93, 27.18, 25.29, 25.35),
                new DaySummary("30-Jun-2009", 25.36, 27.38, 25.02, 26.35),
                new DaySummary("01-Jul-2009", 25.73, 26.31, 24.8, 26.22),
                new DaySummary("02-Jul-2009", 26.22, 28.62, 26.22, 27.95),
                new DaySummary("06-Jul-2009", 30.32, 30.6, 28.99, 29),
                new DaySummary("07-Jul-2009", 29, 30.94, 28.9, 30.85),
                new DaySummary("08-Jul-2009", 30.85, 33.05, 30.43, 31.3),
                new DaySummary("09-Jul-2009", 30.23, 30.49, 29.28, 29.78),
                new DaySummary("10-Jul-2009", 29.78, 30.34, 28.82, 29.02),
                new DaySummary("13-Jul-2009", 28.36, 29.24, 25.42, 26.31),
                new DaySummary("14-Jul-2009", 26.31, 26.84, 24.99, 25.02),
                new DaySummary("15-Jul-2009", 25.05, 26.06, 23.83, 25.89),
                new DaySummary("16-Jul-2009", 25.96, 26.18, 24.51, 25.42),
                new DaySummary("17-Jul-2009", 25.42, 25.55, 23.88, 24.34),
                new DaySummary("20-Jul-2009", 25.06, 25.42, 24.26, 24.4),
                new DaySummary("21-Jul-2009", 24.28, 25.14, 23.81, 23.87),
                new DaySummary("22-Jul-2009", 24.05, 24.14, 23.24, 23.47),
                new DaySummary("23-Jul-2009", 23.71, 24.05, 23.21, 23.43),
                new DaySummary("24-Jul-2009", 23.87, 23.87, 23, 23.09),
                new DaySummary("27-Jul-2009", 24.06, 24.86, 24.02, 24.28),
                new DaySummary("28-Jul-2009", 24.28, 25.61, 24.28, 25.01),
                new DaySummary("29-Jul-2009", 25.47, 26.18, 25.41, 25.61),
                new DaySummary("30-Jul-2009", 25.4, 25.76, 24.85, 25.4),
                new DaySummary("31-Jul-2009", 25.4, 26.22, 24.93, 25.92) };
    }

    public String getProtovisExampleURL() {
        return "http://vis.stanford.edu/protovis/ex/candlestick.html";
    }

    public String getSourceCodeFile() {
        return "CandlestickChartExample.java";
    }

    protected void onAttach() {
        super.onAttach();
        initPVPanel();
        createVisualization(generateData());
        getPVPanel().render();
    }

    public String toString() {
        return "Candlestick Chart";
    }

}