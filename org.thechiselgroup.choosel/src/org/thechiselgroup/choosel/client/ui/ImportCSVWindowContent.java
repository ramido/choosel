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

import java.io.IOException;

import org.thechiselgroup.choosel.client.command.CommandManager;
import org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants;
import org.thechiselgroup.choosel.client.resources.DefaultResourceSet;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ui.ResourceSetAvatarFactory;
import org.thechiselgroup.choosel.client.resources.ui.ResourceSetAvatarResourceSetsPresenter;
import org.thechiselgroup.choosel.client.resources.ui.ResourceSetsPresenter;
import org.thechiselgroup.choosel.client.util.CSVParser;
import org.thechiselgroup.choosel.client.windows.AbstractWindowContent;
import org.thechiselgroup.choosel.client.windows.CreateWindowCommand;
import org.thechiselgroup.choosel.client.windows.Desktop;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class ImportCSVWindowContent extends AbstractWindowContent {

    private static final String IMPORT_CSV_CSS = "importCSV";

    private TextArea pasteArea;

    private DockPanel panel;

    private ResourceSetAvatarFactory defaultDragAvatarFactory;

    private CommandManager commandManager;

    private Desktop desktop;

    public ImportCSVWindowContent(
            ResourceSetAvatarFactory defaultDragAvatarFactory,
            CommandManager commandManager, Desktop desktop) {

        super("Import CSV", ChooselInjectionConstants.WINDOW_CONTENT_CSV_IMPORT);

        this.defaultDragAvatarFactory = defaultDragAvatarFactory;
        this.commandManager = commandManager;
        this.desktop = desktop;
    }

    @Override
    public Widget asWidget() {
        return panel;
    }

    // TODO use dialog panel
    @Override
    public void init() {
        super.init();

        panel = new DockPanel();

        pasteArea = new TextArea();
        pasteArea.setStyleName(IMPORT_CSV_CSS);

        Button button = new Button("parse");
        button.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                try {
                    ResourceSet resources = parseResourcesFromCSV(pasteArea
                            .getText());

                    // TODO show dnd window with parsed data
                    resources.toString();

                    String title = "CSV Import";
                    final ResourceSetsPresenter presenter = new ResourceSetAvatarResourceSetsPresenter(
                            defaultDragAvatarFactory);
                    presenter.init();

                    commandManager.execute(new CreateWindowCommand(desktop,
                            new AbstractWindowContent(title, "TODO") {
                                @Override
                                public Widget asWidget() {
                                    return presenter.asWidget();
                                }
                            }));

                    presenter.addResourceSet(resources);

                } catch (IOException e) {
                    // TODO choosel exception handling
                    e.printStackTrace();
                }
            }

        });

        panel.add(pasteArea, DockPanel.CENTER);
        panel.add(button, DockPanel.SOUTH);
    }

    // TODO move
    /*
     * TODO later: resource set not the perfect result, rather structure and
     * table of String values (since they should not be parsed at this point)
     */
    public ResourceSet parseResourcesFromCSV(String csvText) throws IOException {
        CSVParser parser = new CSVParser(',');

        String[] lines = csvText.split("\n");

        if (lines.length == 0) {
            throw new IOException("no content to parse");
        }

        ResourceSet resources = new DefaultResourceSet();
        resources.setLabel("csv-import"); // TODO changeable, inc number

        String[] attributeNames = parser.parseLine(lines[0]);

        for (int i = 1; i < lines.length; i++) {
            String uri = "csv:" + i; // TODO improved uri generation
            Resource resource = new Resource(uri);

            String[] values = parser.parseLine(lines[i]);
            for (int j = 0; j < values.length; j++) {
                resource.putValue(attributeNames[j], values[j]);
            }

            resources.add(resource);
        }

        return resources;
    }
}