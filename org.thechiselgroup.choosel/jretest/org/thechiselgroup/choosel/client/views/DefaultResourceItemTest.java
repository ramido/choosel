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
package org.thechiselgroup.choosel.client.views;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.thechiselgroup.choosel.client.test.AdvancedAsserts.assertContentEquals;
import static org.thechiselgroup.choosel.client.test.ResourcesTestHelper.eqResources;
import static org.thechiselgroup.choosel.client.test.TestResourceSetFactory.createResources;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.client.resources.DefaultResourceSet;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.ui.popup.PopupManager;
import org.thechiselgroup.choosel.client.views.ResourceItem.Status;
import org.thechiselgroup.choosel.client.views.ResourceItem.Subset;
import org.thechiselgroup.choosel.client.views.ResourceItem.SubsetStatus;

import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;

public class DefaultResourceItemTest {

    private static final String RESOURCE_ITEM_GROUP_ID = "resourceItemCategory";

    private HoverModel hoverModel;

    @Mock
    private SlotMappingConfiguration slotMappingConfiguration;

    @Mock
    private PopupManager popupManager;

    private ResourceSet resources;

    private DefaultResourceItem underTest;

    private Slot numberSlot;

    private SlotMappingChangedHandler captureSlotMappingChangedHandler() {
        ArgumentCaptor<SlotMappingChangedHandler> captor = ArgumentCaptor
                .forClass(SlotMappingChangedHandler.class);
        verify(slotMappingConfiguration, times(1)).addHandler(captor.capture());
        return captor.getValue();
    }

    /**
     * remove highlighting on disposal (issue 65: highlighting remains after
     * window is closed)
     */
    @Test
    public void disposeRemovesHighlighting() {
        resources.addAll(createResources(1, 2));
        underTest.getHighlightingManager().setHighlighting(true);
        assertContentEquals(createResources(1, 2), hoverModel.getResources());

        underTest.dispose();

        assertContentEquals(Collections.<Resource> emptyList(), hoverModel
                .getResources().toList());
    }

    /**
     * remove highlighting on disposal (issue 65: highlighting remains after
     * window is closed)
     */
    @Test
    public void disposeRemovesPopupHighlighting() {
        resources.addAll(createResources(1, 2));
        underTest.popupManager.onMouseOver(0, 0);
        ArgumentCaptor<MouseOverHandler> argument = ArgumentCaptor
                .forClass(MouseOverHandler.class);
        verify(popupManager, times(1)).addPopupMouseOverHandler(
                argument.capture());
        argument.getValue().onMouseOver(new MouseOverEvent() {
        });
        assertContentEquals(createResources(1, 2), hoverModel.getResources());

        underTest.dispose();

        assertContentEquals(Collections.<Resource> emptyList(), hoverModel
                .getResources().toList());
    }

    @Test
    public void getHighlightedResourcesAfterAddHighlightingNoContainedResource() {
        resources.addAll(createResources(1, 2, 3, 4));
        underTest.addHighlightedResources(createResources(5, 6));
        assertContentEquals(createResources(),
                underTest.getHighlightedResources());
    }

    @Test
    public void getHighlightedResourcesAfterAddHighlightingOneContainedContainedResourceOutOfTwoResources() {
        resources.addAll(createResources(1, 2, 3, 4));
        underTest.addHighlightedResources(createResources(1, 5));
        assertContentEquals(createResources(1),
                underTest.getHighlightedResources());
    }

    @Test
    public void getHighlightedResourcesAfterAddHighlightingOnePlusOneContainedContainedResourceOutOfTwoResources() {
        resources.addAll(createResources(1, 2, 3, 4));
        underTest.addHighlightedResources(createResources(1));
        underTest.addHighlightedResources(createResources(2, 5));
        assertContentEquals(createResources(1, 2),
                underTest.getHighlightedResources());
    }

    @Test
    public void getHighlightedResourcesAfterAddHighlightingOnePlusTwoContainedResources() {
        resources.addAll(createResources(1, 2, 3, 4));
        underTest.addHighlightedResources(createResources(1));
        underTest.addHighlightedResources(createResources(2, 3));
        assertContentEquals(createResources(1, 2, 3),
                underTest.getHighlightedResources());
    }

    @Test
    public void getHighlightedResourcesAfterAddHighlightingOnePlusZeroContainedResources() {
        resources.addAll(createResources(1, 2, 3, 4));
        underTest.addHighlightedResources(createResources(1));
        underTest.addHighlightedResources(createResources(5, 6));
        assertContentEquals(createResources(1),
                underTest.getHighlightedResources());
    }

