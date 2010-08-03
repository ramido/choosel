package org.thechiselgroup.choosel.client.ui.widget.chart.protovis;

import com.google.gwt.core.client.JavaScriptObject;

// @formatter:off
/**
 * 
 * @author Bradley Blashko
 * 
 */
public class Scale extends JavaScriptObject {

    public final static native Scale linear(double from, double to) /*-{
        return $wnd.pv.Scale.linear(from, to);
    }-*/;

    protected Scale() {
    }

    public final native Scale range(double min, double max) /*-{
        return this.range(min, max);
    }-*/;

    public final native String tickFormat(String tick) /*-{
        return this.tickFormat(tick);
    }-*/;

    public final native JavaScriptObject ticks() /*-{
        return this.ticks();
    }-*/;

    public final native JavaScriptObject ticks(Number ticks) /*-{
        return this.ticks(ticks);
    }-*/;

}
// @formatter:on
