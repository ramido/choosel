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

import java.util.Map;

import org.thechiselgroup.choosel.core.client.util.collections.CollectionFactory;
import org.thechiselgroup.choosel.core.client.views.model.HighlightingModel;
import org.thechiselgroup.choosel.core.client.views.model.ViewItem;
import org.thechiselgroup.choosel.core.client.views.model.ViewItemBehavior;
import org.thechiselgroup.choosel.core.client.views.model.ViewItemInteraction;

/**
 * Manages {@link ViewItem} highlighting in a single view.
 */
public class HighlightingViewItemBehavior implements ViewItemBehavior {

    /**
     * Maps view item ids to highlighting managers.
     */
    private Map<String, HighlightingManager> highlightingManagers = CollectionFactory
            .createStringMap();

    private HighlightingModel highlightingModel;

    public HighlightingViewItemBehavior(HighlightingModel highlightingModel) {
        assert highlightingModel != null;

        this.highlightingModel = highlightingModel;
    }

    protected HighlightingManager getHighlightingManager(ViewItem viewItem) {
        return highlightingManagers.get(viewItem.getViewItemID());
    }

    @Override
    public void onInteraction(ViewItem viewItem, ViewItemInteraction interaction) {
        assert viewItem != null;
        assert highlightingManagers.containsKey(viewItem.getViewItemID());
        assert interaction != null;

        switch (interaction.getEventType()) {
        case DRAG_END:
        case MOUSE_OUT:
            setHighlighting(viewItem, false);
            break;
        case MOUSE_OVER:
            setHighlighting(viewItem, true);
            break;
        }
    }

    @Override
    public void onViewItemCreated(ViewItem viewItem) {
        assert viewItem != null;
        assert !highlightingManagers.containsKey(viewItem.getViewItemID());

        highlightingManagers.put(viewItem.getViewItemID(),
                new HighlightingManager(highlightingModel, viewItem.getResources()));
    }

    @Override
    public void onViewItemRemoved(ViewItem viewItem) {
        assert viewItem != null;
        assert highlightingManagers.containsKey(viewItem.getViewItemID());

        HighlightingManager manager = highlightingManagers.remove(viewItem
                .getViewItemID());
        manager.dispose();
    }

    protected void setHighlighting(ViewItem viewItem, boolean shouldHighlight) {
        getHighlightingManager(viewItem).setHighlighting(shouldHighlight);
    }

}
