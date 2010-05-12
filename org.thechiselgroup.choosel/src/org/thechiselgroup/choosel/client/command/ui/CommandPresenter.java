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

import org.thechiselgroup.choosel.client.ui.HasEnabledState;
import org.thechiselgroup.choosel.client.ui.Presenter;
import org.thechiselgroup.choosel.client.ui.WidgetAdaptable;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;

// TODO refactor: undoable command only
public class CommandPresenter implements WidgetAdaptable, Presenter {

    public interface Display extends HasClickHandlers, WidgetAdaptable {

    }

    public static class ButtonDisplay extends Button implements Display,
	    HasEnabledState {

	public ButtonDisplay(String label) {
	    setText(label);
	}

	@Override
	public Widget asWidget() {
	    return this;
	}

    }

    private final Display display;

    private final Command command;

    public CommandPresenter(Display display, Command command) {
	assert display != null;
	assert command != null;

	this.display = display;
	this.command = command;
    }

    @Override
    public Widget asWidget() {
	return display.asWidget();
    }

    @Override
    public void init() {
	display.addClickHandler(new ClickHandler() {
	    @Override
	    public void onClick(ClickEvent event) {
		command.execute();
	    }
	});
    }

}