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
import org.thechiselgroup.choosel.client.resources.ui.ResourceSetAvatarResourceSetsPresenter;
import org.thechiselgroup.choosel.client.ui.dialog.AbstractDialog;

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

    private ResourceSetAvatarResourceSetsPresenter presenter;

    private Importer importer;

    public ImportDialog(Importer importer,
            ResourceSetAvatarResourceSetsPresenter presenter) {

        this.importer = importer;
        this.presenter = presenter;
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
        namePanel.add(new TextBox());
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
            StringTable parsedRows = new CSVStringTableParser()
                    .parse(pastedText);
            ResourceSet parsedResources = importer.createResources(parsedRows);
            presenter.addResourceSet(parsedResources);
        } catch (ParseException e) {
            // TODO correct exception handling
            throw new RuntimeException(e);
        }
    }
}