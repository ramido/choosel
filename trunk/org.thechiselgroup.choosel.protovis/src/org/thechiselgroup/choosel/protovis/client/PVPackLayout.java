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

import org.thechiselgroup.choosel.protovis.client.jsutil.JsDoubleFunction;

/**
 * @author Lars Grammel
 */
public final class PVPackLayout extends PVNetworkLayout<PVPackLayout> {

    protected PVPackLayout() {
    }

    public final native PVPackLayout order(String order) /*-{
        return this.order(order);
    }-*/;

    public final native PVPackLayout size(JsDoubleFunction f) /*-{
        return this.size(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsDoubleFunction;)(f));
    }-*/;

    public final native PVPackLayout spacing(int spacing) /*-{
        return this.spacing(spacing);
    }-*/;

}