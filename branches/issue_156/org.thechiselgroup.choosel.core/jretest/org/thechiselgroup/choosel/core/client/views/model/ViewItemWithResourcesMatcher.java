package org.thechiselgroup.choosel.core.client.views.model;

import org.hamcrest.Description;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.thechiselgroup.choosel.core.client.resources.DefaultResourceSet;
import org.thechiselgroup.choosel.core.client.resources.Resource;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;

public class ViewItemWithResourcesMatcher extends
        TypeSafeMatcher<LightweightCollection<ViewItem>> {

    public static ViewItemWithResourcesMatcher containsEqualResource(
            final Resource resource) {

        ResourceSet resources = new DefaultResourceSet();
        resources.add(resource);
        return new ViewItemWithResourcesMatcher(resources);
    }

    public static ViewItemWithResourcesMatcher containsEqualResources(
            final ResourceSet resources) {
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