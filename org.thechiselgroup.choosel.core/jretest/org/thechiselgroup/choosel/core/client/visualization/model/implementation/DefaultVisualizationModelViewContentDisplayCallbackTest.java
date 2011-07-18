/*******************************************************************************
 * Copyright (C) 2011 Lars Grammel 
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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.thechiselgroup.choosel.core.client.visualization.model.implementation.VisualItemValueResolverTestUtils.mockResolver;
import static org.thechiselgroup.choosel.core.client.visualization.model.implementation.VisualItemValueResolverTestUtils.mockResolverThatCanAlwaysResolve;
import static org.thechiselgroup.choosel.core.client.visualization.model.implementation.VisualItemValueResolverTestUtils.mockResolverThatCanNeverResolve;
import static org.thechiselgroup.choosel.core.client.visualization.model.implementation.VisualItemValueResolverTestUtils.mockResolverThatCanResolveExactResourceSet;

import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.resources.ResourceSetTestUtils;
import org.thechiselgroup.choosel.core.client.util.DataType;
import org.thechiselgroup.choosel.core.client.util.collections.CollectionFactory;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightList;
import org.thechiselgroup.choosel.core.client.visualization.model.Slot;
import org.thechiselgroup.choosel.core.client.visualization.model.ViewContentDisplay;
import org.thechiselgroup.choosel.core.client.visualization.model.ViewContentDisplayCallback;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItem;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItemContainer;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItemValueResolver;

/**
 * <p>
 * Tests the {@link ViewContentDisplayCallback} exposed by
 * {@link DefaultVisualizationModel} to its {@link ViewContentDisplay}.
 * </p>
 * 
 * @author Lars Grammel
 */
// TODO extract AbstractDefaultViewModelTest superclass
// TODO handler
public class DefaultVisualizationModelViewContentDisplayCallbackTest {

    private Slot slot;

    private DefaultVisualizationModel underTest;

    private DefaultVisualizationModelTestHelper helper;

    private ViewContentDisplayCallback callback;

    private VisualItemContainer container;

    @Test
    public void containsViewItemsReturnsFalseForViewItemsWithErrors() {
        ResourceSet resources = ResourceSetTestUtils.createResources(
                ResourceSetTestUtils.TYPE_1, 1);

        underTest.setResolver(slot, mockResolverThatCanNeverResolve());
        helper.addToContainedResources(resources);

        assertThat(
                container.containsVisualItem(underTest.getVisualItems()
                        .getFirstElement().getId()), is(false));
    }

    @Test
    public void containsViewItemsReturnsTrueForViewItemsWithoutErrors() {
        ResourceSet resources = ResourceSetTestUtils.createResources(
                ResourceSetTestUtils.TYPE_1, 1);

        underTest.setResolver(slot, mockResolverThatCanAlwaysResolve());
        helper.addToContainedResources(resources);

        assertThat(
                container.containsVisualItem(underTest.getVisualItems()
                        .getFirstElement().getId()), is(true));
    }

    @Test
    public void getByIDReturnsCorrectViewItem() {
        ResourceSet resources = ResourceSetTestUtils.createResources(
                ResourceSetTestUtils.TYPE_1, 1);

        underTest.setResolver(slot, mockResolverThatCanAlwaysResolve());
        helper.addToContainedResources(resources);

        LightweightList<VisualItem> viewItems = CollectionFactory
                .createLightweightList();

        // get view items that are in the content display
        viewItems.add(container.getVisualItem(underTest.getVisualItems()
                .getFirstElement().getId()));

        assertThat(viewItems,
                VisualItemTestUtils
                        .containsVisualItemsForExactResourceSets(resources));
    }

    @Test(expected = NoSuchElementException.class)
    public void getByIDThrowsExceptionIfViewItemHasErrors() {
        ResourceSet resources = ResourceSetTestUtils.createResources(
                ResourceSetTestUtils.TYPE_1, 1);

        underTest.setResolver(slot, mockResolverThatCanNeverResolve());
        helper.addToContainedResources(resources);

        container.getVisualItem(underTest.getVisualItems().getFirstElement()
                .getId());
    }

