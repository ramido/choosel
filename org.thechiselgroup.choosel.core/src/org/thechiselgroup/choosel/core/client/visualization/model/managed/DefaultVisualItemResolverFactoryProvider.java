/*******************************************************************************
 * Copyright (C) 2011 Lars Grammel 
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0 
 *     
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.  
 *******************************************************************************/
package org.thechiselgroup.choosel.core.client.visualization.model.managed;

import java.util.HashMap;
import java.util.Map;

import org.thechiselgroup.choosel.core.client.util.collections.CollectionFactory;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightList;

public class DefaultVisualItemResolverFactoryProvider implements
        VisualItemValueResolverFactoryProvider {

    public Map<String, VisualItemValueResolverFactory> factories = new HashMap<String, VisualItemValueResolverFactory>();

    @Override
    public VisualItemValueResolverFactory getFactoryById(String id) {
        assert factories.containsKey(id) : "VisualItemValueResolverFactory with id '"
                + id + "' not available";
        return factories.get(id);
    }

    @Override
    public LightweightList<VisualItemValueResolverFactory> getResolverFactories() {
        LightweightList<VisualItemValueResolverFactory> results = CollectionFactory
                .createLightweightList();
        results.addAll(factories.values());
        return results;
    }

    /**
     * This method will add in your new factory into the providers Map of
     * factories. If the new factory's ID is already contained in the map, it
     * will not be added.
     */
    public void registerFactory(VisualItemValueResolverFactory resolverFactory) {
        assert resolverFactory != null;
        assert !factories.containsKey(resolverFactory.getId()) : "VisualItemValueResolverFactory "
                + "for id '"
                + resolverFactory.getId()
                + "' is already registered";

        factories.put(resolverFactory.getId(), resolverFactory);
    }
}
