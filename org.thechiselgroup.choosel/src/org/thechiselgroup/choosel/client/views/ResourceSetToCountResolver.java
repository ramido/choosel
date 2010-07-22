package org.thechiselgroup.choosel.client.views;

import org.thechiselgroup.choosel.client.resolver.ResourceSetToValueResolver;
import org.thechiselgroup.choosel.client.resources.ResourceSet;

public class ResourceSetToCountResolver implements ResourceSetToValueResolver {

    @Override
    public Object resolve(ResourceSet resources, String category) {
        return Integer.toString(resources.size());
    }
}