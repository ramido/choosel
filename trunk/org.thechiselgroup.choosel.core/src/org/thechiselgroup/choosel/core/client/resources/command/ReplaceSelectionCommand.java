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
package org.thechiselgroup.choosel.core.client.resources.command;

import org.thechiselgroup.choosel.core.client.command.AbstractUndoableCommand;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.util.HasDescription;
import org.thechiselgroup.choosel.core.client.views.model.SelectionModel;

public class ReplaceSelectionCommand extends AbstractUndoableCommand implements
        HasDescription {

    private ResourceSet originalSelection;

    private ResourceSet resources;

    private SelectionModel selectionModel;

    public ReplaceSelectionCommand(SelectionModel selectionModel,
            ResourceSet resources) {

        assert selectionModel != null;
        assert resources != null;

        this.selectionModel = selectionModel;
        this.resources = resources;
    }

    @Override
    public String getDescription() {
        // XXX label required
        return "Replace selection in '" + selectionModel.toString()
                + "' with '" + resources.getLabel() + "'";
    }

    public ResourceSet getResources() {
        return resources;
    }

    public SelectionModel getSelectionModel() {
        return selectionModel;
    }

    @Override
    public void performExecute() {
        if (originalSelection == null) {
            originalSelection = selectionModel.getSelection();
        }

        selectionModel.setSelection(resources);
    }

    @Override
    public void performUndo() {
        selectionModel.setSelection(originalSelection);
    }

}