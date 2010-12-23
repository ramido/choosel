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
package org.thechiselgroup.choosel.protovis.client;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Generic JavaScript array that can store non-{@link JavaScriptObject}s. It
 * provides a factory method that uses <code>new $wnd.Array()</code> to create
 * Arrays that work across iframes, i.e. the <code>instanceof Array</code> check
 * in 3rd party JavaScript libraries returns true.
 * 
 * @author Lars Grammel
 */
// @formatter:off
public class JsGenericArray<T> extends JavaScriptObject {

    /**
     * Creates a new generic array using <code>new $wnd.Array()</code>. This array is part
     * of the main frame and will thus get recognized as an array by external javascript libraries
     * in an <code>instanceof Array</code> check.
     * 
     * @see <a href="http://groups.google.com/group/google-web-toolkit/browse_thread/thread/09d82fa9a8d87832?fwc=1&pli=1">Google Groups Thread</a>
     * @see <a href="http://perfectionkills.com/instanceof-considered-harmful-or-how-to-write-a-robust-isarray/">Instanceof Considered Harmful</a> 
     */
    public static native <T> JsGenericArray<T> createGenericArray() /*-{
        return new $wnd.Array();
    }-*/;

    protected JsGenericArray() {
    }

    public final native T get(int index) /*-{
        return this[index];
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

    public final native void unshift(T value) /*-{
        this.unshift(value);
    }-*/;
    
}
// @formatter:on