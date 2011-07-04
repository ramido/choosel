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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.thechiselgroup.choosel.core.client.test.ResourcesTestHelper.containsViewItemsForResourceSets;
import static org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory.TYPE_1;
import static org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory.TYPE_2;
import static org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory.createResources;
import static org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory.toResourceSet;
import static org.thechiselgroup.choosel.core.client.views.model.ViewItemValueResolverTestUtils.mockResolver;
import static org.thechiselgroup.choosel.core.client.views.model.ViewItemValueResolverTestUtils.mockResolverThatCanAlwaysResolve;
import static org.thechiselgroup.choosel.core.client.views.model.ViewItemValueResolverTestUtils.mockResolverThatCanNeverResolve;
import static org.thechiselgroup.choosel.core.client.views.model.ViewItemValueResolverTestUtils.mockResolverThatCanResolveExactResourceSet;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.core.client.resources.DataType;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.views.resolvers.ViewItemValueResolver;

/**
 * <p>
 * Tests the {@link ViewContentDisplayCallback} exposed by
 * {@link DefaultViewModel} to its {@link ViewContentDisplay}.
 * </p>
 * 
 * @author Lars Grammel
 */
// TODO extract AbstractDefaultViewModelTest superclass
// TODO handler
// TODO getById
// TODO get... by resources
public class DefaultViewModelViewContentDisplayCallbackTest {

    private Slot slot;

    private DefaultViewModel underTest;

    private DefaultViewModelTestHelper helper;

    private ViewContentDisplayCallback callback;

    @Test
    public void containsViewItemsReturnsFalseForViewItemsWithErrors() {
        ResourceSet resources = createResources(TYPE_1, 1);

        underTest.setResolver(slot, mockResolverThatCanNeverResolve());
        helper.addToContainedResources(resources);

        assertThat(
                callback.containsViewItem(underTest.getViewItems()
                        .getFirstElement().getViewItemID()), is(false));
    }

    @Test
    public void containsViewItemsReturnsTrueForViewItemsWithoutErrors() {
        ResourceSet resources = createResources(TYPE_1, 1);

        underTest.setResolver(slot, mockResolverThatCanAlwaysResolve());
        helper.addToContainedResources(resources);

        assertThat(
                callback.containsViewItem(underTest.getViewItems()
                        .getFirstElement().getViewItemID()), is(true));
    }

    @Test
    public void getViewItemsExcludesViewItemsWithErrors() {
        ResourceSet resources1 = createResources(TYPE_1, 1);
        ResourceSet resources2 = createResources(TYPE_2, 1);

        setCanResolverIfContainsResourceExactlyResolver(resources1);
        helper.addToContainedResources(toResourceSet(resources1, resources2));

        assertThat(callback.getViewItems(),
                containsViewItemsForResourceSets(resources1));
    }

    @Test
    public void getViewItemsReturnsAddedViewItem() {
        ResourceSet resources = createResources(TYPE_1, 1);

        underTest.setResolver(slot, mockResolverThatCanAlwaysResolve());
        helper.addToContainedResources(resources);

        assertThat(callback.getViewItems(),
                containsViewItemsForResourceSets(resources));
    }

    private void setCanResolverIfContainsResourceExactlyResolver(
            ResourceSet resourceSet) {

        ViewItemValueResolver resolver = mockResolverThatCanResolveExactResourceSet(resourceSet);
        underTest.setResolver(slot, resolver);
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        helper = new DefaultViewModelTestHelper();
        slot = helper.createSlots(DataType.TEXT)[0];

        underTest = helper.createTestViewModel();

        underTest.setResolver(slot, mockResolver());

        ArgumentCaptor<ViewContentDisplayCallback> captor = ArgumentCaptor
                .forClass(ViewContentDisplayCallback.class);
        verify(helper.getViewContentDisplay(), times(1)).init(captor.capture());
        callback = captor.getValue();
    }
}