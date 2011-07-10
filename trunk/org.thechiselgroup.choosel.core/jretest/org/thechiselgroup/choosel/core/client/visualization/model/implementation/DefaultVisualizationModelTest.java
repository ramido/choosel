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
package org.thechiselgroup.choosel.core.client.visualization.model.implementation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.thechiselgroup.choosel.core.shared.test.matchers.collections.CollectionMatchers.containsExactly;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.core.client.resources.Resource;
import org.thechiselgroup.choosel.core.client.resources.ResourceByPropertyMultiCategorizer;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.resources.ResourceSetTestUtils;
import org.thechiselgroup.choosel.core.client.util.DataType;
import org.thechiselgroup.choosel.core.client.visualization.model.Slot;
import org.thechiselgroup.choosel.core.client.visualization.model.ViewContentDisplayCallback;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItem;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItem.Status;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItem.Subset;
import org.thechiselgroup.choosel.core.client.visualization.resolvers.FirstResourcePropertyResolver;
import org.thechiselgroup.choosel.core.client.visualization.resolvers.FixedValueResolver;

public class DefaultVisualizationModelTest {

    private Slot slot;

    private DefaultVisualizationModel underTest;

    private DefaultVisualizationModelTestHelper helper;

    private static final String RESOURCE_PROPERTY_1 = "property1";

    @Test
    public void addingResourceSetsAddsViewItems() {
        helper.addToContainedResources(ResourceSetTestUtils.createResources(ResourceSetTestUtils.TYPE_1, 1));
        helper.addToContainedResources(ResourceSetTestUtils.createResources(ResourceSetTestUtils.TYPE_2, 2));

        assertThat(underTest.getViewItems(),
                VisualItemTestUtils.containsViewItemsForExactResourceSets(
                        ResourceSetTestUtils.createResources(ResourceSetTestUtils.TYPE_1, 1), ResourceSetTestUtils.createResources(ResourceSetTestUtils.TYPE_2, 2)));
    }

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

        List<VisualItem> resourceItems = underTest.getViewItems().toList();
        assertEquals(1, resourceItems.size());
        VisualItem resourceItem = resourceItems.get(0);

