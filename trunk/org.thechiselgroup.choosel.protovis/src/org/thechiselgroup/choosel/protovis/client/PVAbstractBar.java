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

import org.thechiselgroup.choosel.protovis.client.functions.PVDoubleFunction;
import org.thechiselgroup.choosel.protovis.client.functions.PVFunction;
import org.thechiselgroup.choosel.protovis.client.functions.PVStringFunction;

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

    public final native T fillStyle(PVStringFunction<T, ?> f) /*-{
        return this.fillStyle(@org.thechiselgroup.choosel.protovis.client.functions.JsFunctionUtils::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/functions/PVStringFunction;)(this,f));
    }-*/;

    public final native T fillStyle(String colour) /*-{
        return this.fillStyle(colour);
    }-*/;

    public final native T fillStyle(PVFunction<T, ?, PVColor> f) /*-{
        return this.fillStyle(@org.thechiselgroup.choosel.protovis.client.functions.JsFunctionUtils::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/functions/PVFunction;)(this,f));
    }-*/;

    public final native T height(double height) /*-{
        return this.height(height);
    }-*/;

    public final native T height(PVDoubleFunction<T, ?> f) /*-{
        return this.height(@org.thechiselgroup.choosel.protovis.client.functions.JsFunctionUtils::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/functions/PVDoubleFunction;)(this,f));
    }-*/;

    public final native T lineWidth(double lineWidth) /*-{
        return this.lineWidth(lineWidth);
    }-*/;

    public final native T strokeStyle(PVStringFunction<T, ?> f) /*-{
        return this.fillStyle(@org.thechiselgroup.choosel.protovis.client.functions.JsFunctionUtils::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/functions/PVStringFunction;)(this,f));
    }-*/;

    public final native T strokeStyle(String strokeStyle) /*-{
        return this.strokeStyle(strokeStyle);
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
    public final native T width(PVDoubleFunction<T, ?> f) /*-{
        return this.width(@org.thechiselgroup.choosel.protovis.client.functions.JsFunctionUtils::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/functions/PVDoubleFunction;)(this,f));
    }-*/;

}