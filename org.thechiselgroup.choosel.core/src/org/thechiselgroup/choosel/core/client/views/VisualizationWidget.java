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
package org.thechiselgroup.choosel.core.client.views;

import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.views.slots.DefaultSlotMappingInitializer;
import org.thechiselgroup.choosel.core.client.views.slots.SlotMappingConfiguration;

import com.google.gwt.user.client.ui.SimplePanel;

public class VisualizationWidget extends SimplePanel {

    private ViewModel viewModel;

    private ViewContentDisplay contentDisplay;

    public VisualizationWidget(ViewContentDisplay contentDisplay,
            ResourceSet selectedResource, ResourceSet containedResources,
            ResourceSet highlightedResources, ViewItemBehavior viewItemBehavior) {

        assert contentDisplay != null;

        this.contentDisplay = contentDisplay;
        this.viewModel = new DefaultViewModel(contentDisplay,
                new SlotMappingConfiguration(), selectedResource,
                containedResources, highlightedResources,
                new DefaultSlotMappingInitializer(), viewItemBehavior);

        setWidget(contentDisplay.asWidget());
    }

    @Override
    public void setSize(String width, String height) {
        assert width != null;
        assert height != null;

        setWidth(width);
        setHeight(height);

        contentDisplay.asWidget().setWidth(width);
        contentDisplay.asWidget().setHeight(height);

        contentDisplay.checkResize();
    }
}