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

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.thechiselgroup.choosel.core.client.resources.DataType;
import org.thechiselgroup.choosel.core.client.views.resolvers.DelegatingViewItemValueResolver;
import org.thechiselgroup.choosel.core.client.views.resolvers.ViewItemValueResolver;

public class SlotMappingConfigurationTest {

    private SlotMappingConfiguration underTest;

    @Test
    public void fireChangesForDelegatingSlotResolversWhenTargetResolverIsChanged() {
        Slot slot1 = new Slot("s1", "", DataType.NUMBER);
        final Slot slot2 = new Slot("s2", "", DataType.NUMBER);

        DelegatingViewItemValueResolver delegatingResolver = mock(DelegatingViewItemValueResolver.class);

        when(delegatingResolver.getTargetSlot()).thenReturn(slot1);

        underTest.setResolver(slot1, mock(ViewItemValueResolver.class));
        underTest.setResolver(slot2, delegatingResolver);

        SlotMappingChangedHandler handler = mock(SlotMappingChangedHandler.class);

        underTest.addHandler(handler);

        underTest.setResolver(slot1, mock(ViewItemValueResolver.class));

        verify(handler, times(1)).onResourceCategoriesChanged(
                argThat(new IsChangeForSlotMatcher(slot2)));
    }

    @Before
    public void setUp() {
        underTest = new SlotMappingConfiguration();
    }

}