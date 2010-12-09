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

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.thechiselgroup.choosel.client.test.AdvancedAsserts.assertContentEquals;
import static org.thechiselgroup.choosel.client.test.TestResourceSetFactory.createResource;

import java.util.Collections;
import java.util.Set;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Assert;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ResourceSetChangedEvent;
import org.thechiselgroup.choosel.client.resources.ResourceSetChangedEventHandler;
import org.thechiselgroup.choosel.client.ui.popup.PopupManager;
import org.thechiselgroup.choosel.client.util.collections.LightweightCollection;
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
        return createResourceItem(resources,
                mock(SlotMappingConfiguration.class));
    }

    public static DefaultResourceItem createResourceItem(ResourceSet resources,
            SlotMappingConfiguration slotMappingConfiguration) {

        return spy(new DefaultResourceItem("", resources,
                mock(HoverModel.class), mock(PopupManager.class),
                slotMappingConfiguration));
    }

    public static <T> LightweightCollection<T> emptyLightweightCollection(
            final Class<T> clazz) {

        return argThat(new BaseMatcher<LightweightCollection<T>>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Empty LightweightCollection Matcher");
            }

            @Override
            public boolean matches(Object item) {
                if (item == null) {
                    return false;
                }

                if (!(item instanceof LightweightCollection)) {
                    return false;
                }

                return ((LightweightCollection) item).isEmpty();
            }
        });
    }

    public static <T> Set<T> emptySet(Class<T> clazz) {
        return eq(Collections.<T> emptySet());
    }

    public static LightweightCollection<ResourceItem> eqResourceItems(
            final LightweightCollection<ResourceItem> resourceItems) {

        return argThat(new ArgumentMatcher<LightweightCollection<ResourceItem>>() {
            @Override
            public boolean matches(Object o) {
                LightweightCollection<ResourceItem> set = (LightweightCollection<ResourceItem>) o;

                if (set.size() != resourceItems.size()) {
                    return false;
                }

                return set.toList().containsAll(resourceItems.toList());
            }
        });
    }

    public static LightweightCollection<Resource> eqResources(
            final LightweightCollection<Resource> resources) {

        return argThat(new ArgumentMatcher<LightweightCollection<Resource>>() {
            @Override
            public boolean matches(Object o) {
                LightweightCollection<Resource> set = (LightweightCollection<Resource>) o;

                if (set.size() != resources.size()) {
                    return false;
                }

                return set.toList().containsAll(resources.toList());
            }
        });
    }

    public static LightweightCollection<ResourceItem> resourceItemsForResourceSets(
            final ResourceSet... resourceSets) {

        return argThat(new ArgumentMatcher<LightweightCollection<ResourceItem>>() {
            @Override
            public boolean matches(Object o) {
                LightweightCollection<ResourceItem> set = (LightweightCollection<ResourceItem>) o;

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

    public static ArgumentCaptor<ResourceSetChangedEvent> verifyOnResourceSetChanged(
            int expectedInvocationCount,
            ResourceSetChangedEventHandler resourcesChangedHandler) {

        ArgumentCaptor<ResourceSetChangedEvent> argument = ArgumentCaptor
                .forClass(ResourceSetChangedEvent.class);

        verify(resourcesChangedHandler, times(expectedInvocationCount))
                .onResourceSetChanged(argument.capture());

        return argument;
    }

    public static void verifyOnResourcesAdded(ResourceSet expectedAddedResources,
            ResourceSetChangedEventHandler handler) {

        ResourceSetChangedEvent event = verifyOnResourceSetChanged(1, handler)
                .getValue();
        assertContentEquals(expectedAddedResources, event.getAddedResources()
                .toList());
        assertEquals(true, event.getRemovedResources().isEmpty());
    }

    private ResourcesTestHelper() {
    }

    public static void verifyOnResourcesRemoved(ResourceSet expectedResources,
            ResourceSetChangedEventHandler handler) {
    
        ResourceSetChangedEvent event = verifyOnResourceSetChanged(1, handler)
                .getValue();
        assertContentEquals(expectedResources, event.getRemovedResources()
                .toList());
        assertEquals(true, event.getAddedResources().isEmpty());
    }
}