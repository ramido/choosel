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

import org.thechiselgroup.choosel.protovis.client.jsutil.JsFunction;

import com.google.gwt.core.client.JsArrayNumber;

/**
 * Wrapper for
 * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.Layout.Bullet.html">pv.Layout.Bullet</a></code>
 * .
 * 
 * @author Lars Grammel
 */
public final class PVBulletLayout extends PVAbstractBar<PVBulletLayout> {

    protected PVBulletLayout() {
    }

    /**
     * Wrapper for
     * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.Layout.Bullet.html#marker">marker</a></code>
     * .
     */
    public final native PVMark marker() /*-{
        return this.marker;
    }-*/;

    /**
     * Wrapper for
     * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.Layout.Bullet.html#markers">markers</a></code>
     * .
     */
    public final native PVBulletLayout markers(JsFunction<JsArrayNumber> f) /*-{
        return this.markers(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsFunction;)(f));
    }-*/;

    /**
     * Wrapper for
     * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.Layout.Bullet.html#measure">measure</a></code>
     * .
     */
    public final native PVMark measure() /*-{
        return this.measure;
    }-*/;

    /**
     * Wrapper for
     * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.Layout.Bullet.html#measures">measures</a></code>
     * .
     */
    public final native PVBulletLayout measures(JsFunction<JsArrayNumber> f) /*-{
        return this.measures(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsFunction;)(f));
    }-*/;

    /**
     * Wrapper for
     * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.Layout.Bullet.html#orient">orient</a></code>
     * .
     * 
     * @param direction
     *            {@link PVAlignment}
     */
    public final native PVBulletLayout orient(String direction) /*-{
        return this.orient(direction);
    }-*/;

    /**
     * Wrapper for
     * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.Layout.Bullet.html#range">range</a></code>
     * .
     */
    public final native PVMark range() /*-{
        return this.range;
    }-*/;

    /**
     * Wrapper for
     * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.Layout.Bullet.html#ranges">ranges</a></code>
     * .
     */
    public final native PVBulletLayout ranges(JsFunction<JsArrayNumber> f) /*-{
        return this.ranges(@org.thechiselgroup.choosel.protovis.client.jsutil.JsFunctionUtils::toJavaScriptFunction(Lorg/thechiselgroup/choosel/protovis/client/jsutil/JsFunction;)(f));
    }-*/;

    public final native PVMark tick() /*-{
        return this.tick;
    }-*/;

    // XXX x is not documented
    public final native PVLinearScale x() /*-{
        return this.x;
    }-*/;

}