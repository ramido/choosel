package org.thechiselgroup.choosel.client.views;

import java.util.HashMap;
import java.util.Map;

import org.thechiselgroup.choosel.client.resolver.ResourceSetToValueResolver;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceByUriTypeCategorizer;
import org.thechiselgroup.choosel.client.resources.ResourceSet;

public class ResourceSetToColorResolver implements
        ResourceSetToValueResolver {

    // TODO this could be variable!
    private ResourceByUriTypeCategorizer categorizer = new ResourceByUriTypeCategorizer();

    private Map<String, String> resourceTypeToColor = new HashMap<String, String>();

    private static final String[] COLORS = new String[] { "#6495ed",
            "#b22222" };

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