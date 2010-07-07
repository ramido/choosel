package org.thechiselgroup.choosel.client.views;

import org.thechiselgroup.choosel.client.resolver.ResourceToValueResolver;

public class DefaultResourceToValueResolverFactory {

    // TODO we need to get this
    private SlotResolver slotResolver;

    public DefaultResourceToValueResolverFactory(SlotResolver slotResolver) {
        this.slotResolver = slotResolver;
    }

    // TODO category is actually resource type
    public ResourceToValueResolver createResolver(String slotID, String category) {

        // TODO current work:
        // need interface: slotId, category, set of resources, TYPE e.g.
        // string
        // --> value
        // what about context?

        assert category != null;
        assert slotID != null;

        // TODO use maps instead, need better slot system
        if (SlotResolver.COLOR_SLOT.equals(slotID)) {
            return slotResolver.createColorSlotResolver(category, null);
        }

        if (SlotResolver.LABEL_SLOT.equals(slotID)) {
            return slotResolver.createLabelSlotResolver(category);
        }

        if (SlotResolver.DESCRIPTION_SLOT.equals(slotID)) {
            return slotResolver.createDescriptionSlotResolver(category);
        }

        if (SlotResolver.DATE_SLOT.equals(slotID)) {
            return slotResolver.createDateSlotResolver(category);
        }

        if (SlotResolver.LOCATION_SLOT.equals(slotID)) {
            return slotResolver.createLocationSlotResolver(category);
        }

        if (SlotResolver.GRAPH_LABEL_SLOT.equals(slotID)) {
            return slotResolver.createGraphLabelSlotResolver(category);
        }

        if (SlotResolver.GRAPH_NODE_BORDER_COLOR_SLOT.equals(slotID)) {
            return slotResolver.createGraphNodeBorderColorResolver(category);
        }

        if (SlotResolver.GRAPH_NODE_BACKGROUND_COLOR_SLOT.equals(slotID)) {
            return slotResolver
                    .createGraphNodeBackgroundColorResolver(category);
        }

        if (slotID.equals(SlotResolver.MAGNITUDE_SLOT)) {
            return slotResolver.createMagnitudeSlotResolver(category);
        }

        if (slotID.equals(SlotResolver.X_COORDINATE_SLOT)) {
            return slotResolver.createXCoordinateSlotResolver(category);
        }

        if (slotID.equals(SlotResolver.Y_COORDINATE_SLOT)) {
            return slotResolver.createYCoordinateSlotResolver(category);
        }

        throw new IllegalArgumentException("Invalid slot id: " + slotID);
    }

}