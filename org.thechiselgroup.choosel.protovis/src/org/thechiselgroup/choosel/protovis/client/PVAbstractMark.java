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

import org.thechiselgroup.choosel.protovis.client.functions.JsFunctionUtils;
import org.thechiselgroup.choosel.protovis.client.functions.PVBooleanFunction;
import org.thechiselgroup.choosel.protovis.client.functions.PVBooleanFunctionDoubleArg;
import org.thechiselgroup.choosel.protovis.client.functions.PVDoubleFunction;
import org.thechiselgroup.choosel.protovis.client.functions.PVDoubleFunctionDoubleArg;
import org.thechiselgroup.choosel.protovis.client.functions.PVEventHandler;
import org.thechiselgroup.choosel.protovis.client.functions.PVFunction;
import org.thechiselgroup.choosel.protovis.client.functions.PVStringFunction;
import org.thechiselgroup.choosel.protovis.client.util.JsUtils;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * A base class for all types of marks/visualizations possible with Protovis
 * (e.g. Bar, Line, Label). This class is not directly instantiable. Any of
 * these possible marks subclasses Mark, implements any methods associated with
 * pv.Mark in the Protovis API, and adds its own method implementations.
 * 
 * Furthermore, any mark that is a subclass of a subclass of Mark in the
 * Protovis API (e.g. Panel, which is a subclass of Bar, which is a subclass of
 * Mark) subclasses Mark directly. This design decision was made due to the fact
 * that any class extending {@link JavaScriptObject} must have all of its
 * methods either be final or private.
 * 
 * Another potential design decision would be to have all methods for any type
 * of Protovis mark in one massive class. However, this was decided against
 * since not all methods would be applicable to any type of mark, and thus
 * Eclipse's auto-complete would be rendered unusable.
 * 
 * @author Bradley Blashko
 * @author Lars Grammel
 */
