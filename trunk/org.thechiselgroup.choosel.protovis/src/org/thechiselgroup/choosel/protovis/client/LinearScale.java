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

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Wrapper for
 * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.Scale.linear.html">pv.Scale.linear</a></code>
 * .
 * 
 * @author Bradley Blashko
 * @author Lars Grammel
 */
// @formatter:off
public class LinearScale extends Scale {

    protected LinearScale() {
    }

    public final native LinearScale domain(double min, double max) /*-{
        return this.domain(min, max);
    }-*/;

    public final native LinearScale range(int min, int max) /*-{
        return this.range(min, max);
    }-*/;

    /**
     * Wrapper for
     * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.Scale.quantitative.html#tickFormat">tickFormat()</a></code>
     * .
     */
    public final native JavaScriptObject tickFormat() /*-{
        return this.tickFormat;
    }-*/;

    /**
     * Wrapper for
     * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.Scale.quantitative.html#tickFormat">tickFormat()</a></code>
     * .
     */
    public final native String tickFormat(int tick) /*-{
        return this.tickFormat(tick);
    }-*/;
    
    public final native JavaScriptObject ticks() /*-{
        return this.ticks();
    }-*/;

    public final native JavaScriptObject ticks(int ticks) /*-{
        return this.ticks(ticks);
    }-*/;


}
// @formatter:on
