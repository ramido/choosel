/*******************************************************************************
 * Copyright 2009, 2010 Lars Grammel 
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
package org.thechiselgroup.choosel.workbench.client;

import static org.thechiselgroup.choosel.core.client.visualization.resolvers.managed.PreconfiguredVisualItemValueResolverFactoryProvider.DATE_PROPERTY_RESOLVER_FACTORY_ID;
import static org.thechiselgroup.choosel.core.client.visualization.resolvers.managed.PreconfiguredVisualItemValueResolverFactoryProvider.FIXED_0_RESOLVER_FACTORY_ID;
import static org.thechiselgroup.choosel.core.client.visualization.resolvers.managed.PreconfiguredVisualItemValueResolverFactoryProvider.FIXED_DATE_TODAY_FACTORY_ID;
import static org.thechiselgroup.choosel.core.client.visualization.resolvers.managed.PreconfiguredVisualItemValueResolverFactoryProvider.FIXED_EMPTY_STRING_FACTORY_ID;
import static org.thechiselgroup.choosel.core.client.visualization.resolvers.managed.PreconfiguredVisualItemValueResolverFactoryProvider.FIXED_STDBLUE_RESOLVER_FACTORY_ID;
import static org.thechiselgroup.choosel.core.client.visualization.resolvers.managed.PreconfiguredVisualItemValueResolverFactoryProvider.LOCATION_PROPERTY_RESOLVER_FACTORY_ID;
import static org.thechiselgroup.choosel.core.client.visualization.resolvers.managed.PreconfiguredVisualItemValueResolverFactoryProvider.SUM_RESOLVER_FACTORY_ID;
import static org.thechiselgroup.choosel.core.client.visualization.resolvers.managed.PreconfiguredVisualItemValueResolverFactoryProvider.TEXT_PROPERTY_RESOLVER_FACTORY_ID;

import org.thechiselgroup.choosel.core.client.util.DataType;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightList;
import org.thechiselgroup.choosel.core.client.visualization.ViewPart;
import org.thechiselgroup.choosel.core.client.visualization.model.managed.DefaultSlotMappingInitializer;
import org.thechiselgroup.choosel.core.client.visualization.model.managed.SlotMappingInitializer;
import org.thechiselgroup.choosel.core.client.visualization.model.managed.VisualItemValueResolverFactoryProvider;
import org.thechiselgroup.choosel.workbench.client.ui.configuration.ViewWindowContentProducer;
import org.thechiselgroup.choosel.workbench.client.workspace.ShareConfigurationFactory;
import org.thechiselgroup.choosel.workbench.client.workspace.ShareConfigurationViewPart;

import com.google.inject.Inject;

public class ChooselWorkbenchViewWindowContentProducer extends
        ViewWindowContentProducer {

    @Inject
    protected VisualItemValueResolverFactoryProvider provider;

    @Inject
    private ShareConfigurationFactory shareConfigurationFactory;

    @Inject
    public VisualItemValueResolverFactoryProvider factoryProvider;

    /**
     * XXX This class relies on the factoryProvider that is defined in the
     * Choosel core package. I'm not sure if this is good or not.
     */
    @Override
    protected SlotMappingInitializer createSlotMappingInitializer(
            String contentType) {

        DefaultSlotMappingInitializer initializer = new DefaultSlotMappingInitializer(
                factoryProvider);

        initializer.setFixedDataTypeResolverId(DataType.NUMBER,
                FIXED_0_RESOLVER_FACTORY_ID);
        initializer.setFixedDataTypeResolverId(DataType.TEXT,
                FIXED_EMPTY_STRING_FACTORY_ID);
        initializer.setFixedDataTypeResolverId(DataType.COLOR,
                FIXED_STDBLUE_RESOLVER_FACTORY_ID);
        initializer.setFixedDataTypeResolverId(DataType.DATE,
                FIXED_DATE_TODAY_FACTORY_ID);

        initializer.setPropertyDataTypeResolverId(DataType.NUMBER,
                SUM_RESOLVER_FACTORY_ID);
        initializer.setPropertyDataTypeResolverId(DataType.TEXT,
                TEXT_PROPERTY_RESOLVER_FACTORY_ID);
        initializer.setPropertyDataTypeResolverId(DataType.LOCATION,
                LOCATION_PROPERTY_RESOLVER_FACTORY_ID);
        initializer.setPropertyDataTypeResolverId(DataType.DATE,
                DATE_PROPERTY_RESOLVER_FACTORY_ID);

        return initializer;
    }

    @Override
    protected LightweightList<ViewPart> createViewParts(String contentType) {
        LightweightList<ViewPart> parts = super.createViewParts(contentType);

        parts.add(new ShareConfigurationViewPart(shareConfigurationFactory
                .createShareConfiguration()));

        return parts;
    }

}
