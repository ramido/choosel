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
package org.thechiselgroup.choosel.client.importer;

import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.ui.dialog.AbstractDialog;
import org.thechiselgroup.choosel.client.views.ResourceSetContainer;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ImportDialog extends AbstractDialog {

    private static final String CSS_IMPORT_PANEL = "choosel-ImportPanel";

    private static final String CSS_IMPORT_PANEL_DATA_SET_NAME = "choosel-ImportPanel-DataSetName";

    private static final String CSS_IMPORT_PANEL_LABEL = "choosel-ImportPanel-Label";

    private TextArea pasteArea;

    private ResourceSetContainer targetContainer;

    private Importer importer;

    private TextBox nameTextBox;

    public ImportDialog(Importer importer, ResourceSetContainer targetContainer) {
        assert targetContainer != null;
        assert importer != null;

        this.importer = importer;
        this.targetContainer = targetContainer;
    }

    @Override
    public void cancel() {
    }

    @Override
    public Widget getContent() {
        VerticalPanel panel = new VerticalPanel();
        panel.addStyleName(CSS_IMPORT_PANEL);

        FlowPanel namePanel = new FlowPanel();
        namePanel.addStyleName(CSS_IMPORT_PANEL_DATA_SET_NAME);
        Label nameLabel = new Label("Name of data set:");
        nameLabel.setStyleName(CSS_IMPORT_PANEL_LABEL);
        namePanel.add(nameLabel);
        nameTextBox = new TextBox();
        namePanel.add(nameTextBox);
        panel.add(namePanel);

        Label contentLabel = new Label("Paste CSV data below:");
        contentLabel.addStyleName(CSS_IMPORT_PANEL_LABEL);
        panel.add(contentLabel);

        pasteArea = new TextArea();
        panel.add(pasteArea);
        panel.setCellHeight(pasteArea, "100%");

        return panel;
    }

    @Override
    public String getHeader() {
        return "Import CSV";
    }

    @Override
    public String getOkayButtonLabel() {
        return "Import";
    }

    @Override
    public String getWindowTitle() {
        return "Import";
    }

    @Override
    public void okay() {
        try {
            String pastedText = pasteArea.getText();

            if (pastedText.length() > 50000) {
                throw new ParseException(
                        "The pasted text is too big. This demo supports only up to 50000 characters in the pasted text.");
            }

            StringTable parsedRows = new CSVStringTableParser()
                    .parse(pastedText);

            if (parsedRows.getColumnCount() > 15) {
                throw new ParseException(
                        "Too many columns. This demo supports only up to 15 columns.");
            }
            if (parsedRows.getRowCount() > 200) {
                throw new ParseException(
                        "Too many rows. This demo supports only up to 200 rows.");
            }

            ResourceSet parsedResources = importer.createResources(parsedRows);
            parsedResources.setLabel(nameTextBox.getText());
            targetContainer.addResourceSet(parsedResources);
        } catch (ParseException e) {
            // TODO correct exception handling
            throw new RuntimeException(e);
        }
    }
}