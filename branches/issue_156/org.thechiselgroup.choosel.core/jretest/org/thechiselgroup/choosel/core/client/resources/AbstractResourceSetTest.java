/*******************************************************************************
 * Copyright 2009, 2010 Lars Grammel 
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
package org.thechiselgroup.choosel.core.client.resources;

import static org.junit.Assert.assertEquals;
import static org.thechiselgroup.choosel.core.client.test.ResourcesTestHelper.verifyOnResourceSetChanged;
import static org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory.createResource;
import static org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory.createResources;

import org.mockito.Mock;
import org.thechiselgroup.choosel.core.client.test.ResourcesTestHelper;

import com.google.gwt.event.shared.HandlerRegistration;

public class AbstractResourceSetTest {

    protected ResourceSet underTestAsResourceSet;

    @Mock
    protected ResourceSetChangedEventHandler changedHandler;

    protected void verifyChangeHandlerNotCalled() {
        verifyOnResourceSetChanged(0, changedHandler);
    }

    protected void verifyOnResourcesAdded(int... resourceNumbers) {
        ResourcesTestHelper.verifyOnResourcesAdded(
                createResources(resourceNumbers), changedHandler);
    }

    protected HandlerRegistration registerEventHandler() {
        return underTestAsResourceSet.addEventHandler(changedHandler);
    }

    protected void assertSizeEquals(int size) {
        assertEquals(size, underTestAsResourceSet.size());
    }

    protected void assertContainsResource(int resourceNumber, boolean expected) {
        assertEquals(expected, underTestAsResourceSet.contains(createResource(resourceNumber)));
    }

}