    @Test
    public void getHighlightedResourcesAfterAddHighlightingTwoContainedResources() {
        resources.addAll(createResources(1, 2, 3, 4));
        underTest.addHighlightedResources(createResources(1, 2));
        assertContentEquals(createResources(1, 2),
                underTest.getHighlightedResources());
    }

    @Test
    public void getHighlightedResourcesAfterRemoveHighlightingNoContainedResource() {
        resources.addAll(createResources(1, 2, 3, 4));
        underTest.removeHighlightedResources(createResources(5, 6));
        assertContentEquals(createResources(),
                underTest.getHighlightedResources());
    }

    @Test
    public void getHighlightedResourcesAfterRemoveHighlightingOneFromOneContainedResource() {
        resources.addAll(createResources(1, 2, 3, 4));
        underTest.addHighlightedResources(createResources(1));
        underTest.removeHighlightedResources(createResources(1, 6));
        assertContentEquals(createResources(),
                underTest.getHighlightedResources());
    }

    @Test
    public void getHighlightedResourcesAfterRemoveHighlightingOneFromTwoContainedResource() {
        resources.addAll(createResources(1, 2, 3, 4));
        underTest.addHighlightedResources(createResources(1, 2));
        underTest.removeHighlightedResources(createResources(2, 5));
        assertContentEquals(createResources(1),
                underTest.getHighlightedResources());
    }

    @Test
    public void getHighlightedResourcesAfterRemoveHighlightingTwoFromTwoContainedResource() {
        resources.addAll(createResources(1, 2, 3, 4));
        underTest.addHighlightedResources(createResources(1, 2));
        underTest.removeHighlightedResources(createResources(1, 2));
        assertContentEquals(createResources(),
                underTest.getHighlightedResources());
    }

    @Test
    public void getHighlightedResourcesAfterRemoveHighlightingZeroFromOneContainedResource() {
        resources.addAll(createResources(1, 2, 3, 4));
        underTest.addHighlightedResources(createResources(1));
        underTest.removeHighlightedResources(createResources(5, 6));
        assertContentEquals(createResources(1),
                underTest.getHighlightedResources());
    }

    @Test
    public void getResourceValue() {
        resources.addAll(createResources(1, 2, 3, 4));

        when(
                slotMappingConfiguration.resolve(eq(numberSlot),
                        eq(RESOURCE_ITEM_GROUP_ID), eq(resources))).thenReturn(
                2d);

        Object result = underTest.getResourceValue(numberSlot);
        assertEquals(2d, result);
    }

    @Test
    public void getResourceValueClearCacheForAllResourcesOnResourceSetChange() {
        resources.addAll(createResources(1, 2, 3, 4));

        when(
                slotMappingConfiguration.resolve(eq(numberSlot),
                        eq(RESOURCE_ITEM_GROUP_ID), eq(resources))).thenReturn(
                2d, 3d);

        underTest.getResourceValue(numberSlot);
        resources.removeAll(createResources(1));
        Object result = underTest.getResourceValue(numberSlot);
        assertEquals(3d, result);
    }

    @Test
    public void getResourceValueClearCacheForAllResourcesOnSlotChange() {
        SlotMappingChangedHandler handler = captureSlotMappingChangedHandler();

        resources.addAll(createResources(1, 2, 3, 4));

        when(
                slotMappingConfiguration.resolve(eq(numberSlot),
                        eq(RESOURCE_ITEM_GROUP_ID), eq(resources))).thenReturn(
                2d, 3d);

        underTest.getResourceValue(numberSlot);
        handler.onResourceCategoriesChanged(new SlotMappingChangedEvent(
                numberSlot));
        assertEquals(3d, underTest.getResourceValue(numberSlot));
    }

    @Test
    public void getResourceValueClearCacheForHighlightedResourcesOnHighlightedResourceSetChange() {
        resources.addAll(createResources(1, 2, 3, 4));

        when(
                slotMappingConfiguration.resolve(eq(numberSlot),
                        eq(RESOURCE_ITEM_GROUP_ID),
                        eqResources(createResources()))).thenReturn(0d);
        when(
                slotMappingConfiguration.resolve(eq(numberSlot),
                        eq(RESOURCE_ITEM_GROUP_ID),
                        eqResources(createResources(2, 3)))).thenReturn(2d);
        when(
                slotMappingConfiguration.resolve(eq(numberSlot),
                        eq(RESOURCE_ITEM_GROUP_ID),
                        eqResources(createResources(3)))).thenReturn(3d);

        underTest.getResourceValue(numberSlot, Subset.HIGHLIGHTED);
        underTest.addHighlightedResources(createResources(2, 3));
        underTest.getResourceValue(numberSlot, Subset.HIGHLIGHTED);
        underTest.removeHighlightedResources(createResources(2));
        assertEquals(3d,
                underTest.getResourceValue(numberSlot, Subset.HIGHLIGHTED));
    }

