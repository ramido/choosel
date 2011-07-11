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
package org.thechiselgroup.choosel.core.client.visualization.model.implementation;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.concurrent.atomic.AtomicReference;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.resources.ResourceSetTestUtils;
import org.thechiselgroup.choosel.core.client.util.collections.CollectionFactory;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightList;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItem;

public final class VisualItemTestUtils {

    private VisualItemTestUtils() {
    }

    public static LightweightList<VisualItem> createViewItems(int... viewItemId) {
        ResourceSet[] resourceSets = new ResourceSet[viewItemId.length];
        for (int i = 0; i < resourceSets.length; i++) {
            resourceSets[i] = ResourceSetTestUtils.toLabeledResourceSet("" + viewItemId[i],
                    ResourceSetTestUtils.createResource(viewItemId[i]));
        }
        return VisualItemTestUtils.createViewItems(resourceSets);
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
            resourceItems.add(VisualItemTestUtils.createViewItem(resourceSet.getLabel(),
                    resourceSet));
        }
    
        return resourceItems;
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

    public static VisualItem createViewItem(int id) {
        return createViewItem("" + id, ResourceSetTestUtils.createResources(id));
    }

    public static Matcher<LightweightCollection<VisualItem>> containsVisualItemsForExactResourceSets(
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

}