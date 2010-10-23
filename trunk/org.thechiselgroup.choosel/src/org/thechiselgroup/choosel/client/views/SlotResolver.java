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
package org.thechiselgroup.choosel.client.views;

import java.util.List;

import org.thechiselgroup.choosel.client.resolver.ResourceToValueResolver;

// TODO create more flexible slot system
public interface SlotResolver {

    Slot CHART_LABEL_SLOT = new Slot("chart-label", "Label", DataType.TEXT);

    Slot CHART_VALUE_SLOT = new Slot("chart-value", "Value", DataType.NUMBER);

    Slot COLOR_SLOT = new Slot("color", "Color", DataType.COLOR);

    Slot DATE_SLOT = new Slot("date", "Date", DataType.DATE);

    Slot DESCRIPTION_SLOT = new Slot("description", "Label", DataType.TEXT);

    Slot GRAPH_LABEL_SLOT = new Slot("graphLabel", "Label", DataType.TEXT);

    Slot GRAPH_NODE_BACKGROUND_COLOR_SLOT = new Slot(
            "graphNodeBackgroundColor", "Node Color", DataType.COLOR);

    Slot GRAPH_NODE_BORDER_COLOR_SLOT = new Slot("graphNodeBorderColor",
            "Node Border Color", DataType.COLOR);

    Slot LOCATION_SLOT = new Slot("location", "Location", DataType.LOCATION);

    Slot FONT_SIZE_SLOT = new Slot("font-size", "Font Size", DataType.NUMBER);

    Slot X_COORDINATE_SLOT = new Slot("x-coord", "X-Axis", DataType.NUMBER);

    Slot Y_COORDINATE_SLOT = new Slot("y-coord", "Y-Axis", DataType.NUMBER);

    ResourceToValueResolver createChartLabelSlotResolver(String category);

    ResourceToValueResolver createChartValueSlotResolver(String category);

    ResourceToValueResolver createColorSlotResolver(String category,
            List<ResourceItemValueResolver> layers);

    ResourceToValueResolver createDateSlotResolver(String category);

    ResourceToValueResolver createDescriptionSlotResolver(String category);

    ResourceToValueResolver createFontSizeSlotResolver(String category);

    ResourceToValueResolver createGraphLabelSlotResolver(String category);

    ResourceToValueResolver createGraphNodeBackgroundColorResolver(
            String category);

    ResourceToValueResolver createGraphNodeBorderColorResolver(String category);

    ResourceToValueResolver createLabelSlotResolver(String category);

    ResourceToValueResolver createLocationSlotResolver(String category);

    ResourceToValueResolver createMagnitudeSlotResolver(String category);

    ResourceToValueResolver createXCoordinateSlotResolver(String category);

    ResourceToValueResolver createYCoordinateSlotResolver(String category);

}