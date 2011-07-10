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
package org.thechiselgroup.choosel.workbench.client.ui.configuration;

import org.thechiselgroup.choosel.core.client.command.CommandManager;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.resources.command.ReplaceSelectionCommand;
import org.thechiselgroup.choosel.core.client.resources.ui.ResourceSetAvatarFactory;
import org.thechiselgroup.choosel.core.client.visualization.ViewAccessor;
import org.thechiselgroup.choosel.core.client.visualization.model.extensions.SelectionModel;
import org.thechiselgroup.choosel.dnd.client.resources.SelectionResourceSetAvatarFactory;

/**
 * Encapsulates the setting of resource sets inside a command.
 * 
 * @author Del
 * 
 */
public class CommandDrivenSelectionResourceSetAvatarFactory extends
        SelectionResourceSetAvatarFactory implements ResourceSetAvatarFactory {

    private CommandManager commandManager;

    /**
     * @param delegate
     * @param viewAccessor
     */
    public CommandDrivenSelectionResourceSetAvatarFactory(
            ResourceSetAvatarFactory delegate, ViewAccessor viewAccessor,
            CommandManager commandManager) {
        super(delegate, viewAccessor);
        this.commandManager = commandManager;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.thechiselgroup.choosel.dnd.client.resources.
     * SelectionResourceSetAvatarFactory
     * #setSelection(org.thechiselgroup.choosel.
     * core.client.views.model.SelectionModel,
     * org.thechiselgroup.choosel.core.client.resources.ResourceSet)
     */
    @Override
    protected void setSelection(SelectionModel selectionModel,
            ResourceSet resourceSet) {
        ResourceSet result = resourceSet;
        if (resourceSet.equals(selectionModel.getSelection())) {
            result = null;
        }
        commandManager.execute(new ReplaceSelectionCommand(selectionModel,
                result));
    }
}
