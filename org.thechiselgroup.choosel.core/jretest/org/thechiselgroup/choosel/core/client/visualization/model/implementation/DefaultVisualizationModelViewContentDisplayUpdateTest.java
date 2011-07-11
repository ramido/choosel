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

import static org.hamcrest.core.IsAnything.any;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.thechiselgroup.choosel.core.client.resources.ResourceSetTestUtils.TYPE_1;
import static org.thechiselgroup.choosel.core.client.resources.ResourceSetTestUtils.createResource;
import static org.thechiselgroup.choosel.core.client.resources.ResourceSetTestUtils.createResources;
import static org.thechiselgroup.choosel.core.client.resources.ResourceSetTestUtils.toResourceSet;
import static org.thechiselgroup.choosel.core.client.visualization.model.implementation.VisualItemTestUtils.containsVisualItemsForExactResourceSets;
import static org.thechiselgroup.choosel.core.client.visualization.model.implementation.VisualItemValueResolverTestUtils.mockResolverThatCanAlwaysResolve;
import static org.thechiselgroup.choosel.core.client.visualization.model.implementation.VisualItemValueResolverTestUtils.mockResolverThatCanNeverResolve;
import static org.thechiselgroup.choosel.core.client.visualization.model.implementation.VisualItemValueResolverTestUtils.mockResolverThatCanResolveExactResourceSet;
import static org.thechiselgroup.choosel.core.client.visualization.model.implementation.VisualItemWithResourcesMatcher.containsEqualResources;
import static org.thechiselgroup.choosel.core.shared.test.matchers.collections.CollectionMatchers.containsExactly;
import static org.thechiselgroup.choosel.core.shared.test.matchers.collections.CollectionMatchers.containsExactlyLightweightCollection;
import static org.thechiselgroup.choosel.core.shared.test.matchers.collections.CollectionMatchers.isEmpty;

import java.util.List;

import org.hamcrest.core.IsAnything;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.thechiselgroup.choosel.core.client.resources.Resource;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.resources.ResourceSetTestUtils;
import org.thechiselgroup.choosel.core.client.util.DataType;
import org.thechiselgroup.choosel.core.client.util.collections.CollectionFactory;
import org.thechiselgroup.choosel.core.client.util.collections.Delta;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightList;
import org.thechiselgroup.choosel.core.client.util.math.SumCalculation;
import org.thechiselgroup.choosel.core.client.visualization.model.Slot;
import org.thechiselgroup.choosel.core.client.visualization.model.ViewContentDisplay;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItem;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItem.Subset;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItemRenderer;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItemValueResolver;
import org.thechiselgroup.choosel.core.client.visualization.resolvers.CalculationResolver;
import org.thechiselgroup.choosel.core.client.visualization.resolvers.FixedValueResolver;
import org.thechiselgroup.choosel.core.shared.test.matchers.collections.CollectionMatchers;

