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
import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.core.client.JsArrayString;

public final class JsUtils {

    /**
     * Creates a new generic array using <code>new $wnd.Array()</code>. This
     * array is part of the main frame and will thus get recognized as an array
     * by external javascript libraries in an <code>instanceof Array</code>
     * check.
     * 
     * @see <a
     *      href="http://groups.google.com/group/google-web-toolkit/browse_thread/thread/09d82fa9a8d87832?fwc=1&pli=1">Google
     *      Groups Thread</a>
     * @see <a
     *      href="http://perfectionkills.com/instanceof-considered-harmful-or-how-to-write-a-robust-isarray/">Instanceof
     *      Considered Harmful</a>
     */
    public static native <T> JsArrayGeneric<T> createJsArrayGeneric() /*-{
        return new $wnd.Array();
    }-*/;

    public final static native JsArrayInteger createJsArrayInteger() /*-{
        return new $wnd.Array();
    }-*/;

    public final static native JsArrayNumber createJsArrayNumber() /*-{
        return new $wnd.Array();
    }-*/;

    public final static native JsArrayString createJsArrayString() /*-{
        return new $wnd.Array();
    }-*/;

    public final static native String toFixed(double d, int decimalPlaces) /*-{
        return d.toFixed(decimalPlaces);
    }-*/;

    public final static native JavaScriptObject toJsComparator(
            Comparator<?> comparator) /*-{
        return function(a,b) {
        return comparator.@java.util.Comparator::compare(Ljava/lang/Object;Ljava/lang/Object;)(a, b);
        };
    }-*/;

    public final static <S> JsArrayGeneric<S> toJsArrayGeneric(
            Iterable<S> values) {

        JsArrayGeneric<S> array = createJsArrayGeneric();
        for (S value : values) {
            array.push(value);
        }
        return array;
    }

    public final static JsArrayString toJsArrayString(String... values) {
        JsArrayString array = createJsArrayString();
        for (String value : values) {
            array.push(value);
        }
        return array;
    }

    public final static <S> JsArrayGeneric<S> toJsArrayGeneric(S... values) {
        JsArrayGeneric<S> array = createJsArrayGeneric();
        for (S value : values) {
            array.push(value);
        }
        return array;
    }

    public final static JsArrayInteger toJsArrayInteger(int... values) {
        JsArrayInteger array = createJsArrayInteger();
        for (int value : values) {
            array.push(value);
        }
        return array;
    }

    public final static JsArrayNumber toJsArrayNumber(double... values) {
        JsArrayNumber array = createJsArrayNumber();
        for (double value : values) {
            array.push(value);
        }
        return array;
    }

    private JsUtils() {
    }
}