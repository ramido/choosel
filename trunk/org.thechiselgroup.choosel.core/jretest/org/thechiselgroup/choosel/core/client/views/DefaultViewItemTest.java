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
package org.thechiselgroup.choosel.core.client.views;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.thechiselgroup.choosel.core.client.test.AdvancedAsserts.assertContentEquals;
import static org.thechiselgroup.choosel.core.client.test.ResourcesTestHelper.eqResources;
import static org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory.createResources;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.core.client.resources.DataType;
import org.thechiselgroup.choosel.core.client.resources.DefaultResourceSet;
import org.thechiselgroup.choosel.core.client.resources.Resource;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.ui.popup.PopupManager;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollections;
import org.thechiselgroup.choosel.core.client.views.ViewItem.Status;
import org.thechiselgroup.choosel.core.client.views.ViewItem.Subset;
import org.thechiselgroup.choosel.core.client.views.ViewItem.SubsetStatus;
import org.thechiselgroup.choosel.core.client.views.slots.Slot;
import org.thechiselgroup.choosel.core.client.views.slots.SlotMappingChangedEvent;
import org.thechiselgroup.choosel.core.client.views.slots.SlotMappingChangedHandler;
import org.thechiselgroup.choosel.core.client.views.slots.SlotMappingConfiguration;

import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;

public class DefaultViewItemTest {

    private static final String VIEW_ITEM_ID = "viewItemCategory";

    private HoverModel hoverModel;

    @Mock
    private SlotMappingConfiguration slotMappingConfiguration;

    @Mock
    private PopupManager popupManager;

    private ResourceSet resources;

