/*******************************************************************************
 * Copyright (C) 2011 Lars Grammel 
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
package org.thechiselgroup.choosel.core.client.views.resolvers;

import org.thechiselgroup.choosel.core.client.views.model.SlotResolverChangedEvent;
import org.thechiselgroup.choosel.core.client.views.model.SlotResolverChangedEventHandler;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Extend this class to give your child class the behavior that
 * SlotResolverChangedEventHandler can watch the resolver for events.
 */
public abstract class AbstractEventHandlingViewItemValueResolver implements
        ViewItemValueResolver {

    HandlerManager eventBus = new HandlerManager(this);

    @Override
    public void addEventHandler(SlotResolverChangedEventHandler handler) {
        eventBus.addHandler(SlotResolverChangedEvent.TYPE, handler);
    }

    public void fireEvent(SlotResolverChangedEvent event) {
        eventBus.fireEvent(event);
    }
}
