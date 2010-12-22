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
public class Label extends Mark {

    public static native Label createLabel() /*-{
        return $wnd.pv.Label;
    }-*/;

    protected Label() {
    }

    public final native <T extends Mark> T add(T mark) /*-{
        return this.add(mark);
    }-*/;


    public final native Label anchor(String anchor) /*-{
        return this.anchor(anchor);
    }-*/;

    public final native Label bottom(double bottom) /*-{
        return this.bottom(bottom);
    }-*/;

    public final native Label bottom(DoubleFunction<?> f) /*-{
        return this.bottom(@org.thechiselgroup.choosel.protovis.client.Mark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunction;)(this,f));
    }-*/;

    public final native Label childIndex(double childIndex) /*-{
        return this.childIndex(childIndex);
    }-*/;

    public final native Label cursor(String cursor) /*-{
        return this.cursor(cursor);
    }-*/;

    public final native Label data(JavaScriptObject data) /*-{
        return this.data(data);
    }-*/;

    public final native Label def(String name) /*-{
        return this.def(name);
    }-*/;

    // TODO Likely needs some fixing
    public final native Label def(String name, DoubleFunction<?> f) /*-{
        return this.def(name, @org.thechiselgroup.choosel.protovis.client.Mark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunction;)(this,f));
    }-*/;

    public final native Label def(String name, String constant) /*-{
        return this.def(name, constant);
    }-*/;

    public final native Label defaults(Mark mark) /*-{
        return this.defaults(mark);
    }-*/;

    public final native Label event(String eventType, ProtovisEventHandler handler) /*-{
        return this.event(eventType, @org.thechiselgroup.choosel.protovis.client.Mark::registerEvent(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/ProtovisEventHandler;)(this, handler));
    }-*/;

    public final native Label events(String events) /*-{
        return this.events(events);
    }-*/;

    public final native Label font(String font) /*-{
        return this.font(font);
    }-*/;

    public final native Label index(int index) /*-{
        return this.index(index);
    }-*/;

    public final native Label left(double left) /*-{
        return this.left(left);
    }-*/;

    public final native Label left(DoubleFunction<?> f) /*-{
        return this.left(@org.thechiselgroup.choosel.protovis.client.Mark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunction;)(this,f));
    }-*/;

    public final native Label parent(Panel panel) /*-{
        return this.parent(panel);
    }-*/;

    public final native Label proto(Mark mark) /*-{
        return this.proto(mark);
    }-*/;

    public final native void render() /*-{
        return this.render();
    }-*/;

    public final native Label reverse(boolean reverse) /*-{
        return this.reverse(reverse);
    }-*/;

    public final native Label right(double right) /*-{
        return this.right(right);
    }-*/;

    public final native Label right(DoubleFunction<?> f) /*-{
        return this.right(@org.thechiselgroup.choosel.protovis.client.Mark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunction;)(this,f));
    }-*/;

    public final native Label root(Panel panel) /*-{
        return this.root(panel);
    }-*/;

    public final native Label scale(double scale) /*-{
        return this.scale(scale);
    }-*/;

    public final native Label text(JavaScriptObject text) /*-{
        return this.text(text);
    }-*/;

    public final native Label text(String text) /*-{
        return this.text(text);
    }-*/;

    public final native Label text(StringFunction<?> f) /*-{
        return this.text(@org.thechiselgroup.choosel.protovis.client.Mark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/StringFunction;)(this,f));
    }-*/;

    public final native Label text(StringFunctionIntArg f) /*-{
        return this.text(@org.thechiselgroup.choosel.protovis.client.Mark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/StringFunctionIntArg;)(this,f));
    }-*/;

    public final native Label text(StringFunctionNoArgs f) /*-{
        return this.text(@org.thechiselgroup.choosel.protovis.client.Mark::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/StringFunctionNoArgs;)(f));
    }-*/;

    public final native Label textAlign(String textAlign) /*-{
        return this.textAlign(textAlign);
    }-*/;

    public final native Label textAlign(StringFunction<?> f) /*-{
        return this.textAlign(@org.thechiselgroup.choosel.protovis.client.Mark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/StringFunction;)(this,f));
    }-*/;

    public final native Label textAngle(double textAngle) /*-{
        return this.textAngle(textAngle);
    }-*/;

    public final native Label textBaseline(String textBaseline) /*-{
        return this.textBaseline(textBaseline);
    }-*/;

    public final native Label textBaseline(StringFunction<?> f) /*-{
        return this.textBaseline(@org.thechiselgroup.choosel.protovis.client.Mark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/StringFunction;)(this,f));
    }-*/;

    public final native Label textDecoration(String textDecoration) /*-{
        return this.textDecoration(textDecoration);
    }-*/;

    public final native Label textMargin(double textMargin) /*-{
        return this.textMargin(textMargin);
    }-*/;

    public final native Label textShadow(String textShadow) /*-{
        return this.textShadow(textShadow);
    }-*/;

    public final native Label textStyle(String textStyle) /*-{
        return this.textStyle(textStyle);
    }-*/;

    public final native Label textStyle(StringFunction<?> f) /*-{
        return this.textStyle(@org.thechiselgroup.choosel.protovis.client.Mark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/StringFunction;)(this,f));
    }-*/;

    public final native Label title(String title) /*-{
        return this.title(title);
    }-*/;

    public final native Label top(double top) /*-{
        return this.top(top);
    }-*/;

    public final native Label top(DoubleFunction<?> f) /*-{
        return this.top(@org.thechiselgroup.choosel.protovis.client.Mark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunction;)(this,f));
    }-*/;

    public final native Label type(String type) /*-{
        return this.type(type);
    }-*/;

    public final native Label visible(boolean visible) /*-{
        return this.visible(visible);
    }-*/;
    
    public final native Label visible(BooleanFunction<?> visible) /*-{
        return this.visible(@org.thechiselgroup.choosel.protovis.client.Mark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/BooleanFunction;)(this,f));
    }-*/;

}
// @formatter:on