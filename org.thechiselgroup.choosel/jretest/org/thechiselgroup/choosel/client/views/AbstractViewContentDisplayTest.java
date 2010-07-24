package org.thechiselgroup.choosel.client.views;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.thechiselgroup.choosel.client.test.TestResourceSetFactory.createResource;
import static org.thechiselgroup.choosel.client.test.TestResourceSetFactory.toResourceSet;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.client.persistence.Memento;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ui.DetailsWidgetHelper;
import org.thechiselgroup.choosel.client.ui.popup.PopupManagerFactory;
import org.thechiselgroup.choosel.client.util.CollectionUtils;

import com.google.gwt.user.client.ui.Widget;

public class AbstractViewContentDisplayTest {

    private AbstractViewContentDisplay underTest;

    private HoverModel hoverModel;

    @Mock
    private PopupManagerFactory popupManagerFactory;

    @Mock
    private DetailsWidgetHelper detailsWidgetHelper;

    @Mock
    private ViewContentDisplayCallback callback;

    @Mock
    private ResourceItem resourceItem;

    @Test
    public void correctHoverShownIfOneHoveredResourceIsNotContainedInView() {
        Resource resource1 = createResource(1);
        Resource resource2 = createResource(2);

        when(callback.containsResource(resource1)).thenReturn(false);
        when(callback.containsResource(resource2)).thenReturn(true);
        when(callback.getResourceItems(resource2)).thenReturn(
                CollectionUtils.toList(resourceItem));

        hoverModel
                .setHighlightedResourceSet(toResourceSet(resource1, resource2));

        verify(resourceItem, times(1)).setHighlighted(true);
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        hoverModel = new HoverModel();
        underTest = new AbstractViewContentDisplay(popupManagerFactory,
                detailsWidgetHelper, hoverModel) {

            @Override
            public ResourceItem createResourceItem(
                    ResourceItemValueResolver resolver, String category,
                    ResourceSet resources) {
                return null;
            }

            @Override
            protected Widget createWidget() {
                return null;
            }

            @Override
            public String[] getSlotIDs() {
                return new String[0];
            }

            @Override
            public void removeResourceItem(ResourceItem resourceItem) {
            }

            @Override
            public void restore(Memento state) {
            }

            @Override
            public Memento save() {
                return null;
            }
        };

        underTest.init(callback);
    }

}