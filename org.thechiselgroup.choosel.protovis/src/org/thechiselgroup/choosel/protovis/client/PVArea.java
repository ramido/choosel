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
 * 
 * @author Bradley Blashko
 * @author Lars Grammel
 */
public final class PVArea extends PVAbstractMark<PVArea> {

    public final static class Type extends PVMarkType<PVArea> {

        protected Type() {
        }

    }

    public static native PVArea create() /*-{
        return new $wnd.pv.Area();
    }-*/;

    protected PVArea() {
    }

    public final native PVColor fillStyle() /*-{
        return this.fillStyle();
    }-*/;

    public final native PVArea fillStyle(JsFunction<PVColor> f) /*-{
        return this.fillStyle(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsFunction;)(f));
    }-*/;

    public final native PVArea fillStyle(JsStringFunction f) /*-{
        return this.fillStyle(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsStringFunction;)(f));
    }-*/;

    public final native PVArea fillStyle(String colour) /*-{
        return this.fillStyle(colour);
    }-*/;

    public final native String font() /*-{
        return this.font();
    }-*/;

    public final native PVArea font(JsStringFunction f) /*-{
        return this.font(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsStringFunction;)(f));
    }-*/;

    public final native PVArea font(String font) /*-{
        return this.font(font);
    }-*/;

    public final native double height() /*-{
        return this.height();
    }-*/;

    public final native PVArea height(double height) /*-{
        return this.height(height);
    }-*/;

    public final native PVArea height(JsDoubleFunction f) /*-{
        return this.height(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsDoubleFunction;)(f));
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
    public final native PVArea interpolate(JsStringFunction f) /*-{
        return this.interpolate(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsStringFunction;)(f));
    }-*/;

    /**
     * @see PVInterpolationMethod
     */
    public final native PVArea interpolate(String interpolate) /*-{
        return this.interpolate(interpolate);
    }-*/;

    public final native double lineWidth() /*-{
        return this.lineWidth();
    }-*/;

    public final native PVArea lineWidth(double lineWidth) /*-{
        return this.lineWidth(lineWidth);
    }-*/;

    public final native PVArea lineWidth(JsDoubleFunction f) /*-{
        return this.lineWidth(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsDoubleFunction;)(f));
    }-*/;

    public final native boolean segmented() /*-{
        return this.segmented();
    }-*/;

    public final native PVArea segmented(boolean segmented) /*-{
        return this.segmented(segmented);
    }-*/;

    public final native PVArea segmented(JsBooleanFunction f) /*-{
        return this.segmented(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsBooleanFunction;)(f));
    }-*/;

    public final native PVColor strokeStyle() /*-{
        return this.strokeStyle();
    }-*/;

    public final native PVArea strokeStyle(JsFunction<PVColor> f) /*-{
        return this.strokeStyle(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsFunction;)(f));
    }-*/;

    public final native PVArea strokeStyle(JsStringFunction f) /*-{
        return this.strokeStyle(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsStringFunction;)(f));
    }-*/;

    public final native PVArea strokeStyle(PVColor strokeStyle) /*-{
        return this.strokeStyle(strokeStyle);
    }-*/;

    public final native PVArea strokeStyle(String strokeStyle) /*-{
        return this.strokeStyle(strokeStyle);
    }-*/;

    public final native double tension() /*-{
        return this.tension();
    }-*/;

    public final native PVArea tension(double tension) /*-{
        return this.tension(tension);
    }-*/;

    public final native PVArea tension(JsDoubleFunction f) /*-{
        return this.tension(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsDoubleFunction;)(f));
    }-*/;

    public final native double width() /*-{
        return this.width();
    }-*/;

    public final native PVArea width(double width) /*-{
        return this.width(width);
    }-*/;

    public final native PVArea width(JsDoubleFunction f) /*-{
        return this.width(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsDoubleFunction;)(f));
    }-*/;

}