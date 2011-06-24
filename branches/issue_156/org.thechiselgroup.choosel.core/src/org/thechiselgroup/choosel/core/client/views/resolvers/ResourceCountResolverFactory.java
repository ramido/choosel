package org.thechiselgroup.choosel.core.client.views.resolvers;

import org.thechiselgroup.choosel.core.client.resources.DataType;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightList;
import org.thechiselgroup.choosel.core.client.views.model.Slot;
import org.thechiselgroup.choosel.core.client.views.model.ViewItem.Subset;

public class ResourceCountResolverFactory implements
        ViewItemValueResolverFactory {

    private final Subset subset;

    public ResourceCountResolverFactory() {
        this(Subset.ALL);
    }

    public ResourceCountResolverFactory(Subset subset) {
        this.subset = subset;
    }

    @Override
    public boolean canCreateApplicableResolver(Slot slot,
            LightweightList<ResourceSet> resourceSets) {
        return slot.getDataType().equals(DataType.NUMBER);
    }

    @Override
    public ViewItemValueResolver create(
            LightweightList<ResourceSet> resourceSets) {
        return new ResourceCountResolver(subset);
    }

    @Override
    public String getId() {
        return "org.thechiselgroup.choosel.core.client.views.resolvers.ResourceCountResolverFactory";
    }

    @Override
    public String getLabel() {
        return "Count";
    }

}
