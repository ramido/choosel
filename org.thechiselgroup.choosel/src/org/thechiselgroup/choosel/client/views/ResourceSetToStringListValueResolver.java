package org.thechiselgroup.choosel.client.views;

import java.util.ArrayList;
import java.util.List;

import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceCategorizer;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.util.CollectionUtils;

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

        List<String> values = new ArrayList<String>();
        for (Resource resource : resources) {
            values.add((String) resolve(resource));
        }
        result += CollectionUtils.deliminateIterableStringCollection(values,
                ", ");
        result += " }";

        return result;
    }
}