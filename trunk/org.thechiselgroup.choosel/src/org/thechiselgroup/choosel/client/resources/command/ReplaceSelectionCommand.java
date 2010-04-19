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
package org.thechiselgroup.choosel.client.resources.command;

import org.thechiselgroup.choosel.client.command.UndoableCommand;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.util.HasDescription;
import org.thechiselgroup.choosel.client.views.View;

public class ReplaceSelectionCommand implements UndoableCommand, HasDescription {

    private ResourceSet originalSelection;

    private ResourceSet resources;

    private View view;

    public ReplaceSelectionCommand(View view, ResourceSet resources) {
	assert view != null;
	assert resources != null;

	this.view = view;
	this.resources = resources;
    }

    @Override
    public void execute() {
	if (originalSelection == null) {
	    originalSelection = view.getSelection();
	}

	view.setSelection(resources);
    }

    @Override
    public String getDescription() {
	// TODO view label
	return "Replace selection in '" + view.toString() + "' with '"
		+ resources.getLabel() + "'";
    }

    public ResourceSet getResources() {
	return resources;
    }

    public View getView() {
	return view;
    }

    @Override
    public void undo() {
	view.setSelection(originalSelection);
    }

}