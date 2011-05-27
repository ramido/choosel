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

import org.thechiselgroup.choosel.core.client.views.model.Slot;
import org.thechiselgroup.choosel.core.client.views.model.SlotMappingConfiguration;

/**
 * {@link ViewItemValueResolver}s that delegate to resolvers for other slots
 * should implement this interface to ensure that events are fired correctly by
 * {@link SlotMappingConfiguration}.
 * 
 * @author Lars Grammel
 */
public interface DelegatingViewItemValueResolver extends ViewItemValueResolver {

    /**
     * @return {@link Slot} that this {@link ViewItemValueResolver} delegates
     *         to.
     */
    Slot getTargetSlot();

}