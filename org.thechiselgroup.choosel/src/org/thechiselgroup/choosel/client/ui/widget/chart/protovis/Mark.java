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
package org.thechiselgroup.choosel.client.ui.widget.chart.protovis;

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
 * 
 */
public abstract class Mark extends JavaScriptObject {

    // @formatter:off
    public static final native JavaScriptObject getFunction(
            JavaScriptObject _this, ProtovisFunction f) /*-{
        return $entry(function(d) 
        {return f.@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.ProtovisFunction::f(Lorg/thechiselgroup/choosel/client/views/chart/ChartItem;I)(d,_this.index);})
    }-*/;

    public static final native JavaScriptObject getFunctionBoolean(
            JavaScriptObject _this, ProtovisFunctionBoolean f) /*-{
        return $entry(function(d) 
            {return f.@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.ProtovisFunctionBoolean::f(Lorg/thechiselgroup/choosel/client/views/chart/ChartItem;I)(d,_this.index);})
    }-*/;

    public static final native JavaScriptObject getFunctionDouble(
            JavaScriptObject _this, ProtovisFunctionDouble f) /*-{
        return $entry(function(d) 
            {return f.@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.ProtovisFunctionDouble::f(Lorg/thechiselgroup/choosel/client/views/chart/ChartItem;I)(d,_this.index);})
    }-*/;
    
    public static final native JavaScriptObject getFunctionString(
            JavaScriptObject _this, ProtovisFunctionString f) /*-{
        return $entry(function(d) 
            {return f.@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.ProtovisFunctionString::f(Lorg/thechiselgroup/choosel/client/views/chart/ChartItem;I)(d,_this.index);})
    }-*/;

    public static final native JavaScriptObject getFunctionStringToString(
            JavaScriptObject _this, ProtovisFunctionStringToString f) /*-{
        return $entry(function(d) 
            {return f.@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.ProtovisFunctionStringToString::f(Ljava/lang/String;I)(d.toString(),_this.index);})
    }-*/;

    public static final native JavaScriptObject registerEvent(
            JavaScriptObject _this, ProtovisEventHandler handler) /*-{
        return $entry(function() 
            {return handler.@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.ProtovisEventHandler::handleEvent(Lcom/google/gwt/user/client/Event;I)($wnd.pv.event, _this.index);})
    }-*/;
    // @formatter:on

    protected Mark() {
    }

}
