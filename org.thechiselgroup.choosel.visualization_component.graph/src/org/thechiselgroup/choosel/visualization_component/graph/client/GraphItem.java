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

import static org.thechiselgroup.choosel.visualization_component.graph.client.GraphViewContentDisplay.NODE_BACKGROUND_COLOR_SLOT;
import static org.thechiselgroup.choosel.visualization_component.graph.client.GraphViewContentDisplay.NODE_BORDER_COLOR_SLOT;
import static org.thechiselgroup.choosel.visualization_component.graph.client.GraphViewContentDisplay.NODE_LABEL_SLOT;
import static org.thechiselgroup.choosel.visualization_component.graph.client.widget.GraphDisplay.NODE_BACKGROUND_COLOR;
import static org.thechiselgroup.choosel.visualization_component.graph.client.widget.GraphDisplay.NODE_BORDER_COLOR;
import static org.thechiselgroup.choosel.visualization_component.graph.client.widget.GraphDisplay.NODE_FONT_COLOR;
import static org.thechiselgroup.choosel.visualization_component.graph.client.widget.GraphDisplay.NODE_FONT_WEIGHT;
import static org.thechiselgroup.choosel.visualization_component.graph.client.widget.GraphDisplay.NODE_FONT_WEIGHT_BOLD;
import static org.thechiselgroup.choosel.visualization_component.graph.client.widget.GraphDisplay.NODE_FONT_WEIGHT_NORMAL;

import org.thechiselgroup.choosel.core.client.ui.Colors;
import org.thechiselgroup.choosel.core.client.views.ViewItem;
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
        return resourceItem.getSlotValue(NODE_LABEL_SLOT);
    }

    public Node getNode() {
        return node;
    }

    public String getNodeBackgroundColorValue() {
        return resourceItem.getSlotValue(NODE_BACKGROUND_COLOR_SLOT);
    }

    public String getNodeBorderColorValue() {
        return resourceItem.getSlotValue(NODE_BORDER_COLOR_SLOT);
    }

    public ViewItem getResourceItem() {
        return resourceItem;
    }

    /**
     * Updates the graph node to reflect the style and values of the underlying
     * resource item.
     */
    public void updateNode() {
        switch (resourceItem.getStatus()) {
        case PARTIALLY_HIGHLIGHTED_SELECTED:
        case HIGHLIGHTED_SELECTED: {
            display.setNodeStyle(node, NODE_BACKGROUND_COLOR, Colors.YELLOW_1);
            display.setNodeStyle(node, NODE_BORDER_COLOR, Colors.ORANGE);
            display.setNodeStyle(node, NODE_FONT_COLOR, Colors.ORANGE);
            display.setNodeStyle(node, NODE_FONT_WEIGHT, NODE_FONT_WEIGHT_BOLD);
        }
            break;
        case PARTIALLY_HIGHLIGHTED:
        case HIGHLIGHTED: {
            display.setNodeStyle(node, NODE_BACKGROUND_COLOR, Colors.YELLOW_1);
            display.setNodeStyle(node, NODE_BORDER_COLOR, Colors.YELLOW_2);
            display.setNodeStyle(node, NODE_FONT_COLOR, Colors.BLACK);
            display.setNodeStyle(node, NODE_FONT_WEIGHT,
                    NODE_FONT_WEIGHT_NORMAL);
        }
            break;
        case DEFAULT: {
            display.setNodeStyle(node, NODE_BACKGROUND_COLOR,
                    getNodeBackgroundColorValue());
            display.setNodeStyle(node, NODE_BORDER_COLOR,
                    getNodeBorderColorValue());
            display.setNodeStyle(node, NODE_FONT_COLOR, Colors.BLACK);
            display.setNodeStyle(node, NODE_FONT_WEIGHT,
                    NODE_FONT_WEIGHT_NORMAL);
        }
            break;
        case SELECTED: {
            display.setNodeStyle(node, NODE_BACKGROUND_COLOR,
                    getNodeBackgroundColorValue());
            display.setNodeStyle(node, NODE_BORDER_COLOR, Colors.ORANGE);
            display.setNodeStyle(node, NODE_FONT_COLOR, Colors.ORANGE);
            display.setNodeStyle(node, NODE_FONT_WEIGHT, NODE_FONT_WEIGHT_BOLD);
        }
            break;
        }
    }

}