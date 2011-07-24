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
package org.thechiselgroup.choosel.core.client.visualization.resolvers.ui;

import org.thechiselgroup.choosel.core.client.util.collections.IdentifiableSet;
import org.thechiselgroup.choosel.core.client.visualization.resolvers.managed.PropertyDependantVisualItemValueResolverFactory;
import org.thechiselgroup.choosel.core.client.visualization.resolvers.managed.SingletonVisualItemResolverFactory;

public class DefaultVisualItemResolverUIFactoryProvider implements
        VisualItemValueResolverUIControllerFactoryProvider {

    private IdentifiableSet<VisualItemValueResolverUIControllerFactory> factories = new IdentifiableSet<VisualItemValueResolverUIControllerFactory>();

    @Override
    public VisualItemValueResolverUIControllerFactory getFactoryById(String id) {
        assert factories.contains(id) : "VisualItemValueResolverUIControllerFactory"
                + " with id '"
                + id
                + "' not available (registered: "
                + factories + ")";
        return factories.get(id);
    }

    protected void register(PropertyDependantVisualItemValueResolverFactory resolverFactory) {
        register(new PropertyListBoxResolverUIControllerFactory(resolverFactory));
    }

    public void register(SingletonVisualItemResolverFactory resolverFactory) {
        register(new EmptyWidgetResolverUIControllerFactory(resolverFactory));
    }

    @Override
    public void register(VisualItemValueResolverUIControllerFactory factory) {
        assert factory != null;
        assert !factories.contains(factory.getId()) : "VisualItemValueResolverUIControllerFactory"
                + " for id " + factory.getId() + " is already registered";
        factories.put(factory);
    }

}
