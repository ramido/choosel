package org.thechiselgroup.choosel.core.client.views.resolvers;

public class FirstResourcePropertyResolverUIControllerFactory implements
        ViewItemValueResolverUIControllerFactory {

    private final String id;

    public FirstResourcePropertyResolverUIControllerFactory(String id) {
        assert id != null;
        this.id = id;
    }

    @Override
    public ViewItemValueResolverUIController create(
            ViewItemValueResolver resolver) {
        return new FirstResourcePropertyResolverUIController(resolver);
    }

    @Override
    public String getId() {
        return id;
    }

}
