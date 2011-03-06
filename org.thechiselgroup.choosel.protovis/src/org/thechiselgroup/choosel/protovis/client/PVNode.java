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
public class PVNode extends JavaScriptObject {

    public static native PVNode create(Object original) /*-{
        return {
        'object': original
        };
    }-*/;

    protected PVNode() {
    }

    public final native int linkDegree() /*-{
        return this.linkDegree;
    }-*/;

    public final native String nodeName() /*-{
        return this.nodeName;
    }-*/;

    public final native PVNode nodeName(String nodeName) /*-{
        this.nodeName = nodeName;
        return this;
    }-*/;

    public final native <T> PVNode nodeValue(T nodeValue) /*-{
        this.nodeValue = nodeValue;
        return this;
    }-*/;

    public final native <T> T object() /*-{
        return this.object;
    }-*/;

    /**
     * X coordinate of node.
     */
    public final native double x() /*-{
        return this.x;
    }-*/;

    /**
     * Y coordinate of node.
     */
    public final native double y() /*-{
        return this.y;
    }-*/;

}