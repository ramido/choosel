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

import static org.thechiselgroup.choosel.core.client.views.resolvers.PreconfiguredViewItemValueResolverFactoryProvider.AVERAGE_RESOLVER_FACTORY_ID;
import static org.thechiselgroup.choosel.core.client.views.resolvers.PreconfiguredViewItemValueResolverFactoryProvider.FIXED_0_RESOLVER_FACTORY_ID;
import static org.thechiselgroup.choosel.core.client.views.resolvers.PreconfiguredViewItemValueResolverFactoryProvider.FIXED_1_RESOLVER_FACTORY_ID;
import static org.thechiselgroup.choosel.core.client.views.resolvers.PreconfiguredViewItemValueResolverFactoryProvider.FIXED_DATE_TODAY_FACTORY_ID;
import static org.thechiselgroup.choosel.core.client.views.resolvers.PreconfiguredViewItemValueResolverFactoryProvider.FIXED_STDBLUE_RESOLVER_FACTORY_ID;
import static org.thechiselgroup.choosel.core.client.views.resolvers.PreconfiguredViewItemValueResolverFactoryProvider.MAX_RESOLVER_FACTORY_ID;
import static org.thechiselgroup.choosel.core.client.views.resolvers.PreconfiguredViewItemValueResolverFactoryProvider.MIN_RESOLVER_FACTORY_ID;
import static org.thechiselgroup.choosel.core.client.views.resolvers.PreconfiguredViewItemValueResolverFactoryProvider.SUM_RESOLVER_FACTORY_ID;
import static org.thechiselgroup.choosel.core.client.views.resolvers.PreconfiguredViewItemValueResolverFactoryProvider.TEXT_PROPERTY_RESOLVER_FACTORY_ID;

import java.util.HashMap;

import com.google.inject.Inject;

public class PreconfiguredViewItemResolverUIFactoryProvider extends
        DefaultViewItemResolverUIFactoryProvider {

    @Inject
    public void registerFactories() {
        idToFactoryMap = new HashMap<String, ViewItemValueResolverUIControllerFactory>();

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
                PreconfiguredViewItemValueResolverFactoryProvider.FIXED_EMPTY_STRING_FACTORY_ID));
    }
}
