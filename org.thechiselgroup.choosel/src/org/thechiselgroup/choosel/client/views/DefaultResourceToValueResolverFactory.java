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

import org.thechiselgroup.choosel.client.resolver.ResourceToValueResolver;

public class DefaultResourceToValueResolverFactory {

    // TODO we need to get this
    private SlotResolver slotResolver;

    public DefaultResourceToValueResolverFactory(SlotResolver slotResolver) {
        this.slotResolver = slotResolver;
    }

    // TODO category is actually resource type
    public ResourceToValueResolver createResolver(Slot slot, String category) {

        // TODO current work:
        // need interface: slotId, category, set of resources, TYPE e.g.
        // string
        // --> value
        // what about context?

        assert category != null;
        assert slot != null;

        // TODO use maps instead, need better slot system
        if (SlotResolver.COLOR_SLOT.equals(slot)) {
            return slotResolver.createColorSlotResolver(category, null);
        }

        if (SlotResolver.LABEL_SLOT.equals(slot)) {
            return slotResolver.createLabelSlotResolver(category);
        }

        if (SlotResolver.DESCRIPTION_SLOT.equals(slot)) {
            return slotResolver.createDescriptionSlotResolver(category);
        }

        if (SlotResolver.CHART_LABEL_SLOT.equals(slot)) {
            return slotResolver.createChartLabelSlotResolver(category);
        }

        if (SlotResolver.DATE_SLOT.equals(slot)) {
            return slotResolver.createDateSlotResolver(category);
        }

        if (SlotResolver.LOCATION_SLOT.equals(slot)) {
            return slotResolver.createLocationSlotResolver(category);
        }

        if (SlotResolver.GRAPH_LABEL_SLOT.equals(slot)) {
            return slotResolver.createGraphLabelSlotResolver(category);
        }

        if (SlotResolver.GRAPH_NODE_BORDER_COLOR_SLOT.equals(slot)) {
            return slotResolver.createGraphNodeBorderColorResolver(category);
        }

        if (SlotResolver.GRAPH_NODE_BACKGROUND_COLOR_SLOT.equals(slot)) {
            return slotResolver
                    .createGraphNodeBackgroundColorResolver(category);
        }

        if (slot.equals(SlotResolver.MAGNITUDE_SLOT)) {
            return slotResolver.createMagnitudeSlotResolver(category);
        }

        if (slot.equals(SlotResolver.X_COORDINATE_SLOT)) {
            return slotResolver.createXCoordinateSlotResolver(category);
        }

        if (slot.equals(SlotResolver.Y_COORDINATE_SLOT)) {
            return slotResolver.createYCoordinateSlotResolver(category);
        }

        if (slot.equals(SlotResolver.FONT_SIZE_SLOT)) {
            return slotResolver.createFontSizeSlotResolver(category);
        }

        if (slot.equals(SlotResolver.CHART_VALUE_SLOT)) {
            return slotResolver.createFontSizeSlotResolver(category);
        }

        throw new IllegalArgumentException("Invalid slot id: " + slot);
    }
}