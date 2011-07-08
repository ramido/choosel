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
package org.thechiselgroup.choosel.core.client.views.model;

import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;

/**
 * Model of the {@link ViewItem}/{@link Slot} resolutions that cannot be
 * computed in a {@link ViewModel}.
 * 
 * @author Lars Grammel
 */
public interface ViewItemResolutionErrorModel {

    /**
     * @return {@link Slot}s for which there are resolution problems for at
     *         least one {@link ViewItem}.
     */
    LightweightCollection<Slot> getSlotsWithErrors();

    /**
     * @return {@link Slot}s that could not be resolved for {@code viewItem}. If
     *         all slots can be resolved for the view item, an empty list is
     *         returned.
     */
    LightweightCollection<Slot> getSlotsWithErrors(ViewItem viewItem);

    /**
     * @return {@link ViewItem}s for which there are resolution problems for at
     *         least one {@link Slot}.
     */
    LightweightCollection<ViewItem> getViewItemsWithErrors();

    /**
     * @return {@link ViewItems}s that could not be resolved for {@code slot}.
     *         If all {@link ViewItem}s can be resolved for the slot, an empty
     *         list is returned.
     */
    LightweightCollection<ViewItem> getViewItemsWithErrors(Slot slot);

    /**
     * @return <code>true</code>, if there are any resolution problems.
     */
    boolean hasErrors();

    /**
     * @return <code>true</code>, if there are any resolution problems for
     *         {@code slot}.
     */
    boolean hasErrors(Slot slot);

    /**
     * @return <code>true</code>, if there are any resolution problems for
     *         {@code viewItem}.
     */
    boolean hasErrors(ViewItem viewItem);

}