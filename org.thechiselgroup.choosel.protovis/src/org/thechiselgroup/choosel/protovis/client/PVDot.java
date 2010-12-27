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
import org.thechiselgroup.choosel.protovis.client.functions.PVStringFunction;

/**
 * 
 * @author Bradley Blashko
 * @author Lars Grammel
 */
public final class PVDot extends PVAbstractMark<PVDot> {

    public static enum Shape {

        CROSS("cross"), TRIANGLE("triangle"), DIAMOND("diamond"), SQUARE(
                "square"), CIRCLE("circle"), TICK("tick"), BAR("bar");

        private String value;

        private Shape(String value) {
            this.value = value;
        }

    }

    protected PVDot() {
    }

    public final native PVDot angle(double angle) /*-{
        return this.angle(angle);
    }-*/;

    public final native PVDot angle(PVDoubleFunction<PVDot, ?> f) /*-{
        return this.angle(@org.thechiselgroup.choosel.protovis.client.functions.JsFunctionUtils::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/functions/PVDoubleFunction;)(this,f));
    }-*/;

    public final native PVDot fillStyle(PVStringFunction<PVDot, ?> f) /*-{
        return this.fillStyle(@org.thechiselgroup.choosel.protovis.client.functions.JsFunctionUtils::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/functions/PVStringFunction;)(this,f));
    }-*/;

    public final native PVDot fillStyle(PVFunction<PVDot, ?, PVColor> f) /*-{
        return this.fillStyle(@org.thechiselgroup.choosel.protovis.client.functions.JsFunctionUtils::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/functions/PVFunction;)(this,f));
    }-*/;

    public final native PVDot fillStyle(String fillStyle) /*-{
        return this.fillStyle(fillStyle);
    }-*/;

    public final native PVDot lineWidth(double lineWidth) /*-{
        return this.lineWidth(lineWidth);
    }-*/;

    public final native PVDot radius(double radius) /*-{
        return this.radius(radius);
    }-*/;

    public final native PVDot radius(PVDoubleFunctionDoubleArg<PVDot> f) /*-{
        return this.radius(@org.thechiselgroup.choosel.protovis.client.functions.JsFunctionUtils::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/functions/PVDoubleFunctionDoubleArg;)(this,f));
    }-*/;

    public final PVDot shape(Shape shape) {
        assert shape != null;
        return _shape(shape.value);
    }

    private final native PVDot _shape(String shape) /*-{
        return this.shape(shape);
    }-*/;

    public final native PVDot size(double size) /*-{
        return this.size(size);
    }-*/;

    public final native PVDot size(PVDoubleFunctionDoubleArg<PVDot> f) /*-{
        return this.size(@org.thechiselgroup.choosel.protovis.client.functions.JsFunctionUtils::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/functions/PVDoubleFunctionDoubleArg;)(this,f));
    }-*/;

    public final native PVDot size(PVDoubleFunction<PVDot, ?> f) /*-{
        return this.size(@org.thechiselgroup.choosel.protovis.client.functions.JsFunctionUtils::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/functions/PVDoubleFunction;)(this,f));
    }-*/;

    public final native PVDot strokeStyle(String strokeStyle) /*-{
        return this.strokeStyle(strokeStyle);
    }-*/;

    public final native PVDot strokeStyle(PVFunction<PVDot, ?, PVColor> f) /*-{
        return this.strokeStyle(@org.thechiselgroup.choosel.protovis.client.functions.JsFunctionUtils::toJavaScriptFunction(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/thechiselgroup/choosel/protovis/client/functions/PVFunction;)(this,f));
    }-*/;

}