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

import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.views.model.Slot;
import org.thechiselgroup.choosel.core.client.views.model.ViewItem;
import org.thechiselgroup.choosel.core.client.views.model.ViewItemValueResolverContext;
import org.thechiselgroup.choosel.core.client.views.model.ViewModel;

/**
 * <p>
 * Calculates a value given a {@link ViewItem}. Implementations of this
 * interface must be immutable.
 * </p>
 * <p>
 * {@code ViewItemValueResolver}s can use other {@code ViewItemValueResolver}s
 * defined on other {@link Slot}s of this {@link ViewModel} in their
 * calculation. The {@link ViewItemValueResolverContext} exposes those during
 * the calculation. However, they must declare the {@link Slot}s they depend on
 * in {@link #getTargetSlots()}.
 * </p>
 * 
 * @author Lars Grammel
 */
// TODO figure out how to handle the type system - we might need to add stuff
// here. a view item value resolver has to match the target slot.
// TODO move to views.model
public interface ViewItemValueResolver {

    /**
     * Tests if this {@link ViewItemValueResolver} can calculate a value for
     * {@code viewItem} in the {@link ViewItemValueResolverContext}
     * {@code context}.
     * 
     * @param viewItem
     *            {@link ViewItem} for which this {@link ViewItemValueResolver}
     *            is asked to calculate a value.
     * @param context
     *            Context that allows accessing resolvers for other slots. For
     *            example, if you want to determine the color of a node's border
     *            based on it's inner color, you could use the context to find
     *            the resolver for the inner color, and darken it to resolve the
     *            border.
     * 
     * @return {@code true}, if a value could be calculated, {@code false}
     *         otherwise.
     */
    boolean canResolve(ViewItem viewItem, ViewItemValueResolverContext context);

    /**
     * @return {@link Slot}s that this {@link ViewItemValueResolver} delegates
     *         to. Must be an empty collection if the this
     *         {@link ViewItemValueResolver} does not delegate (see
     *         {@link AbstractSimpleViewItemValueResolver}).
     */
    LightweightCollection<Slot> getTargetSlots();

    /**
     * Calculates a value for a {@link ViewItem}.
     * 
     * @param viewItem
     *            {@link ViewItem} for which this {@link ViewItemValueResolver}
     *            is asked to calculate a value.
     * @param context
     *            Context that allows accessing resolvers for other slots. For
     *            example, if you want to determine the color of a node's border
     *            based on it's inner color, you could use the context to find
     *            the resolver for the inner color, and darken it to resolve the
     *            border.
     * 
     * @return Value for the {@link ViewItem}.
     */
    // TODO ? document exceptions that are thrown if it cannot be resolved
    // TODO return typed result (requires type system)
    Object resolve(ViewItem viewItem, ViewItemValueResolverContext context);

}