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


/**
 * @author Lars Grammel
 * @author Nikita Zhiltsov
 */
public class PVForceLayout extends PVNetworkLayout<PVForceLayout> {

    protected PVForceLayout() {
    }

    public final native boolean bound() /*-{
        return this.bound();
    }-*/;

    public final native PVForceLayout bound(boolean bound) /*-{
        return this.bound(bound);
    }-*/;

    public final native double chargeConstant() /*-{
        return this.chargeConstant();
    }-*/;

    public final native PVForceLayout chargeConstant(double chargeConstant) /*-{
        return this.chargeConstant(chargeConstant);
    }-*/;

    public final native double chargeMaxDistance() /*-{
        return this.chargeMaxDistance();
    }-*/;

    public final native PVForceLayout chargeMaxDistance(double chargeMaxDistance) /*-{
        return this.chargeMaxDistance(chargeMaxDistance);
    }-*/;

    public final native double chargeTheta() /*-{
        return this.chargeTheta();
    }-*/;

    public final native PVForceLayout chargeTheta(double chargeTheta) /*-{
        return this.chargeTheta(chargeTheta);
    }-*/;

    public final native double dragConstant() /*-{
        return this.dragConstant();
    }-*/;

    public final native PVForceLayout dragConstant(double dragConstant) /*-{
        return this.dragConstant(dragConstant);
    }-*/;

    public final native int iterations() /*-{
        return this.iterations();
    }-*/;

    public final native PVForceLayout iterations(int iterations) /*-{
        return this.iterations(iterations);
    }-*/;

    public final native double springConstant() /*-{
        return this.springConstant();
    }-*/;

    public final native PVForceLayout springConstant(double springConstant) /*-{
        return this.springConstant(springConstant);
    }-*/;

    public final native double springDamping() /*-{
        return this.springDamping();
    }-*/;

    public final native PVForceLayout springDamping(double springDamping) /*-{
        return this.springDamping(springDamping);
    }-*/;

    public final native double springLength() /*-{
        return this.springLength();
    }-*/;

    public final native PVForceLayout springLength(double springLength) /*-{
        return this.springLength(springLength);
    }-*/;

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

}