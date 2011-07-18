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
import static org.thechiselgroup.choosel.core.client.resources.ResourceSetTestUtils.TYPE_1;
import static org.thechiselgroup.choosel.core.client.resources.ResourceSetTestUtils.TYPE_2;
import static org.thechiselgroup.choosel.core.client.resources.ResourceSetTestUtils.createResources;
import static org.thechiselgroup.choosel.core.client.resources.ResourceSetTestUtils.toResourceSet;
import static org.thechiselgroup.choosel.core.client.util.collections.CollectionFactory.createLightweightList;
import static org.thechiselgroup.choosel.core.client.visualization.model.implementation.VisualItemTestUtils.containsVisualItemsForExactResourceSets;
import static org.thechiselgroup.choosel.core.client.visualization.model.implementation.VisualItemValueResolverTestUtils.mockResolver;
import static org.thechiselgroup.choosel.core.client.visualization.model.implementation.VisualItemValueResolverTestUtils.mockResolverThatCanAlwaysResolve;
import static org.thechiselgroup.choosel.core.client.visualization.model.implementation.VisualItemValueResolverTestUtils.mockResolverThatCanNeverResolve;
import static org.thechiselgroup.choosel.core.client.visualization.model.implementation.VisualItemValueResolverTestUtils.mockResolverThatCanResolveExactResourceSet;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.util.DataType;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightList;
import org.thechiselgroup.choosel.core.client.visualization.model.Slot;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItem;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItemContainer;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItemValueResolver;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualizationModel;

/**
 * <p>
 * Tests the error free {@link VisualItemContainer} exposed by
 * {@link VisualizationModel}.
 * </p>
 * 
 * @author Lars Grammel
 */
// TODO add tests for handler notification
public class DefaultVisualizationModelErrorFreeVisualItemContainerTest {

    private Slot slot;

    private DefaultVisualizationModel visualizationModel;

    private DefaultVisualizationModelTestHelper helper;

    private VisualItemContainer underTest;

    @Test
    public void containsViewItemsReturnsFalseForViewItemsWithErrors() {
        ResourceSet resources = createResources(TYPE_1, 1);

        visualizationModel.setResolver(slot, mockResolverThatCanNeverResolve());
        helper.addToContainedResources(resources);

        assertThat(
                underTest.containsVisualItem(visualizationModel
                        .getFullVisualItemContainer().getVisualItems()
                        .getFirstElement().getId()), is(false));
    }

    @Test
    public void containsViewItemsReturnsTrueForViewItemsWithoutErrors() {
        ResourceSet resources = createResources(TYPE_1, 1);

        visualizationModel
                .setResolver(slot, mockResolverThatCanAlwaysResolve());
        helper.addToContainedResources(resources);

        assertThat(
                underTest.containsVisualItem(visualizationModel
                        .getFullVisualItemContainer().getVisualItems()
                        .getFirstElement().getId()), is(true));
    }

    @Test
    public void getByIDReturnsCorrectViewItem() {
        ResourceSet resources = createResources(TYPE_1, 1);

        visualizationModel
                .setResolver(slot, mockResolverThatCanAlwaysResolve());
        helper.addToContainedResources(resources);

        LightweightList<VisualItem> viewItems = createLightweightList();

        // get view items that are in the content display
        viewItems.add(underTest.getVisualItem(visualizationModel
                .getFullVisualItemContainer().getVisualItems()
                .getFirstElement().getId()));

        assertThat(viewItems,
                containsVisualItemsForExactResourceSets(resources));
    }

    @Test
    public void getByResourcesIsEmptyForResourceInvalid() {
        ResourceSet resources = createResources(TYPE_1, 1);

        visualizationModel.setResolver(slot, mockResolverThatCanNeverResolve());

        helper.addToContainedResources(resources);

        assertTrue(underTest.getVisualItems(resources).isEmpty());
    }

    @Test
    public void getByResourcesOnlyReturnsValidViewItems() {
        ResourceSet resources1 = createResources(TYPE_1, 1);
        ResourceSet resources2 = createResources(TYPE_2, 2);

        visualizationModel.setResolver(slot,
                mockResolverThatCanResolveExactResourceSet(resources1));

        ResourceSet allResources = createResources(TYPE_1);
        allResources.addAll(resources1);
        allResources.addAll(resources2);

        helper.addToContainedResources(allResources);

        assertThat(underTest.getVisualItems(allResources),
                containsVisualItemsForExactResourceSets(resources1));
    }

    @Test
    public void getByResourcesReturnsValidViewItemForOneResource() {
        ResourceSet resources = createResources(TYPE_1, 1);

        visualizationModel
                .setResolver(slot, mockResolverThatCanAlwaysResolve());

        helper.addToContainedResources(resources);

        assertThat(underTest.getVisualItems(resources),
                containsVisualItemsForExactResourceSets(resources));
    }

    @Test
    public void getViewItemsExcludesViewItemsWithErrors() {
        ResourceSet resources1 = createResources(TYPE_1, 1);
        ResourceSet resources2 = createResources(TYPE_2, 1);

        setCanResolverIfContainsResourceExactlyResolver(resources1);
        helper.addToContainedResources(toResourceSet(resources1, resources2));

        assertThat(underTest.getVisualItems(),
                containsVisualItemsForExactResourceSets(resources1));
    }

    @Test
    public void getViewItemsReturnsAddedViewItem() {
        ResourceSet resources = createResources(TYPE_1, 1);

        visualizationModel
                .setResolver(slot, mockResolverThatCanAlwaysResolve());
        helper.addToContainedResources(resources);

        assertThat(underTest.getVisualItems(),
                containsVisualItemsForExactResourceSets(resources));
    }

    private void setCanResolverIfContainsResourceExactlyResolver(
            ResourceSet resourceSet) {

        VisualItemValueResolver resolver = mockResolverThatCanResolveExactResourceSet(resourceSet);
        visualizationModel.setResolver(slot, resolver);
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        helper = new DefaultVisualizationModelTestHelper();
        slot = helper.createSlots(DataType.TEXT)[0];

        visualizationModel = helper.createTestVisualizationModel();
        visualizationModel.setResolver(slot, mockResolver());

        underTest = visualizationModel.getErrorFreeVisualItemContainer();
    }

}