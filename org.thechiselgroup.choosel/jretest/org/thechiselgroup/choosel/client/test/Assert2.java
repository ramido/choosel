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
package org.thechiselgroup.choosel.client.test;

import java.util.Collection;
import java.util.List;

import org.junit.Assert;

public final class Assert2 {

    public static <T> void assertContains(Collection<T> c, T value) {
        String failureMessage = value + " should be contained in " + c;
        Assert.assertEquals(failureMessage, true, c.contains(value));
    }

    public static <T> void assertEquals(List<T> expected, List<T> result) {
        String failureMessage = "expected: " + expected + ", but was: "
                + result;

        Assert.assertEquals(failureMessage, expected.size(), result.size());
        for (int i = 0; i < expected.size(); i++) {
            Assert.assertEquals(failureMessage, expected.get(i), result.get(i));
        }
    }

    private Assert2() {
    }

}
