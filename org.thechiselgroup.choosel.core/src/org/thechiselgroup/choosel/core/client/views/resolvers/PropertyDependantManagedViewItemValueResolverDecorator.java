package org.thechiselgroup.choosel.core.client.views.resolvers;

/**
 * This class is a specific type of ManagedResolverDecorator for Resolvers that
 * are Property Dependent. This is needed because in some cases, we assert that
 * the resolvers are property dependent, but because they use a decorator, a
 * simple instanceof check won't work. Instead, we use this class and do an
 * instance check on it
 */
public class PropertyDependantManagedViewItemValueResolverDecorator extends
        ManagedViewItemValueResolverDecorator implements
        PropertyDependantViewItemValueResolver {

    public PropertyDependantManagedViewItemValueResolverDecorator(
            String resolverId, ViewItemValueResolver delegate) {

        super(resolverId, delegate);
        assert delegate instanceof PropertyDependantViewItemValueResolver;
    }

    @Override
    public String getProperty() {
        return ((PropertyDependantViewItemValueResolver) delegate)
                .getProperty();
    }

}
