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
package org.thechiselgroup.choosel.client.ui.widget.chart.protovis;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Element;

/**
 * 
 * @author Bradley Blashko
 * 
 */
public class Panel extends Mark {

    // @formatter:off
    public static native Panel createPanel() /*-{
        return $wnd.pv.Panel;
    }-*/;

    public static native Panel createWindowPanel() /*-{
        return new $wnd.pv.Panel();
    }-*/;
    // @formatter:on

    protected Panel() {
    }

    // @formatter:off
    public final native <T extends Mark> T add(T mark) /*-{
        return this.add(mark);
    }-*/;

    public final native Panel anchor(String anchor) /*-{
        return this.anchor(anchor);
    }-*/;

    
    public final native Panel bottom(double bottom) /*-{
        return this.bottom(bottom);
    }-*/;

    public final native Panel bottom(ProtovisFunctionDouble f) /*-{
        return this.bottom(@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::getFunctionDouble(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisFunctionDouble;)(this,f));
    }-*/;

    public final native Panel canvas(Element element) /*-{
        return this.canvas(element);
    }-*/;

    public final native Panel childIndex(int childIndex) /*-{
        return this.childIndex(childIndex);
    }-*/;

    public final native Panel cursor(String cursor) /*-{
        return this.cursor(cursor);
    }-*/;

    public final native Panel data(JavaScriptObject data) /*-{
        return this.data(data);
    }-*/;

    public final native Panel def(String name) /*-{
        return this.def(name);
    }-*/;

    // TODO Likely needs some fixing
    public final native Panel def(String name, ProtovisFunctionDouble f) /*-{
        return this.def(name, @org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::getFunctionDouble(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisFunctionDouble;)(this,f));
    }-*/;

    public final native Panel def(String name, String constant) /*-{
        return this.def(name, constant);
    }-*/;

    public final native Panel defaults(Mark mark) /*-{
        return this.defaults(mark);
    }-*/;

    public final native Panel event(String eventType, Behavior behavior) /*-{
        return this.event(eventType, behavior);
    }-*/;

    public final native Panel event(String eventType, ProtovisEventHandler handler) /*-{
        return this.event(eventType, @org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::registerEvent(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisEventHandler;)(this, handler));
    }-*/;
    
    public final native Panel events(String events) /*-{
        return this.events(events);
    }-*/;

    public final native Panel fillStyle(ProtovisFunctionString f) /*-{
        return this.fillStyle(@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::getFunctionString(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisFunctionString;)(this,f));
    }-*/;

    public final native Panel fillStyle(String colour) /*-{
        return this.fillStyle(colour);
    }-*/;

    public final native Panel height(double height) /*-{
        return this.height(height);
    }-*/;

    public final native Panel height(ProtovisFunctionDouble f) /*-{
        return this.height(@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::getFunctionDouble(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisFunctionDouble;)(this,f));
    }-*/;

    public final native Panel index(int index) /*-{
        return this.index(index);
    }-*/;

    public final native Panel left(double left) /*-{
        return this.left(left);
    }-*/;

    public final native Panel left(ProtovisFunctionDouble f) /*-{
        return this.left(@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::getFunctionDouble(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisFunctionDouble;)(this,f));
    }-*/;

    public final native Panel parent(Panel panel) /*-{
        return this.parent(panel);
    }-*/;

    public final native Panel proto(Mark mark) /*-{
        return this.proto(mark);
    }-*/;

    public final native void render() /*-{
        return this.render();
    }-*/;

    public final native Panel reverse(boolean reverse) /*-{
        return this.reverse(reverse);
    }-*/;

    public final native Panel right(double right) /*-{
        return this.right(right);
    }-*/;

    public final native Panel right(ProtovisFunctionDouble f) /*-{
        return this.right(@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::getFunctionDouble(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisFunctionDouble;)(this,f));
    }-*/;

    public final native Panel root(Panel panel) /*-{
        return this.root(panel);
    }-*/;

    public final native Panel scale(double scale) /*-{
        return this.scale(scale);
    }-*/;

    public final native Panel title(String title) /*-{
        return this.title(title);
    }-*/;

    public final native Panel top(double top) /*-{
        return this.top(top);
    }-*/;

    public final native Panel top(ProtovisFunctionDouble f) /*-{
        return this.top(@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::getFunctionDouble(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisFunctionDouble;)(this,f));
    }-*/;

    public final native Panel type(String type) /*-{
        return this.type(type);
    }-*/;

    public final native Panel visible(boolean visible) /*-{
        return this.visible(visible);
    }-*/;

    public final native Panel width(double width) /*-{
        return this.width(width);
    }-*/;

    public final native Panel width(ProtovisFunctionDouble f) /*-{
        return this.width(@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::getFunctionDouble(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisFunctionDouble;)(this,f));
    }-*/;
    // @formatter:on

}
