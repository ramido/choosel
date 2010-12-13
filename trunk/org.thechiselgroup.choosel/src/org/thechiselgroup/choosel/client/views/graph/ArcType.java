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

import org.thechiselgroup.choosel.client.ui.widget.graph.Arc;
import org.thechiselgroup.choosel.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.client.views.ResourceItem;

/**
 * A specific class of arcs. Calculates arc items for a given resource item.
 * 
 * @author Lars Grammel
 */
public interface ArcType {

    /**
     * Returns the arcs that are related to a given resource item.
     * 
     * @param resourceItem
     *            resource item for which corresponding arcs should be returned
     * @return arcs that connected to the node representation of the resource
     *         item. The arcs do not have to be the same (in terms of object
     *         references) as already returned arcs for the same resource item.
     *         However, their IDs should match: an equal arc should have an
     *         equal id across multiple calls.
     */
    LightweightCollection<Arc> getArcs(ResourceItem resourceItem);

    /**
     * Returns the default color for arcs of this type.
     */
    String getDefaultArcColor();

    /**
     * Returns the default arc style for arcs of this type.
     */
    String getDefaultArcStyle();

    /**
     * Returns the identifier of this arc type. Each ArcType must have a unique
     * identifier.
     */
    String getID();

}