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

import static org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants.TYPE_BAR;
import static org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants.TYPE_CIRCULAR_BAR;
import static org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants.TYPE_DOT;
import static org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants.TYPE_GRAPH;
import static org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants.TYPE_MAP;
import static org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants.TYPE_PIE;
import static org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants.TYPE_SCATTER;
import static org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants.TYPE_TEXT;
import static org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants.TYPE_TIME;
import static org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants.TYPE_TIMELINE;
import static org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants.WINDOW_CONTENT_NOTE;

import org.thechiselgroup.choosel.client.ChooselApplication;
import org.thechiselgroup.choosel.client.importer.ImportDialog;
import org.thechiselgroup.choosel.client.test.TestResourceSetFactory;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;

public class ChooselExampleApplication extends ChooselApplication {

    public static final String DATA_PANEL = "data";

    // TODO change into command
    private void addTestDataSourceButton() {
        Button button = new Button("Test-Data");

        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                dataSourceResourceSetsPresenter
                        .addResourceSet(TestResourceSetFactory
                                .addTestData(createResourceSet()));
                dataSourceResourceSetsPresenter
                        .addResourceSet(TestResourceSetFactory
                                .addGraphTestData(createResourceSet()));
            }

        });

        addWidget(DEVELOPER_MODE_PANEL, button);
    }

    @Override
    protected void initCustomActions() {
        if (runsInDevelopmentMode()) {
            addTestDataSourceButton();

            addCreateWindowActionToToolbar(DEVELOPER_MODE_PANEL, "Graph",
                    TYPE_GRAPH);
            addCreateWindowActionToToolbar(DEVELOPER_MODE_PANEL,
                    "Circular Bar", TYPE_CIRCULAR_BAR);
            addCreateWindowActionToToolbar(DEVELOPER_MODE_PANEL, "Time",
                    TYPE_TIME);
            addCreateWindowActionToToolbar(DEVELOPER_MODE_PANEL, "Dot",
                    TYPE_DOT);
        }

        addCreateWindowActionToToolbar(VIEWS_PANEL, "Note", WINDOW_CONTENT_NOTE);
        addCreateWindowActionToToolbar(VIEWS_PANEL, "Text", TYPE_TEXT);
        addCreateWindowActionToToolbar(VIEWS_PANEL, "Map", TYPE_MAP);
        addCreateWindowActionToToolbar(VIEWS_PANEL, "Timeline", TYPE_TIMELINE);
        addCreateWindowActionToToolbar(VIEWS_PANEL, "Bar Chart", TYPE_BAR);
        addCreateWindowActionToToolbar(VIEWS_PANEL, "Pie Chart", TYPE_PIE);
        addCreateWindowActionToToolbar(VIEWS_PANEL, "Scatter Plot",
                TYPE_SCATTER);

        addDialogActionToToolbar(DATA_PANEL, "Import", new ImportDialog(
                importer, dataSourceResourceSetsPresenter));
        addWidget(DATA_PANEL, dataSourceResourceSetsPresenter.asWidget());
    }

    @Override
    protected void initCustomPanels() {
        addToolbarPanel(DATA_PANEL, "Data");
        addToolbarPanel(VIEWS_PANEL, "Views");
    }
}