package org.thechiselgroup.choosel.client.ui.widget.chart.protovis;

import com.google.gwt.core.client.JavaScriptObject;

public class Dot extends Mark {

    // @formatter:off        
    public static native Dot createDot() /*-{
        return $wnd.pv.Dot;
    }-*/;
    // @formatter:on 

    protected Dot() {
    }

    // @formatter:off
    public final native <T extends Mark> T add(T mark) /*-{
        return this.add(mark);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Dot anchor(String anchor) /*-{
        return this.anchor(anchor);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Dot angle(double angle) /*-{
        return this.angle(angle);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Dot angle(ProtovisFunctionDouble f) /*-{
        return this.angle(@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::getFunctionDouble(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisFunctionDouble;)(this,f));
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Dot bottom(Number bottom) /*-{
        return this.bottom(bottom);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Dot bottom(ProtovisFunctionDouble f) /*-{
        return this.bottom(@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::getFunctionDouble(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisFunctionDouble;)(this,f));
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Dot childIndex(Number childIndex) /*-{
        return this.childIndex(childIndex);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Dot cursor(String cursor) /*-{
        return this.cursor(cursor);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Dot data(JavaScriptObject data) /*-{
        return this.data(data);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Dot def(String name) /*-{
        return this.def(name);
    }-*/;
    // @formatter:on

    // TODO Likely needs some fixing
    // @formatter:off
    public final native Dot def(String name, ProtovisFunctionDouble f) /*-{
        return this.def(name, @org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::getFunctionDouble(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisFunctionDouble;)(this,f));
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Dot def(String name, String constant) /*-{
        return this.def(name, constant);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Dot defaults(Mark mark) /*-{
        return this.defaults(mark);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Dot event(String eventType, ProtovisEventHandler handler) /*-{
        return this.event(eventType, this.@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::registerEvent(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisEventHandler;)(this, handler));
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Dot events(String events) /*-{
        return this.events(events);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Dot fillStyle(ProtovisFunctionString f) /*-{
        return this.fillStyle(@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::getFunctionString(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisFunctionString;)(this,f));
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Dot fillStyle(String fillStyle) /*-{
        return this.fillStyle(fillStyle);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Dot index(Number index) /*-{
        return this.index(index);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Dot left(double left) /*-{
        return this.left(left);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Dot left(ProtovisFunctionDouble f) /*-{
        return this.left(@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::getFunctionDouble(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisFunctionDouble;)(this,f));
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Dot lineWidth(Number lineWidth) /*-{
        return this.lineWidth(lineWidth);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Dot parent(Panel panel) /*-{
        return this.parent(panel);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Dot proto(Mark mark) /*-{
        return this.proto(mark);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Dot radius(Number radius) /*-{
        return this.radius(radius);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native void render() /*-{
        return this.render();
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Dot reverse(boolean reverse) /*-{
        return this.reverse(reverse);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Dot right(double right) /*-{
        return this.right(right);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Dot right(ProtovisFunctionDouble f) /*-{
        return this.right(@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::getFunctionDouble(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisFunctionDouble;)(this,f));
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Dot root(Panel panel) /*-{
        return this.root(panel);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Dot scale(Number scale) /*-{
        return this.scale(scale);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Dot shape(String shape) /*-{
        return this.shape(shape);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Dot size(Number size) /*-{
        return this.size(size);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Dot strokeStyle(String strokeStyle) /*-{
        return this.strokeStyle(strokeStyle);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Dot title(String title) /*-{
        return this.title(title);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Dot top(double top) /*-{
        return this.top(top);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Dot top(ProtovisFunctionDouble f) /*-{
        return this.top(@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::getFunctionDouble(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisFunctionDouble;)(this,f));
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Dot type(String type) /*-{
        return this.type(type);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Dot visible(boolean visible) /*-{
        return this.visible(visible);
    }-*/;
    // @formatter:on

}
