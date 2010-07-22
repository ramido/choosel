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

import java.util.ArrayList;
import java.util.List;

import org.thechiselgroup.choosel.client.command.UndoableCommand;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.util.HasDescription;
import org.thechiselgroup.choosel.client.views.View;

/**
 * Adds a labelled resource set to a view as an explicitly displayed resource
 * set.
 */
public class AddResourceSetToViewCommand implements UndoableCommand,
        HasDescription {

    private List<Resource> alreadyContainedResources;

    private String description;

    private ResourceSet resourceSet;

    protected View view;

    public AddResourceSetToViewCommand(View view, ResourceSet resourceSet) {
        this(view, resourceSet, "Add resource set '" + resourceSet.getLabel()
                + "' to view");
    }

    public AddResourceSetToViewCommand(View view, ResourceSet resourceSet,
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
        assert !view.containsResourceSet(resourceSet);
        assert alreadyContainedResources == null;

        alreadyContainedResources = new ArrayList<Resource>();
        alreadyContainedResources.addAll(resourceSet.toList());
        alreadyContainedResources.retainAll(view.getResources().toList());

        view.addResourceSet(resourceSet);

        assert view.containsResourceSet(resourceSet);
        assert alreadyContainedResources != null;
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
        assert view.containsResourceSet(resourceSet);
        assert alreadyContainedResources != null;

        view.removeResourceSet(resourceSet);
        view.addResources(alreadyContainedResources);
        alreadyContainedResources = null;

        assert !view.containsResourceSet(resourceSet);
        assert alreadyContainedResources == null;
    }

}