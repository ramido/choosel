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
package org.thechiselgroup.choosel.client.resources;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.thechiselgroup.choosel.client.test.TestResourceSetFactory.createResource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CountingResourceSetTest {

    @Mock
    private ResourcesAddedEventHandler addedHandler;

    @Mock
    private ResourcesRemovedEventHandler removeHandler;

    private Resource resource;

    private CountingResourceSet underTest;

    @Test
    public void addedFiredOnceIfAddedTwice() {
        underTest.addEventHandler(addedHandler);
        underTest.add(resource);
        underTest.add(resource);

        verify(addedHandler, times(1)).onResourcesAdded(
                any(ResourcesAddedEvent.class));
    }

    @Test
    public void removeFiredOnceIfRemovedTwiceOnceAfterAddedTwice() {
        underTest.add(resource);
        underTest.add(resource);
        underTest.addEventHandler(removeHandler);
        underTest.remove(resource);
        underTest.remove(resource);

        verify(removeHandler, times(1)).onResourcesRemoved(
                any(ResourcesRemovedEvent.class));
    }

    @Test
    public void removeNotFiredIfOnlyRemovedOnceAfterAddedTwice() {
        underTest.add(resource);
        underTest.add(resource);
        underTest.addEventHandler(removeHandler);
        underTest.remove(resource);

        verify(removeHandler, never()).onResourcesRemoved(
                any(ResourcesRemovedEvent.class));
    }

    @Test
    public void resourceAddedTwiceNotContainedAfter2ndRemove() {
        underTest.add(resource);
        underTest.add(resource);
        underTest.remove(resource);
        underTest.remove(resource);

        assertEquals(false, underTest.contains(resource));
    }

    @Test
    public void resourceAddedTwiceStillContainedAfterRemove() {
        underTest.add(resource);
        underTest.add(resource);
        underTest.remove(resource);

        assertEquals(true, underTest.contains(resource));
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        resource = createResource(1);
        underTest = new CountingResourceSet();
    }
}