    @Test
    public void getByResourcesIsEmptyForResourceInvalid() {
        ResourceSet resources = ResourceSetTestUtils.createResources(
                ResourceSetTestUtils.TYPE_1, 1);

        underTest.setResolver(slot, mockResolverThatCanNeverResolve());

        helper.addToContainedResources(resources);

        assertTrue(container.getVisualItems(resources).isEmpty());
    }

    @Test
    public void getByResourcesOnlyReturnsValidViewItems() {
        ResourceSet resources1 = ResourceSetTestUtils.createResources(
                ResourceSetTestUtils.TYPE_1, 1);
        ResourceSet resources2 = ResourceSetTestUtils.createResources(
                ResourceSetTestUtils.TYPE_2, 2);

        underTest.setResolver(slot,
                mockResolverThatCanResolveExactResourceSet(resources1));

        ResourceSet allResources = ResourceSetTestUtils
                .createResources(ResourceSetTestUtils.TYPE_1);
        allResources.addAll(resources1);
        allResources.addAll(resources2);

        helper.addToContainedResources(allResources);

        assertThat(container.getVisualItems(allResources),
                VisualItemTestUtils
                        .containsVisualItemsForExactResourceSets(resources1));
    }

    @Test
    public void getByResourcesReturnsValidViewItemForOneResource() {
        ResourceSet resources = ResourceSetTestUtils.createResources(
                ResourceSetTestUtils.TYPE_1, 1);

        underTest.setResolver(slot, mockResolverThatCanAlwaysResolve());

        helper.addToContainedResources(resources);

        assertThat(container.getVisualItems(resources),
                VisualItemTestUtils
                        .containsVisualItemsForExactResourceSets(resources));
    }

    @Test
    public void getViewItemsExcludesViewItemsWithErrors() {
        ResourceSet resources1 = ResourceSetTestUtils.createResources(
                ResourceSetTestUtils.TYPE_1, 1);
        ResourceSet resources2 = ResourceSetTestUtils.createResources(
                ResourceSetTestUtils.TYPE_2, 1);

        setCanResolverIfContainsResourceExactlyResolver(resources1);
        helper.addToContainedResources(ResourceSetTestUtils.toResourceSet(
                resources1, resources2));

        assertThat(container.getVisualItems(),
                VisualItemTestUtils
                        .containsVisualItemsForExactResourceSets(resources1));
    }

    @Test
    public void getViewItemsReturnsAddedViewItem() {
        ResourceSet resources = ResourceSetTestUtils.createResources(
                ResourceSetTestUtils.TYPE_1, 1);

        underTest.setResolver(slot, mockResolverThatCanAlwaysResolve());
        helper.addToContainedResources(resources);

        assertThat(container.getVisualItems(),
                VisualItemTestUtils
                        .containsVisualItemsForExactResourceSets(resources));
    }

    private void setCanResolverIfContainsResourceExactlyResolver(
            ResourceSet resourceSet) {

        VisualItemValueResolver resolver = mockResolverThatCanResolveExactResourceSet(resourceSet);
        underTest.setResolver(slot, resolver);
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        helper = new DefaultVisualizationModelTestHelper();
        slot = helper.createSlots(DataType.TEXT)[0];

        underTest = helper.createTestViewModel();

        underTest.setResolver(slot, mockResolver());

        ArgumentCaptor<ViewContentDisplayCallback> callbackCaptor = ArgumentCaptor
                .forClass(ViewContentDisplayCallback.class);
        ArgumentCaptor<VisualItemContainer> containerCaptor = ArgumentCaptor
                .forClass(VisualItemContainer.class);
        verify(helper.getViewContentDisplay(), times(1)).init(
                containerCaptor.capture(), callbackCaptor.capture());
        callback = callbackCaptor.getValue();
        container = containerCaptor.getValue();
    }

    @Test
    public void viewContentDisplayIsInitialized() {
        verify(helper.getViewContentDisplay(), times(1)).init(
                any(VisualItemContainer.class),
                any(ViewContentDisplayCallback.class));
    }
}