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
 * @author Lars Grammel
 */
public final class PVArcLayout extends PVNetworkLayout<PVArcLayout> {

    protected PVArcLayout() {
    }

    public final native boolean directed() /*-{
        return this.orient;
    }-*/;

    public final native PVArcLayout directed(boolean directed) /*-{
        return this.directed(directed);
    }-*/;

    public final native String orient() /*-{
        return this.orient;
    }-*/;

    public final native PVArcLayout orient(String orient) /*-{
        return this.orient(orient);
    }-*/;

    public PVArcLayout sort(Comparator<PVNode> comparator) {
        return this.sort(JsUtils.toJsComparator(comparator));
    }

    private final native PVArcLayout sort(JavaScriptObject comparator) /*-{
        return this.sort(comparator);
    }-*/;

}