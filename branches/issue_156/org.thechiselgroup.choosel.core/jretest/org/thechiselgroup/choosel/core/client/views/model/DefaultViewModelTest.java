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
package org.thechiselgroup.choosel.core.client.views.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.thechiselgroup.choosel.core.client.test.HamcrestResourceMatchers.containsExactly;
import static org.thechiselgroup.choosel.core.client.test.ResourcesTestHelper.emptyLightweightCollection;
import static org.thechiselgroup.choosel.core.client.test.ResourcesTestHelper.eqViewItems;
import static org.thechiselgroup.choosel.core.client.test.ResourcesTestHelper.resourceItemsForResourceSets;
import static org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory.TYPE_1;
import static org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory.TYPE_2;
import static org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory.createLabeledResources;
import static org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory.createResource;
import static org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory.createResources;
import static org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory.toResourceSet;
import static org.thechiselgroup.choosel.core.client.views.model.DefaultViewModelTestHelper.captureAddedViewItems;
import static org.thechiselgroup.choosel.core.client.views.model.DefaultViewModelTestHelper.captureAddedViewItemsAsList;
import static org.thechiselgroup.choosel.core.client.views.model.DefaultViewModelTestHelper.captureUpdatedViewItems;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.core.client.resources.DataType;
import org.thechiselgroup.choosel.core.client.resources.Resource;
import org.thechiselgroup.choosel.core.client.resources.ResourceByPropertyMultiCategorizer;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.views.model.ViewItem.Status;
import org.thechiselgroup.choosel.core.client.views.model.ViewItem.Subset;
import org.thechiselgroup.choosel.core.client.views.resolvers.FirstResourcePropertyResolver;

public class DefaultViewModelTest {

    private Slot slot;

    private DefaultViewModel underTest;

    private DefaultViewModelTestHelper helper;

    @Test
    public void changeTextSlotMapping() {
        Resource resource = new Resource("test:1");
        resource.putValue("text1", "t1");
        resource.putValue("text2", "t2");

        underTest.setResolver(slot, new FirstResourcePropertyResolver("text1",
                DataType.TEXT));
        helper.getContainedResources().add(resource);
        underTest.setResolver(slot, new FirstResourcePropertyResolver("text2",
                DataType.TEXT));

        List<ViewItem> resourceItems = underTest.getViewItems().toList();
        assertEquals(1, resourceItems.size());
        ViewItem resourceItem = resourceItems.get(0);

        assertEquals("t2", resourceItem.getValue(slot));
    }

    @Test
    public void createResourceItemsWhenResourcesAreAdded() {
        helper.getContainedResources().addAll(createResources(TYPE_1, 1));
        helper.getContainedResources().add(createResource(TYPE_2, 2));

        List<LightweightCollection<ViewItem>> allValues = captureAddedViewItems(
                helper.getViewContentDisplay(), 2);

        List<ViewItem> set1 = allValues.get(0).toList();
        assertEquals(1, set1.size());
        assertThat(set1.get(0).getResources(),
                containsExactly(createResources(TYPE_1, 1)));

        List<ViewItem> set2 = allValues.get(1).toList();
        assertEquals(1, set2.size());
        assertThat(set2.get(0).getResources(),
                containsExactly(createResources(TYPE_2, 2)));
    }

    @Test
    public void deselectResourceItemWhenResourceRemovedFromSelection() {
        ResourceSet resources = createResources(1);

        helper.getContainedResources().addAll(resources);
        List<ViewItem> viewItems = captureAddedViewItemsAsList(helper
                .getViewContentDisplay());

        helper.getSelectedResources().addAll(resources);
        helper.getSelectedResources().removeAll(resources);

        assertThat(viewItems.get(0).getResources(Subset.SELECTED),
                containsExactly(createResources()));
    }

    @Test
    public void grouping() {
        Resource r1 = new Resource("test:1");
        r1.putValue("property1", "value1-1");
        r1.putValue("property2", "value2");

        Resource r2 = new Resource("test:2");
        r2.putValue("property1", "value1-2");
        r2.putValue("property2", "value2");

        helper.getContainedResources().addAll(toResourceSet(r1, r2));
        underTest.getResourceGrouping().setCategorizer(
                new ResourceByPropertyMultiCategorizer("property2"));

        List<ViewItem> resourceItems = underTest.getViewItems().toList();
        assertEquals(1, resourceItems.size());
        ResourceSet resourceItemResources = resourceItems.get(0).getResources();
        assertEquals(2, resourceItemResources.size());
        assertEquals(true, resourceItemResources.contains(r1));
        assertEquals(true, resourceItemResources.contains(r2));
    }

