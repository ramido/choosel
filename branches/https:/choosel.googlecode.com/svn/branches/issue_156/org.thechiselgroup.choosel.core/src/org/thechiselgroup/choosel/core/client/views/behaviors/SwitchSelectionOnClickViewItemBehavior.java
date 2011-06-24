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
package org.thechiselgroup.choosel.core.client.views.behaviors;

import org.thechiselgroup.choosel.core.client.views.model.SelectionModel;
import org.thechiselgroup.choosel.core.client.views.model.ViewItem;
import org.thechiselgroup.choosel.core.client.views.model.ViewItemBehavior;
import org.thechiselgroup.choosel.core.client.views.model.ViewItemInteraction;

/**
 * Manages {@link ViewItem} highlighting in a single view.
 */
public class SwitchSelectionOnClickViewItemBehavior implements ViewItemBehavior {

    private SelectionModel selectionModel;

    public SwitchSelectionOnClickViewItemBehavior(SelectionModel selectionModel) {
        assert selectionModel != null;

        this.selectionModel = selectionModel;
    }

    @Override
    public void onInteraction(ViewItem viewItem, ViewItemInteraction interaction) {
        assert viewItem != null;
        assert interaction != null;

        switch (interaction.getEventType()) {
        case CLICK:
            switchSelection(viewItem);
            break;
        }
    }

    protected void switchSelection(ViewItem viewItem) {
        selectionModel.switchSelection(viewItem.getResources());
    }

    @Override
    public void onViewItemCreated(ViewItem viewItem) {
    }

    @Override
    public void onViewItemRemoved(ViewItem viewItem) {
    }

}
