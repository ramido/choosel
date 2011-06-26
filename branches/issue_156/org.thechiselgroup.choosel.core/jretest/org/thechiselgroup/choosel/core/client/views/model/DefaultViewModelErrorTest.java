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
import static org.mockito.Mockito.mock;
import static org.thechiselgroup.choosel.core.client.test.HamcrestResourceMatchers.containsExactly;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.core.client.resources.DataType;
import org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory;
import org.thechiselgroup.choosel.core.client.views.resolvers.SlotMappingUIModel;

/**
 * Tests the error model {@link ViewItemResolutionErrorModel} of the
 * {@link ViewModel} subsystem implemented using {@link DefaultViewModel}.
 * 
 * @author Lars Grammel
 */
public class DefaultViewModelErrorTest {

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        SlotMappingUIModel.TESTING = true;
    }

    @Ignore("issue 156 - reactivate once fixed stuff is exported from mapping cfg")
    @Test
    public void slotWithoutResolverCausesError() {
        Slot slot = new Slot("id-1", "text-slot", DataType.TEXT);

        DefaultViewModelTestHelper helper = new DefaultViewModelTestHelper();
        helper.setSlots(slot);
        helper.setInitializer(mock(SlotMappingInitializer.class));

        DefaultViewModel underTest = helper.createTestViewModel();

        underTest.getResourceGrouping().getResourceSet()
                .add(TestResourceSetFactory.createResource(1));

        assertThat(underTest.hasErrors(), is(true));
        assertThat(underTest.getSlotsWithErrors(), containsExactly(slot));
    }

    @After
    public void tearDown() {
        SlotMappingUIModel.TESTING = false;
    }

}