    @Test
    public void groupingChangeChangesCategory() {
        Resource resource = new Resource("test:1");
        resource.putValue("text1", "category1");
        resource.putValue("text2", "category2");

        underTest.getResourceGrouping().setCategorizer(
                new ResourceByPropertyMultiCategorizer("text1"));
        helper.getContainedResources().add(resource);
        underTest.getResourceGrouping().setCategorizer(
                new ResourceByPropertyMultiCategorizer("text2"));

        List<ViewItem> resourceItems = underTest.getViewItems().toList();
        assertEquals(1, resourceItems.size());
        ViewItem resourceItem = resourceItems.get(0);
        assertEquals("category2", resourceItem.getViewItemID());
    }

    /**
     * Removing a resource item and adding another resource item, both with the
     * same category, in one operation caused a bug.
     */
    @Test
    public void groupingChangeWithRemovingAndAddingSameCategory() {
        Resource resource = new Resource("test:1");
        resource.putValue("text1", "category1");
        resource.putValue("text2", "category1");

        underTest.getResourceGrouping().setCategorizer(
                new ResourceByPropertyMultiCategorizer("text1"));
        helper.getContainedResources().add(resource);
        underTest.getResourceGrouping().setCategorizer(
                new ResourceByPropertyMultiCategorizer("text2"));

        List<ViewItem> resourceItems = underTest.getViewItems().toList();
        assertEquals(1, resourceItems.size());
        ViewItem resourceItem = resourceItems.get(0);
        assertEquals("category1", resourceItem.getViewItemID());
    }

    @Test
    public void highlightedResourceSetOnCreatedResourceItems() {
        ResourceSet resources = createResources(TYPE_1, 1, 3, 4);

        helper.getHighlightedResources().addAll(resources);
        helper.getContainedResources().addAll(resources);
        List<ViewItem> viewItems = captureAddedViewItemsAsList(helper
                .getViewContentDisplay());

        assertThat(viewItems.get(0).getResources(Subset.HIGHLIGHTED),
                containsExactly(resources));
    }

    @Test
    public void highlightedResourcesGetAddedToResourceItemOnlyOnceWhenSeveralResourcesFromItemAddedToHoverModel() {
        ResourceSet resources = createResources(1, 2);

        helper.getContainedResources().addAll(resources);
        List<ViewItem> viewItems = captureAddedViewItemsAsList(helper
                .getViewContentDisplay());

        helper.getHighlightedResources().addAll(resources);

        assertThat(viewItems.get(0).getResources(Subset.HIGHLIGHTED),
                containsExactly(resources));
    }

    @Test
    public void highlightedResourcesGetAddedToResourceItemWhenHoverModelContainsAdditionalResources() {
        Resource resource2 = createResource(2);
        Resource resource1 = createResource(1);
        ResourceSet viewResources = toResourceSet(resource2);
        ResourceSet highlightedResources2 = toResourceSet(resource1, resource2);

        helper.getContainedResources().addAll(viewResources);
        List<ViewItem> viewItems = captureAddedViewItemsAsList(helper
                .getViewContentDisplay());

        helper.getHighlightedResources().addAll(highlightedResources2);

        assertThat(viewItems.get(0).getResources(Subset.HIGHLIGHTED),
                containsExactly(viewResources));
    }

    @Test
    public void highlightedResourcesGetAddedToResourceItemWhenResourcesAddedToHoverModel() {
        ResourceSet resources = createResources(1);

        helper.getContainedResources().addAll(resources);
        List<ViewItem> viewItems = captureAddedViewItemsAsList(helper
                .getViewContentDisplay());

        helper.getHighlightedResources().addAll(resources);

        assertThat(viewItems.get(0).getResources(Subset.HIGHLIGHTED),
                containsExactly(createResources(1)));
    }

    @Test
    public void highlightedResourcesGetRemovedFromViewItemWhenResourcesRemovedFromHighlightingSet() {
        ResourceSet resources = createResources(1);

        helper.getContainedResources().addAll(resources);
        List<ViewItem> viewItems = captureAddedViewItemsAsList(helper
                .getViewContentDisplay());

        helper.getHighlightedResources().addAll(resources);
        helper.getHighlightedResources().removeAll(resources);

        assertThat(viewItems.get(0).getResources(Subset.HIGHLIGHTED),
                containsExactly(createResources()));
    }

    @Test
    public void initializesContentDisplay() {
        verify(helper.getViewContentDisplay(), times(1)).init(
                any(ViewContentDisplayCallback.class));
    }

    @Test
    public void selectViewItemWhenResourceAddedToSelection() {
        helper.getContainedResources().add(createResource(1));
        List<ViewItem> viewItems = captureAddedViewItemsAsList(helper
                .getViewContentDisplay());

        helper.getSelectedResources().add(createResource(1));

        assertThat(viewItems.get(0).getResources(Subset.SELECTED),
                containsExactly(createResources(1)));
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        slot = new Slot("1", "Description", DataType.TEXT);

        helper = new DefaultViewModelTestHelper();
        helper.setSlots(slot);
        underTest = helper.createTestViewModel();
    }

