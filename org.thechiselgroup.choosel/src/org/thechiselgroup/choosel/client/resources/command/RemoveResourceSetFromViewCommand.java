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

/**
 * Removes a labelled resource set from a view as an explicitly displayed
 * resource set, and removes all resources from this set from the view if they
 * are not explicitly contained in another user set for this view.
 */
public class RemoveResourceSetFromViewCommand implements UndoableCommand,
        HasDescription {

    private ResourceSet resourceSet;

    protected View view;

    private String description;

    public RemoveResourceSetFromViewCommand(View view, ResourceSet resourceSet) {
        this(view, resourceSet, "Remove resource set '"
                + resourceSet.getLabel() + "' from view");
    }

    public RemoveResourceSetFromViewCommand(View view, ResourceSet resourceSet,
            String description) {

        assert view != null;
        assert resourceSet != null;
        assert resourceSet.hasLabel();
        assert description != null;

        this.description = description;
        this.view = view;
        this.resourceSet = resourceSet;
    }

    @Override
    public void execute() {
        assert view.containsResourceSet(resourceSet);
        view.removeResourceSet(resourceSet);
        assert !view.containsResourceSet(resourceSet);
    }

    // TODO add view name / label once available
    @Override
    public String getDescription() {
        return description;
    }

    public ResourceSet getResourceSet() {
        return resourceSet;
    }

    public View getView() {
        return view;
    }

    @Override
    public void undo() {
        assert !view.containsResourceSet(resourceSet);
        view.addResourceSet(resourceSet);
        assert view.containsResourceSet(resourceSet);
    }

}