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

import static org.thechiselgroup.choosel.core.client.resources.TestResourceSetFactory.toResourceSet;

import org.hamcrest.Description;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.thechiselgroup.choosel.core.client.resources.Resource;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItem;

public class VisualItemWithResourcesMatcher extends
        TypeSafeMatcher<LightweightCollection<VisualItem>> {

    public static VisualItemWithResourcesMatcher containsEqualResources(
            Resource... resources) {

        return new VisualItemWithResourcesMatcher(toResourceSet(resources));
    }

    public static VisualItemWithResourcesMatcher containsEqualResources(
            ResourceSet resources) {
        return new VisualItemWithResourcesMatcher(resources);
    }

    private final ResourceSet resources;

    public VisualItemWithResourcesMatcher(ResourceSet resources) {
        this.resources = resources;
    }

    @Override
    public void describeTo(Description description) {
        description.appendValue(resources);
    }

    @Override
    public boolean matchesSafely(LightweightCollection<VisualItem> viewItems) {
        if (viewItems.size() != 1) {
            return false;
        }

        ResourceSet viewItemResources = viewItems.iterator().next()
                .getResources();
        return viewItemResources.size() == resources.size()
                && viewItemResources.containsAll(resources);
    }
}