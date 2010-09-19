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
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.thechiselgroup.choosel.client.test.AdvancedAsserts.assertContentEquals;
import static org.thechiselgroup.choosel.client.test.ResourcesTestHelper.emptyResourceItemSet;
import static org.thechiselgroup.choosel.client.test.ResourcesTestHelper.eqResourceItems;
import static org.thechiselgroup.choosel.client.test.ResourcesTestHelper.resourceItemsForResourceSets;
import static org.thechiselgroup.choosel.client.test.TestResourceSetFactory.TYPE_1;
import static org.thechiselgroup.choosel.client.test.TestResourceSetFactory.TYPE_2;
import static org.thechiselgroup.choosel.client.test.TestResourceSetFactory.createLabeledResources;
import static org.thechiselgroup.choosel.client.test.TestResourceSetFactory.createResource;
import static org.thechiselgroup.choosel.client.test.TestResourceSetFactory.createResources;
import static org.thechiselgroup.choosel.client.test.TestResourceSetFactory.toLabeledResources;
import static org.thechiselgroup.choosel.client.test.TestResourceSetFactory.toResourceSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.client.resolver.ResourceSetToValueResolver;
import org.thechiselgroup.choosel.client.resources.DefaultResourceSetFactory;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceByUriTypeCategorizer;
import org.thechiselgroup.choosel.client.resources.ResourceCategorizerToMultiCategorizerAdapter;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ResourceSplitter;
import org.thechiselgroup.choosel.client.resources.ResourcesAddedEvent;
import org.thechiselgroup.choosel.client.resources.ResourcesAddedEventHandler;
import org.thechiselgroup.choosel.client.resources.ResourcesRemovedEvent;
import org.thechiselgroup.choosel.client.resources.ResourcesRemovedEventHandler;
import org.thechiselgroup.choosel.client.resources.ui.DetailsWidgetHelper;
import org.thechiselgroup.choosel.client.ui.Presenter;
import org.thechiselgroup.choosel.client.ui.popup.PopupManager;
import org.thechiselgroup.choosel.client.ui.popup.PopupManagerFactory;

import com.google.gwt.event.shared.HandlerRegistration;

public class DefaultViewTest {

    public class TestView extends DefaultView {

        public TestView(ResourceSplitter resourceSplitter,
                ViewContentDisplay contentDisplay, String label,
                String contentType, ResourceItemValueResolver configuration,
                SelectionModel selectionModel,
                Presenter selectionModelPresenter, ResourceModel resourceModel,
                Presenter resourceModelPresenter, HoverModel hoverModel,
                PopupManagerFactory popupManagerFactory,
                DetailsWidgetHelper detailsWidgetHelper) {

            super(resourceSplitter, contentDisplay, label, contentType,
                    configuration, selectionModel, selectionModelPresenter,
                    resourceModel, resourceModelPresenter, hoverModel,
                    popupManagerFactory, detailsWidgetHelper);
        }

        @Override
        protected PopupManager createPopupManager(ResourceSet resources,
                ResourceSetToValueResolver resolver) {

            return popupManager;
        }

        @Override
        protected void initUI() {
        }
    }

    @Mock
    private ViewContentDisplay contentDisplay;

    @Mock
    private ResourceItemValueResolver resourceSetToValueResolver;

    @Mock
    private HandlerRegistration selectionAddedHandlerRegistration;

    @Mock
    private HandlerRegistration selectionRemovedHandlerRegistration;

    private DefaultView underTest;

    private ResourceModel resourceModel;

    @Mock
    private Presenter resourceModelPresenter;

    @Mock
    private SelectionModel selectionModel;

    @Mock
    private Presenter selectionModelPresenter;

    private HoverModel hoverModel;

    @Mock
    private PopupManager popupManager;

    // for future testing
    private ViewContentDisplayCallback callback;

    @Mock
    private DetailsWidgetHelper detailsWidgetHelper;

    @Mock
    private PopupManagerFactory popupManagerFactory;

