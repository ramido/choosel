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
package org.thechiselgroup.choosel.core.client.resources.action;

import org.thechiselgroup.choosel.core.client.command.CommandManager;
import org.thechiselgroup.choosel.core.client.command.UndoableCommand;
import org.thechiselgroup.choosel.core.client.label.LabelProvider;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.resources.ResourceSetFactory;
import org.thechiselgroup.choosel.core.client.resources.command.AddResourceSetToViewCommand;
import org.thechiselgroup.choosel.core.client.resources.ui.popup.PopupResourceSetAvatarFactory.Action;
import org.thechiselgroup.choosel.core.client.views.View;

public class CreateSetAction implements Action {

    private CommandManager commandManager;

    private ResourceSetFactory resourceSetFactory;

    private LabelProvider resourceSetLabelFactory;

    public CreateSetAction(ResourceSetFactory resourceSetFactory,
            LabelProvider resourceSetLabelFactory, CommandManager commandManager) {

        this.resourceSetFactory = resourceSetFactory;
        this.resourceSetLabelFactory = resourceSetLabelFactory;
        this.commandManager = commandManager;
    }

    protected UndoableCommand createCommand(ResourceSet resources,
            final View view) {

        final ResourceSet newResources = resourceSetFactory.createResourceSet();
        newResources.setLabel(resourceSetLabelFactory.nextLabel());
        newResources.addAll(resources);

        return new AddResourceSetToViewCommand(view.getResourceModel(),
                newResources, "Create set '" + newResources.getLabel()
                        + "' from selection") {

            @Override
            public void performExecute() {
                view.getSelectionModel().addSelectionSet(newResources);
            }
        };
    }

    @Override
    public void execute(ResourceSet resources, View view) {
        commandManager.execute(createCommand(resources, view));
    }

    @Override
    public String getLabel() {
        return "Create set from selection";
    }
}