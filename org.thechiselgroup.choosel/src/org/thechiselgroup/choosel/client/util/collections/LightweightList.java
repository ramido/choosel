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
 * Minimalistic list for intermediary data processing. A JavaScript
 * implementation is available.
 * 
 * @author Lars Grammel
 */
public interface LightweightList<T> extends Iterable<T> {

    void add(T t);

    T get(int i);

    boolean isEmpty();

    int size();

    /**
     * Converts the lightweight list into a regular list from the Java
     * collections framework. This is especially useful for tests, however, it
     * removes all performance advantages.
     */
    List<T> toList();

}