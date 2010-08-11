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
package org.thechiselgroup.choosel.client.views.graph;

import static org.thechiselgroup.choosel.client.ui.widget.graph.GraphDisplay.NODE_BACKGROUND_COLOR;
import static org.thechiselgroup.choosel.client.ui.widget.graph.GraphDisplay.NODE_BORDER_COLOR;
import static org.thechiselgroup.choosel.client.ui.widget.graph.GraphDisplay.NODE_FONT_COLOR;
import static org.thechiselgroup.choosel.client.ui.widget.graph.GraphDisplay.NODE_FONT_WEIGHT;
import static org.thechiselgroup.choosel.client.ui.widget.graph.GraphDisplay.NODE_FONT_WEIGHT_BOLD;
import static org.thechiselgroup.choosel.client.ui.widget.graph.GraphDisplay.NODE_FONT_WEIGHT_NORMAL;

import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.ui.Colors;
import org.thechiselgroup.choosel.client.ui.widget.graph.Node;
import org.thechiselgroup.choosel.client.views.ResourceItem;
import org.thechiselgroup.choosel.client.views.graph.GraphViewContentDisplay.Display;

public class GraphItem {

    private String defaultBackgroundColor = Colors.BLUE_3;

    private String defaultBorderColor = Colors.BLUE_1;

    private Display display;

    private Node node;

    public GraphItem(ResourceSet resources, String label, String category,
            GraphViewContentDisplay.Display display) {

        assert category != null;
        assert display != null;

        this.node = new Node(resources.getFirstResource().getUri(), label,
                category);
        this.display = display;
    }

    public Node getNode() {
        return node;
    }

    public void setDefaultColors(String backgroundColor, String borderColor) {
        this.defaultBorderColor = borderColor;
        this.defaultBackgroundColor = backgroundColor;
    }

    public void updateNode(ResourceItem.Status status) {
        switch (status) {
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
                    defaultBackgroundColor);
            display.setNodeStyle(node, NODE_BORDER_COLOR, defaultBorderColor);
            display.setNodeStyle(node, NODE_FONT_COLOR, Colors.BLACK);
            display.setNodeStyle(node, NODE_FONT_WEIGHT,
                    NODE_FONT_WEIGHT_NORMAL);
        }
            break;
        case SELECTED: {
            display.setNodeStyle(node, NODE_BACKGROUND_COLOR,
                    defaultBackgroundColor);
            display.setNodeStyle(node, NODE_BORDER_COLOR, Colors.ORANGE);
            display.setNodeStyle(node, NODE_FONT_COLOR, Colors.ORANGE);
            display.setNodeStyle(node, NODE_FONT_WEIGHT, NODE_FONT_WEIGHT_BOLD);
        }
            break;
        }
    }

}