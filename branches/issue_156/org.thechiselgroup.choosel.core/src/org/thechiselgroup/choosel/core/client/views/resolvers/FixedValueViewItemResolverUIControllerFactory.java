package org.thechiselgroup.choosel.core.client.views.resolvers;


public class FixedValueViewItemResolverUIControllerFactory implements
        ViewItemValueResolverUIControllerFactory {

    private final String id;

    public FixedValueViewItemResolverUIControllerFactory(String id) {
        this.id = id;
    }

    @Override
    public ViewItemValueResolverUIController create(
            ViewItemValueResolver resolver) {
        return new FixedValueViewItemResolverUIController();
    }

    @Override
    public String getId() {
        return id;

    }
}
