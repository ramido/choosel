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

import org.thechiselgroup.choosel.protovis.client.jsutil.JsArrayGeneric;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsDoubleFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsStringFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsUtils;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.client.JsDate;

/**
 * Wrapper for
 * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.html">pv</a></code>
 * .
 * 
 * @author Lars Grammel
 */
public final class PV {

    /**
     * @author Bradley Blashko
     * @author Lars Grammel
     * @author Nikita Zhiltsov
     */
    public final static class Behavior extends JavaScriptObject {

        public final static String PAN = "pan";

        public final static String ZOOM = "zoom";

        public final static native Behavior drag() /*-{
            return $wnd.pv.Behavior.drag();
        }-*/;

        public final static native Behavior pan() /*-{
            return $wnd.pv.Behavior.pan();
        }-*/;

        public final static native Behavior select() /*-{
            return $wnd.pv.Behavior.select();
        }-*/;

        public final static native Behavior zoom() /*-{
            return $wnd.pv.Behavior.zoom();
        }-*/;

        protected Behavior() {
        }

    }

    /**
     * Wrapper for
     * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.Colors.html">pv.Colors</a></code>
     * .
     * 
     * @author Lars Grammel
     */
    public static final class Colors {

        public final static native PVOrdinalScale category10() /*-{
            return $wnd.pv.Colors.category10();
        }-*/;

        public final static native PVOrdinalScale category19() /*-{
            return $wnd.pv.Colors.category19();
        }-*/;

        public final static native PVOrdinalScale category20() /*-{
            return $wnd.pv.Colors.category20();
        }-*/;

        private Colors() {
        }

    }

    public final static class Event {

        public static final String MOUSEWHEEL = "mousewheel";

        public static final String MOUSEUP = "mouseup";

        public static final String MOUSEOVER = "mouseover";

        public static final String MOUSEOUT = "mouseout";

        public static final String MOUSEMOVE = "mousemove";

        public static final String MOUSEDOWN = "mousedown";

        public static final String CLICK = "click";

        public static final String DRAG = "drag";

        public static final String DOUBLE_CLICK = "dblclick";

        private Event() {
        }

    }

    public final static class Events {

        public static final String ALL = "all";

        private Events() {
        }
    }

    /**
     * Wrapper for
     * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.Format.html">pv.Format</a></code>
     * .
     * 
     * @author Lars Grammel
     */
    public final static class Format {

        public final native static PVNumberFormat number() /*-{
            return $wnd.pv.Format.number();
        }-*/;

        private Format() {
        }

    }

    /**
     * Wrapper for
     * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.Layout.html">pv.Layout</a></code>
     * .
     * 
     * @author Lars Grammel
     * @author Nikita Zhiltsov
     */
    public final static class Layout {

        public static native PVArcLayout Arc() /*-{
            return $wnd.pv.Layout.Arc;
        }-*/;

        public static native PVBulletLayout Bullet() /*-{
            return $wnd.pv.Layout.Bullet;
        }-*/;

        public static native PVClusterLayout Cluster() /*-{
            return $wnd.pv.Layout.Cluster;
        }-*/;

        public static native PVForceLayout Force() /*-{
            return $wnd.pv.Layout.Force;
        }-*/;

        public static native PVMatrixLayout Matrix() /*-{
            return $wnd.pv.Layout.Matrix;
        }-*/;

        public static native PVPackLayout Pack() /*-{
            return $wnd.pv.Layout.Pack;
        }-*/;

        public static native PVFillPartitionLayout PartitionFill() /*-{
            return $wnd.pv.Layout.Partition.Fill;
        }-*/;

        public static native PVStackLayout Stack() /*-{
            return $wnd.pv.Layout.Stack;
        }-*/;

        public static native PVTreeLayout Tree() /*-{
            return $wnd.pv.Layout.Tree;
        }-*/;

        public static native PVTreemapLayout Treemap() /*-{
            return $wnd.pv.Layout.Treemap;
        }-*/;

        /**
         * Wrapper for pv.Layout.Hierarchy.Links.
         */
        public static native JavaScriptObject HierarchyLinks() /*-{
            return $wnd.pv.Layout.Hierarchy.links;
        }-*/;

        private Layout() {
        }

    }

    /**
     * Wrapper for
     * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.Scale.html">pv.Scale</a></code>
     * .
     * 
     * @author Bradley Blashko
     * @author Lars Grammel
     */
    public final static class Scale {

        /**
         * Wrapper for
         * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.Scale.linear.html#constructor">pv.Scale.linear()</a></code>
         * .
         */
        public final static native PVLinearScale linear() /*-{
            return $wnd.pv.Scale.linear();
        }-*/;

        /**
         * Wrapper for
         * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.Scale.linear.html#constructor">pv.Scale.linear()</a></code>
         * .
         */
        public final static PVLinearScale linear(double min, double max) {
            return linear().domain(min, max);
        }

        /**
         * Wrapper for
         * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.Scale.linear.html#constructor">pv.Scale.linear()</a></code>
         * .
         */
        public final static <S> PVLinearScale linear(JsArrayGeneric<S> array,
                JsDoubleFunction f) {
            return linear().domain(array, f);
        }

        /**
         * Wrapper for
         * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.Scale.linear.html#constructor">pv.Scale.linear()</a></code>
         * .
         */
        public final static <S> PVLinearScale linear(JsArrayGeneric<S> array,
                JsDoubleFunction min, JsDoubleFunction max) {
            return linear().domain(array, min, max);
        }

        /**
         * Wrapper for
         * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.Scale.linear.html#constructor">pv.Scale.linear()</a></code>
         * .
         */
        public final static <S> PVLinearScale linear(JsArrayGeneric<S> array,
                JsFunction<JsDate> f) {
            return linear().domain(array, f);
        }

