/*******************************************************************************
 * Copyright (C) 2011 Lars Grammel, Nikita Zhiltsov
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

import org.thechiselgroup.choosel.protovis.client.jsutil.JsArrayGeneric;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsUtils;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author Lars Grammel
 * @author Nikita Zhiltsov
 */
public class PVForceLayout extends PVAbstractBar<PVForceLayout> {

    protected PVForceLayout() {
    }

    public final native PVForceLayout iterations(int iterations) /*-{
        return this.iterations(iterations);
    }-*/;

    public final native PVMark label() /*-{
        return this.label;
    }-*/;

    public final native PVMark link() /*-{
        return this.link;
    }-*/;

    /**
     * @param links
     *            JavaScript array of java objects
     */
    private final native PVForceLayout links(JavaScriptObject links) /*-{
        return this.links(links);
    }-*/;

    public final PVForceLayout links(Link... links) {
        JsArrayGeneric<PVLink> jsLinks = JsUtils.createJsArrayGeneric();
        for (Link link : links) {
            jsLinks.push(PVLink.create(link));
        }
        return this.links(jsLinks);
    }

    /**
     * Stops the rendering of the force layout by clearing the timer and setting
     * the steps to 0.
     */
    public final native PVForceLayout stop() /*-{
        this.iterations(0);
        $wnd.clearInterval(this.$timer);
        this.$timer = null;
        return this;
    }-*/;

    public final native PVMark node() /*-{
        return this.node;
    }-*/;

    private final native PVForceLayout nodes(JavaScriptObject nodes) /*-{
        return this.nodes(nodes);
    }-*/;

    public final <S> PVForceLayout nodes(PVNodeAdapter<S> adapter, S... nodes) {
        assert adapter != null;
        assert nodes != null;

        JsArrayGeneric<PVNode> jsNodes = JsUtils.createJsArrayGeneric();
        for (S node : nodes) {
            PVNode pvNode = PVNode.create(node);
            String nodeName = adapter.getNodeName(node);
            if (nodeName != null) {
                pvNode.nodeName(nodeName);
            }
            Object nodeValue = adapter.getNodeValue(node);
            if (nodeValue != null) {
                pvNode.nodeValue(nodeValue);
            }
            jsNodes.push(pvNode);
        }

        return this.nodes(jsNodes);
    }
}
