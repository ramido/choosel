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
package org.thechiselgroup.choosel.core.client.test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.thechiselgroup.choosel.core.client.test.AdvancedAsserts.assertContentEquals;
import static org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory.createResource;
import static org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory.createResources;
import static org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory.toLabeledResourceSet;

import java.util.Collections;
import java.util.Set;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Assert;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.thechiselgroup.choosel.core.client.resources.Resource;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.resources.ResourceSetChangedEvent;
import org.thechiselgroup.choosel.core.client.resources.ResourceSetChangedEventHandler;
import org.thechiselgroup.choosel.core.client.util.collections.CollectionFactory;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightList;
import org.thechiselgroup.choosel.core.client.views.model.DefaultViewItem;
import org.thechiselgroup.choosel.core.client.views.model.SlotMappingConfiguration;
import org.thechiselgroup.choosel.core.client.views.model.ViewItem;
import org.thechiselgroup.choosel.core.client.views.model.ViewItemBehavior;

public final class ResourcesTestHelper {

    public static void assertContainsResource(boolean expected,
            ResourceSet resourceSet, String resourceType, int resourceId) {

        Assert.assertEquals(expected,
                resourceSet.contains(createResource(resourceType, resourceId)));
    }

    public static DefaultViewItem createViewItem(int id) {
        return createViewItem("" + id, createResources(id),
                mock(SlotMappingConfiguration.class));
    }

    public static DefaultViewItem createViewItem(String groupId,
            ResourceSet resources) {

        return createViewItem(groupId, resources,
                mock(SlotMappingConfiguration.class));
    }

    public static DefaultViewItem createViewItem(String groupId,
            ResourceSet resources,
            SlotMappingConfiguration slotMappingConfiguration) {

        return spy(new DefaultViewItem(groupId, resources,
                slotMappingConfiguration, mock(ViewItemBehavior.class)));
    }

    public static LightweightList<ViewItem> createViewItems(int... viewItemId) {

        ResourceSet[] resourceSets = new ResourceSet[viewItemId.length];
        for (int i = 0; i < resourceSets.length; i++) {
            resourceSets[i] = toLabeledResourceSet("" + viewItemId[i],
                    createResource(viewItemId[i]));
        }
        return createViewItems(resourceSets);
    }

    /**
     * Creates list of resource items with using the label of the resource sets
     * as group ids.
     */
    public static LightweightList<ViewItem> createViewItems(
            ResourceSet... resourceSets) {

        LightweightList<ViewItem> resourceItems = CollectionFactory
                .createLightweightList();
        for (ResourceSet resourceSet : resourceSets) {
            resourceItems.add(createViewItem(resourceSet.getLabel(),
                    resourceSet));
        }

        return resourceItems;
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

    public static LightweightCollection<ViewItem> eqResourceItems(
            final LightweightCollection<ViewItem> resourceItems) {

        return argThat(new ArgumentMatcher<LightweightCollection<ViewItem>>() {
            @Override
            public boolean matches(Object o) {
                LightweightCollection<ViewItem> set = (LightweightCollection<ViewItem>) o;

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

    public static LightweightCollection<ViewItem> resourceItemsForResourceSets(
            final ResourceSet... resourceSets) {

        return argThat(new ArgumentMatcher<LightweightCollection<ViewItem>>() {
            @Override
            public boolean matches(Object o) {
                LightweightCollection<ViewItem> set = (LightweightCollection<ViewItem>) o;

                if (set.size() != resourceSets.length) {
                    return false;
                }

                for (ResourceSet resourceSet : resourceSets) {
                    boolean found = false;
                    for (ViewItem item : set) {
                        ResourceSet itemSet = item.getResources();

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

    public static void verifyOnResourcesAdded(
            ResourceSet expectedAddedResources,
            ResourceSetChangedEventHandler handler) {

        ResourceSetChangedEvent event = verifyOnResourceSetChanged(1, handler)
                .getValue();
        assertContentEquals(expectedAddedResources, event.getAddedResources()
                .toList());
        assertEquals(true, event.getRemovedResources().isEmpty());
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

    public static void verifyOnResourcesRemoved(ResourceSet expectedResources,
            ResourceSetChangedEventHandler handler) {

        ResourceSetChangedEvent event = verifyOnResourceSetChanged(1, handler)
                .getValue();
        assertContentEquals(expectedResources, event.getRemovedResources()
                .toList());
        assertEquals(true, event.getAddedResources().isEmpty());
    }

    private ResourcesTestHelper() {
    }
}