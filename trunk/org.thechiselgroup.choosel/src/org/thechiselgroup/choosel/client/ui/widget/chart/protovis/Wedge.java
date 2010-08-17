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

// @formatter:off        
/**
 * 
 * @author Bradley Blashko
 * 
 */
public class Wedge extends Mark {

    public static native Wedge createWedge() /*-{
        return $wnd.pv.Wedge;
    }-*/;

    protected Wedge() {
    }
    
    public final native <T extends Mark> T add(T mark) /*-{
        return this.add(mark);
    }-*/;
    
    public final native Wedge anchor(String anchor) /*-{
        return this.anchor(anchor);
    }-*/;
    
    public final native Wedge angle(double angle) /*-{
        return this.angle(angle);
    }-*/;
    
    public final native Wedge angle(ProtovisFunctionDouble f) /*-{
        return this.angle(@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::getFunctionDouble(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisFunctionDouble;)(this,f));
    }-*/;
    
    public final native Wedge bottom(Number bottom) /*-{
        return this.bottom(bottom);
    }-*/;
    
    public final native Wedge bottom(ProtovisFunctionDouble f) /*-{
        return this.bottom(@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::getFunctionDouble(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisFunctionDouble;)(this,f));
    }-*/;
    
    public final native Wedge childIndex(Number childIndex) /*-{
        return this.childIndex(childIndex);
    }-*/;
    
    public final native Wedge cursor(String cursor) /*-{
        return this.cursor(cursor);
    }-*/;
    
    public final native Wedge data(JavaScriptObject data) /*-{
        return this.data(data);
    }-*/;
    
    public final native Wedge def(String name) /*-{
        return this.def(name);
    }-*/;
    
    // TODO Likely needs some fixing
    public final native Wedge def(String name, ProtovisFunctionDouble f) /*-{
        return this.def(name, @org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::getFunctionDouble(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisFunctionDouble;)(this,f));
    }-*/;
    
    public final native Wedge def(String name, String constant) /*-{
        return this.def(name, constant);
    }-*/;
    
    public final native Wedge defaults(Mark mark) /*-{
        return this.defaults(mark);
    }-*/;
    
    public final native Wedge event(String eventType, ProtovisEventHandler handler) /*-{
        return this.event(eventType, @org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::registerEvent(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisEventHandler;)(this, handler));
    }-*/;
    
    public final native Wedge events(String events) /*-{
        return this.events(events);
    }-*/;

    public final native Wedge fillStyle(ProtovisFunctionString f) /*-{
        return this.fillStyle(@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::getFunctionString(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisFunctionString;)(this,f));
    }-*/;

    public final native Wedge fillStyle(String colour) /*-{
        return this.fillStyle(colour);
    }-*/;

    public final native Wedge index(Number index) /*-{
        return this.index(index);
    }-*/;

    public final native Wedge innerRadius(double innerRadius) /*-{
        return this.innerRadius(innerRadius);
    }-*/;
    
    public final native Wedge innerRadius(ProtovisFunctionDouble f) /*-{
        return this.innerRadius(@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::getFunctionDouble(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisFunctionDouble;)(this,f));
    }-*/;
    
    public final native Wedge left(double left) /*-{
        return this.left(left);
    }-*/;
    
    public final native Wedge left(ProtovisFunctionDouble f) /*-{
        return this.left(@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::getFunctionDouble(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisFunctionDouble;)(this,f));
    }-*/;
    
    public final native Wedge outerRadius(double outerRadius) /*-{
        return this.outerRadius(outerRadius);
    }-*/;
    
    public final native Wedge outerRadius(ProtovisFunctionDouble f) /*-{
        return this.outerRadius(@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::getFunctionDouble(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisFunctionDouble;)(this,f));
    }-*/;
    
    public final native Wedge parent(Panel panel) /*-{
        return this.parent(panel);
    }-*/;
    
    public final native Wedge proto(Mark mark) /*-{
        return this.proto(mark);
    }-*/;
    
    public final native void render() /*-{
        return this.render();
    }-*/;
    
    public final native Wedge reverse(boolean reverse) /*-{
        return this.reverse(reverse);
    }-*/;
    
    public final native Wedge right(double right) /*-{
        return this.right(right);
    }-*/;
    
    public final native Wedge right(ProtovisFunctionDouble f) /*-{
        return this.right(@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::getFunctionDouble(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisFunctionDouble;)(this,f));
    }-*/;
    
    public final native Wedge root(Panel panel) /*-{
        return this.root(panel);
    }-*/;
    
    public final native Wedge scale(Number scale) /*-{
        return this.scale(scale);
    }-*/;
    
    public final native Wedge strokeStyle(ProtovisFunctionString f) /*-{
        return this.strokeStyle(@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::getFunctionString(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisFunctionString;)(this,f));
    }-*/;
    
    public final native Wedge strokeStyle(String colour) /*-{
        return this.strokeStyle(colour);
    }-*/;
    
    public final native Wedge title(String title) /*-{
        return this.title(title);
    }-*/;
    
    public final native Wedge top(double top) /*-{
        return this.top(top);
    }-*/;
    
    public final native Wedge top(ProtovisFunctionDouble f) /*-{
        return this.top(@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::getFunctionDouble(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisFunctionDouble;)(this,f));
    }-*/;
    
    public final native Wedge type(String type) /*-{
        return this.type(type);
    }-*/;
    
    public final native Wedge visible(boolean visible) /*-{
        return this.visible(visible);
    }-*/;

}
// @formatter:on
