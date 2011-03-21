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

import org.thechiselgroup.choosel.protovis.client.jsutil.JsArgs;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsBooleanFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsDoubleFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsStringFunction;

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

    private void createVisualization(WeatherRecord[] weatherRecords) {
        final int w = 18;
        final int h = 3;

        PVPanel vis = getPVPanel().width(200).height(250);

        /* Record range. */
        PVBar record = vis.add(PV.Bar).data(weatherRecords)
                .bottom(new JsDoubleFunction() {
                    public double f(JsArgs args) {
                        WeatherRecord d = args.getObject();
                        return d.record.low * h;
                    }
                }).height(new JsDoubleFunction() {
                    public double f(JsArgs args) {
                        WeatherRecord d = args.getObject();
                        return (d.record.high - d.record.low) * h;
                    }
                }).left(new JsDoubleFunction() {
                    public double f(JsArgs args) {
                        PVMark _this = args.getThis();
                        return _this.index() * w;
                    }
                }).width(w - 2).fillStyle("#ccc");

        /* Normal range. */
        record.add(PV.Bar).bottom(new JsDoubleFunction() {
            public double f(JsArgs args) {
                WeatherRecord d = args.getObject(0);
                return d.normal.low * h;
            }
        }).height(new JsDoubleFunction() {
            public double f(JsArgs args) {
                WeatherRecord d = args.getObject(0);
                return (d.normal.high - d.normal.low) * h;
            }
        }).fillStyle("#999");

        /* White grid lines. */
        vis.add(PV.Rule).dataInt(20, 40, 60).bottom(new JsDoubleFunction() {
            public double f(JsArgs args) {
                double d = args.getDouble();
                return d * h + 1;
            }
        }).left(0).right(20).lineWidth(2).strokeStyle("white").anchor(RIGHT)
                .add(PV.Label).text(new JsStringFunction() {
                    public String f(JsArgs args) {
                        int d = args.getInt(0);
                        return d + "\u00b0";
                    }
                });

        /* Actual and forecast range. */
        record.add(PV.Bar).visible(new JsBooleanFunction() {
            public boolean f(JsArgs args) {
                WeatherRecord d = args.getObject();
                return d.hasActual();
            }
        }).bottom(new JsDoubleFunction() {
            public double f(JsArgs args) {
                WeatherRecord d = args.getObject();
                return d.actual.low * h;
            }
        }).height(new JsDoubleFunction() {
            public double f(JsArgs args) {
                WeatherRecord d = args.getObject();
                return (d.actual.high - d.actual.low) * h;
            }
        }).left(new JsDoubleFunction() {
            public double f(JsArgs args) {
                PVMark _this = args.getThis();
                return _this.index() * w + 3;
            }
        }).width(w - 8).fillStyle("black").add(PV.Bar)
                .visible(new JsBooleanFunction() {
                    public boolean f(JsArgs args) {
                        WeatherRecord d = args.getObject();
                        return d.hasForecast();
                    }
                }).bottom(new JsDoubleFunction() {
                    public double f(JsArgs args) {
                        WeatherRecord d = args.getObject();
                        return d.forecast.highMin * h;
                    }
                }).height(new JsDoubleFunction() {
                    public double f(JsArgs args) {
                        WeatherRecord d = args.getObject();
                        return (d.forecast.highMax - d.forecast.highMin) * h;
                    }
                }).add(PV.Bar).bottom(new JsDoubleFunction() {
                    public double f(JsArgs args) {
                        WeatherRecord d = args.getObject();
                        return d.forecast.lowMin * h;
                    }
                }).height(new JsDoubleFunction() {
                    public double f(JsArgs args) {
                        WeatherRecord d = args.getObject();
                        return (d.forecast.lowMax - d.forecast.lowMin) * h;
                    }
                }).add(PV.Bar).bottom(new JsDoubleFunction() {
                    public double f(JsArgs args) {
                        WeatherRecord d = args.getObject();
                        return d.forecast.lowMin * h;
                    }
                }).height(new JsDoubleFunction() {
                    public double f(JsArgs args) {
                        WeatherRecord d = args.getObject();
                        return (d.forecast.highMax - d.forecast.lowMin) * h;
                    }
                }).left(new JsDoubleFunction() {
                    public double f(JsArgs args) {
                        PVMark _this = args.getThis();
                        return _this.index() * w + 3 + Math.floor((w - 8) / 3);
                    }
                }).width(Math.ceil((w - 8) / 3));

        /* Day labels. */
        record.anchor(TOP).add(PV.Label).top(16).text(new JsStringFunction() {
            public String f(JsArgs args) {
                WeatherRecord d = args.getObject(0);
                return d.day;
            }
        });

        /* Title. */
        vis.add(PV.Label).top(0).left(0).textBaseline(TOP)
                .font("bold 10pt Sans-Serif").text("Seattle ");
    }

    private WeatherRecord[] generateData() {
        return new WeatherRecord[] {
                new WeatherRecord("M", new Range(62, 15), new Range(50, 38),
                        new Range(48, 36)),
                new WeatherRecord("T", new Range(62, 23), new Range(50, 38),
                        new Range(50, 40)),
                new WeatherRecord("W", new Range(61, 20), new Range(50, 38),
                        new Range(55, 36)),
                new WeatherRecord("T", new Range(67, 21), new Range(50, 38),
                        new Range(51, 33)),
                new WeatherRecord("F", new Range(61, 23), new Range(50, 38),
                        new Range(50, 30)),
                new WeatherRecord("S", new Range(67, 20), new Range(50, 38),
                        new Forecast(53, 49, 40, 35)),
                new WeatherRecord("S", new Range(63, 23), new Range(50, 39),
                        new Forecast(55, 49, 42, 37)),
                new WeatherRecord("M", new Range(61, 26), new Range(51, 39),
                        new Forecast(53, 49, 43, 40)),
                new WeatherRecord("T", new Range(61, 24), new Range(51, 39),
                        new Forecast(52, 46, 44, 40)),
                new WeatherRecord("W", new Range(63, 20), new Range(51, 39),
                        new Forecast(53, 46, 43, 38)), };
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