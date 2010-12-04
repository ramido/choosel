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
package org.thechiselgroup.choosel.client.util.collections;

import java.util.List;

/**
 * Generic lightweight collection. LightweightCollection provides a read-only
 * interface that extends Iterable and only contains the most important
 * operations and thus facilitate the implementation of optimized JavaScript
 * versions.
 * 
 * @author Lars Grammel
 * 
 * @param <T>
 */
public interface LightweightCollection<T> extends Iterable<T> {

    boolean contains(T t);

    boolean isEmpty();

    int size();

    /**
     * Converts this lightweight collection into a List. This usually has a
     * fairly high performance penalty and is only recommended for testing.
     */
    List<T> toList();

}
