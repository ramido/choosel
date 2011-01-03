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
package org.thechiselgroup.choosel.protovis.client.jsutil;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * <p>
 * <b>PERFORMANCE IMPORTANT $entry function usage</b>: We used
 * "$entry(function()...)", but it decreased the performance by factor 5. Do not
 * add $entry here (except for debugging). However, be aware that errors are
 * dropped.
 * </p>
 */
public final class JsFunctionUtils {

    public static final native JavaScriptObject toJavaScriptFunction(
            JsBooleanFunction f) /*-{
        return function() {
        return f.@org.thechiselgroup.choosel.protovis.client.jsutil.JsBooleanFunction::f(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsArgs;)({ _this: this, _args: arguments});
        };
    }-*/;

    public static final native JavaScriptObject toJavaScriptFunction(
            JsDoubleFunction f) /*-{
        return function() {
        return f.@org.thechiselgroup.choosel.protovis.client.jsutil.JsDoubleFunction::f(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsArgs;)({ _this: this, _args: arguments});
        };
    }-*/;

    public static final native Object toJavaScriptFunction(JsFunction<?> f) /*-{
        return function() {
        return f.@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunction::f(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsArgs;)({ _this: this, _args: arguments});
        };
    }-*/;

    public static final native JavaScriptObject toJavaScriptFunction(
            JsStringFunction f) /*-{
        return function() {
        return f.@org.thechiselgroup.choosel.protovis.client.jsutil.JsStringFunction::f(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsArgs;)({ _this: this, _args: arguments}); 
        };
    }-*/;

    public static final native JavaScriptObject toJavaScriptFunction(
            JsIntFunction f) /*-{
        return function() {
        return f.@org.thechiselgroup.choosel.protovis.client.jsutil.JsIntFunction::f(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsArgs;)({ _this: this, _args: arguments});
        };
    }-*/;

    private JsFunctionUtils() {
    }

}