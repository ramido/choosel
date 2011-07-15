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

import static org.thechiselgroup.choosel.core.client.visualization.resolvers.managed.PreconfiguredVisualItemValueResolverFactoryProvider.*;

import java.util.HashMap;

import org.thechiselgroup.choosel.core.client.visualization.resolvers.managed.PreconfiguredVisualItemValueResolverFactoryProvider;

import com.google.inject.Inject;

public class PreconfiguredViewItemResolverUIFactoryProvider extends
        DefaultVisualItemResolverUIFactoryProvider {

    @Inject
    public void registerFactories() {
        idToFactoryMap = new HashMap<String, ViewItemValueResolverUIControllerFactory>();

        add(new ViewItemStatusIdUIControllerFactory());

        add(new ResourceCountResolverUIControllerFactory());
        add(new CalculationResolverUIControllerFactory(SUM_RESOLVER_FACTORY_ID));
        add(new CalculationResolverUIControllerFactory(
                AVERAGE_RESOLVER_FACTORY_ID));
        add(new CalculationResolverUIControllerFactory(MAX_RESOLVER_FACTORY_ID));
        add(new CalculationResolverUIControllerFactory(MIN_RESOLVER_FACTORY_ID));

        add(new FixedValueViewItemResolverUIControllerFactory(
                FIXED_1_RESOLVER_FACTORY_ID));
        add(new FirstResourcePropertyResolverUIControllerFactory(
                TEXT_PROPERTY_RESOLVER_FACTORY_ID));

        // registering factories for ChooselWorkbecnchViewWindowContentProducers
        add(new FixedValueViewItemResolverUIControllerFactory(
                FIXED_0_RESOLVER_FACTORY_ID));
        add(new FixedValueViewItemResolverUIControllerFactory("circle"));
        add(new FixedValueViewItemResolverUIControllerFactory(
                FIXED_STDBLUE_RESOLVER_FACTORY_ID));
        add(new FixedValueViewItemResolverUIControllerFactory(
                FIXED_DATE_TODAY_FACTORY_ID));
        add(new FixedValueViewItemResolverUIControllerFactory(
                PreconfiguredVisualItemValueResolverFactoryProvider.FIXED_EMPTY_STRING_FACTORY_ID));
    }
}
