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

import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.thechiselgroup.choosel.core.client.test.ResourcesTestHelper.emptyLightweightCollection;
import static org.thechiselgroup.choosel.core.client.test.ResourcesTestHelper.toResourceSet;
import static org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory.createResource;
import static org.thechiselgroup.choosel.core.client.views.model.DefaultViewModelTestHelper.captureAddedViewItems;
import static org.thechiselgroup.choosel.core.client.views.model.DefaultViewModelTestHelper.captureRemovedViewItems;
import static org.thechiselgroup.choosel.core.client.views.model.DefaultViewModelTestHelper.captureUpdatedViewItems;
import static org.thechiselgroup.choosel.core.client.views.model.ViewItemValueResolverTestUtils.createResolverThatCanResolveIfContainsResourcesExactly;
import static org.thechiselgroup.choosel.core.client.views.model.ViewItemValueResolverTestUtils.mockAlwaysApplicableResolver;
import static org.thechiselgroup.choosel.core.client.views.model.ViewItemWithResourcesMatcher.containsEqualResources;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.core.client.resources.DataType;
import org.thechiselgroup.choosel.core.client.resources.Resource;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.views.resolvers.FixedValueResolver;
import org.thechiselgroup.choosel.core.client.views.resolvers.ViewItemValueResolver;

