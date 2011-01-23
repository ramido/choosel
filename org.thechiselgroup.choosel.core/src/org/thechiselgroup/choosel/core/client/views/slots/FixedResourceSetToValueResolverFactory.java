package org.thechiselgroup.choosel.core.client.views.slots;

public class FixedResourceSetToValueResolverFactory implements
        ResourceSetToValueResolverFactory {

    private ResourceSetToValueResolver resolver;

    public FixedResourceSetToValueResolverFactory(
            ResourceSetToValueResolver resolver) {

        this.resolver = resolver;
    }

    @Override
    public String getDescription() {
        return resolver.toString();
    }

    @Override
    public ResourceSetToValueResolver getResolver() {
        return resolver;
    }

}