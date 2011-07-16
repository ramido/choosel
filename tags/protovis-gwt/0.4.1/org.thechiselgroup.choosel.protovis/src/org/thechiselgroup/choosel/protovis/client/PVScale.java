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

import java.util.Date;

import org.thechiselgroup.choosel.protovis.client.jsutil.JsArgs;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsDoubleFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsStringFunction;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsDate;

/**
 * Wrapper for
 * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.Scale.html">pv.Scale</a></code>
 * .
 * 
 * @author Bradley Blashko
 * @author Lars Grammel
 */
public class PVScale extends JavaScriptObject {

    protected PVScale() {
    }

    /**
     * Scales are functions. Use this method if the scale returns a color value.
     */
    public final native PVColor fcolor(double value) /*-{
        return this(value);
    }-*/;

    /**
     * Scales are functions. Use this method if the scale returns color values
     * and you need to call it with a function.
     */
    public final JsFunction<PVColor> fcolor(final JsDoubleFunction f) {
        return new JsFunction<PVColor>() {
            public PVColor f(JsArgs args) {
                return fcolor(f.f(args));
            }
        };
    }

    /**
     * Scales are functions. Use this method if the scale returns a color value.
     */
    public final native PVColor fcolor(Object value) /*-{
        return this(value);
    }-*/;

    /**
     * Scales are functions. Use this method if the scale returns double values
     * and you need to call it with a function.
     */
    public final JsFunction<PVColor> fcolor(final JsStringFunction f) {
        return new JsFunction<PVColor>() {
            public PVColor f(JsArgs args) {
                return fcolor(f.f(args));
            }
        };
    }

    /**
     * Scales are functions. Use this method if the scale returns a double
     * value.
     */
    public final native double fd(double value) /*-{
        return this(value);
    }-*/;

    /**
     * Scales are functions. Use this method if the scale returns double values
     * and you need to call it with a function.
     */
    public final JsDoubleFunction fd(final JsDoubleFunction f) {
        return new JsDoubleFunction() {
            public double f(JsArgs args) {
                return fd(f.f(args));
            }
        };
    }

    /**
     * Scales are functions. Use this method if the scale returns a double
     * value.
     */
    public final native double fd(String value) /*-{
        return this(value);
    }-*/;

    /**
     * Scales are functions. Use this method if the scale returns double values
     * and you need to call it with a function.
     */
    public final JsDoubleFunction fd(final JsStringFunction f) {
        return new JsDoubleFunction() {
            public double f(JsArgs args) {
                return fd(f.f(args));
            }
        };
    }

    /**
     * Scales are functions. Use this method if the scale returns a double
     * value.
     */
    public final double fd(Date value) {
        return this.fd(JsDate.create(value.getTime()));
    };

    /**
     * Scales are functions. Use this method if the scale returns a double
     * value.
     */
    public final native double fd(JsDate value) /*-{
        return this(value);
    }-*/;

    /**
     * Scales are functions. Use this method if the scale returns double values
     * and you need to call it with a function.
     */
    public final JsDoubleFunction fd(final JsFunction<JsDate> f) {
        return new JsDoubleFunction() {
            public double f(JsArgs args) {
                return fd(f.f(args));
            }
        };
    }

}