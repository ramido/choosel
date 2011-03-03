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

/**
 * Wrapper for
 * <code><a href="http://vis.stanford.edu/protovis/jsdoc/symbols/pv.Layout.html">pv.Layout</a></code>
 * .
 * 
 * @author Lars Grammel
 */
public final class PVLayout {

    public static native PVBulletLayout Bullet() /*-{
        return $wnd.pv.Layout.Bullet;
    }-*/;

    public static native PVClusterLayout Cluster() /*-{
        return $wnd.pv.Layout.Cluster;
    }-*/;

    public static native PVPackLayout Pack() /*-{
        return $wnd.pv.Layout.Pack;
    }-*/;

    public static native PVFillPartitionLayout PartitionFill() /*-{
        return $wnd.pv.Layout.Partition.Fill;
    }-*/;

    public static native PVStackLayout Stack() /*-{
        return $wnd.pv.Layout.Stack;
    }-*/;

    public static native PVTreeLayout Tree() /*-{
        return $wnd.pv.Layout.Tree;
    }-*/;

    public static native PVTreemapLayout Treemap() /*-{
        return $wnd.pv.Layout.Treemap;
    }-*/;

    public static native PVArcLayout Arc() /*-{
        return $wnd.pv.Layout.Arc;
    }-*/;

    private PVLayout() {
    }

}