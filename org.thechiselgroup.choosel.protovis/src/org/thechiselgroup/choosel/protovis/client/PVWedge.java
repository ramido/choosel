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

import org.thechiselgroup.choosel.protovis.client.functions.PVDoubleFunction;
import org.thechiselgroup.choosel.protovis.client.functions.PVDoubleFunctionDoubleArg;
import org.thechiselgroup.choosel.protovis.client.functions.PVFunction;
import org.thechiselgroup.choosel.protovis.client.functions.PVFunctionDoubleArg;
import org.thechiselgroup.choosel.protovis.client.functions.PVStringFunction;

/**
 * 
 * @author Bradley Blashko
 * @author Lars Grammel
 */
public final class PVWedge extends PVAbstractMark<PVWedge> {

    protected PVWedge() {
    }

    public final native PVWedge angle(double angle) /*-{
        return this.angle(angle);
    }-*/;

    public final native PVWedge angle(PVDoubleFunction<PVWedge, ?> f) /*-{
        return this.angle(@org.thechiselgroup.choosel.protovis.client.functions.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/functions/PVDoubleFunction;)(f));
    }-*/;

    public final native PVWedge angle(PVDoubleFunctionDoubleArg<PVWedge> f) /*-{
        return this.angle(@org.thechiselgroup.choosel.protovis.client.functions.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/functions/PVDoubleFunctionDoubleArg;)(f));
    }-*/;

    public final native PVWedge fillStyle(PVStringFunction<PVWedge, ?> f) /*-{
        return this.fillStyle(@org.thechiselgroup.choosel.protovis.client.functions.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/functions/PVStringFunction;)(f));
    }-*/;

    public final native PVWedge fillStyle(String colour) /*-{
        return this.fillStyle(colour);
    }-*/;

    public final native PVWedge innerRadius(double innerRadius) /*-{
        return this.innerRadius(innerRadius);
    }-*/;

    public final native PVWedge innerRadius(PVDoubleFunction<PVWedge, ?> f) /*-{
        return this.innerRadius(@org.thechiselgroup.choosel.protovis.client.functions.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/functions/PVDoubleFunction;)(f));
    }-*/;

    public final native PVWedge outerRadius(double outerRadius) /*-{
        return this.outerRadius(outerRadius);
    }-*/;

    public final native PVWedge outerRadius(PVDoubleFunction<PVWedge, ?> f) /*-{
        return this.outerRadius(@org.thechiselgroup.choosel.protovis.client.functions.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/functions/PVDoubleFunction;)(f));
    }-*/;

    public final native PVWedge startAngle(double startAngle) /*-{
        return this.startAngle(startAngle);
    }-*/;

    public final native PVWedge startAngle(PVDoubleFunction<PVWedge, ?> f) /*-{
        return this.startAngle(@org.thechiselgroup.choosel.protovis.client.functions.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/functions/PVDoubleFunction;)(f));
    }-*/;

    public final native PVWedge strokeStyle(PVFunction<PVWedge, ?, PVColor> f) /*-{
        return this.strokeStyle(@org.thechiselgroup.choosel.protovis.client.functions.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/functions/PVFunction;)(f));
    }-*/;

    public final native PVWedge strokeStyle(
            PVFunctionDoubleArg<PVWedge, PVColor> f) /*-{
        return this.strokeStyle(@org.thechiselgroup.choosel.protovis.client.functions.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/functions/PVFunctionDoubleArg;)(f));
    }-*/;

    public final native PVWedge strokeStyle(String strokeStyle) /*-{
        return this.strokeStyle(strokeStyle);
    }-*/;

}