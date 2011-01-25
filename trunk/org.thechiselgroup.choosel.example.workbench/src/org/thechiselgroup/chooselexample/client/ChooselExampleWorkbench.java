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
package org.thechiselgroup.chooselexample.client;

import org.thechiselgroup.choosel.core.client.command.AsyncCommandExecutor;
import org.thechiselgroup.choosel.core.client.ui.Action;
import org.thechiselgroup.choosel.core.client.windows.WindowContent;
import org.thechiselgroup.choosel.workbench.client.ChooselWorkbench;
import org.thechiselgroup.choosel.workbench.client.RestrictImporterToOneDataSourceManager;
import org.thechiselgroup.choosel.workbench.client.importer.ImportDialog;
import org.thechiselgroup.choosel.workbench.client.workspace.command.ConfigureSharedViewsDialogCommand;

import com.google.inject.Inject;

public class ChooselExampleWorkbench extends ChooselWorkbench {

    @Inject
    private ConfigureSharedViewsDialogCommand configSharedViewsCommand;

    @Inject
    private AsyncCommandExecutor asyncCommandExecutor;

    @Override
    protected void afterInit() {
        WindowContent content = windowContentProducer
                .createWindowContent(ChooselWorkbench.WINDOW_CONTENT_HELP);

        desktop.createWindow(content, 30, 120, 800, 600);
    }

    private void createImportDialog() {
        Action importAction = addDialogActionToToolbar(DATA_PANEL, "Import",
                new ImportDialog(importer, dataSources));

        new RestrictImporterToOneDataSourceManager(dataSources, importAction)
                .init();
    }

    @Override
    protected void initAuthenticationBar() {
    }

    @Override
    protected void initCustomActions() {
        // addActionToToolbar(WORKSPACE_PANEL, "Load Workspace",
        // "workspace-open",
        // new AsyncCommandToCommandAdapter(configSharedViewsCommand,
        // asyncCommandExecutor));

        if (runsInDevelopmentMode()) {
            addCreateWindowActionToToolbar(DEVELOPER_MODE_PANEL, "Graph",
                    TYPE_GRAPH);
            addCreateWindowActionToToolbar(DEVELOPER_MODE_PANEL,
                    "Circular Bar", TYPE_CIRCULAR_BAR);
            addCreateWindowActionToToolbar(DEVELOPER_MODE_PANEL, "Time",
                    TYPE_TIME);
            addCreateWindowActionToToolbar(DEVELOPER_MODE_PANEL, "Dot",
                    TYPE_DOT);
            addCreateWindowActionToToolbar(DEVELOPER_MODE_PANEL, "Pie Chart",
                    TYPE_PIE);
        }

        addCreateWindowActionToToolbar(VIEWS_PANEL, "Note", WINDOW_CONTENT_NOTE);
        addCreateWindowActionToToolbar(VIEWS_PANEL, "Text", TYPE_TEXT);
        addCreateWindowActionToToolbar(VIEWS_PANEL, "Map", TYPE_MAP);
        addCreateWindowActionToToolbar(VIEWS_PANEL, "Timeline", TYPE_TIMELINE);
        addCreateWindowActionToToolbar(VIEWS_PANEL, "Bar Chart", TYPE_BAR);
        addCreateWindowActionToToolbar(VIEWS_PANEL, "Scatter Plot",
                TYPE_SCATTER);

        createImportDialog();
    }

    @Override
    protected void initWorkspacePanel() {
        initNewWorkspaceAction();
        if (runsInDevelopmentMode()) {
            initLoadWorkspaceAction();
            initSaveWorkspaceAction();
            initShareWorkspaceAction();
        }
    }
}