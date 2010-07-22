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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.thechiselgroup.choosel.client.test.TestResourceSetFactory.createLabeledResources;
import static org.thechiselgroup.choosel.client.test.TestResourceSetFactory.createResource;
import static org.thechiselgroup.choosel.client.test.TestResourceSetFactory.createResources;
import static org.thechiselgroup.choosel.client.test.TestResourceSetFactory.toLabeledResources;
import static org.thechiselgroup.choosel.client.test.TestResourceSetFactory.toResourceSet;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.client.label.LabelProvider;
import org.thechiselgroup.choosel.client.label.SelectionModelLabelFactory;
import org.thechiselgroup.choosel.client.persistence.Memento;
import org.thechiselgroup.choosel.client.resources.DefaultResourceSet;
import org.thechiselgroup.choosel.client.resources.DefaultResourceSetFactory;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceByUriTypeCategorizer;
import org.thechiselgroup.choosel.client.resources.ResourceCategorizerToMultiCategorizerAdapter;
import org.thechiselgroup.choosel.client.resources.ResourceEventHandler;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ResourceSetFactory;
import org.thechiselgroup.choosel.client.resources.ResourceSplitter;
import org.thechiselgroup.choosel.client.resources.ResourcesAddedEvent;
import org.thechiselgroup.choosel.client.resources.ResourcesAddedEventHandler;
import org.thechiselgroup.choosel.client.resources.ResourcesRemovedEvent;
import org.thechiselgroup.choosel.client.resources.ResourcesRemovedEventHandler;
import org.thechiselgroup.choosel.client.resources.persistence.ResourceSetAccessor;
import org.thechiselgroup.choosel.client.resources.ui.ResourceSetsPresenter;
import org.thechiselgroup.choosel.client.test.ResourcesTestHelper;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

public class DefaultViewTest {

    public static class TestView extends DefaultView {

        public TestView(LabelProvider selectionModelLabelFactory,
                ResourceSetFactory resourceSetFactory,
                ResourceSetsPresenter userSetsPresenter,
                ResourceSetsPresenter allResourcesSetPresenter,
                ResourceSetsPresenter selectionPresenter,
                ResourceSetsPresenter selectionDropPresenter,
                ResourceSplitter resourceSplitter,
                ViewContentDisplay contentDisplay, String label,
                String contentType, ResourceItemValueResolver configuration) {

            super(selectionModelLabelFactory, resourceSetFactory,
                    userSetsPresenter, allResourcesSetPresenter,
                    selectionPresenter, selectionDropPresenter,
                    resourceSplitter, contentDisplay, label, contentType,
                    configuration);
        }

        @Override
        protected void initUI() {
        }
    }

    private static final String CATEGORY_1 = "type-1";

    private static final String CATEGORY_2 = "type-2";

    private static final String SLOT_ID = "slot-id";

    @Mock
    private ResourceSetsPresenter allResourcesSetPresenter;

    @Mock
    private ViewContentDisplay contentDisplay;

    @Mock
    private ResourceSetsPresenter originalSetsPresenter;

    @Mock
    private ResourceItem resourceItem;

    @Mock
    private ResourceItemValueResolver resourceSetToValueResolver;

    @Mock
    private ResourceSet selection;

    @Mock
    private ResourceSetsPresenter selectionDropPresenter;

    @Mock
    private HandlerRegistration selectionHandlerRegistration;

    @Mock
    private ResourceSetsPresenter selectionPresenter;

    private DefaultView view;

    @Test
    public void addingUnlabeledSetDoesNotChangeOriginalSetsPresenter() {
        Resource resource = createResource(1);
        DefaultResourceSet resources = new DefaultResourceSet();
        resources.add(resource);

        view.addResourceSet(resources);

        verify(originalSetsPresenter, never()).addResourceSet(resources);
    }

    @Test
    public void addResourcesAddsToAllResources() {
        ResourceSet resources1 = createResources("test", 1, 2, 3);
        ResourceSet resources2 = createResources("test", 3, 4, 5);

        view.addResources(resources1);
        view.addResources(resources2);
        ResourceSet allResources = view.getResources();

        assertEquals(5, allResources.size());
        for (Resource resource : resources1) {
            assertTrue(allResources.contains(resource));
        }
        for (Resource resource : resources2) {
            assertTrue(allResources.contains(resource));
        }
    }

