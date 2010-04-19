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
package org.thechiselgroup.choosel.client.ui.dnd;

import java.util.ArrayList;

import org.thechiselgroup.choosel.client.command.UndoableCommand;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ui.ResourceSetAvatar;
import org.thechiselgroup.choosel.client.util.HasDescription;
import org.thechiselgroup.choosel.client.views.View;
import org.thechiselgroup.choosel.client.views.ViewAccessor;

import com.google.gwt.user.client.ui.Widget;

public class SelectionPresenterDropCommandFactory implements
	ResourceSetAvatarDropCommandFactory {

    // TODO refactor (replace avatar with resource set)
    private class AddSelectionSetCommand implements UndoableCommand,
	    HasDescription {
	private final ResourceSetAvatar avatar;

	private AddSelectionSetCommand(ResourceSetAvatar avatar) {
	    this.avatar = avatar;
	}

	private ResourceSet oldSelection;

	@Override
	public void execute() {
	    oldSelection = getView().getSelection();
	    getView().addSelectionSet(avatar.getResourceSet());
	    getView().setSelection(avatar.getResourceSet());
	}

	@Override
	public void undo() {
	    getView().setSelection(oldSelection);
	    getView().removeSelectionSet(avatar.getResourceSet());
	}

	@Override
	public String getDescription() {
	    return "add resource set as selection to view";
	}
    }

    private Widget dropTarget;

    private final ViewAccessor viewAccessor;

    public SelectionPresenterDropCommandFactory(Widget dropTarget,
	    ViewAccessor viewAccessor) {

	assert dropTarget != null;
	assert viewAccessor != null;

	this.viewAccessor = viewAccessor;
	this.dropTarget = dropTarget;
    }

    @Override
    public boolean canDrop(ResourceSetAvatar avatar) {
	assert avatar != null;

	if (!avatar.getResourceSet().hasLabel()) {
	    return false;
	}

	// TODO extract intersect operation
	ArrayList<Resource> resources = new ArrayList<Resource>(getView()
		.getResources().toList());
	resources.retainAll(avatar.getResourceSet().toList());
	if (resources.isEmpty()) {
	    return false;
	}

	return !getView().containsSelectionSet(avatar.getResourceSet());
    }

    @Override
    public UndoableCommand createCommand(final ResourceSetAvatar avatar) {
	assert avatar != null;

	return new AddSelectionSetCommand(avatar);
    }

    private View getView() {
	return viewAccessor.findView(dropTarget);
    }

}