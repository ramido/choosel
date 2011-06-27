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

import org.junit.After;
import org.junit.Before;
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

    private DefaultViewModelTestHelper helper;

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