    // TODO check highlighted resources in resource item
    @Test
    public void updateCalledWhenHighlightingChanges() {
        ResourceSet resources = createResources(1);

        helper.getContainedResources().addAll(resources);
        LightweightCollection<ViewItem> addedViewItems = captureAddedViewItems(helper
                .getViewContentDisplay());

        helper.getHighlightedResources().addAll(resources);

        verify(helper.getViewContentDisplay(), times(1)).update(
                emptyLightweightCollection(ViewItem.class),
                eqViewItems(addedViewItems),
                emptyLightweightCollection(ViewItem.class),
                emptyLightweightCollection(Slot.class));
    }

    @Test
    public void updateCalledWhenResourcesRemoved() {
        ResourceSet resources1 = createResources(TYPE_1, 1);
        ResourceSet resources2 = createResources(TYPE_2, 2);
        ResourceSet resources = toResourceSet(resources1, resources2);

        helper.getContainedResources().addAll(resources);
        LightweightCollection<ViewItem> addedViewItems = captureAddedViewItems(helper
                .getViewContentDisplay());

        helper.getContainedResources().removeAll(resources);
        verify(helper.getViewContentDisplay(), times(1)).update(
                emptyLightweightCollection(ViewItem.class),
                emptyLightweightCollection(ViewItem.class),
                eqViewItems(addedViewItems),
                emptyLightweightCollection(Slot.class));
    }

    @Test
    public void updateCalledWhenSelectionChanges() {
        ResourceSet resources = createResources(1);

        helper.getContainedResources().addAll(resources);
        LightweightCollection<ViewItem> addedViewItems = captureAddedViewItems(helper
                .getViewContentDisplay());

        helper.getSelectedResources().add(createResource(1));

        verify(helper.getViewContentDisplay(), times(1)).update(
                emptyLightweightCollection(ViewItem.class),
                eqViewItems(addedViewItems),
                emptyLightweightCollection(ViewItem.class),
                emptyLightweightCollection(Slot.class));
    }

    @Test
    public void updateCalledWith2ViewItemsWhenAddingMixedResourceSet() {
        ResourceSet resources1 = createResources(TYPE_1, 1, 3, 4);
        ResourceSet resources2 = createResources(TYPE_2, 4, 2);
        ResourceSet resources = toResourceSet(resources1, resources2);

        helper.getContainedResources().addAll(resources);

        verify(helper.getViewContentDisplay(), times(1)).update(
                resourceItemsForResourceSets(resources1, resources2),
                emptyLightweightCollection(ViewItem.class),
                emptyLightweightCollection(ViewItem.class),
                emptyLightweightCollection(Slot.class));
    }

    @Test
    public void updateNeverCalledOnHoverModelChangeThatDoesNotAffectViewResources() {
        helper.getContainedResources().add(createResource(2));
        helper.getHighlightedResources().add(createResource(1));

        verify(helper.getViewContentDisplay(), never()).update(
                emptyLightweightCollection(ViewItem.class),
                any(LightweightCollection.class),
                emptyLightweightCollection(ViewItem.class),
                emptyLightweightCollection(Slot.class));
    }

    /**
     * @see <a
     *      href="http://code.google.com/p/choosel/issues/detail?id=149">"Issue 149"</a>
     */
    @Test
    public void viewItemIsHighlightedOnChangeWhenAddedResourcesAreAlreadyHighlighted() {
        ResourceSet originalResources = createResources(TYPE_1, 1);
        ResourceSet addedResources = createResources(TYPE_1, 2);

        helper.getContainedResources().addAll(originalResources);
        helper.getHighlightedResources().addAll(addedResources);
        helper.getContainedResources().addAll(addedResources);

        List<ViewItem> updatedViewItem = captureUpdatedViewItems(
                helper.getViewContentDisplay()).toList();

        assertEquals(1, updatedViewItem.size());
        ViewItem viewItem = updatedViewItem.get(0);

        assertEquals(Status.PARTIAL, viewItem.getStatus(Subset.HIGHLIGHTED));
        assertThat(viewItem.getResources(Subset.HIGHLIGHTED),
                containsExactly(addedResources));
    }

