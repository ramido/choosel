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
 * 
 * @author Bradley Blashko
 * 
 */
public class PVWedge extends PVMark {

    // @formatter:off        
    public static native PVWedge createWedge() /*-{
        return $wnd.pv.Wedge;
    }-*/;

    protected PVWedge() {
    }
    
    public final native <T extends PVMark> T add(T mark) /*-{
        return this.add(mark);
    }-*/;
    
    public final native PVWedge anchor(String anchor) /*-{
        return this.anchor(anchor);
    }-*/;
    
    public final native PVWedge angle(double angle) /*-{
        return this.angle(angle);
    }-*/;
    
    public final native PVWedge angle(DoubleFunction<?> f) /*-{
        return this.angle(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunction;)(this,f));
    }-*/;
    
    public final native PVWedge bottom(double bottom) /*-{
        return this.bottom(bottom);
    }-*/;

    public final native PVWedge bottom(DoubleFunction<?> f) /*-{
        return this.bottom(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunction;)(this,f));
    }-*/;
    
    public final native PVWedge childIndex(int childIndex) /*-{
        return this.childIndex(childIndex);
    }-*/;
    
    public final native PVWedge cursor(String cursor) /*-{
        return this.cursor(cursor);
    }-*/;
    
    public final native PVWedge data(JavaScriptObject data) /*-{
        return this.data(data);
    }-*/;
    
    public final native PVWedge def(String name) /*-{
        return this.def(name);
    }-*/;
    
    // TODO Likely needs some fixing
    public final native PVWedge def(String name, DoubleFunction<?> f) /*-{
        return this.def(name, @org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunction;)(this,f));
    }-*/;
    
    public final native PVWedge def(String name, String constant) /*-{
        return this.def(name, constant);
    }-*/;
    
    public final native PVWedge defaults(PVMark mark) /*-{
        return this.defaults(mark);
    }-*/;
    
    public final native PVWedge event(String eventType, ProtovisEventHandler handler) /*-{
        return this.event(eventType, @org.thechiselgroup.choosel.protovis.client.PVMark::registerEvent(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/ProtovisEventHandler;)(this, handler));
    }-*/;
    
    public final native PVWedge events(String events) /*-{
        return this.events(events);
    }-*/;
    
    public final native PVWedge fillStyle(StringFunction<?> f) /*-{
        return this.fillStyle(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/StringFunction;)(this,f));
    }-*/;
    
    public final native PVWedge fillStyle(String colour) /*-{
        return this.fillStyle(colour);
    }-*/;

    public final native PVWedge index(int index) /*-{
        return this.index(index);
    }-*/;

    public final native PVWedge innerRadius(double innerRadius) /*-{
        return this.innerRadius(innerRadius);
    }-*/;

    public final native PVWedge innerRadius(DoubleFunction<?> f) /*-{
        return this.innerRadius(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunction;)(this,f));
    }-*/;

    public final native PVWedge left(double left) /*-{
        return this.left(left);
    }-*/;
    
    public final native PVWedge left(DoubleFunction<?> f) /*-{
        return this.left(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunction;)(this,f));
    }-*/;
    
    public final native PVWedge outerRadius(double outerRadius) /*-{
        return this.outerRadius(outerRadius);
    }-*/;
    
    public final native PVWedge outerRadius(DoubleFunction<?> f) /*-{
        return this.outerRadius(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunction;)(this,f));
    }-*/;
    
    public final native PVWedge parent(PVPanel panel) /*-{
        return this.parent(panel);
    }-*/;
    
    public final native PVWedge proto(PVMark mark) /*-{
        return this.proto(mark);
    }-*/;
    
    public final native void render() /*-{
        return this.render();
    }-*/;
    
    public final native PVWedge reverse(boolean reverse) /*-{
        return this.reverse(reverse);
    }-*/;
    
    public final native PVWedge right(double right) /*-{
        return this.right(right);
    }-*/;
    
    public final native PVWedge right(DoubleFunction<?> f) /*-{
        return this.right(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunction;)(this,f));
    }-*/;
    
    public final native PVWedge root(PVPanel panel) /*-{
        return this.root(panel);
    }-*/;
    
    public final native PVWedge scale(double scale) /*-{
        return this.scale(scale);
    }-*/;
    
    public final native PVWedge startAngle(double startAngle) /*-{
        return this.startAngle(startAngle);
    }-*/;
    
    public final native PVWedge startAngle(DoubleFunction<?> f) /*-{
        return this.startAngle(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunction;)(this,f));
    }-*/; 
    
    public final native PVWedge strokeStyle(StringFunction<?> f) /*-{
        return this.strokeStyle(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/StringFunction;)(this,f));
    }-*/;
    
    public final native PVWedge strokeStyle(String colour) /*-{
        return this.strokeStyle(colour);
    }-*/;
    
    public final native PVWedge title(String title) /*-{
        return this.title(title);
    }-*/;
    
    public final native PVWedge top(double top) /*-{
        return this.top(top);
    }-*/;
    
    public final native PVWedge top(DoubleFunction<?> f) /*-{
        return this.top(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunction;)(this,f));
    }-*/;
    
    public final native PVWedge type(String type) /*-{
        return this.type(type);
    }-*/;
    
    public final native PVWedge visible(boolean visible) /*-{
        return this.visible(visible);
    }-*/;
    // @formatter:on

}
