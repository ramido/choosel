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
package org.thechiselgroup.choosel.core.client.views.resolvers;

import java.util.HashMap;
import java.util.Map;

import org.thechiselgroup.choosel.core.client.util.math.AverageCalculation;
import org.thechiselgroup.choosel.core.client.util.math.MaxCalculation;
import org.thechiselgroup.choosel.core.client.util.math.MinCalculation;
import org.thechiselgroup.choosel.core.client.util.math.SumCalculation;

import com.google.inject.Inject;

public class DefaultViewItemResolverUIFactoryProvider implements
        ViewItemValueResolverUIControllerFactoryProvider {

    protected Map<String, ViewItemValueResolverUIControllerFactory> idToFactoryMap;

    @Inject
    public DefaultViewItemResolverUIFactoryProvider() {

    }

    @Override
    public void add(ViewItemValueResolverUIControllerFactory factory) {
        if (idToFactoryMap.containsKey(factory.getId())) {
            return;
        }
        idToFactoryMap.put(factory.getId(), factory);
    }

    @Override
    public ViewItemValueResolverUIControllerFactory getFactoryById(String id) {
        if (!idToFactoryMap.containsKey(id)) {
            throw new IllegalArgumentException();
        }
        return idToFactoryMap.get(id);
    }

    @Inject
    public void registerFactories() {
        idToFactoryMap = new HashMap<String, ViewItemValueResolverUIControllerFactory>();
        add(new ResourceCountResolverUIControllerFactory());
        add(new CalculationResolverUIControllerFactory(new SumCalculation()));
        add(new CalculationResolverUIControllerFactory(new AverageCalculation()));
        add(new CalculationResolverUIControllerFactory(new MinCalculation()));
        add(new CalculationResolverUIControllerFactory(new MaxCalculation()));

        add(new FixedValueViewItemResolverUIControllerFactory("Fixed-1"));
        add(new FirstResourcePropertyResolverUIControllerFactory(
                "Text-Property-Resolver"));
    }
}