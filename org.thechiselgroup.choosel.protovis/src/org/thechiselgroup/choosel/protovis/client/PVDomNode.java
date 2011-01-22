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

import com.google.gwt.core.client.JavaScriptObject;

/**
 * 
 * @author Lars Grammel
 */
public class PVDomNode extends JavaScriptObject {

    protected PVDomNode() {
    }

    public static final native PVDomNode create(Object o, String nodeName) /*-{
        var node = new $wnd.pv.Dom.Node(o);
        node.nodeName = nodeName;
        return node;
    }-*/;

    public final native void appendChild(PVDomNode childNode) /*-{
        this.appendChild(childNode);
    }-*/;

    public final native PVDomNode parentNode() /*-{
        return this.parentNode;
    }-*/;

    public final native String nodeName() /*-{
        return this.nodeName;
    }-*/;

    public final native JavaScriptObject nodes() /*-{
        return this.nodes();
    }-*/;

    public final native <T> T nodeValue() /*-{
        return this.nodeValue;
    }-*/;

}