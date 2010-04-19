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
package org.thechiselgroup.choosel.client.workspace.command;

import org.thechiselgroup.choosel.client.command.AsyncCommand;
import org.thechiselgroup.choosel.client.util.HasDescription;
import org.thechiselgroup.choosel.client.workspace.Workspace;
import org.thechiselgroup.choosel.client.workspace.WorkspacePersistenceManager;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class LoadWorkspaceCommand implements AsyncCommand, HasDescription {

    private Long workspaceId;

    private String workspaceName;

    private WorkspacePersistenceManager persistenceManager;

    public LoadWorkspaceCommand(Long workspaceId, String workspaceName,
	    WorkspacePersistenceManager persistenceManager) {

	this.workspaceId = workspaceId;
	this.workspaceName = workspaceName;
	this.persistenceManager = persistenceManager;
    }

    @Override
    public String getDescription() {
	return "Loading workspace '" + workspaceName + "'";
    }

    @Override
    public void execute(final AsyncCallback<Void> callback) {
	persistenceManager.loadWorkspace(workspaceId,
		new AsyncCallback<Workspace>() {
		    @Override
		    public void onFailure(Throwable caught) {
			callback.onFailure(caught);
		    }

		    @Override
		    public void onSuccess(Workspace workspace) {
			callback.onSuccess(null);
		    }
		});
    }

}