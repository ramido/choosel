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

import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightList;
import org.thechiselgroup.choosel.core.client.views.model.Slot;
import org.thechiselgroup.choosel.core.client.views.model.SlotResolverChangedEventHandler;
import org.thechiselgroup.choosel.core.client.views.model.ViewItem;
import org.thechiselgroup.choosel.core.client.views.model.ViewItemValueResolverContext;

/**
 * Calculates a value given a {@link ViewItem}.
 * 
 * @author Lars Grammel
 */
// TODO implementations of this interface should be immutable
// TODO figure out how to handle the type system - we might need to add stuff
// here. a view item value resolver has to match the target slot.
public interface ViewItemValueResolver {

    // XXX remove - can we somehow make this class immutable?
    void addEventHandler(SlotResolverChangedEventHandler handler);

    // XXX remove slot? change list-resource set to viewitem?
    // should be similar to resolve, except that it returns a boolean value
    boolean canResolve(Slot slot, LightweightList<ResourceSet> resourceSets,
            ViewItemValueResolverContext context);

    // XXX remove - should not be required for basic functionality, could
    // we do this somehow differently
    String getResolverId();

    /**
     * Calculates a value for a {@link ViewItem}.
     * 
     * @param context
     *            Context that allows accessing resolvers for other slots. For
     *            example, if you want to determine the color of a node's border
     *            based on it's inner color, you could use the context to find
     *            the resolver for the inner color, and darken it to resolve the
     *            border.
     * 
     * @return Value for the {@link ViewItem}.
     */
    // XXX document exceptions that are thrown if it cannot be resolved
    Object resolve(ViewItem viewItem, ViewItemValueResolverContext context);

}