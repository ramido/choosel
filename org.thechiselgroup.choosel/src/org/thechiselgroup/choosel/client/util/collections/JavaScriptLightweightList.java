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

/**
 * <p>
 * <b>IMPLEMENTATION NOTE</b>: we use an internal JavaScript object because
 * implementing interfaces with generics on JavaScriptObjets causes the
 * development mode to break (see
 * {@link "http://code.google.com/p/google-web-toolkit/issues/detail?id=4864"}
 * ).
 * </p>
 * 
 * @author Lars Grammel
 * 
 * @see http://code.google.com/p/google-web-toolkit/issues/detail?id=4864
 */
public final class JavaScriptLightweightList<T> implements LightweightList<T> {

    private static final class NativeList<T> extends JavaScriptObject {

        protected NativeList() {

        }

        public final native void add(T t) /*-{
            this.push(t);
        }-*/;

        public final native T get(int i) /*-{
            return this[i];
        }-*/;

        public final native int size() /*-{
            return this.length;
        }-*/;
    }

    private static native <T> NativeList<T> createNativeList() /*-{
        return new Array();
    }-*/;

    private NativeList<T> jsList;

    public JavaScriptLightweightList() {
        jsList = createNativeList();
    }

    @Override
    public void add(T t) {
        this.jsList.add(t);
    }

    @Override
    public boolean contains(T t) {
        for (int i = 0; i < size(); i++) {
            if (get(i).equals(t)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public T get(int i) {
        return this.jsList.get(i);
    }

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
    public int size() {
        return this.jsList.size();
    }

    @Override
    public List<T> toList() {
        List<T> result = new ArrayList<T>();

        for (T t : this) {
            result.add(t);
        }

        return result;
    }

}