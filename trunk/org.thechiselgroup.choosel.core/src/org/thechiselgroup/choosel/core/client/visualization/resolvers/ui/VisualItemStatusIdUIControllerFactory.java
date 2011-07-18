package org.thechiselgroup.choosel.core.client.visualization.resolvers.ui;

import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItem;
import org.thechiselgroup.choosel.core.client.visualization.model.managed.ManagedSlotMapping;
import org.thechiselgroup.choosel.core.client.visualization.model.managed.VisualItemValueResolverFactory;
import org.thechiselgroup.choosel.core.client.visualization.resolvers.VisualItemIdResolverFactory;

public class VisualItemStatusIdUIControllerFactory implements
        VisualItemValueResolverUIControllerFactory {

    @Override
    public VisualItemValueResolverUIController create(
            VisualItemValueResolverFactory factory,
            ManagedSlotMapping managedMapping,
            LightweightCollection<VisualItem> viewItems) {
        return new EmptyWidgetUIController();
    }

    @Override
    public String getId() {
        return VisualItemIdResolverFactory.ID;
    }

}
