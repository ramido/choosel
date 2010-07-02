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

public class DefaultResourceSetTest {

    private DefaultResourceSet resources;

    @Mock
    private ResourcesAddedEventHandler resourcesAddedHandler;

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
    public void fireResourcesAddedEvent() {
        ResourceSet resourceSet = createResources(1, 2, 3);
        resources.addHandler(ResourcesAddedEvent.TYPE, resourcesAddedHandler);
        resources.addAll(resourceSet);

        verify(resourcesAddedHandler, times(1)).onResourcesAdded(
                any(ResourcesAddedEvent.class));
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
    public void resourcesAddedEventOnlyAddsResourcesNotContained() {
        Resource containedResource = createResource(1);
        ResourceSet resourceSet = createResources(1, 2, 3);
        resourceSet.add(containedResource);

        resources.add(containedResource);
        resources.addHandler(ResourcesAddedEvent.TYPE, resourcesAddedHandler);
        resources.addAll(resourceSet);

        ArgumentCaptor<ResourcesAddedEvent> argument = ArgumentCaptor
                .forClass(ResourcesAddedEvent.class);

        verify(resourcesAddedHandler, times(1)).onResourcesAdded(
                argument.capture());

        List<Resource> eventResources = argument.getValue().getChangedResources();
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
