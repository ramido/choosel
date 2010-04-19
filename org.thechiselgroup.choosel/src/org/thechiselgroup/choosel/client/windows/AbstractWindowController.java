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
package org.thechiselgroup.choosel.client.windows;

import org.thechiselgroup.choosel.client.command.CommandManager;

import com.google.gwt.user.client.ui.AbsolutePanel;

public abstract class AbstractWindowController implements WindowController {

    protected final AbsolutePanel boundaryPanel;

    protected final WindowMoveController moveController;

    protected final WindowResizeController resizeDragController;

    public AbstractWindowController(AbsolutePanel boundaryPanel,
	    CommandManager commandManager) {

	assert boundaryPanel != null;

	this.boundaryPanel = boundaryPanel;

	moveController = new WindowMoveController(this, commandManager);
	resizeDragController = new WindowResizeController(this, commandManager);
    }

    public AbsolutePanel getBoundaryPanel() {
	return boundaryPanel;
    }

    public WindowMoveController getMoveDragController() {
	return moveController;
    }

    public WindowResizeController getResizeDragController() {
	return resizeDragController;
    }

    @Override
    public void bringToFront(WindowPanel window) {
	// stub, can be overwritten by subclasses
    }

}