    @Test
    public void addResourceSetsAddsToAllResources() {
        ResourceSet resources1 = createResources("test", 1, 2, 3);
        ResourceSet resources2 = createResources("test", 3, 4, 5);

        view.addResourceSet(resources1);
        view.addResourceSet(resources2);
        ResourceSet allResources = view.getResources();

        assertEquals(5, allResources.size());
        for (Resource resource : resources1) {
            assertTrue(allResources.contains(resource));
        }
        for (Resource resource : resources2) {
            assertTrue(allResources.contains(resource));
        }
    }

    @Test
    public void addSelectionHandlers() {
        view.setSelection(selection);

        verify(selection, times(1)).addHandler(eq(ResourcesAddedEvent.TYPE),
                any(ResourcesAddedEventHandler.class));
        verify(selection, times(1)).addHandler(eq(ResourcesRemovedEvent.TYPE),
                any(ResourcesRemovedEventHandler.class));
    }

    @Test
    public void addUnlabeledSetContentsToAutomaticResourceSet() {
        Resource resource = createResource(1);
        DefaultResourceSet resources = new DefaultResourceSet();
        resources.add(resource);

        view.addResourceSet(resources);

        ArgumentCaptor<ViewContentDisplayCallback> argument = ArgumentCaptor
                .forClass(ViewContentDisplayCallback.class);
        verify(contentDisplay, times(1)).init(argument.capture());
        ResourceSet automaticResources = argument.getValue()
                .getAutomaticResourceSet();

        assertNotNull(automaticResources);
        assertEquals(true, automaticResources.contains(resource));
    }

    @Test
    public void allResourcesHasLabel() {
        assertEquals(true, view.getResources().hasLabel());
    }

    @Test
    public void allResourcesPresenterContainsSetWithAllResources() {
        view.addResourceSet(createLabeledResources(1));
        view.addResources(createResources(2));

        ArgumentCaptor<ResourceSet> argument = ArgumentCaptor
                .forClass(ResourceSet.class);
        verify(allResourcesSetPresenter, times(1)).addResourceSet(
                argument.capture());

        assertEquals(true, argument.getValue().contains(createResource(1)));
        assertEquals(true, argument.getValue().contains(createResource(2)));
        assertEquals(2, argument.getValue().size());
    }

    @Test
    public void callOriginalSetsPresenterOnLabeledResourcesAdded() {
        ResourceSet resources = createLabeledResources(1, 2, 3);

        view.addResourceSet(resources);

        verify(originalSetsPresenter, times(1)).addResourceSet(resources);
    }

    @Test
    public void callOriginalSetsPresenterOnLabeledResourcesAddedOnlyOnce() {
        ResourceSet resources = createLabeledResources(1, 2, 3);

        view.addResourceSet(resources);
        view.addResourceSet(resources);

        verify(originalSetsPresenter, times(1)).addResourceSet(resources);
    }

    @Test
    public void callResourceSetsPresenterOnLabeledResourcesRemoved() {
        ResourceSet resources = createLabeledResources(1, 2, 3);

        view.addResourceSet(resources);
        view.removeResourceSet(resources);

        verify(originalSetsPresenter, times(1)).removeResourceSet(resources);
    }

    @Test
    public void categorizeLabeledResources() {
        DefaultResourceSet resources1 = createLabeledResources(CATEGORY_1, 1,
                3, 4);
        DefaultResourceSet resources2 = createLabeledResources(CATEGORY_2, 4, 2);
        DefaultResourceSet resources = toResourceSet(resources1, resources2);

        view.addResourceSet(resources);
        Map<String, ResourceSet> result = view.getCategorizedResourceSets();

        assertEquals(2, result.size());
        assertTrue(result.containsKey(CATEGORY_1));
        assertTrue(result.get(CATEGORY_1).containsEqualResources(resources1));
        assertTrue(result.containsKey(CATEGORY_2));
        assertTrue(result.get(CATEGORY_2).containsEqualResources(resources2));
    }

