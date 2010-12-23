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
 * Wrapper for
 * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.Label.html">pv.Label</a></code>
 * .
 * 
 * @author Bradley Blashko
 * @author Lars Grammel
 */
// @formatter:off        
public class PVLabel extends PVMark {

    public static native PVLabel createLabel() /*-{
        return $wnd.pv.Label;
    }-*/;

    protected PVLabel() {
    }

    public final native <T extends PVMark> T add(T mark) /*-{
        return this.add(mark);
    }-*/;


    public final native PVLabel anchor(String anchor) /*-{
        return this.anchor(anchor);
    }-*/;

    public final native PVLabel bottom(double bottom) /*-{
        return this.bottom(bottom);
    }-*/;

    public final native PVLabel bottom(DoubleFunction<?> f) /*-{
        return this.bottom(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunction;)(this,f));
    }-*/;

    public final native PVLabel childIndex(double childIndex) /*-{
        return this.childIndex(childIndex);
    }-*/;

    public final native PVLabel cursor(String cursor) /*-{
        return this.cursor(cursor);
    }-*/;

    public final native PVLabel data(JavaScriptObject data) /*-{
        return this.data(data);
    }-*/;

    public final native PVLabel def(String name) /*-{
        return this.def(name);
    }-*/;

    // TODO Likely needs some fixing
    public final native PVLabel def(String name, DoubleFunction<?> f) /*-{
        return this.def(name, @org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunction;)(this,f));
    }-*/;

    public final native PVLabel def(String name, String constant) /*-{
        return this.def(name, constant);
    }-*/;

    public final native PVLabel defaults(PVMark mark) /*-{
        return this.defaults(mark);
    }-*/;

    public final native PVLabel event(String eventType, ProtovisEventHandler handler) /*-{
        return this.event(eventType, @org.thechiselgroup.choosel.protovis.client.PVMark::registerEvent(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/ProtovisEventHandler;)(this, handler));
    }-*/;

    public final native PVLabel events(String events) /*-{
        return this.events(events);
    }-*/;

    public final native PVLabel font(String font) /*-{
        return this.font(font);
    }-*/;

    public final native PVLabel index(int index) /*-{
        return this.index(index);
    }-*/;

    public final native PVLabel left(double left) /*-{
        return this.left(left);
    }-*/;

    public final native PVLabel left(DoubleFunction<?> f) /*-{
        return this.left(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunction;)(this,f));
    }-*/;

    public final native PVLabel parent(PVPanel panel) /*-{
        return this.parent(panel);
    }-*/;

    public final native PVLabel proto(PVMark mark) /*-{
        return this.proto(mark);
    }-*/;

    public final native void render() /*-{
        return this.render();
    }-*/;

    public final native PVLabel reverse(boolean reverse) /*-{
        return this.reverse(reverse);
    }-*/;

    public final native PVLabel right(double right) /*-{
        return this.right(right);
    }-*/;

    public final native PVLabel right(DoubleFunction<?> f) /*-{
        return this.right(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunction;)(this,f));
    }-*/;

    public final native PVLabel root(PVPanel panel) /*-{
        return this.root(panel);
    }-*/;

    public final native PVLabel scale(double scale) /*-{
        return this.scale(scale);
    }-*/;

    public final native PVLabel text(JavaScriptObject text) /*-{
        return this.text(text);
    }-*/;

    public final native PVLabel text(String text) /*-{
        return this.text(text);
    }-*/;

    public final native PVLabel text(StringFunction<?> f) /*-{
        return this.text(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/StringFunction;)(this,f));
    }-*/;

    public final native PVLabel text(StringFunctionIntArg f) /*-{
        return this.text(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/StringFunctionIntArg;)(this,f));
    }-*/;

    public final native PVLabel text(StringFunctionNoArgs f) /*-{
        return this.text(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/StringFunctionNoArgs;)(f));
    }-*/;

    public final native PVLabel textAlign(String textAlign) /*-{
        return this.textAlign(textAlign);
    }-*/;

    public final native PVLabel textAlign(StringFunction<?> f) /*-{
        return this.textAlign(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/StringFunction;)(this,f));
    }-*/;

    public final native PVLabel textAngle(double textAngle) /*-{
        return this.textAngle(textAngle);
    }-*/;

    public final native PVLabel textBaseline(String textBaseline) /*-{
        return this.textBaseline(textBaseline);
    }-*/;

    public final native PVLabel textBaseline(StringFunction<?> f) /*-{
        return this.textBaseline(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/StringFunction;)(this,f));
    }-*/;

    public final native PVLabel textDecoration(String textDecoration) /*-{
        return this.textDecoration(textDecoration);
    }-*/;

    public final native PVLabel textMargin(double textMargin) /*-{
        return this.textMargin(textMargin);
    }-*/;

    public final native PVLabel textShadow(String textShadow) /*-{
        return this.textShadow(textShadow);
    }-*/;

    public final native PVLabel textStyle(String textStyle) /*-{
        return this.textStyle(textStyle);
    }-*/;

    public final native PVLabel textStyle(StringFunction<?> f) /*-{
        return this.textStyle(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/StringFunction;)(this,f));
    }-*/;

    public final native PVLabel title(String title) /*-{
        return this.title(title);
    }-*/;

    public final native PVLabel top(double top) /*-{
        return this.top(top);
    }-*/;

    public final native PVLabel top(DoubleFunction<?> f) /*-{
        return this.top(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunction;)(this,f));
    }-*/;

    public final native PVLabel type(String type) /*-{
        return this.type(type);
    }-*/;

    public final native PVLabel visible(boolean visible) /*-{
        return this.visible(visible);
    }-*/;
    
    public final native PVLabel visible(BooleanFunction<?> visible) /*-{
        return this.visible(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/BooleanFunction;)(this,f));
    }-*/;

}
// @formatter:on