    public Set<ResourceItem> captureAddedResourceItems() {
        ArgumentCaptor<Set> captor = ArgumentCaptor.forClass(Set.class);
        verify(contentDisplay, times(1)).update(captor.capture(),
                emptyResourceItemSet(), emptyResourceItemSet());
        return captor.getValue();
    }

    private ArrayList<ResourceItem> captureAddedResourceItemsAsList() {
        return new ArrayList<ResourceItem>(captureAddedResourceItems());
    }

    // TODO move to resource splitter test
    @Test
    public void categorizeLabeledResources() {
        ResourceSet resources1 = createLabeledResources(TYPE_1, 1, 3, 4);
        ResourceSet resources2 = createLabeledResources(TYPE_2, 4, 2);
        ResourceSet resources = toResourceSet(resources1, resources2);

        underTest.getResourceModel().addResourceSet(resources);
        Map<String, ResourceSet> result = underTest
                .getCategorizedResourceSets();

        assertEquals(2, result.size());
        assertTrue(result.containsKey(TYPE_1));
        assertTrue(result.get(TYPE_1).containsEqualResources(resources1));
        assertTrue(result.containsKey(TYPE_2));
        assertTrue(result.get(TYPE_2).containsEqualResources(resources2));
    }

    @Test
    public void createResourceItemsWhenLabeledResourcesAreAdded() {
        ResourceSet resources = createLabeledResources(TYPE_1, 1);

        resourceModel.addResourceSet(resources);

        resources.add(createResource(TYPE_2, 2));

        ArgumentCaptor<Set> captor = ArgumentCaptor.forClass(Set.class);
        verify(contentDisplay, times(2)).update(captor.capture(),
                emptyResourceItemSet(), emptyResourceItemSet());

        Set<ResourceItem> set1 = captor.getAllValues().get(0);
        assertEquals(1, set1.size());
        assertContentEquals(createResources(TYPE_1, 1),
                ((ResourceItem) set1.toArray()[0]).getResourceSet());

        Set<ResourceItem> set2 = captor.getAllValues().get(1);
        assertEquals(1, set2.size());
        assertContentEquals(createResources(TYPE_2, 2),
                ((ResourceItem) set2.toArray()[0]).getResourceSet());
    }

    private void createView() {
        DefaultResourceSetFactory resourceSetFactory = new DefaultResourceSetFactory();

        // TODO replace with mock
        resourceModel = new DefaultResourceModel(resourceSetFactory);
        hoverModel = new HoverModel();

        ResourceSplitter resourceSplitter = new ResourceSplitter(
                new ResourceCategorizerToMultiCategorizerAdapter(
                        new ResourceByUriTypeCategorizer()), resourceSetFactory);

        underTest = spy(new TestView(resourceSplitter, contentDisplay, "", "",
                resourceSetToValueResolver, selectionModel,
                selectionModelPresenter, resourceModel, resourceModelPresenter,
                hoverModel, popupManagerFactory, detailsWidgetHelper));
    }

    private void deselect(ResourceSet resources) {
        ArgumentCaptor<ResourcesRemovedEventHandler> captor = ArgumentCaptor
                .forClass(ResourcesRemovedEventHandler.class);
        verify(selectionModel, times(1)).addEventHandler(captor.capture());
        ResourcesRemovedEventHandler removedHandler = captor.getValue();
        removedHandler.onResourcesRemoved(new ResourcesRemovedEvent(
                createResources(), resources.toList()));
    }

    @Test
    public void deselectResourceItemWhenResourceRemovedFromSelection() {
        ResourceSet resources = createResources(1);

        resourceModel.addResources(resources);
        List<ResourceItem> resourceItems = captureAddedResourceItemsAsList();

        select(resources);
        deselect(resources);

        assertContentEquals(createResources(), resourceItems.get(0)
                .getSelectedResources());
    }

    @Test
    public void disposeContentDisplay() {
        underTest.dispose();

        verify(resourceModelPresenter, times(1)).dispose();
        verify(contentDisplay, times(1)).dispose();
    }

