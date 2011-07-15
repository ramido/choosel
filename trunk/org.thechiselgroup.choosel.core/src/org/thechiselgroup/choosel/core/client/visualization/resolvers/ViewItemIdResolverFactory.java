package org.thechiselgroup.choosel.core.client.visualization.resolvers;

import org.thechiselgroup.choosel.core.client.util.DataType;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.visualization.model.Slot;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItem;
import org.thechiselgroup.choosel.core.client.visualization.model.managed.ManagedVisualItemValueResolver;
import org.thechiselgroup.choosel.core.client.visualization.model.managed.ManagedVisualItemValueResolverDecorator;
import org.thechiselgroup.choosel.core.client.visualization.model.managed.VisualItemValueResolverFactory;

public class ViewItemIdResolverFactory implements
        VisualItemValueResolverFactory {

    public static String ID = "ViewItemStatusIdFactory";

    @Override
    public boolean canCreateApplicableResolver(Slot slot,
            LightweightCollection<VisualItem> viewItems) {
        return DataType.TEXT.equals(slot.getDataType());
    }

    public ManagedVisualItemValueResolver create() {
        return new ManagedVisualItemValueResolverDecorator(ID,
                new ViewItemIdResolver());
    }

    @Override
    public ManagedVisualItemValueResolver create(
            LightweightCollection<VisualItem> viewItems) {
        return create();
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getLabel() {
        return "Group Name";
    }

}
