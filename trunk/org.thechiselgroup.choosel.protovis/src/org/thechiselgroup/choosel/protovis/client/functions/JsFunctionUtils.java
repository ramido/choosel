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
 * We used "$entry(function()...)", but it decreased the performance by
 * factor 5. Do not add $entry here (except for debugging). However, be
 * aware that Protovis errors are dropped.
 */
public final class JsFunctionUtils {

    public static final native JavaScriptObject toJavaScriptEventFunction(
            PVEventHandler<?> handler) /*-{
        return function() 
        { return handler.@org.thechiselgroup.choosel.protovis.client.functions.PVEventHandler::onEvent(Ljava/lang/Object;Lcom/google/gwt/user/client/Event;)(this, $wnd.pv.event);};
    }-*/;

    public static final native JavaScriptObject toJavaScriptFunction(
            PVBooleanFunction<?> f) /*-{
        return function() 
        { return f.@org.thechiselgroup.choosel.protovis.client.functions.PVBooleanFunction::f(Ljava/lang/Object;Lorg/thechiselgroup/choosel/protovis/client/PVArgs;)(this, arguments);};
    }-*/;

    public static final native JavaScriptObject toJavaScriptFunction(
            PVDoubleFunction<?> f) /*-{
        return function()
        { return f.@org.thechiselgroup.choosel.protovis.client.functions.PVDoubleFunction::f(Ljava/lang/Object;Lorg/thechiselgroup/choosel/protovis/client/PVArgs;)(this, arguments);};
    }-*/;

    public static final native JavaScriptObject toJavaScriptFunction(
            PVDoubleFunctionWithoutThis f) /*-{
        return function()
        { return f.@org.thechiselgroup.choosel.protovis.client.functions.PVDoubleFunctionWithoutThis::f(Lorg/thechiselgroup/choosel/protovis/client/PVArgs;)(arguments);};
    }-*/;

    public static final native JavaScriptObject toJavaScriptFunction(
            PVStringFunctionWithoutThis f) /*-{
        return function()
        { return f.@org.thechiselgroup.choosel.protovis.client.functions.PVStringFunctionWithoutThis::f(Lorg/thechiselgroup/choosel/protovis/client/PVArgs;)(arguments);};
    }-*/;

    public static final native JavaScriptObject toJavaScriptFunction(
            PVFunctionWithoutThis<?> f) /*-{
        return function() 
        { return f.@org.thechiselgroup.choosel.protovis.client.functions.PVFunctionWithoutThis::f(Lorg/thechiselgroup/choosel/protovis/client/PVArgs;)(arguments);};
    }-*/;

    public static final native Object toJavaScriptFunction(PVFunction<?, ?> f) /*-{
        return function()
        { return f.@org.thechiselgroup.choosel.protovis.client.functions.PVFunction::f(Ljava/lang/Object;Lorg/thechiselgroup/choosel/protovis/client/PVArgs;)(this, arguments);};
    }-*/;

    public static final native JavaScriptObject toJavaScriptFunction(
            PVStringFunction<?> f) /*-{
        return function()
        { return f.@org.thechiselgroup.choosel.protovis.client.functions.PVStringFunction::f(Ljava/lang/Object;Lorg/thechiselgroup/choosel/protovis/client/PVArgs;)(this, arguments); };
    }-*/;

    public static final native JavaScriptObject toJavaScriptFunction(
            PVIntFunctionWithoutThis f) /*-{
        return function() 
        { return f.@org.thechiselgroup.choosel.protovis.client.functions.PVIntFunctionWithoutThis::f(Lorg/thechiselgroup/choosel/protovis/client/PVArgs;)(arguments);};
    }-*/;

    private JsFunctionUtils() {
    }

}