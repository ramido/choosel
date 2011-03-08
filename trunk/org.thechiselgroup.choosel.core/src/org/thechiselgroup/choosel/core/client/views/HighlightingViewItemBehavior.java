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
package org.thechiselgroup.choosel.core.client.views;

import java.util.Map;

import org.thechiselgroup.choosel.core.client.util.collections.CollectionFactory;

/**
 * Manages {@link ViewItem} highlighting in a single view.
 */
public class HighlightingViewItemBehavior implements ViewItemBehavior {

    /**
     * Maps view item ids to highlighting managers.
     */
    private Map<String, HighlightingManager> highlightingManagers = CollectionFactory
            .createStringMap();

    private HoverModel hoverModel;

    public HighlightingViewItemBehavior(HoverModel hoverModel) {
        assert hoverModel != null;

        this.hoverModel = hoverModel;
    }

    @Override
    public void onInteraction(ViewItem viewItem, ViewItemInteraction interaction) {
        assert viewItem != null;
        assert highlightingManagers.containsKey(viewItem.getViewItemID());
        assert interaction != null;

        HighlightingManager highlightingManager = highlightingManagers
                .get(viewItem.getViewItemID());

        switch (interaction.getEventType()) {
        case DRAG_END:
        case MOUSE_OUT:
            highlightingManager.setHighlighting(false);
            break;
        case MOUSE_OVER:
            highlightingManager.setHighlighting(true);
            break;
        }
    }

    @Override
    public void onViewItemCreated(ViewItem viewItem) {
        assert viewItem != null;
        assert !highlightingManagers.containsKey(viewItem.getViewItemID());

        highlightingManagers.put(viewItem.getViewItemID(),
                new HighlightingManager(hoverModel, viewItem.getResourceSet()));
    }

    @Override
    public void onViewItemRemoved(ViewItem viewItem) {
        assert viewItem != null;
        assert highlightingManagers.containsKey(viewItem.getViewItemID());

        HighlightingManager manager = highlightingManagers.remove(viewItem
                .getViewItemID());
        manager.dispose();
    }

}
