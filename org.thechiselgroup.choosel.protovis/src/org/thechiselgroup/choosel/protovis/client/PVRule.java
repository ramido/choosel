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
 * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.Rule.html">pv.Rule</a></code>
 * .
 * 
 * @author Bradley Blashko
 * @author Lars Grammel
 */
public class PVRule extends PVMark {

    // @formatter:off
    public static native PVRule createRule() /*-{
        return $wnd.pv.Rule;
    }-*/;

    protected PVRule() {
    }

    public final native <T extends PVMark> T add(T mark) /*-{
        return this.add(mark);
    }-*/;

    public final native PVRule anchor(String anchor) /*-{
        return this.anchor(anchor);
    }-*/;

    public final native PVRule bottom(double bottom) /*-{
        return this.bottom(bottom);
    }-*/;

    public final native PVRule bottom(DoubleFunction<?> f) /*-{
        return this.bottom(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunction;)(this,f));
    }-*/;

    public final native PVRule bottom(PVScale scale) /*-{
        return this.bottom(scale);
    }-*/;

    public final native PVRule childIndex(int childIndex) /*-{
        return this.childIndex(childIndex);
    }-*/;

    public final native PVRule cursor(String cursor) /*-{
        return this.cursor(cursor);
    }-*/;

    public final native PVRule data(JavaScriptObject data) /*-{
        return this.data(data);
    }-*/;

    public final native PVRule def(String name) /*-{
        return this.def(name);
    }-*/;

    // TODO Likely needs some fixing
    public final native PVRule def(String name, DoubleFunction<?> f) /*-{
        return this.def(name, @org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunction;)(this,f));
    }-*/;

    public final native PVRule def(String name, String constant) /*-{
        return this.def(name, constant);
    }-*/;

    public final native PVRule defaults(PVMark mark) /*-{
        return this.defaults(mark);
    }-*/;

    public final native PVRule event(String eventType,
            ProtovisEventHandler handler) /*-{
        return this.event(eventType, @org.thechiselgroup.choosel.protovis.client.PVMark::registerEvent(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/ProtovisEventHandler;)(this, handler));
    }-*/;

    public final native PVRule events(String events) /*-{
        return this.events(events);
    }-*/;

    public final native PVRule height(double height) /*-{
        return this.height(height);
    }-*/;

    public final native PVRule height(DoubleFunction<?> f) /*-{
        return this.height(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunction;)(this,f));
    }-*/;

    public final native PVRule index(int index) /*-{
        return this.index(index);
    }-*/;

    public final native PVRule left(double left) /*-{
        return this.left(left);
    }-*/;

    public final native PVRule left(DoubleFunction<?> f) /*-{
        return this.left(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunction;)(this,f));
    }-*/;

    public final native PVRule left(PVScale scale) /*-{
        return this.left(scale);
    }-*/;

    public final native PVRule lineWidth(double lineWidth) /*-{
        return this.lineWidth(lineWidth);
    }-*/;

    public final native PVRule parent(PVPanel panel) /*-{
        return this.parent(panel);
    }-*/;

    public final native PVRule proto(PVMark mark) /*-{
        return this.proto(mark);
    }-*/;

    public final native void render() /*-{
        return this.render();
    }-*/;

    public final native PVRule reverse(boolean reverse) /*-{
        return this.reverse(reverse);
    }-*/;

    public final native PVRule right(double right) /*-{
        return this.right(right);
    }-*/;

    public final native PVRule right(DoubleFunction<?> f) /*-{
        return this.right(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunction;)(this,f));
    }-*/;

    public final native PVRule right(PVScale scale) /*-{
        return this.right(scale);
    }-*/;

    public final native PVRule root(PVPanel panel) /*-{
        return this.root(panel);
    }-*/;

    public final native PVRule scale(double scale) /*-{
        return this.scale(scale);
    }-*/;

    /**
     * Wrapper for
     * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.Rule.html#strokeStyle">strokeStyle()</a></code>
     * .
     */
    public final native PVRule strokeStyle(String strokeStyle) /*-{
        return this.strokeStyle(strokeStyle);
    }-*/;

    /**
     * Wrapper for
     * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.Rule.html#strokeStyle">strokeStyle()</a></code>
     * .
     */
    public final native PVRule strokeStyle(StringFunctionIntArg f) /*-{
        return this.strokeStyle(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/StringFunctionIntArg;)(this,f));
    }-*/;

    /**
     * Wrapper for
     * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.Rule.html#strokeStyle">strokeStyle()</a></code>
     * .
     */
    public final native PVRule strokeStyle(StringFunctionDoubleArg f) /*-{
        return this.strokeStyle(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/StringFunctionDoubleArg;)(this,f));
    }-*/;

    public final native PVRule title(String title) /*-{
        return this.title(title);
    }-*/;

    public final native PVRule top(double top) /*-{
        return this.top(top);
    }-*/;

    public final native PVRule top(DoubleFunction<?> f) /*-{
        return this.top(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunction;)(this,f));
    }-*/;

    public final native PVRule top(PVScale scale) /*-{
        return this.top(scale);
    }-*/;

    public final native PVRule type(String type) /*-{
        return this.type(type);
    }-*/;

    /**
     * Wrapper for
     * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.Mark.html#visible">pv.Mark.visible()</a></code>
     * .
     */
    public final native PVRule visible(boolean visible) /*-{
        return this.visible(visible);
    }-*/;

    /**
     * Wrapper for
     * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.Mark.html#visible">pv.Mark.visible()</a></code>
     * .
     */
    public final native PVRule visible(BooleanFunctionDoubleArg f) /*-{
        return this.visible(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/BooleanFunctionDoubleArg;)(this,f));
    }-*/;

    public final native PVRule width(double width) /*-{
        return this.width(width);
    }-*/;

    public final native PVRule width(DoubleFunction<?> f) /*-{
        return this.width(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunction;)(this,f));
    }-*/;
    // @formatter:on

}
