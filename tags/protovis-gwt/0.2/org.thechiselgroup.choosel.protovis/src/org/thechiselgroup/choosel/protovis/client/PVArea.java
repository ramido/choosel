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

import org.thechiselgroup.choosel.protovis.client.jsutil.JsDoubleFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsStringFunction;

/**
 * 
 * @author Bradley Blashko
 * 
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

    public final native PVArea fillStyle(JsFunction<PVColor> f) /*-{
        return this.fillStyle(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsFunction;)(f));
    }-*/;

    public final native PVArea fillStyle(JsStringFunction f) /*-{
        return this.fillStyle(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsStringFunction;)(f));
    }-*/;

    public final native PVArea fillStyle(String colour) /*-{
        return this.fillStyle(colour);
    }-*/;

    public final native PVColor fillStyle() /*-{
        return this.fillStyle();
    }-*/;

    public final native PVArea font(String font) /*-{
        return this.font(font);
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
    public final native PVArea interpolate(String interpolate) /*-{
        return this.interpolate(interpolate);
    }-*/;

    public final native PVArea lineWidth(double lineWidth) /*-{
        return this.lineWidth(lineWidth);
    }-*/;

    public final native PVArea segmented(boolean segmented) /*-{
        return this.segmented(segmented);
    }-*/;

    public final native PVArea strokeStyle(JsFunction<PVColor> f) /*-{
        return this.strokeStyle(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsFunction;)(f));
    }-*/;

    public final native PVArea strokeStyle(String strokeStyle) /*-{
        return this.strokeStyle(strokeStyle);
    }-*/;

    public final native PVArea tension(double tension) /*-{
        return this.tension(tension);
    }-*/;

    public final native PVArea text(String text) /*-{
        return this.text(text);
    }-*/;

    public final native PVArea textAlign(String textAlign) /*-{
        return this.textAlign(textAlign);
    }-*/;

    public final native PVArea textAngle(double textAngle) /*-{
        return this.textAngle(textAngle);
    }-*/;

    public final native PVArea textBaseline(String textBaseline) /*-{
        return this.textBaseline(textBaseline);
    }-*/;

    public final native PVArea textDecoration(String textDecoration) /*-{
        return this.textDecoration(textDecoration);
    }-*/;

    public final native PVArea textMargin(double textMargin) /*-{
        return this.textMargin(textMargin);
    }-*/;

    public final native PVArea textShadow(String textShadow) /*-{
        return this.textShadow(textShadow);
    }-*/;

    public final native PVArea textStyle(String textStyle) /*-{
        return this.textStyle(textStyle);
    }-*/;

    public final native PVArea width(double width) /*-{
        return this.width(width);
    }-*/;

    public final native PVArea width(JsDoubleFunction f) /*-{
        return this.width(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsDoubleFunction;)(f));
    }-*/;

}