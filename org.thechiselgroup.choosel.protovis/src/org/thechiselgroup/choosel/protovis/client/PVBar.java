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

// @formatter:off
/**
 * 
 * @author Bradley Blashko
 * @author Lars Grammel 
 */
public class PVBar extends PVMark {

    public static native PVBar createBar() /*-{
        return $wnd.pv.Bar;
    }-*/;

    protected PVBar() {
    }

    public final native <T extends PVMark> T add(T mark) /*-{
        return this.add(mark);
    }-*/;

    public final native PVBar anchor(String anchor) /*-{
        return this.anchor(anchor);
    }-*/;

    public final native PVBar bottom(double bottom) /*-{
        return this.bottom(bottom);
    }-*/;

    public final native PVBar bottom(DoubleFunction<?> f) /*-{
        return this.bottom(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunction;)(this,f));
    }-*/;

    public final native PVBar childIndex(int childIndex) /*-{
        return this.childIndex(childIndex);
    }-*/;

    public final native PVBar cursor(String cursor) /*-{
        return this.cursor(cursor);
    }-*/;

    public final native PVBar data(JavaScriptObject data) /*-{
        return this.data(data);
    }-*/;

    public final native PVBar def(String name) /*-{
        return this.def(name);
    }-*/;

    // TODO Likely needs some fixing
    public final native PVBar def(String name, DoubleFunction<?> f) /*-{
        return this.def(name, @org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunction;)(this,f));
    }-*/;

    public final native PVBar def(String name, String constant) /*-{
        return this.def(name, constant);
    }-*/;

    public final native PVBar defaults(PVMark mark) /*-{
        return this.defaults(mark);
    }-*/;

    public final native PVBar event(String eventType, ProtovisEventHandler handler) /*-{
        return this.event(eventType, @org.thechiselgroup.choosel.protovis.client.PVMark::registerEvent(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/ProtovisEventHandler;)(this, handler));
    }-*/;

    public final native PVBar events(String events) /*-{
        return this.events(events);
    }-*/;

    public final native PVBar fillStyle(StringFunction<?> f) /*-{
        return this.fillStyle(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/StringFunction;)(this,f));
    }-*/;

    public final native PVBar fillStyle(String colour) /*-{
        return this.fillStyle(colour);
    }-*/;

    public final native PVBar height(double height) /*-{
        return this.height(height);
    }-*/;

    public final native PVBar height(DoubleFunction<?> f) /*-{
        return this.height(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunction;)(this,f));
    }-*/;

    public final native PVBar index(double index) /*-{
        return this.index(index);
    }-*/;

    public final native PVBar left(double left) /*-{
        return this.left(left);
    }-*/;

    public final native PVBar left(DoubleFunction<?> f) /*-{
        return this.left(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunction;)(this,f));
    }-*/;

    public final native PVBar lineWidth(double lineWidth) /*-{
        return this.lineWidth(lineWidth);
    }-*/;

    public final native PVBar parent(PVPanel panel) /*-{
        return this.parent(panel);
    }-*/;

    public final native PVBar proto(PVMark mark) /*-{
        return this.proto(mark);
    }-*/;

    public final native void render() /*-{
        return this.render();
    }-*/;

    public final native PVBar reverse(boolean reverse) /*-{
        return this.reverse(reverse);
    }-*/;

    public final native PVBar right(double right) /*-{
        return this.right(right);
    }-*/;

    public final native PVBar right(DoubleFunction<?> f) /*-{
        return this.right(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunction;)(this,f));
    }-*/;

    public final native PVBar root(PVPanel panel) /*-{
        return this.root(panel);
    }-*/;

    public final native PVBar scale(double scale) /*-{
        return this.scale(scale);
    }-*/;

    public final native PVBar strokeStyle(StringFunction<?> f) /*-{
        return this.fillStyle(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/StringFunction;)(this,f));
    }-*/;

    public final native PVBar strokeStyle(String strokeStyle) /*-{
        return this.strokeStyle(strokeStyle);
    }-*/;

    public final native PVBar title(String title) /*-{
        return this.title(title);
    }-*/;

    /**
     * Wrapper for
     * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.Bar.html#top">top()</a></code>
     * .
     */
    public final native PVBar top(double top) /*-{
        return this.top(top);
    }-*/;

    /**
     * Wrapper for
     * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.Bar.html#top">top()</a></code>
     * .
     */
    public final native PVBar top(DoubleFunctionDoubleArg f) /*-{
        return this.top(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunctionDoubleArg;)(this,f));
    }-*/;
    
    /**
     * Wrapper for
     * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.Bar.html#top">top()</a></code>
     * .
     */
    public final native PVBar top(DoubleFunction<?> f) /*-{
        return this.top(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunction;)(this,f));
    }-*/;

    public final native PVBar type(String type) /*-{
        return this.type(type);
    }-*/;

    public final native PVBar visible(boolean visible) /*-{
        return this.visible(visible);
    }-*/;
    
    public final native PVBar visible(BooleanFunction<?> f) /*-{
        return this.visible(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/BooleanFunction;)(this,f));
    }-*/;

    /**
     * Wrapper for
     * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.Bar.html#width">width()</a></code>
     * .
     */
    public final native PVBar width(double width) /*-{
        return this.width(width);
    }-*/;
    
    /**
     * Wrapper for
     * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.Bar.html#width">width()</a></code>
     * .
     */
    public final native PVBar width(PVScale scale) /*-{
        return this.width(scale);
    }-*/;
    
    /**
     * Wrapper for
     * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.Bar.html#width">width()</a></code>
     * .
     */
    public final native PVBar width(DoubleFunction<?> f) /*-{
        return this.width(@org.thechiselgroup.choosel.protovis.client.PVMark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/DoubleFunction;)(this,f));
    }-*/;

}
// @formatter:on