    @Test
    public void getResourceValueClearCacheForHighlightedResourcesOnSlotChange() {
        SlotMappingChangedHandler handler = captureSlotMappingChangedHandler();

        resources.addAll(createResources(1, 2, 3, 4));
        when(
                slotMappingConfiguration.resolve(eq(numberSlot),
                        eq(RESOURCE_ITEM_GROUP_ID),
                        eqResources(createResources(2, 3)))).thenReturn(2d, 3d);

        underTest.addHighlightedResources(createResources(2, 3));
        underTest.getResourceValue(numberSlot, Subset.HIGHLIGHTED);
        handler.onResourceCategoriesChanged(new SlotMappingChangedEvent(
                numberSlot));
        assertEquals(3d,
                underTest.getResourceValue(numberSlot, Subset.HIGHLIGHTED));
    }

    @Test
    public void getResourceValueClearCacheForSelectedResourcesOnSelectedResourceSetChange() {
        resources.addAll(createResources(1, 2, 3, 4));

        when(
                slotMappingConfiguration.resolve(eq(numberSlot),
                        eq(RESOURCE_ITEM_GROUP_ID),
                        eqResources(createResources()))).thenReturn(0d);
        when(
                slotMappingConfiguration.resolve(eq(numberSlot),
                        eq(RESOURCE_ITEM_GROUP_ID),
                        eqResources(createResources(2, 3)))).thenReturn(2d);
        when(
                slotMappingConfiguration.resolve(eq(numberSlot),
                        eq(RESOURCE_ITEM_GROUP_ID),
                        eqResources(createResources(3)))).thenReturn(3d);

        underTest.getResourceValue(numberSlot, Subset.SELECTED);
        underTest.addSelectedResources(createResources(2, 3));
        underTest.getResourceValue(numberSlot, Subset.SELECTED);
        underTest.removeSelectedResources(createResources(2));
        Object result = underTest.getResourceValue(numberSlot, Subset.SELECTED);
        assertEquals(3d, result);
    }

    @Test
    public void getResourceValueClearCacheForSelectedResourcesOnSlotChange() {
        SlotMappingChangedHandler handler = captureSlotMappingChangedHandler();

        resources.addAll(createResources(1, 2, 3, 4));
        when(
                slotMappingConfiguration.resolve(eq(numberSlot),
                        eq(RESOURCE_ITEM_GROUP_ID),
                        eqResources(createResources(2, 3)))).thenReturn(2d, 3d);

        underTest.addSelectedResources(createResources(2, 3));
        underTest.getResourceValue(numberSlot, Subset.SELECTED);
        handler.onResourceCategoriesChanged(new SlotMappingChangedEvent(
                numberSlot));
        assertEquals(3d,
                underTest.getResourceValue(numberSlot, Subset.SELECTED));
    }

    @Test
    public void getSelectedResourcesAfterAddSelectingNoContainedResource() {
        resources.addAll(createResources(1, 2, 3, 4));
        underTest.addSelectedResources(createResources(5, 6));
        assertContentEquals(createResources(), underTest.getSelectedResources());
    }

    @Test
    public void getSelectedResourcesAfterAddSelectingOneContainedContainedResourceOutOfTwoResources() {
        resources.addAll(createResources(1, 2, 3, 4));
        underTest.addSelectedResources(createResources(1, 5));
        assertContentEquals(createResources(1),
                underTest.getSelectedResources());
    }

    @Test
    public void getSelectedResourcesAfterAddSelectingOnePlusOneContainedContainedResourceOutOfTwoResources() {
        resources.addAll(createResources(1, 2, 3, 4));
        underTest.addSelectedResources(createResources(1));
        underTest.addSelectedResources(createResources(2, 5));
        assertContentEquals(createResources(1, 2),
                underTest.getSelectedResources());
    }

