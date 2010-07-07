package org.thechiselgroup.choosel.client.views;

import java.util.HashMap;
import java.util.Map;

import org.thechiselgroup.choosel.client.resolver.ResourceSetToValueResolver;
import org.thechiselgroup.choosel.client.resolver.ResourceToValueResolver;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceByUriTypeCategorizer;

public abstract class AbstractResourceSetToValueResolver implements
        ResourceSetToValueResolver {

    protected Map<String, ResourceToValueResolver> resourceTypeToResourceToValueResolvers = new HashMap<String, ResourceToValueResolver>();

    protected ResourceByUriTypeCategorizer categorizer = new ResourceByUriTypeCategorizer();

    private DefaultResourceToValueResolverFactory factory;

    private String slotID;

    public AbstractResourceSetToValueResolver(String slotID,
            DefaultResourceToValueResolverFactory factory) {

        this.slotID = slotID;
        this.factory = factory;
    }

    private ResourceToValueResolver getResourceToValueResolver(
            String resourceType) {

        if (!resourceTypeToResourceToValueResolvers.containsKey(resourceType)) {

            resourceTypeToResourceToValueResolvers.put(resourceType,
                    factory.createResolver(slotID, resourceType));
        }

        return resourceTypeToResourceToValueResolvers.get(resourceType);
    }

    public Object resolve(Resource resource) {
        String resourceType = categorizer.getCategory(resource);
        return getResourceToValueResolver(resourceType).resolve(resource);
    }

}