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
package org.thechiselgroup.choosel.core.client.views;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.Event;

/**
 * User interaction with the visual representation of a {@link ViewItem}. This
 * should wrap mouse and potentially keyboard interaction. A separate class was
 * introduced to facilitate the integration of components using 3rd party
 * technology, e.g. Flash, Silverlight.
 */
public class ViewItemInteraction {

    private NativeEvent nativeEvent;

    private int clientX;

    private int clientY;

    private int eventType;

    public ViewItemInteraction(int eventType, int clientX, int clientY) {
        this.eventType = eventType;
        this.clientX = clientX;
        this.clientY = clientY;
    }

    public ViewItemInteraction(NativeEvent e) {
        assert e != null;

        this.nativeEvent = e;
        this.clientX = e.getClientX();
        this.clientY = e.getClientY();
        this.eventType = Event.as(e).getTypeInt();
    }

    /**
     * X-Coordinate of the event in the browser client area.
     */
    public int getClientX() {
        return clientX;
    }

    /**
     * Y-Coordinate of the event in the browser client area.
     */
    public int getClientY() {
        return clientY;
    }

    /**
     * Int flag representing the event type. See {@link Event} for the
     * constants.
     * 
     * @see Event
     */
    public int getEventType() {
        return eventType;
    }

    /**
     * Native event, if available.
     */
    public NativeEvent getNativeEvent() {
        return nativeEvent;
    }

    public boolean hasNativeEvent() {
        return nativeEvent != null;
    }

}