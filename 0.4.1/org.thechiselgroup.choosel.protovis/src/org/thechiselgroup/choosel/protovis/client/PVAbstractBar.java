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
 * @author Lars Grammel
 */
public abstract class PVAbstractBar<T extends PVAbstractBar<T>> extends
        PVAbstractMark<T> {

    protected PVAbstractBar() {
    }

    public final native T antialias(boolean antialias) /*-{
        return this.antialias(antialias);
    }-*/;

    public final native PVColor fillStyle() /*-{
        return this.fillStyle();
    }-*/;

    public final native T fillStyle(JsFunction<PVColor> f) /*-{
        return this.fillStyle(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsFunction;)(f));
    }-*/;

    public final native T fillStyle(JsStringFunction f) /*-{
        return this.fillStyle(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsStringFunction;)(f));
    }-*/;

    public final native T fillStyle(PVColor color) /*-{
        return this.fillStyle(color);
    }-*/;

    public final native T fillStyle(String color) /*-{
        return this.fillStyle(color);
    }-*/;

    public final native double height() /*-{
        return this.height();
    }-*/;

    public final native T height(double height) /*-{
        return this.height(height);
    }-*/;

    public final native T height(JsDoubleFunction f) /*-{
        return this.height(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsDoubleFunction;)(f));
    }-*/;

    public final native double lineWidth() /*-{
        return this.lineWidth();
    }-*/;

    public final native T lineWidth(double lineWidth) /*-{
        return this.lineWidth(lineWidth);
    }-*/;

    public final native T lineWidth(JsDoubleFunction f) /*-{
        return this.lineWidth(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsDoubleFunction;)(f));
    }-*/;

    public final native PVColor strokeStyle() /*-{
        return this.strokeStyle();
    }-*/;

    public final native T strokeStyle(JsFunction<PVColor> f) /*-{
        return this.strokeStyle(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsFunction;)(f));
    }-*/;

    public final native T strokeStyle(JsStringFunction f) /*-{
        return this.strokeStyle(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsStringFunction;)(f));
    }-*/;

    public final native T strokeStyle(PVColor strokeStyle) /*-{
        return this.strokeStyle(strokeStyle);
    }-*/;

    public final native T strokeStyle(String strokeStyle) /*-{
        return this.strokeStyle(strokeStyle);
    }-*/;

    public final native double width() /*-{
        return this.width();
    }-*/;

    /**
     * Wrapper for
     * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.Bar.html#width">width()</a></code>
     * .
     */
    public final native T width(double width) /*-{
        return this.width(width);
    }-*/;

    /**
     * Wrapper for
     * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.Bar.html#width">width()</a></code>
     * .
     */
    public final native T width(JsDoubleFunction f) /*-{
        return this.width(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsDoubleFunction;)(f));
    }-*/;

}