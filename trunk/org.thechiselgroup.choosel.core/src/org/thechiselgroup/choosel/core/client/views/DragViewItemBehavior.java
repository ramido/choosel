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
 * Manages dragging of {@link ViewItem}.
 */
public class DragViewItemBehavior implements ViewItemBehavior {

    /**
     * Maps view item ids to drag enablers.
     */
    private Map<String, DragEnabler> dragEnablers = CollectionFactory
            .createStringMap();

    private DragEnablerFactory dragEnablerFactory;

    public DragViewItemBehavior(DragEnablerFactory dragEnablerFactory) {
        assert dragEnablerFactory != null;

        this.dragEnablerFactory = dragEnablerFactory;
    }

    @Override
    public void onInteraction(ViewItem viewItem, ViewItemInteraction interaction) {
        assert viewItem != null;
        assert dragEnablers.containsKey(viewItem.getViewItemID());
        assert interaction != null;

        DragEnabler enabler = dragEnablers.get(viewItem.getViewItemID());

        switch (interaction.getEventType()) {
        case MOUSE_MOVE:
            enabler.onMoveInteraction(interaction);
            break;
        case MOUSE_DOWN:
            if (interaction.hasNativeEvent()) {
                enabler.forwardMouseDownWithEventPosition(interaction
                        .getNativeEvent());
            }
            break;
        case MOUSE_OUT:
            if (interaction.hasNativeEvent()) {
                enabler.forwardMouseOut(interaction.getNativeEvent());
            }
            break;
        case MOUSE_UP:
            if (interaction.hasNativeEvent()) {
                enabler.forwardMouseUp(interaction.getNativeEvent());
            }
            break;
        }
    }

    @Override
    public void onViewItemCreated(ViewItem viewItem) {
        assert viewItem != null;
        assert !dragEnablers.containsKey(viewItem.getViewItemID());

        dragEnablers.put(viewItem.getViewItemID(),
                dragEnablerFactory.createDragEnabler(viewItem));
    }

    @Override
    public void onViewItemRemoved(ViewItem viewItem) {
        assert viewItem != null;
        assert dragEnablers.containsKey(viewItem.getViewItemID());

        dragEnablers.remove(viewItem.getViewItemID());
    }

}
