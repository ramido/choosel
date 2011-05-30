package org.thechiselgroup.choosel.core.client.views.resolvers;

import org.thechiselgroup.choosel.core.client.util.collections.LightweightList;

public interface ViewItemValueResolverFactoryProvider {

    ViewItemValueResolverFactory getFactoryById(String id);

    LightweightList<ViewItemValueResolverFactory> getResolverFactories();
}
