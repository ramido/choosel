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

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Wrapper for
 * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.Label.html">pv.Label</a></code>
 * .
 * 
 * @author Bradley Blashko
 * @author Lars Grammel
 */
public final class PVLabel extends PVAbstractMark<PVLabel> {

    public final static class Type extends PVMarkType<PVLabel> {

        protected Type() {
        }

    }

    public static native PVLabel create() /*-{
        return new $wnd.pv.Label();
    }-*/;

    protected PVLabel() {
    }

    public final native String font() /*-{
        return this.font();
    }-*/;

    public final native PVLabel font(JsStringFunction f) /*-{
        return this.font(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsStringFunction;)(f));
    }-*/;

    public final native PVLabel font(String font) /*-{
        return this.font(font);
    }-*/;

    public final native String text() /*-{
        return this.text();
    }-*/;

    public final native PVLabel text(JavaScriptObject text) /*-{
        return this.text(text);
    }-*/;

    public final native PVLabel text(JsStringFunction f) /*-{
        return this.text(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsStringFunction;)(f));
    }-*/;

    public final native PVLabel text(String text) /*-{
        return this.text(text);
    }-*/;

    public final native String textAlign() /*-{
        return this.textAlign();
    }-*/;

    public final native PVLabel textAlign(JsStringFunction f) /*-{
        return this.textAlign(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsStringFunction;)(f));
    }-*/;

    public final native PVLabel textAlign(String textAlign) /*-{
        return this.textAlign(textAlign);
    }-*/;

    public final native double textAngle() /*-{
        return this.textAngle();
    }-*/;

    public final native PVLabel textAngle(double textAngle) /*-{
        return this.textAngle(textAngle);
    }-*/;

    public final native PVLabel textAngle(JsDoubleFunction f) /*-{
        return this.textAngle(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsDoubleFunction;)(f));
    }-*/;

    public final native double textBaseline() /*-{
        return this.textBaseline();
    }-*/;

    public final native PVLabel textBaseline(JsStringFunction f) /*-{
        return this.textBaseline(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsStringFunction;)(f));
    }-*/;

    public final native PVLabel textBaseline(String textBaseline) /*-{
        return this.textBaseline(textBaseline);
    }-*/;

    public final native String textDecoration() /*-{
        return this.textDecoration();
    }-*/;

    public final native PVLabel textDecoration(String textDecoration) /*-{
        return this.textDecoration(textDecoration);
    }-*/;

    public final native PVLabel textDecoration(JsStringFunction f) /*-{
        return this.textDecoration(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsStringFunction;)(f));
    }-*/;

    public final native double textMargin() /*-{
        return this.textMargin();
    }-*/;

    public final native PVLabel textMargin(double textMargin) /*-{
        return this.textMargin(textMargin);
    }-*/;

    public final native PVLabel textMargin(JsDoubleFunction f) /*-{
        return this.textMargin(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsDoubleFunction;)(f));
    }-*/;

    public final native String textShadow() /*-{
        return this.textShadow();
    }-*/;

    public final native PVLabel textShadow(String textShadow) /*-{
        return this.textShadow(textShadow);
    }-*/;

    public final native PVLabel textShadow(JsStringFunction f) /*-{
        return this.textShadow(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsStringFunction;)(f));
    }-*/;

    public final native PVColor textStyle() /*-{
        return this.textStyle();
    }-*/;

    public final native PVLabel textStyle(JsFunction<PVColor> f) /*-{
        return this.textStyle(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsFunction;)(f));
    }-*/;

    public final native PVLabel textStyle(JsStringFunction f) /*-{
        return this.textStyle(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsStringFunction;)(f));
    }-*/;

    public final native PVLabel textStyle(PVColor color) /*-{
        return this.textStyle(color);
    }-*/;

    public final native PVLabel textStyle(String color) /*-{
        return this.textStyle(color);
    }-*/;

}