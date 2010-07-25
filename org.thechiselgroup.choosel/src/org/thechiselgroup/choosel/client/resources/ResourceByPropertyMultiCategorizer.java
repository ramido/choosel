package org.thechiselgroup.choosel.client.resources;

import java.util.Set;

import org.thechiselgroup.choosel.client.util.CollectionUtils;

public class ResourceByPropertyMultiCategorizer implements
        ResourceMultiCategorizer {

    private String property;

    public ResourceByPropertyMultiCategorizer(String property) {
        this.property = property;
    }

    @Override
    public Set<String> getCategories(Resource resource) {
        return CollectionUtils.toSet((String) resource.getValue(property));
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
