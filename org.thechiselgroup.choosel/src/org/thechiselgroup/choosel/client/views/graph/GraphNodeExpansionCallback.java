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

import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceCategorizer;
import org.thechiselgroup.choosel.client.resources.ResourceManager;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.ui.widget.graph.GraphDisplay;

// TODO use resource items instead of single resources
public interface GraphNodeExpansionCallback extends ResourceCategorizer {

    void addAutomaticResource(Resource resource);

    boolean containsResourceWithUri(String resourceUri);

    ResourceSet getAllResources();

    // String getArcId(String arcType, String sourceId, String targetId);

    GraphDisplay getDisplay();

    Resource getResourceByUri(String value);

    ResourceManager getResourceManager();

    boolean isRestoring();

    /**
     * Displays the specified arc. If the arc is already displayed, nothing
     * changes. If the arc is not visible yet, it is created.
     */
    void showArc(String arcType, String sourceId, String targetId);

}