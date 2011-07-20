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

import java.util.Map;

import org.thechiselgroup.choosel.core.client.util.collections.CollectionFactory;

public class DefaultVisualItemResolverUIFactoryProvider implements
        VisualItemValueResolverUIControllerFactoryProvider {

    private Map<String, VisualItemValueResolverUIControllerFactory> idToFactoryMap = CollectionFactory
            .createStringMap();

    @Override
    public VisualItemValueResolverUIControllerFactory getFactoryById(String id) {
        assert idToFactoryMap.containsKey(id) : "VisualItemValueResolverUIControllerFactory"
                + " with id '"
                + id
                + "' not available (registered: "
                + idToFactoryMap.keySet() + ")";
        return idToFactoryMap.get(id);
    }

    @Override
    public void register(VisualItemValueResolverUIControllerFactory factory) {
        assert factory != null;
        assert !idToFactoryMap.containsKey(factory.getId()) : "VisualItemValueResolverUIControllerFactory"
                + " for id " + factory.getId() + " is already registered";
        idToFactoryMap.put(factory.getId(), factory);
    }

}
