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
package org.thechiselgroup.choosel.client.util.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * <p>
 * HandlerManager that supports prioritize calling of {@link EventHandler}s.
 * </p>
 * <p>
 * <b>IMPLEMENTATION NOTE</b>: Implemented using several internal
 * {@link HandlerManager}s that get called in order. This means that
 * higher-priority event handlers can add lower-priority EventHandlers to a
 * PrioritizedHandlerManager that get executed in the same event processing run.
 * </p>
 * 
 * @author Lars Grammel
 * 
 * @see EventHandlerPriority
 */
public class PrioritizedHandlerManager {

    private HandlerManager firstPriorityHandlers;

    private HandlerManager normalPriorityHandlers;

    private HandlerManager lastPriorityHandlers;

    public PrioritizedHandlerManager(Object source) {
        assert source != null;

        firstPriorityHandlers = new HandlerManager(source);
        normalPriorityHandlers = new HandlerManager(source);
        lastPriorityHandlers = new HandlerManager(source);
    }

    /**
     * Adds an event handler with <code>NORMAL</code> priority.
     * 
     * @return handler registration that can be used to remove the event
     *         handler.
     */
    public <H extends EventHandler> HandlerRegistration addHandler(
            GwtEvent.Type<H> type, H handler) {

        return addHandler(type, handler, EventHandlerPriority.NORMAL);
    }

    /**
     * Adds an event handler.
     * 
     * @param priority
     *            priority how the event handler should get called
     * 
     * @return handler registration that can be used to remove the event
     *         handler.
     */
    public <H extends EventHandler> HandlerRegistration addHandler(
            GwtEvent.Type<H> type, H handler, EventHandlerPriority priority) {

        assert priority != null;
        assert type != null;
        assert handler != null;

        switch (priority) {
        case FIRST:
            return firstPriorityHandlers.addHandler(type, handler);
        case NORMAL:
            return normalPriorityHandlers.addHandler(type, handler);
        case LAST:
            return lastPriorityHandlers.addHandler(type, handler);
        }

        throw new IllegalArgumentException("unsupported priority: " + priority);
    }

    public void fireEvent(GwtEvent<?> event) {
        assert event != null;

        firstPriorityHandlers.fireEvent(event);
        normalPriorityHandlers.fireEvent(event);
        lastPriorityHandlers.fireEvent(event);
    }

}