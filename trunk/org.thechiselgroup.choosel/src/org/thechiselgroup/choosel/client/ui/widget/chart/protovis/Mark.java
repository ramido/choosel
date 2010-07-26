package org.thechiselgroup.choosel.client.ui.widget.chart.protovis;

import com.google.gwt.core.client.JavaScriptObject;

public abstract class Mark extends JavaScriptObject {

    // @formatter:off
    protected static final native JavaScriptObject getFunctionDouble(
            JavaScriptObject _this, ProtovisFunctionDouble f) /*-{
        return $entry(function(d) 
            {return f.@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.ProtovisFunctionDouble::f(Ljava/lang/String;I)(d.toString(),_this.index);})
    }-*/;
    // @formatter:on

    // @formatter:off
    protected static final native JavaScriptObject getFunctionString(
            JavaScriptObject _this, ProtovisFunctionString f) /*-{
        return $entry(function(d) 
            {return f.@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.ProtovisFunctionString::f(Ljava/lang/String;I)(d.toString(),_this.index);})
    }-*/;
    // @formatter:on

    // @formatter:off
    protected static final native JavaScriptObject registerEvent(
            JavaScriptObject _this, ProtovisEventHandler handler) /*-{
        return $entry(function() 
            {return handler.@org.thechiselgroup.choosel.client.ui.widget.chart.protovis.ProtovisEventHandler::handleEvent(Lcom/google/gwt/user/client/Event;I)($wnd.pv.event, _this.index);})
    }-*/;
    // @formatter:on

    protected Mark() {
    }

}
