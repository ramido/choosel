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
package org.thechiselgroup.choosel.client.ui.widget.chart.protovis;

import com.google.gwt.core.client.JavaScriptObject;

// @formatter:off
/**
 * 
 * @author Bradley Blashko
 * 
 */
public class Scale extends JavaScriptObject {

    public final static native Scale linear(double from, double to) /*-{
        return $wnd.pv.Scale.linear(from, to);
    }-*/;

    protected Scale() {
    }

    public final native Scale range(double min, double max) /*-{
        return this.range(min, max);
    }-*/;

    public final native String tickFormat(String tick) /*-{
        return this.tickFormat(tick);
    }-*/;

    public final native JavaScriptObject ticks() /*-{
        return this.ticks();
    }-*/;

    public final native JavaScriptObject ticks(Number ticks) /*-{
        return this.ticks(ticks);
    }-*/;

}
// @formatter:on