    @Test
    public void containsAddedLabeledResources() {
        ResourceSet resources = createLabeledResources(1, 2, 3);

        view.addResourceSet(resources);

        assertEquals(true, view.containsResourceSet(resources));
    }

    @Test
    public void containsAddedResources() {
        ResourceSet resources = createResources(1, 2, 3);

        view.addResources(resources);

        assertEquals(true, view.containsResources(resources));
    }

    @Test
    public void createResourceItemsOnResourcesAdded() {
        DefaultResourceSet resources1 = createResources(CATEGORY_1, 1, 3, 4);
        DefaultResourceSet resources2 = createResources(CATEGORY_2, 4, 2);
        DefaultResourceSet resources = toResourceSet(resources1, resources2);

        view.addResourceSet(resources);

        ArgumentCaptor<ResourceSet> argument = ArgumentCaptor
                .forClass(ResourceSet.class);
        verify(contentDisplay, times(2)).createResourceItem(
                any(ResourceItemValueResolver.class), any(String.class),
                argument.capture());

        List<ResourceSet> values = argument.getAllValues();

        assertEquals(2, values.size());
        for (int i = 0; i < 2; i++) {
            ResourceSet resourceSet = values.get(i);

            if (resourceSet.size() == 3) {
                ResourcesTestHelper.assertContainsResource(true, resourceSet,
                        CATEGORY_1, 1);
                ResourcesTestHelper.assertContainsResource(true, resourceSet,
                        CATEGORY_1, 3);
                ResourcesTestHelper.assertContainsResource(true, resourceSet,
                        CATEGORY_1, 4);
            } else if (resourceSet.size() == 2) {
                ResourcesTestHelper.assertContainsResource(true, resourceSet,
                        CATEGORY_2, 4);
                ResourcesTestHelper.assertContainsResource(true, resourceSet,
                        CATEGORY_2, 2);
            } else {
                fail("invalid resource set " + resourceSet);
            }
        }
    }

    @Test
    public void createResourceItemsWhenLabeledResourcesAreAdded() {
        ResourceSet resources = createLabeledResources(CATEGORY_1, 1);

        view.addResourceSet(resources);

        resources.add(createResource(CATEGORY_2, 2));

        ArgumentCaptor<ResourceSet> captor = ArgumentCaptor
                .forClass(ResourceSet.class);
        verify(contentDisplay, times(2)).createResourceItem(
                any(ResourceItemValueResolver.class), any(String.class),
                captor.capture());

        List<ResourceSet> capturedResourceSets = captor.getAllValues();
        for (ResourceSet capturedResourceSet : capturedResourceSets) {
            assertEquals(1, capturedResourceSet.size());
        }

        ResourceSet unionSet = toResourceSet(capturedResourceSets
                .toArray(new ResourceSet[capturedResourceSets.size()]));

        assertTrue(unionSet.containsAll(resources));
    }

    private void createView() {
        ResourceSplitter resourceSplitter = new ResourceSplitter(
                new ResourceCategorizerToMultiCategorizerAdapter(
                        new ResourceByUriTypeCategorizer()),
                new DefaultResourceSetFactory());

        view = spy(new TestView(new SelectionModelLabelFactory(),
                new DefaultResourceSetFactory(), originalSetsPresenter,
                allResourcesSetPresenter, selectionPresenter,
                selectionDropPresenter, resourceSplitter, contentDisplay, "",
                "", resourceSetToValueResolver));
    }

    @Test
    public void dispose() {
        view.setSelection(selection);

        view.dispose();

        verify(contentDisplay, times(1)).dispose();
        verify(selectionPresenter, times(1)).dispose();
        verify(originalSetsPresenter, times(1)).dispose();
        verify(selectionHandlerRegistration, times(2)).removeHandler();
    }

    @Test
    public void disposeShouldRemoveResourceHooks() {
        DefaultResourceSet resources = createResources(3, 4);

        view.addResourceSet(resources);
        view.dispose();

        assertEquals(0, resources.getHandlerCount(ResourcesAddedEvent.TYPE));
        assertEquals(0, resources.getHandlerCount(ResourcesRemovedEvent.TYPE));
    }

