package org.thechiselgroup.choosel.client.views;

import org.thechiselgroup.choosel.client.resolver.ResourceSetToValueResolver;
import org.thechiselgroup.choosel.client.resources.ResourceCategorizer;

public class DefaultResourceSetToValueResolverFactory {

    private DefaultResourceToValueResolverFactory resourceResolverFactory;

    private ResourceCategorizer resourceByTypeCategorizer;

    public DefaultResourceSetToValueResolverFactory(SlotResolver slotResolver,
            ResourceCategorizer resourceByTypeCategorizer) {
        this.resourceByTypeCategorizer = resourceByTypeCategorizer;
        this.resourceResolverFactory = new DefaultResourceToValueResolverFactory(
                slotResolver);
    }

    public ResourceSetToValueResolver createResolver(String slotID) {
        assert slotID != null;

        // TODO need default aggregate resolvers for the different slots
        if (SlotResolver.DESCRIPTION_SLOT.equals(slotID)) {
            return new ResourceSetToStringListValueResolver(slotID,
                    resourceResolverFactory, resourceByTypeCategorizer);
        }

        if (SlotResolver.COLOR_SLOT.equals(slotID)) {
            return new ResourceSetToColorResolver(resourceByTypeCategorizer);
        }

        if (SlotResolver.LABEL_SLOT.equals(slotID)) {
            return new ResourceSetToStringListValueResolver(slotID,
                    resourceResolverFactory, resourceByTypeCategorizer);
        }

        if (SlotResolver.DATE_SLOT.equals(slotID)) {
            return new ResourceSetToFirstResourcePropertyResolver(slotID,
                    resourceResolverFactory, resourceByTypeCategorizer);
        }

        if (SlotResolver.LOCATION_SLOT.equals(slotID)) {
            return new ResourceSetToFirstResourcePropertyResolver(slotID,
                    resourceResolverFactory, resourceByTypeCategorizer);
        }

        if (SlotResolver.GRAPH_LABEL_SLOT.equals(slotID)) {
            return new ResourceSetToStringListValueResolver(slotID,
                    resourceResolverFactory, resourceByTypeCategorizer);
        }

        if (SlotResolver.GRAPH_NODE_BORDER_COLOR_SLOT.equals(slotID)) {
            return new ResourceSetToFirstResourcePropertyResolver(slotID,
                    resourceResolverFactory, resourceByTypeCategorizer);
        }

        if (SlotResolver.GRAPH_NODE_BACKGROUND_COLOR_SLOT.equals(slotID)) {
            return new ResourceSetToFirstResourcePropertyResolver(slotID,
                    resourceResolverFactory, resourceByTypeCategorizer);
        }

        if (slotID.equals(SlotResolver.MAGNITUDE_SLOT)) {
            return new ResourceSetToSumResolver(slotID,
                    resourceResolverFactory, resourceByTypeCategorizer);
        }

        if (slotID.equals(SlotResolver.X_COORDINATE_SLOT)) {
            return new ResourceSetToSumResolver(slotID,
                    resourceResolverFactory, resourceByTypeCategorizer);
        }

        if (slotID.equals(SlotResolver.Y_COORDINATE_SLOT)) {
            return new ResourceSetToSumResolver(slotID,
                    resourceResolverFactory, resourceByTypeCategorizer);
        }

        return new ResourceSetToCountResolver();
    }
}
