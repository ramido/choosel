package org.thechiselgroup.choosel.client.ui.widget.chart.protovis;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * 
 * @author Bradley Blashko
 * 
 */
public class Rule extends Mark {

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

    public final native Rule bottom(Number bottom) /*-{
        return this.bottom(bottom);
    }-*/;

    public final native Rule bottom(ProtovisFunctionDouble f) /*-{
        return this.bottom(@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::getFunctionDouble(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisFunctionDouble;)(this,f));
    }-*/;

    public final native Rule bottom(Scale scale) /*-{
        return this.bottom(scale);
    }-*/;

    public final native Rule childIndex(Number childIndex) /*-{
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
    public final native Rule def(String name, ProtovisFunctionDouble f) /*-{
        return this.def(name, @org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::getFunctionDouble(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisFunctionDouble;)(this,f));
    }-*/;

    public final native Rule def(String name, String constant) /*-{
        return this.def(name, constant);
    }-*/;

    public final native Rule defaults(Mark mark) /*-{
        return this.defaults(mark);
    }-*/;

    public final native Rule event(String eventType,
            ProtovisEventHandler handler) /*-{
        return this.event(eventType, @org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::registerEvent(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisEventHandler;)(this, handler));
    }-*/;

    public final native Rule events(String events) /*-{
        return this.events(events);
    }-*/;

    public final native Rule height(Number height) /*-{
        return this.height(height);
    }-*/;

    public final native Rule height(ProtovisFunctionDouble f) /*-{
        return this.height(@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::getFunctionDouble(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisFunctionDouble;)(this,f));
    }-*/;

    public final native Rule index(Number index) /*-{
        return this.index(index);
    }-*/;

    public final native Rule left(double left) /*-{
        return this.left(left);
    }-*/;

    public final native Rule left(ProtovisFunctionDouble f) /*-{
        return this.left(@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::getFunctionDouble(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisFunctionDouble;)(this,f));
    }-*/;

    public final native Rule left(Scale scale) /*-{
        return this.left(scale);
    }-*/;

    public final native Rule lineWidth(Number lineWidth) /*-{
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

    public final native Rule right(ProtovisFunctionDouble f) /*-{
        return this.right(@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::getFunctionDouble(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisFunctionDouble;)(this,f));
    }-*/;

    public final native Rule right(Scale scale) /*-{
        return this.right(scale);
    }-*/;

    public final native Rule root(Panel panel) /*-{
        return this.root(panel);
    }-*/;

    public final native Rule scale(Number scale) /*-{
        return this.scale(scale);
    }-*/;

    public final native Rule strokeStyle(ProtovisFunctionStringToString f) /*-{
        return this.strokeStyle(@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::getFunctionStringToString(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisFunctionStringToString;)(this,f));
    }-*/;

    public final native Rule strokeStyle(String strokeStyle) /*-{
        return this.strokeStyle(strokeStyle);
    }-*/;

    public final native Rule title(String title) /*-{
        return this.title(title);
    }-*/;

    public final native Rule top(double top) /*-{
        return this.top(top);
    }-*/;

    public final native Rule top(ProtovisFunctionDouble f) /*-{
        return this.top(@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::getFunctionDouble(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisFunctionDouble;)(this,f));
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

    public final native Rule width(Number width) /*-{
        return this.width(width);
    }-*/;

    public final native Rule width(ProtovisFunctionDouble f) /*-{
        return this.width(@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::getFunctionDouble(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisFunctionDouble;)(this,f));
    }-*/;
}
