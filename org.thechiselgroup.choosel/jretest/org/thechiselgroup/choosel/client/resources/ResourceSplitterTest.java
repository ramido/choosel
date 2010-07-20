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
package org.thechiselgroup.choosel.client.resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.thechiselgroup.choosel.client.test.TestResourceSetFactory.createResource;
import static org.thechiselgroup.choosel.client.test.TestResourceSetFactory.createResources;
import static org.thechiselgroup.choosel.client.test.TestResourceSetFactory.toResourceSet;
import static org.thechiselgroup.choosel.client.util.CollectionUtils.toSet;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.client.label.CategoryLabelProvider;
import org.thechiselgroup.choosel.client.label.DefaultCategoryLabelProvider;
import org.thechiselgroup.choosel.client.resources.DefaultResourceSet;
import org.thechiselgroup.choosel.client.resources.DefaultResourceSetFactory;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceCategoryAddedEvent;
import org.thechiselgroup.choosel.client.resources.ResourceCategoryAddedEventHandler;
import org.thechiselgroup.choosel.client.resources.ResourceCategoryRemovedEvent;
import org.thechiselgroup.choosel.client.resources.ResourceCategoryRemovedEventHandler;
import org.thechiselgroup.choosel.client.resources.ResourceMultiCategorizer;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ResourceSplitter;
import org.thechiselgroup.choosel.client.resources.ResourcesRemovedEvent;
import org.thechiselgroup.choosel.client.resources.ResourcesRemovedEventHandler;

public class ResourceSplitterTest {

    private static final String TEST_CATEGORY = "test";

    public static final String CATEGORY_1 = "category1";

    public static final String CATEGORY_2 = "category2";

    @Mock
    private ResourceCategoryRemovedEventHandler removedHandler;

    private DefaultResourceSet resources1;

    private DefaultResourceSet resources2;

    private ResourceSplitter splitter;

    private CategoryLabelProvider labelProvider;

    @Mock
    private ResourceMultiCategorizer categorizer;

    @Test
    public void addResourceWithMultipleCategoriesCreatesMultipleCategories() {
        Resource resource = createResource(1);

        when(categorizer.getCategories(resource)).thenReturn(
                toSet(CATEGORY_1, CATEGORY_2));

        splitter.add(resource);

        Map<String, ResourceSet> result = splitter.getCategorizedResourceSets();

        assertEquals(2, result.size());
        assertTrue(result.containsKey(CATEGORY_1));
        assertTrue(result.get(CATEGORY_1).containsEqualResources(
                toResourceSet(resource)));
        assertTrue(result.containsKey(CATEGORY_2));
        assertTrue(result.get(CATEGORY_2).containsEqualResources(
                toResourceSet(resource)));
    }

    @Test
    public void createCategories() {
        splitter.addAll(resources1);
        splitter.addAll(resources2);

        Map<String, ResourceSet> result = splitter.getCategorizedResourceSets();

        assertEquals(2, result.size());
        assertTrue(result.containsKey(CATEGORY_1));
        assertTrue(result.get(CATEGORY_1).containsEqualResources(resources1));
        assertTrue(result.containsKey(CATEGORY_2));
        assertTrue(result.get(CATEGORY_2).containsEqualResources(resources2));
    }

    /**
     * Tests that the resource category added event is fired and contains all
     * resources when fired (and not just later on).
     */
    @Test
    public void fireResourceCategoryAddedEventOnAdd() {
        final boolean[] called = { false };

        splitter.addHandler(ResourceCategoryAddedEvent.TYPE,
                new ResourceCategoryAddedEventHandler() {
                    @Override
                    public void onResourceCategoryAdded(
                            ResourceCategoryAddedEvent e) {

                        assertEquals(CATEGORY_1, e.getCategory());
                        assertEquals(
                                true,
                                e.getResourceSet().containsEqualResources(
                                        createResources(TEST_CATEGORY, 1)));

                        called[0] = true;
                    }
                });

        splitter.add(createResource(TEST_CATEGORY, 1));

        assertEquals(true, called[0]);
    }

    /**
     * Tests that the resource category added event is fired and contains the
     * resource when fired (and not just later on).
     */
    @Test
    public void fireResourceCategoryAddedEventOnAddAll() {
        final boolean[] called = { false };

        splitter.addHandler(ResourceCategoryAddedEvent.TYPE,
                new ResourceCategoryAddedEventHandler() {
                    @Override
                    public void onResourceCategoryAdded(
                            ResourceCategoryAddedEvent e) {

                        assertEquals(CATEGORY_1, e.getCategory());
                        assertEquals(true, e.getResourceSet()
                                .containsEqualResources(resources1));

                        called[0] = true;
                    }
                });

        splitter.addAll(resources1);

        assertEquals(true, called[0]);
    }

