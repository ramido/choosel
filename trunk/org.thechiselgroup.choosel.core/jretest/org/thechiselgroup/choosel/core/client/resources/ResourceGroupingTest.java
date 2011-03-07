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
package org.thechiselgroup.choosel.core.client.resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.thechiselgroup.choosel.core.client.test.AdvancedAsserts.assertContentEquals;
import static org.thechiselgroup.choosel.core.client.test.AdvancedAsserts.assertMapKeysEqual;
import static org.thechiselgroup.choosel.core.client.test.ResourcesTestHelper.verifyOnResourceSetChanged;
import static org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory.createResource;
import static org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory.createResources;
import static org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory.toResourceSet;
import static org.thechiselgroup.choosel.core.client.util.collections.CollectionUtils.toSet;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ResourceGroupingTest {

    private static final String GROUP_1_1 = "group1-1";

    private static final String GROUP_1_2 = "group1-2";

    private static final String GROUP_2_1 = "group2-1";

    private static final String GROUP_2_2 = "group2-2";

    private static final String GROUP_2_3 = "group2-3";

    private static final String GROUP_2_4 = "group2-4";

    @Mock
    private ResourceMultiCategorizer categorizer1;

    @Mock
    private ResourceMultiCategorizer categorizer2;

    @Mock
    private ResourceGroupingChangedHandler changeHandler;

    private ResourceGrouping underTest;

    @Test
    public void addResourceWithMultipleCategoriesCreatesMultipleCategories() {
        setUpCategory(categorizer1, 1, GROUP_1_1, GROUP_1_2);

        underTest.add(createResource(1));

        Map<String, ResourceSet> result = underTest
                .getCategorizedResourceSets();

        assertMapKeysEqual(result, GROUP_1_1, GROUP_1_2);
        assertContentEquals(createResources(1), result.get(GROUP_1_1));
        assertContentEquals(createResources(1), result.get(GROUP_1_2));
    }

    public List<ResourceGroupingChange> captureChanges() {
        ArgumentCaptor<ResourceGroupingChangedEvent> eventCaptor = ArgumentCaptor
                .forClass(ResourceGroupingChangedEvent.class);
        verify(changeHandler, times(1)).onResourceCategoriesChanged(
                eventCaptor.capture());
        return eventCaptor.getValue().getChanges();
    }

    @Test
    public void changeCategorizerFiresEvents1() {
        underTest.addAll(createResources(1, 2, 3, 4, 5));
        underTest.addHandler(changeHandler);
        underTest.setCategorizer(categorizer2);

        List<ResourceGroupingChange> changes = captureChanges();

        assertContentEquals(
                toSet(new ResourceGroupingChange(
                        ResourceGroupingChangeDelta.GROUP_REMOVED, GROUP_1_1,
                        createResources(1, 2, 3)), new ResourceGroupingChange(
                        ResourceGroupingChangeDelta.GROUP_REMOVED, GROUP_1_2,
                        createResources(4, 5)), new ResourceGroupingChange(
                        ResourceGroupingChangeDelta.GROUP_CREATED, GROUP_2_1,
                        createResources(1)), new ResourceGroupingChange(
                        ResourceGroupingChangeDelta.GROUP_CREATED, GROUP_2_2,
                        createResources(2)), new ResourceGroupingChange(
                        ResourceGroupingChangeDelta.GROUP_CREATED, GROUP_2_3,
                        createResources(3, 4)), new ResourceGroupingChange(
                        ResourceGroupingChangeDelta.GROUP_CREATED, GROUP_2_4,
                        createResources(4, 5))), changes);
    }

    @Test
    public void changeCategorizerFiresEvents2() {
        underTest.addAll(createResources(1, 2, 3, 4, 5));
        underTest.setCategorizer(categorizer2);
        underTest.addHandler(changeHandler);
        underTest.setCategorizer(categorizer1);

        List<ResourceGroupingChange> changes = captureChanges();

        assertContentEquals(
                toSet(new ResourceGroupingChange(
                        ResourceGroupingChangeDelta.GROUP_CREATED, GROUP_1_1,
                        createResources(1, 2, 3)), new ResourceGroupingChange(
                        ResourceGroupingChangeDelta.GROUP_CREATED, GROUP_1_2,
                        createResources(4, 5)), new ResourceGroupingChange(
                        ResourceGroupingChangeDelta.GROUP_REMOVED, GROUP_2_1,
                        createResources(1)), new ResourceGroupingChange(
                        ResourceGroupingChangeDelta.GROUP_REMOVED, GROUP_2_2,
                        createResources(2)), new ResourceGroupingChange(
                        ResourceGroupingChangeDelta.GROUP_REMOVED, GROUP_2_3,
                        createResources(3, 4)), new ResourceGroupingChange(
                        ResourceGroupingChangeDelta.GROUP_REMOVED, GROUP_2_4,
                        createResources(4, 5))), changes);
    }

    @Test
    public void changeCategorizerUpdatesCategories1() {
        underTest.addAll(createResources(1, 2, 3, 4, 5));
        underTest.setCategorizer(categorizer2);

        Map<String, ResourceSet> result = underTest
                .getCategorizedResourceSets();

        assertMapKeysEqual(result, GROUP_2_1, GROUP_2_2, GROUP_2_3, GROUP_2_4);
        assertContentEquals(createResources(1), result.get(GROUP_2_1));
        assertContentEquals(createResources(2), result.get(GROUP_2_2));
        assertContentEquals(createResources(3, 4), result.get(GROUP_2_3));
        assertContentEquals(createResources(4, 5), result.get(GROUP_2_4));
    }

    @Test
    public void changeCategorizerUpdatesCategories2() {
        underTest.addAll(createResources(1, 2, 3, 4, 5));
        underTest.setCategorizer(categorizer2);
        underTest.setCategorizer(categorizer1);

        Map<String, ResourceSet> result = underTest
                .getCategorizedResourceSets();

        assertMapKeysEqual(result, GROUP_1_1, GROUP_1_2);
        assertContentEquals(createResources(1, 2, 3), result.get(GROUP_1_1));
        assertContentEquals(createResources(4, 5), result.get(GROUP_1_2));
    }

    @Test
    public void changeCategorizerUpdatesCategoriesAfterAddAllTwiceAndRemoveAll() {
        underTest.addAll(createResources(1, 2, 3, 4, 5));
        underTest.addAll(createResources(1, 2));
        underTest.removeAll(createResources(1, 2));
        underTest.setCategorizer(categorizer2);

        Map<String, ResourceSet> result = underTest
                .getCategorizedResourceSets();

        assertMapKeysEqual(result, GROUP_2_3, GROUP_2_4);
        assertContentEquals(createResources(3, 4), result.get(GROUP_2_3));
        assertContentEquals(createResources(4, 5), result.get(GROUP_2_4));
    }

    @Test
    public void changeCategorizerUpdatesCategoriesAfterRemoveAll() {
        underTest.addAll(createResources(1, 2, 3, 4, 5));
        underTest.removeAll(createResources(1, 2));
        underTest.setCategorizer(categorizer2);

        Map<String, ResourceSet> result = underTest
                .getCategorizedResourceSets();

        assertMapKeysEqual(result, GROUP_2_3, GROUP_2_4);
        assertContentEquals(createResources(3, 4), result.get(GROUP_2_3));
        assertContentEquals(createResources(4, 5), result.get(GROUP_2_4));
    }

    @Test
    public void changeToSameCategorizerDoesNotFireEvent() {
        underTest.addAll(createResources(1, 2, 3, 4, 5));
        underTest.addHandler(changeHandler);
        underTest.setCategorizer(categorizer1);

        verify(changeHandler, times(0)).onResourceCategoriesChanged(
                any(ResourceGroupingChangedEvent.class));
    }

    @Test
    public void createCategories() {
        underTest.addAll(createResources(1, 2, 3, 4, 5));

        Map<String, ResourceSet> result = underTest
                .getCategorizedResourceSets();

        assertMapKeysEqual(result, GROUP_1_1, GROUP_1_2);
        assertContentEquals(createResources(1, 2, 3), result.get(GROUP_1_1));
        assertContentEquals(createResources(4, 5), result.get(GROUP_1_2));
    }

    @Test
    public void doNotFireResourceCategoryChangesWhenNothingChangesOnRemove() {
        underTest.addHandler(changeHandler);
        underTest.removeAll(Collections.<Resource> emptyList());

        verify(changeHandler, times(0)).onResourceCategoriesChanged(
                any(ResourceGroupingChangedEvent.class));
    }

    @Test
    public void fireResourceCategoryAddedAndChangeChangeOnAdd() {
        Resource resource = createResource(1);

        underTest.addHandler(changeHandler);
        underTest.add(resource);

        List<ResourceGroupingChange> changes = captureChanges();

        assertContentEquals(toSet(new ResourceGroupingChange(
                ResourceGroupingChangeDelta.GROUP_CREATED, GROUP_1_1,
                toResourceSet(resource))), changes);
    }

    // TODO test for add all --> multiple categories

    @Test
    public void fireResourceCategoryAddedAndChangedOnAddAll() {
        underTest.addAll(createResources(1, 2));
        underTest.addHandler(changeHandler);
        underTest.addAll(createResources(3, 4, 5));

        List<ResourceGroupingChange> changes = captureChanges();

        assertContentEquals(
                toSet(new ResourceGroupingChange(
                        ResourceGroupingChangeDelta.GROUP_CHANGED, GROUP_1_1,
                        createResources(1, 2, 3)), new ResourceGroupingChange(
                        ResourceGroupingChangeDelta.GROUP_CREATED, GROUP_1_2,
                        createResources(4, 5))), changes);
    }

    /**
     * Tests that the resource category added event is fired and contains all
     * resources when fired (and not just later on).
     */
    @Test
    public void fireResourceCategoryAddedEventOnAdd() {
        final boolean[] called = { false };

        underTest.addHandler(new ResourceGroupingChangedHandler() {
            @Override
            public void onResourceCategoriesChanged(
                    ResourceGroupingChangedEvent e) {

                assertContentEquals(toSet(new ResourceGroupingChange(
                        ResourceGroupingChangeDelta.GROUP_CREATED, GROUP_1_1,
                        createResources(1))), e.getChanges());

                called[0] = true;
            }
        });

        underTest.add(createResource(1));

        assertEquals(true, called[0]);
    }

    /**
     * Tests that the resource category added event is fired and contains the
     * resource when fired (and not just later on).
     */
    @Test
    public void fireResourceCategoryAddedEventOnAddAll() {
        final boolean[] called = { false };

        underTest.addHandler(new ResourceGroupingChangedHandler() {
            @Override
            public void onResourceCategoriesChanged(
                    ResourceGroupingChangedEvent e) {

                assertContentEquals(toSet(new ResourceGroupingChange(
                        ResourceGroupingChangeDelta.GROUP_CREATED, GROUP_1_1,
                        createResources(1, 2, 3))), e.getChanges());

                called[0] = true;
            }
        });

        underTest.addAll(createResources(1, 2, 3));

        assertEquals(true, called[0]);
    }

    @Test
    public void fireResourceCategoryRemovedAndUpdateChangesWhenRemovingOneAndAHalfCategories() {
        ResourceSet allResources = new DefaultResourceSet();
        allResources.addAll(createResources(1, 2, 3, 4));

        underTest.addAll(createResources(1, 2, 3, 4, 5));
        underTest.addHandler(changeHandler);
        underTest.removeAll(allResources);

        List<ResourceGroupingChange> changes = captureChanges();

        Set<ResourceGroupingChange> expectedChanges = toSet(
                new ResourceGroupingChange(
                        ResourceGroupingChangeDelta.GROUP_REMOVED, GROUP_1_1,
                        createResources(1, 2, 3)), new ResourceGroupingChange(
                        ResourceGroupingChangeDelta.GROUP_CHANGED, GROUP_1_2,
                        createResources(5)));

        assertContentEquals(expectedChanges, changes);
    }

    @Test
    public void fireResourceCategoryRemovedChangeOnRemove() {
        Resource resource = createResource(1);

        underTest.add(resource);
        underTest.addHandler(changeHandler);
        underTest.remove(resource);

        List<ResourceGroupingChange> changes = captureChanges();

        assertContentEquals(toSet(new ResourceGroupingChange(
                ResourceGroupingChangeDelta.GROUP_REMOVED, GROUP_1_1,
                toResourceSet(resource))), changes);
    }

    @Test
    public void fireResourceCategoryRemovedChangeOnRemoveAll() {
        underTest.addAll(createResources(1, 2, 3));
        underTest.addHandler(changeHandler);
        underTest.removeAll(createResources(1, 2, 3));

        List<ResourceGroupingChange> changes = captureChanges();

        assertContentEquals(toSet(new ResourceGroupingChange(
                ResourceGroupingChangeDelta.GROUP_REMOVED, GROUP_1_1,
                createResources(1, 2, 3))), changes);
    }

    @Test
    public void fireResourceCategoryRemovedChangesWhenRemovingTwoCategories() {
        ResourceSet allResources = new DefaultResourceSet();
        allResources.addAll(createResources(1, 2, 3, 4, 5));

        underTest.addAll(createResources(1, 2, 3, 4, 5));
        underTest.addHandler(changeHandler);
        underTest.removeAll(allResources);

        List<ResourceGroupingChange> changes = captureChanges();

        Set<ResourceGroupingChange> expectedChanges = toSet(
                new ResourceGroupingChange(
                        ResourceGroupingChangeDelta.GROUP_REMOVED, GROUP_1_1,
                        createResources(1, 2, 3)), new ResourceGroupingChange(
                        ResourceGroupingChangeDelta.GROUP_REMOVED, GROUP_1_2,
                        createResources(4, 5)));

        assertContentEquals(expectedChanges, changes);
    }

    @Test
    public void getGroupsAfterAddAllAndCategorizerChange() {
        setUpCategory(categorizer1, 1, GROUP_1_1);
        setUpCategory(categorizer1, 2, GROUP_1_2);
        setUpCategory(categorizer1, 3, GROUP_1_1);

        setUpCategory(categorizer2, 1, GROUP_2_2);
        setUpCategory(categorizer2, 2, GROUP_1_2);
        setUpCategory(categorizer2, 3, GROUP_2_1);

        ResourceSet resources = createResources(1, 2, 3);
        underTest.addAll(resources);

        underTest.setCategorizer(categorizer2);

        Set<String> result = underTest.getGroups(createResources(2, 3));

        assertContentEquals(toSet(GROUP_2_1, GROUP_1_2), result);
    }

    @Test
    public void getGroupsAfterAddAllAndRemove() {
        setUpCategory(categorizer1, 1, GROUP_1_1);
        setUpCategory(categorizer1, 2, GROUP_1_2);
        setUpCategory(categorizer1, 3, GROUP_1_1);
        ResourceSet resources = createResources(1, 2, 3);
        underTest.addAll(resources);
        underTest.remove(createResource(3));

        // 3 is not contained any more
        Set<String> result = underTest.getGroups(createResources(2, 3));

        assertContentEquals(toSet(GROUP_1_2), result);
    }

    @Test
    public void getGroupsAfterAddAllReturningExcludingOneGroup() {
        setUpCategory(categorizer1, 1, GROUP_1_1);
        setUpCategory(categorizer1, 2, GROUP_1_2);
        ResourceSet resources = createResources(1, 2);
        underTest.addAll(resources);

        Set<String> result = underTest.getGroups(createResources(1));

        assertContentEquals(toSet(GROUP_1_1), result);
    }

    @Test
    public void getGroupsAfterAddAllReturningNothingForNotIncludedResource() {
        setUpCategory(categorizer1, 1, GROUP_1_1);
        ResourceSet resources = createResources(1);
        underTest.addAll(resources);

        Set<String> result = underTest.getGroups(createResources(2));

        assertEquals(true, result.isEmpty());
    }

    @Test
    public void getGroupsAfterAddAllReturningOneGroupForSeveralResources() {
        setUpCategory(categorizer1, 1, GROUP_1_1);
        setUpCategory(categorizer1, 2, GROUP_1_1);
        ResourceSet resources = createResources(1, 2);
        underTest.addAll(resources);

        Set<String> result = underTest.getGroups(resources);

        assertContentEquals(toSet(GROUP_1_1), result);
    }

    @Test
    public void getGroupsAfterAddAllReturningSingleGroupForSingleResource() {
        setUpCategory(categorizer1, 1, GROUP_1_1);
        ResourceSet resources = createResources(1);
        underTest.addAll(resources);

        Set<String> result = underTest.getGroups(resources);

        assertContentEquals(toSet(GROUP_1_1), result);
    }

    @Test
    public void getGroupsAfterAddAllReturningTwoGroupsForSingleResource() {
        setUpCategory(categorizer1, 1, GROUP_1_1, GROUP_1_2);
        ResourceSet resources = createResources(1);
        underTest.addAll(resources);

        Set<String> result = underTest.getGroups(resources);

        assertContentEquals(toSet(GROUP_1_1, GROUP_1_2), result);
    }

    @Test
    public void noResourceSetEventsFiredOnCompleteCategoryRemovalViaRemove() {
        underTest.add(createResource(1));
        ResourceSet categorizedResources = underTest
                .getCategorizedResourceSets().get(GROUP_1_1);
        ResourceSetChangedEventHandler resourcesChangedHandler = mock(ResourceSetChangedEventHandler.class);
        categorizedResources.addEventHandler(resourcesChangedHandler);
        underTest.remove(createResource(1));

        verifyOnResourceSetChanged(0, resourcesChangedHandler);
    }

    @Test
    public void noResourceSetEventsFiredOnCompleteCategoryRemovalViaRemoveAll() {
        underTest.addAll(createResources(1, 2, 3));
        ResourceSet categorizedResources = underTest
                .getCategorizedResourceSets().get(GROUP_1_1);
        ResourceSetChangedEventHandler resourcesChangedHandler = mock(ResourceSetChangedEventHandler.class);
        categorizedResources.addEventHandler(resourcesChangedHandler);
        underTest.removeAll(createResources(1, 2, 3));

        verifyOnResourceSetChanged(0, resourcesChangedHandler);
    }

    @Test
    public void removeResourceSet() {
        underTest.addAll(createResources(1, 2, 3, 4, 5));
        underTest.removeAll(createResources(1, 2, 3));

        Map<String, ResourceSet> result = underTest
                .getCategorizedResourceSets();

        assertEquals(1, result.size());
        assertTrue(result.containsKey(GROUP_1_2));
        assertTrue(result.get(GROUP_1_2).containsEqualResources(
                createResources(4, 5)));
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        underTest = new ResourceGrouping(categorizer1,
                new DefaultResourceSetFactory());

        setUpCategory(categorizer1, 1, GROUP_1_1);
        setUpCategory(categorizer1, 2, GROUP_1_1);
        setUpCategory(categorizer1, 3, GROUP_1_1);
        setUpCategory(categorizer1, 4, GROUP_1_2);
        setUpCategory(categorizer1, 5, GROUP_1_2);

        setUpCategory(categorizer2, 1, GROUP_2_1);
        setUpCategory(categorizer2, 2, GROUP_2_2);
        setUpCategory(categorizer2, 3, GROUP_2_3);
        setUpCategory(categorizer2, 4, GROUP_2_3, GROUP_2_4);
        setUpCategory(categorizer2, 5, GROUP_2_4);

    }

    private void setUpCategory(ResourceMultiCategorizer categorizer,
            int resourceId, String... category) {

        when(categorizer.getCategories(createResource(resourceId))).thenReturn(
                toSet(category));
    }
}
