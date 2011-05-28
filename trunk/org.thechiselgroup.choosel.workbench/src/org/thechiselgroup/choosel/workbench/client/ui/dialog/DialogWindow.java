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

    private class ButtonClickHandler implements ClickHandler {

        private Button button;

        protected ButtonClickHandler(Button button) {
            this.button = button;
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt
         * .event.dom.client.ClickEvent)
         */
        @Override
        public void onClick(ClickEvent event) {
            handleButtonClick(button,
                    button.getElement().getPropertyInt(DIALOG_STATUS_CODE));
        }

    }

    // TODO extract for i18n
    final static String CANCEL_LABEL = "Cancel";

    private static final String DIALOG_STATUS_CODE = "__choosel_dialogs_button_status_code";

    /**
     * Button code for when an OK button is pressed.
     */
    public static final int OK = 1;

    /**
     * Button code for when a CANCEL button is pressed.
     */
    public static final int CANCEL = 2;

    private DialogWindowManager dialogController;

    private DialogPanel dialogPanel;

    private Dialog dialog;

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

    /**
     * Closes the dialog window.
     */
    @Override
    public final void close() {
        dialogController.close(this);
    }

    /**
     * Creates and adds a button to this window's button bar. The buttons are
     * added from left to right in the order that they are created. The code is
     * used to define the action that should be taken when the button is
     * created. Two default codes are provided in this class: {@link #OK} and
     * {@link #CANCEL}.
     * 
     * @param code
     *            the status code for the button.
     * @param label
     *            the label to be presented on the button.
     * @return the button that has been created and added to the panel.
     */
    public Button createButton(int code, String label) {
        Button b = dialogPanel.createButton(label);
        b.getElement().setPropertyInt(DIALOG_STATUS_CODE, code);
        b.addClickHandler(new ButtonClickHandler(b));
        return b;

    }

    protected void createCancelButton(DialogPanel dialogPanel) {
        createButton(CANCEL, CANCEL_LABEL);
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
        createButton(OK, dialog.getOkayButtonLabel());
    }

    /**
     * Returns the button with the given status code, or null if it doesn't
     * exist.
     * 
     * @param code
     *            the code.
     * @return the button.
     */
    public Button getButton(int code) {
        for (Widget w : dialogPanel) {
            if (w instanceof Button) {
                if (w.getElement().getPropertyInt(DIALOG_STATUS_CODE) == code) {
                    return (Button) w;
                }
            }
        }
        return null;
    }

    @Override
    protected String getClosePopupLabel() {
        return CANCEL_LABEL;
    }

    /**
     * Generic handler for all button clicks.
     * 
     * @param button
     * @param propertyInt
     */
    protected void handleButtonClick(Button button, int statusCode) {
        if (dialog instanceof DialogExtension) {
            ((DialogExtension) dialog).buttonPressed(statusCode, button, this);
        } else {
            if (statusCode == OK) {
                okay();
            } else if (statusCode == CANCEL) {
                cancel();
            }
        }
    }

    // TODO add explanation area
    public void init(DialogWindowManager windowController, Dialog dialog) {

        dialogController = windowController;
        if (dialog instanceof DialogExtension) {
            ((DialogExtension) dialog).dialogCreated(this);
        }
        dialogPanel = new DialogPanel();

        Widget content = dialog.getContent();
        dialogPanel.setContent(content);
        dialogPanel.setHeader(dialog.getHeader());

        initButtons(dialog);

        init(windowController, dialog.getWindowTitle(), false, dialogPanel);

        setZIndex(ZIndex.DIALOG);
        this.dialog = dialog;
        dialog.init(this);
    }

    protected void initButtons(final Dialog dialog) {
        if (dialog instanceof DialogExtension) {
            ((DialogExtension) dialog).createButtons(this);
        } else {
            createOkayButton(dialog, dialogPanel);
            createCancelButton(dialogPanel);
        }
    }

    /**
     * Close the dialog with an okay state.
     */
    public final void okay() {
        try {
            dialog.okay();
            close();
        } catch (Exception e) {
            dialog.handleException(e);
        }
    }

    public void setButtonEnabled(int code, boolean enabled) {
        Button okayButton = getButton(code);
        if (okayButton != null) {
            okayButton.setEnabled(enabled);
        }
    }

    @Override
    public void setOkayButtonEnabled(boolean enabled) {
        setButtonEnabled(OK, enabled);
    }
}