        /**
         * Wrapper for
         * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.Scale.linear.html#constructor">pv.Scale.linear()</a></code>
         * .
         */
        public final static <S> PVLinearScale linear(S[] array,
                JsDoubleFunction f) {
            return linear().domain(array, f);
        }

        /**
         * Wrapper for
         * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.Scale.linear.html#constructor">pv.Scale.linear()</a></code>
         * .
         */
        public final static <S> PVLinearScale linear(S[] array,
                JsDoubleFunction min, JsDoubleFunction max) {
            return linear().domain(array, min, max);
        }

        /**
         * Wrapper for
         * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.Scale.linear.html#constructor">pv.Scale.linear()</a></code>
         * .
         */
        public final static <S> PVLinearScale linear(S[] array,
                JsFunction<JsDate> f) {
            return linear().domain(array, f);
        }

        /**
         * Wrapper for
         * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.Scale.log.html#constructor">pv.Scale.log()</a></code>
         * .
         */
        public final static native PVLogScale log(double min, double max) /*-{
            return $wnd.pv.Scale.log(min, max);
        }-*/;

        public final static native PVOrdinalScale ordinal() /*-{
            return $wnd.pv.Scale.ordinal();
        }-*/;

        public final static native PVOrdinalScale ordinal(
                JsArrayGeneric<?> array, JsFunction<JsDate> f) /*-{
            return $wnd.pv.Scale.ordinal(array, @org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsFunction;)(f));
        }-*/;

        public final static native PVOrdinalScale ordinal(
                JsArrayGeneric<?> array, JsStringFunction f) /*-{
            return $wnd.pv.Scale.ordinal(array, @org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsStringFunction;)(f));
        }-*/;

        public final static native PVOrdinalScale ordinal(JsArrayNumber array) /*-{
            return $wnd.pv.Scale.ordinal(array);
        }-*/;

        public final static <S> PVOrdinalScale ordinal(S[] array,
                JsFunction<JsDate> f) {
            return ordinal(JsUtils.toJsArrayGeneric(array), f);
        }

        /**
         * Wrapper for
         * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.Scale.ordinal.html#constructor">pv.Scale.ordinal()</a></code>
         * .
         */
        public final static <S> PVOrdinalScale ordinal(S[] array,
                JsStringFunction f) {
            return ordinal(JsUtils.toJsArrayGeneric(array), f);
        }

        private Scale() {
        }

    }

    public final static PVArea.Type Area = Area();

    public final static PVBar.Type Bar = Bar();

    public final static PVDot.Type Dot = Dot();

    public final static PVLabel.Type Label = Label();

    public final static PVLine.Type Line = Line();

    public final static PVPanel.Type Panel = Panel();

    public final static PVRule.Type Rule = Rule();

    public final static PVWedge.Type Wedge = Wedge();

    private static native PVArea.Type Area() /*-{
        return $wnd.pv.Area;
    }-*/;

    private static native PVBar.Type Bar() /*-{
        return $wnd.pv.Bar;
    }-*/;

    public static native PVColor color(String value) /*-{
        return $wnd.pv.color(value);
    }-*/;

    /**
     * Wrapper for
     * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.html#.colors">pv.colors()</a></code>
     * .
     */
    public static native PVOrdinalScale colors(JsArrayString values) /*-{
        return $wnd.pv.colors(values);
    }-*/;

    /**
     * Wrapper for
     * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.html#.colors">pv.colors()</a></code>
     * .
     */
    public static PVOrdinalScale colors(String... values) {
        return colors(JsUtils.toJsArrayString(values));
    }

    private static native PVDot.Type Dot() /*-{
        return $wnd.pv.Dot;
    }-*/;

    private static native PVLabel.Type Label() /*-{
        return $wnd.pv.Label;
    }-*/;

    private static native PVLine.Type Line() /*-{
        return $wnd.pv.Line;
    }-*/;

    private static native PVPanel.Type Panel() /*-{
        return $wnd.pv.Panel;
    }-*/;

    public static native PVOrdinalScale ramp(String start, String end) /*-{
        return $wnd.pv.ramp(start, end);
    }-*/;

    /**
     * Wrapper for
     * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.html#.range">pv.range()</a></code>
     * .
     */
    public static native JsArrayNumber range(double stop) /*-{
        return $wnd.pv.range(stop);
    }-*/;

    /**
     * Wrapper for
     * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.html#.range">pv.range()</a></code>
     * .
     */
    public static native JsArrayNumber range(double start, double stop) /*-{
        return $wnd.pv.range(start, stop);
    }-*/;

    /**
     * Wrapper for
     * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.html#.range">pv.range()</a></code>
     * .
     */
    public static native JsArrayNumber range(double start, double stop,
            double step) /*-{
        return $wnd.pv.range(start, stop, step);
    }-*/;

    public static native JavaScriptObject reverseOrder() /*-{
        return $wnd.pv.reverseOrder;
    }-*/;

    public static native PVColor rgb(int r, int g, int b) /*-{
        return $wnd.pv.rgb(r, g, b);
    }-*/;

    public static native PVColor rgb(int r, int g, int b, double a) /*-{
        return $wnd.pv.rgb(r, g, b, a);
    }-*/;

    private static native PVRule.Type Rule() /*-{
        return $wnd.pv.Rule;
    }-*/;

    public static native JsArrayNumber sort(JsArrayNumber data,
            JavaScriptObject comparator) /*-{
        return data.sort(comparator);
    }-*/;

    public static native double sum(JsArrayNumber data) /*-{
        return $wnd.pv.sum(data);
    }-*/;

    private static native PVWedge.Type Wedge() /*-{
        return $wnd.pv.Wedge;
    }-*/;

    private PV() {
    }

}