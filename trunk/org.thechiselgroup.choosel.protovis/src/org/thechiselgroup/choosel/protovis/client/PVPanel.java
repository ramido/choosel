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
import com.google.gwt.user.client.Element;

/**
 * 
 * @author Bradley Blashko
 * @author Lars Grammel
 */
public class PVPanel extends PVMark {

    // @formatter:off
    public static native PVPanel createPanel() /*-{
        return new $wnd.pv.Panel();
    }-*/;
    // @formatter:on

    protected PVPanel() {
    }

    // @formatter:off
    public final native <T extends PVMark> T add(T mark) /*-{
        return this.add(mark);
    }-*/;

    public final native PVPanel anchor(String anchor) /*-{
        return this.anchor(anchor);
    }-*/;

    
    public final native PVPanel bottom(double bottom) /*-{
        return this.bottom(bottom);
    }-*/;

    public final native PVPanel bottom(DoubleFunction<?> f) /*-{
        return this.bottom(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunction;)(this,f));
    }-*/;

    public final native PVPanel canvas(Element element) /*-{
        return this.canvas(element);
    }-*/;

    public final native PVPanel childIndex(int childIndex) /*-{
        return this.childIndex(childIndex);
    }-*/;

    public final native PVPanel cursor(String cursor) /*-{
        return this.cursor(cursor);
    }-*/;

    public final native PVPanel data(JavaScriptObject data) /*-{
        return this.data(data);
    }-*/;

    public final native PVPanel def(String name) /*-{
        return this.def(name);
    }-*/;

    // TODO Likely needs some fixing
    public final native PVPanel def(String name, DoubleFunction<?> f) /*-{
        return this.def(name, @org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunction;)(this,f));
    }-*/;

    public final native PVPanel def(String name, String constant) /*-{
        return this.def(name, constant);
    }-*/;

    public final native PVPanel defaults(PVMark mark) /*-{
        return this.defaults(mark);
    }-*/;

    public final native PVPanel event(String eventType, PVBehavior behavior) /*-{
        return this.event(eventType, behavior);
    }-*/;

    public final native PVPanel event(String eventType, ProtovisEventHandler handler) /*-{
        return this.event(eventType, @org.thechiselgroup.choosel.protovis.client.PVMark::registerEvent(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/ProtovisEventHandler;)(this, handler));
    }-*/;
    
    public final native PVPanel events(String events) /*-{
        return this.events(events);
    }-*/;

    public final native PVPanel fillStyle(String colour) /*-{
        return this.fillStyle(colour);
    }-*/;

    public final native PVPanel fillStyle(StringFunction<?> f) /*-{
        return this.fillStyle(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/StringFunction;)(this,f));
    }-*/;

    public final native PVPanel height(double height) /*-{
        return this.height(height);
    }-*/;

    public final native PVPanel height(DoubleFunction<?> f) /*-{
        return this.height(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunction;)(this,f));
    }-*/;

    public final native PVPanel index(int index) /*-{
        return this.index(index);
    }-*/;

    public final native PVPanel left(double left) /*-{
        return this.left(left);
    }-*/;

    public final native PVPanel left(DoubleFunction<?> f) /*-{
        return this.left(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunction;)(this,f));
    }-*/;

    public final native PVPanel parent(PVPanel panel) /*-{
        return this.parent(panel);
    }-*/;

    public final native PVPanel proto(PVMark mark) /*-{
        return this.proto(mark);
    }-*/;

    public final native void render() /*-{
        return this.render();
    }-*/;

    public final native PVPanel reverse(boolean reverse) /*-{
        return this.reverse(reverse);
    }-*/;

    public final native PVPanel right(double right) /*-{
        return this.right(right);
    }-*/;

    public final native PVPanel right(DoubleFunction<?> f) /*-{
        return this.right(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunction;)(this,f));
    }-*/;

    public final native PVPanel root(PVPanel panel) /*-{
        return this.root(panel);
    }-*/;

    public final native PVPanel scale(double scale) /*-{
        return this.scale(scale);
    }-*/;

    public final native PVPanel title(String title) /*-{
        return this.title(title);
    }-*/;

    public final native PVPanel top(double top) /*-{
        return this.top(top);
    }-*/;

    public final native PVPanel top(DoubleFunction<?> f) /*-{
        return this.top(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunction;)(this,f));
    }-*/;

    public final native PVPanel type(String type) /*-{
        return this.type(type);
    }-*/;

    public final native PVPanel visible(boolean visible) /*-{
        return this.visible(visible);
    }-*/;

    public final native PVPanel width(double width) /*-{
        return this.width(width);
    }-*/;

    public final native PVPanel width(DoubleFunction<?> f) /*-{
        return this.width(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunction;)(this,f));
    }-*/;
    // @formatter:on

}
