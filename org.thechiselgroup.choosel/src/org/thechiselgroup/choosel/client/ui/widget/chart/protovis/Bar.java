package org.thechiselgroup.choosel.client.ui.widget.chart.protovis;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * 
 * @author Bradley Blashko
 * 
 */
public class Bar extends Mark {

    public static native Bar createBar() /*-{
        return $wnd.pv.Bar;
    }-*/;

    protected Bar() {
    }

    // @formatter:off
    public final native <T extends Mark> T add(T mark) /*-{
        return this.add(mark);
    }-*/;

    public final native Bar anchor(String anchor) /*-{
        return this.anchor(anchor);
    }-*/;

    public final native Bar bottom(Number bottom) /*-{
        return this.bottom(bottom);
    }-*/;

    public final native Bar bottom(ProtovisFunctionDouble f) /*-{
        return this.bottom(@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::getFunctionDouble(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisFunctionDouble;)(this,f));
    }-*/;

    public final native Bar childIndex(Number childIndex) /*-{
        return this.childIndex(childIndex);
    }-*/;

    public final native Bar cursor(String cursor) /*-{
        return this.cursor(cursor);
    }-*/;

    public final native Bar data(JavaScriptObject data) /*-{
        return this.data(data);
    }-*/;

    public final native Bar def(String name) /*-{
        return this.def(name);
    }-*/;

    // TODO Likely needs some fixing
    public final native Bar def(String name, ProtovisFunctionDouble f) /*-{
        return this.def(name, @org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::getFunctionDouble(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisFunctionDouble;)(this,f));
    }-*/;

    public final native Bar def(String name, String constant) /*-{
        return this.def(name, constant);
    }-*/;

    public final native Bar defaults(Mark mark) /*-{
        return this.defaults(mark);
    }-*/;

    public final native Bar event(String eventType, ProtovisEventHandler handler) /*-{
        return this.event(eventType, @org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::registerEvent(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisEventHandler;)(this, handler));
    }-*/;

    public final native Bar events(String events) /*-{
        return this.events(events);
    }-*/;

    public final native Bar fillStyle(ProtovisFunctionString f) /*-{
        return this.fillStyle(@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::getFunctionString(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisFunctionString;)(this,f));
    }-*/;

    public final native Bar fillStyle(String colour) /*-{
        return this.fillStyle(colour);
    }-*/;

    public final native Bar height(Number height) /*-{
        return this.height(height);
    }-*/;

    public final native Bar height(ProtovisFunctionDouble f) /*-{
        return this.height(@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::getFunctionDouble(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisFunctionDouble;)(this,f));
    }-*/;

    public final native Bar index(Number index) /*-{
        return this.index(index);
    }-*/;

    public final native Bar left(double left) /*-{
        return this.left(left);
    }-*/;

    public final native Bar left(ProtovisFunctionDouble f) /*-{
        return this.left(@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::getFunctionDouble(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisFunctionDouble;)(this,f));
    }-*/;

    public final native Bar lineWidth(Number lineWidth) /*-{
        return this.lineWidth(lineWidth);
    }-*/;

    public final native Bar parent(Panel panel) /*-{
        return this.parent(panel);
    }-*/;

    public final native Bar proto(Mark mark) /*-{
        return this.proto(mark);
    }-*/;

    public final native void render() /*-{
        return this.render();
    }-*/;

    public final native Bar reverse(boolean reverse) /*-{
        return this.reverse(reverse);
    }-*/;

    public final native Bar right(double right) /*-{
        return this.right(right);
    }-*/;

    public final native Bar right(ProtovisFunctionDouble f) /*-{
        return this.right(@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::getFunctionDouble(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisFunctionDouble;)(this,f));
    }-*/;

    public final native Bar root(Panel panel) /*-{
        return this.root(panel);
    }-*/;

    public final native Bar scale(Number scale) /*-{
        return this.scale(scale);
    }-*/;

    public final native Bar strokeStyle(ProtovisFunctionStringToString f) /*-{
        return this.fillStyle(@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::getFunctionString(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisFunctionString;)(this,f));
    }-*/;

    public final native Bar strokeStyle(String strokeStyle) /*-{
        return this.strokeStyle(strokeStyle);
    }-*/;

    public final native Bar title(String title) /*-{
        return this.title(title);
    }-*/;

    public final native Bar top(double top) /*-{
        return this.top(top);
    }-*/;

    public final native Bar top(ProtovisFunctionDouble f) /*-{
        return this.top(@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::getFunctionDouble(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisFunctionDouble;)(this,f));
    }-*/;

    public final native Bar type(String type) /*-{
        return this.type(type);
    }-*/;

    public final native Bar visible(boolean visible) /*-{
        return this.visible(visible);
    }-*/;

    public final native Bar width(Number width) /*-{
        return this.width(width);
    }-*/;
    
    public final native Bar width(ProtovisFunctionDouble f) /*-{
        return this.width(@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::getFunctionDouble(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisFunctionDouble;)(this,f));
    }-*/;
    // @formatter:on

}
