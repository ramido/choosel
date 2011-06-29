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
package org.thechiselgroup.choosel.core.client.views.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.thechiselgroup.choosel.core.client.test.ResourcesTestHelper.emptyLightweightCollection;
import static org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory.createResources;
import static org.thechiselgroup.choosel.core.client.views.model.DefaultViewModelTestHelper.captureAddedViewItems;
import static org.thechiselgroup.choosel.core.client.views.model.DefaultViewModelTestHelper.captureRemovedViewItems;
import static org.thechiselgroup.choosel.core.client.views.model.ViewItemValueResolverTestUtils.createResolverCanResolveIfContainsExactlyAllResources;
import static org.thechiselgroup.choosel.core.client.views.model.ViewItemValueResolverTestUtils.createResolverCanResolveResource;
import static org.thechiselgroup.choosel.core.client.views.model.ViewItemWithResourcesMatcher.containsEqualResource;
import static org.thechiselgroup.choosel.core.client.views.model.ViewItemWithResourcesMatcher.containsEqualResources;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.core.client.resources.DataType;
import org.thechiselgroup.choosel.core.client.resources.Resource;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.views.resolvers.FixedValueResolver;

/**
 * <p>
 * Tests that the {@link DefaultViewModel} filters the
 * {@link ViewItemContainerDelta} that is passed into the
 * {@link ViewContentDisplay} by the content of the
 * {@link DefaultViewItemResolutionErrorModel}.
 * </p>
 * 
 * @author Lars Grammel
 * @author Patrick Gorman
 */
// TODO extract AbstractDefaultViewModelTest superclass
public class DefaultViewModelDeltaByErrorFilteringTest {

    private Slot slot;

    private DefaultViewModel underTest;

    private DefaultViewModelTestHelper helper;

    private static final String RESOURCE_TYPE_2 = "type2";

    private static final String RESOURCE_TYPE_1 = "type1";

    @Test
    public void invalidViewItemDoesNotGetAddedToAddedDelta() {
        Resource resource = TestResourceSetFactory.createResource(
                RESOURCE_TYPE_1, 1);
        ResourceSet resources = createResources();
        resources.add(resource);
        resources
                .add(TestResourceSetFactory.createResource(RESOURCE_TYPE_2, 1));
        underTest.setResolver(slot, createResolverCanResolveResource(resource));

        helper.getContainedResources().addAll(resources);
        LightweightCollection<ViewItem> addedViewItems = captureAddedViewItems(helper
                .getViewContentDisplay());

        assertEquals(1, addedViewItems.size());
        assertThat(addedViewItems, containsEqualResource(resource));
    }

    @Test
    public void invalidViewItemDoesNotGetAddedToRemoveDelta() {
        Resource resource1 = TestResourceSetFactory.createResource(
                RESOURCE_TYPE_1, 1);
        Resource resource2 = TestResourceSetFactory.createResource(
                RESOURCE_TYPE_2, 2);
        ResourceSet resources = createResources();
        resources.add(resource1);
        resources.add(resource2);
        underTest
                .setResolver(slot, createResolverCanResolveResource(resource1));

        helper.getContainedResources().addAll(resources);

        /*
         * at this point, the view item with resource 2 is invalid as per
         * invalidViewItemDoesNotGetAddedToAddedDelta test
         */

        helper.getContainedResources().removeAll(resources);

        LightweightCollection<ViewItem> removedViewItems = captureRemovedViewItems(helper
                .getViewContentDisplay());

        assertEquals(1, removedViewItems.size());
        assertThat(removedViewItems, containsEqualResource(resource1));
    }

    @Ignore("not implemented")
    @Test
    public void otherViewItemChangesFromInvalidToValidGetsAddedToRemovedDelta() {

    }

    @Ignore("not implemented")
    @Test
    public void otherViewItemChangesFromValidtoInvalidGetsAddedToAddedDelta() {

    }

    @Ignore("not implemented")
    @Test
    public void otherViewItemInvalidBeforeAndAfterGetsIgnored() {

    }

