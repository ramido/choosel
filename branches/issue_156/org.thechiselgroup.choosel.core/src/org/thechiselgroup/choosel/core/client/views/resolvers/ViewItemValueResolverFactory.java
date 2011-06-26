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

/**
 * This interface is used to return new instances of a
 * {@link ViewItemValueResolver} . This is used so that the application can
 * create new instances on the fly as users create the need for them
 */
public interface ViewItemValueResolverFactory {

    /**
     * A factory must always know if a resolver that it has created is
     * applicable to a set of view items in a given slot. This means that any
     * internal state change of a resolver must not change whether or not it can
     * resolve view items, only how it does the resolution.
     */
    boolean canCreateApplicableResolver(Slot slot,
            LightweightCollection<ViewItem> viewItems);

    /**
     * @return A new instance of the corresponding
     *         {@link ManagedViewItemValueResolver}
     */
    /*
     * TODO what I really want to do is pass in a context of the current view in
     * here. For example, besides current view items, what about passing in the
     * currently selected property. This would be great if you wanted to select
     * the same property by default
     * 
     * TODO The create methods might need to be resolver specific, because the
     * ViewItemValueResolvers are immutable and we need to configure parameters.
     */
    ManagedViewItemValueResolver create(
            LightweightCollection<ViewItem> viewItems);

    /**
     * @return the id of both the factory and the resolver
     */
    String getId();

    /**
     * @return The label used in the UI that describes the factory
     */
    String getLabel();

}
