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

import org.thechiselgroup.choosel.protovis.client.jsutil.JsArrayGeneric;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsUtils;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author Lars Grammel
 */
public final class PVArcLayout extends PVAbstractBar<PVArcLayout> {

    protected PVArcLayout() {
    }

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
    private final native PVArcLayout links(JavaScriptObject links) /*-{
        return this.links(links);
    }-*/;

    public final PVArcLayout links(Link... links) {
        JsArrayGeneric<PVLink> jsLinks = JsUtils.createJsArrayGeneric();
        for (Link link : links) {
            jsLinks.push(PVLink.create(link));
        }
        return this.links(jsLinks);
    }

    public final native PVMark node() /*-{
        return this.node;
    }-*/;

    // TODO String[] as nodes
    // TODO double[] / int[] as nodes

    private final native PVArcLayout nodes(JavaScriptObject nodes) /*-{
        return this.nodes(nodes);
    }-*/;

    public final <S> PVArcLayout nodes(PVNodeAdapter<S> adapter, S... nodes) {
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

    private final native PVArcLayout sort(JavaScriptObject comparator) /*-{
        return this.sort(comparator);
    }-*/;

    public PVArcLayout sort(Comparator<PVNode> comparator) {
        return this.sort(JsUtils.toJsComparator(comparator));
    }

}