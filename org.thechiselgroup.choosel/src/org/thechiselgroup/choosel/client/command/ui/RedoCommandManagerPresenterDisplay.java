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
package org.thechiselgroup.choosel.client.command.ui;

import org.thechiselgroup.choosel.client.command.ui.CommandManagerPresenter.CommandManagerPresenterDisplay;
import org.thechiselgroup.choosel.client.ui.ImageButton;
import org.thechiselgroup.choosel.client.ui.WidgetFactory;
import org.thechiselgroup.choosel.client.ui.popup.DefaultPopupManager;
import org.thechiselgroup.choosel.client.ui.popup.PopupManager;
import org.thechiselgroup.choosel.client.ui.popup.PopupManagerFactory;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class RedoCommandManagerPresenterDisplay implements
        CommandManagerPresenterDisplay {

    private ImageButton redoButton;

    private Label redoLabel;

    @Inject
    public RedoCommandManagerPresenterDisplay(
            PopupManagerFactory popupManagerFactory) {

        assert popupManagerFactory != null;

        initRedoButton(popupManagerFactory);
    }

    @Override
    public HasClickHandlers getClickHandlers() {
        return redoButton;
    }

    public ImageButton getRedoButton() {
        return redoButton;
    }

    private void initRedoButton(PopupManagerFactory popupManagerFactory) {
        redoButton = ImageButton.createImageButton("edit-redo");
        redoLabel = new Label();
        PopupManager popupManager = popupManagerFactory
                .createPopupManager(new WidgetFactory() {
                    @Override
                    public Widget createWidget() {
                        return redoLabel;
                    }
                });
        DefaultPopupManager.linkManagerToSource(popupManager, redoButton);
    }

    @Override
    public void setButtonEnabled(boolean enabled) {
        redoButton.setEnabled(enabled);
    }

    @Override
    public void setCommandDescription(String commandDescription) {
        redoLabel.setText("redo " + commandDescription);
    }
}