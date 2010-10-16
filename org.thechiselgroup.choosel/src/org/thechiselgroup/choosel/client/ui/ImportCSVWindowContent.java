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

import org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants;
import org.thechiselgroup.choosel.client.importer.CSVStringTableParser;
import org.thechiselgroup.choosel.client.importer.Importer;
import org.thechiselgroup.choosel.client.importer.ParseException;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ui.ResourceSetAvatarResourceSetsPresenter;
import org.thechiselgroup.choosel.client.windows.AbstractWindowContent;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class ImportCSVWindowContent extends AbstractWindowContent {

    private static final String IMPORT_CSV_CSS = "importCSV";

    private TextArea pasteArea;

    private DialogPanel panel;

    private Importer importer;

    private ResourceSetAvatarResourceSetsPresenter dataSourcesPanel;

    public ImportCSVWindowContent(Importer importer,
            ResourceSetAvatarResourceSetsPresenter dataSourcesPanel) {

        super("Import CSV", ChooselInjectionConstants.WINDOW_CONTENT_CSV_IMPORT);

        this.importer = importer;
        this.dataSourcesPanel = dataSourcesPanel;
    }

    @Override
    public Widget asWidget() {
        return panel;
    }

    // TODO use dialog panel
    @Override
    public void init() {
        super.init();

        panel = new DialogPanel();
        panel.setHeader("Paste CSV content");

        pasteArea = new TextArea();
        panel.setContent(pasteArea);
        pasteArea.addStyleName(IMPORT_CSV_CSS);

        Button button = panel.createButton("parse");
        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                try {
                    ResourceSet resources = importer
                            .createResources(new CSVStringTableParser()
                                    .parse(pasteArea.getText()));

                    dataSourcesPanel.addResourceSet(resources);
                } catch (ParseException e) {
                    // TODO choosel exception handling
                    e.printStackTrace();
                }
            }

        });

    }
}