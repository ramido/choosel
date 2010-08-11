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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.thechiselgroup.choosel.client.test.AdvancedAsserts.assertContentEquals;
import static org.thechiselgroup.choosel.client.test.TestResourceSetFactory.createResource;
import static org.thechiselgroup.choosel.client.test.TestResourceSetFactory.createResources;
import static org.thechiselgroup.choosel.client.test.TestResourceSetFactory.toResourceSet;
import static org.thechiselgroup.choosel.client.util.CollectionUtils.toSet;

import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.client.util.Delta;
import org.thechiselgroup.choosel.client.util.NullIterable;

public class ResourceSplitterTest {

    public static final String CATEGORY_1 = "category1";

    public static final String CATEGORY_2 = "category2";

    private static final String TEST_CATEGORY = "test";

    @Mock
    private ResourceMultiCategorizer categorizer;

    @Mock
    private ResourceCategoriesChangedHandler changeHandler;

    private ResourceSet resources1;

    private ResourceSet resources2;

    private ResourceSplitter splitter;

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

    public Set<ResourceCategoryChange> captureChanges() {
        ArgumentCaptor<ResourceCategoriesChangedEvent> eventCaptor = ArgumentCaptor
                .forClass(ResourceCategoriesChangedEvent.class);
        verify(changeHandler, times(1)).onResourceCategoriesChanged(
                eventCaptor.capture());
        return eventCaptor.getValue().getChanges();
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

    @Test
    public void doNotFireResourceCategoryChangesWhenNothingChangesOnRemove() {
        splitter.addHandler(changeHandler);
        splitter.removeAll(NullIterable.<Resource> nullIterable());

        verify(changeHandler, times(0)).onResourceCategoriesChanged(
                any(ResourceCategoriesChangedEvent.class));
    }

    // TODO test for add all --> multiple categories

    @Test
    public void fireResourceCategoryAddedAndChangeChangeOnAdd() {
        Resource resource = createResource(TEST_CATEGORY, 1);

        splitter.addHandler(changeHandler);
        splitter.add(resource);

        Set<ResourceCategoryChange> changes = captureChanges();

        assertContentEquals(toSet(new ResourceCategoryChange(Delta.ADD,
                CATEGORY_1, toResourceSet(resource))), changes);
    }

    @Test
    public void fireResourceCategoryAddedAndChangedOnAddAll() {
        splitter.addAll(createResources(TEST_CATEGORY, 1, 2));
        splitter.addHandler(changeHandler);
        splitter.addAll(createResources(TEST_CATEGORY, 3, 4, 5));

        Set<ResourceCategoryChange> changes = captureChanges();

        Set<ResourceCategoryChange> expectedChanges = toSet(
                new ResourceCategoryChange(Delta.UPDATE, CATEGORY_1, resources1),
                new ResourceCategoryChange(Delta.ADD, CATEGORY_2, resources2));

        assertContentEquals(expectedChanges, changes);
    }

    /**
     * Tests that the resource category added event is fired and contains all
     * resources when fired (and not just later on).
     */
    @Test
    public void fireResourceCategoryAddedEventOnAdd() {
        final boolean[] called = { false };

        splitter.addHandler(new ResourceCategoriesChangedHandler() {
            @Override
            public void onResourceCategoriesChanged(
                    ResourceCategoriesChangedEvent e) {

                assertContentEquals(toSet(new ResourceCategoryChange(Delta.ADD,
                        CATEGORY_1, createResources(TEST_CATEGORY, 1))), e
                        .getChanges());

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

        splitter.addHandler(new ResourceCategoriesChangedHandler() {
            @Override
            public void onResourceCategoriesChanged(
                    ResourceCategoriesChangedEvent e) {

                assertContentEquals(toSet(new ResourceCategoryChange(Delta.ADD,
                        CATEGORY_1, resources1)), e.getChanges());

                called[0] = true;
            }
        });

        splitter.addAll(resources1);

        assertEquals(true, called[0]);
    }

    @Test
    public void fireResourceCategoryRemovedAndUpdateChangesWhenRemovingOneAndAHalfCategories() {
        ResourceSet allResources = new DefaultResourceSet();
        allResources.addAll(resources1);
        allResources.add(createResource(TEST_CATEGORY, 4));

        splitter.addAll(resources1);
        splitter.addAll(resources2);
        splitter.addHandler(changeHandler);
        splitter.removeAll(allResources);

        Set<ResourceCategoryChange> changes = captureChanges();

        Set<ResourceCategoryChange> expectedChanges = toSet(
                new ResourceCategoryChange(Delta.REMOVE, CATEGORY_1, resources1),
                new ResourceCategoryChange(Delta.UPDATE, CATEGORY_2,
                        createResources(TEST_CATEGORY, 5)));

        assertContentEquals(expectedChanges, changes);
    }

    @Test
    public void fireResourceCategoryRemovedChangeOnRemove() {
        Resource resource = createResource(TEST_CATEGORY, 1);

        splitter.add(resource);
        splitter.addHandler(changeHandler);
        splitter.remove(resource);

        Set<ResourceCategoryChange> changes = captureChanges();

        assertContentEquals(toSet(new ResourceCategoryChange(Delta.REMOVE,
                CATEGORY_1, toResourceSet(resource))), changes);
    }

    @Test
    public void fireResourceCategoryRemovedChangeOnRemoveAll() {
        splitter.addAll(resources1);
        splitter.addHandler(changeHandler);
        splitter.removeAll(resources1);

        Set<ResourceCategoryChange> changes = captureChanges();

        assertContentEquals(toSet(new ResourceCategoryChange(Delta.REMOVE,
                CATEGORY_1, resources1)), changes);
    }

    @Test
    public void fireResourceCategoryRemovedChangesWhenRemovingTwoCategories() {
        ResourceSet allResources = new DefaultResourceSet();
        allResources.addAll(resources1);
        allResources.addAll(resources2);

        splitter.addAll(resources1);
        splitter.addAll(resources2);
        splitter.addHandler(changeHandler);
        splitter.removeAll(allResources);

        Set<ResourceCategoryChange> changes = captureChanges();

        Set<ResourceCategoryChange> expectedChanges = toSet(
                new ResourceCategoryChange(Delta.REMOVE, CATEGORY_1, resources1),
                new ResourceCategoryChange(Delta.REMOVE, CATEGORY_2, resources2));

        assertContentEquals(expectedChanges, changes);
    }

    @Test
    public void noResourceSetEventsFiredOnCompleteCategoryRemovalViaRemove() {
        splitter.add(createResource(TEST_CATEGORY, 1));
        ResourceSet categorizedResources = splitter
                .getCategorizedResourceSets().get(CATEGORY_1);
        ResourcesRemovedEventHandler resourcesRemovedHandler = mock(ResourcesRemovedEventHandler.class);
        categorizedResources.addEventHandler(resourcesRemovedHandler);
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
        categorizedResources.addEventHandler(resourcesRemovedHandler);
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

        splitter = new ResourceSplitter(categorizer,
                new DefaultResourceSetFactory());

        resources1 = createResources(TEST_CATEGORY, 1, 2, 3);
        resources2 = createResources(TEST_CATEGORY, 4, 5);

        when(categorizer.getCategories(createResource(TEST_CATEGORY, 1)))
                .thenReturn(toSet(CATEGORY_1));
        when(categorizer.getCategories(createResource(TEST_CATEGORY, 2)))
                .thenReturn(toSet(CATEGORY_1));
        when(categorizer.getCategories(createResource(TEST_CATEGORY, 3)))
                .thenReturn(toSet(CATEGORY_1));

        when(categorizer.getCategories(createResource(TEST_CATEGORY, 4)))
                .thenReturn(toSet(CATEGORY_2));
        when(categorizer.getCategories(createResource(TEST_CATEGORY, 5)))
                .thenReturn(toSet(CATEGORY_2));
    }
}