public abstract class PVAbstractMark<T extends PVAbstractMark<T>> extends
        JavaScriptObject {

    protected PVAbstractMark() {
    }

    public final native <S extends PVAbstractMark<S>> S add(S mark) /*-{
        return this.add(mark);
    }-*/;

    public final native T anchor(PVStringFunction<T, ?> f) /*-{
        return this.anchor(@org.thechiselgroup.choosel.protovis.client.functions.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/functions/PVStringFunction;)(f));
    }-*/;

    public final native T anchor(String anchor) /*-{
        return this.anchor(anchor);
    }-*/;

    public final native T bottom(double bottom) /*-{
        return this.bottom(bottom);
    }-*/;

    public final native T bottom(PVDoubleFunction<T, ?> f) /*-{
        return this.bottom(@org.thechiselgroup.choosel.protovis.client.functions.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/functions/PVDoubleFunction;)(f));
    }-*/;

    public final native T bottom(PVDoubleFunctionDoubleArg<T> f) /*-{
        return this.bottom(@org.thechiselgroup.choosel.protovis.client.functions.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/functions/PVDoubleFunctionDoubleArg;)(f));
    }-*/;

    public final native T bottom(PVScale scale) /*-{
        return this.bottom(scale);
    }-*/;

    public final native int childIndex() /*-{
        return this.childIndex;
    }-*/;

    public final native T cursor(String cursor) /*-{
        return this.cursor(cursor);
    }-*/;

    public final <S> T data(Iterable<S> data) {
        return this.data(JsUtils.toJsArrayGeneric(data));
    }

    public final native T data(JavaScriptObject data) /*-{
        return this.data(data);
    }-*/;

    public final native T data(PVFunction<T, ?, ?> f) /*-{
        return this.data(@org.thechiselgroup.choosel.protovis.client.functions.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/functions/PVFunction;)(f));
    }-*/;

    public final <S> T data(S[] data) {
        return this.data(JsUtils.toJsArrayGeneric(data));
    }

    public final T dataDouble(double... data) {
        return this.data(JsUtils.toJsArrayNumber(data));
    }

    public final T dataInt(int... data) {
        return this.data(JsUtils.toJsArrayInteger(data));
    }

    public final native JavaScriptObject def(String name) /*-{
        return this.def(name);
    }-*/;

    // XXX Likely needs some fixing
    public final native T def(String name, PVDoubleFunction<T, ?> f) /*-{
        return this.def(name, @org.thechiselgroup.choosel.protovis.client.functions.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/functions/PVDoubleFunction;)(f));
    }-*/;

    public final native T def(String name, String constant) /*-{
        return this.def(name, constant);
    }-*/;

    public final native PVMark defaults() /*-{
        return this.defaults;
    }-*/;

    public final native T event(String eventType, PVEventHandler<T> handler) /*-{
        return this.event(eventType, @org.thechiselgroup.choosel.protovis.client.functions.JsFunctionUtils::toJavaScriptEventFunction(Lorg/thechiselgroup/choosel/protovis/client/functions/PVEventHandler;)( handler));
    }-*/;

    public final native T events(String events) /*-{
        return this.events(events);
    }-*/;

    public final native int index() /*-{
        return this.index;
    }-*/;

    public final native T left(double left) /*-{
        return this.left(left);
    }-*/;

    public final native T left(PVDoubleFunction<T, ?> f) /*-{
        return this.left(@org.thechiselgroup.choosel.protovis.client.functions.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/functions/PVDoubleFunction;)(f));
    }-*/;

    public final native T left(PVDoubleFunctionDoubleArg<T> f) /*-{
        return this.left(@org.thechiselgroup.choosel.protovis.client.functions.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/functions/PVDoubleFunctionDoubleArg;)(f));
    }-*/;

    public final native T left(PVScale scale) /*-{
        return this.left(scale);
    }-*/;

    /**
     * Wrapper for
     * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.Mark.html#margin">pv.Mark.margin()</a></code>
     * .
     */
    public final native T margin(int margin) /*-{
        return this.margin(margin);
    }-*/;

    public final native PVPanel parent() /*-{
        return this.parent;
    }-*/;

    public final native PVMark proto(JsFunctionUtils mark) /*-{
        return this.proto(mark);
    }-*/;

    public final native void render() /*-{
        return this.render();
    }-*/;

    public final native T reverse(boolean reverse) /*-{
        return this.reverse(reverse);
    }-*/;

    public final native T right(double right) /*-{
        return this.right(right);
    }-*/;

    public final native T right(PVDoubleFunction<T, ?> f) /*-{
        return this.right(@org.thechiselgroup.choosel.protovis.client.functions.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/functions/PVDoubleFunction;)(f));
    }-*/;

    public final native T right(PVDoubleFunctionDoubleArg<T> f) /*-{
        return this.right(@org.thechiselgroup.choosel.protovis.client.functions.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/functions/PVDoubleFunctionDoubleArg;)(f));
    }-*/;

    public final native T right(PVScale scale) /*-{
        return this.right(scale);
    }-*/;

    public final native PVPanel root() /*-{
        return this.root();
    }-*/;

    public final native double scale() /*-{
        return this.scale;
    }-*/;

    public final native T scale(double scale) /*-{
        return this.scale(scale);
    }-*/;

    public final native T title(PVStringFunction<T, ?> f) /*-{
        return this.title(@org.thechiselgroup.choosel.protovis.client.functions.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/functions/PVStringFunction;)(f));
    }-*/;

    public final native T title(String title) /*-{
        return this.title(title);
    }-*/;

    /**
     * Wrapper for
     * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.Mark.html#top">pv.Mark.top()</a></code>
     * .
     */
    public final native T top(double top) /*-{
        return this.top(top);
    }-*/;

    /**
     * Wrapper for
     * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.Mark.html#top">pv.Mark.top()</a></code>
     * .
     */
    public final native T top(PVDoubleFunction<T, ?> f) /*-{
        return this.top(@org.thechiselgroup.choosel.protovis.client.functions.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/functions/PVDoubleFunction;)(f));
    }-*/;

    /**
     * Wrapper for
     * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.Mark.html#top">pv.Mark.top()</a></code>
     * .
     */
    public final native T top(PVDoubleFunctionDoubleArg<T> f) /*-{
        return this.top(@org.thechiselgroup.choosel.protovis.client.functions.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/functions/PVDoubleFunctionDoubleArg;)(f));
    }-*/;

    public final native T top(PVScale scale) /*-{
        return this.top(scale);
    }-*/;

    public final native T type(String type) /*-{
        return this.type(type);
    }-*/;

    /**
     * Wrapper for
     * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.Mark.html#visible">pv.Mark.visible()</a></code>
     * .
     */
    public final native T visible(boolean visible) /*-{
        return this.visible(visible);
    }-*/;

    /**
     * Wrapper for
     * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.Mark.html#visible">pv.Mark.visible()</a></code>
     * .
     */
    public final native T visible(PVBooleanFunction<T, ?> f) /*-{
        return this.visible(@org.thechiselgroup.choosel.protovis.client.functions.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/functions/PVBooleanFunction;)(f));
    }-*/;

    /**
     * Wrapper for
     * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.Mark.html#visible">pv.Mark.visible()</a></code>
     * .
     */
    public final native T visible(PVBooleanFunctionDoubleArg<T> f) /*-{
        return this.visible(@org.thechiselgroup.choosel.protovis.client.functions.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/functions/PVBooleanFunctionDoubleArg;)(f));
    }-*/;
}