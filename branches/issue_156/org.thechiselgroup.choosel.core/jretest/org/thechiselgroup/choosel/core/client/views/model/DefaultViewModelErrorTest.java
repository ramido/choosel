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
import static org.thechiselgroup.choosel.core.client.views.model.ViewItemValueResolverTestUtils.createResolverCanResolveResource;
import static org.thechiselgroup.choosel.core.client.views.model.ViewItemValueResolverTestUtils.mockAlwaysApplicableResolver;
import static org.thechiselgroup.choosel.core.client.views.model.ViewItemWithSingleResourceMatcher.containsEqualResource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.core.client.resources.DataType;
import org.thechiselgroup.choosel.core.client.resources.Resource;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory;
import org.thechiselgroup.choosel.core.client.views.resolvers.SlotMappingUIModel;
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

        underTest.setResolver(slots[0],
                ViewItemValueResolverTestUtils.mockAlwaysApplicableResolver());

        getResourceSetFromUnderTest().add(
                TestResourceSetFactory.createResource(1));

        assertThat(underTest.hasErrors(), is(false));
    }

    private ResourceSet getResourceSetFromUnderTest() {
        return underTest.getResourceGrouping().getResourceSet();
    }

    @Test
    public void resolverCannotResolveSomeViewItemsFixedByChangingResolverReturnsNoErrors() {
        Slot[] slots = helper.createSlots(DataType.TEXT);
        underTest = helper.createTestViewModel();

        underTest.setResolver(slots[0],
                createResolverCanResolveResource(resource1));
        addResourcesToUndertest();

        /*
         * there are currently errors on resource2 as per
         * resolverCannotResolveSomeViewItemsThrowsErrors test
         */

        underTest.setResolver(slots[0],
                ViewItemValueResolverTestUtils.mockAlwaysApplicableResolver());
        assertThat(underTest.hasErrors(), is(false));
    }

    @Test
    public void resolverCannotResolveSomeViewItemsFixedByChangingViewItemsReturnsNoErrors() {
        Slot[] slots = helper.createSlots(DataType.TEXT);
        underTest = helper.createTestViewModel();

        underTest.setResolver(slots[0],
                createResolverCanResolveResource(resource1));
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

        underTest.setResolver(slots[0],
                createResolverCanResolveResource(resource1));

        addResourcesToUndertest();

        assertThat(underTest.hasErrors(), is(true));
        assertThat(underTest.getSlotsWithErrors(), containsExactly(slots[0]));

        assertThat(underTest.getViewItemsWithErrors(),
                containsEqualResource(resource2));
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
        ViewItemValueResolver resolver = mockAlwaysApplicableResolver();

        underTest.setResolver(slots[0], resolver);
        assertThat(underTest.hasErrors(), is(false));
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        SlotMappingUIModel.TESTING = true;

        helper = new DefaultViewModelTestHelper();
        helper.mockInitializer();

        /*
         * We give these resources different types because the testHelper's
         * categorizer groups by type
         */
        resource1 = TestResourceSetFactory.createResource("type1", 1);
        resource2 = TestResourceSetFactory.createResource("type2", 2);
    }

    @Test
    public void slotWithoutResolverCausesError() {
        Slot[] slots = helper.createSlots(DataType.TEXT);
        underTest = helper.createTestViewModel();

        getResourceSetFromUnderTest().add(
                TestResourceSetFactory.createResource(1));

        assertThat(underTest.hasErrors(), is(true));
        assertThat(underTest.getSlotsWithErrors(), containsExactly(slots[0]));
    }

    @Test
    public void someSlotsNotConfiguredReturnsError() {
        Slot[] slots = helper.createSlots(DataType.TEXT, DataType.NUMBER);

        underTest = helper.createTestViewModel();

        getResourceSetFromUnderTest().add(
                TestResourceSetFactory.createResource(1));

        underTest.setResolver(slots[0], mockAlwaysApplicableResolver());
        assertThat(underTest.hasErrors(), is(true));
        assertThat(underTest.getSlotsWithErrors(), containsExactly(slots[1]));
    }

    @After
    public void tearDown() {
        SlotMappingUIModel.TESTING = false;
    }

}