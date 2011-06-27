package org.thechiselgroup.choosel.core.client.views.model;

import org.hamcrest.Description;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.thechiselgroup.choosel.core.client.resources.Resource;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;

public class ViewItemWithSingleResourceMatcher extends
        TypeSafeMatcher<LightweightCollection<ViewItem>> {
    // TODO refactor to take in a list of resources
    public static ViewItemWithSingleResourceMatcher containsEqualResource(
            final Resource resource) {
        return new ViewItemWithSingleResourceMatcher(resource);
    }

    private final Resource resource;

    public ViewItemWithSingleResourceMatcher(Resource resource) {
        this.resource = resource;
    }

    @Override
    public void describeTo(Description description) {
        description.appendValue(resource);
    }

    @Override
    public boolean matchesSafely(LightweightCollection<ViewItem> viewItems) {
        if (viewItems.size() != 1) {
            return false;
        }

        ResourceSet viewItemResources = viewItems.iterator().next()
                .getResources();
        return viewItemResources.size() == 1
                && viewItemResources.contains(resource);
    }
}