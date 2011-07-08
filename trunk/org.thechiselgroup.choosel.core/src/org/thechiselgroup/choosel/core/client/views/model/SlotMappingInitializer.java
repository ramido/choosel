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
package org.thechiselgroup.choosel.core.client.views.model;

import java.util.Map;

import org.thechiselgroup.choosel.core.client.resources.Resource;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.views.resolvers.ViewItemValueResolver;

/**
 * Calculates default {@link ViewItemValueResolver}s. Used to set
 * {@link ViewItemValueResolver}s on a {@link VisualizationModel} if the mappings for
 * some {@link Slot}s are invalid.
 */
// TODO rename to DefaultViewItemValueResolverProvider
public interface SlotMappingInitializer {

    /**
     * Calculates default {@link ViewItemValueResolver}s.
     * 
     * @param viewResources
     *            All {@link Resource}s that are contained in the
     *            {@link VisualizationModel}.
     * @param slotsToUpdate
     *            {@link Slot}s for which default {@link ViewItemValueResolver}s
     *            should be calculated.
     * 
     * @return A map that contains {@link ViewItemValueResolver}s for some or
     *         all of the {@link Slot}s specified in {@code slotsToUpdate}.
     *         There must be no {@link Slot}s in the result that are not
     *         contained in {@code slotsToUpdate}.
     */
    Map<Slot, ViewItemValueResolver> getResolvers(ResourceSet viewResources,
            Slot[] slotsToUpdate);

}