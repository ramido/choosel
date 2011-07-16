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
 * Protovis-GWT implementation of <a
 * href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.Transform.html"
 * >pv.Transform</a>.
 * 
 * @author Lars Grammel
 */
public class PVTransform extends JavaScriptObject {

    protected PVTransform() {
    }

    public final native PVTransform invert() /*-{
        return this.invert();
    }-*/;

    public final native double k() /*-{
        return this.k;
    }-*/;

    public final native PVTransform scale(double k) /*-{
        return this.scale(k);
    }-*/;

    public final native PVTransform times(double m) /*-{
        return this.times(m);
    }-*/;

    public final native PVTransform translate(double x, double y) /*-{
        return this.translate(x,y);
    }-*/;

    public final native double x() /*-{
        return this.x;
    }-*/;

    public final native double y() /*-{
        return this.y;
    }-*/;

}