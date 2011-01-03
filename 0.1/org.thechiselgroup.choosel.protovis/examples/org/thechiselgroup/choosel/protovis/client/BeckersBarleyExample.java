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
import static org.thechiselgroup.choosel.protovis.client.PVAlignment.LEFT;
import static org.thechiselgroup.choosel.protovis.client.PVAlignment.RIGHT;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.thechiselgroup.choosel.protovis.client.jsutil.JsArgs;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsArrayGeneric;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsBooleanFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsDoubleFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsStringFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsUtils;

import com.google.gwt.user.client.ui.Widget;

/**
 * Protovis/GWT implementation of <a
 * href="http://vis.stanford.edu/protovis/ex/barley.html">Protovis Becker’s
 * Barley example</a>.
 * 
 * @author Lars Grammel
 */
public class BeckersBarleyExample extends ProtovisWidget implements
        ProtovisExample {

    public static class Barley {

        public double yield;

        public String variety;

        public int year;

        public String site;

        private Barley(double yield, String variety, int year, String site) {
            this.yield = yield;
            this.variety = variety;
            this.year = year;
            this.site = site;
        }

        public String getValue(String field) {
            if ("site".equals(field)) {
                return site;
            } else if ("variety".equals(field)) {
                return variety;
            } else if ("year".equals(field)) {
                return "" + year;
            }
            throw new IllegalArgumentException(field);
        }

    }

    @Override
    public Widget asWidget() {
        return this;
    }

    private static int compareDouble(double a, double b) {
        if (a > b) {
            return -1;
        }
        if (a == b) {
            return 0;
        }
        return 1;
    };

    private double median(List<Barley> barley) {
        Collections.sort(barley, new Comparator<Barley>() {
            public int compare(Barley a, Barley b) {
                return compareDouble(a.yield, b.yield);
            }
        });

        int length = barley.size();
        return (length % 2 == 1) ? barley.get(length / 2).yield
                : (barley.get(length / 2).yield + barley.get((length / 2) - 1).yield) / 2d;
    }

    private Map<String, Double> calculateMediansPerGroup(
            Map<String, List<Barley>> values) {
        Map<String, Double> result = new HashMap<String, Double>();
        for (Entry<String, List<Barley>> entry : values.entrySet()) {
            result.put(entry.getKey(), median(entry.getValue()));
        }
        return result;
    }

    private SortedMap<String, List<Barley>> split(Iterable<Barley> barley,
            String field) {
        SortedMap<String, List<Barley>> result = new TreeMap<String, List<Barley>>();
        for (Barley b : barley) {
            String key = b.getValue(field);
            if (!result.containsKey(key)) {
                result.put(key, new ArrayList<Barley>());
            }
            result.get(key).add(b);
        }
        return result;
    }

    private void createVisualization(List<Barley> barleyParam) {
        /* Compute yield medians by site and by variety. */
        final Map<String, Double> site = calculateMediansPerGroup(split(
                barleyParam, "site"));
        final Map<String, Double> variety = calculateMediansPerGroup(split(
                barleyParam, "variety"));

        /* Nest yields data by site then year. */
        SortedMap<String, Map<String, List<Barley>>> barley = new TreeMap<String, Map<String, List<Barley>>>(
                new Comparator<String>() {
                    public int compare(String a, String b) {
                        return compareDouble(site.get(a), site.get(b));
                    }
                });
        for (Entry<String, List<Barley>> entry : split(barleyParam, "site")
                .entrySet()) {
            Map<String, List<Barley>> byYears = split(entry.getValue(), "year");
            for (List<Barley> barleyList : byYears.values()) {
                Collections.sort(barleyList, new Comparator<Barley>() {
                    public int compare(Barley a, Barley b) {
                        return compareDouble(variety.get(a.variety),
                                variety.get(b.variety));
                    }
                });
            }
            barley.put(entry.getKey(), byYears);
        }

        /* Sizing and scales. */
        int w = 242;
        final int h = 132;
        final PVLinearScale x = PVScale.linear(10, 70).range(0, w);
        final PVOrdinalScale c = PVColors.category10();

        /* The root panel. */
        PVPanel vis = getPVPanel().width(w).height(h * site.size()).top(15)
                .left(90).right(20).bottom(25);

        /* A panel per site-year. */
        PVPanel cell = vis.add(PV.Panel).data(barley.entrySet()).height(h)
                .top(new JsDoubleFunction() {
                    public double f(JsArgs args) {
                        PVMark _this = args.getThis();
                        return _this.index() * h;
                    }
                }).strokeStyle("#999");

        /* Title bar. */
        cell.add(PV.Bar).height(14).fillStyle("bisque").anchor(CENTER)
                .add(PV.Label).text(new JsStringFunction() {
                    public String f(JsArgs args) {
                        Map.Entry<String, Map<String, List<Barley>>> site = args
                                .getObject();
                        return site.getKey();
                    }
                });

        /* A dot showing the yield. */
        PVDot dot = cell
                .add(PV.Panel)
                .data(new JsFunction<JsArrayGeneric<Entry<String, List<Barley>>>>() {
                    public JsArrayGeneric<Entry<String, List<Barley>>> f(
                            JsArgs args) {
                        Map.Entry<String, Map<String, List<Barley>>> site = args
                                .getObject();
                        return JsUtils.toJsArrayGeneric(site.getValue()
                                .entrySet());
                    }
                }).top(23).add(PV.Dot)
                .data(new JsFunction<JsArrayGeneric<Barley>>() {
                    public JsArrayGeneric<Barley> f(JsArgs args) {
                        Map.Entry<String, List<Barley>> year = args.getObject();
                        return JsUtils.toJsArrayGeneric(year.getValue());
                    }
                }).left(new JsDoubleFunction() {
                    public double f(JsArgs args) {
                        Barley d = args.getObject();
                        return x.fd(d.yield);
                    }
                }).top(new JsDoubleFunction() {
                    public double f(JsArgs args) {
                        PVMark _this = args.getThis();
                        return _this.index() * 11;
                    }
                }).size(12).lineWidth(2).strokeStyle(new JsFunction<PVColor>() {
                    public PVColor f(JsArgs args) {
                        Barley d = args.getObject();
                        return c.fcolor(d.year);
                    }
                });

        /* A label showing the variety. */
        dot.anchor(LEFT).add(PV.Label).visible(new JsBooleanFunction() {
            public boolean f(JsArgs args) {
                PVMark _this = args.getThis();
                return _this.parent().index() == 0;
            }
        }).left(-1).text(new JsStringFunction() {
            public String f(JsArgs args) {
                Barley d = args.getObject();
                return d.variety;
            }
        });

        /* X-ticks. */
        vis.add(PV.Rule).data(x.ticks(7)).left(x).bottom(-5).height(5)
                .strokeStyle("#999").anchor(BOTTOM).add(PV.Label);

        // /* A legend showing the year. */
        vis.add(PV.Dot).extend(dot).dataInt(1931, 1932)
                .left(new JsDoubleFunction() {
                    public double f(JsArgs args) {
                        PVMark _this = args.getThis();
                        return 170 + _this.index() * 40;
                    }
                }).top(-8).strokeStyle(new JsFunction<PVColor>() {
                    public PVColor f(JsArgs args) {
                        int year = args.getInt();
                        return c.fcolor(year);
                    }
                }).anchor(RIGHT).add(PV.Label).text(new JsStringFunction() {
                    public String f(JsArgs args) {
                        int year = args.getInt();
                        return "" + year;
                    }
                });
    }

    private List<Barley> barley() {
        Barley[] barley = new Barley[] {
                new Barley(27.00000, "Manchuria", 1931, "University Farm"),
                new Barley(48.86667, "Manchuria", 1931, "Waseca"),
                new Barley(27.43334, "Manchuria", 1931, "Morris"),
                new Barley(39.93333, "Manchuria", 1931, "Crookston"),
                new Barley(32.96667, "Manchuria", 1931, "Grand Rapids"),
                new Barley(28.96667, "Manchuria", 1931, "Duluth"),
                new Barley(43.06666, "Glabron", 1931, "University Farm"),
                new Barley(55.20000, "Glabron", 1931, "Waseca"),
                new Barley(28.76667, "Glabron", 1931, "Morris"),
                new Barley(38.13333, "Glabron", 1931, "Crookston"),
                new Barley(29.13333, "Glabron", 1931, "Grand Rapids"),
                new Barley(29.66667, "Glabron", 1931, "Duluth"),
                new Barley(35.13333, "Svansota", 1931, "University Farm"),
                new Barley(47.33333, "Svansota", 1931, "Waseca"),
                new Barley(25.76667, "Svansota", 1931, "Morris"),
                new Barley(40.46667, "Svansota", 1931, "Crookston"),
                new Barley(29.66667, "Svansota", 1931, "Grand Rapids"),
                new Barley(25.70000, "Svansota", 1931, "Duluth"),
                new Barley(39.90000, "Velvet", 1931, "University Farm"),
                new Barley(50.23333, "Velvet", 1931, "Waseca"),
                new Barley(26.13333, "Velvet", 1931, "Morris"),
                new Barley(41.33333, "Velvet", 1931, "Crookston"),
                new Barley(23.03333, "Velvet", 1931, "Grand Rapids"),
                new Barley(26.30000, "Velvet", 1931, "Duluth"),
                new Barley(36.56666, "Trebi", 1931, "University Farm"),
                new Barley(63.83330, "Trebi", 1931, "Waseca"),
                new Barley(43.76667, "Trebi", 1931, "Morris"),
                new Barley(46.93333, "Trebi", 1931, "Crookston"),
                new Barley(29.76667, "Trebi", 1931, "Grand Rapids"),
                new Barley(33.93333, "Trebi", 1931, "Duluth"),
                new Barley(43.26667, "No. 457", 1931, "University Farm"),
                new Barley(58.10000, "No. 457", 1931, "Waseca"),
                new Barley(28.70000, "No. 457", 1931, "Morris"),
                new Barley(45.66667, "No. 457", 1931, "Crookston"),
                new Barley(32.16667, "No. 457", 1931, "Grand Rapids"),
                new Barley(33.60000, "No. 457", 1931, "Duluth"),
                new Barley(36.60000, "No. 462", 1931, "University Farm"),
                new Barley(65.76670, "No. 462", 1931, "Waseca"),
                new Barley(30.36667, "No. 462", 1931, "Morris"),
                new Barley(48.56666, "No. 462", 1931, "Crookston"),
                new Barley(24.93334, "No. 462", 1931, "Grand Rapids"),
                new Barley(28.10000, "No. 462", 1931, "Duluth"),
                new Barley(32.76667, "Peatland", 1931, "University Farm"),
                new Barley(48.56666, "Peatland", 1931, "Waseca"),
                new Barley(29.86667, "Peatland", 1931, "Morris"),
                new Barley(41.60000, "Peatland", 1931, "Crookston"),
                new Barley(34.70000, "Peatland", 1931, "Grand Rapids"),
                new Barley(32.00000, "Peatland", 1931, "Duluth"),
                new Barley(24.66667, "No. 475", 1931, "University Farm"),
                new Barley(46.76667, "No. 475", 1931, "Waseca"),
                new Barley(22.60000, "No. 475", 1931, "Morris"),
                new Barley(44.10000, "No. 475", 1931, "Crookston"),
                new Barley(19.70000, "No. 475", 1931, "Grand Rapids"),
                new Barley(33.06666, "No. 475", 1931, "Duluth"),
                new Barley(39.30000, "Wisconsin No. 38", 1931,
                        "University Farm"),
                new Barley(58.80000, "Wisconsin No. 38", 1931, "Waseca"),
                new Barley(29.46667, "Wisconsin No. 38", 1931, "Morris"),
                new Barley(49.86667, "Wisconsin No. 38", 1931, "Crookston"),
                new Barley(34.46667, "Wisconsin No. 38", 1931, "Grand Rapids"),
                new Barley(31.60000, "Wisconsin No. 38", 1931, "Duluth"),
                new Barley(26.90000, "Manchuria", 1932, "University Farm"),
                new Barley(33.46667, "Manchuria", 1932, "Waseca"),
                new Barley(34.36666, "Manchuria", 1932, "Morris"),
                new Barley(32.96667, "Manchuria", 1932, "Crookston"),
                new Barley(22.13333, "Manchuria", 1932, "Grand Rapids"),
                new Barley(22.56667, "Manchuria", 1932, "Duluth"),
                new Barley(36.80000, "Glabron", 1932, "University Farm"),
                new Barley(37.73333, "Glabron", 1932, "Waseca"),
                new Barley(35.13333, "Glabron", 1932, "Morris"),
                new Barley(26.16667, "Glabron", 1932, "Crookston"),
                new Barley(14.43333, "Glabron", 1932, "Grand Rapids"),
                new Barley(25.86667, "Glabron", 1932, "Duluth"),
                new Barley(27.43334, "Svansota", 1932, "University Farm"),
                new Barley(38.50000, "Svansota", 1932, "Waseca"),
                new Barley(35.03333, "Svansota", 1932, "Morris"),
                new Barley(20.63333, "Svansota", 1932, "Crookston"),
                new Barley(16.63333, "Svansota", 1932, "Grand Rapids"),
                new Barley(22.23333, "Svansota", 1932, "Duluth"),
                new Barley(26.80000, "Velvet", 1932, "University Farm"),
                new Barley(37.40000, "Velvet", 1932, "Waseca"),
                new Barley(38.83333, "Velvet", 1932, "Morris"),
                new Barley(32.06666, "Velvet", 1932, "Crookston"),
                new Barley(32.23333, "Velvet", 1932, "Grand Rapids"),
                new Barley(22.46667, "Velvet", 1932, "Duluth"),
                new Barley(29.06667, "Trebi", 1932, "University Farm"),
                new Barley(49.23330, "Trebi", 1932, "Waseca"),
                new Barley(46.63333, "Trebi", 1932, "Morris"),
                new Barley(41.83333, "Trebi", 1932, "Crookston"),
                new Barley(20.63333, "Trebi", 1932, "Grand Rapids"),
                new Barley(30.60000, "Trebi", 1932, "Duluth"),
                new Barley(26.43334, "No. 457", 1932, "University Farm"),
                new Barley(42.20000, "No. 457", 1932, "Waseca"),
                new Barley(43.53334, "No. 457", 1932, "Morris"),
                new Barley(34.33333, "No. 457", 1932, "Crookston"),
                new Barley(19.46667, "No. 457", 1932, "Grand Rapids"),
                new Barley(22.70000, "No. 457", 1932, "Duluth"),
                new Barley(25.56667, "No. 462", 1932, "University Farm"),
                new Barley(44.70000, "No. 462", 1932, "Waseca"),
                new Barley(47.00000, "No. 462", 1932, "Morris"),
                new Barley(30.53333, "No. 462", 1932, "Crookston"),
                new Barley(19.90000, "No. 462", 1932, "Grand Rapids"),
                new Barley(22.50000, "No. 462", 1932, "Duluth"),
                new Barley(28.06667, "Peatland", 1932, "University Farm"),
                new Barley(36.03333, "Peatland", 1932, "Waseca"),
                new Barley(43.20000, "Peatland", 1932, "Morris"),
                new Barley(25.23333, "Peatland", 1932, "Crookston"),
                new Barley(26.76667, "Peatland", 1932, "Grand Rapids"),
                new Barley(31.36667, "Peatland", 1932, "Duluth"),
                new Barley(30.00000, "No. 475", 1932, "University Farm"),
                new Barley(41.26667, "No. 475", 1932, "Waseca"),
                new Barley(44.23333, "No. 475", 1932, "Morris"),
                new Barley(32.13333, "No. 475", 1932, "Crookston"),
                new Barley(15.23333, "No. 475", 1932, "Grand Rapids"),
                new Barley(27.36667, "No. 475", 1932, "Duluth"),
                new Barley(38.00000, "Wisconsin No. 38", 1932,
                        "University Farm"),
                new Barley(58.16667, "Wisconsin No. 38", 1932, "Waseca"),
                new Barley(47.16667, "Wisconsin No. 38", 1932, "Morris"),
                new Barley(35.90000, "Wisconsin No. 38", 1932, "Crookston"),
                new Barley(20.66667, "Wisconsin No. 38", 1932, "Grand Rapids"),
                new Barley(29.33333, "Wisconsin No. 38", 1932, "Duluth") };

        List<Barley> result = new ArrayList<Barley>();
        for (Barley b : barley) {
            result.add(b);
        }
        return result;
    }

    public String getProtovisExampleURL() {
        return "http://vis.stanford.edu/protovis/ex/barley.html";
    }

    public String getSourceCodeFile() {
        return "BeckersBarleyExample.java";
    }

    protected void onAttach() {
        super.onAttach();
        initPVPanel();
        createVisualization(barley());
        getPVPanel().render();
    }

    public String toString() {
        return "Becker's Barley";
    }
}