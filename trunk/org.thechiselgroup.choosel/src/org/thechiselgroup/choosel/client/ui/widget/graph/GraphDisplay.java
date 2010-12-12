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
package org.thechiselgroup.choosel.client.ui.widget.graph;

import java.util.Collection;

import org.thechiselgroup.choosel.client.geometry.Point;
import org.thechiselgroup.choosel.client.ui.WidgetAdaptable;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.event.shared.HandlerRegistration;

public interface GraphDisplay extends WidgetAdaptable {

    String DEFAULT_LAYOUT = GraphLayouts.FORCE_DIRECTED_LAYOUT;

    String ARC_COLOR = "normalLineColor";

    /**
     * Arc property that defines the style of the arc (solid or dashed).
     * 
     * @see #ARC_STYLE_DASHED
     * @see #ARC_STYLE_SOLID
     */
    String ARC_STYLE = "normalLineStyle";

    /**
     * Style constant for dashed arcs.
     * 
     * @see #ARC_STYLE
     */
    String ARC_STYLE_DASHED = "dashed";

    /**
     * Style constant for solid arcs.
     * 
     * @see #ARC_STYLE
     */
    String ARC_STYLE_SOLID = "solid";

    // FIXME these a flexviz specific, use generic ones & translate in
    // graphwidget

    String NODE_BACKGROUND_COLOR = "normalBackgroundColor";

    String NODE_BORDER_COLOR = "normalBorderColor";

    String NODE_FONT_COLOR = "normalColor";

    String NODE_FONT_WEIGHT = "normalFontWeight";

    String NODE_FONT_WEIGHT_BOLD = "bold";

    String NODE_FONT_WEIGHT_NORMAL = "normal";

    /**
     * Adds an arc to the graph display. The target and source nodes must
     * already be contained in the graph display. An arc with the same id must
     * not already exist.
     */
    // TODO throw exception when nodes not found
    // TODO throw exception if arc exists already
    void addArc(Arc arc);

    <T extends EventHandler> HandlerRegistration addEventHandler(Type<T> type,
            T handler);

    HandlerRegistration addGraphWidgetReadyHandler(
            GraphWidgetReadyHandler handler);

    void addNode(Node node);

    void addNodeMenuItemHandler(String menuLabel,
            NodeMenuItemClickedHandler handler, String nodeClass);

    void animateMoveTo(Node node, Point targetLocation);

    boolean containsArc(String arcId);

    boolean containsNode(String nodeId);

    Arc getArc(String arcId);

    Point getLocation(Node node);

    Node getNode(String nodeId);

    void removeArc(Arc arc);

    void removeNode(Node node);

    void runLayout() throws LayoutException;

    void runLayout(String layout) throws LayoutException;

    void runLayoutOnNodes(Collection<Node> nodes) throws LayoutException;

    void setArcStyle(Arc arc, String styleProperty, String styleValue);

    void setLocation(Node node, Point location);

    void setNodeStyle(Node node, String styleProperty, String styleValue);

}