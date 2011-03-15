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
package org.thechiselgroup.choosel.core.client.views;

import org.thechiselgroup.choosel.core.client.resources.ResourceGrouping;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.views.slots.Slot;
import org.thechiselgroup.choosel.core.client.views.slots.SlotMappingConfiguration;

public interface ViewModel extends ViewItemContainer, ContainsResourceGrouping {

    /**
     * @return {@link ResourceSet} that the {@link ResourceGrouping} of this
     *         {@link ViewModel} is based upon.
     */
    ResourceSet getContentResourceSet();

    // TODO Type mapping operations

    // TODO set
    // TODO rename: global highlighting
    // TODO ReadableResourcesSet
    ResourceSet getHighlightedResources();

    // TODO set
    // TODO rename: global selection
    // TODO ReadableResourceSet
    ResourceSet getSelectedResources();

    SlotMappingConfiguration getSlotMappingConfiguration();

    Slot[] getSlots();

    ViewContentDisplay getViewContentDisplay();

    void setConfigured(boolean configured);

}