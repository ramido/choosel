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

import org.thechiselgroup.choosel.protovis.client.jsutil.JsUtils;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * 
 * @author Lars Grammel
 */
public class PVDomNode extends JavaScriptObject {

    public static final native PVDomNode create(Object o, String nodeName,
            double nodeValue) /*-{
        var node = new $wnd.pv.Dom.Node(o);
        node.nodeName = nodeName;
        node.nodeValue = nodeValue;
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

    public final PVDomNode sort(Comparator<PVDomNode> comparator) {
        return sort(JsUtils.toJsComparator(comparator));
    }

    public final native PVDomNode sort(JavaScriptObject comparator) /*-{
        return this.sort(comparator);
    }-*/;

    public final native double nodeValue() /*-{
        return this.nodeValue;
    }-*/;

    public final native PVDomNode parentNode() /*-{
        return this.parentNode;
    }-*/;

}