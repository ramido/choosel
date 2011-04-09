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

import org.thechiselgroup.choosel.protovis.client.jsutil.JsArrayGeneric;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsUtils;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author Lars Grammel
 */
public abstract class PVNetworkLayout<T extends PVNetworkLayout<T>> extends
        PVAbstractPanel<T> {

    protected PVNetworkLayout() {
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
    public final native T links(JavaScriptObject links) /*-{
        return this.links(links);
    }-*/;

    public final T links(Link... links) {
        JsArrayGeneric<PVLink> jsLinks = JsUtils.createJsArrayGeneric();
        for (Link link : links) {
            jsLinks.push(PVLink.create(link));
        }
        return this.links(jsLinks);
    }

    public final native PVMark node() /*-{
        return this.node;
    }-*/;

    public final native T nodes(JavaScriptObject nodes) /*-{
        return this.nodes(nodes);
    }-*/;

    public final native T nodes(JsFunction<JavaScriptObject> f) /*-{
        return this.nodes(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsFunction;)(f));
    }-*/;

    public final <S> T nodes(PVNodeAdapter<S> adapter, S... nodes) {
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

    public final native T reset() /*-{
        return this.reset();
    }-*/;

    // TODO String[] as nodes
    // TODO double[] / int[] as nodes

}