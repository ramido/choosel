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

import org.thechiselgroup.choosel.protovis.client.functions.PVDoubleFunctionNoIndex;
import org.thechiselgroup.choosel.protovis.client.functions.PVStringFunctionNoIndex;
import org.thechiselgroup.choosel.protovis.client.util.JsArrayGeneric;
import org.thechiselgroup.choosel.protovis.client.util.JsUtils;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayNumber;

/**
 * Wrapper for
 * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.Scale.html">pv.Scale</a></code>
 * .
 * 
 * @author Bradley Blashko
 * @author Lars Grammel
 */
public class PVScale extends JavaScriptObject {

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
    public final static native PVLinearScale linear(double min, double max) /*-{
        return $wnd.pv.Scale.linear(min, max);
    }-*/;

    /**
     * Wrapper for
     * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.Scale.linear.html#constructor">pv.Scale.linear()</a></code>
     * .
     */
    public final static native PVLinearScale linear(JsArrayGeneric<?> array,
            PVDoubleFunctionNoIndex<?> f) /*-{
        return $wnd.pv.Scale.linear(array, @org.thechiselgroup.choosel.protovis.client.functions.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/functions/PVDoubleFunctionNoIndex;)(f));
    }-*/;

    /**
     * Wrapper for
     * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.Scale.linear.html#constructor">pv.Scale.linear()</a></code>
     * .
     */
    public final static <S> PVLinearScale linear(S[] array,
            PVDoubleFunctionNoIndex<?> f) {
        return linear(JsUtils.toJsArrayGeneric(array), f);
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

    public final static native PVOrdinalScale ordinal(JsArrayNumber array) /*-{
        return $wnd.pv.Scale.ordinal(array);
    }-*/;

    public final static native PVOrdinalScale ordinal(JsArrayGeneric<?> array,
            PVStringFunctionNoIndex<?> f) /*-{
        return $wnd.pv.Scale.ordinal(array, @org.thechiselgroup.choosel.protovis.client.functions.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/functions/PVStringFunctionNoIndex;)(f));
    }-*/;

    /**
     * Wrapper for
     * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.Scale.ordinal.html#constructor">pv.Scale.ordinal()</a></code>
     * .
     */
    public final static <S> PVOrdinalScale ordinal(S[] array,
            PVStringFunctionNoIndex<?> f) {
        return ordinal(JsUtils.toJsArrayGeneric(array), f);
    }

    protected PVScale() {
    }

    /**
     * Scales are functions. Use this method if the scale returns a double
     * value.
     */
    public final native double fd(double value) /*-{
        return this(value);
    }-*/;

    /**
     * Scales are functions. Use this method if the scale returns a double
     * value.
     */
    public final native double fd(String value) /*-{
        return this(value);
    }-*/;

    /**
     * Scales are functions. Use this method if the scale returns a color value.
     */
    public final native PVColor fcolor(double value) /*-{
        return this(value);
    }-*/;

}