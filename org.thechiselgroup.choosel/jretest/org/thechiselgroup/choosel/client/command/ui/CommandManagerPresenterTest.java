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

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.client.command.CommandManager;
import org.thechiselgroup.choosel.client.command.DefaultCommandManager;
import org.thechiselgroup.choosel.client.command.ui.CommandManagerPresenter;
import org.thechiselgroup.choosel.client.command.ui.CommandManagerPresenter.CommandManagerPresenterDisplay;
import org.thechiselgroup.choosel.client.resolver.PropertyValueResolver;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.test.MockitoGWTBridge;
import org.thechiselgroup.choosel.client.test.TestUndoableCommandWithDescription;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;

public class CommandManagerPresenterTest {

    private static final String COMMAND_DESCRIPTION = "command";

    @Mock
    private TestUndoableCommandWithDescription command;

    private CommandManager commandManager;

    private CommandManagerPresenter commandManagerPresenter;

    @Mock
    private CommandManagerPresenterDisplay display;

    @Mock
    private HasClickHandlers redoClickHandlers;

    @Mock
    private PropertyValueResolver resolver;

    @Mock
    private HasClickHandlers undoClickHandlers;

    // TODO tests for command manager with initial state
    @Test
    public void buttonsDisabledInitialyForEmptyCommandManager() {
	verify(display, times(1)).setRedoButtonEnabled(false);
	verify(display, times(1)).setUndoButtonEnabled(false);
    }

    @Test
    public void callsRedoOnClick() {
	commandManager.addExecutedCommand(command);
	commandManager.undo();

	ArgumentCaptor<ClickHandler> argument = ArgumentCaptor
		.forClass(ClickHandler.class);
	verify(redoClickHandlers, times(1)).addClickHandler(argument.capture());

	ClickHandler handler = argument.getValue();

	handler.onClick(mock(ClickEvent.class));

	verify(commandManager, times(1)).redo();
    }

    @Test
    public void callsUndoOnClick() {
	commandManager.addExecutedCommand(command);

	ArgumentCaptor<ClickHandler> argument = ArgumentCaptor
		.forClass(ClickHandler.class);
	verify(undoClickHandlers, times(1)).addClickHandler(argument.capture());

	ClickHandler handler = argument.getValue();

	handler.onClick(mock(ClickEvent.class));

	verify(commandManager, times(1)).undo();
    }

    @Test
    public void disableRedoButtonOnEventIfNotRedoable() {
	commandManager.addExecutedCommand(command);
	commandManager.undo();
	commandManager.redo();

	verify(display, times(3)).setRedoButtonEnabled(false);
    }

    @Test
    public void disableRedoCommandDescriptionOnEventIfNotRedoable() {
	commandManager.addExecutedCommand(command);
	commandManager.undo();
	commandManager.redo();

	verify(display, times(3)).setRedoCommandDescription("");
    }

    @Test
    public void disableUndoButtonOnEventIfNotUndoable() {
	commandManager.addExecutedCommand(command);
	commandManager.undo();

	verify(display, times(2)).setUndoButtonEnabled(false);
    }

    @Test
    public void disableButtonsOnClear() {
	commandManager.addExecutedCommand(command);
	commandManager.addExecutedCommand(command);
	commandManager.undo();

	verify(display, times(1)).setUndoButtonEnabled(false);
	verify(display, times(3)).setRedoButtonEnabled(false);

	commandManager.clear();

	verify(display, times(2)).setUndoButtonEnabled(false);
	verify(display, times(4)).setRedoButtonEnabled(false);
    }

    @Test
    public void enableRedoButtonOnEventIfRedoable() {
	commandManager.addExecutedCommand(command);
	commandManager.undo();

	verify(display, times(1)).setRedoButtonEnabled(true);
    }

    @Test
    public void enableUndoButtonOnEventIfUndoable() {
	commandManager.addExecutedCommand(command);

	verify(display).setUndoButtonEnabled(true);
    }

    @Test
    public void initRegistersClickHandler() {
	verify(undoClickHandlers, times(1)).addClickHandler(
		any(ClickHandler.class));
    }

    @Test
    public void setRedoButtonDescriptionOnEventIfRedoable() {
	commandManager.addExecutedCommand(command);
	commandManager.undo();

	verify(display, times(1))
		.setRedoCommandDescription(COMMAND_DESCRIPTION);
    }

    @Test
    public void setUndoButtonCommandDescriptionOnEventIfNotUndoable() {
	commandManager.addExecutedCommand(command);
	commandManager.undo();

	verify(display, times(2)).setUndoCommandDescription("");
    }

    @Test
    public void setUndoButtonDescriptionOnEventIfUndoable() {
	commandManager.addExecutedCommand(command);

	verify(display).setUndoCommandDescription(COMMAND_DESCRIPTION);
    }

    @Before
    public void setUp() throws Exception {
	MockitoGWTBridge.setUp();
	MockitoAnnotations.initMocks(this);

	commandManager = spy(new DefaultCommandManager());
	commandManagerPresenter = new CommandManagerPresenter(commandManager,
		display);

	when(resolver.getValue(any(Resource.class))).thenReturn("");
	when(display.getUndoClickHandlers()).thenReturn(undoClickHandlers);
	when(display.getRedoClickHandlers()).thenReturn(redoClickHandlers);
	when(command.getDescription()).thenReturn(COMMAND_DESCRIPTION);

	commandManagerPresenter.init();
    }

    @After
    public void tearDown() {
	MockitoGWTBridge.tearDown();
    }

}
