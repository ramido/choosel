package org.thechiselgroup.choosel.client.views;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.client.resources.DefaultResourceSet;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.test.ResourcesTestHelper;
import org.thechiselgroup.choosel.client.ui.popup.PopupManager;

public class ResourceItemUpdateTest {

    private static final String RESOURCE_ITEM_CATEGORY = "resourceItemCategory";

    @Mock
    private PopupManager popupManager;

    @Mock
    private ResourceItemValueResolver layer;

    @Mock
    private ResourceSet hoverModel;

    private ResourceSet resources;

    private ResourceItem underTest;

    private int updateCalled;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        resources = new DefaultResourceSet();
        updateCalled = 0;
        underTest = new ResourceItem(RESOURCE_ITEM_CATEGORY, resources,
                hoverModel, popupManager, layer) {
            @Override
            protected void updateContent() {
                updateCalled++;
            }
        };
    }

    @Test
    public void updateResourceItemOnCreation() {
        assertEquals(1, updateCalled);
    }

    @Test
    public void updateResourceItemWhenResourcesAreAddedToUnderlyingResourceSet() {
        updateCalled = 0;
        resources.add(ResourcesTestHelper.createResource(1));

        assertEquals(1, updateCalled);
    }

    @Test
    public void updateResourceItemWhenResourcesAreRemovedFromUnderlyingResourceSet() {
        resources.add(ResourcesTestHelper.createResource(1));
        updateCalled = 0;
        resources.remove(ResourcesTestHelper.createResource(1));

        assertEquals(1, updateCalled);
    }

}
