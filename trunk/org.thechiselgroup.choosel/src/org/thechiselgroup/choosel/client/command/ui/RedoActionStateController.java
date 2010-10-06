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
package org.thechiselgroup.choosel.client.command.ui;

import org.thechiselgroup.choosel.client.command.CommandAddedEvent;
import org.thechiselgroup.choosel.client.command.CommandAddedEventHandler;
import org.thechiselgroup.choosel.client.command.CommandManager;
import org.thechiselgroup.choosel.client.command.CommandManagerClearedEvent;
import org.thechiselgroup.choosel.client.command.CommandManagerClearedEventHandler;
import org.thechiselgroup.choosel.client.command.CommandRedoneEvent;
import org.thechiselgroup.choosel.client.command.CommandRedoneEventHandler;
import org.thechiselgroup.choosel.client.command.CommandUndoneEvent;
import org.thechiselgroup.choosel.client.command.CommandUndoneEventHandler;
import org.thechiselgroup.choosel.client.command.UndoableCommand;
import org.thechiselgroup.choosel.client.ui.Action;
import org.thechiselgroup.choosel.client.util.HasDescription;
import org.thechiselgroup.choosel.client.util.Initializable;

public class RedoActionStateController implements Initializable {

    private class CommandManagerHandler implements CommandAddedEventHandler,
            CommandRedoneEventHandler, CommandUndoneEventHandler,
            CommandManagerClearedEventHandler {

        @Override
        public void onCleared(CommandManagerClearedEvent event) {
            updateButtonState();
        }

        @Override
        public void onCommandAdded(CommandAddedEvent event) {
            updateButtonState();
        }

        @Override
        public void onCommandRedone(CommandRedoneEvent commandRedoneEvent) {
            updateButtonState();
        }

        @Override
        public void onCommandUndone(CommandUndoneEvent commandUndoneEvent) {
            updateButtonState();
        }
    }

    private final CommandManager commandManager;

    private CommandManagerHandler commandManagerHandler;

    private final Action action;

    public RedoActionStateController(CommandManager commandManager,
            Action action) {

        this.commandManager = commandManager;
        this.action = action;
    }

    private String getRedoCommandDescription() {
        if (!commandManager.canRedo()) {
            return "";
        }

        UndoableCommand redoCommand = commandManager.getRedoCommand();

        if (!(redoCommand instanceof HasDescription)) {
            return "";
        }

        return ((HasDescription) redoCommand).getDescription();
    }

    @Override
    public void init() {
        updateButtonState();

        commandManagerHandler = new CommandManagerHandler();

        commandManager.addHandler(CommandRedoneEvent.TYPE,
                commandManagerHandler);
        commandManager.addHandler(CommandUndoneEvent.TYPE,
                commandManagerHandler);
        commandManager
                .addHandler(CommandAddedEvent.TYPE, commandManagerHandler);
        commandManager.addHandler(CommandManagerClearedEvent.TYPE,
                commandManagerHandler);
    }

    private void updateButtonState() {
        action.setEnabled(commandManager.canRedo());
        action.setDescription(getRedoCommandDescription());
    }
}