/**
 * <p>
 * Tests that the {@link DefaultVisualizationModel} calls
 * {@link VisualItemRenderer#update(LightweightCollection, LightweightCollection, LightweightCollection, LightweightCollection)}
 * correctly.
 * </p>
 * <p>
 * As part of this, we test that it filters the {@link VisualItemContainerDelta}
 * that is passed into the {@link ViewContentDisplay} by the content of the
 * {@link DefaultVisualItemResolutionErrorModel}.
 * </p>
 * <p>
 * We distinguish the following cases:
 * <ul>
 * <li>delta=added, current_state=valid ==&gt; add
 * {@link #addingTwoViewItemsInTwoStepsTriggersTwoUpdateCalls()}</li>
 * <li>delta=added, current_state=errors ==&gt; ignore
 * {@link #addedViewItemsWithErrorsGetIgnoredWhenResourcesChange()}</li>
 * <li>delta=removed, old_state=valid ==&gt; remove
 * {@link DefaultVisualizationModelTest#updateCalledWhenResourcesRemoved}</li>
 * <li>delta=removed, old_state=errors ==&gt; ignore
 * {@link #removedViewItemsWithErrorsGetIgnoredWhenResourcesChange()}</li>
 * <li>delta=updated, old_state=valid, current_state=valid ==&gt; update
 * {@link #updatedViewItemsThatAreValidNowAndBeforeGetUpdatedWhenResourcesChange()}
 * </li>
 * <li>delta=updated, old_state=valid, current_state=errors ==&gt; remove
 * {@link #updatedViewItemsChangingFromValidToErrorsGetRemovedWhenResourcesChange()}
 * </li>
 * <li>delta=updated, old_state=errors, current_state=valid ==&gt; add
 * {@link #updatedViewItemsChangingFromErrorsToValidGetAddedWhenResourcesChange()}
 * </li>
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
public class DefaultVisualizationModelViewContentDisplayUpdateTest {

    /**
     * Convert input to {@code LightWeightCollection<Delta>}
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static LightweightList<Delta<VisualItem>> cast(List<Delta> original) {
        LightweightList<Delta<VisualItem>> result = CollectionFactory
                .createLightweightList();
        for (Delta delta : original) {
            result.add(delta);
        }
        return result;
    }

    private DefaultVisualizationModel underTest;

    private DefaultVisualizationModelTestHelper helper;

    private Slot slot;

    /**
     * <p>
     * delta=added, current_state=errors ==&gt; ignore
     * </p>
     * <p>
     * 2 view items get added to trigger call, we check that one is ignored
     * </p>
     */
    @Test
    public void addedViewItemsWithErrorsGetIgnoredWhenResourcesChange() {
        Resource validResource = createResource(TYPE_1, 1);

        setCanResolveIfContainsResourceExactlyResolver(ResourceSetTestUtils
                .toResourceSet(validResource));

        helper.addToContainedResources(ResourceSetTestUtils.toResourceSet(
                validResource, createResource(ResourceSetTestUtils.TYPE_2, 1)));

        assertThat(captureDelta().getAddedElements(),
                containsEqualResources(validResource));
    }

    @Test
    public void addingTwoViewItemsInOneStepCallsUpdateOnce() {
        ResourceSet resources1 = createResources(TYPE_1, 1);
        ResourceSet resources2 = createResources(ResourceSetTestUtils.TYPE_2, 2);

        helper.addToContainedResources(ResourceSetTestUtils.toResourceSet(
                resources1, resources2));

        verify(helper.getViewContentDisplay(), times(1)).update(
                argThat(CollectionMatchers.<VisualItem> matchesDelta(
                        VisualItemTestUtils
                                .containsVisualItemsForExactResourceSets(
                                        resources1, resources2),
                        CollectionMatchers.isEmpty(VisualItem.class),
                        CollectionMatchers.isEmpty(VisualItem.class))),
                argThat(CollectionMatchers.isEmpty(Slot.class)));
    }

    @Test
    public void addingTwoViewItemsInTwoStepsCallsUpdateTwice() {
        ResourceSet resources1 = createResources(TYPE_1, 1);
        ResourceSet resources2 = createResources(ResourceSetTestUtils.TYPE_2, 2);

        helper.addToContainedResources(resources1);
        helper.addToContainedResources(resources2);

        LightweightList<Delta<VisualItem>> allValues = captureDeltas(2);

        assertThat(allValues.get(0),
                CollectionMatchers.<VisualItem> matchesDelta(
                        containsVisualItemsForExactResourceSets(resources1),
                        isEmpty(VisualItem.class), isEmpty(VisualItem.class)));
        assertThat(allValues.get(1),
                CollectionMatchers.<VisualItem> matchesDelta(
                        containsVisualItemsForExactResourceSets(resources2),
                        isEmpty(VisualItem.class), isEmpty(VisualItem.class)));
    }

    private Delta<VisualItem> captureDelta() {
        return captureDeltas(1).getFirstElement();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private LightweightList<Delta<VisualItem>> captureDeltas(
            int wantedNumberOfInvocations) {

        ArgumentCaptor<Delta> captor = ArgumentCaptor.forClass(Delta.class);
        verify(helper.getViewContentDisplay(), times(wantedNumberOfInvocations))
                .update(captor.capture(),
                        argThat(CollectionMatchers.isEmpty(Slot.class)));
        return cast(captor.getAllValues());
    }

    /**
     * We store the value on update, because this is what happens during the
     * update call. If we would check the value on the view item later, e.g.
     * using verify, the bug would not show up (it is important that the
     * viewItem returns the new values when the content display is updated.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private double[] captureViewItemNumberSlotValueOnUpdate(
            final VisualItem viewItem) {
        final double[] result = new double[1];
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                result[0] = viewItem.getValue(slot);
                return null;
            }
        }).when(helper.getViewContentDisplay()).update(
                argThat(any(Delta.class)),
                argThat(any(LightweightCollection.class)));
        return result;
    }

    @Test
    public void highlightingChangeOnSeveralResourcesTriggersSingleUpdate() {
        final ResourceSet resources = ResourceSetTestUtils
                .createResources(1, 2);

        helper.getContainedResources().addAll(resources);
        helper.getHighlightedResources().addAll(resources);

        Delta<VisualItem> delta = captureDeltas(2).get(1);

        assertThat(
                delta.getUpdatedElements().getFirstElement()
                        .getResources(Subset.HIGHLIGHTED),
                containsExactly(resources));
    }

    @Test
    public void numberSlot() {
        underTest.setResolver(slot, mockResolverThatCanNeverResolve());
        helper.addToContainedResources(createResource(1));
        verifyNoViewItemsAdded();

        underTest.setResolver(slot, mockResolverThatCanAlwaysResolve());

        verify(helper.getViewContentDisplay(), times(1))
                .update(argThat(CollectionMatchers.<VisualItem> matchesDelta(
                        VisualItemTestUtils
                                .containsVisualItemsForExactResourceSets(ResourceSetTestUtils
                                        .createResources(1)),
                        CollectionMatchers.isEmpty(VisualItem.class),
                        CollectionMatchers.isEmpty(VisualItem.class))),
                        (LightweightCollection<Slot>) argThat(containsExactly(slot)));
    }

    /**
     * delta=removed, old_state=errors ==&gt; ignore
     */
    @Test
    public void removedViewItemsWithErrorsGetIgnoredWhenResourcesChange() {
        Resource validResource = createResource(TYPE_1, 1);
        Resource errorResource = createResource(ResourceSetTestUtils.TYPE_2, 1);

        setCanResolveIfContainsResourceExactlyResolver(ResourceSetTestUtils
                .toResourceSet(validResource));

        helper.addToContainedResources(ResourceSetTestUtils.toResourceSet(
                validResource, errorResource));

        /*
         * at this point, the view item with errorResource is invalid as per
         * invalidViewItemDoesNotGetAddedToAddedDelta test
         */

        helper.getContainedResources().removeAll(
                ResourceSetTestUtils
                        .toResourceSet(validResource, errorResource));

        assertThat(captureDeltas(2).get(1).getRemovedElements(),
                containsEqualResources(validResource));
    }

    private void setCanResolveIfContainsResourceExactlyResolver(
            ResourceSet resourceSet) {

        VisualItemValueResolver resolver = mockResolverThatCanResolveExactResourceSet(resourceSet);
        underTest.setResolver(slot, resolver);
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        helper = new DefaultVisualizationModelTestHelper();
        slot = helper.createSlots(DataType.NUMBER)[0];
        underTest = helper.createTestViewModel();

        underTest
                .setResolver(slot, new FixedValueResolver(1d, DataType.NUMBER));
    }

    // TODO check highlighted resources in resource item
    @Test
    public void updateCalledWhenHighlightingChanges() {
        ResourceSet resources = createResources(1);

        helper.getContainedResources().addAll(resources);
        LightweightCollection<VisualItem> addedViewItems = captureDelta()
                .getAddedElements();

        helper.getHighlightedResources().addAll(resources);

        verify(helper.getViewContentDisplay(), times(1)).update(
                argThat(CollectionMatchers.<VisualItem> matchesDelta(isEmpty(),
                        containsExactlyLightweightCollection(addedViewItems),
                        isEmpty())), argThat(isEmpty(Slot.class)));
    }

    @Test
    public void updateCalledWhenResourcesRemoved() {
        ResourceSet resources1 = createResources(TYPE_1, 1);
        ResourceSet resources2 = createResources(ResourceSetTestUtils.TYPE_2, 2);
        ResourceSet resources = ResourceSetTestUtils.toResourceSet(resources1,
                resources2);

        helper.getContainedResources().addAll(resources);
        LightweightCollection<VisualItem> addedViewItems = captureDelta()
                .getAddedElements();

        helper.getContainedResources().removeAll(resources);
        verify(helper.getViewContentDisplay(), times(1))
                .update(argThat(CollectionMatchers
                        .<VisualItem> matchesDelta(
                                CollectionMatchers.isEmpty(VisualItem.class),
                                CollectionMatchers.isEmpty(VisualItem.class),
                                CollectionMatchers
                                        .containsExactlyLightweightCollection(addedViewItems))),
                        argThat(CollectionMatchers.isEmpty(Slot.class)));
    }

    @Test
    public void updateCalledWhenSelectionChanges() {
        ResourceSet resources = createResources(1);

        helper.getContainedResources().addAll(resources);
        LightweightCollection<VisualItem> addedViewItems = captureDelta()
                .getAddedElements();

        helper.getSelectedResources().add(createResource(1));

        verify(helper.getViewContentDisplay(), times(1)).update(
                argThat(CollectionMatchers.<VisualItem> matchesDelta(isEmpty(),
                        containsExactlyLightweightCollection(addedViewItems),
                        isEmpty())), argThat(isEmpty(Slot.class)));
    }

    /**
     * delta=updated, old_state=errors, current_state=valid ==&gt; add
     */
    @Test
    public void updatedViewItemsChangingFromErrorsToValidGetAddedWhenResourcesChange() {
        Resource resource1 = createResource(TYPE_1, 1);
        Resource resource2 = createResource(TYPE_1, 2);

        setCanResolveIfContainsResourceExactlyResolver(ResourceSetTestUtils
                .toResourceSet(resource1, resource2));

        helper.addToContainedResources(resource1);

        /* should not have been added yet - 0 update calls so far */

        helper.addToContainedResources(resource2);

        assertThat(captureDelta().getAddedElements(),
                containsEqualResources(resource1, resource2));
    }

    /**
     * delta=updated, old_state=valid, current_state=errors ==&gt; remove
     */
    @Test
    public void updatedViewItemsChangingFromValidToErrorsGetRemovedWhenResourcesChange() {
        Resource resource1 = createResource(TYPE_1, 1);
        Resource resource2 = createResource(TYPE_1, 2);

        setCanResolveIfContainsResourceExactlyResolver(toResourceSet(resource1,
                resource2));

        helper.addToContainedResources(toResourceSet(resource1, resource2));

        /* should now have 1 valid view item */

        helper.getContainedResources().remove(resource2);

        assertThat(captureDeltas(2).get(1).getRemovedElements(),
                containsEqualResources(resource1));
    }

    /**
     * delta=updated, old_state=valid, current_state=valid ==&gt; updated
     */
    @Test
    public void updatedViewItemsThatAreValidNowAndBeforeGetUpdatedWhenResourcesChange() {
        Resource resource1 = createResource(TYPE_1, 1);
        Resource resource2 = createResource(TYPE_1, 2);

        underTest.setResolver(slot, mockResolverThatCanAlwaysResolve());

        helper.addToContainedResources(resource1);

        // update call
        helper.addToContainedResources(resource2);

        assertThat(captureDeltas(2).get(1).getUpdatedElements(),
                containsEqualResources(resource1, resource2));
    }

    /**
     * delta=updated, old_state=errors, current_state=errors ==&gt; ignore
     */
    @SuppressWarnings("unchecked")
    @Test
    public void updatedViewItemsWithErrorsNowAndBeforeGetIgnoredWhenResourcesChange() {
        Resource validResource = createResource(TYPE_1, 3);

        setCanResolveIfContainsResourceExactlyResolver(ResourceSetTestUtils
                .toResourceSet(validResource));

        // adds error view item and correct view item
        helper.addToContainedResources(createResource(TYPE_1, 1));

        // updates error view item
        helper.addToContainedResources(createResource(TYPE_1, 2));

        // neither adding nor updating view item should have triggered calls to
        // update
        verify(helper.getViewContentDisplay(), never()).update(
                argThat(any(Delta.class)),
                argThat(CollectionMatchers.isEmpty(Slot.class)));
    }

    @Test
    public void updateNeverCalledOnHighlightingChangeThatDoesNotAffectViewResources() {
        helper.getContainedResources().add(createResource(2));
        helper.getHighlightedResources().add(createResource(1));

        verifyNoViewItemsUpdated();
    }

    private void verifyNoViewItemsAdded() {
        verify(helper.getViewContentDisplay(), never()).update(
                argThat(CollectionMatchers.<VisualItem> matchesDelta(
                        new IsAnything<LightweightCollection<VisualItem>>(),
                        CollectionMatchers.isEmpty(VisualItem.class),
                        CollectionMatchers.isEmpty(VisualItem.class))),
                argThat(CollectionMatchers.isEmpty(Slot.class)));
    }

    private void verifyNoViewItemsUpdated() {
        verify(helper.getViewContentDisplay(), never()).update(
                argThat(CollectionMatchers.<VisualItem> matchesDelta(
                        CollectionMatchers.isEmpty(VisualItem.class),
                        new IsAnything<LightweightCollection<VisualItem>>(),
                        CollectionMatchers.isEmpty(VisualItem.class))),
                argThat(CollectionMatchers.isEmpty(Slot.class)));
    }

    /**
     * Shows the bug that happens when the {@link ViewContentDisplay} is updated
     * before the {@link VisualItem} cache is cleaned on a {@link Slot} change.
     * The {@link DefaultVisualizationModel} needs to call
     * {@link DefaultVisualItem#clearValueCache(Slot)}.
     */
    @Test
    public void viewItemsReturnCorrectValuesOnViewContentDisplayUpdateAfterClearingSlotCache() {
        helper.addToContainedResources(createResource(TYPE_1, 1));

        final VisualItem viewItem = underTest.getVisualItems()
                .getFirstElement();
        viewItem.getValue(slot); // caches values

        // needs to be done before changing slot
        final double[] result = captureViewItemNumberSlotValueOnUpdate(viewItem);

        underTest
                .setResolver(slot, new FixedValueResolver(5d, DataType.NUMBER));

        assertEquals(5d, result[0], 0.000001d);
    }

    /**
     * Shows the bug that happens when the {@link ViewContentDisplay} is updated
     * before the {@link VisualItem} cache is cleaned on a {@link ResourceSet}
     * change.
     */
    @Test
    public void viewItemsReturnCorrectValuesOnViewContentDisplayUpdateAfterResourceSetChange() {
        String propertyName = "property";

        underTest.setResolver(slot, new CalculationResolver(propertyName,
                new SumCalculation()));

        Resource resource1 = createResource(TYPE_1, 1);
        resource1.putValue(propertyName, 1d);
        helper.addToContainedResources(resource1);

        final VisualItem viewItem = underTest.getVisualItems()
                .getFirstElement();
        viewItem.getValue(slot); // caches values

        // needs to be done before adding
        final double[] result = captureViewItemNumberSlotValueOnUpdate(viewItem);

        Resource resource2 = createResource(TYPE_1, 2);
        resource2.putValue(propertyName, 2d);
        helper.addToContainedResources(resource2);

        assertEquals(1d + 2d, result[0], 0.000001d);
    }

    @Test
    public void viewItemsThatBecomeInvalidAfterSlotResolverChangeGetRemoved() {
        underTest.setResolver(slot, mockResolverThatCanAlwaysResolve());
        helper.addToContainedResources(createResource(1));

        underTest.setResolver(slot, mockResolverThatCanNeverResolve());

        verify(helper.getViewContentDisplay(), times(1))
                .update(argThat(CollectionMatchers
                        .<VisualItem> matchesDelta(
                                isEmpty(VisualItem.class),
                                isEmpty(VisualItem.class),
                                containsVisualItemsForExactResourceSets(createResources(1)))),
                        (LightweightCollection<Slot>) argThat(containsExactly(slot)));
    }

}