    @Test
    public void getSelectedResourcesAfterAddSelectingOnePlusTwoContainedResources() {
        resources.addAll(createResources(1, 2, 3, 4));
        underTest.addSelectedResources(createResources(1));
        underTest.addSelectedResources(createResources(2, 3));
        assertContentEquals(createResources(1, 2, 3),
                underTest.getSelectedResources());
    }

    @Test
    public void getSelectedResourcesAfterAddSelectingOnePlusZeroContainedResources() {
        resources.addAll(createResources(1, 2, 3, 4));
        underTest.addSelectedResources(createResources(1));
        underTest.addSelectedResources(createResources(5, 6));
        assertContentEquals(createResources(1),
                underTest.getSelectedResources());
    }

    @Test
    public void getSelectedResourcesAfterAddSelectingTwoContainedResources() {
        resources.addAll(createResources(1, 2, 3, 4));
        underTest.addSelectedResources(createResources(1, 2));
        assertContentEquals(createResources(1, 2),
                underTest.getSelectedResources());
    }

    @Test
    public void getSelectedResourcesAfterRemoveSelectingNoContainedResource() {
        resources.addAll(createResources(1, 2, 3, 4));
        underTest.removeSelectedResources(createResources(5, 6));
        assertContentEquals(createResources(), underTest.getSelectedResources());
    }

    @Test
    public void getSelectedResourcesAfterRemoveSelectingOneFromOneContainedResource() {
        resources.addAll(createResources(1, 2, 3, 4));
        underTest.addSelectedResources(createResources(1));
        underTest.removeSelectedResources(createResources(1, 6));
        assertContentEquals(createResources(), underTest.getSelectedResources());
    }

    @Test
    public void getSelectedResourcesAfterRemoveSelectingOneFromTwoContainedResource() {
        resources.addAll(createResources(1, 2, 3, 4));
        underTest.addSelectedResources(createResources(1, 2));
        underTest.removeSelectedResources(createResources(2, 5));
        assertContentEquals(createResources(1),
                underTest.getSelectedResources());
    }

    @Test
    public void getSelectedResourcesAfterRemoveSelectingTwoFromTwoContainedResource() {
        resources.addAll(createResources(1, 2, 3, 4));
        underTest.addSelectedResources(createResources(1, 2));
        underTest.removeSelectedResources(createResources(1, 2));
        assertContentEquals(createResources(), underTest.getSelectedResources());
    }

    @Test
    public void getSelectedResourcesAfterRemoveSelectingZeroFromOneContainedResource() {
        resources.addAll(createResources(1, 2, 3, 4));
        underTest.addSelectedResources(createResources(1));
        underTest.removeSelectedResources(createResources(5, 6));
        assertContentEquals(createResources(1),
                underTest.getSelectedResources());
    }

    @Test
    public void highlightStatusCompleteWhenTwoOutOfTwoResourcesHighlighted() {
        resources.addAll(createResources(1, 2));
        underTest.addHighlightedResources(createResources(1, 2));

        assertEquals(SubsetStatus.COMPLETE, underTest.getHighlightStatus());
    }

    @Test
    public void highlightStatusNoneWhenZeroOutOfTwoResourcesHighlighted() {
        resources.addAll(createResources(1, 2));
        underTest.addHighlightedResources(createResources());

        assertEquals(SubsetStatus.NONE, underTest.getHighlightStatus());
    }

    @Test
    public void highlightStatusNoneWhenZeroOutOfZeroResourcesHighlighted() {
        resources.addAll(createResources());
        underTest.addHighlightedResources(createResources());

        assertEquals(SubsetStatus.NONE, underTest.getHighlightStatus());
    }

    @Test
    public void highlightStatusPartialWhenOneOutOfTwoResourcesHighlighted() {
        resources.addAll(createResources(1, 2));
        underTest.addHighlightedResources(createResources(1));

        assertEquals(SubsetStatus.PARTIAL, underTest.getHighlightStatus());
    }

    @Test
    public void isHighlightedIsFalseAfterInit() {
        assertEquals(SubsetStatus.NONE, underTest.getHighlightStatus());
    }

    @Test
    public void isSelectedIsFalseAfterInit() {
        // assertEquals(false, underTest.isSelected());
        assertEquals(SubsetStatus.NONE, underTest.getSelectionStatus());
    }

    @Test
    public void selectStatusCompleteWhenTwoOutOfTwoResourcesSelected() {
        resources.addAll(createResources(1, 2));
        underTest.addSelectedResources(createResources(1, 2));

        assertEquals(SubsetStatus.COMPLETE, underTest.getSelectionStatus());
    }

