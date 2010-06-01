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

    @Mock
    private PopupManager popupManager;
    @Mock
    private Layer layer;
    @Mock
    private ResourceSet hoverModel;
    @Mock
    private Resource resource;

    private ResourceItem underTest;

    @Before
    public void setUp() {
	MockitoAnnotations.initMocks(this);

	underTest = spy(new ResourceItem(resource, hoverModel, popupManager,
		layer));
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
    public void statusIsHighlightedSelected() {
	underTest.setHightlighted(true);
	underTest.setSelected(true);

	assertEquals(Status.HIGHLIGHTED_SELECTED, underTest.calculateStatus());
    }

    @Test
    public void statusisHighlighted() {
	underTest.setHightlighted(true);
	underTest.setSelected(false);
	assertEquals(Status.HIGHLIGHTED, underTest.calculateStatus());
    }

    @Test
    public void statusisSelected() {
	underTest.setSelected(true);
	assertEquals(Status.SELECTED, underTest.calculateStatus());
    }

    @Test
    public void statusisDefault() {
	underTest.setSelected(false);
	assertEquals(Status.DEFAULT, underTest.calculateStatus());
    }

    @Test
    public void statusVsGrayIsHighlighted() {
	underTest.setHightlighted(true);
	assertEquals(Status.HIGHLIGHTED, underTest
		.calculateStatusNormalVsGraySelection());
    }

    @Test
    public void statusVsGrayIsDefaultHighlightTrue() {
	underTest.setSelectionStatusVisible(true);
	underTest.setSelected(true);
	assertEquals(Status.DEFAULT, underTest
		.calculateStatusNormalVsGraySelection());
    }

    @Test
    public void statusVsGreyIsGrayedOut() {
	underTest.setSelected(false);
	underTest.setSelectionStatusVisible(true);
	assertEquals(Status.GRAYED_OUT, underTest
		.calculateStatusNormalVsGraySelection());
    }

    @Test
    public void statusVsGrayDefaultAllFalse() {
	underTest.setSelected(false);
	underTest.setSelectionStatusVisible(false);
	assertEquals(Status.DEFAULT, underTest
		.calculateStatusNormalVsGraySelection());
    }

//    @Test
//    public void setHighlightedAddsResourceCalled() {
//	underTest.setHightlighted(true);
//	verify(hoverModel).add(underTest.getResource());
//    }
//
//    @Test
//    public void setHighlightedRemovedResourceCalled() {
//	underTest.setHightlighted(true);
//	verify(hoverModel, never()).remove(underTest.getResource());
//	underTest.setHightlighted(false);
//	verify(hoverModel, times(1)).remove(underTest.getResource());
//    }

    @Test
    public void setHightlightedUpdateStylingCalled() {
	underTest.setHightlighted(true);
	verify(underTest, times(1)).updateStyling();
    }

    @Test
    public void setSelectedWithoutChangeNeverCallsUpdateStyling() {
	underTest.setSelected(false);
	verify(underTest, never()).updateStyling();
    }

    @Test
    public void setSelectedWithChangeCallsUpdateStyling() {
	underTest.setSelected(true);
	verify(underTest, times(1)).updateStyling();
    }

    public static Resource createResource(String type, int index) {
	Resource r = new Resource(type + ":" + index);
	r.putValue("testlabelkey", index + "-value");
	return r;
    }

}
