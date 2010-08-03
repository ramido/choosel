package org.thechiselgroup.choosel.client.ui.widget.chart.protovis;

import com.google.gwt.core.client.JavaScriptObject;

// @formatter:off
/**
 * 
 * @author Bradley Blashko
 * 
 */
public class Behavior extends JavaScriptObject {

    public final static native Behavior select() /*-{
        return $wnd.pv.Behavior.select();
    }-*/;

    protected Behavior() {
    }

}
// @formatter:on
