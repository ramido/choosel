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
import static org.thechiselgroup.choosel.core.client.resources.ResourcesTestHelper.verifyOnResourceSetChanged;
import static org.thechiselgroup.choosel.core.client.resources.ResourcesTestHelper.verifyOnResourcesAdded;
import static org.thechiselgroup.choosel.core.client.resources.ResourcesTestHelper.verifyOnResourcesRemoved;
import static org.thechiselgroup.choosel.core.client.resources.TestResourceSetFactory.createResource;
import static org.thechiselgroup.choosel.core.client.resources.TestResourceSetFactory.createResources;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CountingResourceSetTest {

    @Mock
    private ResourceSetChangedEventHandler changedHandler;

    private Resource resource;

    private CountingResourceSet underTest;

    @Test
    public void addAllFiresEvent() {
        ResourceSet resources = createResources(1, 2);

        underTest.addEventHandler(changedHandler);
        underTest.addAll(resources);

        verifyOnResourcesAdded(resources, changedHandler);
    }

    @Test
    public void addAllWithContainedResourcesDoesNotFireEvent() {
        ResourceSet containedResources = createResources(1, 2, 3);

        underTest.addAll(containedResources);
        underTest.addEventHandler(changedHandler);
        underTest.addAll(containedResources);

        verifyOnResourceSetChanged(0, changedHandler);
    }

    @Test
    public void addedFiredOnceIfAddedTwice() {
        underTest.addEventHandler(changedHandler);
        underTest.add(resource);
        underTest.add(resource);

        verifyOnResourceSetChanged(1, changedHandler);
    }

    @Test
    public void eventNotFiredOnRemoveAllOfDoubleContainedResources() {
        ResourceSet resources = createResources(1, 2, 3);

        underTest.addAll(resources);
        underTest.addAll(resources);
        underTest.addEventHandler(changedHandler);
        underTest.removeAll(resources);

        verifyOnResourceSetChanged(0, changedHandler);
    }

    @Test
    public void mixedAddAllFiresEventWithNewResources() {
        ResourceSet containedResources = createResources(1);
        ResourceSet resources = createResources(1, 2, 3);

        underTest.addAll(containedResources);
        underTest.addEventHandler(changedHandler);
        underTest.addAll(resources);

        verifyOnResourcesAdded(createResources(2, 3), changedHandler);
    }

    @Test
    public void mixedRemoveAllFiresEventWithActuallyRemovedResources() {
        ResourceSet doubleResources = createResources(1);
        ResourceSet resources = createResources(1, 2, 3);

        underTest.addAll(resources);
        underTest.addAll(doubleResources);
        underTest.addEventHandler(changedHandler);
        underTest.removeAll(resources);

        verifyOnResourcesRemoved(createResources(2, 3), changedHandler);
    }

    @Test
    public void removeAllFiresEvent() {
        ResourceSet resources = createResources(1, 2);

        underTest.addAll(resources);
        underTest.addEventHandler(changedHandler);
        underTest.removeAll(resources);

        verifyOnResourcesRemoved(resources, changedHandler);
    }

    @Test
    public void removeFiredOnceIfRemovedTwiceOnceAfterAddedTwice() {
        underTest.add(resource);
        underTest.add(resource);
        underTest.addEventHandler(changedHandler);
        underTest.remove(resource);
        underTest.remove(resource);

        verifyOnResourceSetChanged(1, changedHandler);
    }

    @Test
    public void removeNotFiredIfOnlyRemovedOnceAfterAddedTwice() {
        underTest.add(resource);
        underTest.add(resource);
        underTest.addEventHandler(changedHandler);
        underTest.remove(resource);

        verifyOnResourceSetChanged(0, changedHandler);
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
        underTest.addEventHandler(changedHandler);
        underTest.retainAll(createResources(1, 2));

        List<Resource> removedResources = verifyOnResourceSetChanged(1,
                changedHandler).getValue().getRemovedResources().toList();

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
        underTest.addEventHandler(changedHandler);
        underTest.retainAll(createResources(1, 2));

        List<Resource> removedResources = verifyOnResourceSetChanged(1,
                changedHandler).getValue().getRemovedResources().toList();

        assertEquals(1, removedResources.size());
        assertEquals(false, removedResources.contains(createResource(1)));
        assertEquals(false, removedResources.contains(createResource(2)));
        assertEquals(false, removedResources.contains(createResource(3)));
        assertEquals(true, removedResources.contains(createResource(4)));
    }

    @Test
    public void retainAllWithoutChangesDoesNotFireResourcesRemovedEvent() {
        underTest.addAll(createResources(1, 2));
        underTest.addEventHandler(changedHandler);
        underTest.retainAll(createResources(1, 2, 3));

        verifyOnResourceSetChanged(0, changedHandler);
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        resource = createResource(1);
        underTest = new CountingResourceSet();
    }
}
