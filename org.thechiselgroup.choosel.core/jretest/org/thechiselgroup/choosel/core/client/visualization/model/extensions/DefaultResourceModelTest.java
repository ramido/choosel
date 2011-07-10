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
package org.thechiselgroup.choosel.core.client.visualization.model.extensions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.thechiselgroup.choosel.core.client.test.AdvancedAsserts.assertContains;
import static org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory.createLabeledResources;
import static org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory.createResource;
import static org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory.createResources;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.core.client.persistence.Memento;
import org.thechiselgroup.choosel.core.client.persistence.PersistableRestorationService;
import org.thechiselgroup.choosel.core.client.resources.DefaultResourceSet;
import org.thechiselgroup.choosel.core.client.resources.DefaultResourceSetFactory;
import org.thechiselgroup.choosel.core.client.resources.Resource;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.resources.ResourceSetChangedEvent;
import org.thechiselgroup.choosel.core.client.resources.persistence.ResourceSetAccessor;
import org.thechiselgroup.choosel.core.client.visualization.model.extensions.DefaultResourceModel;

public class DefaultResourceModelTest {

    private DefaultResourceModel underTest;

    @Mock
    private PersistableRestorationService restorationManager;

    @Test
    public void addResourcesAddsToAllResources() {
        ResourceSet resources1 = createResources("test", 1, 2, 3);
        ResourceSet resources2 = createResources("test", 3, 4, 5);

        underTest.addUnnamedResources(resources1);
        underTest.addUnnamedResources(resources2);
        ResourceSet allResources = underTest.getResources();

        assertEquals(5, allResources.size());
        for (Resource resource : resources1) {
            assertTrue(allResources.contains(resource));
        }
        for (Resource resource : resources2) {
            assertTrue(allResources.contains(resource));
        }
    }

    @Test
    public void addResourceSetsAddsToAllResources() {
        ResourceSet resources1 = createResources("test", 1, 2, 3);
        ResourceSet resources2 = createResources("test", 3, 4, 5);

        underTest.addResourceSet(resources1);
        underTest.addResourceSet(resources2);
        ResourceSet allResources = underTest.getResources();

        assertEquals(5, allResources.size());
        for (Resource resource : resources1) {
            assertTrue(allResources.contains(resource));
        }
        for (Resource resource : resources2) {
            assertTrue(allResources.contains(resource));
        }
    }

    @Test
    public void addUnlabeledSetContentsToAutomaticResourceSet() {
        ResourceSet resources = createResources(1);
        assertEquals(false, resources.hasLabel());

        underTest.addResourceSet(resources);

        assertContains(underTest.getAutomaticResourceSet(), createResource(1));
    }

    @Test
    public void containsAddedLabeledResources() {
        ResourceSet resources = createLabeledResources(1, 2, 3);

        underTest.addResourceSet(resources);

        assertEquals(true, underTest.containsResourceSet(resources));
    }

    @Test
    public void containsAddedResources() {
        ResourceSet resources = createResources(1, 2, 3);

        underTest.addUnnamedResources(resources);

        assertEquals(true, underTest.containsResources(resources));
    }

    @Test
    public void disposeShouldRemoveResourceHooks() {
        DefaultResourceSet resources = new DefaultResourceSet();
        resources.add(createResource(3));
        resources.add(createResource(4));

        underTest.addResourceSet(resources);
        underTest.dispose();

        assertEquals(0, resources.getHandlerCount(ResourceSetChangedEvent.TYPE));
    }

    @Test
    public void doesNotContainResourceSetAfterAddingResources() {
        ResourceSet resources = createResources(1, 2, 3);
        resources.setLabel("test");

        underTest.addUnnamedResources(resources);

        assertEquals(false, underTest.containsResourceSet(resources));
    }

    @Test
    public void getResourcesHasLabel() {
        assertEquals(true, underTest.getResources().hasLabel());
    }

    @Test
    public void removeLabeledResourceSetDoesNotRemoveDuplicateResources() {
        ResourceSet resources1 = createLabeledResources(1, 2, 3);
        ResourceSet resources2 = createLabeledResources(3, 4, 5);

        underTest.addResourceSet(resources1);
        underTest.addResourceSet(resources2);
        underTest.removeResourceSet(resources2);
        ResourceSet allResources = underTest.getResources();

        assertEquals(3, allResources.size());
        for (Resource resource : resources1) {
            assertTrue(allResources.contains(resource));
        }
    }

    @Test
    public void removeResourcesDoesRemoveDuplicateResources() {
        ResourceSet resources1 = createResources(1, 2, 3);
        ResourceSet resources2 = createResources(3, 4, 5);

        underTest.addUnnamedResources(resources1);
        underTest.addUnnamedResources(resources2);
        underTest.removeUnnamedResources(resources2);
        ResourceSet allResources = underTest.getResources();

        assertEquals(2, allResources.size());
        assertEquals(true, allResources.contains(createResource(1)));
        assertEquals(true, allResources.contains(createResource(2)));
        assertEquals(false, allResources.contains(createResource(3)));
        assertEquals(false, allResources.contains(createResource(4)));
        assertEquals(false, allResources.contains(createResource(5)));
    }

    @Test
    public void restoreFromMementoAddsAutomaticResourcesToAllResources() {
        Memento state = new Memento();

        state.setValue(DefaultResourceModel.MEMENTO_AUTOMATIC_RESOURCES, 0);
        state.setValue(DefaultResourceModel.MEMENTO_RESOURCE_SET_COUNT, 0);

        ResourceSetAccessor accessor = mock(ResourceSetAccessor.class);
        when(accessor.getResourceSet(0)).thenReturn(createResources(1));
        when(accessor.getResourceSet(1)).thenReturn(createResources());

        underTest.restore(state, restorationManager, accessor);

        assertEquals(true, underTest.getResources().contains(createResource(1)));
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        underTest = new DefaultResourceModel(new DefaultResourceSetFactory());
    }

}
