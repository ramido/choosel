package org.thechiselgroup.choosel.client.util;

import com.google.gwt.core.client.JavaScriptObject;

public class JavaScriptUtils {

    public static native double toDouble(JavaScriptObject jso) /*-{
        return jso;
    }-*/;

    public static native int toInt(JavaScriptObject jso) /*-{
        return jso;
    }-*/;

    public static native String toString(JavaScriptObject jso) /*-{
        return jso;
    }-*/;

}
