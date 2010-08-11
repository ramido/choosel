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

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CountingResourceSetTest {

    @Mock
    private ResourcesAddedEventHandler addedHandler;

    @Mock
    private ResourcesRemovedEventHandler removedHandler;

    private Resource resource;

    private CountingResourceSet underTest;

    @Test
    public void addAllFiresEvent() {
        ResourceSet resources = createResources(1, 2);

        underTest.addEventHandler(addedHandler);
        underTest.addAll(resources);

        ResourcesAddedEvent event = verifyOnResourcesAdded(1, addedHandler)
                .getValue();
        assertContentEquals(resources, event.getAddedResources());
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
        underTest.addEventHandler(removedHandler);
        underTest.removeAll(resources);

        verifyOnResourcesRemoved(0, removedHandler);
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
        assertContentEquals(createResources(2, 3), event.getAddedResources());
    }

    @Test
    public void mixedRemoveAllFiresEventWithActuallyRemovedResources() {
        ResourceSet doubleResources = createResources(1);
        ResourceSet resources = createResources(1, 2, 3);

        underTest.addAll(resources);
        underTest.addAll(doubleResources);
        underTest.addEventHandler(removedHandler);
        underTest.removeAll(resources);

        ResourcesRemovedEvent event = verifyOnResourcesRemoved(1,
                removedHandler).getValue();
        assertContentEquals(createResources(2, 3), event.getRemovedResources());
    }

    @Test
    public void removeAllFiresEvent() {
        ResourceSet resources = createResources(1, 2);

        underTest.addAll(resources);
        underTest.addEventHandler(removedHandler);
        underTest.removeAll(resources);

        ResourcesRemovedEvent event = verifyOnResourcesRemoved(1,
                removedHandler).getValue();
        assertContentEquals(resources, event.getRemovedResources());
    }

    @Test
    public void removeFiredOnceIfRemovedTwiceOnceAfterAddedTwice() {
        underTest.add(resource);
        underTest.add(resource);
        underTest.addEventHandler(removedHandler);
        underTest.remove(resource);
        underTest.remove(resource);

        verify(removedHandler, times(1)).onResourcesRemoved(
                any(ResourcesRemovedEvent.class));
    }

    @Test
    public void removeNotFiredIfOnlyRemovedOnceAfterAddedTwice() {
        underTest.add(resource);
        underTest.add(resource);
        underTest.addEventHandler(removedHandler);
        underTest.remove(resource);

        verify(removedHandler, never()).onResourcesRemoved(
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

    @Test
    public void retainAll() {
        underTest.addAll(createResources(1, 2, 3, 4));
        boolean result = underTest.retainAll(createResources(1, 2));

        assertEquals(true, result);
        assertEquals(2, underTest.size());
        assertEquals(true, underTest.contains(createResource(1)));
        assertEquals(true, underTest.contains(createResource(2)));
        assertEquals(false, underTest.contains(createResource(3)));
        assertEquals(false, underTest.contains(createResource(4)));
    }

    @Test
    public void retainAllFiresResourcesRemovedEvent() {
        underTest.addAll(createResources(1, 2, 3, 4));
        underTest.addEventHandler(removedHandler);
        underTest.retainAll(createResources(1, 2));

        List<Resource> removedResources = verifyOnResourcesRemoved(1,
                removedHandler).getValue().getRemovedResources();

        assertEquals(2, removedResources.size());
        assertEquals(false, removedResources.contains(createResource(1)));
        assertEquals(false, removedResources.contains(createResource(2)));
        assertEquals(true, removedResources.contains(createResource(3)));
        assertEquals(true, removedResources.contains(createResource(4)));
    }

    @Test
    public void retainAllWithDoubleResource() {
        underTest.addAll(createResources(1, 2, 3, 4));
        underTest.addAll(createResources(3));
        boolean result = underTest.retainAll(createResources(1, 2));

        assertEquals(true, result);
        assertEquals(3, underTest.size());
        assertEquals(true, underTest.contains(createResource(1)));
        assertEquals(true, underTest.contains(createResource(2)));
        assertEquals(true, underTest.contains(createResource(3)));
        assertEquals(false, underTest.contains(createResource(4)));
    }

    @Test
    public void retainAllWithDoubleResourceFiresResourcesRemovedEvent() {
        underTest.addAll(createResources(1, 2, 3, 4));
        underTest.addAll(createResources(3));
        underTest.addEventHandler(removedHandler);
        underTest.retainAll(createResources(1, 2));

        List<Resource> removedResources = verifyOnResourcesRemoved(1,
                removedHandler).getValue().getRemovedResources();

        assertEquals(1, removedResources.size());
        assertEquals(false, removedResources.contains(createResource(1)));
        assertEquals(false, removedResources.contains(createResource(2)));
        assertEquals(false, removedResources.contains(createResource(3)));
        assertEquals(true, removedResources.contains(createResource(4)));
    }

    @Test
    public void retainAllWithoutChangesDoesNotFireResourcesRemovedEvent() {
        underTest.addAll(createResources(1, 2));
        underTest.addEventHandler(removedHandler);
        underTest.retainAll(createResources(1, 2, 3));

        verifyOnResourcesRemoved(0, removedHandler);
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        resource = createResource(1);
        underTest = new CountingResourceSet();
    }
}
