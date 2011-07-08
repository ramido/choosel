package org.thechiselgroup.choosel.core.client.views.resolvers;

import java.util.ArrayList;
import java.util.List;

import org.thechiselgroup.choosel.core.client.resources.Resource;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.views.model.Slot;
import org.thechiselgroup.choosel.core.client.views.model.ViewItem;

public abstract class PropertyDependantViewItemValueResolverFactory implements
        ViewItemValueResolverFactory {

    @Override
    public boolean canCreateApplicableResolver(Slot slot,
            LightweightCollection<ViewItem> viewItems) {

        if (getSharedProperties(viewItems).isEmpty()) {
            return false;
        }
        return true;
    }

    @Override
    public abstract ManagedViewItemValueResolver create(
            LightweightCollection<ViewItem> viewItems);

    public abstract ManagedViewItemValueResolver create(String property);

    @Override
    public abstract String getId();

    @Override
    public abstract String getLabel();

    // TODO move somewhere else
    protected List<String> getSharedProperties(
            LightweightCollection<ViewItem> viewItems) {

        List<String> properties = new ArrayList<String>();

        if (viewItems.isEmpty()) {
            return properties;
        }

        // intialize properties to be the ones in the first resource
        Resource firstResource = viewItems.getFirstElement().getResources()
                .getFirstElement();
        properties.addAll(firstResource.getProperties().keySet());

        // only keep properties that are shared by all of the resource
        for (ViewItem viewItem : viewItems) {
            for (Resource resource : viewItem.getResources()) {
                properties.retainAll(resource.getProperties().keySet());
            }
        }
        return properties;
    }

}
