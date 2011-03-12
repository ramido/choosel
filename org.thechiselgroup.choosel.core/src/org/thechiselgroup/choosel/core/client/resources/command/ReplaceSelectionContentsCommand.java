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
package org.thechiselgroup.choosel.core.client.resources.command;

import java.util.List;

import org.thechiselgroup.choosel.core.client.command.UndoableCommand;
import org.thechiselgroup.choosel.core.client.resources.Resource;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.util.HasDescription;
import org.thechiselgroup.choosel.core.client.views.ViewModel;

public class ReplaceSelectionContentsCommand implements UndoableCommand,
        HasDescription {

    private List<Resource> originalTargetResources;

    protected ResourceSet resources;

    private final ViewModel viewModel;

    public ReplaceSelectionContentsCommand(ResourceSet resources,
            ViewModel viewModel) {

        assert resources != null;
        assert viewModel != null;

        this.resources = resources;
        this.viewModel = viewModel;
    }

    @Override
    public void execute() {
        if (originalTargetResources == null) {
            originalTargetResources = getSelection().toList();
        }

        getSelection().clear();

        ResourceSet selectedResourcesFromView = resources;
        selectedResourcesFromView.retainAll(viewModel.getResourceModel()
                .getResources());

        getSelection().addAll(selectedResourcesFromView);

        assert getSelection().containsAll(selectedResourcesFromView);
        assert getSelection().size() == selectedResourcesFromView.size();
    }

    // TODO add view name once available
    @Override
    public String getDescription() {
        return "Replace selection in view with resources from '"
                + resources.getLabel() + "'";
    }

    public ResourceSet getResources() {
        return resources;
    }

    private ResourceSet getSelection() {
        return viewModel.getSelectionModel().getSelection();
    }

    public ViewModel getViewModel() {
        return viewModel;
    }

    @Override
    public void undo() {
        getSelection().clear();
        getSelection().addAll(originalTargetResources);
    }

}