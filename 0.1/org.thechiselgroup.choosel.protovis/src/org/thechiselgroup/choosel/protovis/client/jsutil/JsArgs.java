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

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Provides access to the argument list and the this reference that are passed
 * in JavaScript function calls. Variable function arguments similar to
 * JavaScript are not available in GWT / Java.
 * 
 * @author Lars Grammel
 */
public final class JsArgs extends JavaScriptObject {

    protected JsArgs() {
    }

    /**
     * Returns 'this' Object that called the function.
     */
    public final native <S> S getThis() /*-{
        return this._this;
    }-*/;

    /**
     * Returns an object from the argument list that gets passed in javascript
     * functions.
     * 
     * @param index
     *            0 is current Mark, 1 is parent Mark etc.
     */
    public final native <S> S getObject(int index) /*-{
        return this._args[index];
    }-*/;

    /**
     * Returns the object at index 0 from the argument list that gets passed in
     * javascript functions.
     */
    public final native <S> S getObject() /*-{
        return this._args[0];
    }-*/;

    /**
     * Returns an object from the argument list that gets passed in javascript
     * functions.
     * 
     * @param index
     *            0 is current Mark, 1 is parent Mark etc.
     */
    public final native double getDouble(int index) /*-{
        return this._args[index];
    }-*/;

    /**
     * Returns the object at index 0 from the argument list that gets passed in
     * javascript functions.
     */
    public final native double getDouble() /*-{
        return this._args[0];
    }-*/;

    /**
     * Returns an object from the argument list that gets passed in javascript
     * functions.
     * 
     * @param index
     *            0 is current Mark, 1 is parent Mark etc.
     */
    public final native int getInt(int index) /*-{
        return this._args[index];
    }-*/;

    /**
     * Returns the object at index 0 from the argument list that gets passed in
     * javascript functions.
     */
    public final native int getInt() /*-{
        return this._args[0];
    }-*/;
}