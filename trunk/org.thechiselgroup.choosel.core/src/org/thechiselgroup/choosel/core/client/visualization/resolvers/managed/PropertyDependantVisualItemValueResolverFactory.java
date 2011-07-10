package org.thechiselgroup.choosel.core.client.visualization.resolvers.managed;

import java.util.ArrayList;
import java.util.List;

import org.thechiselgroup.choosel.core.client.resources.Resource;
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

        if (getSharedProperties(viewItems).isEmpty()) {
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

    // TODO move somewhere else
    protected List<String> getSharedProperties(
            LightweightCollection<VisualItem> viewItems) {

        List<String> properties = new ArrayList<String>();

        if (viewItems.isEmpty()) {
            return properties;
        }

        // intialize properties to be the ones in the first resource
        Resource firstResource = viewItems.getFirstElement().getResources()
                .getFirstElement();
        properties.addAll(firstResource.getProperties().keySet());

        // only keep properties that are shared by all of the resource
        for (VisualItem viewItem : viewItems) {
            for (Resource resource : viewItem.getResources()) {
                properties.retainAll(resource.getProperties().keySet());
            }
        }
        return properties;
    }

}
