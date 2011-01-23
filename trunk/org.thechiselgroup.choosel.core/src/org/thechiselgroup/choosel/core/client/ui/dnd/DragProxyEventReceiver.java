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
package org.thechiselgroup.choosel.core.client.ui.dnd;

/**
 * Part of solution to Problem: MouseOutEvents do not get fired if mouse out
 * occurs because of DOM changes (instead of mouse movement) - use as a tagging
 * interface for widgets, will get called automagically by DragAvatar children.
 */
public interface DragProxyEventReceiver {

    /**
     * Called when the drag proxy is attached (drag and drop operations starts).
     */
    void dragProxyAttached();

    /**
     * Called when the drag proxy is removed (drag and drop operations ends).
     */
    void dragProxyDetached();

}