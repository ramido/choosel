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

    String COLOR_SLOT = "color";

    String DATE_SLOT = "date";

    String DESCRIPTION_SLOT = "description";

    String GRAPH_LABEL_SLOT = "graphLabel";

    String GRAPH_NODE_BACKGROUND_COLOR_SLOT = "graphNodeBackgroundColor";

    String GRAPH_NODE_BORDER_COLOR_SLOT = "graphNodeBorderColor";

    String LABEL_SLOT = "label";

    String LOCATION_SLOT = "location";

    // TODO slots should be view-centric, not data centric
    String MAGNITUDE_SLOT = "magnitude";

    String FONT_SIZE_SLOT = "font-size";

    String X_COORDINATE_SLOT = "x-coord";

    String Y_COORDINATE_SLOT = "y-coord";

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