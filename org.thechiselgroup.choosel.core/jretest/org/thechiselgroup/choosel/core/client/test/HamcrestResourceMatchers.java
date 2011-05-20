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
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.resources.UriList;
import org.thechiselgroup.choosel.core.client.util.StringUtils;

/**
 * Contains Hamcrest matchers (not Mockito Matchers!) for {@link Resource} and
 * related classes.
 * 
 * @author Lars Grammel
 */
public final class HamcrestResourceMatchers {

    public static Matcher<Iterable<Resource>> containsEqualResources(
            final ResourceSet expected) {

        return new TypeSafeMatcher<Iterable<Resource>>() {
            @Override
            public void describeTo(Description description) {
                description
                        .appendText(" does not contain the same resources as "
                                + expected.toString());
            }

            @Override
            public boolean matchesSafely(Iterable<Resource> actual) {
                int actualSize = 0;
                for (Resource resource : actual) {
                    actualSize++;
                }

                return expected.containsAll(actual)
                        && expected.size() == actualSize;
            }
        };
    }

    public static Matcher<UriList> containsExactly(final String... uris) {
        return new TypeSafeMatcher<UriList>() {

            @Override
            public void describeTo(Description description) {
                description.appendText(" does not exactly contain { "
                        + StringUtils.toString(",", uris) + " }");
            }

            @Override
            public boolean matchesSafely(UriList uriList) {
                if (uriList.size() != uris.length) {
                    return false;
                }

                for (String uri : uris) {
                    if (!uriList.contains(uri)) {
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
