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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ResourceSetContainer;
import org.thechiselgroup.choosel.client.resources.ResourceSetContainerChangedEvent;
import org.thechiselgroup.choosel.client.resources.ResourceSetContainerChangedEventHandler;

public class ResourceSetContainerTest {

    private ResourceSetContainer undertest;

    @Mock
    private ResourceSet resources;

    @Mock
    private ResourceSetContainerChangedEventHandler handler;

    @Test
    public void doNotFireIfNoChangeA() {
        undertest.setResourceSet(resources);
        undertest.setResourceSet(resources);

        ArgumentCaptor<ResourceSetContainerChangedEvent> argument = ArgumentCaptor
                .forClass(ResourceSetContainerChangedEvent.class);
        verify(handler, times(1)).onResourceSetContainerChanged(
                argument.capture());

        assertEquals(resources, argument.getValue().getResourceSet());
    }

    @Test
    public void doNotFireIfNoChangeB() {
        undertest.setResourceSet(null);

        verify(handler, never()).onResourceSetContainerChanged(
                any(ResourceSetContainerChangedEvent.class));
    }

    @Test
    public void fireResourceSetChangedEventOnSettingResourceSet() {
        undertest.setResourceSet(resources);

        ArgumentCaptor<ResourceSetContainerChangedEvent> argument = ArgumentCaptor
                .forClass(ResourceSetContainerChangedEvent.class);
        verify(handler, times(1)).onResourceSetContainerChanged(
                argument.capture());

        assertEquals(resources, argument.getValue().getResourceSet());
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        undertest = new ResourceSetContainer();

        undertest.addResourceSetContainerChangedEventHandler(handler);
    }
}
