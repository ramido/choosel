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
import static org.thechiselgroup.choosel.core.client.test.HamcrestResourceMatchers.containsExactly;
import static org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory.createResource;
import static org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory.toResourceSet;
import static org.thechiselgroup.choosel.core.client.views.model.ViewItemValueResolverTestUtils.mockResolverThatCanAlwaysResolve;
import static org.thechiselgroup.choosel.core.client.views.model.ViewItemValueResolverTestUtils.mockResolverThatCanResolveExactResourceSet;
import static org.thechiselgroup.choosel.core.client.views.model.ViewItemWithResourcesMatcher.containsEqualResources;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.core.client.resources.DataType;
import org.thechiselgroup.choosel.core.client.resources.DefaultResourceSet;
import org.thechiselgroup.choosel.core.client.resources.Resource;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory;
import org.thechiselgroup.choosel.core.client.views.resolvers.ViewItemValueResolver;

/**
 * Tests the error model {@link ViewItemResolutionErrorModel} of the
 * {@link ViewModel} subsystem implemented using {@link DefaultViewModel}.
 * 
 * @author Lars Grammel
 */
public class DefaultViewModelErrorTest {

    private DefaultViewModelTestHelper helper;

    private Resource resource1;

    private Resource resource2;

    private DefaultViewModel underTest;

    private void addResourcesToUndertest() {
        getResourceSetFromUnderTest().add(resource1);
        getResourceSetFromUnderTest().add(resource2);
    }

    @Test
    public void allSlotsHaveResolversCausesNoErrors() {
        Slot[] slots = helper.createSlots(DataType.TEXT);
        underTest = helper.createTestViewModel();

        underTest.setResolver(slots[0], mockResolverThatCanAlwaysResolve());

        getResourceSetFromUnderTest().add(createResource(1));

        assertThat(underTest.hasErrors(), is(false));
    }

    private ResourceSet getResourceSetFromUnderTest() {
        return underTest.getResourceGrouping().getResourceSet();
    }

    @Test
    public void resolverCannotResolveSomeViewItemsFixedByChangingResolverReturnsNoErrors() {
        Slot[] slots = helper.createSlots(DataType.TEXT);
        underTest = helper.createTestViewModel();

        setResolver(slots, resource1);
        addResourcesToUndertest();

        /*
         * there are currently errors on resource2 as per
         * resolverCannotResolveSomeViewItemsThrowsErrors test
         */

        underTest.setResolver(slots[0], mockResolverThatCanAlwaysResolve());
        assertThat(underTest.hasErrors(), is(false));
    }

    @Test
    public void resolverCannotResolveSomeViewItemsFixedByChangingViewItemsReturnsNoErrors() {
        Slot[] slots = helper.createSlots(DataType.TEXT);
        underTest = helper.createTestViewModel();

        setResolver(slots, resource1);
        addResourcesToUndertest();

        /*
         * there are currently errors on resource2 as per
         * resolverCannotResolveSomeViewItemsThrowsErrors test
         */
        getResourceSetFromUnderTest().remove(resource2);
        assertThat(underTest.hasErrors(), is(false));
    }

    @Test
    public void resolverCannotResolveSomeViewItemsThrowsErrors() {
        Slot[] slots = helper.createSlots(DataType.TEXT);
        underTest = helper.createTestViewModel();

        setResolver(slots, resource1);

        addResourcesToUndertest();

        assertThat(underTest.hasErrors(), is(true));
        assertThat(underTest.getSlotsWithErrors(), containsExactly(slots[0]));

        ResourceSet resources = new DefaultResourceSet();
        resources.add(resource2);
        assertThat(underTest.getViewItemsWithErrors(),
                containsEqualResources(resource2));
    }

    private void setResolver(Slot[] slots, Resource... resources) {
        ViewItemValueResolver resolver = mockResolverThatCanResolveExactResourceSet(toResourceSet(resources));
        underTest.setResolver(slots[0], resolver);
    }

    @Test
    public void setResolversFromUnconfiguredToValidReturnsNoErrors() {
        Slot[] slots = helper.createSlots(DataType.TEXT);
        underTest = helper.createTestViewModel();

        getResourceSetFromUnderTest().add(
                TestResourceSetFactory.createResource(1));
        /*
         * Model currently in a error state as per
         * slotWithoutResolverCausesError test
         */
        ViewItemValueResolver resolver = mockResolverThatCanAlwaysResolve();

        underTest.setResolver(slots[0], resolver);
        assertThat(underTest.hasErrors(), is(false));
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        helper = new DefaultViewModelTestHelper();
        resource1 = createResource("type1", 1);
        resource2 = createResource("type2", 2);
    }

    @Test
    public void slotWithoutResolverCausesError() {
        Slot[] slots = helper.createSlots(DataType.TEXT);
        underTest = helper.createTestViewModel();

        getResourceSetFromUnderTest().add(createResource(1));

        assertThat(underTest.hasErrors(), is(true));
        assertThat(underTest.getSlotsWithErrors(), containsExactly(slots[0]));
    }

    @Test
    public void someSlotsNotConfiguredReturnsError() {
        Slot[] slots = helper.createSlots(DataType.TEXT, DataType.NUMBER);

        underTest = helper.createTestViewModel();

        getResourceSetFromUnderTest().add(createResource(1));

        underTest.setResolver(slots[0], mockResolverThatCanAlwaysResolve());
        assertThat(underTest.hasErrors(), is(true));
        assertThat(underTest.getSlotsWithErrors(), containsExactly(slots[1]));
    }

}