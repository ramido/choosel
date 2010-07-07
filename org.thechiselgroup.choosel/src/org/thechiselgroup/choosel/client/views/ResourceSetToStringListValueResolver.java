package org.thechiselgroup.choosel.client.views;

import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceCategorizer;
import org.thechiselgroup.choosel.client.resources.ResourceSet;

public class ResourceSetToStringListValueResolver extends
        AbstractResourceSetToValueResolver {

    public ResourceSetToStringListValueResolver(String slotID,
            DefaultResourceToValueResolverFactory factory,
            ResourceCategorizer categorizer) {
        super(slotID, factory, categorizer);
    }

    @Override
    public Object resolve(ResourceSet resources, String category) {
        if (resources.isEmpty()) {
            return "";
        }

        if (resources.size() == 1) {
            return resolve(resources.getFirstResource()).toString();
        }

        String result = "{ ";
        boolean first = true;
        for (Resource resource : resources) {
            if (!first) {
                result += ", ";
            }

            result += resolve(resource);

            first = false;
        }
        result += " }";

        return result;
    }
}