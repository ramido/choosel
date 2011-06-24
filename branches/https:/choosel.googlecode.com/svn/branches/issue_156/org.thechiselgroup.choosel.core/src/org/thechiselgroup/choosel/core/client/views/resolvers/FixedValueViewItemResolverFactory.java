package org.thechiselgroup.choosel.core.client.views.resolvers;

import org.thechiselgroup.choosel.core.client.resources.DataType;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightList;
import org.thechiselgroup.choosel.core.client.views.model.Slot;

public class FixedValueViewItemResolverFactory implements
        ViewItemValueResolverFactory {

    private final Object value;

    private final DataType dataType;

    private final String id;

    public FixedValueViewItemResolverFactory(Object value, DataType valueType,
            String id) {
        this.value = value;
        this.dataType = valueType;
        this.id = id;
    }

    @Override
    public boolean canCreateApplicableResolver(Slot slot,
            LightweightList<ResourceSet> resourceSets) {
        return slot.getDataType().equals(dataType);
    }

    /**
     * This method does not need to worry about the viewItems because it is
     * fixed value
     */
    @Override
    public ViewItemValueResolver create(
            LightweightList<ResourceSet> resourceSets) {
        return new FixedValueResolver(value, id, dataType);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getLabel() {
        return value.toString();
    }

}
