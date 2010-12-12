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
import org.thechiselgroup.choosel.client.resources.ResourceManager;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.ui.widget.graph.GraphDisplay;
import org.thechiselgroup.choosel.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.client.views.ResourceItem;

// TODO better separation of concern - introduce factories for the expanders
// TODO use resource items instead of single resources
// TODO split up interface
public interface GraphNodeExpansionCallback {

    void addAutomaticResource(Resource resource);

    boolean containsResourceWithUri(String resourceUri);

    ResourceSet getAllResources();

    String getCategory(Resource resource);

    GraphDisplay getDisplay();

    Resource getResourceByUri(String value);

    LightweightCollection<ResourceItem> getResourceItems(
            Iterable<Resource> resources);

    ResourceManager getResourceManager();

    boolean isRestoring();

    void updateArcsForResourceItems(
            LightweightCollection<ResourceItem> resourceItems);

}