    /**
     * @see <a
     *      href="http://code.google.com/p/choosel/issues/detail?id=149">"Issue 149"</a>
     */
    @Test
    public void viewItemIsNotHighlightedOnChangeWhenOnlyRemovedResourcesAreHighlighted() {
        ResourceSet originalResources = createLabeledResources(TYPE_1, 1, 2);
        ResourceSet removedResources = createResources(TYPE_1, 2);

        helper.getHighlightedResources().addAll(removedResources);
        helper.getContainedResources().addAll(originalResources);
        helper.getHighlightedResources().removeAll(removedResources);

        List<ViewItem> updatedViewItem = captureUpdatedViewItems(
                helper.getViewContentDisplay()).toList();
        assertEquals(1, updatedViewItem.size());
        ViewItem viewItem = updatedViewItem.get(0);

        assertEquals(Status.NONE, viewItem.getStatus(Subset.HIGHLIGHTED));
        assertEquals(true, viewItem.getResources(Subset.HIGHLIGHTED).isEmpty());
    }

    /**
     * @see <a
     *      href="http://code.google.com/p/choosel/issues/detail?id=149">"Issue 149"</a>
     */
    @Test
    public void viewItemIsNotSelectedOnChangeWhenOnlyRemovedResourcesAreSelected() {
        ResourceSet originalResources = createLabeledResources(TYPE_1, 1, 2);
        ResourceSet removedResources = createResources(TYPE_1, 2);

        helper.getSelectedResources().addAll(removedResources);
        helper.getContainedResources().addAll(originalResources);
        helper.getSelectedResources().removeAll(removedResources);

        List<ViewItem> updatedViewItem = captureUpdatedViewItems(
                helper.getViewContentDisplay()).toList();
        assertEquals(1, updatedViewItem.size());
        ViewItem viewItem = updatedViewItem.get(0);

        assertEquals(Status.NONE, viewItem.getStatus(Subset.SELECTED));
        assertEquals(true, viewItem.getResources(Subset.SELECTED).isEmpty());
    }

    /**
     * @see <a
     *      href="http://code.google.com/p/choosel/issues/detail?id=149">"Issue 149"</a>
     */
    @Test
    public void viewItemIsSelectedHighlightedOnChangeWhenAddedResourcesAreAlreadySelectedHighlighted() {
        ResourceSet originalResources = createResources(TYPE_1, 1);
        ResourceSet addedResources = createResources(TYPE_1, 2);

        helper.getContainedResources().addAll(originalResources);
        helper.getSelectedResources().addAll(addedResources);
        helper.getHighlightedResources().addAll(addedResources);
        helper.getContainedResources().addAll(addedResources);

        List<ViewItem> updatedViewItem = captureUpdatedViewItems(
                helper.getViewContentDisplay()).toList();
        assertEquals(1, updatedViewItem.size());
        ViewItem viewItem = updatedViewItem.get(0);

        assertEquals(Status.PARTIAL, viewItem.getStatus(Subset.SELECTED));
        assertThat(viewItem.getResources(Subset.SELECTED),
                containsExactly(addedResources));

        assertEquals(Status.PARTIAL, viewItem.getStatus(Subset.HIGHLIGHTED));
        assertThat(viewItem.getResources(Subset.HIGHLIGHTED),
                containsExactly(addedResources));
    }

    /**
     * @see <a
     *      href="http://code.google.com/p/choosel/issues/detail?id=149">"Issue 149"</a>
     */
    @Test
    public void viewItemIsSelectedOnChangeWhenAddedResourcesAreAlreadySelected() {
        ResourceSet originalResources = createResources(TYPE_1, 1);
        ResourceSet addedResources = createResources(TYPE_1, 2);

        helper.getContainedResources().addAll(originalResources);
        helper.getSelectedResources().addAll(addedResources);
        helper.getContainedResources().addAll(addedResources);

        List<ViewItem> updatedViewItem = captureUpdatedViewItems(
                helper.getViewContentDisplay()).toList();
        assertEquals(1, updatedViewItem.size());
        ViewItem viewItem = updatedViewItem.get(0);

        assertEquals(Status.PARTIAL, viewItem.getStatus(Subset.SELECTED));
        assertThat(viewItem.getResources(Subset.SELECTED),
                containsExactly(addedResources));
    }

    /**
     * @see <a
     *      href="http://code.google.com/p/choosel/issues/detail?id=123">"Issue 123"</a>
     */
    @Test
    public void viewItemIsSelectedOnCreateWhenResourcesAreAlreadySelected() {
        ResourceSet resources = createResources(1);

        helper.getSelectedResources().addAll(resources);
        helper.getContainedResources().addAll(resources);

        List<ViewItem> addedViewItem = captureAddedViewItemsAsList(helper
                .getViewContentDisplay());
        assertEquals(1, addedViewItem.size());
        ViewItem viewItem = addedViewItem.get(0);

        assertEquals(Status.FULL, viewItem.getStatus(Subset.SELECTED));
        assertThat(viewItem.getResources(Subset.SELECTED),
                containsExactly(resources));
    }
}
