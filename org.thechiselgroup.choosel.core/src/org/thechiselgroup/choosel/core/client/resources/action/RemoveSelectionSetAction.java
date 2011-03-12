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
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.resources.command.RemoveSelectionSetFromViewCommand;
import org.thechiselgroup.choosel.core.client.resources.ui.popup.PopupResourceSetAvatarFactory.Action;
import org.thechiselgroup.choosel.core.client.views.SelectionModel;
import org.thechiselgroup.choosel.core.client.views.ViewModel;

public class RemoveSelectionSetAction implements Action {

    private CommandManager commandManager;

    public RemoveSelectionSetAction(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    protected UndoableCommand createCommand(ResourceSet resources,
            SelectionModel selectionModel) {

        return new RemoveSelectionSetFromViewCommand(selectionModel, resources,
                "Remove set '" + resources.getLabel() + "' from selection");
    }

    @Override
    public void execute(ResourceSet resources, ViewModel viewModel) {
        commandManager.execute(createCommand(resources,
                viewModel.getSelectionModel()));
    }

    @Override
    public String getLabel() {
        return "Remove selection set from view";
    }
}