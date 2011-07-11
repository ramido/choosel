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
import static org.thechiselgroup.choosel.core.client.resources.ResourceSetTestUtils.TEXT_PROPERTY_1;
import static org.thechiselgroup.choosel.core.client.resources.ResourceSetTestUtils.TEXT_PROPERTY_2;
import static org.thechiselgroup.choosel.core.client.resources.ResourceSetTestUtils.TYPE_1;
import static org.thechiselgroup.choosel.core.client.resources.ResourceSetTestUtils.TYPE_2;
import static org.thechiselgroup.choosel.core.client.resources.ResourceSetTestUtils.createLabeledResources;
import static org.thechiselgroup.choosel.core.client.resources.ResourceSetTestUtils.createResource;
import static org.thechiselgroup.choosel.core.client.resources.ResourceSetTestUtils.createResources;
import static org.thechiselgroup.choosel.core.client.resources.ResourceSetTestUtils.toResourceSet;
import static org.thechiselgroup.choosel.core.client.visualization.model.implementation.VisualItemTestUtils.containsVisualItemsForExactResourceSets;
import static org.thechiselgroup.choosel.core.shared.test.matchers.collections.CollectionMatchers.containsExactly;

import java.util.logging.Logger;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.core.client.resources.DefaultResourceSet;
import org.thechiselgroup.choosel.core.client.resources.DefaultResourceSetFactory;
import org.thechiselgroup.choosel.core.client.resources.Resource;
import org.thechiselgroup.choosel.core.client.resources.ResourceByPropertyMultiCategorizer;
import org.thechiselgroup.choosel.core.client.resources.ResourceByUriMultiCategorizer;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.resources.ResourceSetTestUtils;
import org.thechiselgroup.choosel.core.client.util.DataType;
import org.thechiselgroup.choosel.core.client.visualization.behaviors.CompositeVisualItemBehavior;
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

    @Test
    public void addingResourceSetsAddsVisualItems() {
        ResourceSet resources1 = createResources(TYPE_1, 1);
        ResourceSet resources2 = createResources(TYPE_2, 2);

        helper.addToContainedResources(resources1);
        helper.addToContainedResources(resources2);

        assertThat(underTest.getVisualItems(),
                containsVisualItemsForExactResourceSets(resources1, resources2));
    }

    @Test
    public void addResourcesToInitialContentCreatesVisualItems() {
        DefaultVisualizationModel model = new DefaultVisualizationModel(
                helper.getViewContentDisplay(), new DefaultResourceSet(),
                new DefaultResourceSet(), new CompositeVisualItemBehavior(),
                Logger.getAnonymousLogger(), new DefaultResourceSetFactory(),
                new ResourceByUriMultiCategorizer());

        Resource r1 = createResource(1);
        r1.putValue(TEXT_PROPERTY_1, "value1-1");
        r1.putValue(TEXT_PROPERTY_2, "value2");

        Resource r2 = createResource(2);
        r2.putValue(TEXT_PROPERTY_1, "value1-2");
        r2.putValue(TEXT_PROPERTY_2, "value2");

        model.getContentResourceSet().addAll(toResourceSet(r1, r2));

        model.setCategorizer(new ResourceByPropertyMultiCategorizer(
                TEXT_PROPERTY_2));

        assertThat(model.getVisualItems().getFirstElement().getResources(),
                containsExactly(r1, r2));
    }

    @Test
    public void changeTextSlotMapping() {
        Resource resource = createResource(1);
        resource.putValue(TEXT_PROPERTY_1, "t1");
        resource.putValue(TEXT_PROPERTY_2, "t2");

        underTest.setResolver(slot, new FirstResourcePropertyResolver(
                TEXT_PROPERTY_1, DataType.TEXT));
        helper.getContainedResources().add(resource);
        underTest.setResolver(slot, new FirstResourcePropertyResolver(
                TEXT_PROPERTY_2, DataType.TEXT));

        assertEquals("t2", getFirstVisualItem().getValue(slot));
    }

    @Test
    public void deselectVisualItemWhenResourceRemovedFromSelection() {
        ResourceSet resources = ResourceSetTestUtils.createResources(1);

        helper.getContainedResources().addAll(resources);

        helper.getSelectedResources().addAll(resources);
        helper.getSelectedResources().removeAll(resources);

        assertThat(getFirstVisualItem().getResources(Subset.SELECTED),
                containsExactly(ResourceSetTestUtils.createResources()));
    }

    private VisualItem getFirstVisualItem() {
        return underTest.getVisualItems().getFirstElement();
    }

    @Test
    public void grouping() {
        Resource r1 = createResource(1);
        r1.putValue(TEXT_PROPERTY_1, "value1-1");
        r1.putValue(TEXT_PROPERTY_2, "value2");

        Resource r2 = createResource(2);
        r2.putValue(TEXT_PROPERTY_1, "value1-2");
        r2.putValue(TEXT_PROPERTY_2, "value2");

        helper.getContainedResources().addAll(toResourceSet(r1, r2));

        underTest.setCategorizer(new ResourceByPropertyMultiCategorizer(
                TEXT_PROPERTY_2));

        assertThat(getFirstVisualItem().getResources(), containsExactly(r1, r2));
    }

    @Test
    public void groupingChangeChangesCategory() {
        Resource resource = createResource(1);
        resource.putValue(TEXT_PROPERTY_1, "category1");
        resource.putValue(TEXT_PROPERTY_2, "category2");

        underTest.setCategorizer(new ResourceByPropertyMultiCategorizer(
                TEXT_PROPERTY_1));
        helper.getContainedResources().add(resource);
        underTest.setCategorizer(new ResourceByPropertyMultiCategorizer(
                TEXT_PROPERTY_2));

        assertEquals("category2", getFirstVisualItem().getId());
    }

    /**
     * Removing a resource item and adding another resource item, both with the
     * same category, in one operation caused a bug.
     */
    @Test
    public void groupingChangeWithRemovingAndAddingSameCategory() {
        Resource resource = createResource(1);
        resource.putValue(TEXT_PROPERTY_1, "category1");
        resource.putValue(TEXT_PROPERTY_2, "category1");

        underTest.setCategorizer(new ResourceByPropertyMultiCategorizer(
                TEXT_PROPERTY_1));
        helper.getContainedResources().add(resource);
        underTest.setCategorizer(new ResourceByPropertyMultiCategorizer(
                TEXT_PROPERTY_2));

        assertEquals("category1", getFirstVisualItem().getId());
    }

    @Test
    public void highlightedResourceSetOnCreatedVisualItems() {
        ResourceSet resources = createResources(TYPE_1, 1, 3, 4);

        helper.getHighlightedResources().addAll(resources);
        helper.getContainedResources().addAll(resources);

        assertThat(getFirstVisualItem().getResources(Subset.HIGHLIGHTED),
                containsExactly(resources));
    }

    @Test
    public void highlightedResourcesGetAddedToVisualItemWhenHoverModelContainsAdditionalResources() {
        ResourceSet viewResources = createResources(2);
        ResourceSet highlightedResources2 = createResources(1, 2);

        helper.getContainedResources().addAll(viewResources);
        helper.getHighlightedResources().addAll(highlightedResources2);

        assertThat(getFirstVisualItem().getResources(Subset.HIGHLIGHTED),
                containsExactly(viewResources));
    }

    @Test
    public void highlightedResourcesGetAddedToVisualItemWhenResourcesAddedToHoverModel() {
        ResourceSet resources = createResources(1);

        helper.getContainedResources().addAll(resources);
        helper.getHighlightedResources().addAll(resources);

        assertThat(getFirstVisualItem().getResources(Subset.HIGHLIGHTED),
                containsExactly(createResources(1)));
    }

    @Test
    public void highlightedResourcesGetRemovedFromVisualItemWhenResourcesRemovedFromHighlightingSet() {
        ResourceSet resources = createResources(1);

        helper.getContainedResources().addAll(resources);
        helper.getHighlightedResources().addAll(resources);
        helper.getHighlightedResources().removeAll(resources);

        assertThat(getFirstVisualItem().getResources(Subset.HIGHLIGHTED),
                containsExactly(createResources()));
    }

    @Test
    public void initializesContentDisplay() {
        verify(helper.getViewContentDisplay(), times(1)).init(
                any(ViewContentDisplayCallback.class));
    }

    @Test
    public void selectVisualItemWhenResourceAddedToSelection() {
        helper.getContainedResources().add(createResource(1));

        helper.getSelectedResources().add(createResource(1));

        assertThat(getFirstVisualItem().getResources(Subset.SELECTED),
                containsExactly(createResources(1)));
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
    public void VisualItemIsHighlightedOnChangeWhenAddedResourcesAreAlreadyHighlighted() {
        ResourceSet originalResources = createResources(TYPE_1, 1);
        ResourceSet addedResources = createResources(TYPE_1, 2);

        helper.getContainedResources().addAll(originalResources);
        helper.getHighlightedResources().addAll(addedResources);
        helper.getContainedResources().addAll(addedResources);

        VisualItem item = getFirstVisualItem();

        assertEquals(Status.PARTIAL, item.getStatus(Subset.HIGHLIGHTED));
        assertThat(item.getResources(Subset.HIGHLIGHTED),
                containsExactly(addedResources));
    }

    /**
     * @see <a
     *      href="http://code.google.com/p/choosel/issues/detail?id=149">"Issue 149"</a>
     */
    @Test
    public void VisualItemIsNotHighlightedOnChangeWhenOnlyRemovedResourcesAreHighlighted() {
        ResourceSet originalResources = createLabeledResources(TYPE_1, 1, 2);
        ResourceSet removedResources = createResources(TYPE_1, 2);

        helper.getHighlightedResources().addAll(removedResources);
        helper.getContainedResources().addAll(originalResources);
        helper.getHighlightedResources().removeAll(removedResources);

        VisualItem item = getFirstVisualItem();

        assertEquals(Status.NONE, item.getStatus(Subset.HIGHLIGHTED));
        assertEquals(true, item.getResources(Subset.HIGHLIGHTED).isEmpty());
    }

    /**
     * @see <a
     *      href="http://code.google.com/p/choosel/issues/detail?id=149">"Issue 149"</a>
     */
    @Test
    public void VisualItemIsNotSelectedOnChangeWhenOnlyRemovedResourcesAreSelected() {
        ResourceSet originalResources = createLabeledResources(TYPE_1, 1, 2);
        ResourceSet removedResources = createResources(TYPE_1, 2);

        helper.getSelectedResources().addAll(removedResources);
        helper.getContainedResources().addAll(originalResources);
        helper.getSelectedResources().removeAll(removedResources);

        VisualItem item = getFirstVisualItem();

        assertEquals(Status.NONE, item.getStatus(Subset.SELECTED));
        assertEquals(true, item.getResources(Subset.SELECTED).isEmpty());
    }

    /**
     * @see <a
     *      href="http://code.google.com/p/choosel/issues/detail?id=149">"Issue 149"</a>
     */
    @Test
    public void VisualItemIsSelectedHighlightedOnChangeWhenAddedResourcesAreAlreadySelectedHighlighted() {
        ResourceSet originalResources = createResources(TYPE_1, 1);
        ResourceSet addedResources = createResources(TYPE_1, 2);

        helper.getContainedResources().addAll(originalResources);
        helper.getSelectedResources().addAll(addedResources);
        helper.getHighlightedResources().addAll(addedResources);
        helper.getContainedResources().addAll(addedResources);

        VisualItem item = getFirstVisualItem();

        assertEquals(Status.PARTIAL, item.getStatus(Subset.SELECTED));
        assertThat(item.getResources(Subset.SELECTED),
                containsExactly(addedResources));

        assertEquals(Status.PARTIAL, item.getStatus(Subset.HIGHLIGHTED));
        assertThat(item.getResources(Subset.HIGHLIGHTED),
                containsExactly(addedResources));
    }

    /**
     * @see <a
     *      href="http://code.google.com/p/choosel/issues/detail?id=149">"Issue 149"</a>
     */
    @Test
    public void VisualItemIsSelectedOnChangeWhenAddedResourcesAreAlreadySelected() {
        ResourceSet originalResources = createResources(TYPE_1, 1);
        ResourceSet addedResources = createResources(TYPE_1, 2);

        helper.getContainedResources().addAll(originalResources);
        helper.getSelectedResources().addAll(addedResources);
        helper.getContainedResources().addAll(addedResources);

        VisualItem item = getFirstVisualItem();

        assertEquals(Status.PARTIAL, item.getStatus(Subset.SELECTED));
        assertThat(item.getResources(Subset.SELECTED),
                containsExactly(addedResources));
    }

    /**
     * @see <a
     *      href="http://code.google.com/p/choosel/issues/detail?id=123">"Issue 123"</a>
     */
    @Test
    public void VisualItemIsSelectedOnCreateWhenResourcesAreAlreadySelected() {
        ResourceSet resources = ResourceSetTestUtils.createResources(1);

        helper.getSelectedResources().addAll(resources);
        helper.getContainedResources().addAll(resources);

        VisualItem item = getFirstVisualItem();

        assertEquals(Status.FULL, item.getStatus(Subset.SELECTED));
        assertThat(item.getResources(Subset.SELECTED),
                containsExactly(resources));
    }

}
