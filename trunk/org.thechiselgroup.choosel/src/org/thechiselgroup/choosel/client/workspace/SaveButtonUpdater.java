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
package org.thechiselgroup.choosel.client.workspace;

import org.thechiselgroup.choosel.client.ui.HasEnabledState;
import org.thechiselgroup.choosel.client.util.Disposable;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasText;

public class SaveButtonUpdater implements Disposable {

    private HasEnabledState hasEnabledState;

    private HasText hasText;

    private HandlerRegistration savingStateHandlerRegistration;

    private WorkspaceManager workspaceManager;

    private HandlerRegistration workspaceSwitchedHandler;

    private Focusable focusable;

    public <T extends HasText & Focusable> SaveButtonUpdater(
            WorkspaceManager workspaceManager, T button,
            HasEnabledState hasEnabledState) {

        this.workspaceManager = workspaceManager;
        this.hasText = button;
        this.hasEnabledState = hasEnabledState;
        this.focusable = button;
    }

    protected void delayedSwitchToNotSaved() {
        // we use timers for now to switch back to not_saved, once
        // everything
        // is done within commands, we can use those to set the state of the
        // button
        new Timer() {
            @Override
            public void run() {
                workspaceManager.getWorkspace().setSavingState(
                        WorkspaceSavingState.NOT_SAVED);
            }
        }.schedule(2000);
    }

    @Override
    public void dispose() {
        workspaceSwitchedHandler.removeHandler();
        savingStateHandlerRegistration.removeHandler();
    }

    public void init() {
        registerWorkspaceSwitchedHandler();
        setWorkspace(workspaceManager.getWorkspace());
    }

    private void registerWorkspaceSavingStateHandler(Workspace workspace) {
        savingStateHandlerRegistration = workspace
                .addWorkspaceSavingStateChangeHandler(new WorkspaceSavingStateChangedEventHandler() {
                    @Override
                    public void onWorkspaceSavingStateChanged(
                            WorkspaceSavingStateChangedEvent event) {
                        update(event.getState());
                    }
                });
    }

    private void registerWorkspaceSwitchedHandler() {
        workspaceSwitchedHandler = workspaceManager
                .addSwitchedWorkspaceEventHandler(new WorkspaceSwitchedEventHandler() {
                    @Override
                    public void onWorkspaceSwitched(WorkspaceSwitchedEvent event) {
                        setWorkspace(event.getWorkspace());
                    }
                });
    }

    private void setWorkspace(Workspace workspace) {
        registerWorkspaceSavingStateHandler(workspace);
        update(workspace.getSavingState());
    }

    private void update(String label, boolean enabled) {
        hasText.setText(label);
        hasEnabledState.setEnabled(enabled);

        if (!enabled) {
            focusable.setFocus(false);
        }
    }

    private void update(WorkspaceSavingState state) {
        assert state != null;

        switch (state) {
        case NOT_SAVED: {
            update("Save", true);
        }
            break;
        case SAVED: {
            update("Saved", false);
            delayedSwitchToNotSaved();
        }
            break;
        case SAVING: {
            update("Saving", false);
        }
            break;
        }
    }

}
