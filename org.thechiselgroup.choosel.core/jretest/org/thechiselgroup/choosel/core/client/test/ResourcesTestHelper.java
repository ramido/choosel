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
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.thechiselgroup.choosel.core.client.test.AdvancedAsserts.assertContentEquals;
import static org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory.createResource;
import static org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory.createResources;
import static org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory.toLabeledResourceSet;

import java.util.concurrent.atomic.AtomicReference;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.thechiselgroup.choosel.core.client.resources.Resource;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.resources.ResourceSetChangedEvent;
import org.thechiselgroup.choosel.core.client.resources.ResourceSetChangedEventHandler;
import org.thechiselgroup.choosel.core.client.util.collections.CollectionFactory;
import org.thechiselgroup.choosel.core.client.util.collections.Delta;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollections;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightList;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItem;

public final class ResourcesTestHelper {

    public static void assertContainsResource(boolean expected,
            ResourceSet resourceSet, String resourceType, int resourceId) {

        Assert.assertEquals(expected,
                resourceSet.contains(createResource(resourceType, resourceId)));
    }

    public static Matcher<LightweightCollection<VisualItem>> containsViewItemsForExactResourceSets(
            final ResourceSet... resourceSets) {

        return new TypeSafeMatcher<LightweightCollection<VisualItem>>() {
            @Override
            public void describeTo(Description description) {
                for (ResourceSet resourceSet : resourceSets) {
                    description.appendValue(resourceSet);
                }
            }

            @Override
            public boolean matchesSafely(LightweightCollection<VisualItem> set) {
                if (set.size() != resourceSets.length) {
                    return false;
                }

                for (ResourceSet resourceSet : resourceSets) {
                    boolean found = false;
                    for (VisualItem item : set) {
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
        };
    }

    public static VisualItem createViewItem(int id) {
        return createViewItem("" + id, createResources(id));
    }

    public static VisualItem createViewItem(String viewItemId,
            ResourceSet resources) {

        final AtomicReference<Object> displayObjectBuffer = new AtomicReference<Object>();

        VisualItem viewItem = mock(VisualItem.class);

        when(viewItem.getResources()).thenReturn(resources);
        when(viewItem.getId()).thenReturn(viewItemId);
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                displayObjectBuffer.set(invocation.getArguments()[0]);
                return null;
            }
        }).when(viewItem).setDisplayObject(any(Object.class));
        when(viewItem.getDisplayObject()).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return displayObjectBuffer.get();
            }
        });

        return viewItem;
    }

    public static LightweightList<VisualItem> createViewItems(int... viewItemId) {
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
    public static LightweightList<VisualItem> createViewItems(
            ResourceSet... resourceSets) {

        LightweightList<VisualItem> resourceItems = CollectionFactory
                .createLightweightList();
        for (ResourceSet resourceSet : resourceSets) {
            resourceItems.add(createViewItem(resourceSet.getLabel(),
                    resourceSet));
        }

        return resourceItems;
    }

    // TODO extract hamcrest matcher, inline argThat
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

    public static ArgumentMatcher<LightweightCollection<VisualItem>> eqViewItems(
            final LightweightCollection<VisualItem> viewItems) {
        return new ArgumentMatcher<LightweightCollection<VisualItem>>() {
            @Override
            public boolean matches(Object o) {
                LightweightCollection<VisualItem> set = (LightweightCollection<VisualItem>) o;

                if (set.size() != viewItems.size()) {
                    return false;
                }

                return set.toList().containsAll(viewItems.toList());
            }
        };
    }

    public static LightweightCollection<VisualItem> eqViewItems(
            VisualItem... viewItems) {

        return argThat(eqViewItems(LightweightCollections.toCollection(viewItems)));
    }

    public static <T> Matcher<LightweightCollection<T>> isEmptyLightweightCollection(
            final Class<T> clazz) {

        return new TypeSafeMatcher<LightweightCollection<T>>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Empty LightweightCollection<"
                        + clazz.getName() + ">");
            }

            @Override
            public boolean matchesSafely(LightweightCollection<T> actual) {
                return actual.isEmpty();
            }
        };
    }

    public static <T> Matcher<Delta<T>> matchesDelta(
            final Matcher<LightweightCollection<T>> addedElementsMatcher,
            final Matcher<LightweightCollection<T>> updatedElementsMatcher,
            final Matcher<LightweightCollection<T>> removedElementsMatcher) {

        return new TypeSafeMatcher<Delta<T>>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("added ")
                        .appendDescriptionOf(addedElementsMatcher)
                        .appendText("updated ")
                        .appendDescriptionOf(updatedElementsMatcher)
                        .appendText("removed ")
                        .appendDescriptionOf(removedElementsMatcher);
            }

            @Override
            public boolean matchesSafely(Delta<T> actual) {
                return addedElementsMatcher.matches(actual.getAddedElements())
                        && updatedElementsMatcher.matches(actual
                                .getUpdatedElements())
                        && removedElementsMatcher.matches(actual
                                .getRemovedElements());
            }
        };
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