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
import org.thechiselgroup.choosel.protovis.client.jsutil.JsUtils;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Wrapper for
 * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.Layout.Stack.html">pv.Layout.Stack</a></code>
 * .
 * 
 * @author Lars Grammel
 */
public final class PVStackLayout extends PVAbstractPanel<PVStackLayout> {

    protected PVStackLayout() {
    }

    /**
     * Wrapper for
     * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.Layout.Stack.html#layer">pv.Layout.Stack.layer</a></code>
     * .
     */
    public final native PVMark layer() /*-{
        return this.layer;
    }-*/;

    public final <S> PVStackLayout layers(Iterable<S> data) {
        return this.layers(JsUtils.toJsArrayGeneric(data));
    }

    /**
     * Wrapper for
     * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.Layout.Stack.html#layers">pv.Layout.Stack.layers()</a></code>
     * .
     */
    public final native PVStackLayout layers(JavaScriptObject data) /*-{
        return this.layers(data);
    }-*/;

    public final native PVStackLayout layers(
            JsFunction<? extends JavaScriptObject> f) /*-{
        return this.layers(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsFunction;)(f));
    }-*/;

    public final <S> PVStackLayout layers(S... data) {
        return this.layers(JsUtils.toJsArrayGeneric(data));
    }

    /**
     * Wrapper for
     * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.Layout.Stack.html#offset">pv.Layout.Stack.offset()</a></code>
     * .
     */
    public final native PVStackLayout offset(String offset) /*-{
        return this.offset(offset);
    }-*/;

    /**
     * Wrapper for
     * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.Layout.Stack.html#order">pv.Layout.Stack.order()</a></code>
     * .
     */
    public final native PVStackLayout order(String order) /*-{
        return this.order(order);
    }-*/;

    /**
     * IMPORTANT: function args will have mark as this reference.
     */
    public final native PVStackLayout x(JsDoubleFunction f) /*-{
        return this.x(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsDoubleFunction;)(f));
    }-*/;

    /**
     * IMPORTANT: function args will have mark as this reference.
     */
    public final native PVStackLayout y(JsDoubleFunction f) /*-{
        return this.y(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsDoubleFunction;)(f));
    }-*/;

    public final native PVStackLayout values(JavaScriptObject data) /*-{
        return this.values(data);
    }-*/;

    public final <S> PVStackLayout values(S... data) {
        return this.values(JsUtils.toJsArrayGeneric(data));
    }

}