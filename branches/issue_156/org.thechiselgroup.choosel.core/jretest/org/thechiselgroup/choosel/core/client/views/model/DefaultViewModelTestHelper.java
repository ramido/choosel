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
package org.thechiselgroup.choosel.core.client.views.model;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.thechiselgroup.choosel.core.client.test.ResourcesTestHelper.emptyLightweightCollection;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.thechiselgroup.choosel.core.client.resources.DataType;
import org.thechiselgroup.choosel.core.client.resources.DefaultResourceSet;
import org.thechiselgroup.choosel.core.client.resources.DefaultResourceSetFactory;
import org.thechiselgroup.choosel.core.client.resources.Resource;
import org.thechiselgroup.choosel.core.client.resources.ResourceByUriTypeCategorizer;
import org.thechiselgroup.choosel.core.client.resources.ResourceCategorizerToMultiCategorizerAdapter;
import org.thechiselgroup.choosel.core.client.resources.ResourceGrouping;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.resources.ResourceSetChangedEventHandler;
import org.thechiselgroup.choosel.core.client.util.collections.CollectionFactory;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightList;
import org.thechiselgroup.choosel.core.client.util.collections.NullIterator;
import org.thechiselgroup.choosel.core.client.views.resolvers.FixedValueResolver;
import org.thechiselgroup.choosel.core.client.views.resolvers.ViewItemValueResolverFactory;
import org.thechiselgroup.choosel.core.client.views.resolvers.ViewItemValueResolverFactoryProvider;

import com.google.gwt.event.shared.HandlerRegistration;

public final class DefaultViewModelTestHelper {

    public static LightweightCollection<ViewItem> captureAddedViewItems(
            ViewContentDisplay contentDisplay) {

        return captureAddedViewItems(contentDisplay, 1).get(0);
    }

    public static List<LightweightCollection<ViewItem>> captureAddedViewItems(
            ViewContentDisplay contentDisplay, int wantedNumberOfInvocation) {

        ArgumentCaptor<LightweightCollection> captor = ArgumentCaptor
                .forClass(LightweightCollection.class);
        verify(contentDisplay, times(wantedNumberOfInvocation)).update(
                captor.capture(), emptyLightweightCollection(ViewItem.class),
                emptyLightweightCollection(ViewItem.class),
                emptyLightweightCollection(Slot.class));

        return cast(captor.getAllValues());
    }

    public static List<ViewItem> captureAddedViewItemsAsList(
            ViewContentDisplay contentDisplay) {

        return captureAddedViewItems(contentDisplay).toList();
    }

    public static LightweightCollection<ViewItem> captureUpdatedViewItems(
            ViewContentDisplay contentDisplay) {

        ArgumentCaptor<LightweightCollection> captor = ArgumentCaptor
                .forClass(LightweightCollection.class);
        verify(contentDisplay, times(1)).update(
                emptyLightweightCollection(ViewItem.class), captor.capture(),
                emptyLightweightCollection(ViewItem.class),
                emptyLightweightCollection(Slot.class));

        return captor.getValue();
    }

    /**
     * convert to LightWeightCollection<ViewItem>
     */
    private static List<LightweightCollection<ViewItem>> cast(
            List<LightweightCollection> allValues) {

        List<LightweightCollection<ViewItem>> result = new ArrayList<LightweightCollection<ViewItem>>();
        for (LightweightCollection<ViewItem> lightweightCollection : allValues) {
            result.add(lightweightCollection);
        }
        return result;
    }

    public static DefaultViewModel createTestViewModel(
            ResourceSet containedResources, ResourceSet highlightedResources,
            ResourceSet selectedResources, boolean useDefaultFactories,
            Slot... slots) {

        ViewContentDisplay contentDisplay = mock(ViewContentDisplay.class);

        return createTestViewModel(containedResources, highlightedResources,
                selectedResources, contentDisplay, useDefaultFactories, slots);
    }