    @Ignore("not implemented")
    @Test
    public void otherViewItemValidBeforeAndAfterGetsIgnored() {

    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        slot = new Slot("1", "Description", DataType.TEXT);

        helper = new DefaultViewModelTestHelper();
        helper.setSlots(slot);
        underTest = helper.createTestViewModel();
        underTest.setResolver(slot, new FixedValueResolver("a", DataType.TEXT));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void updatedViewItemChangingFromInvalidToValidGetsAddedToAddedDelta() {

        Resource resource1 = TestResourceSetFactory.createResource(
                RESOURCE_TYPE_1, 1);
        Resource resource2 = TestResourceSetFactory.createResource(
                RESOURCE_TYPE_1, 2);

        ResourceSet resources = createResources();
        resources.add(resource1);
        resources.add(resource2);
        underTest
                .setResolver(
                        slot,
                        createResolverCanResolveIfContainsExactlyAllResources(resources));

        helper.getContainedResources().add(resource1);

        /* should not add it here should have 0 items captured */

        helper.getContainedResources().add(resource2);

        ArgumentCaptor<LightweightCollection> captor = ArgumentCaptor
                .forClass(LightweightCollection.class);
        verify(helper.getViewContentDisplay(), times(4)).update(
                captor.capture(), emptyLightweightCollection(ViewItem.class),
                emptyLightweightCollection(ViewItem.class),
                any(LightweightCollection.class));

        LightweightCollection<ViewItem> addedViewItems = captor.getAllValues()
                .get(3);

        assertEquals(1, addedViewItems.size());
        assertThat(addedViewItems, containsEqualResources(resources));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void updatedViewItemChangingFromValidToInvalidGetsAddedToRemovedDelta() {

        Resource resource1 = TestResourceSetFactory.createResource(
                RESOURCE_TYPE_1, 1);
        Resource resource2 = TestResourceSetFactory.createResource(
                RESOURCE_TYPE_1, 2);

        ResourceSet resources = createResources();
        resources.add(resource1);
        resources.add(resource2);

        underTest
                .setResolver(
                        slot,
                        createResolverCanResolveIfContainsExactlyAllResources(resources));

        helper.getContainedResources().addAll(resources);

        /* should now have 1 valid view item */

        helper.getContainedResources().remove(resource2);

        ArgumentCaptor<LightweightCollection> captor = ArgumentCaptor
                .forClass(LightweightCollection.class);
        verify(helper.getViewContentDisplay(), times(3)).update(
                emptyLightweightCollection(ViewItem.class),
                emptyLightweightCollection(ViewItem.class), captor.capture(),
                any(LightweightCollection.class));

        LightweightCollection<ViewItem> removedViewItems = captor
                .getAllValues().get(2);

        assertEquals(1, removedViewItems.size());
        assertThat(removedViewItems, containsEqualResource(resource1));
    }

    @SuppressWarnings({ "unchecked" })
    @Test
    public void updatedViewItemInvalidBeforeAndAfterGetsIgnored() {

        Resource resource1 = TestResourceSetFactory.createResource(
                RESOURCE_TYPE_1, 1);
        Resource resource2 = TestResourceSetFactory.createResource(
                RESOURCE_TYPE_1, 2);
        Resource resource3 = TestResourceSetFactory.createResource(
                RESOURCE_TYPE_1, 3);

        ResourceSet validResources = createResources();
        validResources.add(resource3);

        ResourceSet otherResources = createResources();
        otherResources.add(resource1);
        otherResources.add(resource2);

        underTest
                .setResolver(
                        slot,
                        createResolverCanResolveIfContainsExactlyAllResources(validResources));

        helper.getContainedResources().addAll(otherResources);

        helper.getContainedResources().remove(resource2);

        // TODO will this enforce that they are all empty the 4th time?
        verify(helper.getViewContentDisplay(), times(4)).update(
                emptyLightweightCollection(ViewItem.class),
                emptyLightweightCollection(ViewItem.class),
                emptyLightweightCollection(ViewItem.class),
                any(LightweightCollection.class));

    }

}