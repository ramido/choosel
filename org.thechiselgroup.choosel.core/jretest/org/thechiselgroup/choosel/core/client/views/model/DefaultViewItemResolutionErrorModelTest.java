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
    private VisualItem viewItem1;

    @Mock
    private VisualItem viewItem2;

    private Slot slot1;

    private Slot slot2;

    @Test
    public void clearSlotDoesNotRemoveViewItemMarkIfOtherErrors() {
        underTest.reportError(slot1, viewItem1);
        underTest.reportError(slot2, viewItem1);
        underTest.clearErrors(slot1);

        assertThat(underTest.hasErrors(viewItem1), is(true));
    }

    @Test
    public void clearSlotRemovesSlotFromViewItemErrors() {
        underTest.reportError(slot1, viewItem1);
        underTest.reportError(slot2, viewItem1);
        underTest.clearErrors(slot1);

        assertThat(underTest.getSlotsWithErrors(viewItem1),
                containsExactly(slot2));
    }

    @Test
    public void clearSlotRemovesSlotMark() {
        underTest.reportError(slot1, viewItem1);
        underTest.clearErrors(slot1);

        assertThat(underTest.hasErrors(slot1), is(false));
    }

    @Test
    public void clearSlotRemovesViewItemMarkIfNoOtherErrors() {
        underTest.reportError(slot1, viewItem1);
        underTest.clearErrors(slot1);

        assertThat(underTest.hasErrors(viewItem1), is(false));
    }

    @Test
    public void clearViewItemDoesNotRemovesSlotMarkIfOtherErrors() {
        underTest.reportError(slot1, viewItem1);
        underTest.reportError(slot1, viewItem2);
        underTest.clearErrors(viewItem1);

        assertThat(underTest.hasErrors(slot1), is(true));
    }

    @Test
    public void clearViewItemRemovesSlotMarkIfNoOtherErrors() {
        underTest.reportError(slot1, viewItem1);
        underTest.clearErrors(viewItem1);

        assertThat(underTest.hasErrors(slot1), is(false));
    }

    @Test
    public void clearViewItemRemovesViewItemFromSlotErrors() {
        underTest.reportError(slot1, viewItem1);
        underTest.reportError(slot1, viewItem2);
        underTest.clearErrors(viewItem1);

        assertThat(underTest.getViewItemsWithErrors(slot1),
                containsExactly(viewItem2));
    }

    @Test
    public void clearViewItemRemovesViewItemMark() {
        underTest.reportError(slot1, viewItem1);
        underTest.clearErrors(viewItem1);

        assertThat(underTest.hasErrors(viewItem1), is(false));
    }

    @Test
    public void getSlotsWithErrorsReturnEmptyListIfViewItemIsErrorFree() {
        assertThat(underTest.getSlotsWithErrors(viewItem1).isEmpty(), is(true));
    }

    @Test
    public void getViewItemsWithErrorsReturnEmptyListIfSlotIsErrorFree() {
        assertThat(underTest.getViewItemsWithErrors(slot1).isEmpty(), is(true));
    }

    @Test
    public void initiallyErrorFree() {
        assertThat(underTest.hasErrors(), is(false));
    }

    @Test
    public void removeDoesNotRemoveSlotItemMarkIfOtherErrors() {
        underTest.reportError(slot1, viewItem1);
        underTest.reportError(slot1, viewItem2);
        underTest.removeError(slot1, viewItem1);

        assertThat(underTest.hasErrors(slot1), is(true));
    }

    @Test
    public void removeDoesNotRemoveViewItemMarkIfOtherErrors() {
        underTest.reportError(slot1, viewItem1);
        underTest.reportError(slot2, viewItem1);
        underTest.removeError(slot1, viewItem1);

        assertThat(underTest.hasErrors(viewItem1), is(true));
    }

    @Test
    public void removeRemovesSlotFromViewItemErrors() {
        underTest.reportError(slot1, viewItem1);
        underTest.reportError(slot2, viewItem1);
        underTest.removeError(slot1, viewItem1);

        assertThat(underTest.getSlotsWithErrors(viewItem1),
                containsExactly(slot2));
    }

    @Test
    public void removeRemovesSlotItemMarkIfNoOtherErrors() {
        underTest.reportError(slot1, viewItem1);
        underTest.removeError(slot1, viewItem1);

        assertThat(underTest.hasErrors(slot1), is(false));
    }

    @Test
    public void removeRemovesSlotMark() {
        underTest.reportError(slot1, viewItem1);
        underTest.removeError(slot1, viewItem1);

        assertThat(underTest.hasErrors(slot1), is(false));
    }

    @Test
    public void removeRemovesViewItemFromSlotErrors() {
        underTest.reportError(slot1, viewItem1);
        underTest.reportError(slot1, viewItem2);
        underTest.removeError(slot1, viewItem1);

        assertThat(underTest.getViewItemsWithErrors(slot1),
                containsExactly(viewItem2));
    }

    @Test
    public void removeRemovesViewItemMark() {
        underTest.reportError(slot1, viewItem1);
        underTest.removeError(slot1, viewItem1);

        assertThat(underTest.hasErrors(viewItem1), is(false));
    }

    @Test
    public void removeRemovesViewItemMarkIfNoOtherErrors() {
        underTest.reportError(slot1, viewItem1);
        underTest.removeError(slot1, viewItem1);

        assertThat(underTest.hasErrors(viewItem1), is(false));
    }

    @Test
    public void reportErrorAddsSlotToErrorSlots() {
        underTest.reportError(slot1, viewItem1);

        assertThat(underTest.getSlotsWithErrors(), containsExactly(slot1));
    }

    @Test
    public void reportErrorAddsSlotToViewItemErrors() {
        underTest.reportError(slot1, viewItem1);

        assertThat(underTest.getSlotsWithErrors(viewItem1),
                containsExactly(slot1));
    }

    @Test
    public void reportErrorAddsViewItemToErrorViewItems() {
        underTest.reportError(slot1, viewItem1);

        assertThat(underTest.getViewItemsWithErrors(slot1),
                containsExactly(viewItem1));
    }

    @Test
    public void reportErrorAddsViewItemToViewItemErrors() {
        underTest.reportError(slot1, viewItem1);

        assertThat(underTest.getViewItemsWithErrors(),
                containsExactly(viewItem1));
    }

    @Test
    public void reportErrorMarksSlotAsErroneous() {
        underTest.reportError(slot1, viewItem1);

        assertThat(underTest.hasErrors(slot1), is(true));
    }

    @Test
    public void reportErrorMarksViewItemAsErroneous() {
        underTest.reportError(slot1, viewItem1);

        assertThat(underTest.hasErrors(viewItem1), is(true));
    }

    @Test
    public void reportErrorSetsHasErrors() {
        underTest.reportError(slot1, viewItem1);

        assertThat(underTest.hasErrors(), is(true));
    }

    @Test
    public void reportErrorTwiceAddsSlotToErrorSlotsOnce() {
        underTest.reportError(slot1, viewItem1);
        underTest.reportError(slot1, viewItem1);

        assertThat(underTest.getSlotsWithErrors(), containsExactly(slot1));
    }

    @Test
    public void reportErrorTwiceAddsSlotToViewItemErrorsOnce() {
        underTest.reportError(slot1, viewItem1);
        underTest.reportError(slot1, viewItem1);

        assertThat(underTest.getSlotsWithErrors(viewItem1),
                containsExactly(slot1));
    }

    @Test
    public void reportErrorTwiceAddsViewItemToErrorViewItemsOnce() {
        underTest.reportError(slot1, viewItem1);
        underTest.reportError(slot1, viewItem1);

        assertThat(underTest.getViewItemsWithErrors(slot1),
                containsExactly(viewItem1));
    }

    @Test
    public void reportErrorTwiceAddsViewItemToViewItemErrorsOnce() {
        underTest.reportError(slot1, viewItem1);
        underTest.reportError(slot1, viewItem1);

        assertThat(underTest.getViewItemsWithErrors(),
                containsExactly(viewItem1));
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        slot1 = new Slot("s1", "", DataType.TEXT);
        slot2 = new Slot("s2", "", DataType.TEXT);

        when(viewItem1.getId()).thenReturn("v1");
        when(viewItem2.getId()).thenReturn("v2");

        underTest = new DefaultViewItemResolutionErrorModel();
    }
}