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
package org.thechiselgroup.choosel.client.resources.ui;

import java.util.Map;

import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.ui.Presenter;
import org.thechiselgroup.choosel.client.ui.WidgetAdaptable;
import org.thechiselgroup.choosel.client.util.Disposable;

public interface ResourceSetsPresenter extends Presenter, Disposable,
        WidgetAdaptable {

    void addResourceSet(ResourceSet resources);

    // XXX HACK TODO remove
    Map<ResourceSet, ResourceSetAvatar> getAvatars();

    void removeResourceSet(ResourceSet resources);

    void replaceResourceSet(ResourceSet oldResources, ResourceSet newResources);

    void setResourceSetEnabled(ResourceSet resource, boolean enabled);

}