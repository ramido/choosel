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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.user.client.Element;

/**
 * Wrapper for
 * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.html">pv</a></code>
 * .
 * 
 * @author Lars Grammel
 */
public final class PV {

    public static native PVArea Area() /*-{
        return $wnd.pv.Area;
    }-*/;

    public static native PVBar Bar() /*-{
        return $wnd.pv.Bar;
    }-*/;

    public static native PVPanel createPanel() /*-{
        return new $wnd.pv.Panel();
    }-*/;

    /**
     * Creates a {@link PVPanel} that renders the visualization on
     * <code>element</code>.
     */
    public static PVPanel createPanel(Element element) {
        return createPanel().canvas(element);
    }

    public static native PVDot Dot() /*-{
        return $wnd.pv.Dot;
    }-*/;

    public static native PVLabel Label() /*-{
        return $wnd.pv.Label;
    }-*/;

    public static native PVLine Line() /*-{
        return $wnd.pv.Line;
    }-*/;

    public static native PVPanel Panel() /*-{
        return $wnd.pv.Panel;
    }-*/;

    /**
     * Wrapper for
     * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.html#.range">pv.range()</a></code>
     * .
     */
    public static native JsArrayNumber range(double stop) /*-{
        return $wnd.pv.range(stop);
    }-*/;

    public static native JavaScriptObject reverseOrder() /*-{
        return $wnd.pv.reverseOrder;
    }-*/;

    public static native PVRule Rule() /*-{
        return $wnd.pv.Rule;
    }-*/;

    public static native JsArrayNumber sort(JsArrayNumber data,
            JavaScriptObject comparator) /*-{
        return data.sort(comparator);
    }-*/;

    public static native double sum(JsArrayNumber data) /*-{
        return $wnd.pv.sum(data);
    }-*/;

    public static native PVWedge Wedge() /*-{
        return $wnd.pv.Wedge;
    }-*/;

    private PV() {
    }
}