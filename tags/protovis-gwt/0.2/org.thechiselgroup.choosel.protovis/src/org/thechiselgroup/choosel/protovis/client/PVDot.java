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
 * @author Lars Grammel
 */
public final class PVDot extends PVAbstractMark<PVDot> {

    public final static class Type extends PVMarkType<PVDot> {

        protected Type() {
        }

    }

    public static native PVDot create() /*-{
        return new $wnd.pv.Dot();
    }-*/;

    protected PVDot() {
    }

    public final native PVDot angle(double angle) /*-{
        return this.angle(angle);
    }-*/;

    public final native PVDot angle(JsDoubleFunction f) /*-{
        return this.angle(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsDoubleFunction;)(f));
    }-*/;

    public final native PVColor fillStyle() /*-{
        return this.fillStyle();
    }-*/;

    public final native PVDot fillStyle(JsFunction<PVColor> f) /*-{
        return this.fillStyle(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsFunction;)(f));
    }-*/;

    public final native PVDot fillStyle(JsStringFunction f) /*-{
        return this.fillStyle(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsStringFunction;)(f));
    }-*/;

    public final native PVDot fillStyle(String fillStyle) /*-{
        return this.fillStyle(fillStyle);
    }-*/;

    public final native PVDot lineWidth(double lineWidth) /*-{
        return this.lineWidth(lineWidth);
    }-*/;

    public final native PVDot radius(double radius) /*-{
        return this.radius(radius);
    }-*/;

    public final native PVDot radius(JsDoubleFunction f) /*-{
        return this.radius(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsDoubleFunction;)(f));
    }-*/;

    /**
     * @see PVShape
     */
    public final native PVDot shape(String shape) /*-{
        return this.shape(shape);
    }-*/;

    public final native PVDot size(double size) /*-{
        return this.size(size);
    }-*/;

    public final native PVDot size(JsDoubleFunction f) /*-{
        return this.size(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsDoubleFunction;)(f));
    }-*/;

    public final native PVDot strokeStyle(JsFunction<PVColor> f) /*-{
        return this.strokeStyle(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsFunction;)(f));
    }-*/;

    public final native PVDot strokeStyle(String strokeStyle) /*-{
        return this.strokeStyle(strokeStyle);
    }-*/;

}