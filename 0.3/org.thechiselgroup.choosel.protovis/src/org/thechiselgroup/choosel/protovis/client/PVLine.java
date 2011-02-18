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

import org.thechiselgroup.choosel.protovis.client.jsutil.JsBooleanFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsDoubleFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsStringFunction;

/**
 * 
 * @author Bradley Blashko
 */
public final class PVLine extends PVAbstractMark<PVLine> {

    public final static class Type extends PVMarkType<PVLine> {

        protected Type() {
        }

    }

    /**
     * @see #lineJoin(String)
     */
    public final static String LINE_JOIN_BEVEL = "bevel";

    /**
     * @see #lineJoin(String)
     */
    public final static String LINE_JOIN_NONE = null;

    /**
     * @see #lineJoin(String)
     */
    public final static String LINE_JOIN_MITER = "miter";

    /**
     * @see #lineJoin(String)
     */
    public final static String LINE_JOIN_ROUND = "round";

    /**
     * @see #lineJoin(String)
     */
    public final static String LINE_JOIN_LINEAR = "linear";

    public static native PVLine create() /*-{
        return new $wnd.pv.Line();
    }-*/;

    protected PVLine() {
    }

    public final native PVLine antialias(boolean antialias) /*-{
        return this.antialias(antialias);
    }-*/;

    public final native double eccentricity() /*-{
        return this.eccentricity();
    }-*/;

    public final native PVLine eccentricity(double eccentricity) /*-{
        return this.eccentricity(eccentricity);
    }-*/;

    public final native PVLine eccentricity(JsDoubleFunction f) /*-{
        return this.eccentricity(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsDoubleFunction;)(f));
    }-*/;

    public final native PVColor fillStyle() /*-{
        return this.fillStyle();
    }-*/;

    public final native PVLine fillStyle(JsFunction<PVColor> f) /*-{
        return this.fillStyle(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsFunction;)(f));
    }-*/;

    public final native PVLine fillStyle(JsStringFunction f) /*-{
        return this.fillStyle(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsStringFunction;)(f));
    }-*/;

    public final native PVLine fillStyle(PVColor color) /*-{
        return this.fillStyle(color);
    }-*/;

    public final native PVLine fillStyle(String color) /*-{
        return this.fillStyle(color);
    }-*/;

    /**
     * @see PVInterpolationMethod
     */
    public final native String interpolate() /*-{
        return this.interpolate();
    }-*/;

    /**
     * @see PVInterpolationMethod
     */
    public final native PVLine interpolate(JsStringFunction f) /*-{
        return this.interpolate(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsStringFunction;)(f));
    }-*/;

    /**
     * @see PVInterpolationMethod
     */
    public final native PVLine interpolate(String interpolate) /*-{
        return this.interpolate(interpolate);
    }-*/;

    public final native String lineJoin() /*-{
        return this.lineJoin();
    }-*/;

    /**
     * @see #LINE_JOIN_BEVEL
     * @see #LINE_JOIN_MITER
     * @see #LINE_JOIN_ROUND
     * @see #LINE_JOIN_NONE
     * @see #LINE_JOIN_LINEAR
     */
    public final native PVLine lineJoin(JsStringFunction f) /*-{
        return this.lineJoin(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsStringFunction;)(f));
    }-*/;

    /**
     * @see #LINE_JOIN_BEVEL
     * @see #LINE_JOIN_MITER
     * @see #LINE_JOIN_ROUND
     * @see #LINE_JOIN_NONE
     * @see #LINE_JOIN_LINEAR
     */
    public final native PVLine lineJoin(String lineJoin) /*-{
        return this.lineJoin(lineJoin);
    }-*/;

    public final native double lineWidth() /*-{
        return this.lineWidth();
    }-*/;

    public final native PVLine lineWidth(double lineWidth) /*-{
        return this.lineWidth(lineWidth);
    }-*/;

    public final native PVLine lineWidth(JsDoubleFunction f) /*-{
        return this.lineWidth(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsDoubleFunction;)(f));
    }-*/;

    public final native boolean segmented() /*-{
        return this.segmented();
    }-*/;

    public final native PVLine segmented(boolean segmented) /*-{
        return this.segmented(segmented);
    }-*/;

    public final native PVLine segmented(JsBooleanFunction f) /*-{
        return this.segmented(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsBooleanFunction;)(f));
    }-*/;

    public final native PVColor strokeStyle() /*-{
        return this.strokeStyle();
    }-*/;

    public final native PVLine strokeStyle(JsFunction<PVColor> f) /*-{
        return this.strokeStyle(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsFunction;)(f));
    }-*/;

    public final native PVLine strokeStyle(JsStringFunction f) /*-{
        return this.strokeStyle(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsStringFunction;)(f));
    }-*/;

    public final native PVLine strokeStyle(PVColor strokeStyle) /*-{
        return this.strokeStyle(strokeStyle);
    }-*/;

    public final native PVLine strokeStyle(String strokeStyle) /*-{
        return this.strokeStyle(strokeStyle);
    }-*/;

    public final native double tension() /*-{
        return this.tension();
    }-*/;

    public final native PVLine tension(double tension) /*-{
        return this.tension(tension);
    }-*/;

    public final native PVLine tension(JsDoubleFunction f) /*-{
        return this.tension(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsDoubleFunction;)(f));
    }-*/;
}