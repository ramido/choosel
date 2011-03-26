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
package org.thechiselgroup.choosel.visualization_component.graph.client;

import static org.thechiselgroup.choosel.visualization_component.graph.client.widget.GraphDisplay.NODE_BACKGROUND_COLOR;
import static org.thechiselgroup.choosel.visualization_component.graph.client.widget.GraphDisplay.NODE_BORDER_COLOR;
import static org.thechiselgroup.choosel.visualization_component.graph.client.widget.GraphDisplay.NODE_FONT_COLOR;
import static org.thechiselgroup.choosel.visualization_component.graph.client.widget.GraphDisplay.NODE_FONT_WEIGHT;
import static org.thechiselgroup.choosel.visualization_component.graph.client.widget.GraphDisplay.NODE_FONT_WEIGHT_BOLD;
import static org.thechiselgroup.choosel.visualization_component.graph.client.widget.GraphDisplay.NODE_FONT_WEIGHT_NORMAL;

import org.thechiselgroup.choosel.core.client.ui.Colors;
import org.thechiselgroup.choosel.core.client.views.model.ViewItem;
import org.thechiselgroup.choosel.core.client.views.model.ViewItem.Status;
import org.thechiselgroup.choosel.core.client.views.model.ViewItem.Subset;
import org.thechiselgroup.choosel.visualization_component.graph.client.widget.GraphDisplay;
import org.thechiselgroup.choosel.visualization_component.graph.client.widget.Node;

/**
 * Represents a resource item in the graph view.
 * 
 * @author Lars Grammel
 */
public class GraphItem {

    private GraphDisplay display;

    private Node node;

    private final ViewItem resourceItem;

    public GraphItem(ViewItem resourceItem, String type, GraphDisplay display) {

        assert resourceItem != null;
        assert type != null;
        assert display != null;

        this.resourceItem = resourceItem;
        this.display = display;

        node = new Node(resourceItem.getViewItemID(), getLabelValue(), type);
    }

    public String getLabelValue() {
        return resourceItem.getValue(Graph.NODE_LABEL_SLOT);
    }

    public Node getNode() {
        return node;
    }

    public String getNodeBackgroundColorValue() {
        return resourceItem.getValue(Graph.NODE_BACKGROUND_COLOR_SLOT);
    }

    public String getNodeBorderColorValue() {
        return resourceItem.getValue(Graph.NODE_BORDER_COLOR_SLOT);
    }

    public ViewItem getResourceItem() {
        return resourceItem;
    }

    /**
     * Updates the graph node to reflect the style and values of the underlying
     * resource item.
     */
    // TODO expose border color, node color, font weight, font color as slots
    public void updateNode() {
        Status highlighStatus = resourceItem.getStatus(Subset.HIGHLIGHTED);
        Status selectionStatus = resourceItem.getStatus(Subset.SELECTED);

        boolean isHighlighted = Status.PARTIAL == highlighStatus
                || Status.FULL == highlighStatus;
        boolean isSelected = Status.PARTIAL == selectionStatus
                || Status.FULL == selectionStatus;

        if (isHighlighted) {
            display.setNodeStyle(node, NODE_BACKGROUND_COLOR, Colors.YELLOW_1);
        } else {
            display.setNodeStyle(node, NODE_BACKGROUND_COLOR,
                    getNodeBackgroundColorValue());
        }

        if (isSelected) {
            display.setNodeStyle(node, NODE_FONT_COLOR, Colors.ORANGE);
            display.setNodeStyle(node, NODE_FONT_WEIGHT, NODE_FONT_WEIGHT_BOLD);
        } else {
            display.setNodeStyle(node, NODE_FONT_COLOR, Colors.BLACK);
            display.setNodeStyle(node, NODE_FONT_WEIGHT,
                    NODE_FONT_WEIGHT_NORMAL);
        }

        if (isHighlighted && !isSelected) {
            display.setNodeStyle(node, NODE_BORDER_COLOR, Colors.YELLOW_2);
        } else if (isSelected) {
            display.setNodeStyle(node, NODE_BORDER_COLOR, Colors.ORANGE);
        } else {
            display.setNodeStyle(node, NODE_BORDER_COLOR,
                    getNodeBorderColorValue());
        }
    }

}