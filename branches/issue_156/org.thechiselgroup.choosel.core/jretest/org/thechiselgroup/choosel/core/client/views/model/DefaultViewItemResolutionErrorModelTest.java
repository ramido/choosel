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
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.thechiselgroup.choosel.core.client.test.HamcrestResourceMatchers.containsExactly;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.core.client.resources.DataType;

public class DefaultViewItemResolutionErrorModelTest {

    private DefaultViewItemResolutionErrorModel underTest;

    @Mock
    private ViewItem viewItem;

    private Slot slot;

    @Test
    public void addErrorAddsSlotToErrorSlots() {
        underTest.addError(slot, viewItem);

        assertThat(underTest.getSlotsWithErrors(), containsExactly(slot));
    }

    @Test
    public void addErrorAddsSlotToViewItemErrors() {
        underTest.addError(slot, viewItem);

        assertThat(underTest.getSlotsWithErrors(viewItem),
                containsExactly(slot));
    }

    @Test
    public void addErrorAddsViewItemToErrorViewItems() {
        underTest.addError(slot, viewItem);

        assertThat(underTest.getViewItemsWithErrors(slot),
                containsExactly(viewItem));
    }

    @Test
    public void addErrorAddsViewItemToSlotErrors() {
        underTest.addError(slot, viewItem);

        assertThat(underTest.getSlotsWithErrors(), containsExactly(slot));
    }

    @Test
    public void addErrorMarksSlotAsErroneous() {
        underTest.addError(slot, viewItem);

        assertThat(underTest.hasErrors(slot), is(true));
    }

    @Test
    public void addErrorMarksViewItemAsErroneous() {
        underTest.addError(slot, viewItem);

        assertThat(underTest.hasErrors(viewItem), is(true));
    }

    @Test
    public void addErrorSetsHasErrors() {
        underTest.addError(slot, viewItem);

        assertThat(underTest.hasErrors(), is(true));
    }

    @Test
    public void getSlotsWithErrorsReturnEmptyListIfViewItemIsErrorFree() {
        assertThat(underTest.getSlotsWithErrors(viewItem).isEmpty(), is(true));
    }

    @Test
    public void getViewItemsWithErrorsReturnEmptyListIfSlotIsErrorFree() {
        assertThat(underTest.getViewItemsWithErrors(slot).isEmpty(), is(true));
    }

    @Test
    public void initiallyErrorFree() {
        assertThat(underTest.hasErrors(), is(false));
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        slot = new Slot("s1", "", DataType.TEXT);

        when(viewItem.getViewItemID()).thenReturn("v1");

        underTest = new DefaultViewItemResolutionErrorModel();
    }
}