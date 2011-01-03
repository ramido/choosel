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

import org.thechiselgroup.choosel.protovis.client.jsutil.JsArrayGeneric;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsFunction;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Wrapper for
 * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.Scale.log.html">pv.Scale.log</a></code>
 * .
 * 
 * @author Lars Grammel
 */
// @formatter:off
public class PVLogScale extends PVScale {

    protected PVLogScale() {
    }

    /**
     * Wrapper for
     * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.Scale.quantitative.html#domain">pv.Scale.quantitative.domain()</a></code>
     * .
     */  
    public final native PVLogScale domain(double min, double max) /*-{
        return this.domain(min, max);
    }-*/;
    
    /**
     * Wrapper for
     * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.Scale.quantitative.html#domain">pv.Scale.quantitative.domain()</a></code>
     * .
     */  
    public final native PVLogScale domain(JsArrayGeneric<?> array, JsFunction<?> f) /*-{
        return this.domain(array, @org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsFunction;)( f));
    }-*/;

    public final native PVLogScale range(int from, int to) /*-{
        return this.range(from, to);
    }-*/;

    public final native PVLogScale range(String fromColor, String toColor) /*-{
        return this.range(fromColor, toColor);
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
