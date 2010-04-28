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

import static org.thechiselgroup.choosel.client.ui.widget.graph.GraphDisplay.*;

import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.ui.popup.PopupManager;
import org.thechiselgroup.choosel.client.ui.widget.graph.Node;
import org.thechiselgroup.choosel.client.views.Layer;
import org.thechiselgroup.choosel.client.views.ResourceItem;
import org.thechiselgroup.choosel.client.views.graph.GraphViewContentDisplay.Display;

public class GraphItem extends ResourceItem {

    private static final String BLACK = "#000000";

    private static final String BLUE_1 = "#AFC6E5";

    private static final String BLUE_2 = "#447595";

    private static final String BLUE_3 = "#DAE5F3";

    public static final String GRAY_1 = "#D4D4D4";

    public static final String GRAY_2 = "#474444";

    public static final String ORANGE = "#E7B076";

    private static final String YELLOW_1 = "#FFFFE1";

    private static final String YELLOW_2 = "#D4D0C8";

    private String defaultBackgroundColor = BLUE_3;

    private String defaultBorderColor = BLUE_1;

    private Display display;

    private Node node;

    public GraphItem(Resource individual, ResourceSet hoverModel,
	    PopupManager popupManager, Node node,
	    GraphViewContentDisplay.Display display, Layer Layer) {

	super(individual, hoverModel, popupManager, Layer);

	this.node = node;
	this.display = display;

	updateStyling();
    }

    public Node getNode() {
	return node;
    }

    public void setDefaultColors(String backgroundColor, String borderColor) {
	this.defaultBorderColor = borderColor;
	this.defaultBackgroundColor = backgroundColor;
	updateStyling();
    }

    @Override
    protected void setStatusStyling(Status status) {
	switch (status) {
	case HIGHLIGHTED_SELECTED: {
	    display.setNodeStyle(node, NODE_BACKGROUND_COLOR, YELLOW_1);
	    display.setNodeStyle(node, NODE_BORDER_COLOR, ORANGE);
	    display.setNodeStyle(node, NODE_FONT_COLOR, ORANGE);
	    display.setNodeStyle(node, NODE_FONT_WEIGHT, NODE_FONT_WEIGHT_BOLD);
	}
	    break;
	case HIGHLIGHTED: {
	    display.setNodeStyle(node, NODE_BACKGROUND_COLOR, YELLOW_1);
	    display.setNodeStyle(node, NODE_BORDER_COLOR, YELLOW_2);
	    display.setNodeStyle(node, NODE_FONT_COLOR, BLACK);
	    display.setNodeStyle(node, NODE_FONT_WEIGHT,
		    NODE_FONT_WEIGHT_NORMAL);
	}
	    break;
	case DEFAULT: {
	    display.setNodeStyle(node, NODE_BACKGROUND_COLOR,
		    defaultBackgroundColor);
	    display.setNodeStyle(node, NODE_BORDER_COLOR, defaultBorderColor);
	    display.setNodeStyle(node, NODE_FONT_COLOR, BLACK);
	    display.setNodeStyle(node, NODE_FONT_WEIGHT,
		    NODE_FONT_WEIGHT_NORMAL);
	}
	    break;
	case GRAYED_OUT: {
	    display.setNodeStyle(node, NODE_BACKGROUND_COLOR, GRAY_1);
	    display.setNodeStyle(node, NODE_BORDER_COLOR, GRAY_2);
	    display.setNodeStyle(node, NODE_FONT_COLOR, BLACK);
	    display.setNodeStyle(node, NODE_FONT_WEIGHT,
		    NODE_FONT_WEIGHT_NORMAL);
	}
	    break;
	case SELECTED: {
	    display.setNodeStyle(node, NODE_BACKGROUND_COLOR,
		    defaultBackgroundColor);
	    display.setNodeStyle(node, NODE_BORDER_COLOR, ORANGE);
	    display.setNodeStyle(node, NODE_FONT_COLOR, ORANGE);
	    display.setNodeStyle(node, NODE_FONT_WEIGHT, NODE_FONT_WEIGHT_BOLD);
	}
	    break;
	}
    }

}