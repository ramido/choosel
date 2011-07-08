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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.thechiselgroup.choosel.core.client.resources.CategorizableResourceGroupingChange.newGroupChangedDelta;
import static org.thechiselgroup.choosel.core.client.resources.CategorizableResourceGroupingChange.newGroupCreatedDelta;
import static org.thechiselgroup.choosel.core.client.resources.CategorizableResourceGroupingChange.newGroupRemovedDelta;
import static org.thechiselgroup.choosel.core.client.test.AdvancedAsserts.assertContentEquals;
import static org.thechiselgroup.choosel.core.client.test.AdvancedAsserts.assertMapKeysEqual;
import static org.thechiselgroup.choosel.core.client.test.HamcrestResourceMatchers.containsExactly;
import static org.thechiselgroup.choosel.core.client.test.ResourcesTestHelper.verifyOnResourceSetChanged;
import static org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory.TYPE_1;
import static org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory.TYPE_2;
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
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightList;

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
    private ResourceMultiCategorizer categorizer3;

    @Mock
    private ResourceGroupingChangedHandler changeHandler;

    private ResourceGrouping underTest;

    private DefaultResourceSet testResources;

    // XXX
    @Test
    public void addAndRemoveUncategorizableAndCategorizableResourcesEventsFired() {
        setUpCategory(categorizer1, createResource(TYPE_1, 1), GROUP_1_1);
        setUpCategory(categorizer1, createResource(TYPE_1, 2), GROUP_1_1);
        setUpNullCategory(categorizer1, createResource(TYPE_1, 3));
        setUpNullCategory(categorizer1, createResource(TYPE_1, 4));
        setUpCategory(categorizer1, createResource(TYPE_1, 5), GROUP_1_1);
        setUpNullCategory(categorizer1, createResource(TYPE_1, 6));
        underTest.setCategorizer(categorizer1);
        testResources.addAll(createResources(TYPE_1, 1, 2, 3, 4));

        underTest.addHandler(changeHandler);

        testResources.change(createResources(TYPE_1, 5, 6),
                createResources(TYPE_1, 1, 3));

        ResourceGroupingChangedEvent event = captureResourceChangedEvent();
        LightweightList<CategorizableResourceGroupingChange> changes = event
                .getChanges();
        UncategorizableResourceGroupingChange uncategorizableResourceChanges = event
                .getUncategorizableResourceChanges();

        assertContentEquals(
                toSet(newGroupChangedDelta(GROUP_1_1,
                        createResources(TYPE_1, 2, 5),
                        createResources(TYPE_1, 5), null),
                        newGroupChangedDelta(GROUP_1_1,
                                createResources(TYPE_1, 2, 5), null,
                                createResources(TYPE_1, 1))), changes.toList());

        assertContentEquals(uncategorizableResourceChanges.addedResources,
                createResources(TYPE_1, 6));
        assertContentEquals(uncategorizableResourceChanges.removedResources,
                createResources(TYPE_1, 3));
    }

    // XXX
    @Test
    public void addAndRemoveUncategorizableAndCategorizableResourcesSetContent() {
        setUpCategory(categorizer1, createResource(TYPE_1, 1), GROUP_1_1);
        setUpCategory(categorizer1, createResource(TYPE_1, 2), GROUP_1_1);
        setUpNullCategory(categorizer1, createResource(TYPE_1, 3));
        setUpNullCategory(categorizer1, createResource(TYPE_1, 4));
        setUpCategory(categorizer1, createResource(TYPE_1, 5), GROUP_1_1);
        setUpNullCategory(categorizer1, createResource(TYPE_1, 6));

        underTest.setCategorizer(categorizer1);

        testResources.addAll(createResources(TYPE_1, 1, 2, 3, 4));

        testResources.change(createResources(TYPE_1, 5, 6),
                createResources(TYPE_1, 1, 3));

        Map<String, ResourceSet> categorizedResourceSets = underTest
                .getCategorizedResourceSets();
        ResourceSet uncategorizedResources = underTest
                .getUncategorizableResources();

        assertThat(categorizedResourceSets.get(GROUP_1_1),
                containsExactly(createResources(TYPE_1, 2, 5)));

        assertContentEquals(uncategorizedResources, createResources(4, 6));
    }

    // XXX
    @Test
    public void addOneCategorizableAndOneUncategorizableResourceToEmptyGroupEventFired() {
        setUpCategory(categorizer1, createResource(TYPE_1, 1), GROUP_1_1);
        setUpNullCategory(categorizer1, createResource(TYPE_2, 2));
        underTest.setCategorizer(categorizer1);
        underTest.addHandler(changeHandler);

        ResourceSet resources = createResources(TYPE_1, 1);
        resources.add(createResource(TYPE_2, 2));
        testResources.addAll(resources);

        ResourceGroupingChangedEvent event = captureResourceChangedEvent();

        LightweightList<CategorizableResourceGroupingChange> changes = event
                .getChanges();

        UncategorizableResourceGroupingChange uncategorizableChange = event
                .getUncategorizableResourceChanges();

        assertContentEquals(
                toSet(newGroupCreatedDelta(GROUP_1_1,
                        createResources(TYPE_1, 1))), changes.toList());
        assertContentEquals(uncategorizableChange.addedResources,
                createResources(TYPE_2, 2));
        assertTrue(uncategorizableChange.removedResources.isEmpty());

    }

    // XXX
    @Test
    public void addOneCategorizableAndOneUncategorizableResourceToEmptyGroupSetContent() {
        setUpCategory(categorizer1, createResource(TYPE_1, 1), GROUP_1_1);
        setUpNullCategory(categorizer1, createResource(TYPE_2, 2));
        underTest.setCategorizer(categorizer1);

        ResourceSet resources = createResources(TYPE_1, 1);
        resources.add(createResource(TYPE_2, 2));
        testResources.addAll(resources);

        Map<String, ResourceSet> result = underTest
                .getCategorizedResourceSets();

        ResourceSet uncategorizableResources = underTest
                .getUncategorizableResources();

        assertMapKeysEqual(result, GROUP_1_1);
        assertThat(result.get(GROUP_1_1),
                containsExactly(createResources(TYPE_1, 1)));

        assertNotNull(uncategorizableResources);
        assertThat(uncategorizableResources,
                containsExactly(createResources(TYPE_2, 2)));
    }

    @Test
    public void addResourceWithMultipleCategoriesCreatesMultipleCategories() {
        setUpCategory(categorizer1, 1, GROUP_1_1, GROUP_1_2);

        testResources.add(createResource(1));

        Map<String, ResourceSet> result = underTest
                .getCategorizedResourceSets();

        assertMapKeysEqual(result, GROUP_1_1, GROUP_1_2);
        assertThat(result.get(GROUP_1_1),
                containsExactly(createResources(1)));
        assertThat(result.get(GROUP_1_2),
                containsExactly(createResources(1)));
    }

    // XXX
    @Test
    public void addUncategorizableResourcestoEmptyGroupEventFired() {
        setUpNullCategorizer(categorizer3);
        underTest.setCategorizer(categorizer3);
        underTest.addHandler(changeHandler);

        testResources.addAll(createResources(TYPE_1, 1, 2));
        UncategorizableResourceGroupingChange changes = captureUncategorizableChanges();

        LightweightCollection<Resource> addedResources = changes.addedResources;
        LightweightCollection<Resource> removedResources = changes.removedResources;

        assertThat(changes.resourceSet, containsExactly(testResources));
        assertTrue(addedResources.contains(createResource(TYPE_1, 1)));
        assertTrue(addedResources.contains(createResource(TYPE_1, 2)));
        assertTrue(removedResources.isEmpty());
    }

    // XXX
    @Test
    public void addUncategorizableResourcestoEmptyGroupSetContent() {
        setUpNullCategorizer(categorizer3);
        underTest.setCategorizer(categorizer3);

        testResources.addAll(createResources(TYPE_1, 1, 2));
        ResourceSet result = underTest.getUncategorizableResources();

        assertNotNull(result);
        assertThat(result, containsExactly(testResources));
    }

    public List<CategorizableResourceGroupingChange> captureChanges() {
        ResourceGroupingChangedEvent resourceChangedEvent = captureResourceChangedEvent();
        return resourceChangedEvent.getChanges().toList();
    }

    public ResourceGroupingChangedEvent captureResourceChangedEvent() {
        ArgumentCaptor<ResourceGroupingChangedEvent> eventCaptor = ArgumentCaptor
                .forClass(ResourceGroupingChangedEvent.class);
        verify(changeHandler, times(1)).onResourceCategoriesChanged(
                eventCaptor.capture());
        ResourceGroupingChangedEvent resourceChangedEvent = eventCaptor
                .getValue();
        return resourceChangedEvent;
    }

    public UncategorizableResourceGroupingChange captureUncategorizableChanges() {
        ResourceGroupingChangedEvent resourceChangedEvent = captureResourceChangedEvent();
        return resourceChangedEvent.getUncategorizableResourceChanges();
    }

    @Test
    public void categorizeResources() {
        /*
         * TODO: This test was migrated here. Check if case is already covered
         * by other tests.
         */
        ResourceSet resources1 = createResources(TYPE_1, 1, 3, 4);
        setUpCategory(categorizer1, createResource(TYPE_1, 1), GROUP_1_1);
        setUpCategory(categorizer1, createResource(TYPE_1, 3), GROUP_1_1);
        setUpCategory(categorizer1, createResource(TYPE_1, 4), GROUP_1_1);

        ResourceSet resources2 = createResources(TYPE_2, 2, 4);
        setUpCategory(categorizer1, createResource(TYPE_2, 2), GROUP_1_2);
        setUpCategory(categorizer1, createResource(TYPE_2, 4), GROUP_1_2);

        ResourceSet resources = toResourceSet(resources1, resources2);

        testResources.addAll(resources);
        Map<String, ResourceSet> result = underTest
                .getCategorizedResourceSets();

        assertEquals(2, result.size());
        assertTrue(result.containsKey(GROUP_1_1));
        assertTrue(result.get(GROUP_1_1).containsEqualResources(resources1));
        assertTrue(result.containsKey(GROUP_1_2));
        assertTrue(result.get(GROUP_1_2).containsEqualResources(resources2));
    }

    // XXX
    @Test
    public void changeCategorizerCreatesTwoCategorizedFromTwoUncategorizedEventsFired() {
        setUpNullCategorizer(categorizer2);
        underTest.setCategorizer(categorizer2);
        testResources.addAll(createResources(TYPE_1, 1, 2));

        setUpCategory(categorizer3, createResource(TYPE_1, 1), GROUP_1_1);
        setUpCategory(categorizer3, createResource(TYPE_1, 2), GROUP_1_1);

        underTest.addHandler(changeHandler);

        underTest.setCategorizer(categorizer3);

        ResourceGroupingChangedEvent event = captureResourceChangedEvent();
        UncategorizableResourceGroupingChange uncategorizableResourceChanges = event
                .getUncategorizableResourceChanges();
        LightweightList<CategorizableResourceGroupingChange> changes = event
                .getChanges();

        assertContentEquals(uncategorizableResourceChanges.removedResources,
                createResources(TYPE_1, 1, 2));
        assertTrue(uncategorizableResourceChanges.addedResources.isEmpty());

        assertContentEquals(
                toSet(newGroupCreatedDelta(GROUP_1_1,
                        createResources(TYPE_1, 1, 2))), changes.toList());
    }

    // XXX
    @Test
    public void changeCategorizerCreatesTwoCategorizedFromTwoUncategorizedSetContent() {
        setUpNullCategorizer(categorizer2);
        underTest.setCategorizer(categorizer2);
        testResources.addAll(createResources(TYPE_1, 1, 2));

        setUpCategory(categorizer3, createResource(TYPE_1, 1), GROUP_1_1);
        setUpCategory(categorizer3, createResource(TYPE_1, 2), GROUP_1_1);

        underTest.setCategorizer(categorizer3);

        Map<String, ResourceSet> categorizedResourceSets = underTest
                .getCategorizedResourceSets();
        ResourceSet uncategorizedResources = underTest
                .getUncategorizableResources();

        assertTrue(uncategorizedResources.isEmpty());
        assertMapKeysEqual(categorizedResourceSets, GROUP_1_1);
        assertThat(categorizedResourceSets.get(GROUP_1_1),
                containsExactly(createResources(TYPE_1, 1, 2)));
    }

    // XXX
    @Test
    public void changeCategorizerCreatesTwoUncategorizedFrom2CategorizedResourcesEventFired() {
        setUpCategory(categorizer2, createResource(TYPE_1, 1), GROUP_1_1);
        setUpCategory(categorizer2, createResource(TYPE_1, 2), GROUP_1_1);

        underTest.setCategorizer(categorizer2);
        testResources.addAll(createResources(TYPE_1, 1, 2));

        underTest.addHandler(changeHandler);

        setUpNullCategory(categorizer3, createResource(TYPE_1, 1));
        setUpNullCategory(categorizer3, createResource(TYPE_1, 2));

        underTest.setCategorizer(categorizer3);

        UncategorizableResourceGroupingChange change = captureUncategorizableChanges();
        assertContentEquals(change.addedResources,
                createResources(TYPE_1, 1, 2));
        assertTrue(change.removedResources.isEmpty());
    }

    // XXX
    @Test
    public void changeCategorizerCreatesTwoUncategorizedFrom2CategorizedResourcesSetContent() {
        testResources.addAll(createResources(TYPE_1, 1, 2));
        setUpCategory(categorizer2, createResource(TYPE_1, 1), GROUP_1_1);
        setUpCategory(categorizer2, createResource(TYPE_1, 2), GROUP_1_1);

        underTest.setCategorizer(categorizer2);

        setUpNullCategory(categorizer3, createResource(TYPE_1, 1));
        setUpNullCategory(categorizer3, createResource(TYPE_1, 2));

        underTest.setCategorizer(categorizer3);

        ResourceSet resources = underTest.getUncategorizableResources();
        assertContentEquals(resources, createResources(TYPE_1, 1, 2));

        assertTrue(underTest.getCategorizedResourceSets().entrySet().isEmpty());
    }

    @Test
    public void changeCategorizerFiresEvents1() {
        testResources.addAll(createResources(1, 2, 3, 4, 5));
        underTest.addHandler(changeHandler);
        underTest.setCategorizer(categorizer2);

        List<CategorizableResourceGroupingChange> changes = captureChanges();

        assertContentEquals(
                toSet(newGroupRemovedDelta(GROUP_1_1, createResources(1, 2, 3)),
                        newGroupRemovedDelta(GROUP_1_2, createResources(4, 5)),
                        newGroupCreatedDelta(GROUP_2_1, createResources(1)),
                        newGroupCreatedDelta(GROUP_2_2, createResources(2)),
                        newGroupCreatedDelta(GROUP_2_3, createResources(3, 4)),
                        newGroupCreatedDelta(GROUP_2_4, createResources(4, 5))),
                changes);
    }

    @Test
    public void changeCategorizerFiresEvents2() {
        testResources.addAll(createResources(1, 2, 3, 4, 5));
        underTest.setCategorizer(categorizer2);
        underTest.addHandler(changeHandler);
        underTest.setCategorizer(categorizer1);

        List<CategorizableResourceGroupingChange> changes = captureChanges();

        assertContentEquals(
                toSet(newGroupCreatedDelta(GROUP_1_1, createResources(1, 2, 3)),
                        newGroupCreatedDelta(GROUP_1_2, createResources(4, 5)),
                        newGroupRemovedDelta(GROUP_2_1, createResources(1)),
                        newGroupRemovedDelta(GROUP_2_2, createResources(2)),
                        newGroupRemovedDelta(GROUP_2_3, createResources(3, 4)),
                        newGroupRemovedDelta(GROUP_2_4, createResources(4, 5))),
                changes);
    }

    // XXX
    @Test
    public void changeCategorizerMixedCategorizationResourcesEventsFired() {
        setUpCategory(categorizer2, createResource(TYPE_1, 1), GROUP_1_1);
        setUpCategory(categorizer2, createResource(TYPE_1, 2), GROUP_1_1);
        setUpCategory(categorizer2, createResource(TYPE_1, 3), GROUP_1_1);
        setUpNullCategory(categorizer2, createResource(TYPE_1, 4));
        setUpNullCategory(categorizer2, createResource(TYPE_1, 5));

        underTest.setCategorizer(categorizer2);

        testResources.addAll(createResources(TYPE_1, 1, 2, 3, 4, 5));

        setUpCategory(categorizer3, createResource(TYPE_1, 1), GROUP_1_1);
        setUpCategory(categorizer3, createResource(TYPE_1, 2), GROUP_1_1);
        setUpNullCategory(categorizer3, createResource(TYPE_1, 3));
        setUpNullCategory(categorizer3, createResource(TYPE_1, 4));
        setUpCategory(categorizer3, createResource(TYPE_1, 5), GROUP_1_1);

        underTest.addHandler(changeHandler);
        underTest.setCategorizer(categorizer3);

        ResourceGroupingChangedEvent event = captureResourceChangedEvent();
        UncategorizableResourceGroupingChange uncategorizableResourceChanges = event
                .getUncategorizableResourceChanges();
        LightweightList<CategorizableResourceGroupingChange> changes = event
                .getChanges();

        // TODO this illustrates somethign about the bug that we found
        // this test shows that 4 is removed and added later to the
        // uncategorizedresourceset

        // The problem is related to the fact that we never destroy the
        // uncategorized group
        // the problem is solved in categorized groups by destroying the group
        // and then recreating it

        // Possble solutions: change how both work to calculate the overall
        // change
        // do a calculation on the added and removed and remove any duplicated
        // as they will cancel
        assertContentEquals(uncategorizableResourceChanges.addedResources,
                createResources(TYPE_1, 3, 4));
        assertContentEquals(uncategorizableResourceChanges.removedResources,
                createResources(TYPE_1, 4, 5));

        // TODO this illustrates a similar issue with categorized resources
        assertContentEquals(
                toSet(newGroupRemovedDelta(GROUP_1_1,
                        createResources(TYPE_1, 1, 2, 3)),
                        newGroupCreatedDelta(GROUP_1_1,
                                createResources(TYPE_1, 1, 2, 5))),
                changes.toList());

    }

    // XXX
    @Test
    public void changeCategorizerMixedCategorizationResourcesSetContent() {
        setUpCategory(categorizer2, createResource(TYPE_1, 1), GROUP_1_1);
        setUpCategory(categorizer2, createResource(TYPE_1, 2), GROUP_1_1);
        setUpCategory(categorizer2, createResource(TYPE_1, 3), GROUP_1_1);
        setUpNullCategory(categorizer2, createResource(TYPE_1, 4));
        setUpNullCategory(categorizer2, createResource(TYPE_1, 5));

        underTest.setCategorizer(categorizer2);

        testResources.addAll(createResources(TYPE_1, 1, 2, 3, 4, 5));

        setUpCategory(categorizer3, createResource(TYPE_1, 1), GROUP_1_1);
        setUpCategory(categorizer3, createResource(TYPE_1, 2), GROUP_1_1);
        setUpNullCategory(categorizer3, createResource(TYPE_1, 3));
        setUpNullCategory(categorizer3, createResource(TYPE_1, 4));
        setUpCategory(categorizer3, createResource(TYPE_1, 5), GROUP_1_1);

        underTest.setCategorizer(categorizer3);

        Map<String, ResourceSet> categorizedResourceSets = underTest
                .getCategorizedResourceSets();
        ResourceSet uncategorizedResources = underTest
                .getUncategorizableResources();

        assertContentEquals(uncategorizedResources,
                createResources(TYPE_1, 3, 4));
        assertMapKeysEqual(categorizedResourceSets, GROUP_1_1);
        assertContentEquals(categorizedResourceSets.get(GROUP_1_1),
                createResources(TYPE_1, 1, 2, 5));
    }

    @Test
    public void changeCategorizerUpdatesCategories1() {
        testResources.addAll(createResources(1, 2, 3, 4, 5));
        underTest.setCategorizer(categorizer2);

        Map<String, ResourceSet> result = underTest
                .getCategorizedResourceSets();

        assertMapKeysEqual(result, GROUP_2_1, GROUP_2_2, GROUP_2_3, GROUP_2_4);
        assertThat(result.get(GROUP_2_1),
                containsExactly(createResources(1)));
        assertThat(result.get(GROUP_2_2),
                containsExactly(createResources(2)));
        assertThat(result.get(GROUP_2_3),
                containsExactly(createResources(3, 4)));
        assertThat(result.get(GROUP_2_4),
                containsExactly(createResources(4, 5)));
    }

    @Test
    public void changeCategorizerUpdatesCategories2() {
        testResources.addAll(createResources(1, 2, 3, 4, 5));
        underTest.setCategorizer(categorizer2);
        underTest.setCategorizer(categorizer1);

        Map<String, ResourceSet> result = underTest
                .getCategorizedResourceSets();

        assertMapKeysEqual(result, GROUP_1_1, GROUP_1_2);
        assertThat(result.get(GROUP_1_1),
                containsExactly(createResources(1, 2, 3)));
        assertThat(result.get(GROUP_1_2),
                containsExactly(createResources(4, 5)));
    }

    @Test
    public void changeCategorizerUpdatesCategoriesAfterAddAllTwiceAndRemoveAll() {
        testResources.addAll(createResources(1, 2, 3, 4, 5));
        testResources.addAll(createResources(1, 2));
        testResources.removeAll(createResources(1, 2));
        underTest.setCategorizer(categorizer2);

        Map<String, ResourceSet> result = underTest
                .getCategorizedResourceSets();

        assertMapKeysEqual(result, GROUP_2_3, GROUP_2_4);
        assertThat(result.get(GROUP_2_3),
                containsExactly(createResources(3, 4)));
        assertThat(result.get(GROUP_2_4),
                containsExactly(createResources(4, 5)));
    }

    @Test
    public void changeCategorizerUpdatesCategoriesAfterRemoveAll() {
        testResources.addAll(createResources(1, 2, 3, 4, 5));
        testResources.removeAll(createResources(1, 2));
        underTest.setCategorizer(categorizer2);

        Map<String, ResourceSet> result = underTest
                .getCategorizedResourceSets();

        assertMapKeysEqual(result, GROUP_2_3, GROUP_2_4);
        assertThat(result.get(GROUP_2_3),
                containsExactly(createResources(3, 4)));
        assertThat(result.get(GROUP_2_4),
                containsExactly(createResources(4, 5)));
    }

    @Test
    public void changeToSameCategorizerDoesNotFireEvent() {
        testResources.addAll(createResources(1, 2, 3, 4, 5));
        underTest.addHandler(changeHandler);
        underTest.setCategorizer(categorizer1);

        verify(changeHandler, times(0)).onResourceCategoriesChanged(
                any(ResourceGroupingChangedEvent.class));
    }

    @Test
    public void createCategories() {
        testResources.addAll(createResources(1, 2, 3, 4, 5));

        Map<String, ResourceSet> result = underTest
                .getCategorizedResourceSets();

        assertMapKeysEqual(result, GROUP_1_1, GROUP_1_2);
        assertThat(result.get(GROUP_1_1),
                containsExactly(createResources(1, 2, 3)));
        assertThat(result.get(GROUP_1_2),
                containsExactly(createResources(4, 5)));
    }

    @Test
    public void doNotFireResourceCategoryChangesWhenNothingChangesOnRemove() {
        underTest.addHandler(changeHandler);
        testResources.removeAll(Collections.<Resource> emptyList());

        verify(changeHandler, times(0)).onResourceCategoriesChanged(
                any(ResourceGroupingChangedEvent.class));
    }

    @Test
    public void fireResourceCategoryAddedAndChangeChangeOnAdd() {
        Resource resource = createResource(1);

        underTest.addHandler(changeHandler);
        testResources.add(resource);

        List<CategorizableResourceGroupingChange> changes = captureChanges();

        assertContentEquals(
                toSet(newGroupCreatedDelta(GROUP_1_1, toResourceSet(resource))),
                changes);
    }

    // TODO test for add all --> multiple categories

    @Test
    public void fireResourceCategoryAddedAndChangedOnAddAll() {
        testResources.addAll(createResources(1, 2));
        underTest.addHandler(changeHandler);
        testResources.addAll(createResources(3, 4, 5));

        List<CategorizableResourceGroupingChange> changes = captureChanges();

        assertContentEquals(
                toSet(newGroupChangedDelta(GROUP_1_1, createResources(1, 2, 3),
                        createResources(3), null),
                        newGroupCreatedDelta(GROUP_1_2, createResources(4, 5))),
                changes);
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

                assertContentEquals(
                        toSet(newGroupCreatedDelta(GROUP_1_1,
                                createResources(1))), e.getChanges().toList());

                called[0] = true;
            }
        });

        testResources.add(createResource(1));

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

                assertContentEquals(
                        toSet(newGroupCreatedDelta(GROUP_1_1,
                                createResources(1, 2, 3))), e.getChanges()
                                .toList());

                called[0] = true;
            }
        });

        testResources.addAll(createResources(1, 2, 3));

        assertEquals(true, called[0]);
    }

    @Test
    public void fireResourceCategoryRemovedAndUpdateChangesWhenRemovingOneAndAHalfCategories() {
        ResourceSet allResources = new DefaultResourceSet();
        allResources.addAll(createResources(1, 2, 3, 4));

        testResources.addAll(createResources(1, 2, 3, 4, 5));
        underTest.addHandler(changeHandler);
        testResources.removeAll(allResources);

        List<CategorizableResourceGroupingChange> changes = captureChanges();

        Set<CategorizableResourceGroupingChange> expectedChanges = toSet(
                CategorizableResourceGroupingChange.newGroupRemovedDelta(
                        GROUP_1_1, createResources(1, 2, 3)),
                newGroupChangedDelta(GROUP_1_2, createResources(5), null,
                        createResources(4)));

        assertContentEquals(expectedChanges, changes);
    }

    @Test
    public void fireResourceCategoryRemovedChangeOnRemove() {
        Resource resource = createResource(1);

        testResources.add(resource);
        underTest.addHandler(changeHandler);
        testResources.remove(resource);

        List<CategorizableResourceGroupingChange> changes = captureChanges();

        assertContentEquals(
                toSet(CategorizableResourceGroupingChange.newGroupRemovedDelta(
                        GROUP_1_1, toResourceSet(resource))), changes);
    }

    @Test
    public void fireResourceCategoryRemovedChangeOnRemoveAll() {
        testResources.addAll(createResources(1, 2, 3));
        underTest.addHandler(changeHandler);
        testResources.removeAll(createResources(1, 2, 3));

        List<CategorizableResourceGroupingChange> changes = captureChanges();

        assertContentEquals(
                toSet(CategorizableResourceGroupingChange.newGroupRemovedDelta(
                        GROUP_1_1, createResources(1, 2, 3))), changes);
    }

    @Test
    public void fireResourceCategoryRemovedChangesWhenRemovingTwoCategories() {
        ResourceSet allResources = new DefaultResourceSet();
        allResources.addAll(createResources(1, 2, 3, 4, 5));

        testResources.addAll(createResources(1, 2, 3, 4, 5));
        underTest.addHandler(changeHandler);
        testResources.removeAll(allResources);

        List<CategorizableResourceGroupingChange> changes = captureChanges();

        Set<CategorizableResourceGroupingChange> expectedChanges = toSet(
                CategorizableResourceGroupingChange.newGroupRemovedDelta(
                        GROUP_1_1, createResources(1, 2, 3)),
                CategorizableResourceGroupingChange.newGroupRemovedDelta(
                        GROUP_1_2, createResources(4, 5)));

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
        testResources.addAll(resources);

        underTest.setCategorizer(categorizer2);

        Set<String> result = underTest.getGroupIds(createResources(2, 3));

        assertContentEquals(toSet(GROUP_2_1, GROUP_1_2), result);
    }

    @Test
    public void getGroupsAfterAddAllAndRemove() {
        setUpCategory(categorizer1, 1, GROUP_1_1);
        setUpCategory(categorizer1, 2, GROUP_1_2);
        setUpCategory(categorizer1, 3, GROUP_1_1);
        ResourceSet resources = createResources(1, 2, 3);
        testResources.addAll(resources);
        testResources.remove(createResource(3));

        // 3 is not contained any more
        Set<String> result = underTest.getGroupIds(createResources(2, 3));

        assertContentEquals(toSet(GROUP_1_2), result);
    }

    @Test
    public void getGroupsAfterAddAllReturningExcludingOneGroup() {
        setUpCategory(categorizer1, 1, GROUP_1_1);
        setUpCategory(categorizer1, 2, GROUP_1_2);
        ResourceSet resources = createResources(1, 2);
        testResources.addAll(resources);

        Set<String> result = underTest.getGroupIds(createResources(1));

        assertContentEquals(toSet(GROUP_1_1), result);
    }

    @Test
    public void getGroupsAfterAddAllReturningNothingForNotIncludedResource() {
        setUpCategory(categorizer1, 1, GROUP_1_1);
        ResourceSet resources = createResources(1);
        resources.addAll(resources);

        Set<String> result = underTest.getGroupIds(createResources(2));

        assertEquals(true, result.isEmpty());
    }

    @Test
    public void getGroupsAfterAddAllReturningOneGroupForSeveralResources() {
        setUpCategory(categorizer1, 1, GROUP_1_1);
        setUpCategory(categorizer1, 2, GROUP_1_1);
        ResourceSet resources = createResources(1, 2);
        testResources.addAll(resources);

        Set<String> result = underTest.getGroupIds(resources);

        assertContentEquals(toSet(GROUP_1_1), result);
    }

    @Test
    public void getGroupsAfterAddAllReturningSingleGroupForSingleResource() {
        setUpCategory(categorizer1, 1, GROUP_1_1);
        ResourceSet resources = createResources(1);
        testResources.addAll(resources);

        Set<String> result = underTest.getGroupIds(resources);

        assertContentEquals(toSet(GROUP_1_1), result);
    }

    @Test
    public void getGroupsAfterAddAllReturningTwoGroupsForSingleResource() {
        setUpCategory(categorizer1, 1, GROUP_1_1, GROUP_1_2);
        ResourceSet resources = createResources(1);
        testResources.addAll(resources);

        Set<String> result = underTest.getGroupIds(resources);

        assertContentEquals(toSet(GROUP_1_1, GROUP_1_2), result);
    }

    @Test
    public void noResourceSetEventsFiredOnCompleteCategoryRemovalViaRemove() {
        testResources.add(createResource(1));
        ResourceSet categorizedResources = underTest
                .getCategorizedResourceSets().get(GROUP_1_1);
        ResourceSetChangedEventHandler resourcesChangedHandler = mock(ResourceSetChangedEventHandler.class);
        categorizedResources.addEventHandler(resourcesChangedHandler);
        testResources.remove(createResource(1));

        verifyOnResourceSetChanged(0, resourcesChangedHandler);
    }

    @Test
    public void noResourceSetEventsFiredOnCompleteCategoryRemovalViaRemoveAll() {
        testResources.addAll(createResources(1, 2, 3));
        ResourceSet categorizedResources = underTest
                .getCategorizedResourceSets().get(GROUP_1_1);
        ResourceSetChangedEventHandler resourcesChangedHandler = mock(ResourceSetChangedEventHandler.class);
        categorizedResources.addEventHandler(resourcesChangedHandler);
        testResources.removeAll(createResources(1, 2, 3));

        verifyOnResourceSetChanged(0, resourcesChangedHandler);
    }

    // XXX
    @Test
    public void removeAndAddUncategorizableToSetWithTwoUncategorizableEventsFired() {
        setUpNullCategorizer(categorizer3);
        underTest.setCategorizer(categorizer3);
        testResources.addAll(createResources(TYPE_1, 1, 2));
        underTest.addHandler(changeHandler);

        testResources.change(createResources(TYPE_1, 3),
                createResources(TYPE_1, 1));
        UncategorizableResourceGroupingChange changes = captureUncategorizableChanges();

        assertContentEquals(changes.addedResources, createResources(TYPE_1, 3));
        assertContentEquals(changes.removedResources,
                createResources(TYPE_1, 1));
    }

    // XXX
    @Test
    public void removeAndAddUncategorizableToSetWithTwoUncategorizableSetContent() {
        setUpNullCategorizer(categorizer3);
        underTest.setCategorizer(categorizer3);
        testResources.addAll(createResources(TYPE_1, 1, 2));

        testResources.change(createResources(TYPE_1, 3),
                createResources(TYPE_1, 1));

        ResourceSet resources = underTest.getUncategorizableResources();
        assertContentEquals(createResources(TYPE_1, 2, 3), resources);
    }

    // XXX
    @Test
    public void removeOneUncategorizedResourceAndOneCategorizedResourceFromGroupingEventFired() {
        setUpCategory(categorizer1, createResource(TYPE_1, 1), GROUP_1_1);
        setUpNullCategory(categorizer1, createResource(TYPE_1, 2));
        underTest.setCategorizer(categorizer1);
        testResources.addAll(createResources(TYPE_1, 1, 2));

        underTest.addHandler(changeHandler);
        testResources.removeAll(createResources(TYPE_1, 1, 2));

        ResourceGroupingChangedEvent event = captureResourceChangedEvent();
        LightweightList<CategorizableResourceGroupingChange> categorizableEvents = event
                .getChanges();
        UncategorizableResourceGroupingChange uncategorizableEvent = event
                .getUncategorizableResourceChanges();

        assertContentEquals(
                toSet(newGroupRemovedDelta(GROUP_1_1,
                        createResources(TYPE_1, 1))),
                categorizableEvents.toList());

        assertTrue(uncategorizableEvent.addedResources.isEmpty());
        assertContentEquals(uncategorizableEvent.removedResources,
                createResources(TYPE_1, 2));
    }

    // XXX
    @Test
    public void removeOneUncategorizedResourceAndOneCategorizedResourceFromGroupingSetContent() {
        setUpCategory(categorizer1, createResource(TYPE_1, 1), GROUP_1_1);
        setUpNullCategory(categorizer1, createResource(TYPE_1, 2));
        underTest.setCategorizer(categorizer1);
        testResources.addAll(createResources(TYPE_1, 1, 2));

        testResources.removeAll(createResources(TYPE_1, 1, 2));

        ResourceSet uncategorizedResources = underTest
                .getUncategorizableResources();

        assertThat(underTest.containsGroup(GROUP_1_1), is(false));
        assertTrue(uncategorizedResources.isEmpty());
    }

    @Test
    public void removeResourceSet() {
        testResources.addAll(createResources(1, 2, 3, 4, 5));
        testResources.removeAll(createResources(1, 2, 3));

        Map<String, ResourceSet> result = underTest
                .getCategorizedResourceSets();

        assertEquals(1, result.size());
        assertTrue(result.containsKey(GROUP_1_2));
        assertTrue(result.get(GROUP_1_2).containsEqualResources(
                createResources(4, 5)));
    }

    // XXX
    @Test
    public void removeUncategorizableResourcesFromGroupFireEvent() {
        testResources.addAll(createResources(TYPE_1, 1, 2));
        setUpNullCategorizer(categorizer3);
        underTest.setCategorizer(categorizer3);

        underTest.addHandler(changeHandler);
        testResources.removeAll(createResources(TYPE_1, 1, 2));

        UncategorizableResourceGroupingChange changes = captureUncategorizableChanges();

        assertContentEquals(changes.removedResources,
                createResources(TYPE_1, 1, 2));
        assertTrue(changes.addedResources.isEmpty());
    }

    // XXX
    @Test
    public void removeUncategorizableResourcesFromGroupSetContent() {
        setUpNullCategorizer(categorizer3);
        underTest.setCategorizer(categorizer3);
        testResources.addAll(createResources(TYPE_1, 1, 2));

        testResources.removeAll(createResources(TYPE_1, 1, 2));

        ResourceSet uncategorizableResources = underTest
                .getUncategorizableResources();

        assertTrue(uncategorizableResources.isEmpty());
        assertTrue(underTest.getResourceSet().isEmpty());
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        underTest = new ResourceGrouping(categorizer1,
                new DefaultResourceSetFactory());

        testResources = new DefaultResourceSet();
        underTest.setResourceSet(testResources);

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

        setUpNullCategorizer(categorizer3);

    }

    private void setUpCategory(ResourceMultiCategorizer categorizer,
            int resourceId, String... category) {

        setUpCategory(categorizer, createResource(resourceId), category);
    }

    public void setUpCategory(ResourceMultiCategorizer categorizer,
            Resource resource, String... category) {

        when(categorizer.getCategories(resource)).thenReturn(toSet(category));
        when(categorizer.canCategorize(resource)).thenReturn(true);
    }

    public void setUpNullCategorizer(ResourceMultiCategorizer categorizer) {
        when(categorizer.getCategories(any(Resource.class))).thenReturn(null);
        when(categorizer.canCategorize(any(Resource.class))).thenReturn(false);
    }

    public void setUpNullCategory(ResourceMultiCategorizer categorizer,
            Resource resource) {
        when(categorizer.getCategories(resource)).thenReturn(null);
        when(categorizer.canCategorize(resource)).thenReturn(false);
    }
}
