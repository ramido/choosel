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
package org.thechiselgroup.choosel.client.ui;

import org.thechiselgroup.choosel.client.command.CommandManager;
import org.thechiselgroup.choosel.client.resources.ui.ResourceSetAvatarFactory;
import org.thechiselgroup.choosel.client.windows.Desktop;
import org.thechiselgroup.choosel.client.windows.WindowContent;
import org.thechiselgroup.choosel.client.windows.WindowContentFactory;

public class ImportCSVWindowContentFactory implements WindowContentFactory {

    private Desktop desktop;

    private ResourceSetAvatarFactory defaultDragAvatarFactory;

    private CommandManager commandManager;

    public ImportCSVWindowContentFactory(Desktop desktop,
            ResourceSetAvatarFactory defaultDragAvatarFactory,
            CommandManager commandManager) {

        this.desktop = desktop;
        this.defaultDragAvatarFactory = defaultDragAvatarFactory;
        this.commandManager = commandManager;
    }

    @Override
    public WindowContent createWindowContent() {
        return new ImportCSVWindowContent(defaultDragAvatarFactory,
                commandManager, desktop);
    }
}