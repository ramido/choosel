package org.thechiselgroup.choosel.client.resources;

import java.util.Set;

import org.thechiselgroup.choosel.client.util.CollectionUtils;

public class ResourceByPropertyMultiCategorizer implements
        ResourceMultiCategorizer {

    @Override
    public Set<String> getCategories(Resource resource) {
        return CollectionUtils.toSet((String) resource.getValue("tagContent"));
    }
}
