package org.thechiselgroup.choosel.client.ui.widget.chart.protovis;

import com.google.gwt.core.client.JavaScriptObject;

public class Area extends Mark {

    // @formatter:off        
    public static final native Area createArea() /*-{
        return $wnd.pv.Area;
    }-*/;
    // @formatter:on 

    protected Area() {
    }

    // @formatter:off
    public final native <T extends Mark> T add(T mark) /*-{
        return this.add(mark);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Area anchor(String anchor) /*-{
        return this.anchor(anchor);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Area bottom(Number bottom) /*-{
        return this.bottom(bottom);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Area bottom(ProtovisFunctionDouble f) /*-{
        return this.bottom(@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::getFunctionDouble(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisFunctionDouble;)(this,f));
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Area childIndex(Number childIndex) /*-{
        return this.childIndex(childIndex);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Area cursor(String cursor) /*-{
        return this.cursor(cursor);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Area data(JavaScriptObject data) /*-{
        return this.data(data);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Area def(String name) /*-{
        return this.def(name);
    }-*/;
    // @formatter:on

    // TODO Likely needs some fixing
    // @formatter:off
    public final native Area def(String name, ProtovisFunctionDouble f) /*-{
        return this.def(name, @org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::getFunctionDouble(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisFunctionDouble;)(this,f));
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Area def(String name, String constant) /*-{
        return this.def(name, constant);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Area defaults(Mark mark) /*-{
        return this.defaults(mark);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Area event(String eventType, ProtovisEventHandler handler) /*-{
        return this.event(eventType, this.@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::registerEvent(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisEventHandler;)(this, handler));
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Area events(String events) /*-{
        return this.events(events);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Area fillStyle(ProtovisFunctionDouble f) /*-{
        return this.fillStyle(@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::getFunctionString(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisFunctionString;)(this,f));
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Area fillStyle(String colour) /*-{
        return this.fillStyle(colour);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Area font(String font) /*-{
        return this.font(font);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Area height(Number height) /*-{
        return this.height(height);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Area height(ProtovisFunctionDouble f) /*-{
        return this.height(@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::getFunctionDouble(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisFunctionDouble;)(this,f));
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Area index(Number index) /*-{
        return this.index(index);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Area interpolate(String interpolate) /*-{
        return this.interpolate(interpolate);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Area left(double left) /*-{
        return this.left(left);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Area left(ProtovisFunctionDouble f) /*-{
        return this.left(@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::getFunctionDouble(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisFunctionDouble;)(this,f));
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Area lineWidth(Number lineWidth) /*-{
        return this.lineWidth(lineWidth);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Area parent(Panel panel) /*-{
        return this.parent(panel);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Area proto(Mark mark) /*-{
        return this.proto(mark);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native void render() /*-{
        return this.render();
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Area reverse(boolean reverse) /*-{
        return this.reverse(reverse);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Area right(double right) /*-{
        return this.right(right);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Area right(ProtovisFunctionDouble f) /*-{
        return this.right(@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::getFunctionDouble(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisFunctionDouble;)(this,f));
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Area root(Panel panel) /*-{
        return this.root(panel);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Area scale(Number scale) /*-{
        return this.scale(scale);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Area segmented(boolean segmented) /*-{
        return this.segmented(segmented);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Area strokeStyle(String strokeStyle) /*-{
        return this.strokeStyle(strokeStyle);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Area tension(Number tension) /*-{
        return this.tension(tension);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Area text(String text) /*-{
        return this.text(text);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Area textAlign(String textAlign) /*-{
        return this.textAlign(textAlign);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Area textAngle(Number textAngle) /*-{
        return this.textAngle(textAngle);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Area textBaseline(String textBaseline) /*-{
        return this.textBaseline(textBaseline);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Area textDecoration(String textDecoration) /*-{
        return this.textDecoration(textDecoration);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Area textMargin(Number textMargin) /*-{
        return this.textMargin(textMargin);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Area textShadow(String textShadow) /*-{
        return this.textShadow(textShadow);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Area textStyle(String textStyle) /*-{
        return this.textStyle(textStyle);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Area title(String title) /*-{
        return this.title(title);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Area top(double top) /*-{
        return this.top(top);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Area top(ProtovisFunctionDouble f) /*-{
        return this.top(@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::getFunctionDouble(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisFunctionDouble;)(this,f));
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Area type(String type) /*-{
        return this.type(type);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Area visible(boolean visible) /*-{
        return this.visible(visible);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Area width(Number width) /*-{
        return this.width(width);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native Area width(ProtovisFunctionDouble f) /*-{
        return this.width(@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark::getFunctionDouble(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/client/ui/widget/chart/protovis/ProtovisFunctionDouble;)(this,f));
    }-*/;
    // @formatter:on

}