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
public class HighlightingVisualItemBehavior implements VisualItemBehavior {

    /**
     * Maps view item ids to highlighting managers.
     */
    private Map<String, HighlightingManager> highlightingManagers = CollectionFactory
            .createStringMap();

    private HighlightingModel highlightingModel;

    public HighlightingVisualItemBehavior(HighlightingModel highlightingModel) {
        assert highlightingModel != null;

        this.highlightingModel = highlightingModel;
    }

    protected HighlightingManager getHighlightingManager(VisualItem visualItem) {
        return highlightingManagers.get(visualItem.getId());
    }

    @Override
    public void onInteraction(VisualItem visualItem,
            VisualItemInteraction interaction) {

        assert visualItem != null;
        assert highlightingManagers.containsKey(visualItem.getId()) : "no manager for visual item with id "
                + visualItem.getId();
        assert interaction != null;

        switch (interaction.getEventType()) {
        case DRAG_END:
        case MOUSE_OUT:
            setHighlighting(visualItem, false);
            break;
        case MOUSE_OVER:
            setHighlighting(visualItem, true);
            break;
        }
    }

    @Override
    public void onVisualItemContainerChanged(
            VisualItemContainerChangeEvent event) {
        for (VisualItem item : event.getDelta().getAddedElements()) {
            onVisualItemCreated(item);
        }
        for (VisualItem item : event.getDelta().getRemovedElements()) {
            onVisualItemRemoved(item);
        }
    }

    public void onVisualItemCreated(VisualItem visualItem) {
        assert visualItem != null;
        assert !highlightingManagers.containsKey(visualItem.getId());

        highlightingManagers.put(visualItem.getId(), new HighlightingManager(
                highlightingModel, visualItem.getResources()));
    }

    public void onVisualItemRemoved(VisualItem visualItem) {
        assert visualItem != null;
        assert highlightingManagers.containsKey(visualItem.getId()) : "no highlighting manager for visual item "
                + visualItem.getId();

        HighlightingManager manager = highlightingManagers.remove(visualItem
                .getId());
        manager.dispose();
    }

    protected void setHighlighting(VisualItem visualItem,
            boolean shouldHighlight) {
        getHighlightingManager(visualItem).setHighlighting(shouldHighlight);
    }

}
