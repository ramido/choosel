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

import java.util.List;

import org.thechiselgroup.choosel.client.command.UndoableCommand;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.util.CollectionUtils;
import org.thechiselgroup.choosel.client.util.HasDescription;
import org.thechiselgroup.choosel.client.views.View;

/**
 * Adds resources to a view.
 */
public class AddResourcesToViewCommand implements UndoableCommand,
        HasDescription {

    private List<Resource> addedResources;

    private Iterable<Resource> resources;

    private View view;

    public AddResourcesToViewCommand(View view, Iterable<Resource> resources) {
        assert view != null;
        assert resources != null;

        this.view = view;
        this.resources = resources;
    }

    @Override
    public void execute() {
        assert addedResources == null;

        ResourceSet viewResources = view.getResources();
        addedResources = CollectionUtils.toList(resources);
        addedResources.removeAll(viewResources.toList());

        view.addResources(addedResources);

        assert addedResources != null;
        assert view.containsResources(resources);
    }

    // TODO add view name / label once available
    @Override
    public String getDescription() {
        return "Add resources to view";
    }

    @Override
    public void undo() {
        assert addedResources != null;
        assert view.containsResources(resources);

        view.removeResources(addedResources);
        addedResources = null;

        assert addedResources == null;
    }

}