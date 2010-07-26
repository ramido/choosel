package org.thechiselgroup.choosel.client.ui.widget.chart.protovis;

import com.google.gwt.core.client.JavaScriptObject;

public class Wedge extends Mark {

    // @formatter:off        
    public static native Wedge createWedge() /*-{
        return $wnd.pv.Wedge;
    }-*/;
    // @formatter:on 

    protected Wedge() {
    }

    // @formatter:off
    public final native <T extends Mark> T add(T mark) /*-{
        return this.add(mark);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Wedge anchor(String anchor) /*-{
        return this.anchor(anchor);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Wedge angle(double angle) /*-{
        return this.angle(angle);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Wedge angle(ProtovisFunctionDouble f) /*-{
        return this.angle(@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::getFunctionDouble(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisFunctionDouble;)(this,f));
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Wedge bottom(Number bottom) /*-{
        return this.bottom(bottom);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Wedge bottom(ProtovisFunctionDouble f) /*-{
        return this.bottom(@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::getFunctionDouble(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisFunctionDouble;)(this,f));
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Wedge childIndex(Number childIndex) /*-{
        return this.childIndex(childIndex);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Wedge cursor(String cursor) /*-{
        return this.cursor(cursor);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Wedge data(JavaScriptObject data) /*-{
        return this.data(data);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Wedge def(String name) /*-{
        return this.def(name);
    }-*/;
    // @formatter:on

    // TODO Likely needs some fixing
    // @formatter:off
    public final native Wedge def(String name, ProtovisFunctionDouble f) /*-{
        return this.def(name, @org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::getFunctionDouble(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisFunctionDouble;)(this,f));
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Wedge def(String name, String constant) /*-{
        return this.def(name, constant);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Wedge defaults(Mark mark) /*-{
        return this.defaults(mark);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Wedge event(String eventType, ProtovisEventHandler handler) /*-{
        return this.event(eventType, this.@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::registerEvent(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisEventHandler;)(this, handler));
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Wedge events(String events) /*-{
        return this.events(events);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Wedge index(Number index) /*-{
        return this.index(index);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Wedge innerRadius(double innerRadius) /*-{
        return this.innerRadius(innerRadius);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Wedge innerRadius(ProtovisFunctionDouble f) /*-{
        return this.innerRadius(@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::getFunctionDouble(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisFunctionDouble;)(this,f));
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Wedge left(double left) /*-{
        return this.left(left);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Wedge left(ProtovisFunctionDouble f) /*-{
        return this.left(@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::getFunctionDouble(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisFunctionDouble;)(this,f));
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Wedge outerRadius(double outerRadius) /*-{
        return this.outerRadius(outerRadius);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Wedge outerRadius(ProtovisFunctionDouble f) /*-{
        return this.outerRadius(@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::getFunctionDouble(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisFunctionDouble;)(this,f));
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Wedge parent(Panel panel) /*-{
        return this.parent(panel);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Wedge proto(Mark mark) /*-{
        return this.proto(mark);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native void render() /*-{
        return this.render();
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Wedge reverse(boolean reverse) /*-{
        return this.reverse(reverse);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Wedge right(double right) /*-{
        return this.right(right);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Wedge right(ProtovisFunctionDouble f) /*-{
        return this.right(@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::getFunctionDouble(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisFunctionDouble;)(this,f));
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Wedge root(Panel panel) /*-{
        return this.root(panel);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Wedge scale(Number scale) /*-{
        return this.scale(scale);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Wedge title(String title) /*-{
        return this.title(title);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Wedge top(double top) /*-{
        return this.top(top);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Wedge top(ProtovisFunctionDouble f) /*-{
        return this.top(@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::getFunctionDouble(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisFunctionDouble;)(this,f));
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Wedge type(String type) /*-{
        return this.type(type);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Wedge visible(boolean visible) /*-{
        return this.visible(visible);
    }-*/;
    // @formatter:on

}
