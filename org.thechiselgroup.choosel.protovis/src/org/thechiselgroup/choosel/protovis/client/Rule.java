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
 * @author Lars Grammel
 */
public class Rule extends Mark {

    // @formatter:off
    public static native Rule createRule() /*-{
        return $wnd.pv.Rule;
    }-*/;

    protected Rule() {
    }

    public final native <T extends Mark> T add(T mark) /*-{
        return this.add(mark);
    }-*/;

    public final native Rule anchor(String anchor) /*-{
        return this.anchor(anchor);
    }-*/;

    public final native Rule bottom(double bottom) /*-{
        return this.bottom(bottom);
    }-*/;

    public final native Rule bottom(DoubleFunction<?> f) /*-{
        return this.bottom(@org.thechiselgroup.choosel.protovis.client.Mark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunction;)(this,f));
    }-*/;

    public final native Rule bottom(Scale scale) /*-{
        return this.bottom(scale);
    }-*/;

    public final native Rule childIndex(int childIndex) /*-{
        return this.childIndex(childIndex);
    }-*/;

    public final native Rule cursor(String cursor) /*-{
        return this.cursor(cursor);
    }-*/;

    public final native Rule data(JavaScriptObject data) /*-{
        return this.data(data);
    }-*/;

    public final native Rule def(String name) /*-{
        return this.def(name);
    }-*/;

    // TODO Likely needs some fixing
    public final native Rule def(String name, DoubleFunction<?> f) /*-{
        return this.def(name, @org.thechiselgroup.choosel.protovis.client.Mark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunction;)(this,f));
    }-*/;

    public final native Rule def(String name, String constant) /*-{
        return this.def(name, constant);
    }-*/;

    public final native Rule defaults(Mark mark) /*-{
        return this.defaults(mark);
    }-*/;

    public final native Rule event(String eventType,
            ProtovisEventHandler handler) /*-{
        return this.event(eventType, @org.thechiselgroup.choosel.protovis.client.Mark::registerEvent(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/ProtovisEventHandler;)(this, handler));
    }-*/;

    public final native Rule events(String events) /*-{
        return this.events(events);
    }-*/;

    public final native Rule height(double height) /*-{
        return this.height(height);
    }-*/;

    public final native Rule height(DoubleFunction<?> f) /*-{
        return this.height(@org.thechiselgroup.choosel.protovis.client.Mark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunction;)(this,f));
    }-*/;

    public final native Rule index(int index) /*-{
        return this.index(index);
    }-*/;

    public final native Rule left(double left) /*-{
        return this.left(left);
    }-*/;

    public final native Rule left(DoubleFunction<?> f) /*-{
        return this.left(@org.thechiselgroup.choosel.protovis.client.Mark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunction;)(this,f));
    }-*/;

    public final native Rule left(Scale scale) /*-{
        return this.left(scale);
    }-*/;

    public final native Rule lineWidth(double lineWidth) /*-{
        return this.lineWidth(lineWidth);
    }-*/;

    public final native Rule parent(Panel panel) /*-{
        return this.parent(panel);
    }-*/;

    public final native Rule proto(Mark mark) /*-{
        return this.proto(mark);
    }-*/;

    public final native void render() /*-{
        return this.render();
    }-*/;

    public final native Rule reverse(boolean reverse) /*-{
        return this.reverse(reverse);
    }-*/;

    public final native Rule right(double right) /*-{
        return this.right(right);
    }-*/;

    public final native Rule right(DoubleFunction<?> f) /*-{
        return this.right(@org.thechiselgroup.choosel.protovis.client.Mark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunction;)(this,f));
    }-*/;

    public final native Rule right(Scale scale) /*-{
        return this.right(scale);
    }-*/;

    public final native Rule root(Panel panel) /*-{
        return this.root(panel);
    }-*/;

    public final native Rule scale(double scale) /*-{
        return this.scale(scale);
    }-*/;

    public final native Rule strokeStyle(String strokeStyle) /*-{
        return this.strokeStyle(strokeStyle);
    }-*/;

    public final native Rule strokeStyle(StringFunctionIntArg f) /*-{
        return this.strokeStyle(@org.thechiselgroup.choosel.protovis.client.Mark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/StringFunctionIntArg;)(this,f));
    }-*/;

    public final native Rule title(String title) /*-{
        return this.title(title);
    }-*/;

    public final native Rule top(double top) /*-{
        return this.top(top);
    }-*/;

    public final native Rule top(DoubleFunction<?> f) /*-{
        return this.top(@org.thechiselgroup.choosel.protovis.client.Mark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunction;)(this,f));
    }-*/;

    public final native Rule top(Scale scale) /*-{
        return this.top(scale);
    }-*/;

    public final native Rule type(String type) /*-{
        return this.type(type);
    }-*/;

    public final native Rule visible(boolean visible) /*-{
        return this.visible(visible);
    }-*/;

    public final native Rule width(double width) /*-{
        return this.width(width);
    }-*/;

    public final native Rule width(DoubleFunction<?> f) /*-{
        return this.width(@org.thechiselgroup.choosel.protovis.client.Mark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunction;)(this,f));
    }-*/;
    // @formatter:on

}
