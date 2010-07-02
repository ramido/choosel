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
package org.thechiselgroup.choosel.client.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class CollectionUtils {

    public static <T> List<T> toList(Iterable<T> iterable) {
        List<T> result = new ArrayList<T>();
        for (T t : iterable) {
            result.add(t);
        }
        return result;
    }

    public static <T> List<T> toList(T... ts) {
        List<T> list = new ArrayList<T>();
        for (T t : ts) {
            list.add(t);
        }
        return list;
    }

    public static <T> Set<T> toSet(T... ts) {
        Set<T> set = new HashSet<T>();
        for (T t : ts) {
            set.add(t);
        }
        return set;
    }

    private CollectionUtils() {

    }

}
