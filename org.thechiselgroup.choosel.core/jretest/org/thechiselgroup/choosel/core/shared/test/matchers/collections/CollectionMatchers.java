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
package org.thechiselgroup.choosel.core.shared.test.matchers.collections;

import static org.thechiselgroup.choosel.core.client.util.collections.CollectionFactory.createLightweightList;

import java.util.Collection;

import org.hamcrest.Matcher;
import org.thechiselgroup.choosel.core.client.resources.UriList;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightList;

public final class CollectionMatchers {

    public static <T> Matcher<T> contains(Collection<T> expected) {
        return new ContainsMatcher<T>(expected);
    }

    public static <T> Matcher<T> contains(LightweightCollection<T> expected) {
        return CollectionMatchers.<T> contains(expected.toList());
    }

    public static <T> Matcher<Iterable<T>> containsExactly(
            Collection<T> expected) {

        return new ContainsExactlyMatcher<T>(expected);
    }

    public static <T> Matcher<Iterable<T>> containsExactly(
            LightweightCollection<T> expected) {

        return CollectionMatchers.<T> containsExactly(expected.toList());
    }

    public static <T> Matcher<Iterable<T>> containsExactly(T... expected) {
        LightweightList<T> list = createLightweightList();
        list.addAll(expected);
        return CollectionMatchers.<T> containsExactly(list);
    }

    // TODO move
    public static Matcher<UriList> containsUrisExactly(String... expectedUris) {
        return new UriListEqualsMatcher(expectedUris);
    }

    public static <T> Matcher<T[]> equalsArray(T... expected) {
        return new ArrayEqualsMatcher<T>(expected);
    }

    private CollectionMatchers() {
    }

}