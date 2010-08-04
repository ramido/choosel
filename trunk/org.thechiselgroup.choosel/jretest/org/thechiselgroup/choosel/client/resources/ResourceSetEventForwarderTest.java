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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.thechiselgroup.choosel.client.test.TestResourceSetFactory.createResource;
import static org.thechiselgroup.choosel.client.test.TestResourceSetFactory.createResources;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.client.util.CollectionUtils;

public class ResourceSetEventForwarderTest {

    private ResourceSet source;

    private ResourceSet source2 = createResources(1, 2, 3);

    @Mock
    private ResourceContainer target;

    @Test
    public void addAllResourcesOnEventFired() {
        ResourceSetEventForwarder underTest = new ResourceSetEventForwarder(source,
                target);

        underTest.init();

        source.addAll(createResources(1, 2));

        ArgumentCaptor<Iterable> argument = ArgumentCaptor
                .forClass(Iterable.class);

        verify(target, times(1)).addAll(argument.capture());

        List<Resource> result = CollectionUtils.toList(argument.getValue());

        assertEquals(2, result.size());
        assertEquals(true, result.contains(createResource(1)));
        assertEquals(true, result.contains(createResource(2)));
    }

    // TODO check again
    @Test
    public void removeAllResourcesOnEventFired() {
        ResourceSetEventForwarder underTest = new ResourceSetEventForwarder(
                source2, target);

        underTest.init();

        source2.removeAll(createResources(1, 2));

        ArgumentCaptor<Iterable> argument = ArgumentCaptor
                .forClass(Iterable.class);

        verify(target, times(1)).removeAll(argument.capture());

        List<Resource> result = CollectionUtils.toList(argument.getValue());

        assertEquals(2, result.size());
        assertEquals(true, result.contains(createResource(1)));
        assertEquals(true, result.contains(createResource(2)));
        assertEquals(false, result.contains(createResource(3)));
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        source = new DefaultResourceSet();
    }

}
