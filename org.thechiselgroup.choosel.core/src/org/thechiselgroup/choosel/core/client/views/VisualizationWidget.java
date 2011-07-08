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

import java.util.logging.Logger;

import org.thechiselgroup.choosel.core.client.resources.DefaultResourceSetFactory;
import org.thechiselgroup.choosel.core.client.resources.HasResourceCategorizer;
import org.thechiselgroup.choosel.core.client.resources.ResourceByUriMultiCategorizer;
import org.thechiselgroup.choosel.core.client.resources.ResourceGrouping;
import org.thechiselgroup.choosel.core.client.resources.ResourceMultiCategorizer;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.views.model.ContainsResourceGrouping;
import org.thechiselgroup.choosel.core.client.views.model.DefaultVisualizationModel;
import org.thechiselgroup.choosel.core.client.views.model.Slot;
import org.thechiselgroup.choosel.core.client.views.model.ViewContentDisplay;
import org.thechiselgroup.choosel.core.client.views.model.VisualItemBehavior;
import org.thechiselgroup.choosel.core.client.views.model.VisualizationModel;
import org.thechiselgroup.choosel.core.client.views.resolvers.ViewItemValueResolver;

import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Facade that facilitates usage of {@link ViewContentDisplay} as {@link Widget}
 * .
 * 
 * @author Lars Grammel
 */
public class VisualizationWidget<T extends ViewContentDisplay> extends
        SimplePanel implements HasResourceCategorizer, ContainsResourceGrouping {

    private VisualizationModel viewModel;

    private T contentDisplay;

    public VisualizationWidget(T contentDisplay, ResourceSet selectedResource,
            ResourceSet highlightedResources, VisualItemBehavior viewItemBehavior) {

        assert contentDisplay != null;

        this.contentDisplay = contentDisplay;
        this.viewModel = new DefaultVisualizationModel(contentDisplay, selectedResource,
                highlightedResources, viewItemBehavior, new ResourceGrouping(
                        new ResourceByUriMultiCategorizer(),
                        new DefaultResourceSetFactory()), Logger.getLogger(""));

        setWidget(contentDisplay.asWidget());
    }

    @Override
    public ResourceMultiCategorizer getCategorizer() {
        return viewModel.getResourceGrouping().getCategorizer();
    }

    public T getContentDisplay() {
        return contentDisplay;
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

    public void setPropertyValue(String property, Object value) {
        viewModel.getViewContentDisplay().setPropertyValue(property, value);
    }

    public void setResolver(Slot slot, ViewItemValueResolver resolver) {
        viewModel.setResolver(slot, resolver);
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