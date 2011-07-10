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
package org.thechiselgroup.choosel.core.client.visualization.behaviors;

import org.thechiselgroup.choosel.core.client.command.CommandManager;
import org.thechiselgroup.choosel.core.client.resources.command.SwitchSelectionCommand;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItem;
import org.thechiselgroup.choosel.core.client.visualization.model.extensions.SelectionModel;

/**
 * Sub-class of SwitchSelectionOnClickViewItemBehaviour that uses a command to
 * switch the selection. This is done so as to not break previous API that does
 * not require a command manager to change selections (for simple choosel
 * applications that don't have a workbench, for example).
 * 
 * @author Del
 * 
 */
public class CommandDrivenSwitchSelectionOnClickViewItemBehaviour extends
        SwitchSelectionOnClickViewItemBehavior {

    private CommandManager commandManager;

    /**
     * @param selectionModel
     * @param commandManager
     */
    public CommandDrivenSwitchSelectionOnClickViewItemBehaviour(
            SelectionModel selectionModel, CommandManager commandManager) {
        super(selectionModel);
        assert commandManager != null : "Cannot run command on a null command manager";
        this.commandManager = commandManager;
    }

    @Override
    protected void switchSelection(VisualItem viewItem) {
        SwitchSelectionCommand command = new SwitchSelectionCommand(
                viewItem.getResources(), getSelectionModel());
        commandManager.execute(command);

    }

}
