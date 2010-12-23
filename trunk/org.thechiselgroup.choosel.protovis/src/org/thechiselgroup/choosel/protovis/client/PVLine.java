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
public class PVLine extends PVMark {

    // @formatter:off        
    public static native PVLine createLine() /*-{
        return $wnd.pv.Line;
    }-*/;
    // @formatter:on 

    protected PVLine() {
    }

    // @formatter:off
    public final native <T extends PVMark> T add(T mark) /*-{
        return this.add(mark);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native PVLine anchor(String anchor) /*-{
        return this.anchor(anchor);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native PVLine bottom(double bottom) /*-{
        return this.bottom(bottom);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native PVLine bottom(DoubleFunction<?> f) /*-{
        return this.bottom(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunction;)(this,f));
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native PVLine childIndex(int childIndex) /*-{
        return this.childIndex(childIndex);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native PVLine cursor(String cursor) /*-{
        return this.cursor(cursor);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native PVLine data(JavaScriptObject data) /*-{
        return this.data(data);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native PVLine def(String name) /*-{
        return this.def(name);
    }-*/;
    // @formatter:on

    // TODO Likely needs some fixing
    // @formatter:off
    public final native PVLine def(String name, DoubleFunction<?> f) /*-{
        return this.def(name, @org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunction;)(this,f));
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native PVLine def(String name, String constant) /*-{
        return this.def(name, constant);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native PVLine defaults(PVMark mark) /*-{
        return this.defaults(mark);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native PVLine eccentricity(double eccentricity) /*-{
        return this.eccentricity(eccentricity);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native PVLine event(String eventType, ProtovisEventHandler handler) /*-{
        return this.event(eventType, @org.thechiselgroup.choosel.protovis.client.PVMark::registerEvent(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/ProtovisEventHandler;)(this, handler));
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native PVLine events(String events) /*-{
        return this.events(events);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native PVLine fillStyle(StringFunction<?> f) /*-{
        return this.fillStyle(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/StringFunction;)(this,f));
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native PVLine fillStyle(String colour) /*-{
        return this.fillStyle(colour);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native PVLine index(int index) /*-{
        return this.index(index);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native PVLine interpolate(String interpolate) /*-{
        return this.interpolate(interpolate);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native PVLine left(double left) /*-{
        return this.left(left);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native PVLine left(DoubleFunction<?> f) /*-{
        return this.left(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunction;)(this,f));
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native PVLine lineJoin(String lineJoin) /*-{
        return this.lineJoin(lineJoin);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native PVLine lineWidth(double lineWidth) /*-{
        return this.lineWidth(lineWidth);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native PVLine parent(PVPanel panel) /*-{
        return this.parent(panel);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native PVLine proto(PVMark mark) /*-{
        return this.proto(mark);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native void render() /*-{
        return this.render();
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native PVLine reverse(boolean reverse) /*-{
        return this.reverse(reverse);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native PVLine right(double right) /*-{
        return this.right(right);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native PVLine right(DoubleFunction<?> f) /*-{
        return this.right(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunction;)(this,f));
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native PVLine root(PVPanel panel) /*-{
        return this.root(panel);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native PVLine scale(double scale) /*-{
        return this.scale(scale);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native PVLine segmented(boolean segmented) /*-{
        return this.segmented(segmented);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native PVLine strokeStyle(String strokeStyle) /*-{
        return this.strokeStyle(strokeStyle);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native PVLine tension(double tension) /*-{
        return this.tension(tension);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native PVLine title(String title) /*-{
        return this.title(title);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native PVLine top(double top) /*-{
        return this.top(top);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native PVLine top(DoubleFunction<?> f) /*-{
        return this.top(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunction;)(this,f));
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native PVLine type(String type) /*-{
        return this.type(type);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native PVLine visible(boolean visible) /*-{
        return this.visible(visible);
    }-*/;
    // @formatter:on

}
