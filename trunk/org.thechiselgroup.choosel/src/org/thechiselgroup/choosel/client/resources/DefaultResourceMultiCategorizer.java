package org.thechiselgroup.choosel.client.resources;

import java.util.HashSet;
import java.util.Set;

public class DefaultResourceMultiCategorizer implements
        ResourceMultiCategorizer {

    @Override
    public Set<String> getCategories(Resource resource) {
        Set<String> categories = new HashSet<String>();
        String description = (String) (resource.getValue("description"));

        if (description == null)
            return categories;

        if (description.contains("N"))
            categories.add("N");

        if (description.contains("W"))
            categories.add("W");

        if (description.contains("E"))
            categories.add("E");

        if (description.contains("S"))
            categories.add("S");

        return categories;
    }
}
