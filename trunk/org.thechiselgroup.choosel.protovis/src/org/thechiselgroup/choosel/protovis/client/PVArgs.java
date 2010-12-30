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
 * Returns the parameter stack that gets passed in javascript functions. Because
 * variable function arguments similar to JavaScript are not available in GWT,
 * the stack is made available using {@link PVArgs}.
 * 
 * @author Lars Grammel
 */
public final class PVArgs extends JavaScriptObject {

    protected PVArgs() {
    }

    /**
     * Returns the parameter stack that gets passed in javascript functions.
     * Because variable function arguments similar to JavaScript are not
     * available in GWT, the stack is made available here.
     * 
     * @param index
     *            0 is current Mark, 1 is parent Mark etc.
     */
    public final native <S> S getObject(int index) /*-{
        return this[index];
    }-*/;

    /**
     * Returns the parameter stack that gets passed in javascript functions.
     * Because variable function arguments similar to JavaScript are not
     * available in GWT, the stack is made available here.
     * 
     * @param index
     *            0 is current Mark, 1 is parent Mark etc.
     */
    public final native double getDouble(int index) /*-{
        return this[index];
    }-*/;

    /**
     * Returns the parameter stack that gets passed in javascript functions.
     * Because variable function arguments similar to JavaScript are not
     * available in GWT, the stack is made available here.
     * 
     * @param index
     *            0 is current Mark, 1 is parent Mark etc.
     */
    public final native int getInt(int index) /*-{
        return this[index];
    }-*/;
}