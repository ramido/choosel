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
// @formatter:off        
public class PVArea extends PVMark {

    public static final native PVArea createArea() /*-{
        return $wnd.pv.Area;
    }-*/;

    protected PVArea() {
    }

    public final native <T extends PVMark> T add(T mark) /*-{
        return this.add(mark);
    }-*/;

    public final native PVArea anchor(String anchor) /*-{
        return this.anchor(anchor);
    }-*/;

    public final native PVArea bottom(double bottom) /*-{
        return this.bottom(bottom);
    }-*/;

    public final native PVArea bottom(DoubleFunction<?> f) /*-{
        return this.bottom(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunction;)(this,f));
    }-*/;

    public final native PVArea childIndex(int childIndex) /*-{
        return this.childIndex(childIndex);
    }-*/;

    public final native PVArea cursor(String cursor) /*-{
        return this.cursor(cursor);
    }-*/;

    public final native PVArea data(JavaScriptObject data) /*-{
        return this.data(data);
    }-*/;

    public final native PVArea def(String name) /*-{
        return this.def(name);
    }-*/;

    // TODO Likely needs some fixing
    public final native PVArea def(String name, DoubleFunction<?> f) /*-{
        return this.def(name, @org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunction;)(this,f));
    }-*/;

    public final native PVArea def(String name, String constant) /*-{
        return this.def(name, constant);
    }-*/;

    public final native PVArea defaults(PVMark mark) /*-{
        return this.defaults(mark);
    }-*/;

    public final native PVArea event(String eventType, ProtovisEventHandler handler) /*-{
        return this.event(eventType, @org.thechiselgroup.choosel.protovis.client.PVMark::registerEvent(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/ProtovisEventHandler;)(this, handler));
    }-*/;

    public final native PVArea events(String events) /*-{
        return this.events(events);
    }-*/;

    public final native PVArea fillStyle(StringFunction<?> f) /*-{
        return this.fillStyle(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/StringFunction;)(this,f));
    }-*/;

    public final native PVArea fillStyle(String colour) /*-{
        return this.fillStyle(colour);
    }-*/;

    public final native PVArea font(String font) /*-{
        return this.font(font);
    }-*/;

    public final native PVArea height(double height) /*-{
        return this.height(height);
    }-*/;

    public final native PVArea height(DoubleFunction<?> f) /*-{
        return this.height(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunction;)(this,f));
    }-*/;

    public final native PVArea index(int index) /*-{
        return this.index(index);
    }-*/;

    public final native PVArea interpolate(String interpolate) /*-{
        return this.interpolate(interpolate);
    }-*/;

    public final native PVArea left(double left) /*-{
        return this.left(left);
    }-*/;

    public final native PVArea left(DoubleFunction<?> f) /*-{
        return this.left(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunction;)(this,f));
    }-*/;

    public final native PVArea lineWidth(double lineWidth) /*-{
        return this.lineWidth(lineWidth);
    }-*/;

    public final native PVArea parent(PVPanel panel) /*-{
        return this.parent(panel);
    }-*/;

    public final native PVArea proto(PVMark mark) /*-{
        return this.proto(mark);
    }-*/;

    public final native void render() /*-{
        return this.render();
    }-*/;

    public final native PVArea reverse(boolean reverse) /*-{
        return this.reverse(reverse);
    }-*/;

    public final native PVArea right(double right) /*-{
        return this.right(right);
    }-*/;

    public final native PVArea right(DoubleFunction<?> f) /*-{
        return this.right(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunction;)(this,f));
    }-*/;

    public final native PVArea root(PVPanel panel) /*-{
        return this.root(panel);
    }-*/;

    public final native PVArea scale(double scale) /*-{
        return this.scale(scale);
    }-*/;

    public final native PVArea segmented(boolean segmented) /*-{
        return this.segmented(segmented);
    }-*/;

    public final native PVArea strokeStyle(String strokeStyle) /*-{
        return this.strokeStyle(strokeStyle);
    }-*/;

    public final native PVArea tension(double tension) /*-{
        return this.tension(tension);
    }-*/;

    public final native PVArea text(String text) /*-{
        return this.text(text);
    }-*/;

    public final native PVArea textAlign(String textAlign) /*-{
        return this.textAlign(textAlign);
    }-*/;
    
    public final native PVArea textAngle(double textAngle) /*-{
        return this.textAngle(textAngle);
    }-*/;
    
    public final native PVArea textBaseline(String textBaseline) /*-{
        return this.textBaseline(textBaseline);
    }-*/;
    
    public final native PVArea textDecoration(String textDecoration) /*-{
        return this.textDecoration(textDecoration);
    }-*/;
    
    public final native PVArea textMargin(double textMargin) /*-{
        return this.textMargin(textMargin);
    }-*/;
    
    public final native PVArea textShadow(String textShadow) /*-{
        return this.textShadow(textShadow);
    }-*/;
    
    public final native PVArea textStyle(String textStyle) /*-{
        return this.textStyle(textStyle);
    }-*/;
    
    public final native PVArea title(String title) /*-{
        return this.title(title);
    }-*/;
    
    public final native PVArea top(double top) /*-{
        return this.top(top);
    }-*/;
    
    public final native PVArea top(DoubleFunction<?> f) /*-{
        return this.top(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunction;)(this,f));
    }-*/;
    
    public final native PVArea type(String type) /*-{
        return this.type(type);
    }-*/;
    
    public final native PVArea visible(boolean visible) /*-{
        return this.visible(visible);
    }-*/;
    
    public final native PVArea width(double width) /*-{
        return this.width(width);
    }-*/;
    
    public final native PVArea width(DoubleFunction<?> f) /*-{
        return this.width(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunction;)(this,f));
    }-*/;
    // @formatter:on

}