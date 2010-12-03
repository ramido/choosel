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
import static org.thechiselgroup.choosel.client.test.ResourcesTestHelper.verifyOnResourcesAdded;
import static org.thechiselgroup.choosel.client.test.ResourcesTestHelper.verifyOnResourcesRemoved;
import static org.thechiselgroup.choosel.client.test.TestResourceSetFactory.createResource;
import static org.thechiselgroup.choosel.client.test.TestResourceSetFactory.createResources;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class DefaultResourceSetTest {

    private ResourceSet underTest;

    @Mock
    private ResourcesAddedEventHandler addedHandler;

    @Mock
    private ResourcesRemovedEventHandler removedHandler;

    @Test
    public void addAllFiresResourcesAddedEvent() {
        underTest.addEventHandler(addedHandler);
        underTest.addAll(createResources(1, 2, 3));

        verifyOnResourcesAdded(1, addedHandler);
    }

    @Test
    public void addAllWithoutChangesDoesNotFireResourcesAddedEvent() {
        underTest.addAll(createResources(1, 2, 3));
        underTest.addEventHandler(addedHandler);
        underTest.addAll(createResources(1, 2, 3));

        verifyOnResourcesAdded(0, addedHandler);
    }

    @Test
    public void addFiresResourcesAddedEvent() {
        underTest.addEventHandler(addedHandler);
        underTest.add(createResource(1));

        verifyOnResourcesAdded(1, addedHandler);
    }

    @Test
    public void addResource() {
        underTest.add(createResource(1));

        assertEquals(1, underTest.size());
        assertEquals(true, underTest.contains(createResource(1)));
    }

    @Test
    public void addResources() {
        underTest.addAll(createResources(1, 2, 3));

        assertEquals(3, underTest.size());
        assertEquals(true, underTest.contains(createResource(1)));
        assertEquals(true, underTest.contains(createResource(2)));
        assertEquals(true, underTest.contains(createResource(3)));
    }

    @Test
    public void hasLabelIsFalseWhenLabelNull() {
        underTest.setLabel(null);

        assertEquals(false, underTest.hasLabel());
    }

    @Test
    public void hasLabelIsTrueWhenLabelText() {
        underTest.setLabel("some text");

        assertEquals(true, underTest.hasLabel());
    }

    @Test
    public void removeAllFiresResourcesRemovedEvent() {
        underTest.addAll(createResources(1, 2, 3));
        underTest.addEventHandler(removedHandler);
        underTest.removeAll(createResources(1, 2, 3));

        verifyOnResourcesRemoved(1, removedHandler);
    }

    @Test
    public void removeAllWithoutChangesDoesNotFireResourcesRemovedEvent() {
        underTest.addEventHandler(removedHandler);
        underTest.removeAll(createResources(1, 2, 3));

        verifyOnResourcesRemoved(0, removedHandler);
    }

    @Test
    public void removeFiresResourcesRemovedEvent() {
        underTest.addAll(createResources(1, 2, 3));
        underTest.addEventHandler(removedHandler);
        underTest.remove(createResource(1));

        verifyOnResourcesRemoved(1, removedHandler);
    }

    @Test
    public void removeResource() {
        underTest.addAll(createResources(1, 2, 3));
        underTest.remove(createResource(1));

        assertEquals(2, underTest.size());
        assertEquals(false, underTest.contains(createResource(1)));
        assertEquals(true, underTest.contains(createResource(2)));
        assertEquals(true, underTest.contains(createResource(3)));
    }

    @Test
    public void removeResources() {
        underTest.addAll(createResources(1, 2, 3));
        underTest.removeAll(createResources(1, 2, 3));

        assertEquals(0, underTest.size());
        assertEquals(false, underTest.contains(createResource(1)));
        assertEquals(false, underTest.contains(createResource(2)));
        assertEquals(false, underTest.contains(createResource(3)));
    }

    @Test
    public void resourcesAddedEventOnlyContainAddedResources() {
        underTest.add(createResource(1));
        underTest.addEventHandler(addedHandler);
        underTest.addAll(createResources(1, 2, 3));

        List<Resource> addedResources = verifyOnResourcesAdded(1, addedHandler)
                .getValue().getAddedResources().toList();

        assertEquals(2, addedResources.size());
        assertEquals(false, addedResources.contains(createResource(1)));
        assertEquals(true, addedResources.contains(createResource(2)));
        assertEquals(true, addedResources.contains(createResource(3)));
    }

    @Test
    public void resourcesRemovedEventOnlyContainsRemovedResources() {
        underTest.addAll(createResources(2, 3));
        underTest.addEventHandler(removedHandler);
        underTest.removeAll(createResources(1, 2, 3));

        List<Resource> removedResources = verifyOnResourcesRemoved(1,
                removedHandler).getValue().getRemovedResources().toList();

        assertEquals(2, removedResources.size());
        assertEquals(false, removedResources.contains(createResource(1)));
        assertEquals(true, removedResources.contains(createResource(2)));
        assertEquals(true, removedResources.contains(createResource(3)));
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
                removedHandler).getValue().getRemovedResources().toList();

        assertEquals(2, removedResources.size());
        assertEquals(false, removedResources.contains(createResource(1)));
        assertEquals(false, removedResources.contains(createResource(2)));
        assertEquals(true, removedResources.contains(createResource(3)));
        assertEquals(true, removedResources.contains(createResource(4)));
    }

    @Test
    public void retainAllWithoutChangesDoesNotFireResourcesRemovedEvent() {
        underTest.addAll(createResources(1, 2));
        underTest.addEventHandler(removedHandler);
        underTest.retainAll(createResources(1, 2, 3));

        verifyOnResourcesRemoved(0, removedHandler);
    }

    @Test
    public void returnEmptyStringIfLabelNull() {
        underTest.setLabel(null);

        assertEquals("", underTest.getLabel());
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        underTest = new DefaultResourceSet();
    }
}
