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
/**
 * 
 */
package org.thechiselgroup.choosel.client.workspace.command;


import org.thechiselgroup.choosel.client.workspace.WorkspaceManager;

import com.google.gwt.user.client.Command;
import com.google.inject.Inject;

public class NewWorkspaceCommand implements Command {

    private final WorkspaceManager workspaceManager;

    @Inject
    public NewWorkspaceCommand(WorkspaceManager workspaceManager) {
	this.workspaceManager = workspaceManager;
    }

    @Override
    public void execute() {
	workspaceManager.createNewWorkspace();
    }
}