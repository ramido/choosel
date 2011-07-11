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
package org.thechiselgroup.choosel.core.client.visualization.behaviors;

import java.util.Map;

import org.thechiselgroup.choosel.core.client.util.collections.CollectionFactory;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItem;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItemBehavior;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItemContainerChangeEvent;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItemInteraction;
import org.thechiselgroup.choosel.core.client.visualization.model.extensions.HighlightingModel;

/**
 * Manages {@link VisualItem} highlighting in a single view.
 */
public class HighlightingViewItemBehavior implements VisualItemBehavior {

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

    protected HighlightingManager getHighlightingManager(VisualItem viewItem) {
        return highlightingManagers.get(viewItem.getId());
    }

    @Override
    public void onInteraction(VisualItem viewItem,
            VisualItemInteraction interaction) {
        assert viewItem != null;
        assert highlightingManagers.containsKey(viewItem.getId());
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
    public void onViewItemContainerChanged(VisualItemContainerChangeEvent event) {
        for (VisualItem viewItem : event.getDelta().getAddedElements()) {
            onViewItemCreated(viewItem);
        }
        for (VisualItem viewItem : event.getDelta().getRemovedElements()) {
            onViewItemRemoved(viewItem);
        }
    }

    public void onViewItemCreated(VisualItem viewItem) {
        assert viewItem != null;
        assert !highlightingManagers.containsKey(viewItem.getId());

        highlightingManagers.put(viewItem.getId(), new HighlightingManager(
                highlightingModel, viewItem.getResources()));
    }

    public void onViewItemRemoved(VisualItem viewItem) {
        assert viewItem != null;
        assert highlightingManagers.containsKey(viewItem.getId());

        HighlightingManager manager = highlightingManagers.remove(viewItem
                .getId());
        manager.dispose();
    }

    protected void setHighlighting(VisualItem viewItem, boolean shouldHighlight) {
        getHighlightingManager(viewItem).setHighlighting(shouldHighlight);
    }

}