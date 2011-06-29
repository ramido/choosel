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
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollections;
import org.thechiselgroup.choosel.core.client.views.resolvers.ViewItemValueResolver;

public final class ViewItemValueResolverTestUtils {

    public static ViewItemValueResolver createResolverCanResolveResourceWithProperty(
            final String property) {
        ViewItemValueResolver resolver = mockViewItemValueResolver();
        when(
                resolver.canResolve(any(ViewItem.class),
                        any(ViewItemValueResolverContext.class))).thenAnswer(
                new Answer<Boolean>() {
                    @Override
                    public Boolean answer(InvocationOnMock invocation)
                            throws Throwable {
                        ViewItem viewItem = (ViewItem) invocation
                                .getArguments()[0];
                        ResourceSet set = viewItem.getResources();

                        return set.size() == 1
                                && set.getFirstResource().containsProperty(
                                        property);
                    };
                });
        return resolver;
    }

    public static ViewItemValueResolver createResolverThatCanResolveIfContainsResourcesExactly(
            final ResourceSet resources) {

        ViewItemValueResolver resolver = mockViewItemValueResolver();
        when(
                resolver.canResolve(any(ViewItem.class),
                        any(ViewItemValueResolverContext.class))).thenAnswer(
                new Answer<Boolean>() {
                    @Override
                    public Boolean answer(InvocationOnMock invocation)
                            throws Throwable {
                        ViewItem viewItem = (ViewItem) invocation
                                .getArguments()[0];
                        ResourceSet set = viewItem.getResources();
                        return set.size() == resources.size()
                                && set.containsAll(resources);

                    };
                });
        return resolver;
    }

    public static ViewItemValueResolver mockAlwaysApplicableResolver() {
        ViewItemValueResolver resolver = mockViewItemValueResolver();
        when(
                resolver.canResolve(any(ViewItem.class),
                        any(ViewItemValueResolverContext.class))).thenReturn(
                true);
        return resolver;
    }

    public static ViewItemValueResolver mockViewItemValueResolver() {
        ViewItemValueResolver resolver = mock(ViewItemValueResolver.class);
        when(resolver.getTargetSlots()).thenReturn(
                LightweightCollections.<Slot> emptyCollection());
        return resolver;
    }

    private ViewItemValueResolverTestUtils() {
    }

}
