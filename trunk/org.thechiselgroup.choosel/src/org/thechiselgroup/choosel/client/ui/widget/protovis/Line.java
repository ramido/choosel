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
package org.thechiselgroup.choosel.client.ui.widget.protovis;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * 
 * @author Bradley Blashko
 * 
 */
public class Line extends Mark {

    // @formatter:off        
    public static native Line createLine() /*-{
        return $wnd.pv.Line;
    }-*/;
    // @formatter:on 

    protected Line() {
    }

    // @formatter:off
    public final native <T extends Mark> T add(T mark) /*-{
        return this.add(mark);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Line anchor(String anchor) /*-{
        return this.anchor(anchor);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Line bottom(double bottom) /*-{
        return this.bottom(bottom);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Line bottom(DoubleFunction<?> f) /*-{
        return this.bottom(@org.thechiselgroup.choosel.client.ui.widget.protovis.Mark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/protovis/DoubleFunction;)(this,f));
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Line childIndex(int childIndex) /*-{
        return this.childIndex(childIndex);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Line cursor(String cursor) /*-{
        return this.cursor(cursor);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Line data(JavaScriptObject data) /*-{
        return this.data(data);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Line def(String name) /*-{
        return this.def(name);
    }-*/;
    // @formatter:on

    // TODO Likely needs some fixing
    // @formatter:off
    public final native Line def(String name, DoubleFunction<?> f) /*-{
        return this.def(name, @org.thechiselgroup.choosel.client.ui.widget.protovis.Mark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/protovis/DoubleFunction;)(this,f));
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Line def(String name, String constant) /*-{
        return this.def(name, constant);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Line defaults(Mark mark) /*-{
        return this.defaults(mark);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Line eccentricity(double eccentricity) /*-{
        return this.eccentricity(eccentricity);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Line event(String eventType, ProtovisEventHandler handler) /*-{
        return this.event(eventType, @org.thechiselgroup.choosel.client.ui.widget.protovis.Mark::registerEvent(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/protovis/ProtovisEventHandler;)(this, handler));
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Line events(String events) /*-{
        return this.events(events);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Line fillStyle(StringFunction<?> f) /*-{
        return this.fillStyle(@org.thechiselgroup.choosel.client.ui.widget.protovis.Mark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/protovis/StringFunction;)(this,f));
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Line fillStyle(String colour) /*-{
        return this.fillStyle(colour);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Line index(int index) /*-{
        return this.index(index);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Line interpolate(String interpolate) /*-{
        return this.interpolate(interpolate);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Line left(double left) /*-{
        return this.left(left);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Line left(DoubleFunction<?> f) /*-{
        return this.left(@org.thechiselgroup.choosel.client.ui.widget.protovis.Mark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/protovis/DoubleFunction;)(this,f));
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Line lineJoin(String lineJoin) /*-{
        return this.lineJoin(lineJoin);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Line lineWidth(double lineWidth) /*-{
        return this.lineWidth(lineWidth);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Line parent(Panel panel) /*-{
        return this.parent(panel);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Line proto(Mark mark) /*-{
        return this.proto(mark);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native void render() /*-{
        return this.render();
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Line reverse(boolean reverse) /*-{
        return this.reverse(reverse);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Line right(double right) /*-{
        return this.right(right);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Line right(DoubleFunction<?> f) /*-{
        return this.right(@org.thechiselgroup.choosel.client.ui.widget.protovis.Mark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/protovis/DoubleFunction;)(this,f));
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Line root(Panel panel) /*-{
        return this.root(panel);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Line scale(double scale) /*-{
        return this.scale(scale);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Line segmented(boolean segmented) /*-{
        return this.segmented(segmented);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Line strokeStyle(String strokeStyle) /*-{
        return this.strokeStyle(strokeStyle);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Line tension(double tension) /*-{
        return this.tension(tension);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Line title(String title) /*-{
        return this.title(title);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Line top(double top) /*-{
        return this.top(top);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Line top(DoubleFunction<?> f) /*-{
        return this.top(@org.thechiselgroup.choosel.client.ui.widget.protovis.Mark::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/protovis/DoubleFunction;)(this,f));
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Line type(String type) /*-{
        return this.type(type);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Line visible(boolean visible) /*-{
        return this.visible(visible);
    }-*/;
    // @formatter:on

}
