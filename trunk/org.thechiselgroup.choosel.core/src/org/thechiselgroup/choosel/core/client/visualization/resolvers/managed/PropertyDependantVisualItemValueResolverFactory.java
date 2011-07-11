package org.thechiselgroup.choosel.core.client.visualization.resolvers.managed;

import org.thechiselgroup.choosel.core.client.resources.ResourceSetUtils;
import org.thechiselgroup.choosel.core.client.util.DataType;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.visualization.model.Slot;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItem;
import org.thechiselgroup.choosel.core.client.visualization.model.managed.ManagedVisualItemValueResolver;
import org.thechiselgroup.choosel.core.client.visualization.model.managed.VisualItemValueResolverFactory;

public abstract class PropertyDependantVisualItemValueResolverFactory implements
        VisualItemValueResolverFactory {

    @Override
    public boolean canCreateApplicableResolver(Slot slot,
            LightweightCollection<VisualItem> viewItems) {

        if (ResourceSetUtils.getSharedPropertiesOfDataType(viewItems,
                getValidDataType()).isEmpty()) {
            return false;
        }
        return true;
    }

    @Override
    public abstract ManagedVisualItemValueResolver create(
            LightweightCollection<VisualItem> viewItems);

    public abstract ManagedVisualItemValueResolver create(String property);

    @Override
    public abstract String getId();

    @Override
    public abstract String getLabel();

    public abstract DataType getValidDataType();
}
