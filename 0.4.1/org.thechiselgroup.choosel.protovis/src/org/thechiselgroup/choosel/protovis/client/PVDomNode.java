/*******************************************************************************
 * Copyright 2009, 2010 Lars Grammel 
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0 
 *     
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.  
 *******************************************************************************/
package org.thechiselgroup.choosel.protovis.client;

import java.util.Comparator;

import org.thechiselgroup.choosel.protovis.client.jsutil.JsBooleanFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsUtils;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author Lars Grammel
 * @author Guillaume Godin
 */
public class PVDomNode extends JavaScriptObject {

    public static final native PVDomNode create() /*-{
        return new $wnd.pv.Dom.Node(Number.NaN);
    }-*/;

    public static final native PVDomNode create(Object o, String nodeName,
            double nodeValue) /*-{
        var node = new $wnd.pv.Dom.Node(nodeValue);
        node.nodeName = nodeName;
        node.nodeObject = o;
        return node;
    }-*/;

    protected PVDomNode() {
    }

    public final native void appendChild(PVDomNode childNode) /*-{
        this.appendChild(childNode);
    }-*/;

    public final native PVDomNode firstChild() /*-{
        return this.firstChild;
    }-*/;

    public final native boolean hasNodeValue() /*-{
        return this.nodeValue != undefined;
    }-*/;

    public final native String nodeName() /*-{
        return this.nodeName;
    }-*/;

    /**
     * Returns the original object from which the node was constructed.
     */
    public final native <T> T nodeObject() /*-{
        return this.nodeObject;
    }-*/;

    public final native JavaScriptObject nodes() /*-{
        return this.nodes();
    }-*/;

    public final native <T> T nodeValue() /*-{
        return this.nodeValue;
    }-*/;

    public final native double nodeValueDouble() /*-{
        return this.nodeValue;
    }-*/;

    public final native int nodeValueInt() /*-{
        return this.nodeValue;
    }-*/;

    public final native PVDomNode parentNode() /*-{
        return this.parentNode;
    }-*/;

    public final PVDomNode sort(Comparator<PVDomNode> comparator) {
        return sort(JsUtils.toJsComparator(comparator));
    }

    public final native PVDomNode sort(JavaScriptObject comparator) /*-{
        return this.sort(comparator);
    }-*/;

    public final native PVDomNode toggle(boolean toogle) /*-{
        return this.toggle(toogle);
    }-*/;

    public final native PVDomNode toggle() /*-{
        return this.toggle();
    }-*/;

    public final native PVDomNode toggle(JsBooleanFunction f) /*-{
        return this.toggle(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsBooleanFunction;)(f));
    }-*/;

    public final native boolean toggled() /*-{
        // convert undefined
        return !this.toggled ? false : true;
    }-*/;

}