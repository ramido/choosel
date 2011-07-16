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
public final class PVFillPartitionLayout extends
        PVNetworkLayout<PVFillPartitionLayout> {

    public static class PVPartitionNode extends PVDomNode {

        protected PVPartitionNode() {
        }

        public final native double dx() /*-{
            return this.dx;
        }-*/;

        public final native double dy() /*-{
            return this.dy;
        }-*/;

        public final native double midAngle() /*-{
            return this.midAngle;
        }-*/;

        public final native double x() /*-{
            return this.x;
        }-*/;

        public final native double y() /*-{
            return this.y;
        }-*/;

    }

    public static class PVRadialNode extends PVPartitionNode {

        protected PVRadialNode() {
        }

        public final native double angle() /*-{
            return this.angle;
        }-*/;

        public final native double innerRadius() /*-{
            return this.innerRadius;
        }-*/;

        public final native double outerRadius() /*-{
            return this.outerRadius;
        }-*/;

        public final native double startAngle() /*-{
            return this.startAngle;
        }-*/;

    }

    protected PVFillPartitionLayout() {
    }

    public final native PVFillPartitionLayout order(String order) /*-{
        return this.order(order);
    }-*/;

    public final native PVFillPartitionLayout orient(String orient) /*-{
        return this.orient(orient);
    }-*/;

    public final native PVFillPartitionLayout size(JsDoubleFunction f) /*-{
        return this.size(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsDoubleFunction;)(f));
    }-*/;
}