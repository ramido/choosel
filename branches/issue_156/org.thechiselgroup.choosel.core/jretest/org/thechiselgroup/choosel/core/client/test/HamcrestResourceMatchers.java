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
package org.thechiselgroup.choosel.core.client.test;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.thechiselgroup.choosel.core.client.resources.Resource;
import org.thechiselgroup.choosel.core.client.resources.UriList;
import org.thechiselgroup.choosel.core.client.util.StringUtils;
import org.thechiselgroup.choosel.core.client.util.collections.CollectionFactory;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightList;

/**
 * Contains Hamcrest matchers (not Mockito Matchers!) for {@link Resource} and
 * related classes.
 * 
 * @author Lars Grammel
 */
// TODO reconcile matchers, renamings
public final class HamcrestResourceMatchers {

    // TODO move to generic hamcrest matcher utility class
    public static <T> Matcher<Iterable<T>> containsExactly(
            final LightweightCollection<T> expected) {

        return new TypeSafeMatcher<Iterable<T>>() {
            @Override
            public void describeTo(Description description) {
                description.appendValue(expected);
            }

            @Override
            public boolean matchesSafely(Iterable<T> actual) {
                int actualSize = 0;
                for (T t : actual) {
                    if (!expected.contains(t)) {
                        return false;
                    }
                    actualSize++;
                }

                return expected.size() == actualSize;
            }
        };
    }

    // TODO move to generic hamcrest matcher utility class
    public static <T> Matcher<Iterable<T>> containsExactly(T... expected) {
        LightweightList<T> list = CollectionFactory.createLightweightList();
        for (T t : expected) {
            list.add(t);
        }
        return containsExactly(list);
    }

    public static Matcher<UriList> containsUrisExactly(
            final String... expectedUris) {
        return new TypeSafeMatcher<UriList>() {

            @Override
            public void describeTo(Description description) {
                description.appendText("{ "
                        + StringUtils.toString(",", expectedUris) + " }");
            }

            @Override
            public boolean matchesSafely(UriList actualUris) {
                if (actualUris.size() != expectedUris.length) {
                    return false;
                }

                for (String uri : expectedUris) {
                    if (!actualUris.contains(uri)) {
                        return false;
                    }
                }

                return true;
            }
        };
    }

    public static <T> Matcher<T[]> equalsArray(final T... expected) {
        return new TypeSafeMatcher<T[]>() {

            @Override
            public void describeTo(Description description) {
                description.appendText("{ "
                        + StringUtils.toString(",", expected) + " }");
            }

            @Override
            public boolean matchesSafely(T[] actual) {
                if (expected.length != actual.length) {
                    return false;
                }

                for (int i = 0; i < expected.length; i++) {
                    if (!expected[i].equals(actual[i])) {
                        return false;
                    }
                }

                return true;
            }
        };
    }

    private HamcrestResourceMatchers() {
    }

}
