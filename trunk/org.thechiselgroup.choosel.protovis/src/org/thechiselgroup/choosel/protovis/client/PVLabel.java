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

import org.thechiselgroup.choosel.protovis.client.functions.PVStringFunction;
import org.thechiselgroup.choosel.protovis.client.functions.PVStringFunctionDoubleArg;
import org.thechiselgroup.choosel.protovis.client.functions.PVStringFunctionIntArg;
import org.thechiselgroup.choosel.protovis.client.functions.PVStringFunctionNoArgs;

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

    protected PVLabel() {
    }

    public final native PVLabel font(String font) /*-{
        return this.font(font);
    }-*/;

    public final native PVLabel text(JavaScriptObject text) /*-{
        return this.text(text);
    }-*/;

    public final native PVLabel text(PVStringFunction<PVLabel, ?> f) /*-{
        return this.text(@org.thechiselgroup.choosel.protovis.client.functions.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/functions/PVStringFunction;)(f));
    }-*/;

    public final native PVLabel text(PVStringFunctionDoubleArg<PVLabel> f) /*-{
        return this.text(@org.thechiselgroup.choosel.protovis.client.functions.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/functions/PVStringFunctionDoubleArg;)(f));
    }-*/;

    public final native PVLabel text(PVStringFunctionIntArg<PVLabel> f) /*-{
        return this.text(@org.thechiselgroup.choosel.protovis.client.functions.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/functions/PVStringFunctionIntArg;)(f));
    }-*/;

    public final native PVLabel text(PVStringFunctionNoArgs f) /*-{
        return this.text(@org.thechiselgroup.choosel.protovis.client.functions.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/functions/PVStringFunctionNoArgs;)(f));
    }-*/;

    public final native PVLabel text(String text) /*-{
        return this.text(text);
    }-*/;

    public final native PVLabel textAlign(PVStringFunction<PVLabel, ?> f) /*-{
        return this.textAlign(@org.thechiselgroup.choosel.protovis.client.functions.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/functions/PVStringFunction;)(f));
    }-*/;

    public final native PVLabel textAlign(String textAlign) /*-{
        return this.textAlign(textAlign);
    }-*/;

    public final native PVLabel textAngle(double textAngle) /*-{
        return this.textAngle(textAngle);
    }-*/;

    public final native PVLabel textBaseline(PVStringFunction<PVLabel, ?> f) /*-{
        return this.textBaseline(@org.thechiselgroup.choosel.protovis.client.functions.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/functions/PVStringFunction;)(f));
    }-*/;

    public final native PVLabel textBaseline(String textBaseline) /*-{
        return this.textBaseline(textBaseline);
    }-*/;

    public final native PVLabel textDecoration(String textDecoration) /*-{
        return this.textDecoration(textDecoration);
    }-*/;

    public final native PVLabel textMargin(double textMargin) /*-{
        return this.textMargin(textMargin);
    }-*/;

    public final native PVLabel textShadow(String textShadow) /*-{
        return this.textShadow(textShadow);
    }-*/;

    public final native PVLabel textStyle(PVStringFunction<PVLabel, ?> f) /*-{
        return this.textStyle(@org.thechiselgroup.choosel.protovis.client.functions.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/functions/PVStringFunction;)(f));
    }-*/;

    public final native PVLabel textStyle(PVStringFunctionDoubleArg<PVLabel> f) /*-{
        return this.textStyle(@org.thechiselgroup.choosel.protovis.client.functions.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/functions/PVStringFunctionDoubleArg;)(f));
    }-*/;

    public final native PVLabel textStyle(String textStyle) /*-{
        return this.textStyle(textStyle);
    }-*/;

}