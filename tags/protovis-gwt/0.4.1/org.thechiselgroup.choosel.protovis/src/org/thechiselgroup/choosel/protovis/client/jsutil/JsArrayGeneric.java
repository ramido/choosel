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
package org.thechiselgroup.choosel.protovis.client.jsutil;

import java.util.Comparator;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Generic JavaScript array that can store non-{@link JavaScriptObject}s. It
 * provides a factory method that uses <code>new $wnd.Array()</code> to create
 * Arrays that work across iframes, i.e. the <code>instanceof Array</code> check
 * in 3rd party JavaScript libraries returns true.
 * 
 * @author Lars Grammel
 */
public class JsArrayGeneric<T> extends JavaScriptObject {

    protected JsArrayGeneric() {
    }

    public final native T get(int index) /*-{
        return this[index];
    }-*/;

    private final native void jsSort(JavaScriptObject comparator) /*-{
        this.sort(comparator);
    }-*/;

    private final native void jsSortStable(JavaScriptObject comparator) /*-{
        // create temporary array
        var tempArray =  new Array();
        for (var i = 0; i < this.length; i++) {
        tempArray.push({ value: this[i], index: i });
        }

        // sort temporary array (stable)
        tempArray.sort(function(a,b) {
        var valueComparison = comparator(a.value, b.value);
        if (valueComparison != 0) {
        return valueComparison;
        }
        return a.index > b.index ? 1 : -1;
        });

        // copy elements from temporary array back to main array
        for (var j = 0; j < tempArray.length; j++) {
        this[j] = tempArray[j].value;
        }
    }-*/;

    public final native int length() /*-{
        return this.length;
    }-*/;

    public final native void push(T value) /*-{
        this[this.length] = value;
    }-*/;

    public final native void set(int index, T value) /*-{
        this[index] = value;
    }-*/;

    public final native void setLength(int newLength) /*-{
        this.length = newLength;
    }-*/;

    public final native T shift() /*-{
        return this.shift();
    }-*/;

    /**
     * Sorts this array using the JavaScript array sort, which is not required
     * to be stable according to the ECMA specification.
     * 
     * @see <a
     *      href="http://stackoverflow.com/questions/3195941/sorting-an-array-of-objects-in-chrome">V8
     *      Sorting Not Stable</a>
     */
    public final void sort(Comparator<T> comparator) {
        jsSort(JsUtils.toJsComparator(comparator));
    }

    /**
     * Sorts this array. This sort guarantees that the order if the elements is
     * retained if their comparison value is 0. It uses a separate array and
     * comparator and is thus slower and consumes more memory than the basic
     * sort.
     */
    public final void sortStable(Comparator<T> comparator) {
        jsSortStable(JsUtils.toJsComparator(comparator));
    }

    public final native void unshift(T value) /*-{
        this.unshift(value);
    }-*/;

}