        assertEquals("t2", resourceItem.getValue(slot));
    }

    @Test
    public void deselectViewItemWhenResourceRemovedFromSelection() {
        ResourceSet resources = ResourceSetTestUtils.createResources(1);

        helper.getContainedResources().addAll(resources);

        helper.getSelectedResources().addAll(resources);
        helper.getSelectedResources().removeAll(resources);

        assertThat(
                underTest.getViewItems().getFirstElement()
                        .getResources(Subset.SELECTED),
                containsExactly(ResourceSetTestUtils.createResources()));
    }

    @Test
    public void grouping() {
        Resource r1 = new Resource("test:1");
        r1.putValue(RESOURCE_PROPERTY_1, "value1-1");
        r1.putValue("property2", "value2");

        Resource r2 = new Resource("test:2");
        r2.putValue(RESOURCE_PROPERTY_1, "value1-2");
        r2.putValue("property2", "value2");

        helper.getContainedResources().addAll(ResourceSetTestUtils.toResourceSet(r1, r2));
        underTest.getResourceGrouping().setCategorizer(
                new ResourceByPropertyMultiCategorizer("property2"));

        List<VisualItem> resourceItems = underTest.getViewItems().toList();
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

        List<VisualItem> resourceItems = underTest.getViewItems().toList();
        assertEquals(1, resourceItems.size());
        VisualItem resourceItem = resourceItems.get(0);
        assertEquals("category2", resourceItem.getId());
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

        List<VisualItem> resourceItems = underTest.getViewItems().toList();
        assertEquals(1, resourceItems.size());
        VisualItem resourceItem = resourceItems.get(0);
        assertEquals("category1", resourceItem.getId());
    }

    @Test
    public void highlightedResourceSetOnCreatedViewItems() {
        ResourceSet resources = ResourceSetTestUtils.createResources(ResourceSetTestUtils.TYPE_1, 1, 3, 4);

        helper.getHighlightedResources().addAll(resources);
        helper.getContainedResources().addAll(resources);

        assertThat(
                underTest.getViewItems().getFirstElement()
                        .getResources(Subset.HIGHLIGHTED),
                containsExactly(resources));
    }

    @Test
    public void highlightedResourcesGetAddedToViewItemWhenHoverModelContainsAdditionalResources() {
        ResourceSet viewResources = ResourceSetTestUtils.toResourceSet(ResourceSetTestUtils.createResource(2));
        ResourceSet highlightedResources2 = ResourceSetTestUtils.toResourceSet(ResourceSetTestUtils.createResource(1),
                ResourceSetTestUtils.createResource(2));

        helper.getContainedResources().addAll(viewResources);
        helper.getHighlightedResources().addAll(highlightedResources2);

        assertThat(
                underTest.getViewItems().getFirstElement()
                        .getResources(Subset.HIGHLIGHTED),
                containsExactly(viewResources));
    }

    @Test
    public void highlightedResourcesGetAddedToViewItemWhenResourcesAddedToHoverModel() {
        ResourceSet resources = ResourceSetTestUtils.createResources(1);

        helper.getContainedResources().addAll(resources);
        helper.getHighlightedResources().addAll(resources);

        assertThat(
                underTest.getViewItems().getFirstElement()
                        .getResources(Subset.HIGHLIGHTED),
                containsExactly(ResourceSetTestUtils.createResources(1)));
    }

    @Test
    public void highlightedResourcesGetRemovedFromViewItemWhenResourcesRemovedFromHighlightingSet() {
        ResourceSet resources = ResourceSetTestUtils.createResources(1);

        helper.getContainedResources().addAll(resources);
        helper.getHighlightedResources().addAll(resources);
        helper.getHighlightedResources().removeAll(resources);

        assertThat(
                underTest.getViewItems().getFirstElement()
                        .getResources(Subset.HIGHLIGHTED),
                containsExactly(ResourceSetTestUtils.createResources()));
    }

    @Test
    public void initializesContentDisplay() {
        verify(helper.getViewContentDisplay(), times(1)).init(
                any(ViewContentDisplayCallback.class));
    }

    @Test
    public void selectViewItemWhenResourceAddedToSelection() {
        helper.getContainedResources().add(ResourceSetTestUtils.createResource(1));

        helper.getSelectedResources().add(ResourceSetTestUtils.createResource(1));

        assertThat(
                underTest.getViewItems().getFirstElement()
                        .getResources(Subset.SELECTED),
                containsExactly(ResourceSetTestUtils.createResources(1)));
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        slot = new Slot("1", "Description", DataType.TEXT);

        helper = new DefaultVisualizationModelTestHelper();
        helper.setSlots(slot);
        underTest = helper.createTestViewModel();
        underTest.setResolver(slot, new FixedValueResolver("a", DataType.TEXT));
    }

    /**
     * @see <a
     *      href="http://code.google.com/p/choosel/issues/detail?id=149">"Issue 149"</a>
     */
    @Test
    public void viewItemIsHighlightedOnChangeWhenAddedResourcesAreAlreadyHighlighted() {
        ResourceSet originalResources = ResourceSetTestUtils.createResources(ResourceSetTestUtils.TYPE_1, 1);
        ResourceSet addedResources = ResourceSetTestUtils.createResources(ResourceSetTestUtils.TYPE_1, 2);

        helper.getContainedResources().addAll(originalResources);
        helper.getHighlightedResources().addAll(addedResources);
        helper.getContainedResources().addAll(addedResources);

        VisualItem viewItem = underTest.getViewItems().getFirstElement();

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
        ResourceSet originalResources = ResourceSetTestUtils.createLabeledResources(ResourceSetTestUtils.TYPE_1, 1, 2);
        ResourceSet removedResources = ResourceSetTestUtils.createResources(ResourceSetTestUtils.TYPE_1, 2);

        helper.getHighlightedResources().addAll(removedResources);
        helper.getContainedResources().addAll(originalResources);
        helper.getHighlightedResources().removeAll(removedResources);

        VisualItem viewItem = underTest.getViewItems().getFirstElement();

        assertEquals(Status.NONE, viewItem.getStatus(Subset.HIGHLIGHTED));
        assertEquals(true, viewItem.getResources(Subset.HIGHLIGHTED).isEmpty());
    }

    /**
     * @see <a
     *      href="http://code.google.com/p/choosel/issues/detail?id=149">"Issue 149"</a>
     */
    @Test
    public void viewItemIsNotSelectedOnChangeWhenOnlyRemovedResourcesAreSelected() {
        ResourceSet originalResources = ResourceSetTestUtils.createLabeledResources(ResourceSetTestUtils.TYPE_1, 1, 2);
        ResourceSet removedResources = ResourceSetTestUtils.createResources(ResourceSetTestUtils.TYPE_1, 2);

        helper.getSelectedResources().addAll(removedResources);
        helper.getContainedResources().addAll(originalResources);
        helper.getSelectedResources().removeAll(removedResources);

        VisualItem viewItem = underTest.getViewItems().getFirstElement();

        assertEquals(Status.NONE, viewItem.getStatus(Subset.SELECTED));
        assertEquals(true, viewItem.getResources(Subset.SELECTED).isEmpty());
    }

    /**
     * @see <a
     *      href="http://code.google.com/p/choosel/issues/detail?id=149">"Issue 149"</a>
     */
    @Test
    public void viewItemIsSelectedHighlightedOnChangeWhenAddedResourcesAreAlreadySelectedHighlighted() {
        ResourceSet originalResources = ResourceSetTestUtils.createResources(ResourceSetTestUtils.TYPE_1, 1);
        ResourceSet addedResources = ResourceSetTestUtils.createResources(ResourceSetTestUtils.TYPE_1, 2);

        helper.getContainedResources().addAll(originalResources);
        helper.getSelectedResources().addAll(addedResources);
        helper.getHighlightedResources().addAll(addedResources);
        helper.getContainedResources().addAll(addedResources);

        VisualItem viewItem = underTest.getViewItems().getFirstElement();

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
        ResourceSet originalResources = ResourceSetTestUtils.createResources(ResourceSetTestUtils.TYPE_1, 1);
        ResourceSet addedResources = ResourceSetTestUtils.createResources(ResourceSetTestUtils.TYPE_1, 2);

        helper.getContainedResources().addAll(originalResources);
        helper.getSelectedResources().addAll(addedResources);
        helper.getContainedResources().addAll(addedResources);

        VisualItem viewItem = underTest.getViewItems().getFirstElement();

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
        ResourceSet resources = ResourceSetTestUtils.createResources(1);

        helper.getSelectedResources().addAll(resources);
        helper.getContainedResources().addAll(resources);

        VisualItem viewItem = underTest.getViewItems().getFirstElement();

        assertEquals(Status.FULL, viewItem.getStatus(Subset.SELECTED));
        assertThat(viewItem.getResources(Subset.SELECTED),
                containsExactly(resources));
    }

}
