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
package org.thechiselgroup.choosel.client.views;

import org.thechiselgroup.choosel.client.resources.CombinedResourceSet;
import org.thechiselgroup.choosel.client.resources.CountingResourceSet;
import org.thechiselgroup.choosel.client.resources.DefaultResourceSet;
import org.thechiselgroup.choosel.client.resources.DelegatingReadableResourceSet;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ResourceSetDelegateChangedEventHandler;
import org.thechiselgroup.choosel.client.resources.SwitchingResourceSet;

import com.google.gwt.event.shared.HandlerRegistration;

public class HoverModel extends DelegatingReadableResourceSet {

    private SwitchingResourceSet highlightedResourceSetContainer;

    private ResourceSet highlightedSingleResources;

    public HoverModel() {
        super(new CombinedResourceSet(new DefaultResourceSet()));

        highlightedResourceSetContainer = new SwitchingResourceSet();

        /*
         * We use a counting resource set, because elements might get removed
         * from the set after they have been added again, e.g. when moving the
         * mouse from over a resource item with popup to over a resource set and
         * the popup removes the resource a bit later.
         */
        highlightedSingleResources = new CountingResourceSet();

        getCombinedResourceSet()
                .addResourceSet(highlightedResourceSetContainer);
        getCombinedResourceSet().addResourceSet(highlightedSingleResources);
    }

    public HandlerRegistration addEventHandler(
            ResourceSetDelegateChangedEventHandler handler) {

        return highlightedResourceSetContainer.addEventHandler(handler);
    }

    public void addHighlightedResources(ResourceSet resource) {
        highlightedSingleResources.addAll(resource);
    }

    private CombinedResourceSet getCombinedResourceSet() {
        return (CombinedResourceSet) delegate;
    }

    public void removeHighlightedResources(ResourceSet resources) {
        highlightedSingleResources.removeAll(resources);
    }

    public void setHighlightedResourceSet(ResourceSet resourceSet) {
        highlightedResourceSetContainer.setDelegate(resourceSet);
    }

}