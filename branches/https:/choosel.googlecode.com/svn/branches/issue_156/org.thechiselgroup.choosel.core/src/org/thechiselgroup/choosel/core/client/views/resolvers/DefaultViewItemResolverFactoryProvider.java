package org.thechiselgroup.choosel.core.client.views.resolvers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.thechiselgroup.choosel.core.client.util.collections.CollectionFactory;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightList;

public class DefaultViewItemResolverFactoryProvider implements
        ViewItemValueResolverFactoryProvider {

    public Map<String, ViewItemValueResolverFactory> factories = new HashMap<String, ViewItemValueResolverFactory>();

    @Override
    public ViewItemValueResolverFactory getFactoryById(String id) {
        return factories.get(id);
    }

    @Override
    public LightweightList<ViewItemValueResolverFactory> getResolverFactories() {
        LightweightList<ViewItemValueResolverFactory> results = CollectionFactory
                .createLightweightList();
        results.addAll(new ArrayList<ViewItemValueResolverFactory>(factories
                .values()));
        return results;
    }

    /**
     * This method will add in your new factory into the providers Map of
     * factories. If the new factory's ID is already contained in the map, it
     * will not be added.
     */
    public void registerFactory(ViewItemValueResolverFactory resolverFactory) {
        if (factories.containsKey(resolverFactory.getId())) {
            return;
        }
        factories.put(resolverFactory.getId(), resolverFactory);
    }
}
