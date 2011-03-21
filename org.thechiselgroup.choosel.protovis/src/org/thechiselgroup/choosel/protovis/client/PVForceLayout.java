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

    public final native PVForceLayout iterations(int iterations) /*-{
        return this.iterations(iterations);
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