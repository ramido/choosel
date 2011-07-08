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

import static org.thechiselgroup.choosel.core.client.views.resolvers.PreconfiguredViewItemValueResolverFactoryProvider.FIXED_0_RESOLVER_FACTORY_ID;
import static org.thechiselgroup.choosel.core.client.views.resolvers.PreconfiguredViewItemValueResolverFactoryProvider.FIXED_CIRCLE_RESOLVER_FACTORY_ID;
import static org.thechiselgroup.choosel.core.client.views.resolvers.PreconfiguredViewItemValueResolverFactoryProvider.FIXED_DATE_TODAY_FACTORY_ID;
import static org.thechiselgroup.choosel.core.client.views.resolvers.PreconfiguredViewItemValueResolverFactoryProvider.FIXED_EMPTY_STRING_FACTORY_ID;
import static org.thechiselgroup.choosel.core.client.views.resolvers.PreconfiguredViewItemValueResolverFactoryProvider.FIXED_STDBLUE_RESOLVER_FACTORY_ID;

import org.thechiselgroup.choosel.core.client.resources.DataType;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightList;
import org.thechiselgroup.choosel.core.client.views.ViewPart;
import org.thechiselgroup.choosel.core.client.views.model.DefaultSlotMappingInitializer;
import org.thechiselgroup.choosel.core.client.views.model.SlotMappingInitializer;
import org.thechiselgroup.choosel.core.client.views.resolvers.FixedValueViewItemResolverFactory;
import org.thechiselgroup.choosel.core.client.views.resolvers.ViewItemValueResolverFactoryProvider;
import org.thechiselgroup.choosel.workbench.client.ui.configuration.ViewWindowContentProducer;
import org.thechiselgroup.choosel.workbench.client.workspace.ShareConfigurationFactory;
import org.thechiselgroup.choosel.workbench.client.workspace.ShareConfigurationViewPart;

import com.google.inject.Inject;

public class ChooselWorkbenchViewWindowContentProducer extends
        ViewWindowContentProducer {

    @Inject
    private ShareConfigurationFactory shareConfigurationFactory;

    @Inject
    private ViewItemValueResolverFactoryProvider factoryProvider;

    /**
     * XXX This class relies on the factoryProvider that is defined in the
     * Choosel core package. I'm not sure if this is good or not.
     */
    @Override
    protected SlotMappingInitializer createSlotMappingInitializer(
            String contentType) {

        DefaultSlotMappingInitializer initializer = new DefaultSlotMappingInitializer(
                factoryProvider);

        initializer.putDefaultDataTypeValues(DataType.NUMBER,
                ((FixedValueViewItemResolverFactory) factoryProvider
                        .getFactoryById(FIXED_0_RESOLVER_FACTORY_ID)).create());

        initializer.putDefaultDataTypeValues(DataType.TEXT,
                ((FixedValueViewItemResolverFactory) factoryProvider
                        .getFactoryById(FIXED_EMPTY_STRING_FACTORY_ID))
                        .create());

        initializer.putDefaultDataTypeValues(DataType.SHAPE,
                ((FixedValueViewItemResolverFactory) factoryProvider
                        .getFactoryById(FIXED_CIRCLE_RESOLVER_FACTORY_ID))
                        .create());
        // TODO
        // PVShape.CIRCLE
        initializer.putDefaultDataTypeValues(DataType.COLOR,
                ((FixedValueViewItemResolverFactory) factoryProvider
                        .getFactoryById(FIXED_STDBLUE_RESOLVER_FACTORY_ID))
                        .create());

        initializer.putDefaultDataTypeValues(DataType.DATE,
                ((FixedValueViewItemResolverFactory) factoryProvider
                        .getFactoryById(FIXED_DATE_TODAY_FACTORY_ID)).create());

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
