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
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.thechiselgroup.choosel.core.client.test.HamcrestResourceMatchers.containsExactly;
import static org.thechiselgroup.choosel.core.client.test.ResourcesTestHelper.containsViewItemsForExactResourceSets;
import static org.thechiselgroup.choosel.core.client.test.ResourcesTestHelper.emptyLightweightCollection;
import static org.thechiselgroup.choosel.core.client.test.ResourcesTestHelper.eqViewItems;
import static org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory.TYPE_1;
import static org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory.TYPE_2;
import static org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory.createResource;
import static org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory.createResources;
import static org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory.toResourceSet;
import static org.thechiselgroup.choosel.core.client.views.model.VisualItemValueResolverTestUtils.mockResolverThatCanAlwaysResolve;
import static org.thechiselgroup.choosel.core.client.views.model.VisualItemValueResolverTestUtils.mockResolverThatCanNeverResolve;
import static org.thechiselgroup.choosel.core.client.views.model.VisualItemValueResolverTestUtils.mockResolverThatCanResolveExactResourceSet;
import static org.thechiselgroup.choosel.core.client.views.model.VisualItemWithResourcesMatcher.containsEqualResources;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.thechiselgroup.choosel.core.client.resources.DataType;
import org.thechiselgroup.choosel.core.client.resources.Resource;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.util.math.SumCalculation;
import org.thechiselgroup.choosel.core.client.views.model.VisualItem.Subset;
import org.thechiselgroup.choosel.core.client.views.resolvers.CalculationResolver;
import org.thechiselgroup.choosel.core.client.views.resolvers.FixedValueResolver;
import org.thechiselgroup.choosel.core.client.views.resolvers.ViewItemValueResolver;

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
     * Convert input to {@code LightWeightCollection<ViewItem>}
     */
    @SuppressWarnings("rawtypes")
    private static List<LightweightCollection<VisualItem>> cast(
            List<LightweightCollection> allValues) {

        List<LightweightCollection<VisualItem>> result = new ArrayList<LightweightCollection<VisualItem>>();
        for (LightweightCollection<VisualItem> lightweightCollection : allValues) {
            result.add(lightweightCollection);
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

        setCanResolverIfContainsResourceExactlyResolver(toResourceSet(validResource));

        helper.addToContainedResources(toResourceSet(validResource,
                createResource(TYPE_2, 1)));

        assertThat(captureAddedViewItems(),
                containsEqualResources(validResource));
    }

    @Test
    public void addingTwoViewItemsInOneStepCallsUpdateOnce() {
        ResourceSet resources1 = createResources(TYPE_1, 1);
        ResourceSet resources2 = createResources(TYPE_2, 2);

        helper.addToContainedResources(toResourceSet(resources1, resources2));

        verify(helper.getViewContentDisplay(), times(1))
                .update(argThat(containsViewItemsForExactResourceSets(resources1,
                        resources2)),
                        emptyLightweightCollection(VisualItem.class),
                        emptyLightweightCollection(VisualItem.class),
                        emptyLightweightCollection(Slot.class));
    }

    @Test
    public void addingTwoViewItemsInTwoStepsCallsUpdateTwice() {
        ResourceSet resources1 = createResources(TYPE_1, 1);
        ResourceSet resources2 = createResources(TYPE_2, 2);

        helper.addToContainedResources(resources1);
        helper.addToContainedResources(resources2);

        List<LightweightCollection<VisualItem>> allValues = captureAddedViewItems(2);

        assertThat(allValues.get(0),
                containsViewItemsForExactResourceSets(resources1));
        assertThat(allValues.get(1),
                containsViewItemsForExactResourceSets(resources2));
    }

    private LightweightCollection<VisualItem> captureAddedViewItems() {
        return captureAddedViewItems(1).get(0);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private List<LightweightCollection<VisualItem>> captureAddedViewItems(
            int wantedNumberOfInvocation) {

        ArgumentCaptor<LightweightCollection> captor = ArgumentCaptor
                .forClass(LightweightCollection.class);
        verify(helper.getViewContentDisplay(), times(wantedNumberOfInvocation))
                .update(captor.capture(),
                        emptyLightweightCollection(VisualItem.class),
                        emptyLightweightCollection(VisualItem.class),
                        emptyLightweightCollection(Slot.class));

        return cast(captor.getAllValues());
    }

    private LightweightCollection<VisualItem> captureRemovedViewItems() {
        return captureRemovedViewItems(1).get(0);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private List<LightweightCollection<VisualItem>> captureRemovedViewItems(
            int wantedNumberOfInvocation) {

        ArgumentCaptor<LightweightCollection> captor = ArgumentCaptor
                .forClass(LightweightCollection.class);
        verify(helper.getViewContentDisplay(), times(wantedNumberOfInvocation))
                .update(emptyLightweightCollection(VisualItem.class),
                        emptyLightweightCollection(VisualItem.class),
                        captor.capture(),
                        emptyLightweightCollection(Slot.class));

        return cast(captor.getAllValues());
    }

    private LightweightCollection<VisualItem> captureUpdatedViewItems() {
        return captureUpdatedViewItems(1).get(0);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private List<LightweightCollection<VisualItem>> captureUpdatedViewItems(
            int wantedNumberOfInvocation) {

        ArgumentCaptor<LightweightCollection> captor = ArgumentCaptor
                .forClass(LightweightCollection.class);
        verify(helper.getViewContentDisplay(), times(wantedNumberOfInvocation))
                .update(emptyLightweightCollection(VisualItem.class),
                        captor.capture(),
                        emptyLightweightCollection(VisualItem.class),
                        emptyLightweightCollection(Slot.class));

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
                any(LightweightCollection.class),
                any(LightweightCollection.class),
                any(LightweightCollection.class),
                any(LightweightCollection.class));
        return result;
    }

    @Test
    public void highlightingChangeOnSeveralResourcesTriggersSingleUpdate() {
        ResourceSet resources = createResources(1, 2);

        helper.getContainedResources().addAll(resources);
        helper.getHighlightedResources().addAll(resources);

        LightweightCollection<VisualItem> viewItems = captureUpdatedViewItems();

        assertThat(
                viewItems.getFirstElement().getResources(Subset.HIGHLIGHTED),
                containsExactly(resources));
    }

    @Test
    public void numberSlot() {
        underTest.setResolver(slot, mockResolverThatCanNeverResolve());
        helper.addToContainedResources(createResource(1));
        verifyNoViewItemsAdded();

        underTest.setResolver(slot, mockResolverThatCanAlwaysResolve());

        verify(helper.getViewContentDisplay(), times(1)).update(
                argThat(containsViewItemsForExactResourceSets(createResources(1))),
                emptyLightweightCollection(VisualItem.class),
                emptyLightweightCollection(VisualItem.class),
                (LightweightCollection<Slot>) argThat(containsExactly(slot)));
    }

    /**
     * delta=removed, old_state=errors ==&gt; ignore
     */
    @Test
    public void removedViewItemsWithErrorsGetIgnoredWhenResourcesChange() {
        Resource validResource = createResource(TYPE_1, 1);
        Resource errorResource = createResource(TYPE_2, 1);

        setCanResolverIfContainsResourceExactlyResolver(TestResourceSetFactory
                .toResourceSet(validResource));

        helper.addToContainedResources(TestResourceSetFactory.toResourceSet(
                validResource, errorResource));

        /*
         * at this point, the view item with errorResource is invalid as per
         * invalidViewItemDoesNotGetAddedToAddedDelta test
         */

        helper.getContainedResources().removeAll(
                TestResourceSetFactory.toResourceSet(validResource,
                        errorResource));

        assertThat(captureRemovedViewItems(),
                containsEqualResources(validResource));
    }

    private void setCanResolverIfContainsResourceExactlyResolver(
            ResourceSet resourceSet) {

        ViewItemValueResolver resolver = mockResolverThatCanResolveExactResourceSet(resourceSet);
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
        LightweightCollection<VisualItem> addedViewItems = captureAddedViewItems();

        helper.getHighlightedResources().addAll(resources);

        verify(helper.getViewContentDisplay(), times(1)).update(
                emptyLightweightCollection(VisualItem.class),
                eqViewItems(addedViewItems),
                emptyLightweightCollection(VisualItem.class),
                emptyLightweightCollection(Slot.class));
    }

    @Test
    public void updateCalledWhenResourcesRemoved() {
        ResourceSet resources1 = createResources(TYPE_1, 1);
        ResourceSet resources2 = createResources(TYPE_2, 2);
        ResourceSet resources = toResourceSet(resources1, resources2);

        helper.getContainedResources().addAll(resources);
        LightweightCollection<VisualItem> addedViewItems = captureAddedViewItems();

        helper.getContainedResources().removeAll(resources);
        verify(helper.getViewContentDisplay(), times(1)).update(
                emptyLightweightCollection(VisualItem.class),
                emptyLightweightCollection(VisualItem.class),
                eqViewItems(addedViewItems),
                emptyLightweightCollection(Slot.class));
    }

    @Test
    public void updateCalledWhenSelectionChanges() {
        ResourceSet resources = createResources(1);

        helper.getContainedResources().addAll(resources);
        LightweightCollection<VisualItem> addedViewItems = captureAddedViewItems();

        helper.getSelectedResources().add(createResource(1));

        verify(helper.getViewContentDisplay(), times(1)).update(
                emptyLightweightCollection(VisualItem.class),
                eqViewItems(addedViewItems),
                emptyLightweightCollection(VisualItem.class),
                emptyLightweightCollection(Slot.class));
    }

    /**
     * delta=updated, old_state=errors, current_state=valid ==&gt; add
     */
    @Test
    public void updatedViewItemsChangingFromErrorsToValidGetAddedWhenResourcesChange() {
        Resource resource1 = createResource(TYPE_1, 1);
        Resource resource2 = createResource(TYPE_1, 2);

        setCanResolverIfContainsResourceExactlyResolver(toResourceSet(
                resource1, resource2));

        helper.addToContainedResources(resource1);

        /* should not have been added yet - 0 update calls so far */

        helper.addToContainedResources(resource2);

        assertThat(captureAddedViewItems(),
                containsEqualResources(resource1, resource2));
    }

    /**
     * delta=updated, old_state=valid, current_state=errors ==&gt; remove
     */
    @Test
    public void updatedViewItemsChangingFromValidToErrorsGetRemovedWhenResourcesChange() {
        Resource resource1 = createResource(TYPE_1, 1);
        Resource resource2 = createResource(TYPE_1, 2);

        setCanResolverIfContainsResourceExactlyResolver(toResourceSet(
                resource1, resource2));

        helper.addToContainedResources(toResourceSet(resource1, resource2));

        /* should now have 1 valid view item */

        helper.getContainedResources().remove(resource2);

        assertThat(captureRemovedViewItems(), containsEqualResources(resource1));
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

        assertThat(captureUpdatedViewItems(),
                containsEqualResources(resource1, resource2));
    }

    /**
     * delta=updated, old_state=errors, current_state=errors ==&gt; ignore
     */
    @SuppressWarnings("unchecked")
    @Test
    public void updatedViewItemsWithErrorsNowAndBeforeGetIgnoredWhenResourcesChange() {
        Resource validResource = createResource(TYPE_1, 3);

        setCanResolverIfContainsResourceExactlyResolver(toResourceSet(validResource));

        // adds error view item and correct view item
        helper.addToContainedResources(createResource(TYPE_1, 1));

        // updates error view item
        helper.addToContainedResources(createResource(TYPE_1, 2));

        // neither adding nor updating view item should have triggered calls to
        // update
        verify(helper.getViewContentDisplay(), never()).update(
                any(LightweightCollection.class),
                any(LightweightCollection.class),
                any(LightweightCollection.class),
                emptyLightweightCollection(Slot.class));

    }

    @Test
    public void updateNeverCalledOnHighlightingChangeThatDoesNotAffectViewResources() {
        helper.getContainedResources().add(createResource(2));
        helper.getHighlightedResources().add(createResource(1));

        verifyNoViewItemsUpdated();
    }

    @SuppressWarnings("unchecked")
    private void verifyNoViewItemsAdded() {
        verify(helper.getViewContentDisplay(), never()).update(
                any(LightweightCollection.class),
                emptyLightweightCollection(VisualItem.class),
                emptyLightweightCollection(VisualItem.class),
                emptyLightweightCollection(Slot.class));
    }

    @SuppressWarnings("unchecked")
    private void verifyNoViewItemsUpdated() {
        verify(helper.getViewContentDisplay(), never()).update(
                emptyLightweightCollection(VisualItem.class),
                any(LightweightCollection.class),
                emptyLightweightCollection(VisualItem.class),
                emptyLightweightCollection(Slot.class));
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

        final VisualItem viewItem = underTest.getViewItems().getFirstElement();
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

        final VisualItem viewItem = underTest.getViewItems().getFirstElement();
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

        verify(helper.getViewContentDisplay(), times(1)).update(
                emptyLightweightCollection(VisualItem.class),
                emptyLightweightCollection(VisualItem.class),
                argThat(containsViewItemsForExactResourceSets(createResources(1))),
                (LightweightCollection<Slot>) argThat(containsExactly(slot)));
    }

}