    @Ignore
    @Test
    public void disposeResourceItemsWhenViewDisposed() {
        // when(resourceItem.getResourceSet()).thenReturn(createResources(1));
        // resourceModel.addResources(createResources(1));
        //
        // underTest.dispose();
        //
        // verify(resourceItem, times(1)).dispose();
    }

    @Ignore
    @Test
    public void disposeResourceItemWhenResourceItemRemoved() {
        // ResourceSet resources = createLabeledResources(1);
        //
        // resourceModel.addResourceSet(resources);
        // resourceModel.removeResourceSet(resources);

        // verify(resourceItem, times(1)).dispose();
    }

    @Test
    public void disposeResourceModelPresenter() {
        underTest.dispose();

        verify(resourceModelPresenter, times(1)).dispose();
    }

    @Test
    public void disposeSelectionModelEventHandlers() {
        underTest.dispose();

        verify(selectionAddedHandlerRegistration, times(1)).removeHandler();
        verify(selectionRemovedHandlerRegistration, times(1)).removeHandler();
    }

    @Test
    public void disposeSelectionModelPresenter() {
        underTest.dispose();

        verify(selectionModelPresenter, times(1)).dispose();
    }

    @Test
    public void highlightedResourceSetOnCreatedResourceItems() {
        ResourceSet resources = createResources(TYPE_1, 1, 3, 4);

        hoverModel.setHighlightedResourceSet(resources);
        resourceModel.addResourceSet(resources);
        List<ResourceItem> resourceItems = captureAddedResourceItemsAsList();

        assertContentEquals(resources, resourceItems.get(0)
                .getHighlightedResources());
    }

    @Test
    public void highlightedResourcesGetAddedToResourceItemOnlyOnceWhenSeveralResourcesFromItemAddedToHoverModel() {
        ResourceSet resources = createResources(1, 2);

        resourceModel.addResourceSet(resources);
        List<ResourceItem> resourceItems = captureAddedResourceItemsAsList();

        hoverModel.setHighlightedResourceSet(resources);

        assertContentEquals(resources, resourceItems.get(0)
                .getHighlightedResources());
    }

    @Test
    public void highlightedResourcesGetAddedToResourceItemWhenHoverModelContainsAdditionalResources() {
        Resource resource2 = createResource(2);
        Resource resource1 = createResource(1);
        ResourceSet viewResources = toResourceSet(resource2);
        ResourceSet highlightedResources = toResourceSet(resource1, resource2);

        resourceModel.addResourceSet(viewResources);
        List<ResourceItem> resourceItems = captureAddedResourceItemsAsList();

        hoverModel.setHighlightedResourceSet(highlightedResources);

        assertContentEquals(viewResources, resourceItems.get(0)
                .getHighlightedResources());
    }

    @Test
    public void highlightedResourcesGetAddedToResourceItemWhenResourcesAddedToHoverModel() {
        ResourceSet resources = createResources(1);

        resourceModel.addResourceSet(resources);
        List<ResourceItem> resourceItems = captureAddedResourceItemsAsList();

        hoverModel.setHighlightedResourceSet(resources);

        assertContentEquals(createResources(1), resourceItems.get(0)
                .getHighlightedResources());
    }

    @Test
    public void highlightedResourcesGetRemovedFromResourceItemWhenResourcesRemovedFromHoverModel() {
        ResourceSet resources = createResources(1);

        resourceModel.addResourceSet(resources);
        List<ResourceItem> resourceItems = captureAddedResourceItemsAsList();

        hoverModel.setHighlightedResourceSet(resources);
        hoverModel.setHighlightedResourceSet(createResources());

        assertContentEquals(createResources(), resourceItems.get(0)
                .getHighlightedResources());
    }

    @Test
    public void initializesContentDisplay() {
        verify(contentDisplay, times(1)).init(
                any(ViewContentDisplayCallback.class));
    }

