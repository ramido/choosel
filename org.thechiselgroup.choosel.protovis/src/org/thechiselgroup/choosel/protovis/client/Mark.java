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

import com.google.gwt.core.client.JavaScriptObject;

/**
 * A base class for all types of marks/visualizations possible with Protovis
 * (e.g. Bar, Line, Label). This class is not directly instantiable. Any of
 * these possible marks subclasses Mark, implements any methods associated with
 * pv.Mark in the Protovis API, and adds its own method implementations.
 * 
 * Furthermore, any mark that is a subclass of a subclass of Mark in the
 * Protovis API (e.g. Panel, which is a subclass of Bar, which is a subclass of
 * Mark) subclasses Mark directly. This design decision was made due to the fact
 * that any class extending {@link JavaScriptObject} must have all of its
 * methods either be final or private.
 * 
 * Another potential design decision would be to have all methods for any type
 * of Protovis mark in one massive class. However, this was decided against
 * since not all methods would be applicable to any type of mark, and thus
 * Eclipse's auto-complete would be rendered unusable.
 * 
 * @author Bradley Blashko
 * @author Lars Grammel
 */
public abstract class Mark extends JavaScriptObject {

    /*
     * PERFORMANCE IMPORTANT $entry function usage
     * 
     * We used "$entry(function(d) ...)", but it decreased the performance by
     * factor 5. Do not add $entry here (except for debugging). However, be
     * aware that Protovis errors are dropped.
     */

    // @formatter:off
    public static final native JavaScriptObject registerEvent(
            JavaScriptObject _this, ProtovisEventHandler handler) /*-{
        return function() 
            { return handler.@org.thechiselgroup.choosel.protovis.client.ProtovisEventHandler::handleEvent(Lcom/google/gwt/user/client/Event;I)($wnd.pv.event, _this.index);};
    }-*/;
    
    public static final native JavaScriptObject toJavaScriptFunction(
            DoubleFunctionNoArgs f) /*-{
        return function() 
            { return f.@org.thechiselgroup.choosel.protovis.client.DoubleFunctionNoArgs::f()();};
    }-*/;
    
    public static final native JavaScriptObject toJavaScriptFunction(
            IntFunctionNoArgs f) /*-{
        return function() 
            { return f.@org.thechiselgroup.choosel.protovis.client.IntFunctionNoArgs::f()();};
    }-*/;

    public static final native JavaScriptObject toJavaScriptFunction(
            JavaScriptObject _this, BooleanFunction<?> f) /*-{
        return function(d) 
            { return f.@org.thechiselgroup.choosel.protovis.client.BooleanFunction::f(Ljava/lang/Object;I)(d,_this.index);};
    }-*/;
    
    public static final native JavaScriptObject toJavaScriptFunction(
            JavaScriptObject _this, DoubleFunction<?> f) /*-{
        return function(d) 
            { return f.@org.thechiselgroup.choosel.protovis.client.DoubleFunction::f(Ljava/lang/Object;I)(d,_this.index);};
    }-*/;
    
    public static final native JavaScriptObject toJavaScriptFunction(
            JavaScriptObject _this, DoubleFunctionDoubleArg f) /*-{
        return function(d) 
            { return f.@org.thechiselgroup.choosel.protovis.client.DoubleFunctionDoubleArg::f(DI)(d,_this.index);};
    }-*/;
    
    public static final native JavaScriptObject toJavaScriptFunction(
            JavaScriptObject _this, StringFunction<?> f) /*-{
        return function(d) 
            { return f.@org.thechiselgroup.choosel.protovis.client.StringFunction::f(Ljava/lang/Object;I)(d,_this.index); };
    }-*/;

    public static final native JavaScriptObject toJavaScriptFunction(
            JavaScriptObject _this, StringFunctionIntArg f) /*-{
        return function(d) 
            { return f.@org.thechiselgroup.choosel.protovis.client.StringFunctionIntArg::f(II)(d,_this.index); };
    }-*/;
    
    public static final native JavaScriptObject toJavaScriptFunction(
            JavaScriptObject _this, StringFunctionDoubleArg f) /*-{
        return function(d) 
            { return f.@org.thechiselgroup.choosel.protovis.client.StringFunctionDoubleArg::f(DI)(d,_this.index); };
    }-*/;

    public static final native JavaScriptObject toJavaScriptFunction(
            StringFunctionNoArgs f) /*-{
        return function() 
            { return f.@org.thechiselgroup.choosel.protovis.client.StringFunctionNoArgs::f()();};
    }-*/;
    // @formatter:on

    protected Mark() {
    }

}
