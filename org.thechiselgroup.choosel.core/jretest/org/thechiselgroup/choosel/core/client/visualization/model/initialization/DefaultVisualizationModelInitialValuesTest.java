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
package org.thechiselgroup.choosel.core.client.visualization.model.initialization;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.core.client.resources.Resource;
import org.thechiselgroup.choosel.core.client.util.DataType;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightList;
import org.thechiselgroup.choosel.core.client.visualization.model.Slot;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItem;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItemResolutionErrorModel;
import org.thechiselgroup.choosel.core.client.visualization.model.implementation.DefaultVisualizationModel;
import org.thechiselgroup.choosel.core.client.visualization.model.implementation.DefaultVisualizationModelTestHelper;
import org.thechiselgroup.choosel.core.client.visualization.model.managed.DefaultSlotMappingInitializer;
import org.thechiselgroup.choosel.core.client.visualization.model.managed.DefaultVisualItemResolverFactoryProvider;
import org.thechiselgroup.choosel.core.client.visualization.model.managed.ManagedSlotMappingConfiguration;
import org.thechiselgroup.choosel.core.client.visualization.model.managed.ManagedVisualItemValueResolverDecorator;
import org.thechiselgroup.choosel.core.client.visualization.resolvers.FixedValueResolver;
import org.thechiselgroup.choosel.core.client.visualization.resolvers.managed.FixedVisualItemResolverFactory;
import org.thechiselgroup.choosel.core.client.visualization.resolvers.managed.PropertyDependantVisualItemValueResolverFactory;

// TODO migrate to change default slot mapping initializer
public class DefaultVisualizationModelInitialValuesTest {

    private DefaultVisualizationModel underTest;

    private DefaultVisualizationModelTestHelper helper;

    @Mock
    private VisualItemResolutionErrorModel errorModel;

    private Slot[] slots;

    private DefaultSlotMappingInitializer initializer;

    private DefaultVisualItemResolverFactoryProvider resolverProvider;

    private Date datePropertyReturnValue;

    private Double numberPropertyReturnValue;

    private String textPropertyReturnValue;

    private Double fixedNumberReturnValue;

    @Test
    public void initialSlotValueForDateSlot() {
        Resource resource = new Resource("test:1");
        resource.putValue("date1", new Date(100, 1, 1, 0, 0, 0));

        helper.getContainedResources().add(resource);

        assertEquals(true, underTest.isConfigured(slots[2]));

        List<VisualItem> viewItems = underTest.getFullVisualItemContainer()
                .getVisualItems().toList();
        assertEquals(1, viewItems.size());
        VisualItem viewItem = viewItems.get(0);

        assertEquals(datePropertyReturnValue, viewItem.getValue(slots[2]));
    }

    @Test
    public void initialSlotValueForNumberSlotIfNoNumberIsAvailableInData() {
        Resource resource = new Resource("test:1");
        resource.putValue("text1", "xt1");
        resource.putValue("text2", "xt2");

        helper.getContainedResources().add(resource);

        assertEquals(true, underTest.isConfigured(slots[1]));

        List<VisualItem> viewItems = underTest.getFullVisualItemContainer()
                .getVisualItems().toList();
        assertEquals(1, viewItems.size());
        VisualItem viewItem = viewItems.get(0);

        assertEquals(fixedNumberReturnValue, viewItem.getValue(slots[1]));
    }

    @Test
    public void initialSlotValueForTextSlot() {
        Resource resource = new Resource("test:1");
        resource.putValue("text1", "t1x");
        resource.putValue("text2", "t2x");

        helper.getContainedResources().add(resource);

        assertEquals(true, underTest.isConfigured(slots[0]));

        List<VisualItem> viewItems = underTest.getFullVisualItemContainer()
                .getVisualItems().toList();
        assertEquals(1, viewItems.size());
        VisualItem viewItem = viewItems.get(0);

        assertEquals(textPropertyReturnValue, viewItem.getValue(slots[0]));
    }

    @SuppressWarnings("unchecked")
    private void registerDefaultResolverFactory(DataType dataType, String id,
            Object value) {

        ManagedVisualItemValueResolverDecorator resolver = new ManagedVisualItemValueResolverDecorator(
                id, new FixedValueResolver(value, dataType));

        FixedVisualItemResolverFactory resolverFactory = mock(FixedVisualItemResolverFactory.class);
        when(resolverFactory.getId()).thenReturn(id);
        when(
                resolverFactory.canCreateApplicableResolver(any(Slot.class),
                        any(LightweightList.class))).thenReturn(true);
        when(resolverFactory.create()).thenReturn(resolver);

        resolverProvider.registerFactory(resolverFactory);

        initializer.setFixedDataTypeResolverId(dataType, id);
    }

    @SuppressWarnings("unchecked")
    private void registerPropertyResolver(DataType dataType, String id,
            Object value) {

        ManagedVisualItemValueResolverDecorator resolver = new ManagedVisualItemValueResolverDecorator(
                id, new FixedValueResolver(value, dataType));

        PropertyDependantVisualItemValueResolverFactory resolverFactory = mock(PropertyDependantVisualItemValueResolverFactory.class);
        when(resolverFactory.getId()).thenReturn(id);
        when(
                resolverFactory.canCreateApplicableResolver(any(Slot.class),
                        any(LightweightList.class))).thenReturn(true);
        when(resolverFactory.create(any(String.class))).thenReturn(resolver);

        resolverProvider.registerFactory(resolverFactory);

        initializer.setPropertyDataTypeResolverId(dataType, id);
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        numberPropertyReturnValue = new Double(0);
        textPropertyReturnValue = "t1";
        datePropertyReturnValue = new Date(100, 1, 1, 0, 0, 1);

        helper = new DefaultVisualizationModelTestHelper();
        slots = helper.createSlots(DataType.TEXT, DataType.NUMBER,
                DataType.DATE, DataType.LOCATION);

        underTest = helper.createTestVisualizationModel();

        resolverProvider = new DefaultVisualItemResolverFactoryProvider();

        initializer = spy(new DefaultSlotMappingInitializer(resolverProvider));

        registerPropertyResolver(DataType.NUMBER, "property-number",
                numberPropertyReturnValue);
        registerPropertyResolver(DataType.TEXT, "property-text",
                textPropertyReturnValue);
        registerPropertyResolver(DataType.DATE, "property-date",
                datePropertyReturnValue);

        fixedNumberReturnValue = new Double(1);

        registerDefaultResolverFactory(DataType.NUMBER, "fixed-number",
                fixedNumberReturnValue);
        registerDefaultResolverFactory(DataType.TEXT, "fixed-text", "fixed");
        registerDefaultResolverFactory(DataType.DATE, "fixed-date", new Date());
        registerDefaultResolverFactory(DataType.LOCATION, "fixed-location",
                new Resource("location:0"));

        new ManagedSlotMappingConfiguration(resolverProvider, initializer,
                underTest, errorModel);
    }
}