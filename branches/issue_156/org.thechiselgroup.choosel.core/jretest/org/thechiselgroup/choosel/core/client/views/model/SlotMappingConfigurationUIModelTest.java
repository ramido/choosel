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

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.thechiselgroup.choosel.core.client.views.model.ViewItemValueResolverTestUtils.createSlots;
import static org.thechiselgroup.choosel.core.client.views.model.ViewItemValueResolverTestUtils.mockResolverThatCanAlwaysResolve;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.core.client.resources.DataType;
import org.thechiselgroup.choosel.core.client.views.resolvers.ViewItemValueResolverFactoryProvider;

/**
 * 
 * @author Lars Grammel
 * @author Patrick Gorman
 */
public class SlotMappingConfigurationUIModelTest {

    private SlotMappingConfigurationUIModel underTest;

    @Mock
    private ViewItemValueResolverFactoryProvider resolverProvider;

    @Mock
    private SlotMappingInitializer slotMappingInitializer;

    @Mock
    private ViewModel viewModel;

    private Slot[] slots;

    private SlotMappingChangedHandler captureSlotMappingChangedHandler() {
        ArgumentCaptor<SlotMappingChangedHandler> captor = ArgumentCaptor
                .forClass(SlotMappingChangedHandler.class);
        verify(viewModel, times(1)).addHandler(captor.capture());
        return captor.getValue();
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        underTest = new SlotMappingConfigurationUIModel(resolverProvider,
                slotMappingInitializer, viewModel);

        slots = createSlots(DataType.TEXT);
    }

    // TODO mock view model and fire events
    @Test
    public void test01() {
        SlotMappingChangedHandler handler = captureSlotMappingChangedHandler();

        handler.onSlotMappingChanged(new SlotMappingChangedEvent(slots[0],
                mockResolverThatCanAlwaysResolve()));

        // set current resolver with no UI sets to no UI
    }
}
