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
package org.thechiselgroup.choosel.client.model.resources;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.thechiselgroup.choosel.client.test.ResourcesTestHelper.createResource;
import static org.thechiselgroup.choosel.client.test.ResourcesTestHelper.createResources;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.client.resources.DefaultResourceSet;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ResourcesAddedEvent;
import org.thechiselgroup.choosel.client.resources.ResourcesAddedEventHandler;
import org.thechiselgroup.choosel.client.resources.ResourcesRemovedEvent;
import org.thechiselgroup.choosel.client.resources.ResourcesRemovedEventHandler;

public class DefaultResourceSetTest {

    private DefaultResourceSet resources;

    private DefaultResourceSet resources2 = createResources(1, 2, 3);

    @Mock
    private ResourcesAddedEventHandler resourcesAddedHandler;

    @Mock
    private ResourcesRemovedEventHandler resourcesRemovedHandler;

    @Test
    public void addResourcesToDefaultResourceSet() {
        resources.addAll(createResources(1, 2, 3));

        assertEquals(3, resources.size());
        assertEquals(true, resources.contains(createResource(1)));
        assertEquals(true, resources.contains(createResource(2)));
        assertEquals(true, resources.contains(createResource(3)));
    }

    @Test
    public void addResourceToDefaultResourceSet() {
        resources.add(createResource(1));

        assertEquals(1, resources.size());
        assertEquals(true, resources.contains(createResource(1)));
    }

    @Test
    public void fireResourceAddedEvent() {
        resources.addHandler(ResourcesAddedEvent.TYPE, resourcesAddedHandler);
        resources.add(createResource(1));

        verify(resourcesAddedHandler, times(1)).onResourcesAdded(
                any(ResourcesAddedEvent.class));
    }

    @Test
    public void fireResourceRemovedEvent() {
        resources2.addHandler(ResourcesRemovedEvent.TYPE,
                resourcesRemovedHandler);
        resources2.remove(createResource(1));

        verify(resourcesRemovedHandler, times(1)).onResourcesRemoved(
                any(ResourcesRemovedEvent.class));
    }

    @Test
    public void fireResourcesAddedEvent() {
        resources.addHandler(ResourcesAddedEvent.TYPE, resourcesAddedHandler);
        resources.addAll(createResources(1, 2, 3));

        verify(resourcesAddedHandler, times(1)).onResourcesAdded(
                any(ResourcesAddedEvent.class));
    }

    @Test
    public void fireResourcesRemovedEvent() {
        resources2.addHandler(ResourcesRemovedEvent.TYPE,
                resourcesRemovedHandler);
        resources2.removeAll(createResources(1, 2, 3));

        verify(resourcesRemovedHandler, times(1)).onResourcesRemoved(
                any(ResourcesRemovedEvent.class));
    }

    @Test
    public void hasLabelIsFalseWhenLabelNull() {
        resources.setLabel(null);

        assertEquals(false, resources.hasLabel());
    }

    @Test
    public void hasLabelIsTrueWhenLabelText() {
        resources.setLabel("some text");

        assertEquals(true, resources.hasLabel());
    }

    @Test
    public void removeResourceFromDefaultResourceSet() {
        resources2.remove(createResource(1));

        assertEquals(2, resources2.size());
        assertEquals(false, resources2.contains(createResource(1)));
        assertEquals(true, resources2.contains(createResource(2)));
        assertEquals(true, resources2.contains(createResource(3)));
    }

    @Test
    public void removeResourcesFromDefaultResourceSet() {
        resources2.removeAll(createResources(1, 2, 3));

        assertEquals(0, resources2.size());
        assertEquals(false, resources2.contains(createResource(1)));
        assertEquals(false, resources2.contains(createResource(2)));
        assertEquals(false, resources2.contains(createResource(3)));
    }

    @Test
    public void resourcesAddedEventOnlyAddsResourcesNotContained() {
        Resource containedResource = createResource(1);
        ResourceSet resourceSet = createResources(1, 2, 3);

        resources.add(containedResource);
        resources.addHandler(ResourcesAddedEvent.TYPE, resourcesAddedHandler);
        resources.addAll(resourceSet);

        ArgumentCaptor<ResourcesAddedEvent> argument = ArgumentCaptor
                .forClass(ResourcesAddedEvent.class);

        verify(resourcesAddedHandler, times(1)).onResourcesAdded(
                argument.capture());

        List<Resource> eventResources = argument.getValue()
                .getChangedResources();
        assertEquals(2, eventResources.size());
        assertEquals(false, eventResources.contains(containedResource));
        assertEquals(true, eventResources.contains(createResource(2)));
        assertEquals(true, eventResources.contains(createResource(3)));
    }

    @Test
    public void resourcesRemovedEventOnlyRemovesResourcesContained() {
        Resource containedResource = createResource(1);
        ResourceSet resourceSet = createResources(1, 2, 3);

        resources2.remove(containedResource);
        resources2.addHandler(ResourcesRemovedEvent.TYPE,
                resourcesRemovedHandler);
        resources2.removeAll(resourceSet);

        ArgumentCaptor<ResourcesRemovedEvent> argument = ArgumentCaptor
                .forClass(ResourcesRemovedEvent.class);

        verify(resourcesRemovedHandler, times(1)).onResourcesRemoved(
                argument.capture());

        List<Resource> eventResources = argument.getValue()
                .getChangedResources();
        assertEquals(2, eventResources.size());
        assertEquals(false, eventResources.contains(containedResource));
        assertEquals(true, eventResources.contains(createResource(2)));
        assertEquals(true, eventResources.contains(createResource(3)));
    }

    @Test
    public void returnEmptyStringIfLabelNull() {
        resources.setLabel(null);

        assertEquals("", resources.getLabel());
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        resources = new DefaultResourceSet();
    }
}
