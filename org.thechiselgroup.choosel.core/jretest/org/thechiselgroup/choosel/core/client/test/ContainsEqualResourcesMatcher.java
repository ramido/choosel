/*******************************************************************************
 * Copyright 2009, 2010 Lars Grammel 
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

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;

public class ContainsEqualResourcesMatcher extends BaseMatcher<ResourceSet> {

    private ResourceSet expected;

    public ContainsEqualResourcesMatcher(ResourceSet expected) {
        this.expected = expected;
    }

    @Override
    public void describeTo(Description description) {
        description.appendValue(expected);
    }

    @Override
    public boolean matches(Object actual) {
        return expected.containsEqualResources((ResourceSet) actual);
    }
}