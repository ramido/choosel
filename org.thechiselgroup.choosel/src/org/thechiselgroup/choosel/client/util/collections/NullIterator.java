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

import java.util.Iterator;
import java.util.NoSuchElementException;

public final class NullIterator<T> implements Iterator<T> {

    @SuppressWarnings("rawtypes")
    private static NullIterator NULL_ITERATOR = new NullIterator();

    @SuppressWarnings("unchecked")
    public static <T> Iterator<T> nullIterator() {
        return NULL_ITERATOR;
    }

    private NullIterator() {

    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public T next() {
        throw new NoSuchElementException();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}