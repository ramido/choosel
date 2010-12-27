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

import static org.thechiselgroup.choosel.protovis.client.PVAlignment.RIGHT;
import static org.thechiselgroup.choosel.protovis.client.PVAlignment.TOP;

import org.thechiselgroup.choosel.protovis.client.functions.PVBooleanFunction;
import org.thechiselgroup.choosel.protovis.client.functions.PVDoubleFunction;
import org.thechiselgroup.choosel.protovis.client.functions.PVDoubleFunctionDoubleArg;
import org.thechiselgroup.choosel.protovis.client.functions.PVStringFunction;
import org.thechiselgroup.choosel.protovis.client.functions.PVStringFunctionIntArg;
import org.thechiselgroup.choosel.protovis.client.util.JsArrayGeneric;
import org.thechiselgroup.choosel.protovis.client.util.JsUtils;

import com.google.gwt.user.client.ui.Widget;

/**
 * Protovis/GWT implementation of <a
 * href="http://vis.stanford.edu/protovis/ex/weather.html">Protovis Seattle
 * weather example</a>.
 * 
 * @author Lars Grammel
 */
public class SeattleWeatherExample extends ProtovisWidget implements
        ProtovisExample {

    public static class Forecast {

        public int highMax;

        public int highMin;

        public int lowMax;

        public int lowMin;

        private Forecast(int highMax, int highMin, int lowMax, int lowMin) {
            this.highMax = highMax;
            this.highMin = highMin;
            this.lowMax = lowMax;
            this.lowMin = lowMin;
        }

    }

    public static class Range {

        public int high;

        public int low;

        private Range(int high, int low) {
            this.high = high;
            this.low = low;
        }

    }

    public static class WeatherRecord {

        public String day;

        public Range record;

        public Range normal;

        public Range actual;

        public Forecast forecast;

        private WeatherRecord(String day, Range record, Range normal,
                Forecast forecast) {

            this.day = day;
            this.record = record;
            this.normal = normal;
            this.forecast = forecast;
        }

        private WeatherRecord(String day, Range record, Range normal,
                Range actual) {

            this.day = day;
            this.record = record;
            this.normal = normal;
            this.actual = actual;
        }

        public boolean hasActual() {
            return actual != null;
        }

        public boolean hasForecast() {
            return forecast != null;
        }

    }

    @Override
    public Widget asWidget() {
        return this;
    }

    private void createVisualization(JsArrayGeneric<WeatherRecord> weather) {
        final int w = 18;
        final int h = 3;

        PVPanel vis = getPVPanel().width(200).height(250);

        /* Record range. */
        PVBar record = vis.add(PV.Bar()).data(weather)
                .bottom(new PVDoubleFunction<PVBar, WeatherRecord>() {
                    @Override
                    public double f(PVBar _this, WeatherRecord d) {
                        return d.record.low * h;
                    }
                }).height(new PVDoubleFunction<PVBar, WeatherRecord>() {
                    @Override
                    public double f(PVBar _this, WeatherRecord d) {
                        return (d.record.high - d.record.low) * h;
                    }
                }).left(new PVDoubleFunction<PVBar, WeatherRecord>() {
                    @Override
                    public double f(PVBar _this, WeatherRecord d) {
                        return _this.index() * w;
                    }
                }).width(w - 2).fillStyle("#ccc");

        /* Normal range. */
        record.add(PV.Bar())
                .bottom(new PVDoubleFunction<PVBar, WeatherRecord>() {
                    @Override
                    public double f(PVBar _this, WeatherRecord d) {
                        return d.normal.low * h;
                    }
                }).height(new PVDoubleFunction<PVBar, WeatherRecord>() {
                    @Override
                    public double f(PVBar _this, WeatherRecord d) {
                        return (d.normal.high - d.normal.low) * h;
                    }
                }).fillStyle("#999");

        /* White grid lines. */
        vis.add(PV.Rule()).dataInt(20, 40, 60)
                .bottom(new PVDoubleFunctionDoubleArg<PVRule>() {
                    @Override
                    public double f(PVRule _this, double d) {
                        return d * h + 1;
                    }
                }).left(0).right(20).lineWidth(2).strokeStyle("white")
                .anchor(RIGHT).add(PV.Label())
                .text(new PVStringFunctionIntArg<PVLabel>() {
                    @Override
                    public String f(PVLabel _this, int d) {
                        return d + "\u00b0";
                    }
                });

        /* Actual and forecast range. */
        record.add(PV.Bar())
                .visible(new PVBooleanFunction<PVBar, WeatherRecord>() {
                    @Override
                    public boolean f(PVBar _this, WeatherRecord d) {
                        return d.hasActual();
                    }
                }).bottom(new PVDoubleFunction<PVBar, WeatherRecord>() {
                    @Override
                    public double f(PVBar _this, WeatherRecord d) {
                        return d.actual.low * h;
                    }
                }).height(new PVDoubleFunction<PVBar, WeatherRecord>() {
                    @Override
                    public double f(PVBar _this, WeatherRecord d) {
                        return (d.actual.high - d.actual.low) * h;
                    }
                }).left(new PVDoubleFunction<PVBar, WeatherRecord>() {
                    @Override
                    public double f(PVBar _this, WeatherRecord d) {
                        return _this.index() * w + 3;
                    }
                }).width(w - 8).fillStyle("black").add(PV.Bar())
                .visible(new PVBooleanFunction<PVBar, WeatherRecord>() {
                    @Override
                    public boolean f(PVBar _this, WeatherRecord d) {
                        return d.hasForecast();
                    }
                }).bottom(new PVDoubleFunction<PVBar, WeatherRecord>() {
                    @Override
                    public double f(PVBar _this, WeatherRecord d) {
                        return d.forecast.highMin * h;
                    }
                }).height(new PVDoubleFunction<PVBar, WeatherRecord>() {
                    @Override
                    public double f(PVBar _this, WeatherRecord d) {
                        return (d.forecast.highMax - d.forecast.highMin) * h;
                    }
                }).add(PV.Bar())
                .bottom(new PVDoubleFunction<PVBar, WeatherRecord>() {
                    @Override
                    public double f(PVBar _this, WeatherRecord d) {
                        return d.forecast.lowMin * h;
                    }
                }).height(new PVDoubleFunction<PVBar, WeatherRecord>() {
                    @Override
                    public double f(PVBar _this, WeatherRecord d) {
                        return (d.forecast.lowMax - d.forecast.lowMin) * h;
                    }
                }).add(PV.Bar())
                .bottom(new PVDoubleFunction<PVBar, WeatherRecord>() {
                    @Override
                    public double f(PVBar _this, WeatherRecord d) {
                        return d.forecast.lowMin * h;
                    }
                }).height(new PVDoubleFunction<PVBar, WeatherRecord>() {
                    @Override
                    public double f(PVBar _this, WeatherRecord d) {
                        return (d.forecast.highMax - d.forecast.lowMin) * h;
                    }
                }).left(new PVDoubleFunction<PVBar, WeatherRecord>() {
                    @Override
                    public double f(PVBar _this, WeatherRecord d) {
                        return _this.index() * w + 3 + Math.floor((w - 8) / 3);
                    }
                }).width(Math.ceil((w - 8) / 3));

        /* Day labels. */
        record.anchor(TOP).add(PV.Label()).top(16)
                .text(new PVStringFunction<PVLabel, WeatherRecord>() {
                    @Override
                    public String f(PVLabel _this, WeatherRecord d) {
                        return d.day;
                    }
                });

        /* Title. */
        vis.add(PV.Label()).top(0).left(0).textBaseline(TOP)
                .font("bold 10pt Sans-Serif").text("Seattle ");
    }

    private JsArrayGeneric<WeatherRecord> generateData() {
        JsArrayGeneric<WeatherRecord> weather = JsUtils.createJsArrayGeneric();
        weather.push(new WeatherRecord("M", new Range(62, 15),
                new Range(50, 38), new Range(48, 36)));
        weather.push(new WeatherRecord("T", new Range(62, 23),
                new Range(50, 38), new Range(50, 40)));
        weather.push(new WeatherRecord("W", new Range(61, 20),
                new Range(50, 38), new Range(55, 36)));
        weather.push(new WeatherRecord("T", new Range(67, 21),
                new Range(50, 38), new Range(51, 33)));
        weather.push(new WeatherRecord("F", new Range(61, 23),
                new Range(50, 38), new Range(50, 30)));
        weather.push(new WeatherRecord("S", new Range(67, 20),
                new Range(50, 38), new Forecast(53, 49, 40, 35)));
        weather.push(new WeatherRecord("S", new Range(63, 23),
                new Range(50, 39), new Forecast(55, 49, 42, 37)));
        weather.push(new WeatherRecord("M", new Range(61, 26),
                new Range(51, 39), new Forecast(53, 49, 43, 40)));
        weather.push(new WeatherRecord("T", new Range(61, 24),
                new Range(51, 39), new Forecast(52, 46, 44, 40)));
        weather.push(new WeatherRecord("W", new Range(63, 20),
                new Range(51, 39), new Forecast(53, 46, 43, 38)));
        return weather;
    }

    public String getProtovisExampleURL() {
        return "http://vis.stanford.edu/protovis/ex/weather.html";
    }

    public String getSourceCodeFile() {
        return "SeattleWeatherExample.java";
    }

    protected void onAttach() {
        super.onAttach();
        initPVPanel();
        createVisualization(generateData());
        getPVPanel().render();
    }

    public String toString() {
        return "Seattle Weather";
    }
}