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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory.createResource;
import static org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory.createResources;
import static org.thechiselgroup.choosel.core.client.util.collections.CollectionUtils.toList;

import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ResourceSetEventForwarderTest {

    @Mock
    private ResourceContainer target;

    @Test
    public void addAllResourcesOnEventFired() {
        ResourceSet source = new DefaultResourceSet();
        ResourceSetEventForwarder underTest = new ResourceSetEventForwarder(
                source, target);

        underTest.init();

        source.addAll(createResources(1, 2));

        ArgumentCaptor<Collection> argument = ArgumentCaptor
                .forClass(Collection.class);

        verify(target, times(1)).addAll(argument.capture());

        List<Resource> result = toList(argument.getValue());

        assertEquals(2, result.size());
        assertEquals(true, result.contains(createResource(1)));
        assertEquals(true, result.contains(createResource(2)));
    }

    // TODO check again
    @Test
    public void removeAllResourcesOnEventFired() {
        ResourceSet source = createResources(1, 2, 3);

        ResourceSetEventForwarder underTest = new ResourceSetEventForwarder(
                source, target);

        underTest.init();

        source.removeAll(createResources(1, 2));

        ArgumentCaptor<Collection> argument = ArgumentCaptor
                .forClass(Collection.class);

        verify(target, times(1)).removeAll(argument.capture());

        List<Resource> result = toList(argument.getValue());

        assertEquals(2, result.size());
        assertEquals(true, result.contains(createResource(1)));
        assertEquals(true, result.contains(createResource(2)));
        assertEquals(false, result.contains(createResource(3)));
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

    }

}
