package org.thechiselgroup.choosel.client.views;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.ui.popup.PopupManager;
import org.thechiselgroup.choosel.client.views.ResourceItem.Status;

public class ResourceItemTest {

    private static final String RESOURCE_ITEM_CATEGORY = "resourceItemCategory";

    public static Resource createResource(String type, int index) {
        Resource r = new Resource(type + ":" + index);
        r.putValue("testlabelkey", index + "-value");
        return r;
    }

    @Mock
    private HoverModel hoverModel;

    @Mock
    private ResourceItemValueResolver layer;

    @Mock
    private PopupManager popupManager;

    @Mock
    private ResourceSet resources;

    private ResourceItem underTest;

    @Test
    public void isHighlightedIsFalseAfterInit() {
        assertEquals(false, underTest.isHighlighted());
    }

    @Test
    public void isSelectIsFalseAfterInit() {
        assertEquals(false, underTest.isSelected());
    }

    @Test
    public void setHighlightedUpdateStylingCalled() {
        underTest.setHighlighted(true);
        verify(underTest, times(1)).updateStyling();
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
        underTest.setHighlighted(true);
        underTest.setSelected(false);
        assertEquals(Status.HIGHLIGHTED, underTest.calculateStatus());
    }

    @Test
    public void statusIsHighlightedSelected() {
        underTest.setHighlighted(true);
        underTest.setSelected(true);

        assertEquals(Status.HIGHLIGHTED_SELECTED, underTest.calculateStatus());
    }

    @Test
    public void statusIsSelected() {
        underTest.setSelected(true);
        assertEquals(Status.SELECTED, underTest.calculateStatus());
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
        underTest.setHighlighted(true);
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
