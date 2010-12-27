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

import com.google.gwt.core.client.JsArrayNumber;

/**
 * Wrapper for
 * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.Scale.ordinal.html">pv.Scale.ordinal</a></code>
 * .
 * 
 * @author Lars Grammel
 */
public class PVOrdinalScale extends PVScale {

    protected PVOrdinalScale() {
    }

    public final native PVOrdinalScale domain(JsArrayNumber array) /*-{
        return this.domain(array);
    }-*/;

    /**
     * Wrapper for
     * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.Scale.ordinal.html#range">range()</a></code>
     * .
     */
    public final native JsArrayNumber range() /*-{
        return this.range();
    }-*/;

    // TODO 4 variants of domain method -- is this even possible?

    /**
     * Wrapper for <code>range().band</code>, as explained in
     * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.Scale.ordinal.html#splitBanded">splitBanded()</a></code>
     * .
     */
    public final native double rangeBand() /*-{
        return this.range().band;
    }-*/;

    /**
     * Wrapper for
     * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.Scale.ordinal.html#splitBanded">splitBanded()</a></code>
     * .
     */
    public final native PVOrdinalScale splitBanded(double min, double max) /*-{
        return this.splitBanded(min, max);
    }-*/;

    /**
     * Wrapper for
     * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.Scale.ordinal.html#splitBanded">splitBanded()</a></code>
     * .
     */
    public final native PVOrdinalScale splitBanded(double min, double max,
            double band) /*-{
        return this.splitBanded(min, max, band);
    }-*/;

}