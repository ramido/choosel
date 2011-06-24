package org.thechiselgroup.choosel.core.client.views.resolvers;

public class ResourceCountResolverUIControllerFactory implements
        ViewItemValueResolverUIControllerFactory {

    private static final String ID = "ResourceCountResolver";

    @Override
    public ViewItemValueResolverUIController create(
            ViewItemValueResolver resolver) {
        return new ResourceCountResolverUIController();
    }

    @Override
    public String getId() {
        return ID;
    }

}
