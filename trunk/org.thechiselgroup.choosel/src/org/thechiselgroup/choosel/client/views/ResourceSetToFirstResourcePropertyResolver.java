package org.thechiselgroup.choosel.client.views;

import org.thechiselgroup.choosel.client.resources.ResourceSet;

public class ResourceSetToFirstResourcePropertyResolver extends
        AbstractResourceSetToValueResolver {

    public ResourceSetToFirstResourcePropertyResolver(String slotID,
            DefaultResourceToValueResolverFactory factory) {
        super(slotID, factory);
    }

    @Override
    public Object resolve(ResourceSet resources, String category) {
        assert !resources.isEmpty();
        return resolve(resources.getFirstResource());
    }

}