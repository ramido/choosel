package org.thechiselgroup.choosel.client.ui.widget.chart.protovis;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * 
 * @author Bradley Blashko
 * 
 */
public class Behavior extends JavaScriptObject {

    // @formatter:off
    public final static native Behavior select() /*-{
        return $wnd.pv.Behavior.select();
    }-*/;
    // @formatter:on

    protected Behavior() {
    }

}
