package org.thechiselgroup.choosel.client.views;

import java.util.HashMap;
import java.util.Map;

import org.thechiselgroup.choosel.client.resolver.ResourceSetToValueResolver;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceCategorizer;
import org.thechiselgroup.choosel.client.resources.ResourceSet;

public class ResourceSetToColorResolver implements ResourceSetToValueResolver {

    private ResourceCategorizer categorizer;

    private Map<String, String> resourceTypeToColor = new HashMap<String, String>();

    private static final String[] COLORS = new String[] { "#6495ed", "#b22222",
            "#A9C0B1" };

    public ResourceSetToColorResolver(ResourceCategorizer categorizer) {
        this.categorizer = categorizer;
    }

    @Override
    public Object resolve(ResourceSet resources, String category) {
        // TODO what if resource.isEmpty?
        if (resources.isEmpty()) {
            return COLORS[0]; // TODO we need something better
        }

        Resource resource = resources.getFirstResource();
        String resourceType = categorizer.getCategory(resource);

        if (!resourceTypeToColor.containsKey(resourceType)) {
            resourceTypeToColor.put(resourceType,
                    COLORS[resourceTypeToColor.size()]);
        }

        return resourceTypeToColor.get(resourceType);
    }
}