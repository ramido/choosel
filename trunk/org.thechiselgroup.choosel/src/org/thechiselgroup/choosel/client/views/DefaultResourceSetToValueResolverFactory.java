package org.thechiselgroup.choosel.client.views;

import org.thechiselgroup.choosel.client.resolver.ResourceSetToValueResolver;

public class DefaultResourceSetToValueResolverFactory {

    private DefaultResourceToValueResolverFactory resourceResolverFactory;

    public DefaultResourceSetToValueResolverFactory(SlotResolver slotResolver) {
        this.resourceResolverFactory = new DefaultResourceToValueResolverFactory(
                slotResolver);
    }

    public ResourceSetToValueResolver createResolver(String slotID) {
        assert slotID != null;

        // TODO need default aggregate resolvers for the different slots
        if (SlotResolver.DESCRIPTION_SLOT.equals(slotID)) {
            return new ResourceSetToStringListValueResolver(slotID,
                    resourceResolverFactory);
        }

        if (SlotResolver.COLOR_SLOT.equals(slotID)) {
            return new ResourceSetToColorResolver();
        }

        if (SlotResolver.LABEL_SLOT.equals(slotID)) {
            return new ResourceSetToStringListValueResolver(slotID,
                    resourceResolverFactory);
        }

        if (SlotResolver.DATE_SLOT.equals(slotID)) {
            return new ResourceSetToFirstResourcePropertyResolver(slotID,
                    resourceResolverFactory);
        }

        if (SlotResolver.LOCATION_SLOT.equals(slotID)) {
            return new ResourceSetToFirstResourcePropertyResolver(slotID,
                    resourceResolverFactory);
        }

        if (SlotResolver.GRAPH_LABEL_SLOT.equals(slotID)) {
            return new ResourceSetToStringListValueResolver(slotID,
                    resourceResolverFactory);
        }

        // if (SlotResolver.GRAPH_NODE_BORDER_COLOR_SLOT.equals(slotID)) {
        // return slotResolver.createGraphNodeBorderColorResolver(category);
        // }

        // if (SlotResolver.GRAPH_NODE_BACKGROUND_COLOR_SLOT.equals(slotID)) {
        // return slotResolver
        // .createGraphNodeBackgroundColorResolver(category);
        // }

        if (slotID.equals(SlotResolver.MAGNITUDE_SLOT)) {
            return new ResourceSetToSumResolver(slotID, resourceResolverFactory);
        }

        if (slotID.equals(SlotResolver.X_COORDINATE_SLOT)) {
            return new ResourceSetToSumResolver(slotID, resourceResolverFactory);
        }

        if (slotID.equals(SlotResolver.Y_COORDINATE_SLOT)) {
            return new ResourceSetToSumResolver(slotID, resourceResolverFactory);
        }

        return new ResourceSetToCountResolver();
    }
}
