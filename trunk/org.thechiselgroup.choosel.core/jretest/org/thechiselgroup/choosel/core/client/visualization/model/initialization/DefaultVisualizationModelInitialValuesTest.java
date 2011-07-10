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
package org.thechiselgroup.choosel.core.client.visualization.model.initialization;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.thechiselgroup.choosel.core.client.resources.Resource;
import org.thechiselgroup.choosel.core.client.util.DataType;
import org.thechiselgroup.choosel.core.client.util.collections.CollectionFactory;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightList;
import org.thechiselgroup.choosel.core.client.visualization.model.Slot;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItem;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItemResolutionErrorModel;
import org.thechiselgroup.choosel.core.client.visualization.model.implementation.DefaultVisualizationModel;
import org.thechiselgroup.choosel.core.client.visualization.model.implementation.DefaultVisualizationModelTestHelper;
import org.thechiselgroup.choosel.core.client.visualization.model.managed.DefaultSlotMappingInitializer;
import org.thechiselgroup.choosel.core.client.visualization.model.managed.ManagedSlotMapping;
import org.thechiselgroup.choosel.core.client.visualization.model.managed.ManagedSlotMappingConfiguration;
import org.thechiselgroup.choosel.core.client.visualization.model.managed.ManagedVisualItemValueResolverDecorator;
import org.thechiselgroup.choosel.core.client.visualization.model.managed.VisualItemValueResolverFactory;
import org.thechiselgroup.choosel.core.client.visualization.model.managed.VisualItemValueResolverFactoryProvider;
import org.thechiselgroup.choosel.core.client.visualization.resolvers.FixedValueResolver;

// TODO migrate to change default slot mapping initializer
public class DefaultVisualizationModelInitialValuesTest {

    private DefaultVisualizationModel underTest;

    private Slot textSlot;

    private Slot numberSlot;

    private DefaultVisualizationModelTestHelper helper;

    @Mock
    private VisualItemResolutionErrorModel errorModel;

    @Ignore("these tests need to be reactivated and moved, more of an integration test")
    // TODO This Behavior has changed
    // TODO this changes need to be migrated
    @Test
    public void initialSlotValueForNumberSlotIfNoNumberIsAvailable() {
        Resource resource = new Resource("test:1");
        resource.putValue("text1", "t1");
        resource.putValue("text2", "t2");

        helper.getContainedResources().add(resource);

        assertEquals(true, underTest.isConfigured(numberSlot));

        List<VisualItem> viewItems = underTest.getViewItems().toList();
        assertEquals(1, viewItems.size());
        VisualItem viewItem = viewItems.get(0);

        assertEquals(new Double(0), viewItem.getValue(numberSlot));
    }

    @Ignore("these tests need to be reactivated and moved, more of an integration test")
    // TODO This Behavior has changed
    // TODO this changes need to be migrated
    @Test
    public void initialSlotValueForTextSlot() {
        Resource resource = new Resource("test:1");
        resource.putValue("text1", "t1");
        resource.putValue("text2", "t2");

        helper.getContainedResources().add(resource);

        assertEquals(true, underTest.isConfigured(textSlot));

        List<VisualItem> viewItems = underTest.getViewItems().toList();
        assertEquals(1, viewItems.size());
        VisualItem viewItem = viewItems.get(0);

        assertEquals("t1", viewItem.getValue(textSlot));
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        // TODO use correct factories...
        ManagedSlotMapping.TESTING = true;

        textSlot = new Slot("id-1", "text-slot", DataType.TEXT);
        numberSlot = new Slot("id-2", "number-slot", DataType.NUMBER);

        helper = new DefaultVisualizationModelTestHelper();
        helper.setSlots(textSlot, numberSlot);

        underTest = helper.createTestViewModel();

        VisualItemValueResolverFactoryProvider resolverProvider = mock(VisualItemValueResolverFactoryProvider.class);
        final LightweightList<VisualItemValueResolverFactory> resolverFactories = CollectionFactory
                .createLightweightList();

        when(resolverProvider.getFactoryById(any(String.class))).thenAnswer(
                new Answer<VisualItemValueResolverFactory>() {
                    @Override
                    public VisualItemValueResolverFactory answer(
                            InvocationOnMock invocation) throws Throwable {

                        VisualItemValueResolverFactory resolverFactory = mock(VisualItemValueResolverFactory.class);
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

        DefaultSlotMappingInitializer initializer = spy(new DefaultSlotMappingInitializer(
                resolverProvider));
        initializer
                .putDefaultDataTypeValues(DataType.NUMBER,
                        new ManagedVisualItemValueResolverDecorator("Fixed-0",
                                new FixedValueResolver(new Double(0),
                                        DataType.NUMBER)));
        {

            VisualItemValueResolverFactory resolverFactory = mock(VisualItemValueResolverFactory.class);
            when(
                    resolverFactory.canCreateApplicableResolver(
                            any(Slot.class), any(LightweightList.class)))
                    .thenReturn(true);
            when(resolverFactory.getId()).thenReturn("Fixed-0");

            resolverFactories.add(resolverFactory);
        }

        when(resolverProvider.getResolverFactories()).thenReturn(
                resolverFactories);

        new ManagedSlotMappingConfiguration(resolverProvider, initializer,
                underTest, errorModel);
    }

    @After
    public void tearDown() {
        ManagedSlotMapping.TESTING = false;
    }
}