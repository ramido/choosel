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
package org.thechiselgroup.choosel.client.views.graph;

import org.thechiselgroup.choosel.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.client.views.ResourceItem;

/**
 * Creates arc items for graph items.
 * 
 * @author Lars Grammel
 */
public interface ArcType {

    /**
     * Returns the arc items representing arcs that should be connected to the
     * node items for the given resource items.
     * 
     * @param resourceItems
     *            resource items for which corresponding arc items should be
     *            returned
     * @return arc items that connected to the node representation of the
     *         resource items. The arc items do not have to be the same (in
     *         terms of object references) as already returned arc items for the
     *         same resource items. However, their ids should match: an equal
     *         arc item should have an equal id accross multiple calls.
     */
    LightweightCollection<ArcItem> getArcItems(
            LightweightCollection<ResourceItem> resourceItems);

}