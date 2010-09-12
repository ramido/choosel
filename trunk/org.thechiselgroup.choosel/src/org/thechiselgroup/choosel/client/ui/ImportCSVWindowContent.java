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
import org.thechiselgroup.choosel.client.windows.AbstractWindowContent;

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

    public ImportCSVWindowContent() {
        super("Import CSV", ChooselInjectionConstants.WINDOW_CONTENT_CSV_IMPORT);
    }

    @Override
    public Widget asWidget() {
        return panel;
    }

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
                // parse content & put into resources
                // new csvparser -->XXX

                // String[] fieldNames = in.readLine().trim().split(",");
                // Set<Resource> resources = new HashSet<Resource>();
                //
                // String line = in.readLine();
                // while (line != null) {
                // String[] fieldValues = line.trim().split(",");
                //
                // // TODO maybe should be if else
                // assert (fieldNames.length == fieldValues.length);
                //
                // Resource resource = new Resource("csv:" + count++);
                // for (int i = 0; i < fieldValues.length; i++) {
                //
                // resource.putValue(fieldNames[i].trim(),
                // fieldValues[i].trim());
                // }
                //
                // resources.add(resource);
                // line = in.readLine();
                // }
                //
                // return resources;
            }
        });

        panel.add(pasteArea, DockPanel.CENTER);
        panel.add(button, DockPanel.SOUTH);
    }
}