package org.thechiselgroup.choosel.core.client.views.resolvers;

import org.thechiselgroup.choosel.core.client.util.collections.LightweightList;

public interface ViewItemValueResolverUIFactoryProvider {

    void add(ViewItemValueResolverUIFactory factory);

    LightweightList<ViewItemValueResolverUIFactory> getDefaultFactories();

    LightweightList<ViewItemValueResolverUIFactory> getFactories();

    ViewItemValueResolverUIFactory getFactoryById(String id);

}
