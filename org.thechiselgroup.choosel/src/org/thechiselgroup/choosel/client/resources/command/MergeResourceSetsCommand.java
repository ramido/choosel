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

import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.views.View;

public class MergeResourceSetsCommand extends
	AddResourceSetToResourceSetCommand {

    private View view;

    public MergeResourceSetsCommand(ResourceSet sourceSet,
	    ResourceSet targetSet, View view) {

	super(sourceSet, targetSet);

	assert view != null;
	this.view = view;
    }

    @Override
    public void execute() {
	super.execute();

	view.removeResourceSet(addedSet);
    }

    @Override
    public String getDescription() {
	return "Merge resource set '" + addedSet.getLabel()
		+ "' into resource set '" + modifiedSet.getLabel() + "'";
    }

    public View getView() {
	return view;
    }

    @Override
    public void undo() {
	super.undo();

	view.addResourceSet(addedSet);
    }
}