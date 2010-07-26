package org.thechiselgroup.choosel.client.ui.widget.chart.protovis;

import com.google.gwt.core.client.JavaScriptObject;

public class Scale extends JavaScriptObject {

    // public final static native Scale createScale() /*-{
    // return $wnd.pv.Scale;
    // }-*/;

    // @formatter:off
    public final static native Scale linear(double from, double to) /*-{
        return $wnd.pv.Scale.linear(from, to);
    }-*/;
    // @formatter:on

    protected Scale() {
    }

    // @formatter:off
    public final native Scale range(double min, double max) /*-{
        return this.range(min, max);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native String tickFormat(String tick) /*-{
        return this.tickFormat(tick);
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native JavaScriptObject ticks() /*-{
        return this.ticks();
    }-*/;
    // @formatter:on

    // @formatter:off
    public final native JavaScriptObject ticks(Number ticks) /*-{
        return this.ticks(ticks);
    }-*/;
    // @formatter:on

}