    // TODO test for add all --> multiple categories

    // TODO test for remove all --> multiple categories

    @Test
    public void fireResourceCategoryRemovedEventOnRemove() {
        splitter.addHandler(ResourceCategoryRemovedEvent.TYPE, removedHandler);
        splitter.add(createResource(TEST_CATEGORY, 1));
        splitter.remove(createResource(TEST_CATEGORY, 1));

        ArgumentCaptor<ResourceCategoryRemovedEvent> eventCaptor = ArgumentCaptor
                .forClass(ResourceCategoryRemovedEvent.class);
        verify(removedHandler, times(1)).onResourceCategoryRemoved(
                eventCaptor.capture());
        ResourceCategoryRemovedEvent event = eventCaptor.getValue();
        assertEquals(CATEGORY_1, event.getCategory());
        assertEquals(
                true,
                event.getResourceSet().contains(
                        createResource(TEST_CATEGORY, 1)));
    }

    @Test
    public void fireResourceCategoryRemovedEventOnRemoveAll() {
        splitter.addHandler(ResourceCategoryRemovedEvent.TYPE, removedHandler);
        splitter.addAll(resources1);
        splitter.removeAll(resources1);

        ArgumentCaptor<ResourceCategoryRemovedEvent> eventCaptor = ArgumentCaptor
                .forClass(ResourceCategoryRemovedEvent.class);
        verify(removedHandler, times(1)).onResourceCategoryRemoved(
                eventCaptor.capture());
        ResourceCategoryRemovedEvent event = eventCaptor.getValue();
        assertEquals(CATEGORY_1, event.getCategory());
        assertEquals(true, event.getResourceSet().containsAll(resources1));
    }

    @Test
    public void noResourceSetEventsFiredOnCompleteCategoryRemovalViaRemove() {
        splitter.add(createResource(TEST_CATEGORY, 1));
        ResourceSet categorizedResources = splitter
                .getCategorizedResourceSets().get(CATEGORY_1);
        ResourcesRemovedEventHandler resourcesRemovedHandler = mock(ResourcesRemovedEventHandler.class);
        categorizedResources.addHandler(ResourcesRemovedEvent.TYPE,
                resourcesRemovedHandler);
        splitter.remove(createResource(TEST_CATEGORY, 1));

        verify(resourcesRemovedHandler, never()).onResourcesRemoved(
                any(ResourcesRemovedEvent.class));
    }

    @Test
    public void noResourceSetEventsFiredOnCompleteCategoryRemovalViaRemoveAll() {
        splitter.addAll(resources1);
        ResourceSet categorizedResources = splitter
                .getCategorizedResourceSets().get(CATEGORY_1);
        ResourcesRemovedEventHandler resourcesRemovedHandler = mock(ResourcesRemovedEventHandler.class);
        categorizedResources.addHandler(ResourcesRemovedEvent.TYPE,
                resourcesRemovedHandler);
        splitter.removeAll(resources1);

        verify(resourcesRemovedHandler, never()).onResourcesRemoved(
                any(ResourcesRemovedEvent.class));
    }

    @Test
    public void removeResourceSet() {
        splitter.addAll(resources1);
        splitter.addAll(resources2);
        splitter.removeAll(resources1);

        Map<String, ResourceSet> result = splitter.getCategorizedResourceSets();

        assertEquals(1, result.size());
        assertTrue(result.containsKey(CATEGORY_2));
        assertTrue(result.get(CATEGORY_2).containsEqualResources(resources2));
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        labelProvider = spy(new DefaultCategoryLabelProvider());

        splitter = new ResourceSplitter(categorizer,
                new DefaultResourceSetFactory());

        resources1 = createResources(TEST_CATEGORY, 1, 2, 3);
        resources2 = createResources(TEST_CATEGORY, 4, 5);

        when(categorizer.getCategories(resources1.toList().get(0))).thenReturn(
                toSet(CATEGORY_1));
        when(categorizer.getCategories(resources1.toList().get(1))).thenReturn(
                toSet(CATEGORY_1));
        when(categorizer.getCategories(resources1.toList().get(2))).thenReturn(
                toSet(CATEGORY_1));

        when(categorizer.getCategories(resources2.toList().get(0))).thenReturn(
                toSet(CATEGORY_2));
        when(categorizer.getCategories(resources2.toList().get(1))).thenReturn(
                toSet(CATEGORY_2));
    }
}
