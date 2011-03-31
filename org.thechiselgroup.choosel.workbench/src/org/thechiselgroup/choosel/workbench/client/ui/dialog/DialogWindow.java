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
package org.thechiselgroup.choosel.workbench.client.ui.dialog;

import org.thechiselgroup.choosel.core.client.ui.DialogPanel;
import org.thechiselgroup.choosel.core.client.ui.ZIndex;
import org.thechiselgroup.choosel.core.client.ui.popup.PopupManagerFactory;
import org.thechiselgroup.choosel.dnd.client.windows.WindowPanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public final class DialogWindow extends WindowPanel implements DialogCallback {

    // TODO extract for i18n
    private final static String CANCEL = "Cancel";

    private DialogWindowManager dialogController;

    private Button okayButton;

    @Inject
    public DialogWindow(PopupManagerFactory popupManagerFactory) {
        super(popupManagerFactory);
    }

    /**
     * Close the dialog with an cancel state.
     */
    public final void cancel() {
        dialogController.cancelDialog(this);
    }

    protected void createCancelButton(DialogPanel dialogPanel) {
        Button cancelButton = dialogPanel.createButton(CANCEL);
        cancelButton.addClickHandler(createCloseButtonClickHandler());
    }

    @Override
    protected ClickHandler createCloseButtonClickHandler() {
        return new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                cancel();
            }

        };
    }

    protected void createOkayButton(final Dialog dialog, DialogPanel dialogPanel) {
        okayButton = dialogPanel.createButton(dialog.getOkayButtonLabel());
        okayButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                okay();
            }
        });
    }

    @Override
    protected String getClosePopupLabel() {
        return CANCEL;
    }

    // TODO add explanation area
    public void init(DialogWindowManager windowController, final Dialog dialog) {

        dialogController = windowController;

        DialogPanel dialogPanel = new DialogPanel();

        Widget content = dialog.getContent();
        dialogPanel.setContent(content);
        dialogPanel.setHeader(dialog.getHeader());

        initButtons(dialog, dialogPanel);

        init(windowController, dialog.getWindowTitle(), false, dialogPanel);

        setZIndex(ZIndex.DIALOG);
    }

    protected void initButtons(final Dialog dialog, DialogPanel dialogPanel) {
        createOkayButton(dialog, dialogPanel);
        createCancelButton(dialogPanel);
    }

    /**
     * Close the dialog with an okay state.
     */
    public final void okay() {
        dialogController.okayPressed(this);
    }

    @Override
    public void setOkayButtonEnabled(boolean enabled) {
        okayButton.setEnabled(enabled);
    }

}