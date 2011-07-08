/*******************************************************************************
 * Copyright (C) 2011 Lars Grammel 
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
package org.thechiselgroup.choosel.core.client.views.model;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.mockito.stubbing.OngoingStubbing;
import org.thechiselgroup.choosel.core.client.resources.DataType;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollections;
import org.thechiselgroup.choosel.core.client.views.resolvers.ManagedViewItemValueResolver;
import org.thechiselgroup.choosel.core.client.views.resolvers.ViewItemValueResolver;

/**
 * Creates mock {@link ViewItemValueResolver}s.
 * 
 * @author Lars Grammel
 */
public final class VisualItemValueResolverTestUtils {

    public static Slot[] createSlots(DataType... dataTypes) {
        Slot[] slots = new Slot[dataTypes.length];
        for (int i = 0; i < slots.length; i++) {
            slots[i] = new Slot("slot" + i, "Slot " + i, dataTypes[i]);
        }
        return slots;
    }

    public static ManagedViewItemValueResolver mockManagedResolver(String id) {
        ManagedViewItemValueResolver resolver = mock(ManagedViewItemValueResolver.class);
        when(resolver.getTargetSlots()).thenReturn(
                LightweightCollections.<Slot> emptyCollection());
        when(resolver.getResolverId()).thenReturn(id);
        return resolver;
    }

    public static ViewItemValueResolver mockResolver() {
        ViewItemValueResolver resolver = mock(ViewItemValueResolver.class);
        when(resolver.getTargetSlots()).thenReturn(
                LightweightCollections.<Slot> emptyCollection());
        return resolver;
    }

    public static ViewItemValueResolver mockResolverThatCanAlwaysResolve() {
        ViewItemValueResolver resolver = mockResolver();
        whenCanResolve(resolver).thenReturn(true);
        return resolver;
    }

    public static ViewItemValueResolver mockResolverThatCanNeverResolve() {
        ViewItemValueResolver resolver = mockResolver();
        whenCanResolve(resolver).thenReturn(false);
        return resolver;
    }

    public static ViewItemValueResolver mockResolverThatCanResolveExactResourceSet(
            final ResourceSet resources) {

        ViewItemValueResolver resolver = mockResolver();
        whenCanResolve(resolver).thenAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                VisualItem viewItem = (VisualItem) invocation.getArguments()[0];
                ResourceSet set = viewItem.getResources();

                return set.size() == resources.size()
                        && set.containsAll(resources);
            };
        });
        return resolver;
    }

    public static ViewItemValueResolver mockResolverThatCanResolveIfContainsResources(
            final ResourceSet resources) {
        ViewItemValueResolver resolver = mockResolver();
        whenCanResolve(resolver).thenAnswer(new Answer<Boolean>() {

            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                VisualItem viewItem = (VisualItem) invocation.getArguments()[0];
                ResourceSet set = viewItem.getResources();

                return set.containsAll(resources);
            }
        });
        return resolver;
    }

    public static ViewItemValueResolver mockResolverThatCanResolveProperty(
            final String property) {

        ViewItemValueResolver resolver = mockResolver();
        whenCanResolve(resolver).thenAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                VisualItem viewItem = (VisualItem) invocation.getArguments()[0];
                ResourceSet set = viewItem.getResources();

                return set.size() == 1
                        && set.getFirstElement().containsProperty(property);
            };
        });
        return resolver;
    }

    private static OngoingStubbing<Boolean> whenCanResolve(
            ViewItemValueResolver resolver) {

        return when(resolver.canResolve(any(VisualItem.class),
                any(VisualItemValueResolverContext.class)));
    }

    private VisualItemValueResolverTestUtils() {
    }

}
