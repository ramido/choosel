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
package org.thechiselgroup.choosel.protovis.client.functions;

import com.google.gwt.core.client.JavaScriptObject;

/*
 * PERFORMANCE IMPORTANT $entry function usage
 * 
 * We used "$entry(function(d) ...)", but it decreased the performance by
 * factor 5. Do not add $entry here (except for debugging). However, be
 * aware that Protovis errors are dropped.
 */
public final class JsFunctionUtils {

    public static final native JavaScriptObject toJavaScriptEventFunction(
            JavaScriptObject _this, PVEventHandler<?> handler) /*-{
        return function() 
        { return handler.@org.thechiselgroup.choosel.protovis.client.functions.PVEventHandler::onEvent(Ljava/lang/Object;Lcom/google/gwt/user/client/Event;)(_this, $wnd.pv.event);};
    }-*/;

    public static final native JavaScriptObject toJavaScriptFunction(
            JavaScriptObject _this, PVBooleanFunction<?, ?> f) /*-{
        return function(d) 
        { return f.@org.thechiselgroup.choosel.protovis.client.functions.PVBooleanFunction::f(Ljava/lang/Object;Ljava/lang/Object;)(_this, d);};
    }-*/;

    public static final native JavaScriptObject toJavaScriptFunction(
            JavaScriptObject _this, PVBooleanFunctionDoubleArg<?> f) /*-{
        return function(d) 
        { return f.@org.thechiselgroup.choosel.protovis.client.functions.PVBooleanFunctionDoubleArg::f(Ljava/lang/Object;D)(_this,d); };
    }-*/;

    public static final native JavaScriptObject toJavaScriptFunction(
            JavaScriptObject _this, PVDoubleFunction<?, ?> f) /*-{
        return function(d) 
        { return f.@org.thechiselgroup.choosel.protovis.client.functions.PVDoubleFunction::f(Ljava/lang/Object;Ljava/lang/Object;)(_this, d);};
    }-*/;

    public static final native JavaScriptObject toJavaScriptFunction(
            JavaScriptObject _this, PVDoubleFunctionDoubleArg<?> f) /*-{
        return function(d) 
        { return f.@org.thechiselgroup.choosel.protovis.client.functions.PVDoubleFunctionDoubleArg::f(Ljava/lang/Object;D)(_this, d);};
    }-*/;

    public static final native JavaScriptObject toJavaScriptFunction(
            PVDoubleFunctionWithoutThis<?> f) /*-{
        return function(d) 
        { return f.@org.thechiselgroup.choosel.protovis.client.functions.PVDoubleFunctionWithoutThis::f(Ljava/lang/Object;)(d);};
    }-*/;

    public static final native JavaScriptObject toJavaScriptFunction(
            PVStringFunctionWithoutThis<?> f) /*-{
        return function(d) 
        { return f.@org.thechiselgroup.choosel.protovis.client.functions.PVStringFunctionWithoutThis::f(Ljava/lang/Object;)(d);};
    }-*/;

    public static final native JavaScriptObject toJavaScriptFunction(
            PVFunctionWithoutThis<?, ?> f) /*-{
        return function(d)  
        { return f.@org.thechiselgroup.choosel.protovis.client.functions.PVFunctionWithoutThis::f(Ljava/lang/Object;)(d);};
    }-*/;

    public static final native Object toJavaScriptFunction(
            JavaScriptObject _this, PVFunction<?, ?, ?> f) /*-{
        return function(d) 
        { return f.@org.thechiselgroup.choosel.protovis.client.functions.PVFunction::f(Ljava/lang/Object;Ljava/lang/Object;)(_this, d);};
    }-*/;

    public static final native JavaScriptObject toJavaScriptFunction(
            JavaScriptObject _this, PVStringFunction<?, ?> f) /*-{
        return function(d) 
        { return f.@org.thechiselgroup.choosel.protovis.client.functions.PVStringFunction::f(Ljava/lang/Object;Ljava/lang/Object;)(_this, d); };
    }-*/;

    public static final native JavaScriptObject toJavaScriptFunction(
            JavaScriptObject _this, PVStringFunctionDoubleArg<?> f) /*-{
        return function(d) 
        { return f.@org.thechiselgroup.choosel.protovis.client.functions.PVStringFunctionDoubleArg::f(Ljava/lang/Object;D)(_this, d); };
    }-*/;

    public static final native JavaScriptObject toJavaScriptFunction(
            JavaScriptObject _this, PVStringFunctionIntArg<?> f) /*-{
        return function(d) 
        { return f.@org.thechiselgroup.choosel.protovis.client.functions.PVStringFunctionIntArg::f(Ljava/lang/Object;I)(_this, d); };
    }-*/;

    public static final native JavaScriptObject toJavaScriptFunction(
            PVDoubleFunctionNoArgs f) /*-{
        return function() 
        { return f.@org.thechiselgroup.choosel.protovis.client.functions.PVDoubleFunctionNoArgs::f()();};
    }-*/;

    public static final native JavaScriptObject toJavaScriptFunction(
            PVIntFunctionNoArgs f) /*-{
        return function() 
        { return f.@org.thechiselgroup.choosel.protovis.client.functions.PVIntFunctionNoArgs::f()();};
    }-*/;

    public static final native JavaScriptObject toJavaScriptFunction(
            PVStringFunctionNoArgs f) /*-{
        return function() 
        { return f.@org.thechiselgroup.choosel.protovis.client.functions.PVStringFunctionNoArgs::f()();};
    }-*/;

    private JsFunctionUtils() {
    }

}