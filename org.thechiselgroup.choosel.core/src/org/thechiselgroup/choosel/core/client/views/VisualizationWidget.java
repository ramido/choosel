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

import org.thechiselgroup.choosel.core.client.resources.DefaultResourceSetFactory;
import org.thechiselgroup.choosel.core.client.resources.HasResourceCategorizer;
import org.thechiselgroup.choosel.core.client.resources.ResourceByUriMultiCategorizer;
import org.thechiselgroup.choosel.core.client.resources.ResourceGrouping;
import org.thechiselgroup.choosel.core.client.resources.ResourceMultiCategorizer;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.views.slots.DefaultSlotMappingInitializer;
import org.thechiselgroup.choosel.core.client.views.slots.SlotMappingConfiguration;

import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Facade that facilitates usage of {@link ViewContentDisplay} as {@link Widget}
 * .
 * 
 * @author Lars Grammel
 */
public class VisualizationWidget extends SimplePanel implements
        HasResourceCategorizer, ContainsResourceGrouping {

    private ViewModel viewModel;

    private ViewContentDisplay contentDisplay;

    public VisualizationWidget(ViewContentDisplay contentDisplay,
            ResourceSet selectedResource, ResourceSet highlightedResources,
            ViewItemBehavior viewItemBehavior) {

        assert contentDisplay != null;

        this.contentDisplay = contentDisplay;
        this.viewModel = new DefaultViewModel(contentDisplay,
                new SlotMappingConfiguration(), selectedResource,
                highlightedResources, new DefaultSlotMappingInitializer(),
                viewItemBehavior, new ResourceGrouping(
                        new ResourceByUriMultiCategorizer(),
                        new DefaultResourceSetFactory()));

        setWidget(contentDisplay.asWidget());
    }

    @Override
    public ResourceMultiCategorizer getCategorizer() {
        return viewModel.getResourceGrouping().getCategorizer();
    }

    public ResourceSet getContentResourceSet() {
        return viewModel.getResourceGrouping().getResourceSet();
    }

    @Override
    public ResourceGrouping getResourceGrouping() {
        return viewModel.getResourceGrouping();
    }

    @Override
    public void setCategorizer(ResourceMultiCategorizer newCategorizer) {
        viewModel.getResourceGrouping().setCategorizer(newCategorizer);
    }

    public void setContentResourceSet(ResourceSet contentResourceSet) {
        viewModel.getResourceGrouping().setResourceSet(contentResourceSet);
    }

    @Override
    public void setResourceGrouping(ResourceGrouping resourceGrouping) {
        viewModel.setResourceGrouping(resourceGrouping);
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