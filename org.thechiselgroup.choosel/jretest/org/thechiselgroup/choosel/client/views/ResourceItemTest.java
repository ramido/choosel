package org.thechiselgroup.choosel.client.views;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.thechiselgroup.choosel.client.test.AdvancedAsserts.assertContentEquals;
import static org.thechiselgroup.choosel.client.test.TestResourceSetFactory.createResources;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.client.resources.DefaultResourceSet;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.ui.popup.PopupManager;
import org.thechiselgroup.choosel.client.views.ResourceItem.Status;

public class ResourceItemTest {

    private static final String RESOURCE_ITEM_CATEGORY = "resourceItemCategory";

    @Mock
    private HoverModel hoverModel;

    @Mock
    private ResourceItemValueResolver layer;

    @Mock
    private PopupManager popupManager;

    private ResourceSet resources;

    private ResourceItem underTest;

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
    public void isHighlightedIsFalseAfterInit() {
        assertEquals(false, underTest.isHighlighted());
    }

    @Test
    public void isSelectIsFalseAfterInit() {
        assertEquals(false, underTest.isSelected());
    }

    @Test
    public void setHighlightedNeverCallsUpdateStyling() {
        resources.addAll(createResources(1));
        underTest.addHighlightedResources(createResources(1));
        verify(underTest, never()).updateStyling();
    }

    @Test
    public void setSelectedWithChangeCallsUpdateStyling() {
        underTest.setSelected(true);
        verify(underTest, times(1)).updateStyling();
    }

    @Test
    public void setSelectedWithoutChangeNeverCallsUpdateStyling() {
        underTest.setSelected(false);
        verify(underTest, never()).updateStyling();
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        resources = new DefaultResourceSet();
        underTest = spy(new ResourceItem(RESOURCE_ITEM_CATEGORY, resources,
                hoverModel, popupManager, layer));
    }

    @Test
    public void statusIsDefault() {
        underTest.setSelected(false);
        assertEquals(Status.DEFAULT, underTest.calculateStatus());
    }

    @Test
    public void statusIsHighlighted() {
        resources.addAll(createResources(1));
        underTest.addHighlightedResources(createResources(1));
        underTest.setSelected(false);
        assertEquals(Status.HIGHLIGHTED, underTest.calculateStatus());
    }

    @Test
    public void statusIsHighlightedSelected() {
        resources.addAll(createResources(1));
        underTest.addHighlightedResources(createResources(1));
        underTest.setSelected(true);

        assertEquals(Status.HIGHLIGHTED_SELECTED, underTest.calculateStatus());
    }

    @Test
    public void statusIsNotHighlightedAfterRemovingHighlightedResources() {
        resources.addAll(createResources(1));
        underTest.addHighlightedResources(createResources(1));
        underTest.removeHighlightedResources(createResources(1));
        underTest.setSelected(false);
        assertEquals(Status.DEFAULT, underTest.calculateStatus());
    }

    @Test
    public void statusIsNotHighlightedOnEmptyAdd() {
        resources.addAll(createResources(1));
        underTest.addHighlightedResources(createResources(2));
        underTest.setSelected(false);
        assertEquals(Status.DEFAULT, underTest.calculateStatus());
    }

    @Test
    public void statusIsSelected() {
        underTest.setSelected(true);
        assertEquals(Status.SELECTED, underTest.calculateStatus());
    }

    @Test
    public void statusRemainsHighlightedAfterOneResourceIsRemovedFromHighlight() {
        resources.addAll(createResources(1, 2));
        underTest.addHighlightedResources(createResources(1, 2));
        underTest.removeHighlightedResources(createResources(1));
        underTest.setSelected(false);
        assertEquals(Status.HIGHLIGHTED, underTest.calculateStatus());
    }

    @Test
    public void statusVsGrayDefaultAllFalse() {
        underTest.setSelected(false);
        underTest.setSelectionStatusVisible(false);
        assertEquals(Status.DEFAULT,
                underTest.calculateStatusNormalVsGraySelection());
    }

    @Test
    public void statusVsGrayIsDefaultHighlightTrue() {
        underTest.setSelectionStatusVisible(true);
        underTest.setSelected(true);
        assertEquals(Status.DEFAULT,
                underTest.calculateStatusNormalVsGraySelection());
    }

    @Test
    public void statusVsGrayIsHighlighted() {
        resources.addAll(createResources(1));
        underTest.addHighlightedResources(createResources(1));
        assertEquals(Status.HIGHLIGHTED,
                underTest.calculateStatusNormalVsGraySelection());
    }

    @Test
    public void statusVsGreyIsGrayedOut() {
        underTest.setSelected(false);
        underTest.setSelectionStatusVisible(true);
        assertEquals(Status.GRAYED_OUT,
                underTest.calculateStatusNormalVsGraySelection());
    }

}
