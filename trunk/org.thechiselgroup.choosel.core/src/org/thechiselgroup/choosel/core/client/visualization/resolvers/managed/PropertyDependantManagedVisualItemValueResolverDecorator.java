package org.thechiselgroup.choosel.core.client.visualization.resolvers.managed;

import org.thechiselgroup.choosel.core.client.visualization.model.VisualItemValueResolver;
import org.thechiselgroup.choosel.core.client.visualization.model.managed.ManagedVisualItemValueResolverDecorator;
import org.thechiselgroup.choosel.core.client.visualization.resolvers.PropertyDependantViewItemValueResolver;

/**
 * This class is a specific type of ManagedResolverDecorator for Resolvers that
 * are Property Dependent. This is needed because in some cases, we assert that
 * the resolvers are property dependent, but because they use a decorator, a
 * simple instanceof check won't work. Instead, we use this class and do an
 * instance check on it
 */
public class PropertyDependantManagedVisualItemValueResolverDecorator extends
        ManagedVisualItemValueResolverDecorator implements
        PropertyDependantViewItemValueResolver {

    public PropertyDependantManagedVisualItemValueResolverDecorator(
            String resolverId, VisualItemValueResolver delegate) {

        super(resolverId, delegate);
        assert delegate instanceof PropertyDependantViewItemValueResolver;
    }

    @Override
    public String getProperty() {
        return ((PropertyDependantViewItemValueResolver) delegate)
                .getProperty();
    }

}
