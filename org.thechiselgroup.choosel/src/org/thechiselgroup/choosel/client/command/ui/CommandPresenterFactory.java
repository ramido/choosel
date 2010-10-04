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

import org.thechiselgroup.choosel.client.command.AsyncCommand;
import org.thechiselgroup.choosel.client.command.AsyncCommandExecutor;
import org.thechiselgroup.choosel.client.command.AsyncCommandToCommandAdapter;
import org.thechiselgroup.choosel.client.command.ui.CommandPresenter.CommandDisplay;

import com.google.gwt.user.client.Command;
import com.google.inject.Inject;

// TODO use command manager? --> right now, commands are not added there
// TODO what about not undoable // cleaning commands? (use for them only for now)
public class CommandPresenterFactory {

    private AsyncCommandExecutor asyncCommandExecutor;

    @Inject
    public CommandPresenterFactory(AsyncCommandExecutor asyncCommandExecutor) {
        this.asyncCommandExecutor = asyncCommandExecutor;
    }

    public ButtonCommandDisplay createCommandButton(String text,
            AsyncCommand command) {

        return createCommandButton(text, new AsyncCommandToCommandAdapter(
                command, asyncCommandExecutor));
    }

    public ButtonCommandDisplay createCommandButton(String text, Command command) {
        ButtonCommandDisplay display = new ButtonCommandDisplay(text);
        initPresenter(command, display);
        return display;
    }

    public ImageCommandDisplay createCommandImage(String imageUrl,
            AsyncCommand command) {

        return createCommandImage(imageUrl, new AsyncCommandToCommandAdapter(
                command, asyncCommandExecutor));
    }

    public ImageCommandDisplay createCommandImage(String imageUrl,
            Command command) {
        ImageCommandDisplay display = new ImageCommandDisplay(imageUrl);
        initPresenter(command, display);
        return display;
    }

    private void initPresenter(Command command, CommandDisplay display) {
        CommandPresenter presenter = new CommandPresenter(display, command);
        presenter.init();
    }
}