    @Test
    public void selectStatusNoneWhenZeroOutOfTwoResourcesSelected() {
        resources.addAll(createResources(1, 2));
        underTest.addSelectedResources(createResources());

        assertEquals(SubsetStatus.NONE, underTest.getSelectionStatus());
    }

    @Test
    public void selectStatusNoneWhenZeroOutOfZeroResourcesSelected() {
        resources.addAll(createResources());
        underTest.addSelectedResources(createResources());

        assertEquals(SubsetStatus.NONE, underTest.getSelectionStatus());
    }

    @Test
    public void selectStatusPartialWhenOneOutOfTwoResourcesSelected() {
        resources.addAll(createResources(1, 2));
        underTest.addSelectedResources(createResources(1));

        assertEquals(SubsetStatus.PARTIAL, underTest.getSelectionStatus());
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        numberSlot = new Slot("id-2", "number-slot", DataType.NUMBER);
        hoverModel = spy(new HoverModel());
        resources = new DefaultResourceSet();
        underTest = spy(new DefaultResourceItem(RESOURCE_ITEM_GROUP_ID,
                resources, hoverModel, popupManager, slotMappingConfiguration));
    }

    @Test
    public void statusIsDefault() {
        assertEquals(Status.DEFAULT, underTest.getStatus());
    }

    @Test
    public void statusIsHighlighted() {
        resources.addAll(createResources(1));
        underTest.addHighlightedResources(createResources(1));
        assertEquals(Status.HIGHLIGHTED, underTest.getStatus());
    }

    @Test
    public void statusIsHighlightedSelected() {
        resources.addAll(createResources(1));
        underTest.addHighlightedResources(createResources(1));
        underTest.addSelectedResources(createResources(1));
        assertEquals(Status.HIGHLIGHTED_SELECTED, underTest.getStatus());
    }

    @Test
    public void statusIsNotHighlightedAfterRemovingHighlightedResources() {
        resources.addAll(createResources(1));
        underTest.addHighlightedResources(createResources(1));
        underTest.removeHighlightedResources(createResources(1));
        assertEquals(Status.DEFAULT, underTest.getStatus());
    }

    @Test
    public void statusIsNotHighlightedOnEmptyAdd() {
        resources.addAll(createResources(1));
        underTest.addHighlightedResources(createResources(2));
        assertEquals(Status.DEFAULT, underTest.getStatus());
    }

    @Test
    public void statusIsNotSelectedAfterRemovingSelectedResources() {
        resources.addAll(createResources(1));
        underTest.addSelectedResources(createResources(1));
        underTest.removeSelectedResources(createResources(1));
        assertEquals(Status.DEFAULT, underTest.getStatus());
    }

    @Test
    public void statusIsNotSelectedOnEmptyAdd() {
        resources.addAll(createResources(1));
        underTest.addSelectedResources(createResources(2));
        assertEquals(Status.DEFAULT, underTest.getStatus());
    }

    @Test
    public void statusIsSelected() {
        resources.addAll(createResources(1));
        underTest.addSelectedResources(createResources(1));
        assertEquals(Status.SELECTED, underTest.getStatus());
    }

    @Test
    public void statusPartiallyHighlightedAfterOneResourceIsRemovedFromHighlight() {
        resources.addAll(createResources(1, 2));
        underTest.addHighlightedResources(createResources(1, 2));
        underTest.removeHighlightedResources(createResources(1));
        assertEquals(Status.PARTIALLY_HIGHLIGHTED, underTest.getStatus());
    }

    @Test
    public void statusPartiallyHighlightedSelectedAfterOneResourceIsRemovedFromHighlightAndSelection() {
        resources.addAll(createResources(1, 2));
        underTest.addHighlightedResources(createResources(1, 2));
        underTest.removeHighlightedResources(createResources(1));
        underTest.addSelectedResources(createResources(1, 2));
        underTest.removeSelectedResources(createResources(1));
        assertEquals(Status.PARTIALLY_HIGHLIGHTED_SELECTED,
                underTest.getStatus());
    }

    @Test
    public void statusPartiallySelectedAfterOneResourceIsRemovedFromSelect() {
        resources.addAll(createResources(1, 2));
        underTest.addSelectedResources(createResources(1, 2));
        underTest.removeSelectedResources(createResources(1));
        assertEquals(Status.PARTIALLY_SELECTED, underTest.getStatus());
    }

}
