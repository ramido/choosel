package org.thechiselgroup.choosel.core.client.views;

import java.util.Map;
import java.util.Set;

import org.thechiselgroup.choosel.core.client.util.collections.CollectionFactory;

// TODO move to framework
public class ViewContentDisplaysConfiguration {

    private Map<String, ViewContentDisplayFactory> factoryMap = CollectionFactory
            .createStringMap();

    public ViewContentDisplay createDisplay(String type) {
        return getFactory(type).createViewContentDisplay();
    }

    public ViewContentDisplayFactory getFactory(String type) {
        assert type != null;
        assert factoryMap.containsKey(type);

        return factoryMap.get(type);

    }

    public Set<String> getRegisteredTypes() {
        return factoryMap.keySet();
    }

    public void register(ViewContentDisplayFactory factory) {
        assert factory != null;

        factoryMap.put(factory.getViewContentTypeID(), factory);
    }

}