    private void select(ResourceSet selectedResources) {
        ArgumentCaptor<ResourcesAddedEventHandler> captor = ArgumentCaptor
                .forClass(ResourcesAddedEventHandler.class);
        verify(selectionModel, times(1)).addEventHandler(captor.capture());
        ResourcesAddedEventHandler addedHandler = captor.getValue();
        addedHandler.onResourcesAdded(new ResourcesAddedEvent(
                selectedResources, selectedResources.toList()));
    }

    @Test
    public void selectResourceItemWhenResourceAddedToSelection() {
        ResourceSet resources = createResources(1);

        resourceModel.addResources(resources);
        List<ResourceItem> resourceItems = captureAddedResourceItemsAsList();

        select(createResources(1));

        assertContentEquals(createResources(1), resourceItems.get(0)
                .getSelectedResources());
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        createView();

        when(
                selectionModel
                        .addEventHandler(any(ResourcesAddedEventHandler.class)))
                .thenReturn(selectionAddedHandlerRegistration);
        when(
                selectionModel
                        .addEventHandler(any(ResourcesRemovedEventHandler.class)))
                .thenReturn(selectionRemovedHandlerRegistration);

        when(contentDisplay.getSlotIDs()).thenReturn(new String[0]);

        when(contentDisplay.isReady()).thenReturn(true);

        underTest.init();

        ArgumentCaptor<ViewContentDisplayCallback> captor = ArgumentCaptor
                .forClass(ViewContentDisplayCallback.class);
        verify(contentDisplay).init(captor.capture());
        callback = captor.getValue();
    }

    // TODO check highlighted resources in resource item
    @Test
    public void updateCalledOnHoverModelChange() {
        ResourceSet highlightedResources = createResources(1);

        resourceModel.addResourceSet(highlightedResources);
        Set<ResourceItem> addedResourceItems = captureAddedResourceItems();

        hoverModel.setHighlightedResourceSet(highlightedResources);

        verify(contentDisplay, times(1)).update(emptyResourceItemSet(),
                eqResourceItems(addedResourceItems), emptyResourceItemSet());
    }

    @Test
    public void updateCalledWhenResourcesRemoved() {
        ResourceSet resources1 = createResources(TYPE_1, 1);
        ResourceSet resources2 = createResources(TYPE_2, 2);
        ResourceSet resources = toLabeledResources(resources1, resources2);

        resourceModel.addResourceSet(resources);
        Set<ResourceItem> addedResourceItems = captureAddedResourceItems();

        underTest.getResourceModel().removeResourceSet(resources);
        verify(contentDisplay, times(1)).update(emptyResourceItemSet(),
                emptyResourceItemSet(), eqResourceItems(addedResourceItems));
    }

    @Test
    public void updateCalledWhenSelectionChanges() {
        ResourceSet resources = createResources(1);

        resourceModel.addResources(resources);
        Set<ResourceItem> addedResourceItems = captureAddedResourceItems();

        select(createResources(1));

        verify(contentDisplay, times(1)).update(emptyResourceItemSet(),
                eqResourceItems(addedResourceItems), emptyResourceItemSet());
    }

    @Test
    public void updateCalledWith2ResourceItemsWhenAddingMixedResourceSet() {
        ResourceSet resources1 = createResources(TYPE_1, 1, 3, 4);
        ResourceSet resources2 = createResources(TYPE_2, 4, 2);
        ResourceSet resources = toResourceSet(resources1, resources2);

        resourceModel.addResourceSet(resources);

        verify(contentDisplay, times(1)).update(
                resourceItemsForResourceSets(resources1, resources2),
                emptyResourceItemSet(), emptyResourceItemSet());
    }

    @Test
    public void updateNeverCalledOnHoverModelChangeThatDoesNotAffectViewResources() {
        resourceModel.addResourceSet(createResources(2));
        hoverModel.setHighlightedResourceSet(createResources(1));

        verify(contentDisplay, never()).update(emptyResourceItemSet(),
                any(Set.class), emptyResourceItemSet());
    }

}
