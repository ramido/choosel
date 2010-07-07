package org.thechiselgroup.choosel.client.views;

import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceSet;

public class ResourceSetToSumResolver extends
        AbstractResourceSetToValueResolver {

    public ResourceSetToSumResolver(String slotID,
            DefaultResourceToValueResolverFactory factory) {
        super(slotID, factory);
    }

    @Override
    public Object resolve(ResourceSet resources, String category) {
        double sum = 0d;
        for (Resource resource : resources) {
            sum += ((Number) resolve(resource)).doubleValue();
        }

        return Double.toString(sum);
    }
}