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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.thechiselgroup.choosel.client.test.ResourcesTestHelper.createLabeledResources;
import static org.thechiselgroup.choosel.client.test.ResourcesTestHelper.createResource;
import static org.thechiselgroup.choosel.client.test.ResourcesTestHelper.createResources;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CombinedResourceSetTest {

    @Mock
    private ResourcesAddedEventHandler addedHandler;

    private CombinedResourceSet combinedResources;

    private DefaultResourceSet resources1;

    private DefaultResourceSet resources2;

    @Test
    public void addMultipleResourcesToContainedResourceSet() {
        combinedResources.addResourceSet(resources1);

        resources1.addAll(createResources(4, 5));

        assertEquals(true, combinedResources.contains(createResource(4)));
        assertEquals(true, combinedResources.contains(createResource(5)));
    }

    @Test
    public void addResourcesAddsToAllResources() {
        combinedResources.addResourceSet(resources1);
        combinedResources.addResourceSet(resources2);

        assertEquals(5, combinedResources.size());
        assertTrue(combinedResources.containsAll(resources1));
        assertTrue(combinedResources.containsAll(resources2));
    }

    @Test
    public void containsResourcesAddedToChildren() {
        Resource addedResource = createResource(5);

        combinedResources.addResourceSet(resources1);
        resources1.add(addedResource);

        assertEquals(4, combinedResources.size());
        assertTrue(combinedResources.containsAll(resources1));
    }

    @Test
    public void doesNotContainResourcesRemovedFromChildren() {
        Resource removedResource = createResource(1);

        combinedResources.addResourceSet(resources1);
        resources1.remove(removedResource);

        assertEquals(2, combinedResources.size());
        assertTrue(combinedResources.containsAll(resources1));
        assertFalse(combinedResources.contains(removedResource));
    }

    @Test
    public void doesNotRemoveDuplicateResourceOnRemove() {
        Resource removedResource = createResource(3);

        combinedResources.addResourceSet(resources1);
        combinedResources.addResourceSet(resources2);
        resources1.remove(removedResource);

        assertEquals(5, combinedResources.size());
        assertTrue(combinedResources.containsAll(resources1));
        assertTrue(combinedResources.containsAll(resources2));
    }

    @Test
    public void fireAddEventsWhenResourceSetAdded() {
        combinedResources.addHandler(ResourcesAddedEvent.TYPE, addedHandler);
        combinedResources.addResourceSet(resources1);

        ArgumentCaptor<ResourcesAddedEvent> argument = ArgumentCaptor
                .forClass(ResourcesAddedEvent.class);

        verify(addedHandler, times(1)).onResourcesAdded(argument.capture());

        // TODO extract list comparison
        List<Resource> eventResources = argument.getValue().getAddedResources();
        assertEquals(3, eventResources.size());
    }

    @Test
    public void noContainmenChangeWhenResourceAddedAfterRemove() {
        Resource addedResource = createResource(8);

        combinedResources.addResourceSet(resources1);
        combinedResources.removeResourceSet(resources1);
        resources1.add(addedResource);

        assertEquals(0, combinedResources.size());
        assertFalse(combinedResources.contains(addedResource));
    }

    @Test
    public void noFailureOnRemoveInvalidResourceSet() {
        combinedResources.removeResourceSet(resources1);
    }

    @Test
    public void removeResourcesDoesNotRemoveDuplicateResource() {
        combinedResources.addResourceSet(resources1);
        combinedResources.addResourceSet(resources2);
        combinedResources.removeResourceSet(resources2);

        assertEquals(3, combinedResources.size());
        assertTrue(combinedResources.containsAll(resources1));
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        combinedResources = new CombinedResourceSet(new DefaultResourceSet());

        resources1 = createLabeledResources(1, 2, 3);
        resources2 = createLabeledResources(3, 4, 5);
    }
}
