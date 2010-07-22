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
package org.thechiselgroup.choosel.client.test;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.thechiselgroup.choosel.client.test.TestResourceSetFactory.createResource;

import org.junit.Assert;
import org.mockito.ArgumentCaptor;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ResourcesAddedEvent;
import org.thechiselgroup.choosel.client.resources.ResourcesAddedEventHandler;
import org.thechiselgroup.choosel.client.resources.ResourcesRemovedEvent;
import org.thechiselgroup.choosel.client.resources.ResourcesRemovedEventHandler;

public final class ResourcesTestHelper {

    public static void assertContainsResource(boolean expected,
            ResourceSet resourceSet, String resourceType, int resourceId) {

        Assert.assertEquals(expected, resourceSet.contains(createResource(
                resourceType, resourceId)));
    }

    public static ArgumentCaptor<ResourcesAddedEvent> verifyOnResourcesAdded(
            int expectedInvocationCount,
            ResourcesAddedEventHandler resourcesAddedHandler) {

        ArgumentCaptor<ResourcesAddedEvent> argument = ArgumentCaptor
                .forClass(ResourcesAddedEvent.class);

        verify(resourcesAddedHandler, times(expectedInvocationCount))
                .onResourcesAdded(argument.capture());

        return argument;
    }

    public static ArgumentCaptor<ResourcesRemovedEvent> verifyOnResourcesRemoved(
            int expectedInvocationCount,
            ResourcesRemovedEventHandler resourcesRemovedHandler) {

        ArgumentCaptor<ResourcesRemovedEvent> argument = ArgumentCaptor
                .forClass(ResourcesRemovedEvent.class);

        verify(resourcesRemovedHandler, times(expectedInvocationCount))
                .onResourcesRemoved(argument.capture());

        return argument;
    }

    private ResourcesTestHelper() {
    }
}