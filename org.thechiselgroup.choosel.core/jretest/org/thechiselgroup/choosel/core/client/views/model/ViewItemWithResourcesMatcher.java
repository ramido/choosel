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

import static org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory.toResourceSet;

import org.hamcrest.Description;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.thechiselgroup.choosel.core.client.resources.Resource;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;

public class ViewItemWithResourcesMatcher extends
        TypeSafeMatcher<LightweightCollection<ViewItem>> {

    public static ViewItemWithResourcesMatcher containsEqualResources(
            Resource... resources) {

        return new ViewItemWithResourcesMatcher(toResourceSet(resources));
    }

    public static ViewItemWithResourcesMatcher containsEqualResources(
            ResourceSet resources) {
        return new ViewItemWithResourcesMatcher(resources);
    }

    private final ResourceSet resources;

    public ViewItemWithResourcesMatcher(ResourceSet resources) {
        this.resources = resources;
    }

    @Override
    public void describeTo(Description description) {
        description.appendValue(resources);
    }

    @Override
    public boolean matchesSafely(LightweightCollection<ViewItem> viewItems) {
        if (viewItems.size() != 1) {
            return false;
        }

        ResourceSet viewItemResources = viewItems.iterator().next()
                .getResources();
        return viewItemResources.size() == resources.size()
                && viewItemResources.containsAll(resources);
    }
}