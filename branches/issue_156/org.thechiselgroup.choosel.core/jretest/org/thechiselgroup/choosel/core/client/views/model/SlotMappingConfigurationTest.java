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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.thechiselgroup.choosel.core.client.test.HamcrestResourceMatchers.equalsArray;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.core.client.persistence.Memento;
import org.thechiselgroup.choosel.core.client.resources.DataType;
import org.thechiselgroup.choosel.core.client.resources.persistence.ResourceSetCollector;
import org.thechiselgroup.choosel.core.client.util.collections.CollectionFactory;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightList;
import org.thechiselgroup.choosel.core.client.views.resolvers.DelegatingViewItemValueResolver;
import org.thechiselgroup.choosel.core.client.views.resolvers.ViewItemValueResolver;
import org.thechiselgroup.choosel.core.client.views.resolvers.ViewItemValueResolverFactory;
import org.thechiselgroup.choosel.core.client.views.resolvers.ViewItemValueResolverFactoryProvider;

public class SlotMappingConfigurationTest {

    private SlotMappingConfiguration underTest;

    private Slot slot1;

    private Slot slot2;

    @Mock
    private ViewItemValueResolverFactoryProvider resolverProvider;

    @Mock
    private SlotMappingInitializer slotMappingInitializer;

    private LightweightList<ViewItemValueResolverFactory> resolverFactories;

    @Test
    public void containsFixedSlot() {
        Map<Slot, ViewItemValueResolver> fixedSlotResolvers = new HashMap<Slot, ViewItemValueResolver>();
        fixedSlotResolvers.put(slot1, mock(ViewItemValueResolver.class));

        underTest = new SlotMappingConfiguration(fixedSlotResolvers,
                new Slot[] { slot1 }, resolverProvider, slotMappingInitializer);

        assertThat(underTest.containsResolver(slot1), is(true));
    }

    @Test
    public void doesNotSaveFixedMappingsToMemento() {
        Map<Slot, ViewItemValueResolver> fixedSlotResolvers = new HashMap<Slot, ViewItemValueResolver>();
        fixedSlotResolvers.put(slot1, mock(ViewItemValueResolver.class));

        underTest = new SlotMappingConfiguration(fixedSlotResolvers,
                new Slot[] { slot1 }, resolverProvider, slotMappingInitializer);

        Memento result = underTest.save(mock(ResourceSetCollector.class));

        assertEquals(0, result.getChildren().size());
    }

    // XXX implementation might be broken!
    @SuppressWarnings("unchecked")
    @Test
    public void fireChangesForDelegatingSlotResolversWhenTargetResolverIsChanged() {
        String resolverId = "id";

        ViewItemValueResolverFactory resolverFactory = mock(ViewItemValueResolverFactory.class);
        when(resolverFactory.getId()).thenReturn(resolverId);
        when(
                resolverFactory.canCreateApplicableResolver(any(Slot.class),
                        any(LightweightList.class))).thenReturn(true);

        resolverFactories.add(resolverFactory);
        when(resolverProvider.getFactoryById(resolverId)).thenReturn(resolverFactory);

        underTest.initSlots(new Slot[] { slot1, slot2 }, null, null);

        DelegatingViewItemValueResolver delegatingResolver = mock(DelegatingViewItemValueResolver.class);
        when(delegatingResolver.getTargetSlot()).thenReturn(slot1);
        when(delegatingResolver.getResolverId()).thenReturn(resolverId);
        when(
                delegatingResolver.canResolve(any(Slot.class),
                        any(LightweightList.class),
                        any(ViewItemValueResolverContext.class))).thenReturn(
                true);

        ViewItemValueResolver resolver = mock(ViewItemValueResolver.class);
        when(resolver.getResolverId()).thenReturn(resolverId);
        when(
                resolver.canResolve(any(Slot.class),
                        any(LightweightList.class),
                        any(ViewItemValueResolverContext.class))).thenReturn(
                true);

        underTest.setResolver(slot1, resolver);
        underTest.setResolver(slot2, delegatingResolver);

        SlotMappingChangedHandler handler = mock(SlotMappingChangedHandler.class);

        underTest.addHandler(handler);

        ViewItemValueResolver resolver2 = mock(ViewItemValueResolver.class);
        when(resolver2.getResolverId()).thenReturn(resolverId);
        when(
                resolver2.canResolve(any(Slot.class),
                        any(LightweightList.class),
                        any(ViewItemValueResolverContext.class))).thenReturn(
                true);

        // XXX shouldnt this be changing slot 2, and if not, shouldn't we be
        // checking slot 1 below??????
        underTest.setResolver(slot1, resolver2);

        verify(handler, times(1)).onResourceCategoriesChanged(
                argThat(new IsChangeForSlotMatcher(slot2)));
    }

    @Test
    public void getRequiredSlotsExcludesFixedSlots() {
        Map<Slot, ViewItemValueResolver> fixedSlotResolvers = new HashMap<Slot, ViewItemValueResolver>();
        fixedSlotResolvers.put(slot1, mock(ViewItemValueResolver.class));

        underTest = new SlotMappingConfiguration(fixedSlotResolvers,
                new Slot[] { slot1, slot2 }, resolverProvider,
                slotMappingInitializer);

        assertThat(underTest.getRequiredSlots(), equalsArray(slot2));
    }

    @Test
    public void resolveFixedSlot() {
        Map<Slot, ViewItemValueResolver> fixedSlotResolvers = new HashMap<Slot, ViewItemValueResolver>();
        ViewItemValueResolver fixedResolver = mock(ViewItemValueResolver.class);
        fixedSlotResolvers.put(slot1, fixedResolver);

        underTest = new SlotMappingConfiguration(fixedSlotResolvers,
                new Slot[] { slot1 }, resolverProvider, slotMappingInitializer);

        ViewItem viewItem = mock(ViewItem.class);

        underTest.resolve(slot1, viewItem);

        verify(fixedResolver, times(1)).resolve(eq(viewItem),
                any(ViewItemValueResolverContext.class));
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        slot1 = new Slot("s1", "", DataType.NUMBER);
        slot2 = new Slot("s2", "", DataType.NUMBER);

        resolverFactories = CollectionFactory
                .<ViewItemValueResolverFactory> createLightweightList();
        when(resolverProvider.getResolverFactories()).thenReturn(
                resolverFactories);
        // TODO
        // when(resolverProvider.getFactoryById(id)).thenReturn(correctFactory);

        underTest = new SlotMappingConfiguration(new Slot[] { slot1, slot2 },
                resolverProvider, slotMappingInitializer);

        underTest.initSlots(new Slot[] { slot1, slot2 }, null, null);
    }
}