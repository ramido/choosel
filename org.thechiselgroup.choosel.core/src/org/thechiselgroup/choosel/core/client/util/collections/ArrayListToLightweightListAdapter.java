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
package org.thechiselgroup.choosel.core.client.util.collections;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ArrayListToLightweightListAdapter<T> implements LightweightList<T> {

    private List<T> delegate = new ArrayList<T>();

    @Override
    public void add(T t) {
        delegate.add(t);
    }

    @Override
    public void addAll(Iterable<? extends T> collection) {
        for (T t : collection) {
            add(t);
        }
    }

    @Override
    public void addAll(T[] array) {
        for (T t : array) {
            add(t);
        }
    }

    @Override
    public boolean contains(T t) {
        return delegate.contains(t);
    }

    @Override
    public T get(int i) {
        return delegate.get(i);
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public Iterator<T> iterator() {
        return delegate.iterator();
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public List<T> toList() {
        return delegate;
    }

}
