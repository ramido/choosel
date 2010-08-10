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
import static org.thechiselgroup.choosel.client.test.AdvancedAsserts.assertContentEquals;
import static org.thechiselgroup.choosel.client.test.ResourcesTestHelper.verifyOnResourcesAdded;
import static org.thechiselgroup.choosel.client.test.ResourcesTestHelper.verifyOnResourcesRemoved;
import static org.thechiselgroup.choosel.client.test.TestResourceSetFactory.createResource;
import static org.thechiselgroup.choosel.client.test.TestResourceSetFactory.createResources;

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
    public void addAllFiresEvent() {
        ResourceSet resources = createResources(1, 2);

        underTest.addEventHandler(addedHandler);
        underTest.addAll(resources);

        ResourcesAddedEvent event = verifyOnResourcesAdded(1, addedHandler)
                .getValue();
        assertContentEquals(resources.toList(), event.getAddedResources());
    }

    @Test
    public void addAllWithContainedResourcesDoesNotFireEvent() {
        ResourceSet containedResources = createResources(1, 2, 3);

        underTest.addAll(containedResources);
        underTest.addEventHandler(addedHandler);
        underTest.addAll(containedResources);

        verifyOnResourcesAdded(0, addedHandler);
    }

    @Test
    public void addedFiredOnceIfAddedTwice() {
        underTest.addEventHandler(addedHandler);
        underTest.add(resource);
        underTest.add(resource);

        verify(addedHandler, times(1)).onResourcesAdded(
                any(ResourcesAddedEvent.class));
    }

    @Test
    public void eventNotFiredOnRemoveAllOfDoubleContainedResources() {
        ResourceSet resources = createResources(1, 2, 3);

        underTest.addAll(resources);
        underTest.addAll(resources);
        underTest.addEventHandler(removeHandler);
        underTest.removeAll(resources);

        verifyOnResourcesRemoved(0, removeHandler);
    }

    @Test
    public void mixedAddAllFiresEventWithNewResources() {
        ResourceSet containedResources = createResources(1);
        ResourceSet resources = createResources(1, 2, 3);

        underTest.addAll(containedResources);
        underTest.addEventHandler(addedHandler);
        underTest.addAll(resources);

        ResourcesAddedEvent event = verifyOnResourcesAdded(1, addedHandler)
                .getValue();
        assertContentEquals(createResources(2, 3).toList(),
                event.getAddedResources());
    }

    @Test
    public void mixedRemoveAllFiresEventWithActuallyRemovedResources() {
        ResourceSet doubleResources = createResources(1);
        ResourceSet resources = createResources(1, 2, 3);

        underTest.addAll(resources);
        underTest.addAll(doubleResources);
        underTest.addEventHandler(removeHandler);
        underTest.removeAll(resources);

        ResourcesRemovedEvent event = verifyOnResourcesRemoved(1, removeHandler)
                .getValue();
        assertContentEquals(createResources(2, 3).toList(),
                event.getRemovedResources());
    }

    // TODO not fire remove if not remove
    @Test
    public void removeAllFiresEvent() {
        ResourceSet resources = createResources(1, 2);

        underTest.addAll(resources);
        underTest.addEventHandler(removeHandler);
        underTest.removeAll(resources);

        ResourcesRemovedEvent event = verifyOnResourcesRemoved(1, removeHandler)
                .getValue();
        assertContentEquals(resources.toList(), event.getRemovedResources());
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
