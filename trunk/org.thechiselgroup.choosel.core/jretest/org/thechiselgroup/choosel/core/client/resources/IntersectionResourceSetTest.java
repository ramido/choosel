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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.thechiselgroup.choosel.core.client.resources.ResourcesTestHelper.verifyOnResourceSetChanged;
import static org.thechiselgroup.choosel.core.client.resources.ResourcesTestHelper.verifyOnResourcesAdded;
import static org.thechiselgroup.choosel.core.client.resources.TestResourceSetFactory.createLabeledResources;
import static org.thechiselgroup.choosel.core.client.resources.TestResourceSetFactory.createResource;
import static org.thechiselgroup.choosel.core.client.resources.TestResourceSetFactory.createResources;
import static org.thechiselgroup.choosel.core.shared.test.matchers.collections.CollectionMatchers.containsExactly;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class IntersectionResourceSetTest {

    @Mock
    private ResourceSetChangedEventHandler resourceSetChangedHandler;

    private IntersectionResourceSet underTest;

    @Test
    public void addMultipleResourcesToContainedResourceSet() {
        ResourceSet resources = createLabeledResources(1, 2, 3);

        underTest.addResourceSet(resources);

        resources.addAll(createResources(4, 5));

        assertTrue(underTest.contains(createResource(4)));
        assertTrue(underTest.contains(createResource(5)));
    }

    @Test
    public void addResourceSetCreatesIntersection() {
        underTest.addResourceSet(createLabeledResources(1, 2, 3));
        underTest.addResourceSet(createLabeledResources(3, 4, 5));

        assertThat(underTest, containsExactly(createResources(3)));
    }

    @Test
    public void containsResourcesAddedToChildren() {
        ResourceSet resources = createLabeledResources(1, 2, 3);

        underTest.addResourceSet(resources);
        resources.add(createResource(5));

        assertTrue(underTest.containsEqualResources(resources));
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
    public void doesRemoveDuplicateResourceOnRemoveInOneResourceSet() {
        ResourceSet resources = createLabeledResources(1, 2, 3);

        underTest.addResourceSet(resources);
        underTest.addResourceSet(createLabeledResources(3, 4, 5));
        resources.remove(createResource(3));

        assertThat(underTest, containsExactly(createResources()));
    }

    @Test
    public void doNotFireEventWhenResourceSetAddedButNoRemovedResources() {
        underTest.addResourceSet(createLabeledResources(1, 2, 3));
        underTest.addEventHandler(resourceSetChangedHandler);
        underTest.addResourceSet(createLabeledResources(1, 2, 3, 4));

        verifyOnResourceSetChanged(0, resourceSetChangedHandler);
    }

    @Test
    public void fireEventWhenResourceSetAdded() {
        underTest.addEventHandler(resourceSetChangedHandler);
        underTest.addResourceSet(createLabeledResources(1, 2, 3));

        verifyOnResourcesAdded(createResources(1, 2, 3),
                resourceSetChangedHandler);
    }

    @Test
    public void noContainmenChangeWhenResourceAddedAfterRemove() {
        Resource addedResource = createResource(8);
        ResourceSet resources = createLabeledResources(1, 2, 3);

        underTest.addResourceSet(resources);
        underTest.removeResourceSet(resources);
        resources.add(addedResource);

        assertThat(underTest, containsExactly(createResources()));
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

        assertThat(underTest, containsExactly(createResources(1, 2, 3)));
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        underTest = new IntersectionResourceSet(new DefaultResourceSet());
    }
}
