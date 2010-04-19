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
package org.thechiselgroup.choosel.client.ui.dialog;

import org.adamtacy.client.ui.effects.NEffect;
import org.thechiselgroup.choosel.client.fx.FXUtil;
import org.thechiselgroup.choosel.client.ui.ZIndex;
import org.thechiselgroup.choosel.client.ui.dialog.DialogController.State;
import org.thechiselgroup.choosel.client.windows.WindowPanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public final class DialogWindow extends WindowPanel implements DialogCallback {

    // TODO extract for i18n
    private final static String CANCEL = "Cancel";

    private static final String CSS_DIALOG_BUTTONBAR = "dialog-buttonbar";

    private static final String CSS_DIALOG_CONTENT = "dialog-content";

    private static final String CSS_DIALOG_PANEL = "dialog-panel";

    private DialogController dialogController;

    private Button okayButton;

    private State state = State.RUNNING;

    private void createCancelButton(HorizontalPanel buttonBar) {
	Button cancelButton = new Button(CANCEL);
	cancelButton.addClickHandler(createCloseButtonClickHandler());
	buttonBar.add(cancelButton);
    }

    @Override
    protected ClickHandler createCloseButtonClickHandler() {
	return new ClickHandler() {
	    @Override
	    public void onClick(ClickEvent event) {
		dialogController.cancelDialog(DialogWindow.this);
	    }
	};
    }

    @Override
    protected NEffect createHideEffect() {
	return FXUtil.createOpacityMorph(OPACITY_80_PERCENT,
		OPACITY_TRANSPARENT);
    }

    private void createOkayButton(final Dialog dialog, HorizontalPanel buttonBar) {
	okayButton = new Button(dialog.getOkayButtonLabel());
	okayButton.addClickHandler(new ClickHandler() {
	    @Override
	    public void onClick(ClickEvent event) {
		dialogController.finishDialog(DialogWindow.this);
	    }
	});
	buttonBar.add(okayButton);
    }

    @Override
    protected NEffect createShowEffect() {
	return FXUtil.createOpacityMorph(OPACITY_TRANSPARENT,
		OPACITY_80_PERCENT);
    }

    @Override
    protected String getClosePopupLabel() {
	return CANCEL;
    }

    public State getState() {
	return state;
    }

    // TODO add explanation area
    public void init(DialogController windowController, final Dialog dialog) {

	this.dialogController = windowController;

	VerticalPanel dialogPanel = new VerticalPanel();
	dialogPanel.addStyleName(CSS_DIALOG_PANEL);

	Widget content = dialog.getContent();
	content.addStyleName(CSS_DIALOG_CONTENT);
	dialogPanel.add(content);

	HorizontalPanel buttonBar = new HorizontalPanel();
	buttonBar.addStyleName(CSS_DIALOG_BUTTONBAR);
	buttonBar.setSpacing(5);
	buttonBar.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);

	createOkayButton(dialog, buttonBar);
	createCancelButton(buttonBar);

	dialogPanel.add(buttonBar);
	dialogPanel.setCellHorizontalAlignment(buttonBar,
		HasAlignment.ALIGN_RIGHT);
	dialogPanel.setCellVerticalAlignment(buttonBar,
		HasAlignment.ALIGN_BOTTOM);

	init(windowController, dialog.getTitle(), dialogPanel);

	setZIndex(ZIndex.DIALOG);
    }

    @Override
    public void setOkayButtonEnabled(boolean enabled) {
	okayButton.setEnabled(enabled);
    }

    public void setState(State state) {
	this.state = state;
    }
}