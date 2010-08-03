package org.thechiselgroup.choosel.client.ui.widget.chart.protovis;

import com.google.gwt.core.client.JavaScriptObject;

//@formatter:off
/**
 * 
 * @author Bradley Blashko
 * 
 */
public class Dot extends Mark {

    public static native Dot createDot() /*-{
        return $wnd.pv.Dot;
    }-*/;

    protected Dot() {
    }

    public final native <T extends Mark> T add(T mark) /*-{
        return this.add(mark);
    }-*/;

    public final native Dot anchor(String anchor) /*-{
        return this.anchor(anchor);
    }-*/;

    public final native Dot angle(double angle) /*-{
        return this.angle(angle);
    }-*/;

    public final native Dot angle(ProtovisFunctionDouble f) /*-{
        return this.angle(@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::getFunctionDouble(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisFunctionDouble;)(this,f));
    }-*/;

    public final native Dot bottom(Number bottom) /*-{
        return this.bottom(bottom);
    }-*/;

    public final native Dot bottom(ProtovisFunctionDouble f) /*-{
        return this.bottom(@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::getFunctionDouble(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisFunctionDouble;)(this,f));
    }-*/;

    public final native Dot childIndex(Number childIndex) /*-{
        return this.childIndex(childIndex);
    }-*/;

    public final native Dot cursor(String cursor) /*-{
        return this.cursor(cursor);
    }-*/;

    public final native Dot data(JavaScriptObject data) /*-{
        return this.data(data);
    }-*/;

    public final native Dot def(String name) /*-{
        return this.def(name);
    }-*/;

    // TODO Likely needs some fixing
    public final native Dot def(String name, ProtovisFunctionDouble f) /*-{
        return this.def(name, @org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::getFunctionDouble(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisFunctionDouble;)(this,f));
    }-*/;

    public final native Dot def(String name, String constant) /*-{
        return this.def(name, constant);
    }-*/;

    public final native Dot defaults(Mark mark) /*-{
        return this.defaults(mark);
    }-*/;

    public final native Dot event(String eventType, ProtovisEventHandler handler) /*-{
        return this.event(eventType, @org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::registerEvent(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisEventHandler;)(this, handler));
    }-*/;

    public final native Dot events(String events) /*-{
        return this.events(events);
    }-*/;

    public final native Dot fillStyle(ProtovisFunctionString f) /*-{
        return this.fillStyle(@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::getFunctionString(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisFunctionString;)(this,f));
    }-*/;

    public final native Dot fillStyle(String fillStyle) /*-{
        return this.fillStyle(fillStyle);
    }-*/;

    public final native Dot index(Number index) /*-{
        return this.index(index);
    }-*/;

    public final native Dot left(double left) /*-{
        return this.left(left);
    }-*/;

    public final native Dot left(ProtovisFunctionDouble f) /*-{
        return this.left(@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::getFunctionDouble(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisFunctionDouble;)(this,f));
    }-*/;

    public final native Dot lineWidth(Number lineWidth) /*-{
        return this.lineWidth(lineWidth);
    }-*/;

    public final native Dot parent(Panel panel) /*-{
        return this.parent(panel);
    }-*/;

    public final native Dot proto(Mark mark) /*-{
        return this.proto(mark);
    }-*/;

    public final native Dot radius(Number radius) /*-{
        return this.radius(radius);
    }-*/;

    public final native void render() /*-{
        return this.render();
    }-*/;

    public final native Dot reverse(boolean reverse) /*-{
        return this.reverse(reverse);
    }-*/;

    public final native Dot right(double right) /*-{
        return this.right(right);
    }-*/;

    public final native Dot right(ProtovisFunctionDouble f) /*-{
        return this.right(@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::getFunctionDouble(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisFunctionDouble;)(this,f));
    }-*/;

    public final native Dot root(Panel panel) /*-{
        return this.root(panel);
    }-*/;

    public final native Dot scale(Number scale) /*-{
        return this.scale(scale);
    }-*/;

    public final native Dot shape(String shape) /*-{
        return this.shape(shape);
    }-*/;

    public final native Dot size(Number size) /*-{
        return this.size(size);
    }-*/;

    public final native Dot strokeStyle(String strokeStyle) /*-{
        return this.strokeStyle(strokeStyle);
    }-*/;

    public final native Dot title(String title) /*-{
        return this.title(title);
    }-*/;

    public final native Dot top(double top) /*-{
        return this.top(top);
    }-*/;

    public final native Dot top(ProtovisFunctionDouble f) /*-{
        return this.top(@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::getFunctionDouble(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisFunctionDouble;)(this,f));
    }-*/;

    public final native Dot type(String type) /*-{
        return this.type(type);
    }-*/;

    public final native Dot visible(boolean visible) /*-{
        return this.visible(visible);
    }-*/;

}
// @formatter:on