/**
 * <p>
 * Tests that the {@link DefaultViewModel} filters the
 * {@link ViewItemContainerDelta} that is passed into the
 * {@link ViewContentDisplay} by the content of the
 * {@link DefaultViewItemResolutionErrorModel}.
 * </p>
 * <p>
 * We distinguish the following cases:
 * <ul>
 * <li>delta=added, current_state=valid ==&gt; add
 * {@link DefaultViewModelTest#updateCalledWith2ViewItemsWhenAddingMixedResourceSet}
 * </li>
 * <li>delta=added, current_state=errors ==&gt; ignore
 * {@link #addedWithErrorsGetsIgnored()}</li>
 * <li>delta=removed, old_state=valid ==&gt; remove
 * {@link DefaultViewModelTest#updateCalledWhenResourcesRemoved}</li>
 * <li>delta=removed, old_state=errors ==&gt; ignore
 * {@link #removedWithErrorsGetsIgnored()}</li>
 * <li>delta=updated, old_state=valid, current_state=valid ==&gt; update
 * {@link #updatedValidNowAndBeforeGetsUpdated()}</li>
 * <li>delta=updated, old_state=valid, current_state=errors ==&gt; remove
 * {@link #updatedChangingFromValidToErrorsGetsRemoved()}</li>
 * <li>delta=updated, old_state=errors, current_state=valid ==&gt; add
 * {@link #updatedChangingFromErrorsToValidGetsAdded()}</li>
 * <li>delta=updated, old_state=errors, current_state=errors ==&gt; ignore
 * {@link #updatedWithErrorsNowAndBeforeGetsIgnore()}</li>
 * </ul>
 * Other items are ignored, because they cannot have changed (otherwise they
 * would be in the updated set), and thus their state cannot have changed.
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

    /**
     * <p>
     * delta=added, current_state=errors ==&gt; ignore
     * </p>
     * <p>
     * 2 view items get added to trigger call, we check that one is ignored
     * </p>
     */
    @Test
    public void addedWithErrorsGetsIgnored() {
        Resource validResource = createResource(RESOURCE_TYPE_1, 1);

        setCanResolverIfContainsResourceExactlyResolver(toResourceSet(validResource));

        helper.addAllToContainerResources(toResourceSet(validResource,
                createResource(RESOURCE_TYPE_2, 1)));

        assertThat(captureAddedViewItems(helper.getViewContentDisplay()),
                containsEqualResources(validResource));
    }

    /**
     * delta=removed, old_state=errors ==&gt; ignore
     */
    @Test
    public void removedWithErrorsGetsIgnored() {
        Resource validResource = createResource(RESOURCE_TYPE_1, 1);
        Resource errorResource = createResource(RESOURCE_TYPE_2, 1);

        setCanResolverIfContainsResourceExactlyResolver(toResourceSet(validResource));

        helper.addAllToContainerResources(toResourceSet(validResource,
                errorResource));

        /*
         * at this point, the view item with errorResource is invalid as per
         * invalidViewItemDoesNotGetAddedToAddedDelta test
         */

        helper.getContainedResources().removeAll(
                toResourceSet(validResource, errorResource));

        assertThat(captureRemovedViewItems(helper.getViewContentDisplay()),
                containsEqualResources(validResource));
    }

    private void setCanResolverIfContainsResourceExactlyResolver(
            ResourceSet resourceSet) {

        ViewItemValueResolver resolver = createResolverThatCanResolveIfContainsResourcesExactly(resourceSet);
        underTest.setResolver(slot, resolver);
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

    /**
     * delta=updated, old_state=errors, current_state=valid ==&gt; add
     */
    @Test
    public void updatedChangingFromErrorsToValidGetsAdded() {
        Resource resource1 = createResource(RESOURCE_TYPE_1, 1);
        Resource resource2 = createResource(RESOURCE_TYPE_1, 2);

        setCanResolverIfContainsResourceExactlyResolver(toResourceSet(
                resource1, resource2));

        helper.addToContainedResources(resource1);

        /* should not have been added yet - 0 update calls so far */

        helper.addToContainedResources(resource2);

        assertThat(captureAddedViewItems(helper.getViewContentDisplay()),
                containsEqualResources(resource1, resource2));
    }

    /**
     * delta=updated, old_state=valid, current_state=errors ==&gt; remove
     */
    @Test
    public void updatedChangingFromValidToErrorsGetsRemoved() {
        Resource resource1 = createResource(RESOURCE_TYPE_1, 1);
        Resource resource2 = createResource(RESOURCE_TYPE_1, 2);

        setCanResolverIfContainsResourceExactlyResolver(toResourceSet(
                resource1, resource2));

        helper.addAllToContainerResources(toResourceSet(resource1, resource2));

        /* should now have 1 valid view item */

        helper.getContainedResources().remove(resource2);

        assertThat(captureRemovedViewItems(helper.getViewContentDisplay()),
                containsEqualResources(resource1));
    }

    /**
     * delta=updated, old_state=errors, current_state=errors ==&gt; ignore
     */
    @SuppressWarnings("unchecked")
    @Test
    public void updatedErrorsNowAndBeforeGetsIgnored() {
        Resource validResource = createResource(RESOURCE_TYPE_1, 3);

        setCanResolverIfContainsResourceExactlyResolver(toResourceSet(validResource));

        // adds error view item and correct view item
        helper.addToContainedResources(createResource(RESOURCE_TYPE_1, 1));

        // updates error view item
        helper.addToContainedResources(createResource(RESOURCE_TYPE_1, 2));

        // neither adding nor updating view item should have triggered calls to
        // update
        verify(helper.getViewContentDisplay(), times(0)).update(
                any(LightweightCollection.class),
                any(LightweightCollection.class),
                any(LightweightCollection.class),
                emptyLightweightCollection(Slot.class));

    }

    /**
     * delta=updated, old_state=valid, current_state=valid ==&gt; updated
     */
    @Test
    public void updatedValidNowAndBeforeGetsUpdated() {
        Resource resource1 = createResource(RESOURCE_TYPE_1, 1);
        Resource resource2 = createResource(RESOURCE_TYPE_1, 2);

        underTest.setResolver(slot, mockAlwaysApplicableResolver());

        helper.addToContainedResources(resource1);

        // update call
        helper.addToContainedResources(resource2);

        assertThat(captureUpdatedViewItems(helper.getViewContentDisplay()),
                containsEqualResources(resource1, resource2));
    }
}