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

import org.thechiselgroup.choosel.protovis.client.jsutil.JsStringFunction;

/**
 * 
 * @author Bradley Blashko
 */
public final class PVLine extends PVAbstractMark<PVLine> {

    public final static class Type extends PVMarkType<PVLine> {

        protected Type() {
        }

    }

    public static native PVLine create() /*-{
        return new $wnd.pv.Line();
    }-*/;

    protected PVLine() {
    }

    public final native PVLine antialias(boolean antialias) /*-{
        return this.antialias(antialias);
    }-*/;

    public final native PVLine eccentricity(double eccentricity) /*-{
        return this.eccentricity(eccentricity);
    }-*/;

    public final native PVLine fillStyle(JsStringFunction f) /*-{
        return this.fillStyle(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsStringFunction;)(f));
    }-*/;

    public final native PVLine fillStyle(String colour) /*-{
        return this.fillStyle(colour);
    }-*/;

    /**
     * @see PVInterpolationMethod
     */
    public final native PVLine interpolate(String interpolate) /*-{
        return this.interpolate(interpolate);
    }-*/;

    public final native PVLine lineJoin(String lineJoin) /*-{
        return this.lineJoin(lineJoin);
    }-*/;

    public final native PVLine lineWidth(double lineWidth) /*-{
        return this.lineWidth(lineWidth);
    }-*/;

    public final native PVLine lineWidth(int lineWidth) /*-{
        return this.lineWidth(lineWidth);
    }-*/;

    public final native PVLine segmented(boolean segmented) /*-{
        return this.segmented(segmented);
    }-*/;

    public final native PVLine strokeStyle(String strokeStyle) /*-{
        return this.strokeStyle(strokeStyle);
    }-*/;

    public final native PVLine tension(double tension) /*-{
        return this.tension(tension);
    }-*/;

}