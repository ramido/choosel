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
import static org.thechiselgroup.choosel.protovis.client.PVAlignment.TOP;

import java.util.HashMap;
import java.util.Map;

import org.thechiselgroup.choosel.protovis.client.jsutil.JsArgs;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsArrayGeneric;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsBooleanFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsDoubleFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsStringFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsUtils;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.Widget;

/**
 * Protovis/GWT implementation of <a
 * href="http://vis.stanford.edu/protovis/ex/flowers.html">Protovis Anderson’s
 * Flowers example</a>.
 * 
 * @author Lars Grammel
 */
public class AndersonsFlowersExample extends ProtovisWidget implements
        ProtovisExample {

    private static final String PETAL_WIDTH = "petal width";

    private static final String PETAL_LENGTH = "petal length";

    private static final String SEPAL_WIDTH = "sepal width";

    private static final String SEPAL_LENGTH = "sepal length";

    public static class Flower {

        public double sepalLength;

        public double sepalWidth;

        public double petalLength;

        public double petalWidth;

        public String species;

        private Flower(double sepalLength, double sepalWidth,
                double petalLength, double petalWidth, String species) {

            this.sepalLength = sepalLength;
            this.sepalWidth = sepalWidth;
            this.petalLength = petalLength;
            this.petalWidth = petalWidth;
            this.species = species;
        }

        public double getTraitValue(String trait) {
            if (SEPAL_LENGTH.equals(trait)) {
                return sepalLength;
            }
            if (SEPAL_WIDTH.equals(trait)) {
                return sepalWidth;
            }
            if (PETAL_LENGTH.equals(trait)) {
                return petalLength;
            }
            if (PETAL_WIDTH.equals(trait)) {
                return petalWidth;
            }

            throw new IllegalArgumentException(trait);
        }

    }

    public static class TraitPair {

        public String px;

        public String py;

        private TraitPair(String px, String py) {
            this.px = px;
            this.py = py;
        }

        @Override
        public String toString() {
            return "{ px: " + px + ", py:" + py + "}";
        }

    }

    @Override
    public Widget asWidget() {
        return this;
    }

    private void createVisualization(Flower[] flowers) {
        final String[] species = new String[] { "setosa", "versicolor",
                "virginica" };
        final String[] traits = new String[] { SEPAL_LENGTH, SEPAL_WIDTH,
                PETAL_LENGTH, PETAL_WIDTH };

        /* Size parameters. */
        final int size = 150;
        final int padding = 20;

        /* Scales for color and position. */
        final PVOrdinalScale color = PV.colors("rgba(50%, 0%, 0%, .5)",
                "rgba(0%, 50%, 0%, .5)", "rgba(0%, 0%, 50%, .5)");
        final Map<String, PVLinearScale> position = new HashMap<String, PVLinearScale>();
        putTraitScale(position, flowers, SEPAL_LENGTH, size);
        putTraitScale(position, flowers, SEPAL_WIDTH, size);
        putTraitScale(position, flowers, PETAL_LENGTH, size);
        putTraitScale(position, flowers, PETAL_WIDTH, size);

        /* Root panel. */
        PVPanel vis = getPVPanel().width((size + padding) * traits.length)
                .height((size + padding) * traits.length + padding).left(10)
                .top(5);

        /* One cell per trait pair. */
        JsDoubleFunction panelPosition = new JsDoubleFunction() {
            public double f(JsArgs args) {
                PVPanel _this = args.getThis();
                return _this.index() * (size + padding) + padding / 2;
            }
        };
        final PVPanel cell = vis.add(PV.Panel).data(traits).top(panelPosition)
                .height(size).add(PV.Panel)
                .data(new JsFunction<JsArrayGeneric<TraitPair>>() {
                    public JsArrayGeneric<TraitPair> f(JsArgs args) {
                        String d = args.getObject();
                        JsArrayGeneric<TraitPair> result = JsUtils
                                .createJsArrayGeneric();
                        for (int i = 0; i < traits.length; i++) {
                            result.push(new TraitPair(traits[i], d));
                        }
                        return result;
                    }
                }).left(panelPosition).width(size);

        /* Framed dot plots not along the diagonal. */
        PVPanel plot = cell.add(PV.Panel).visible(new JsBooleanFunction() {
            public boolean f(JsArgs args) {
                TraitPair d = args.getObject(0);
                return !d.px.equals(d.py);
            }
        }).strokeStyle("#aaa");

        /* X-axis ticks. */
        PVRule xtick = plot.add(PV.Rule)
                .data(new JsFunction<JavaScriptObject>() {
                    public JavaScriptObject f(JsArgs args) {
                        TraitPair t = args.getObject();
                        return position.get(t.px).ticks(5);
                    };
                }).left(new JsDoubleFunction() {
                    public double f(JsArgs args) {
                        double d = args.getDouble();
                        TraitPair t = args.getObject(1);
                        return position.get(t.px).fd(d);
                    };
                }).strokeStyle("#eee");

        /* Bottom label. */
        xtick.anchor(BOTTOM).add(PV.Label).visible(new JsBooleanFunction() {
            public boolean f(JsArgs args) {
                return (cell.parent().index() == traits.length - 1)
                        && (cell.index() % 2 == 0);
            }
        }).text(new JsStringFunction() {
            public String f(JsArgs args) {
                double d = args.getDouble();
                TraitPair t = args.getObject(1);
                return position.get(t.px).tickFormatDouble(d);
            };
        });

        /* Top label. */
        xtick.anchor(TOP).add(PV.Label).visible(new JsBooleanFunction() {
            public boolean f(JsArgs args) {
                return (cell.parent().index() == 0) && (cell.index() % 2 == 1);
            }
        }).text(new JsStringFunction() {
            public String f(JsArgs args) {
                double d = args.getDouble(0);
                TraitPair t = args.getObject(1);
                return position.get(t.px).tickFormatDouble(d);
            };
        });

        /* Y-axis ticks. */
        PVRule ytick = plot.add(PV.Rule)
                .data(new JsFunction<JavaScriptObject>() {
                    public JavaScriptObject f(JsArgs args) {
                        TraitPair t = args.getObject(0);
                        return position.get(t.py).ticks(5);
                    };
                }).bottom(new JsDoubleFunction() {
                    public double f(JsArgs args) {
                        double d = args.getDouble(0);
                        TraitPair t = args.getObject(1);
                        return position.get(t.py).fd(d);
                    };
                }).strokeStyle("#eee");

        /* Left label. */
        ytick.anchor(LEFT).add(PV.Label).visible(new JsBooleanFunction() {
            public boolean f(JsArgs args) {
                return (cell.index() == 0) && (cell.parent().index() % 2 == 1);
            }
        }).text(new JsStringFunction() {
            public String f(JsArgs args) {
                double d = args.getDouble(0);
                TraitPair t = args.getObject(1);
                return position.get(t.py).tickFormatDouble(d);
            };
        });

        /* Right label. */
        ytick.anchor(RIGHT).add(PV.Label).visible(new JsBooleanFunction() {
            public boolean f(JsArgs args) {
                return (cell.index() == traits.length - 1)
                        && (cell.parent().index() % 2 == 0);
            }
        }).text(new JsStringFunction() {
            public String f(JsArgs args) {
                double d = args.getDouble(0);
                TraitPair t = args.getObject(1);
                return position.get(t.py).tickFormatDouble(d);
            };
        });

        /* Frame and dot plot. */
        plot.add(PV.Dot).data(flowers).left(new JsDoubleFunction() {
            public double f(JsArgs args) {
                Flower d = args.getObject(0);
                TraitPair t = args.getObject(1);
                return position.get(t.px).fd(d.getTraitValue(t.px));
            };
        }).bottom(new JsDoubleFunction() {
            public double f(JsArgs args) {
                Flower d = args.getObject(0);
                TraitPair t = args.getObject(1);
                return position.get(t.py).fd(d.getTraitValue(t.py));
            };
        }).size(10).strokeStyle((String) null)
                .fillStyle(new JsFunction<PVColor>() {
                    public PVColor f(JsArgs args) {
                        Flower d = args.getObject(0);
                        return color.fcolor(d.species);
                    }
                });

        /* Labels along the diagonal. */
        cell.anchor(CENTER).add(PV.Label).visible(new JsBooleanFunction() {
            public boolean f(JsArgs args) {
                TraitPair t = args.getObject(0);
                return t.px.equals(t.py);
            }
        }).font("bold 14px sans-serif").text(new JsStringFunction() {
            public String f(JsArgs args) {
                TraitPair t = args.getObject(0);
                return t.px;
            }
        });

        /* Legend. */
        vis.add(PV.Dot).data(species).bottom(10).left(new JsDoubleFunction() {
            public double f(JsArgs args) {
                PVMark _this = args.getThis();
                return 15 + _this.index() * 65;
            }
        }).size(8).strokeStyle((String) null)
                .fillStyle(new JsFunction<PVColor>() {
                    public PVColor f(JsArgs args) {
                        String d = args.getObject(0);
                        return color.fcolor(d);
                    }
                }).anchor(RIGHT).add(PV.Label);
    }

    private void putTraitScale(final Map<String, PVLinearScale> position,
            Flower[] flowers, final String trait, final int size) {
        position.put(trait, PV.Scale.linear(flowers, new JsDoubleFunction() {
            public double f(JsArgs args) {
                Flower d = args.getObject(0);
                return d.getTraitValue(trait);
            }
        }).range(0, size));
    }

    private Flower[] generateFlowerData() {
        return new Flower[] { new Flower(5.1, 3.5, 1.4, 0.2, "setosa"),
                new Flower(4.9, 3.0, 1.4, 0.2, "setosa"),
                new Flower(4.7, 3.2, 1.3, 0.2, "setosa"),
                new Flower(4.6, 3.1, 1.5, 0.2, "setosa"),
                new Flower(5.0, 3.6, 1.4, 0.2, "setosa"),
                new Flower(5.4, 3.9, 1.7, 0.4, "setosa"),
                new Flower(4.6, 3.4, 1.4, 0.3, "setosa"),
                new Flower(5.0, 3.4, 1.5, 0.2, "setosa"),
                new Flower(4.4, 2.9, 1.4, 0.2, "setosa"),
                new Flower(4.9, 3.1, 1.5, 0.1, "setosa"),
                new Flower(5.4, 3.7, 1.5, 0.2, "setosa"),
                new Flower(4.8, 3.4, 1.6, 0.2, "setosa"),
                new Flower(4.8, 3.0, 1.4, 0.1, "setosa"),
                new Flower(4.3, 3.0, 1.1, 0.1, "setosa"),
                new Flower(5.8, 4.0, 1.2, 0.2, "setosa"),
                new Flower(5.7, 4.4, 1.5, 0.4, "setosa"),
                new Flower(5.4, 3.9, 1.3, 0.4, "setosa"),
                new Flower(5.1, 3.5, 1.4, 0.3, "setosa"),
                new Flower(5.7, 3.8, 1.7, 0.3, "setosa"),
                new Flower(5.1, 3.8, 1.5, 0.3, "setosa"),
                new Flower(5.4, 3.4, 1.7, 0.2, "setosa"),
                new Flower(5.1, 3.7, 1.5, 0.4, "setosa"),
                new Flower(4.6, 3.6, 1.0, 0.2, "setosa"),
                new Flower(5.1, 3.3, 1.7, 0.5, "setosa"),
                new Flower(4.8, 3.4, 1.9, 0.2, "setosa"),
                new Flower(5.0, 3.0, 1.6, 0.2, "setosa"),
                new Flower(5.0, 3.4, 1.6, 0.4, "setosa"),
                new Flower(5.2, 3.5, 1.5, 0.2, "setosa"),
                new Flower(5.2, 3.4, 1.4, 0.2, "setosa"),
                new Flower(4.7, 3.2, 1.6, 0.2, "setosa"),
                new Flower(4.8, 3.1, 1.6, 0.2, "setosa"),
                new Flower(5.4, 3.4, 1.5, 0.4, "setosa"),
                new Flower(5.2, 4.1, 1.5, 0.1, "setosa"),
                new Flower(5.5, 4.2, 1.4, 0.2, "setosa"),
                new Flower(4.9, 3.1, 1.5, 0.2, "setosa"),
                new Flower(5.0, 3.2, 1.2, 0.2, "setosa"),
                new Flower(5.5, 3.5, 1.3, 0.2, "setosa"),
                new Flower(4.9, 3.6, 1.4, 0.1, "setosa"),
                new Flower(4.4, 3.0, 1.3, 0.2, "setosa"),
                new Flower(5.1, 3.4, 1.5, 0.2, "setosa"),
                new Flower(5.0, 3.5, 1.3, 0.3, "setosa"),
                new Flower(4.5, 2.3, 1.3, 0.3, "setosa"),
                new Flower(4.4, 3.2, 1.3, 0.2, "setosa"),
                new Flower(5.0, 3.5, 1.6, 0.6, "setosa"),
                new Flower(5.1, 3.8, 1.9, 0.4, "setosa"),
                new Flower(4.8, 3.0, 1.4, 0.3, "setosa"),
                new Flower(5.1, 3.8, 1.6, 0.2, "setosa"),
                new Flower(4.6, 3.2, 1.4, 0.2, "setosa"),
                new Flower(5.3, 3.7, 1.5, 0.2, "setosa"),
                new Flower(5.0, 3.3, 1.4, 0.2, "setosa"),
                new Flower(7.0, 3.2, 4.7, 1.4, "versicolor"),
                new Flower(6.4, 3.2, 4.5, 1.5, "versicolor"),
                new Flower(6.9, 3.1, 4.9, 1.5, "versicolor"),
                new Flower(5.5, 2.3, 4.0, 1.3, "versicolor"),
                new Flower(6.5, 2.8, 4.6, 1.5, "versicolor"),
                new Flower(5.7, 2.8, 4.5, 1.3, "versicolor"),
                new Flower(6.3, 3.3, 4.7, 1.6, "versicolor"),
                new Flower(4.9, 2.4, 3.3, 1.0, "versicolor"),
                new Flower(6.6, 2.9, 4.6, 1.3, "versicolor"),
                new Flower(5.2, 2.7, 3.9, 1.4, "versicolor"),
                new Flower(5.0, 2.0, 3.5, 1.0, "versicolor"),
                new Flower(5.9, 3.0, 4.2, 1.5, "versicolor"),
                new Flower(6.0, 2.2, 4.0, 1.0, "versicolor"),
                new Flower(6.1, 2.9, 4.7, 1.4, "versicolor"),
                new Flower(5.6, 2.9, 3.6, 1.3, "versicolor"),
                new Flower(6.7, 3.1, 4.4, 1.4, "versicolor"),
                new Flower(5.6, 3.0, 4.5, 1.5, "versicolor"),
                new Flower(5.8, 2.7, 4.1, 1.0, "versicolor"),
                new Flower(6.2, 2.2, 4.5, 1.5, "versicolor"),
                new Flower(5.6, 2.5, 3.9, 1.1, "versicolor"),
                new Flower(5.9, 3.2, 4.8, 1.8, "versicolor"),
                new Flower(6.1, 2.8, 4.0, 1.3, "versicolor"),
                new Flower(6.3, 2.5, 4.9, 1.5, "versicolor"),
                new Flower(6.1, 2.8, 4.7, 1.2, "versicolor"),
                new Flower(6.4, 2.9, 4.3, 1.3, "versicolor"),
                new Flower(6.6, 3.0, 4.4, 1.4, "versicolor"),
                new Flower(6.8, 2.8, 4.8, 1.4, "versicolor"),
                new Flower(6.7, 3.0, 5.0, 1.7, "versicolor"),
                new Flower(6.0, 2.9, 4.5, 1.5, "versicolor"),
                new Flower(5.7, 2.6, 3.5, 1.0, "versicolor"),
                new Flower(5.5, 2.4, 3.8, 1.1, "versicolor"),
                new Flower(5.5, 2.4, 3.7, 1.0, "versicolor"),
                new Flower(5.8, 2.7, 3.9, 1.2, "versicolor"),
                new Flower(6.0, 2.7, 5.1, 1.6, "versicolor"),
                new Flower(5.4, 3.0, 4.5, 1.5, "versicolor"),
                new Flower(6.0, 3.4, 4.5, 1.6, "versicolor"),
                new Flower(6.7, 3.1, 4.7, 1.5, "versicolor"),
                new Flower(6.3, 2.3, 4.4, 1.3, "versicolor"),
                new Flower(5.6, 3.0, 4.1, 1.3, "versicolor"),
                new Flower(5.5, 2.5, 4.0, 1.3, "versicolor"),
                new Flower(5.5, 2.6, 4.4, 1.2, "versicolor"),
                new Flower(6.1, 3.0, 4.6, 1.4, "versicolor"),
                new Flower(5.8, 2.6, 4.0, 1.2, "versicolor"),
                new Flower(5.0, 2.3, 3.3, 1.0, "versicolor"),
                new Flower(5.6, 2.7, 4.2, 1.3, "versicolor"),
                new Flower(5.7, 3.0, 4.2, 1.2, "versicolor"),
                new Flower(5.7, 2.9, 4.2, 1.3, "versicolor"),
                new Flower(6.2, 2.9, 4.3, 1.3, "versicolor"),
                new Flower(5.1, 2.5, 3.0, 1.1, "versicolor"),
                new Flower(5.7, 2.8, 4.1, 1.3, "versicolor"),
                new Flower(6.3, 3.3, 6.0, 2.5, "virginica"),
                new Flower(5.8, 2.7, 5.1, 1.9, "virginica"),
                new Flower(7.1, 3.0, 5.9, 2.1, "virginica"),
                new Flower(6.3, 2.9, 5.6, 1.8, "virginica"),
                new Flower(6.5, 3.0, 5.8, 2.2, "virginica"),
                new Flower(7.6, 3.0, 6.6, 2.1, "virginica"),
                new Flower(4.9, 2.5, 4.5, 1.7, "virginica"),
                new Flower(7.3, 2.9, 6.3, 1.8, "virginica"),
                new Flower(6.7, 2.5, 5.8, 1.8, "virginica"),
                new Flower(7.2, 3.6, 6.1, 2.5, "virginica"),
                new Flower(6.5, 3.2, 5.1, 2.0, "virginica"),
                new Flower(6.4, 2.7, 5.3, 1.9, "virginica"),
                new Flower(6.8, 3.0, 5.5, 2.1, "virginica"),
                new Flower(5.7, 2.5, 5.0, 2.0, "virginica"),
                new Flower(5.8, 2.8, 5.1, 2.4, "virginica"),
                new Flower(6.4, 3.2, 5.3, 2.3, "virginica"),
                new Flower(6.5, 3.0, 5.5, 1.8, "virginica"),
                new Flower(7.7, 3.8, 6.7, 2.2, "virginica"),
                new Flower(7.7, 2.6, 6.9, 2.3, "virginica"),
                new Flower(6.0, 2.2, 5.0, 1.5, "virginica"),
                new Flower(6.9, 3.2, 5.7, 2.3, "virginica"),
                new Flower(5.6, 2.8, 4.9, 2.0, "virginica"),
                new Flower(7.7, 2.8, 6.7, 2.0, "virginica"),
                new Flower(6.3, 2.7, 4.9, 1.8, "virginica"),
                new Flower(6.7, 3.3, 5.7, 2.1, "virginica"),
                new Flower(7.2, 3.2, 6.0, 1.8, "virginica"),
                new Flower(6.2, 2.8, 4.8, 1.8, "virginica"),
                new Flower(6.1, 3.0, 4.9, 1.8, "virginica"),
                new Flower(6.4, 2.8, 5.6, 2.1, "virginica"),
                new Flower(7.2, 3.0, 5.8, 1.6, "virginica"),
                new Flower(7.4, 2.8, 6.1, 1.9, "virginica"),
                new Flower(7.9, 3.8, 6.4, 2.0, "virginica"),
                new Flower(6.4, 2.8, 5.6, 2.2, "virginica"),
                new Flower(6.3, 2.8, 5.1, 1.5, "virginica"),
                new Flower(6.1, 2.6, 5.6, 1.4, "virginica"),
                new Flower(7.7, 3.0, 6.1, 2.3, "virginica"),
                new Flower(6.3, 3.4, 5.6, 2.4, "virginica"),
                new Flower(6.4, 3.1, 5.5, 1.8, "virginica"),
                new Flower(6.0, 3.0, 4.8, 1.8, "virginica"),
                new Flower(6.9, 3.1, 5.4, 2.1, "virginica"),
                new Flower(6.7, 3.1, 5.6, 2.4, "virginica"),
                new Flower(6.9, 3.1, 5.1, 2.3, "virginica"),
                new Flower(5.8, 2.7, 5.1, 1.9, "virginica"),
                new Flower(6.8, 3.2, 5.9, 2.3, "virginica"),
                new Flower(6.7, 3.3, 5.7, 2.5, "virginica"),
                new Flower(6.7, 3.0, 5.2, 2.3, "virginica"),
                new Flower(6.3, 2.5, 5.0, 1.9, "virginica"),
                new Flower(6.5, 3.0, 5.2, 2.0, "virginica"),
                new Flower(6.2, 3.4, 5.4, 2.3, "virginica"),
                new Flower(5.9, 3.0, 5.1, 1.8, "virginica") };
    }

    public String getProtovisExampleURL() {
        return "http://vis.stanford.edu/protovis/ex/flowers.html";
    }

    public String getSourceCodeFile() {
        return "AndersonsFlowersExample.java";
    }

    protected void onAttach() {
        super.onAttach();
        initPVPanel();
        createVisualization(generateFlowerData());
        getPVPanel().render();
    }

    public String toString() {
        return "Anderson's Flowers";
    }

}