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
public final class PVWedge extends PVAbstractMark<PVWedge> {

    public final static class Type extends PVMarkType<PVWedge> {

        protected Type() {
        }

    }

    public static native PVWedge create() /*-{
        return new $wnd.pv.Wedge();
    }-*/;

    protected PVWedge() {
    }

    public final native double angle() /*-{
        return this.angle;
    }-*/;

    public final native PVWedge angle(double angle) /*-{
        return this.angle(angle);
    }-*/;

    public final native PVWedge angle(JsDoubleFunction f) /*-{
        return this.angle(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsDoubleFunction;)(f));
    }-*/;

    public final native PVWedge fillStyle(JsFunction<PVColor> f) /*-{
        return this.fillStyle(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsFunction;)(f));
    }-*/;

    public final native PVWedge fillStyle(JsStringFunction f) /*-{
        return this.fillStyle(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsStringFunction;)(f));
    }-*/;

    public final native PVWedge fillStyle(String colour) /*-{
        return this.fillStyle(colour);
    }-*/;

    public final native PVWedge innerRadius(double innerRadius) /*-{
        return this.innerRadius(innerRadius);
    }-*/;

    public final native PVWedge innerRadius(JsDoubleFunction f) /*-{
        return this.innerRadius(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsDoubleFunction;)(f));
    }-*/;

    public final native PVWedge lineWidth(double lineWidth) /*-{
        return this.lineWidth(lineWidth);
    }-*/;

    public final native double outerRadius() /*-{
        return this.outerRadius;
    }-*/;

    public final native PVWedge outerRadius(double outerRadius) /*-{
        return this.outerRadius(outerRadius);
    }-*/;

    public final native PVWedge outerRadius(JsDoubleFunction f) /*-{
        return this.outerRadius(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsDoubleFunction;)(f));
    }-*/;

    public final native double startAngle() /*-{
        return this.startAngle();
    }-*/;

    public final native PVWedge startAngle(double startAngle) /*-{
        return this.startAngle(startAngle);
    }-*/;

    public final native PVWedge startAngle(JsDoubleFunction f) /*-{
        return this.startAngle(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsDoubleFunction;)(f));
    }-*/;

    public final native PVWedge strokeStyle(JsFunction<PVColor> f) /*-{
        return this.strokeStyle(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsFunction;)(f));
    }-*/;

    public final native PVWedge strokeStyle(String strokeStyle) /*-{
        return this.strokeStyle(strokeStyle);
    }-*/;

}