    public static DefaultViewModel createTestViewModel(
            ResourceSet containedResources, ResourceSet highlightedResources,
            ResourceSet selectedResources, ViewContentDisplay contentDisplay,
            boolean useDefaultFactories, Slot... slots) {

        ViewItemValueResolverFactoryProvider resolverProvider = mock(ViewItemValueResolverFactoryProvider.class);
        final LightweightList<ViewItemValueResolverFactory> resolverFactories = CollectionFactory
                .createLightweightList();

        when(resolverProvider.getFactoryById(any(String.class))).thenAnswer(
                new Answer<ViewItemValueResolverFactory>() {
                    @Override
                    public ViewItemValueResolverFactory answer(
                            InvocationOnMock invocation) throws Throwable {

                        ViewItemValueResolverFactory resolverFactory = mock(ViewItemValueResolverFactory.class);
                        when(
                                resolverFactory.canCreateApplicableResolver(
                                        any(Slot.class),
                                        any(LightweightList.class)))
                                .thenReturn(true);
                        when(resolverFactory.getId()).thenReturn(
                                (String) invocation.getArguments()[0]);

                        resolverFactories.add(resolverFactory);

                        return resolverFactory;
                    }
                });
        when(resolverProvider.getResolverFactories()).thenReturn(
                resolverFactories);

        // XXX needs corresponding factories
        // TODO change once relevant tests are migrated
        DefaultSlotMappingInitializer initializer = spy(new DefaultSlotMappingInitializer());
        initializer.putDefaultDataTypeValues(DataType.NUMBER,
                new FixedValueResolver(new Double(0), "Fixed-0",
                        DataType.NUMBER));
        {

            ViewItemValueResolverFactory resolverFactory = mock(ViewItemValueResolverFactory.class);
            when(
                    resolverFactory.canCreateApplicableResolver(
                            any(Slot.class), any(LightweightList.class)))
                    .thenReturn(true);
            when(resolverFactory.getId()).thenReturn("Fixed-0");

            resolverFactories.add(resolverFactory);
        }
        if (useDefaultFactories) {
            initializer.putDefaultDataTypeValues(DataType.TEXT,
                    new FixedValueResolver("", "Fixed-EmptyString",
                            DataType.TEXT));
            {

                ViewItemValueResolverFactory resolverFactory = mock(ViewItemValueResolverFactory.class);
                when(
                        resolverFactory.canCreateApplicableResolver(
                                any(Slot.class), any(LightweightList.class)))
                        .thenReturn(true);
                when(resolverFactory.getId()).thenReturn("Fixed-EmptyString");

                resolverFactories.add(resolverFactory);
            }
        }

        SlotMappingConfigurationUIModel configurationUIModel = new SlotMappingConfigurationUIModel(
                resolverProvider, initializer);
        SlotMappingConfiguration resourceSetToValueResolver = spy(new SlotMappingConfiguration(
                slots));

        when(contentDisplay.getSlots()).thenReturn(slots);
        when(contentDisplay.isReady()).thenReturn(true);

        ResourceGrouping resourceGrouping = new ResourceGrouping(
                new ResourceCategorizerToMultiCategorizerAdapter(
                        new ResourceByUriTypeCategorizer()),
                new DefaultResourceSetFactory());

        resourceGrouping.setResourceSet(containedResources);

        DefaultViewModel underTest = spy(new DefaultViewModel(contentDisplay,
                resourceSetToValueResolver, selectedResources,
                highlightedResources, mock(ViewItemBehavior.class),
                resourceGrouping, mock(Logger.class),
                configurationUIModel));

        // deactivate slot initialization

        return underTest;
    }

    public static DefaultViewModel createTestViewModel(Slot... slots) {
        ResourceSet containedResources = new DefaultResourceSet();
        ResourceSet highlightedResources = new DefaultResourceSet();
        ResourceSet selectedResources = new DefaultResourceSet();

        return createTestViewModel(containedResources, highlightedResources,
                selectedResources, true, slots);
    }

    public static void stubHandlerRegistration(ResourceSet mockedResources,
            HandlerRegistration handlerRegistrationToReturn) {

        when(mockedResources.iterator()).thenReturn(
                NullIterator.<Resource> nullIterator());

        when(
                mockedResources
                        .addEventHandler(any(ResourceSetChangedEventHandler.class)))
                .thenReturn(handlerRegistrationToReturn);
    }

    private DefaultViewModelTestHelper() {

    }

}
