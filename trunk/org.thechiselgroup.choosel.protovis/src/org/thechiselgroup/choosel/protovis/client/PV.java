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

import org.thechiselgroup.choosel.protovis.client.jsutil.JsUtils;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.core.client.JsArrayString;

/**
 * Wrapper for
 * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.html">pv</a></code>
 * .
 * 
 * @author Lars Grammel
 */
public final class PV {

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