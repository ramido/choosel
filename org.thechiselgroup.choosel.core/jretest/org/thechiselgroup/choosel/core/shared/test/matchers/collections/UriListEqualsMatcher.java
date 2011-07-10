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

import org.hamcrest.Description;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.thechiselgroup.choosel.core.client.resources.UriList;
import org.thechiselgroup.choosel.core.client.util.StringUtils;

public final class UriListEqualsMatcher extends TypeSafeMatcher<UriList> {

    private final String[] expectedUris;

    public UriListEqualsMatcher(String[] expectedUris) {
        this.expectedUris = expectedUris;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("{ " + StringUtils.toString(",", expectedUris)
                + " }");
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
}