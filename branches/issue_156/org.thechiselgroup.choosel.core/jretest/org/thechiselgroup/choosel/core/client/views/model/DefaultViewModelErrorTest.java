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
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.thechiselgroup.choosel.core.client.test.HamcrestResourceMatchers.containsExactly;
import static org.thechiselgroup.choosel.core.client.views.model.ViewItemValueResolverTestUtils.mockViewItemValueResolver;
import static org.thechiselgroup.choosel.core.client.views.model.ViewItemWithSingleResourceMatcher.containsEqualResource;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
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

    @Test
    public void allSlotsHaveResolversCausesNoErrors() {
        Slot[] slots = helper.createSlots(DataType.TEXT);
        DefaultViewModel underTest = helper.createTestViewModel();

        ViewItemValueResolver resolver = mockViewItemValueResolver();

        when(
                resolver.canResolve(any(ViewItem.class),
                        any(ViewItemValueResolverContext.class))).thenReturn(
                true);

        underTest.setResolver(slots[0], resolver);

        underTest.getResourceGrouping().getResourceSet()
                .add(TestResourceSetFactory.createResource(1));

        assertThat(underTest.hasErrors(), is(false));
    }

    @Ignore("Not yet implemented")
    @Test
    public void fixResolverCannotResolveSomeViewItemsByChangingResolverThrowsNoErrors() {

    }

    @Ignore("Not yet implemented")
    @Test
    public void fixResolverCannotResolveSomeViewItemsByChangingViewItemsThrowsNoErrors() {

    }

    @Test
    public void resolverCannotResolveSomeViewItemsThrowsErrors() {
        Slot[] slots = helper.createSlots(DataType.TEXT);
        DefaultViewModel underTest = helper.createTestViewModel();

        ViewItemValueResolver resolver = mockViewItemValueResolver();
        /*
         * We give these resources different types because the testHelper's
         * categorizer groups by type
         */
        final Resource resource1 = TestResourceSetFactory.createResource(
                "type1", 1);
        final Resource resource2 = TestResourceSetFactory.createResource(
                "type12", 2);

        when(
                resolver.canResolve(any(ViewItem.class),
                        any(ViewItemValueResolverContext.class))).thenAnswer(
                new Answer<Boolean>() {
                    @Override
                    public Boolean answer(InvocationOnMock invocation)
                            throws Throwable {
                        ViewItem viewItem = (ViewItem) invocation
                                .getArguments()[0];
                        ResourceSet set = viewItem.getResources();

                        return set.size() == 1 && set.contains(resource1);
                    };
                });

        underTest.setResolver(slots[0], resolver);

        underTest.getResourceGrouping().getResourceSet().add(resource1);
        underTest.getResourceGrouping().getResourceSet().add(resource2);

        assertThat(underTest.hasErrors(), is(true));
        assertThat(underTest.getSlotsWithErrors(), containsExactly(slots[0]));

        assertThat(underTest.getViewItemsWithErrors(),
                containsEqualResource(resource2));
    }

    @Ignore("Not yet implemented")
    @Test
    public void setResolversFromUnconfiguredToValidThrowsNoErrors() {

    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        SlotMappingUIModel.TESTING = true;

        helper = new DefaultViewModelTestHelper();
        helper.mockInitializer();
    }

    @Test
    public void slotWithoutResolverCausesError() {
        Slot[] slots = helper.createSlots(DataType.TEXT);
        DefaultViewModel underTest = helper.createTestViewModel();

        underTest.getResourceGrouping().getResourceSet()
                .add(TestResourceSetFactory.createResource(1));

        assertThat(underTest.hasErrors(), is(true));
        assertThat(underTest.getSlotsWithErrors(), containsExactly(slots[0]));
    }

    @After
    public void tearDown() {
        SlotMappingUIModel.TESTING = false;
    }

}