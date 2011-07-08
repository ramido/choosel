/*******************************************************************************
 * Copyright (C) 2011 Lars Grammel 
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

import org.thechiselgroup.choosel.core.client.resources.ui.DetailsWidgetHelper;
import org.thechiselgroup.choosel.core.client.ui.popup.PopupManager;
import org.thechiselgroup.choosel.core.client.ui.popup.PopupManagerFactory;
import org.thechiselgroup.choosel.core.client.util.collections.CollectionFactory;
import org.thechiselgroup.choosel.core.client.views.model.ViewItem;
import org.thechiselgroup.choosel.core.client.views.model.ViewItemBehavior;
import org.thechiselgroup.choosel.core.client.views.model.ViewItemContainerChangeEvent;
import org.thechiselgroup.choosel.core.client.views.model.ViewItemInteraction;

import com.google.gwt.dom.client.NativeEvent;

/**
 * Manages {@link ViewItem} popups in a single view.
 */
public class PopupViewItemBehavior implements ViewItemBehavior {

    /**
     * Maps view item ids to popup managers.
     */
    private final Map<String, PopupManager> popupManagers = CollectionFactory
            .createStringMap();

    private DetailsWidgetHelper detailsWidgetHelper;

    private PopupManagerFactory popupManagerFactory;

    public PopupViewItemBehavior(DetailsWidgetHelper detailsWidgetHelper,
            PopupManagerFactory popupManagerFactory) {

        assert detailsWidgetHelper != null;
        assert popupManagerFactory != null;

        this.detailsWidgetHelper = detailsWidgetHelper;
        this.popupManagerFactory = popupManagerFactory;
    }

    // for test
    protected PopupManager createPopupManager(final ViewItem viewItem) {
        return popupManagerFactory.createPopupManager(detailsWidgetHelper
                .createDetailsWidget(viewItem));
    }

    protected PopupManager getPopupManager(ViewItem viewItem) {
        return popupManagers.get(viewItem.getViewItemID());
    }

    @Override
    public void onInteraction(ViewItem viewItem, ViewItemInteraction interaction) {
        assert viewItem != null;
        assert popupManagers.containsKey(viewItem.getViewItemID());
        assert interaction != null;

        PopupManager popupManager = getPopupManager(viewItem);

        switch (interaction.getEventType()) {
        case DRAG_START:
            popupManager.hidePopup();
            break;
        case MOUSE_MOVE:
            popupManager.onMouseMove(interaction.getClientX(),
                    interaction.getClientY());
            break;
        case MOUSE_DOWN:
            if (interaction.hasNativeEvent()) {
                NativeEvent nativeEvent = interaction.getNativeEvent();
                popupManager.onMouseDown(nativeEvent);
            }
            break;
        case MOUSE_OUT:
            popupManager.onMouseOut(interaction.getClientX(),
                    interaction.getClientY());
            break;
        case MOUSE_OVER:
            popupManager.onMouseOver(interaction.getClientX(),
                    interaction.getClientY());
            break;
        }
    }

    @Override
    public void onViewItemContainerChanged(ViewItemContainerChangeEvent event) {
        for (ViewItem viewItem : event.getDelta().getAddedViewItems()) {
            onViewItemCreated(viewItem);
        }
        for (ViewItem viewItem : event.getDelta().getRemovedViewItems()) {
            onViewItemRemoved(viewItem);
        }
    }

    public void onViewItemCreated(ViewItem viewItem) {
        assert viewItem != null;
        assert !popupManagers.containsKey(viewItem.getViewItemID());

        popupManagers.put(viewItem.getViewItemID(),
                createPopupManager(viewItem));
    }

    public void onViewItemRemoved(ViewItem viewItem) {
        assert viewItem != null;
        assert popupManagers.containsKey(viewItem.getViewItemID());

        popupManagers.remove(viewItem.getViewItemID());
    }

}