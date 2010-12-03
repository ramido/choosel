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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;

public final class JavaScriptLightweightList<T> extends JavaScriptObject
        implements LightweightList<T> {

    public static native <T> JavaScriptLightweightList<T> create() /*-{
        return new Array();
    }-*/;

    protected JavaScriptLightweightList() {
    }

    @Override
    public final native void add(T t) /*-{
        this.push(t);
    }-*/;

    @Override
    public final native T get(int i) /*-{
        return this[i];
    }-*/;

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {

            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < size();
            }

            @Override
            public T next() {
                return get(index++);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("remove not supported");
            }
        };
    }

    @Override
    public final native int size() /*-{
        return this.length;
    }-*/;

    @Override
    public List<T> toList() {
        List<T> result = new ArrayList<T>();

        for (T t : this) {
            result.add(t);
        }

        return result;
    }

}