    private DefaultViewItem underTest;

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
        underTest.updateHighlightedResources(createResources(5, 6),
                LightweightCollections.<Resource> emptyCollection());
        assertContentEquals(createResources(),
                underTest.getHighlightedResources());
    }

    @Test
    public void getHighlightedResourcesAfterAddHighlightingOneContainedContainedResourceOutOfTwoResources() {
        resources.addAll(createResources(1, 2, 3, 4));
        underTest.updateHighlightedResources(createResources(1, 5),
                LightweightCollections.<Resource> emptyCollection());
        assertContentEquals(createResources(1),
                underTest.getHighlightedResources());
    }

    @Test
    public void getHighlightedResourcesAfterAddHighlightingOnePlusOneContainedContainedResourceOutOfTwoResources() {
        resources.addAll(createResources(1, 2, 3, 4));
        underTest.updateHighlightedResources(createResources(1),
                LightweightCollections.<Resource> emptyCollection());
        underTest.updateHighlightedResources(createResources(2, 5),
                LightweightCollections.<Resource> emptyCollection());
        assertContentEquals(createResources(1, 2),
                underTest.getHighlightedResources());
    }

    @Test
    public void getHighlightedResourcesAfterAddHighlightingOnePlusTwoContainedResources() {
        resources.addAll(createResources(1, 2, 3, 4));
        underTest.updateHighlightedResources(createResources(1),
                LightweightCollections.<Resource> emptyCollection());
        underTest.updateHighlightedResources(createResources(2, 3),
                LightweightCollections.<Resource> emptyCollection());
        assertContentEquals(createResources(1, 2, 3),
                underTest.getHighlightedResources());
    }

    @Test
    public void getHighlightedResourcesAfterAddHighlightingOnePlusZeroContainedResources() {
        resources.addAll(createResources(1, 2, 3, 4));
        underTest.updateHighlightedResources(createResources(1),
                LightweightCollections.<Resource> emptyCollection());
        underTest.updateHighlightedResources(createResources(5, 6),
                LightweightCollections.<Resource> emptyCollection());
        assertContentEquals(createResources(1),
                underTest.getHighlightedResources());
    }

    @Test
    public void getHighlightedResourcesAfterAddHighlightingTwoContainedResources() {
        resources.addAll(createResources(1, 2, 3, 4));
        underTest.updateHighlightedResources(createResources(1, 2),
                LightweightCollections.<Resource> emptyCollection());
        assertContentEquals(createResources(1, 2),
                underTest.getHighlightedResources());
    }

    @Test
    public void getHighlightedResourcesAfterRemoveHighlightingNoContainedResource() {
        resources.addAll(createResources(1, 2, 3, 4));
        underTest.updateHighlightedResources(
                LightweightCollections.<Resource> emptyCollection(),
                createResources(5, 6));
        assertContentEquals(createResources(),
                underTest.getHighlightedResources());
    }

    @Test
    public void getHighlightedResourcesAfterRemoveHighlightingOneFromOneContainedResource() {
        resources.addAll(createResources(1, 2, 3, 4));
        underTest.updateHighlightedResources(createResources(1),
                LightweightCollections.<Resource> emptyCollection());
        underTest.updateHighlightedResources(
                LightweightCollections.<Resource> emptyCollection(),
                createResources(1, 6));
        assertContentEquals(createResources(),
                underTest.getHighlightedResources());
    }

    @Test
    public void getHighlightedResourcesAfterRemoveHighlightingOneFromTwoContainedResource() {
        resources.addAll(createResources(1, 2, 3, 4));
        underTest.updateHighlightedResources(createResources(1, 2),
                LightweightCollections.<Resource> emptyCollection());
        underTest.updateHighlightedResources(
                LightweightCollections.<Resource> emptyCollection(),
                createResources(2, 5));
        assertContentEquals(createResources(1),
                underTest.getHighlightedResources());
    }

    @Test
    public void getHighlightedResourcesAfterRemoveHighlightingTwoFromTwoContainedResource() {
        resources.addAll(createResources(1, 2, 3, 4));
        underTest.updateHighlightedResources(createResources(1, 2),
                LightweightCollections.<Resource> emptyCollection());
        underTest.updateHighlightedResources(
                LightweightCollections.<Resource> emptyCollection(),
                createResources(1, 2));
        assertContentEquals(createResources(),
                underTest.getHighlightedResources());
    }

    @Test
    public void getHighlightedResourcesAfterRemoveHighlightingZeroFromOneContainedResource() {
        resources.addAll(createResources(1, 2, 3, 4));
        underTest.updateHighlightedResources(createResources(1),
                LightweightCollections.<Resource> emptyCollection());
        underTest.updateHighlightedResources(
                LightweightCollections.<Resource> emptyCollection(),
                createResources(5, 6));
        assertContentEquals(createResources(1),
                underTest.getHighlightedResources());
    }

    @Test
    public void getResourceValue() {
        resources.addAll(createResources(1, 2, 3, 4));

        when(
                slotMappingConfiguration.resolve(eq(numberSlot),
                        eq(VIEW_ITEM_ID), eq(resources))).thenReturn(2d);

        Object result = underTest.getResourceValue(numberSlot);
        assertEquals(2d, result);
    }

    @Test
    public void getResourceValueClearCacheForAllResourcesOnResourceSetChange() {
        resources.addAll(createResources(1, 2, 3, 4));

        when(
                slotMappingConfiguration.resolve(eq(numberSlot),
                        eq(VIEW_ITEM_ID), eq(resources))).thenReturn(2d, 3d);

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
                        eq(VIEW_ITEM_ID), eq(resources))).thenReturn(2d, 3d);

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
                        eq(VIEW_ITEM_ID), eqResources(createResources())))
                .thenReturn(0d);
        when(
                slotMappingConfiguration.resolve(eq(numberSlot),
                        eq(VIEW_ITEM_ID), eqResources(createResources(2, 3))))
                .thenReturn(2d);
        when(
                slotMappingConfiguration.resolve(eq(numberSlot),
                        eq(VIEW_ITEM_ID), eqResources(createResources(3))))
                .thenReturn(3d);

        underTest.getResourceValue(numberSlot, Subset.HIGHLIGHTED);
        underTest.updateHighlightedResources(createResources(2, 3),
                LightweightCollections.<Resource> emptyCollection());
        underTest.getResourceValue(numberSlot, Subset.HIGHLIGHTED);
        underTest.updateHighlightedResources(
                LightweightCollections.<Resource> emptyCollection(),
                createResources(2));
        assertEquals(3d,
                underTest.getResourceValue(numberSlot, Subset.HIGHLIGHTED));
    }

    @Test
    public void getResourceValueClearCacheForHighlightedResourcesOnSlotChange() {
        SlotMappingChangedHandler handler = captureSlotMappingChangedHandler();

        resources.addAll(createResources(1, 2, 3, 4));
        when(
                slotMappingConfiguration.resolve(eq(numberSlot),
                        eq(VIEW_ITEM_ID), eqResources(createResources(2, 3))))
                .thenReturn(2d, 3d);

        underTest.updateHighlightedResources(createResources(2, 3),
                LightweightCollections.<Resource> emptyCollection());
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
                        eq(VIEW_ITEM_ID), eqResources(createResources())))
                .thenReturn(0d);
        when(
                slotMappingConfiguration.resolve(eq(numberSlot),
                        eq(VIEW_ITEM_ID), eqResources(createResources(2, 3))))
                .thenReturn(2d);
        when(
                slotMappingConfiguration.resolve(eq(numberSlot),
                        eq(VIEW_ITEM_ID), eqResources(createResources(3))))
                .thenReturn(3d);

        underTest.getResourceValue(numberSlot, Subset.SELECTED);
        underTest.updateSelectedResources(createResources(2, 3),
        LightweightCollections.<Resource> emptyCollection());
        underTest.getResourceValue(numberSlot, Subset.SELECTED);
        underTest.updateSelectedResources(
        LightweightCollections.<Resource> emptyCollection(),
        createResources(2));
        Object result = underTest.getResourceValue(numberSlot, Subset.SELECTED);
        assertEquals(3d, result);
    }

    @Test
    public void getResourceValueClearCacheForSelectedResourcesOnSlotChange() {
        SlotMappingChangedHandler handler = captureSlotMappingChangedHandler();

        resources.addAll(createResources(1, 2, 3, 4));
        when(
                slotMappingConfiguration.resolve(eq(numberSlot),
                        eq(VIEW_ITEM_ID), eqResources(createResources(2, 3))))
                .thenReturn(2d, 3d);

        underTest.updateSelectedResources(createResources(2, 3),
        LightweightCollections.<Resource> emptyCollection());
        underTest.getResourceValue(numberSlot, Subset.SELECTED);
        handler.onResourceCategoriesChanged(new SlotMappingChangedEvent(
                numberSlot));
        assertEquals(3d,
                underTest.getResourceValue(numberSlot, Subset.SELECTED));
    }

    @Test
    public void getSelectedResourcesAfterAddSelectingNoContainedResource() {
        resources.addAll(createResources(1, 2, 3, 4));
        underTest.updateSelectedResources(createResources(5, 6),
        LightweightCollections.<Resource> emptyCollection());
        assertContentEquals(createResources(), underTest.getSelectedResources());
    }

    @Test
    public void getSelectedResourcesAfterAddSelectingOneContainedContainedResourceOutOfTwoResources() {
        resources.addAll(createResources(1, 2, 3, 4));
        underTest.updateSelectedResources(createResources(1, 5),
        LightweightCollections.<Resource> emptyCollection());
        assertContentEquals(createResources(1),
                underTest.getSelectedResources());
    }

    @Test
    public void getSelectedResourcesAfterAddSelectingOnePlusOneContainedContainedResourceOutOfTwoResources() {
        resources.addAll(createResources(1, 2, 3, 4));
        underTest.updateSelectedResources(createResources(1),
        LightweightCollections.<Resource> emptyCollection());
        underTest.updateSelectedResources(createResources(2, 5),
        LightweightCollections.<Resource> emptyCollection());
        assertContentEquals(createResources(1, 2),
                underTest.getSelectedResources());
    }

    @Test
    public void getSelectedResourcesAfterAddSelectingOnePlusTwoContainedResources() {
        resources.addAll(createResources(1, 2, 3, 4));
        underTest.updateSelectedResources(createResources(1),
        LightweightCollections.<Resource> emptyCollection());
        underTest.updateSelectedResources(createResources(2, 3),
        LightweightCollections.<Resource> emptyCollection());
        assertContentEquals(createResources(1, 2, 3),
                underTest.getSelectedResources());
    }

    @Test
    public void getSelectedResourcesAfterAddSelectingOnePlusZeroContainedResources() {
        resources.addAll(createResources(1, 2, 3, 4));
        underTest.updateSelectedResources(createResources(1),
        LightweightCollections.<Resource> emptyCollection());
        underTest.updateSelectedResources(createResources(5, 6),
        LightweightCollections.<Resource> emptyCollection());
        assertContentEquals(createResources(1),
                underTest.getSelectedResources());
    }

    @Test
    public void getSelectedResourcesAfterAddSelectingTwoContainedResources() {
        resources.addAll(createResources(1, 2, 3, 4));
        underTest.updateSelectedResources(createResources(1, 2),
        LightweightCollections.<Resource> emptyCollection());
        assertContentEquals(createResources(1, 2),
                underTest.getSelectedResources());
    }

    @Test
    public void getSelectedResourcesAfterRemoveSelectingNoContainedResource() {
        resources.addAll(createResources(1, 2, 3, 4));
        underTest.updateSelectedResources(
        LightweightCollections.<Resource> emptyCollection(),
        createResources(5, 6));
        assertContentEquals(createResources(), underTest.getSelectedResources());
    }

    @Test
    public void getSelectedResourcesAfterRemoveSelectingOneFromOneContainedResource() {
        resources.addAll(createResources(1, 2, 3, 4));
        underTest.updateSelectedResources(createResources(1),
        LightweightCollections.<Resource> emptyCollection());
        underTest.updateSelectedResources(
        LightweightCollections.<Resource> emptyCollection(),
        createResources(1, 6));
        assertContentEquals(createResources(), underTest.getSelectedResources());
    }

    @Test
    public void getSelectedResourcesAfterRemoveSelectingOneFromTwoContainedResource() {
        resources.addAll(createResources(1, 2, 3, 4));
        underTest.updateSelectedResources(createResources(1, 2),
        LightweightCollections.<Resource> emptyCollection());
        underTest.updateSelectedResources(
        LightweightCollections.<Resource> emptyCollection(),
        createResources(2, 5));
        assertContentEquals(createResources(1),
                underTest.getSelectedResources());
    }

    @Test
    public void getSelectedResourcesAfterRemoveSelectingTwoFromTwoContainedResource() {
        resources.addAll(createResources(1, 2, 3, 4));
        underTest.updateSelectedResources(createResources(1, 2),
        LightweightCollections.<Resource> emptyCollection());
        underTest.updateSelectedResources(
        LightweightCollections.<Resource> emptyCollection(),
        createResources(1, 2));
        assertContentEquals(createResources(), underTest.getSelectedResources());
    }

    @Test
    public void getSelectedResourcesAfterRemoveSelectingZeroFromOneContainedResource() {
        resources.addAll(createResources(1, 2, 3, 4));
        underTest.updateSelectedResources(createResources(1),
        LightweightCollections.<Resource> emptyCollection());
        underTest.updateSelectedResources(
        LightweightCollections.<Resource> emptyCollection(),
        createResources(5, 6));
        assertContentEquals(createResources(1),
                underTest.getSelectedResources());
    }

    @Test
    public void highlightStatusCompleteWhenTwoOutOfTwoResourcesHighlighted() {
        resources.addAll(createResources(1, 2));
        underTest.updateHighlightedResources(createResources(1, 2),
                LightweightCollections.<Resource> emptyCollection());

        assertEquals(SubsetStatus.COMPLETE, underTest.getHighlightStatus());
    }

    @Test
    public void highlightStatusNoneWhenZeroOutOfTwoResourcesHighlighted() {
        resources.addAll(createResources(1, 2));
        underTest.updateHighlightedResources(createResources(),
                LightweightCollections.<Resource> emptyCollection());

        assertEquals(SubsetStatus.NONE, underTest.getHighlightStatus());
    }

    @Test
    public void highlightStatusNoneWhenZeroOutOfZeroResourcesHighlighted() {
        resources.addAll(createResources());
        underTest.updateHighlightedResources(createResources(),
                LightweightCollections.<Resource> emptyCollection());

        assertEquals(SubsetStatus.NONE, underTest.getHighlightStatus());
    }

    @Test
    public void highlightStatusPartialWhenOneOutOfTwoResourcesHighlighted() {
        resources.addAll(createResources(1, 2));
        underTest.updateHighlightedResources(createResources(1),
                LightweightCollections.<Resource> emptyCollection());

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
        underTest.updateSelectedResources(createResources(1, 2),
        LightweightCollections.<Resource> emptyCollection());

        assertEquals(SubsetStatus.COMPLETE, underTest.getSelectionStatus());
    }

    @Test
    public void selectStatusNoneWhenZeroOutOfTwoResourcesSelected() {
        resources.addAll(createResources(1, 2));
        underTest.updateSelectedResources(createResources(),
        LightweightCollections.<Resource> emptyCollection());

        assertEquals(SubsetStatus.NONE, underTest.getSelectionStatus());
    }

    @Test
    public void selectStatusNoneWhenZeroOutOfZeroResourcesSelected() {
        resources.addAll(createResources());
        underTest.updateSelectedResources(createResources(),
        LightweightCollections.<Resource> emptyCollection());

        assertEquals(SubsetStatus.NONE, underTest.getSelectionStatus());
    }

    @Test
    public void selectStatusPartialWhenOneOutOfTwoResourcesSelected() {
        resources.addAll(createResources(1, 2));
        underTest.updateSelectedResources(createResources(1),
        LightweightCollections.<Resource> emptyCollection());

        assertEquals(SubsetStatus.PARTIAL, underTest.getSelectionStatus());
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        numberSlot = new Slot("id-2", "number-slot", DataType.NUMBER);
        hoverModel = spy(new HoverModel());
        resources = new DefaultResourceSet();
        underTest = spy(new DefaultViewItem(VIEW_ITEM_ID, resources,
                hoverModel, popupManager, slotMappingConfiguration));
    }

    @Test
    public void statusIsDefault() {
        assertEquals(Status.DEFAULT, underTest.getStatus());
    }

    @Test
    public void statusIsHighlighted() {
        resources.addAll(createResources(1));
        underTest.updateHighlightedResources(createResources(1),
                LightweightCollections.<Resource> emptyCollection());
        assertEquals(Status.HIGHLIGHTED, underTest.getStatus());
    }

    @Test
    public void statusIsHighlightedSelected() {
        resources.addAll(createResources(1));
        underTest.updateHighlightedResources(createResources(1),
                LightweightCollections.<Resource> emptyCollection());
        underTest.updateSelectedResources(createResources(1),
        LightweightCollections.<Resource> emptyCollection());
        assertEquals(Status.HIGHLIGHTED_SELECTED, underTest.getStatus());
    }

    @Test
    public void statusIsNotHighlightedAfterRemovingHighlightedResources() {
        resources.addAll(createResources(1));
        underTest.updateHighlightedResources(createResources(1),
                LightweightCollections.<Resource> emptyCollection());
        underTest.updateHighlightedResources(
                LightweightCollections.<Resource> emptyCollection(),
                createResources(1));
        assertEquals(Status.DEFAULT, underTest.getStatus());
    }

    @Test
    public void statusIsNotHighlightedOnEmptyAdd() {
        resources.addAll(createResources(1));
        underTest.updateHighlightedResources(createResources(2),
                LightweightCollections.<Resource> emptyCollection());
        assertEquals(Status.DEFAULT, underTest.getStatus());
    }

    @Test
    public void statusIsNotSelectedAfterRemovingSelectedResources() {
        resources.addAll(createResources(1));
        underTest.updateSelectedResources(createResources(1),
        LightweightCollections.<Resource> emptyCollection());
        underTest.updateSelectedResources(
        LightweightCollections.<Resource> emptyCollection(),
        createResources(1));
        assertEquals(Status.DEFAULT, underTest.getStatus());
    }

    @Test
    public void statusIsNotSelectedOnEmptyAdd() {
        resources.addAll(createResources(1));
        underTest.updateSelectedResources(createResources(2),
        LightweightCollections.<Resource> emptyCollection());
        assertEquals(Status.DEFAULT, underTest.getStatus());
    }

    @Test
    public void statusIsSelected() {
        resources.addAll(createResources(1));
        underTest.updateSelectedResources(createResources(1),
        LightweightCollections.<Resource> emptyCollection());
        assertEquals(Status.SELECTED, underTest.getStatus());
    }

    @Test
    public void statusPartiallyHighlightedAfterOneResourceIsRemovedFromHighlight() {
        resources.addAll(createResources(1, 2));
        underTest.updateHighlightedResources(createResources(1, 2),
                LightweightCollections.<Resource> emptyCollection());
        underTest.updateHighlightedResources(
                LightweightCollections.<Resource> emptyCollection(),
                createResources(1));
        assertEquals(Status.PARTIALLY_HIGHLIGHTED, underTest.getStatus());
    }

    @Test
    public void statusPartiallyHighlightedSelectedAfterOneResourceIsRemovedFromHighlightAndSelection() {
        resources.addAll(createResources(1, 2));
        underTest.updateHighlightedResources(createResources(1, 2),
                LightweightCollections.<Resource> emptyCollection());
        underTest.updateHighlightedResources(
                LightweightCollections.<Resource> emptyCollection(),
                createResources(1));
        underTest.updateSelectedResources(createResources(1, 2),
        LightweightCollections.<Resource> emptyCollection());
        underTest.updateSelectedResources(
        LightweightCollections.<Resource> emptyCollection(),
        createResources(1));
        assertEquals(Status.PARTIALLY_HIGHLIGHTED_SELECTED,
                underTest.getStatus());
    }

    @Test
    public void statusPartiallySelectedAfterOneResourceIsRemovedFromSelect() {
        resources.addAll(createResources(1, 2));
        underTest.updateSelectedResources(createResources(1, 2),
        LightweightCollections.<Resource> emptyCollection());
        underTest.updateSelectedResources(
        LightweightCollections.<Resource> emptyCollection(),
        createResources(1));
        assertEquals(Status.PARTIALLY_SELECTED, underTest.getStatus());
    }

}
