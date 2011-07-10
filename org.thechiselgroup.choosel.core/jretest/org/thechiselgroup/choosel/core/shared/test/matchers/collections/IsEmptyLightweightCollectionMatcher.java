package org.thechiselgroup.choosel.core.shared.test.matchers.collections;

import org.hamcrest.Description;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;

public final class IsEmptyLightweightCollectionMatcher<T> extends
        TypeSafeMatcher<LightweightCollection<T>> {
    private final Class<T> clazz;

    public IsEmptyLightweightCollectionMatcher(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("Empty LightweightCollection<"
                + clazz.getName() + ">");
    }

    @Override
    public boolean matchesSafely(LightweightCollection<T> actual) {
        return actual.isEmpty();
    }
}