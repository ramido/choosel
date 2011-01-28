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
package org.thechiselgroup.choosel.example.workbench.client;

import org.thechiselgroup.choosel.core.client.command.AsyncCommandExecutor;
import org.thechiselgroup.choosel.core.client.ui.Action;
import org.thechiselgroup.choosel.core.client.windows.WindowContent;
import org.thechiselgroup.choosel.visualization_component.chart.client.barchart.BarChartVisualization;
import org.thechiselgroup.choosel.visualization_component.chart.client.piechart.PieChartVisualization;
import org.thechiselgroup.choosel.visualization_component.chart.client.scatterplot.ScatterPlotVisualization;
import org.thechiselgroup.choosel.visualization_component.graph.client.GraphVisualization;
import org.thechiselgroup.choosel.visualization_component.map.client.MapVisualization;
import org.thechiselgroup.choosel.visualization_component.text.client.TextVisualization;
import org.thechiselgroup.choosel.visualization_component.timeline.client.TimelineVisualization;
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
                    GraphVisualization.ID);
            // addCreateWindowActionToToolbar(DEVELOPER_MODE_PANEL,
            // "Circular Bar", TYPE_CIRCULAR_BAR);
            // addCreateWindowActionToToolbar(DEVELOPER_MODE_PANEL, "Time",
            // TYPE_TIME);
            // addCreateWindowActionToToolbar(DEVELOPER_MODE_PANEL, "Dot",
            // TYPE_DOT);
            addCreateWindowActionToToolbar(DEVELOPER_MODE_PANEL, "Pie Chart",
                    PieChartVisualization.ID);
        }

        addCreateWindowActionToToolbar(VIEWS_PANEL, "Note", WINDOW_CONTENT_NOTE);
        addCreateWindowActionToToolbar(VIEWS_PANEL, "Text",
                TextVisualization.ID);
        addCreateWindowActionToToolbar(VIEWS_PANEL, "Map", MapVisualization.ID);
        addCreateWindowActionToToolbar(VIEWS_PANEL, "Timeline",
                TimelineVisualization.ID);
        addCreateWindowActionToToolbar(VIEWS_PANEL, "Bar Chart",
                BarChartVisualization.ID);
        addCreateWindowActionToToolbar(VIEWS_PANEL, "Scatter Plot",
                ScatterPlotVisualization.ID);

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