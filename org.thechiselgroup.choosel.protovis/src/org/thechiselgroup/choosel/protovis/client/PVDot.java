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

//@formatter:off
/**
 * 
 * @author Bradley Blashko
 * 
 */
public class PVDot extends PVMark {

    public static native PVDot createDot() /*-{
        return $wnd.pv.Dot;
    }-*/;

    protected PVDot() {
    }

    public final native <T extends PVMark> T add(T mark) /*-{
        return this.add(mark);
    }-*/;

    public final native PVDot anchor(String anchor) /*-{
        return this.anchor(anchor);
    }-*/;

    public final native PVDot angle(double angle) /*-{
        return this.angle(angle);
    }-*/;

    public final native PVDot angle(DoubleFunction<?> f) /*-{
        return this.angle(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunction;)(this,f));
    }-*/;

    public final native PVDot bottom(double bottom) /*-{
        return this.bottom(bottom);
    }-*/;

    public final native PVDot bottom(DoubleFunction<?> f) /*-{
        return this.bottom(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunction;)(this,f));
    }-*/;

    public final native PVDot bottom(DoubleFunctionDoubleArg f) /*-{
        return this.bottom(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunctionDoubleArg;)(this,f));
    }-*/;

    public final native PVDot childIndex(int childIndex) /*-{
        return this.childIndex(childIndex);
    }-*/;

    public final native PVDot cursor(String cursor) /*-{
        return this.cursor(cursor);
    }-*/;

    public final native PVDot data(JavaScriptObject data) /*-{
        return this.data(data);
    }-*/;

    public final native PVDot def(String name) /*-{
        return this.def(name);
    }-*/;

    // TODO Likely needs some fixing
    public final native PVDot def(String name, DoubleFunction<?> f) /*-{
        return this.def(name, @org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunction;)(this,f));
    }-*/;

    public final native PVDot def(String name, String constant) /*-{
        return this.def(name, constant);
    }-*/;

    public final native PVDot defaults(PVMark mark) /*-{
        return this.defaults(mark);
    }-*/;

    public final native PVDot event(String eventType, ProtovisEventHandler handler) /*-{
        return this.event(eventType, @org.thechiselgroup.choosel.protovis.client.PVMark::registerEvent(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/ProtovisEventHandler;)(this, handler));
    }-*/;

    public final native PVDot events(String events) /*-{
        return this.events(events);
    }-*/;

    public final native PVDot fillStyle(StringFunction<?> f) /*-{
        return this.fillStyle(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/StringFunction;)(this,f));
    }-*/;

    public final native PVDot fillStyle(String fillStyle) /*-{
        return this.fillStyle(fillStyle);
    }-*/;

    public final native PVDot index(int index) /*-{
        return this.index(index);
    }-*/;
    
    public final native PVDot left(double left) /*-{
        return this.left(left);
    }-*/;
    
    public final native PVDot left(DoubleFunction<?> f) /*-{
        return this.left(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunction;)(this,f));
    }-*/;

    public final native PVDot left(DoubleFunctionDoubleArg f) /*-{
        return this.left(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunctionDoubleArg;)(this,f));
    }-*/;

    public final native PVDot lineWidth(double lineWidth) /*-{
        return this.lineWidth(lineWidth);
    }-*/;

    public final native PVDot parent(PVPanel panel) /*-{
        return this.parent(panel);
    }-*/;

    public final native PVDot proto(PVMark mark) /*-{
        return this.proto(mark);
    }-*/;

    public final native PVDot radius(double radius) /*-{
        return this.radius(radius);
    }-*/;

    public final native PVDot radius(DoubleFunctionDoubleArg f) /*-{
        return this.radius(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunctionDoubleArg;)(this,f));
    }-*/;

    public final native void render() /*-{
        return this.render();
    }-*/;

    public final native PVDot reverse(boolean reverse) /*-{
        return this.reverse(reverse);
    }-*/;

    public final native PVDot right(double right) /*-{
        return this.right(right);
    }-*/;

    public final native PVDot right(DoubleFunction<?> f) /*-{
        return this.right(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunction;)(this,f));
    }-*/;

    public final native PVDot right(DoubleFunctionDoubleArg f) /*-{
        return this.right(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunctionDoubleArg;)(this,f));
    }-*/;

    public final native PVDot root(PVPanel panel) /*-{
        return this.root(panel);
    }-*/;

    public final native PVDot scale(double scale) /*-{
        return this.scale(scale);
    }-*/;

    public final native PVDot shape(String shape) /*-{
        return this.shape(shape);
    }-*/;

    public final native PVDot size(double size) /*-{
        return this.size(size);
    }-*/;
    
    public final native PVDot size(DoubleFunctionDoubleArg f) /*-{
        return this.size(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunctionDoubleArg;)(this,f));
    }-*/;
    
    public final native PVDot strokeStyle(String strokeStyle) /*-{
        return this.strokeStyle(strokeStyle);
    }-*/;

    public final native PVDot title(String title) /*-{
        return this.title(title);
    }-*/;

    public final native PVDot top(double top) /*-{
        return this.top(top);
    }-*/;

    public final native PVDot top(DoubleFunction<?> f) /*-{
        return this.top(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunction;)(this,f));
    }-*/;

    public final native PVDot top(DoubleFunctionDoubleArg f) /*-{
        return this.top(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunctionDoubleArg;)(this,f));
    }-*/;

    public final native PVDot type(String type) /*-{
        return this.type(type);
    }-*/;

    public final native PVDot visible(boolean visible) /*-{
        return this.visible(visible);
    }-*/;

}
// @formatter:on