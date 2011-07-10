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

import java.util.Collection;

import org.hamcrest.Description;
import org.junit.internal.matchers.TypeSafeMatcher;

public final class ContainsExactlyMatcher<T> extends
        TypeSafeMatcher<Iterable<T>> {

    private final Collection<T> expected;

    public ContainsExactlyMatcher(Collection<T> expected) {
        this.expected = expected;
    }

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
}