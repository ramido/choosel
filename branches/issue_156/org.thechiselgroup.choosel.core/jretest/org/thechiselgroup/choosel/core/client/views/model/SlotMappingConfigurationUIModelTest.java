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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.thechiselgroup.choosel.core.client.test.HamcrestResourceMatchers.containsExactly;
import static org.thechiselgroup.choosel.core.client.views.model.ViewItemValueResolverTestUtils.createSlots;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.core.client.resources.DataType;
import org.thechiselgroup.choosel.core.client.util.collections.CollectionFactory;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightList;
import org.thechiselgroup.choosel.core.client.views.resolvers.ManagedViewItemValueResolver;
import org.thechiselgroup.choosel.core.client.views.resolvers.SlotMappingUIModel;
import org.thechiselgroup.choosel.core.client.views.resolvers.ViewItemValueResolver;
import org.thechiselgroup.choosel.core.client.views.resolvers.ViewItemValueResolverFactory;
import org.thechiselgroup.choosel.core.client.views.resolvers.ViewItemValueResolverFactoryProvider;

/**
 * 
 * @author Lars Grammel
 * @author Patrick Gorman
 */

// SlotMappingChangedHandler handler =
// captureSlotMappingChangedHandler();

// handler.onSlotMappingChanged(new SlotMappingChangedEvent(slots[0],
// mockResolverThatCanAlwaysResolve()));

// TODO change the state of ViewModel and see that underTest changes too
public class SlotMappingConfigurationUIModelTest {

    private static final String RESOLVER_ID_1 = "resolver-id-1";

    private static final String RESOLVER_ID_2 = "resolver-id-2";

    public static Matcher<LightweightCollection<SlotMappingUIModel>> uiModelsContainSlots(
            final Slot... slots) {

        return new TypeSafeMatcher<LightweightCollection<SlotMappingUIModel>>() {
            @Override
            public void describeTo(Description description) {
                for (Slot slot : slots) {
                    description.appendValue(slot);
                }
            }

            @Override
            public boolean matchesSafely(
                    LightweightCollection<SlotMappingUIModel> uiModels) {
                if (uiModels.size() != slots.length) {
                    return false;
                }

                for (Slot slot : slots) {
                    boolean found = false;
                    for (SlotMappingUIModel uiModel : uiModels) {
                        if (uiModel.getSlot().equals(slot)) {
                            found = true;
                        }
                    }

                    if (!found) {
                        return false;
                    }
                }
                return true;
            }
        };
    }

    private SlotMappingConfigurationUIModel underTest;

    @Mock
    private ViewItemValueResolverFactoryProvider resolverProvider;

    @Mock
    private SlotMappingInitializer slotMappingInitializer;

    @Mock
    private ViewModel viewModel;

    private Slot[] slots;

    @Mock
    private ViewItemValueResolverFactory factory1;

    @Mock
    private ManagedViewItemValueResolver resolver1;

    @Mock
    private ManagedViewItemValueResolver resolver2;

    private DefaultViewItemResolutionErrorModel errorModel;

    private SlotMappingChangedHandler captureSlotMappingChangedHandler() {
        ArgumentCaptor<SlotMappingChangedHandler> captor = ArgumentCaptor
                .forClass(SlotMappingChangedHandler.class);
        verify(viewModel, times(1)).addHandler(captor.capture());
        return captor.getValue();
    }

    @Test
    public void configurationUIModelContainsUIModelForEachSlotInViewModel() {
        setUpSlots(DataType.TEXT, DataType.NUMBER);

        underTest = new SlotMappingConfigurationUIModel(resolverProvider,
                slotMappingInitializer, viewModel, errorModel);
        LightweightCollection<SlotMappingUIModel> uiModels = underTest
                .getSlotMappingUIModels();

        assertThat(uiModels, uiModelsContainSlots(slots));
    }

    @Test
    public void configurationUIModelContainsUIModelForOneSlotInViewModel() {
        setUpSlots(DataType.TEXT);

        underTest = new SlotMappingConfigurationUIModel(resolverProvider,
                slotMappingInitializer, viewModel, errorModel);
        LightweightList<SlotMappingUIModel> uiModels = underTest
                .getSlotMappingUIModels();

        assertThat(uiModels, uiModelsContainSlots(slots));
    }

    @Test
    public void errorInErrorModelResolverInViewModelNotApplicable() {
        setUpSlots(DataType.TEXT);
        when(viewModel.getResolver(slots[0])).thenReturn(resolver1);

        ViewItem viewItem = mock(ViewItem.class);
        when(viewItem.getViewItemID()).thenReturn("a");
        errorModel.reportError(slots[0], viewItem);

        underTest = new SlotMappingConfigurationUIModel(resolverProvider,
                slotMappingInitializer, viewModel, errorModel);

        assertThat(underTest.getSlotsWithInvalidResolvers(),
                containsExactly(slots[0]));
    }

    @Test
    public void getResolverFromViewModelContext() {
        setUpSlots(DataType.TEXT);
        when(viewModel.getResolver(slots[0])).thenReturn(resolver1);

        underTest = new SlotMappingConfigurationUIModel(resolverProvider,
                slotMappingInitializer, viewModel, errorModel);

        assertEquals(resolver1, underTest.getCurrentResolver(slots[0]));
    }

    @SuppressWarnings("unchecked")
    private void mockResolversAndFactories() {
        when(factory1.getId()).thenReturn(RESOLVER_ID_1);
        when(
                factory1.canCreateApplicableResolver(any(Slot.class),
                        any(LightweightList.class))).thenReturn(true);
        when(factory1.create(any(LightweightList.class))).thenReturn(resolver1);
        when(resolver1.getResolverId()).thenReturn(RESOLVER_ID_1);
        when(resolver2.getResolverId()).thenReturn(RESOLVER_ID_2);
    }

    @Test
    public void nonAllowableResolverInViewModelNotApplicable() {
        setUpSlots(DataType.TEXT);
        when(viewModel.getResolver(slots[0])).thenReturn(resolver2);

        underTest = new SlotMappingConfigurationUIModel(resolverProvider,
                slotMappingInitializer, viewModel, errorModel);

        assertEquals(1, underTest.getSlotsWithInvalidResolvers().size());
        assertEquals(slots[0], underTest.getSlotsWithInvalidResolvers().get(0));
    }

    @Test
    public void nonManagedResolverInViewModelNotApplicable() {
        setUpSlots(DataType.TEXT);
        ViewItemValueResolver unManagedResolver = mock(ViewItemValueResolver.class);
        when(viewModel.getResolver(slots[0])).thenReturn(unManagedResolver);

        underTest = new SlotMappingConfigurationUIModel(resolverProvider,
                slotMappingInitializer, viewModel, errorModel);

        assertThat(underTest.getSlotsWithInvalidResolvers(),
                containsExactly(slots[0]));
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        LightweightList<ViewItemValueResolverFactory> factories = CollectionFactory
                .createLightweightList();

        mockResolversAndFactories();
        factories.add(factory1);

        when(resolverProvider.getResolverFactories()).thenReturn(factories);

        errorModel = new DefaultViewItemResolutionErrorModel();
    }

    private void setUpSlots(DataType... dataTypes) {
        slots = createSlots(dataTypes);
        when(viewModel.getSlots()).thenReturn(slots);
    }

}
