package org.thechiselgroup.choosel.client.views;

import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceCategorizer;
import org.thechiselgroup.choosel.client.resources.ResourceSet;

public class ResourceSetToSumResolver extends
        AbstractResourceSetToValueResolver {

    public ResourceSetToSumResolver(String slotID,
            DefaultResourceToValueResolverFactory factory,
            ResourceCategorizer categorizer) {
        super(slotID, factory, categorizer);
    }

    @Override
    public Object resolve(ResourceSet resources, String category) {
        double sum = 0d;
        for (Resource resource : resources) {
            Object value = resolve(resource);

            if (value instanceof String) {
                value = Double.parseDouble((String) value);
            }

            sum += ((Number) value).doubleValue();
        }

        return Double.toString(sum);
    }
}