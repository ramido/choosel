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

import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.thechiselgroup.choosel.client.test.TestResourceSetFactory.createResource;

import java.util.Collections;
import java.util.Set;

import org.junit.Assert;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ResourcesAddedEvent;
import org.thechiselgroup.choosel.client.resources.ResourcesAddedEventHandler;
import org.thechiselgroup.choosel.client.resources.ResourcesRemovedEvent;
import org.thechiselgroup.choosel.client.resources.ResourcesRemovedEventHandler;
import org.thechiselgroup.choosel.client.ui.popup.PopupManager;
import org.thechiselgroup.choosel.client.views.DefaultResourceItem;
import org.thechiselgroup.choosel.client.views.HoverModel;
import org.thechiselgroup.choosel.client.views.ResourceItem;
import org.thechiselgroup.choosel.client.views.SlotMappingConfiguration;

public final class ResourcesTestHelper {

    public static void assertContainsResource(boolean expected,
            ResourceSet resourceSet, String resourceType, int resourceId) {

        Assert.assertEquals(expected,
                resourceSet.contains(createResource(resourceType, resourceId)));
    }

    public static DefaultResourceItem createResourceItem(ResourceSet resources) {
        return spy(new DefaultResourceItem("", resources,
                mock(HoverModel.class), mock(PopupManager.class),
                mock(SlotMappingConfiguration.class)));
    }

    public static <T> Set<T> emptySet(Class<T> clazz) {
        return eq(Collections.<T> emptySet());
    }

    public static Set<ResourceItem> eqResourceItems(
            final Set<ResourceItem> resourceItems) {

        return argThat(new ArgumentMatcher<Set<ResourceItem>>() {
            @Override
            public boolean matches(Object o) {
                Set<ResourceItem> set = (Set<ResourceItem>) o;

                if (set.size() != resourceItems.size()) {
                    return false;
                }

                return set.containsAll(resourceItems);
            }
        });
    }

    public static Set<ResourceItem> resourceItemsForResourceSets(
            final ResourceSet... resourceSets) {

        return argThat(new ArgumentMatcher<Set<ResourceItem>>() {
            @Override
            public boolean matches(Object o) {
                Set<ResourceItem> set = (Set<ResourceItem>) o;

                if (set.size() != resourceSets.length) {
                    return false;
                }

                for (ResourceSet resourceSet : resourceSets) {
                    boolean found = false;
                    for (ResourceItem item : set) {
                        ResourceSet itemSet = item.getResourceSet();

                        if (itemSet.size() == resourceSet.size()
                                && itemSet.containsAll(resourceSet)) {
                            found = true;
                        }
                    }

                    if (!found) {
                        return false;
                    }
                }

                return true;
            }
        });
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