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
import static org.thechiselgroup.choosel.client.test.AdvancedAsserts.assertMapKeysEqual;
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

    private static final String CATEGORY_1_1 = "category1-1";

    private static final String CATEGORY_1_2 = "category1-2";

    private static final String CATEGORY_2_1 = "category2-1";

    private static final String CATEGORY_2_2 = "category2-2";

    private static final String CATEGORY_2_3 = "category2-3";

    private static final String CATEGORY_2_4 = "category2-4";

    @Mock
    private ResourceMultiCategorizer categorizer1;

    @Mock
    private ResourceMultiCategorizer categorizer2;

    @Mock
    private ResourceCategoriesChangedHandler changeHandler;

    private ResourceSplitter splitter;

    @Test
    public void addResourceWithMultipleCategoriesCreatesMultipleCategories() {
        setUpCategory(categorizer1, 1, CATEGORY_1_1, CATEGORY_1_2);

        splitter.add(createResource(1));

        Map<String, ResourceSet> result = splitter.getCategorizedResourceSets();

        assertMapKeysEqual(result, CATEGORY_1_1, CATEGORY_1_2);
        assertContentEquals(createResources(1), result.get(CATEGORY_1_1));
        assertContentEquals(createResources(1), result.get(CATEGORY_1_2));
    }

    public Set<ResourceCategoryChange> captureChanges() {
        ArgumentCaptor<ResourceCategoriesChangedEvent> eventCaptor = ArgumentCaptor
                .forClass(ResourceCategoriesChangedEvent.class);
        verify(changeHandler, times(1)).onResourceCategoriesChanged(
                eventCaptor.capture());
        return eventCaptor.getValue().getChanges();
    }

    @Test
    public void changeCategorizerFiresEvents1() {
        splitter.addAll(createResources(1, 2, 3, 4, 5));
        splitter.addHandler(changeHandler);
        splitter.setCategorizer(categorizer2);

        Set<ResourceCategoryChange> changes = captureChanges();

        assertContentEquals(
                toSet(new ResourceCategoryChange(Delta.REMOVE, CATEGORY_1_1,
                        createResources(1, 2, 3)), new ResourceCategoryChange(
                        Delta.REMOVE, CATEGORY_1_2, createResources(4, 5)),
                        new ResourceCategoryChange(Delta.ADD, CATEGORY_2_1,
                                createResources(1)),
                        new ResourceCategoryChange(Delta.ADD, CATEGORY_2_2,
                                createResources(2)),
                        new ResourceCategoryChange(Delta.ADD, CATEGORY_2_3,
                                createResources(3, 4)),
                        new ResourceCategoryChange(Delta.ADD, CATEGORY_2_4,
                                createResources(4, 5))), changes);
    }

    @Test
    public void changeCategorizerFiresEvents2() {
        splitter.addAll(createResources(1, 2, 3, 4, 5));
        splitter.setCategorizer(categorizer2);
        splitter.addHandler(changeHandler);
        splitter.setCategorizer(categorizer1);

        Set<ResourceCategoryChange> changes = captureChanges();

        assertContentEquals(
                toSet(new ResourceCategoryChange(Delta.ADD, CATEGORY_1_1,
                        createResources(1, 2, 3)), new ResourceCategoryChange(
                        Delta.ADD, CATEGORY_1_2, createResources(4, 5)),
                        new ResourceCategoryChange(Delta.REMOVE, CATEGORY_2_1,
                                createResources(1)),
                        new ResourceCategoryChange(Delta.REMOVE, CATEGORY_2_2,
                                createResources(2)),
                        new ResourceCategoryChange(Delta.REMOVE, CATEGORY_2_3,
                                createResources(3, 4)),
                        new ResourceCategoryChange(Delta.REMOVE, CATEGORY_2_4,
                                createResources(4, 5))), changes);
    }

    @Test
    public void changeCategorizerUpdatesCategories1() {
        splitter.addAll(createResources(1, 2, 3, 4, 5));
        splitter.setCategorizer(categorizer2);

        Map<String, ResourceSet> result = splitter.getCategorizedResourceSets();

        assertMapKeysEqual(result, CATEGORY_2_1, CATEGORY_2_2, CATEGORY_2_3,
                CATEGORY_2_4);
        assertContentEquals(createResources(1), result.get(CATEGORY_2_1));
        assertContentEquals(createResources(2), result.get(CATEGORY_2_2));
        assertContentEquals(createResources(3, 4), result.get(CATEGORY_2_3));
        assertContentEquals(createResources(4, 5), result.get(CATEGORY_2_4));
    }

    @Test
    public void changeCategorizerUpdatesCategories2() {
        splitter.addAll(createResources(1, 2, 3, 4, 5));
        splitter.setCategorizer(categorizer2);
        splitter.setCategorizer(categorizer1);

        Map<String, ResourceSet> result = splitter.getCategorizedResourceSets();

        assertMapKeysEqual(result, CATEGORY_1_1, CATEGORY_1_2);
        assertContentEquals(createResources(1, 2, 3), result.get(CATEGORY_1_1));
        assertContentEquals(createResources(4, 5), result.get(CATEGORY_1_2));
    }

    @Test
    public void changeCategorizerUpdatesCategoriesAfterAddAllTwiceAndRemoveAll() {
        splitter.addAll(createResources(1, 2, 3, 4, 5));
        splitter.addAll(createResources(1, 2));
        splitter.removeAll(createResources(1, 2));
        splitter.setCategorizer(categorizer2);

        Map<String, ResourceSet> result = splitter.getCategorizedResourceSets();

        assertMapKeysEqual(result, CATEGORY_2_3, CATEGORY_2_4);
        assertContentEquals(createResources(3, 4), result.get(CATEGORY_2_3));
        assertContentEquals(createResources(4, 5), result.get(CATEGORY_2_4));
    }

    @Test
    public void changeCategorizerUpdatesCategoriesAfterRemoveAll() {
        splitter.addAll(createResources(1, 2, 3, 4, 5));
        splitter.removeAll(createResources(1, 2));
        splitter.setCategorizer(categorizer2);

        Map<String, ResourceSet> result = splitter.getCategorizedResourceSets();

        assertMapKeysEqual(result, CATEGORY_2_3, CATEGORY_2_4);
        assertContentEquals(createResources(3, 4), result.get(CATEGORY_2_3));
        assertContentEquals(createResources(4, 5), result.get(CATEGORY_2_4));
    }

    @Test
    public void changeToSameCategorizerDoesNotFireEvent() {
        splitter.addAll(createResources(1, 2, 3, 4, 5));
        splitter.addHandler(changeHandler);
        splitter.setCategorizer(categorizer1);

        verify(changeHandler, times(0)).onResourceCategoriesChanged(
                any(ResourceCategoriesChangedEvent.class));
    }

    @Test
    public void createCategories() {
        splitter.addAll(createResources(1, 2, 3, 4, 5));

        Map<String, ResourceSet> result = splitter.getCategorizedResourceSets();

        assertMapKeysEqual(result, CATEGORY_1_1, CATEGORY_1_2);
        assertContentEquals(createResources(1, 2, 3), result.get(CATEGORY_1_1));
        assertContentEquals(createResources(4, 5), result.get(CATEGORY_1_2));
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
        Resource resource = createResource(1);

        splitter.addHandler(changeHandler);
        splitter.add(resource);

        Set<ResourceCategoryChange> changes = captureChanges();

        assertContentEquals(toSet(new ResourceCategoryChange(Delta.ADD,
                CATEGORY_1_1, toResourceSet(resource))), changes);
    }

    @Test
    public void fireResourceCategoryAddedAndChangedOnAddAll() {
        splitter.addAll(createResources(1, 2));
        splitter.addHandler(changeHandler);
        splitter.addAll(createResources(3, 4, 5));

        Set<ResourceCategoryChange> changes = captureChanges();

        assertContentEquals(
                toSet(new ResourceCategoryChange(Delta.UPDATE, CATEGORY_1_1,
                        createResources(1, 2, 3)), new ResourceCategoryChange(
                        Delta.ADD, CATEGORY_1_2, createResources(4, 5))),
                changes);
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
                        CATEGORY_1_1, createResources(1))), e.getChanges());

                called[0] = true;
            }
        });

        splitter.add(createResource(1));

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
                        CATEGORY_1_1, createResources(1, 2, 3))), e
                        .getChanges());

                called[0] = true;
            }
        });

        splitter.addAll(createResources(1, 2, 3));

        assertEquals(true, called[0]);
    }

    @Test
    public void fireResourceCategoryRemovedAndUpdateChangesWhenRemovingOneAndAHalfCategories() {
        ResourceSet allResources = new DefaultResourceSet();
        allResources.addAll(createResources(1, 2, 3, 4));

        splitter.addAll(createResources(1, 2, 3, 4, 5));
        splitter.addHandler(changeHandler);
        splitter.removeAll(allResources);

        Set<ResourceCategoryChange> changes = captureChanges();

        Set<ResourceCategoryChange> expectedChanges = toSet(
                new ResourceCategoryChange(Delta.REMOVE, CATEGORY_1_1,
                        createResources(1, 2, 3)), new ResourceCategoryChange(
                        Delta.UPDATE, CATEGORY_1_2, createResources(5)));

        assertContentEquals(expectedChanges, changes);
    }

    @Test
    public void fireResourceCategoryRemovedChangeOnRemove() {
        Resource resource = createResource(1);

        splitter.add(resource);
        splitter.addHandler(changeHandler);
        splitter.remove(resource);

        Set<ResourceCategoryChange> changes = captureChanges();

        assertContentEquals(toSet(new ResourceCategoryChange(Delta.REMOVE,
                CATEGORY_1_1, toResourceSet(resource))), changes);
    }

    @Test
    public void fireResourceCategoryRemovedChangeOnRemoveAll() {
        splitter.addAll(createResources(1, 2, 3));
        splitter.addHandler(changeHandler);
        splitter.removeAll(createResources(1, 2, 3));

        Set<ResourceCategoryChange> changes = captureChanges();

        assertContentEquals(toSet(new ResourceCategoryChange(Delta.REMOVE,
                CATEGORY_1_1, createResources(1, 2, 3))), changes);
    }

    @Test
    public void fireResourceCategoryRemovedChangesWhenRemovingTwoCategories() {
        ResourceSet allResources = new DefaultResourceSet();
        allResources.addAll(createResources(1, 2, 3, 4, 5));

        splitter.addAll(createResources(1, 2, 3, 4, 5));
        splitter.addHandler(changeHandler);
        splitter.removeAll(allResources);

        Set<ResourceCategoryChange> changes = captureChanges();

        Set<ResourceCategoryChange> expectedChanges = toSet(
                new ResourceCategoryChange(Delta.REMOVE, CATEGORY_1_1,
                        createResources(1, 2, 3)), new ResourceCategoryChange(
                        Delta.REMOVE, CATEGORY_1_2, createResources(4, 5)));

        assertContentEquals(expectedChanges, changes);
    }

    @Test
    public void noResourceSetEventsFiredOnCompleteCategoryRemovalViaRemove() {
        splitter.add(createResource(1));
        ResourceSet categorizedResources = splitter
                .getCategorizedResourceSets().get(CATEGORY_1_1);
        ResourcesRemovedEventHandler resourcesRemovedHandler = mock(ResourcesRemovedEventHandler.class);
        categorizedResources.addEventHandler(resourcesRemovedHandler);
        splitter.remove(createResource(1));

        verify(resourcesRemovedHandler, never()).onResourcesRemoved(
                any(ResourcesRemovedEvent.class));
    }

    @Test
    public void noResourceSetEventsFiredOnCompleteCategoryRemovalViaRemoveAll() {
        splitter.addAll(createResources(1, 2, 3));
        ResourceSet categorizedResources = splitter
                .getCategorizedResourceSets().get(CATEGORY_1_1);
        ResourcesRemovedEventHandler resourcesRemovedHandler = mock(ResourcesRemovedEventHandler.class);
        categorizedResources.addEventHandler(resourcesRemovedHandler);
        splitter.removeAll(createResources(1, 2, 3));

        verify(resourcesRemovedHandler, never()).onResourcesRemoved(
                any(ResourcesRemovedEvent.class));
    }

    @Test
    public void removeResourceSet() {
        splitter.addAll(createResources(1, 2, 3, 4, 5));
        splitter.removeAll(createResources(1, 2, 3));

        Map<String, ResourceSet> result = splitter.getCategorizedResourceSets();

        assertEquals(1, result.size());
        assertTrue(result.containsKey(CATEGORY_1_2));
        assertTrue(result.get(CATEGORY_1_2).containsEqualResources(
                createResources(4, 5)));
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        splitter = new ResourceSplitter(categorizer1,
                new DefaultResourceSetFactory());

        setUpCategory(categorizer1, 1, CATEGORY_1_1);
        setUpCategory(categorizer1, 2, CATEGORY_1_1);
        setUpCategory(categorizer1, 3, CATEGORY_1_1);
        setUpCategory(categorizer1, 4, CATEGORY_1_2);
        setUpCategory(categorizer1, 5, CATEGORY_1_2);

        setUpCategory(categorizer2, 1, CATEGORY_2_1);
        setUpCategory(categorizer2, 2, CATEGORY_2_2);
        setUpCategory(categorizer2, 3, CATEGORY_2_3);
        setUpCategory(categorizer2, 4, CATEGORY_2_3, CATEGORY_2_4);
        setUpCategory(categorizer2, 5, CATEGORY_2_4);

    }

    private void setUpCategory(ResourceMultiCategorizer categorizer,
            int resourceId, String... category) {

        when(categorizer.getCategories(createResource(resourceId))).thenReturn(
                toSet(category));
    }
}
