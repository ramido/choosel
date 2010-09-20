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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.thechiselgroup.choosel.client.test.ResourcesTestHelper.verifyOnResourcesAdded;
import static org.thechiselgroup.choosel.client.test.TestResourceSetFactory.createLabeledResources;
import static org.thechiselgroup.choosel.client.test.TestResourceSetFactory.createResource;
import static org.thechiselgroup.choosel.client.test.TestResourceSetFactory.createResources;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CombinedResourceSetTest {

    @Mock
    private ResourcesAddedEventHandler addedHandler;

    private CombinedResourceSet underTest;

    @Test
    public void addMultipleResourcesToContainedResourceSet() {
        ResourceSet resources = createLabeledResources(1, 2, 3);

        underTest.addResourceSet(resources);

        resources.addAll(createResources(4, 5));

        assertEquals(true, underTest.contains(createResource(4)));
        assertEquals(true, underTest.contains(createResource(5)));
    }

    @Test
    public void addResourcesAddsToAllResources() {
        underTest.addResourceSet(createLabeledResources(1, 2, 3));
        underTest.addResourceSet(createLabeledResources(3, 4, 5));

        assertEquals(5, underTest.size());
        assertTrue(underTest.containsAll(createLabeledResources(1, 2, 3)));
        assertTrue(underTest.containsAll(createLabeledResources(3, 4, 5)));
    }

    @Test
    public void containsResourcesAddedToChildren() {
        ResourceSet resources = createLabeledResources(1, 2, 3);

        underTest.addResourceSet(resources);
        resources.add(createResource(5));

        assertEquals(4, underTest.size());
        assertTrue(underTest.containsAll(resources));
    }

    @Test
    public void doesNotContainResourcesRemovedFromChildren() {
        ResourceSet resources = createLabeledResources(1, 2, 3);

        underTest.addResourceSet(resources);
        resources.remove(createResource(1));

        assertEquals(2, underTest.size());
        assertTrue(underTest.containsAll(resources));
        assertFalse(underTest.contains(createResource(1)));
    }

    @Test
    public void doesNotRemoveDuplicateResourceOnRemove() {
        ResourceSet resources = createLabeledResources(1, 2, 3);

        underTest.addResourceSet(resources);
        underTest.addResourceSet(createLabeledResources(3, 4, 5));
        resources.remove(createResource(3));

        assertEquals(5, underTest.size());
        assertTrue(underTest.containsAll(resources));
        assertTrue(underTest.containsAll(createLabeledResources(3, 4, 5)));
    }

    @Test
    public void fireAddEventsWhenResourceSetAdded() {
        underTest.addEventHandler(addedHandler);
        underTest.addResourceSet(createLabeledResources(1, 2, 3));

        ArgumentCaptor<ResourcesAddedEvent> argument = verifyOnResourcesAdded(
                1, addedHandler);

        List<Resource> eventResources = argument.getValue().getAddedResources();
        assertEquals(3, eventResources.size());
    }

    @Test
    public void noContainmenChangeWhenResourceAddedAfterRemove() {
        Resource addedResource = createResource(8);
        ResourceSet resources = createLabeledResources(1, 2, 3);

        underTest.addResourceSet(resources);
        underTest.removeResourceSet(resources);
        resources.add(addedResource);

        assertEquals(0, underTest.size());
        assertFalse(underTest.contains(addedResource));
    }

    @Test
    public void noFailureOnRemoveInvalidResourceSet() {
        ResourceSet resources1 = createLabeledResources(1, 2, 3);

        underTest.removeResourceSet(resources1);
    }

    @Test
    public void removeResourcesDoesNotRemoveDuplicateResource() {
        ResourceSet resources = createLabeledResources(3, 4, 5);

        underTest.addResourceSet(createLabeledResources(1, 2, 3));
        underTest.addResourceSet(resources);
        underTest.removeResourceSet(resources);

        assertEquals(3, underTest.size());
        assertTrue(underTest.containsAll(createLabeledResources(1, 2, 3)));
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        underTest = new CombinedResourceSet(new DefaultResourceSet());
    }
}