    @Test
    public void doesNotContainResourceSetAfterAddingResources() {
        ResourceSet resources = createResources(1, 2, 3);
        resources.setLabel("test");

        view.addResources(resources);

        assertEquals(false, view.containsResourceSet(resources));
    }

    @Test
    public void doNotCallOriginalSetsPresenterOnAddingUnlabeledResources() {
        Resource resource = createResource(1);
        DefaultResourceSet resources = new DefaultResourceSet();
        resources.add(resource);

        view.addResourceSet(resources);

        verify(originalSetsPresenter, never()).addResourceSet(
                any(ResourceSet.class));
    }

    @Test
    public void initializesContentDisplay() {
        verify(contentDisplay, times(1)).init(
                any(ViewContentDisplayCallback.class));
    }

    @Test
    public void removeLabeledResourceSetDoesNotRemoveDuplicateResources() {
        ResourceSet resources1 = createLabeledResources(1, 2, 3);
        ResourceSet resources2 = createLabeledResources(3, 4, 5);

        view.addResourceSet(resources1);
        view.addResourceSet(resources2);
        view.removeResourceSet(resources2);
        ResourceSet allResources = view.getResources();

        assertEquals(3, allResources.size());
        for (Resource resource : resources1) {
            assertTrue(allResources.contains(resource));
        }
    }

    @Test
    public void removeLayersOnResourceSetRemoved() {
        DefaultResourceSet resources1 = createResources(CATEGORY_1, 1, 3, 4);
        DefaultResourceSet resources2 = createResources(CATEGORY_2, 4, 2);
        DefaultResourceSet resources = toLabeledResources(resources1,
                resources2);

        view.addResourceSet(resources);
        view.removeResourceSet(resources);

        verify(contentDisplay, times(2)).removeResourceItem(
                any(ResourceItem.class));
    }

    @Test
    public void removeResourcesDoesRemoveDuplicateResources() {
        ResourceSet resources1 = createResources(1, 2, 3);
        ResourceSet resources2 = createResources(3, 4, 5);

        view.addResources(resources1);
        view.addResources(resources2);
        view.removeResources(resources2);
        ResourceSet allResources = view.getResources();

        assertEquals(2, allResources.size());
        assertEquals(true, allResources.contains(createResource(1)));
        assertEquals(true, allResources.contains(createResource(2)));
        assertEquals(false, allResources.contains(createResource(3)));
        assertEquals(false, allResources.contains(createResource(4)));
        assertEquals(false, allResources.contains(createResource(5)));
    }

    @Test
    public void restoreFromMementoAddsAutomaticResourcesToAllResources() {
        Memento state = new Memento();

        state.setValue(DefaultView.MEMENTO_AUTOMATIC_RESOURCES, 0);
        state.setValue(DefaultView.MEMENTO_SELECTION, 1);
        state.setValue(DefaultView.MEMENTO_RESOURCE_SET_COUNT, 0);
        state.setValue(DefaultView.MEMENTO_SELECTION_SET_COUNT, 1);
        state.setValue(DefaultView.MEMENTO_SELECTION_SET_PREFIX + 0, 1);

        ResourceSetAccessor accessor = mock(ResourceSetAccessor.class);
        when(accessor.getResourceSet(0)).thenReturn(createResources(1));
        when(accessor.getResourceSet(1)).thenReturn(createResources());

        view.restore(state, accessor);

        assertEquals(true, view.getResources().contains(createResource(1)));
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        createView();

        when(
                contentDisplay.createResourceItem(
                        any(ResourceItemValueResolver.class),
                        any(String.class), any(ResourceSet.class))).thenReturn(
                resourceItem);

        when(contentDisplay.getSlotIDs()).thenReturn(new String[] { SLOT_ID },
                new String[] {});

        when(
                selection.addHandler(any(GwtEvent.Type.class),
                        any(ResourceEventHandler.class))).thenReturn(
                selectionHandlerRegistration);
        when(contentDisplay.isReady()).thenReturn(true);

        view.init();
    }
}
