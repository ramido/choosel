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
 * @author Lars Grammel
 */
public final class PVEllipse extends PVAbstractMark<PVEllipse> {

    public final static class Type extends PVMarkType<PVEllipse> {

        protected Type() {
        }

    }

    public static native PVEllipse create() /*-{
        return new $wnd.pv.Ellipse();
    }-*/;

    protected PVEllipse() {
    }

    public final native PVEllipse antialias(boolean antialias) /*-{
        return this.antialias(antialias);
    }-*/;

    public final native PVColor fillStyle() /*-{
        return this.fillStyle();
    }-*/;

    public final native PVEllipse fillStyle(JsFunction<PVColor> f) /*-{
        return this.fillStyle(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsFunction;)(f));
    }-*/;

    public final native PVEllipse fillStyle(JsStringFunction f) /*-{
        return this.fillStyle(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsStringFunction;)(f));
    }-*/;

    public final native PVEllipse fillStyle(PVColor color) /*-{
        return this.fillStyle(color);
    }-*/;

    public final native PVEllipse fillStyle(String color) /*-{
        return this.fillStyle(color);
    }-*/;

    public final native double horizontalRadius() /*-{
        return this.horizontalRadius();
    }-*/;

    public final native PVEllipse horizontalRadius(double height) /*-{
        return this.horizontalRadius(height);
    }-*/;

    public final native PVEllipse horizontalRadius(JsDoubleFunction f) /*-{
        return this.horizontalRadius(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsDoubleFunction;)(f));
    }-*/;

    public final native double lineWidth() /*-{
        return this.lineWidth();
    }-*/;

    public final native PVEllipse lineWidth(double lineWidth) /*-{
        return this.lineWidth(lineWidth);
    }-*/;

    public final native PVEllipse lineWidth(JsDoubleFunction f) /*-{
        return this.lineWidth(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsDoubleFunction;)(f));
    }-*/;

    public final native PVColor strokeStyle() /*-{
        return this.strokeStyle();
    }-*/;

    public final native PVEllipse strokeStyle(JsFunction<PVColor> f) /*-{
        return this.strokeStyle(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsFunction;)(f));
    }-*/;

    public final native PVEllipse strokeStyle(JsStringFunction f) /*-{
        return this.strokeStyle(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsStringFunction;)(f));
    }-*/;

    public final native PVEllipse strokeStyle(PVColor strokeStyle) /*-{
        return this.strokeStyle(strokeStyle);
    }-*/;

    public final native PVEllipse strokeStyle(String strokeStyle) /*-{
        return this.strokeStyle(strokeStyle);
    }-*/;

    public final native double verticalRadius() /*-{
        return this.verticalRadius();
    }-*/;

    public final native PVEllipse verticalRadius(double height) /*-{
        return this.verticalRadius(height);
    }-*/;

    public final native PVEllipse verticalRadius(JsDoubleFunction f) /*-{
        return this.verticalRadius(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsDoubleFunction;)(f));
    }-*/;

}