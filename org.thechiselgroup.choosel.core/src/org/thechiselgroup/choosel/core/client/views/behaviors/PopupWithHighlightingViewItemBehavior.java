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

import org.thechiselgroup.choosel.core.client.fx.Opacity;
import org.thechiselgroup.choosel.core.client.resources.ui.DetailsWidgetHelper;
import org.thechiselgroup.choosel.core.client.ui.popup.Popup;
import org.thechiselgroup.choosel.core.client.ui.popup.PopupManager;
import org.thechiselgroup.choosel.core.client.ui.popup.PopupManagerFactory;
import org.thechiselgroup.choosel.core.client.ui.popup.PopupOpacityChangedEvent;
import org.thechiselgroup.choosel.core.client.ui.popup.PopupOpacityChangedEventHandler;
import org.thechiselgroup.choosel.core.client.util.collections.CollectionFactory;
import org.thechiselgroup.choosel.core.client.views.model.HoverModel;
import org.thechiselgroup.choosel.core.client.views.model.ViewItem;
import org.thechiselgroup.choosel.core.client.views.model.ViewItemBehavior;
import org.thechiselgroup.choosel.core.client.views.model.ViewItemInteraction;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;

/**
 * Manages {@link ViewItem} popups in a single view.
 */
public class PopupWithHighlightingViewItemBehavior implements ViewItemBehavior {

    /**
     * Maps view item ids to popup managers.
     */
    private Map<String, PopupManager> popupManagers = CollectionFactory
            .createStringMap();

    /**
     * Maps view item ids to popup highlighting managers.
     */
    private Map<String, HighlightingManager> highlightingManagers = CollectionFactory
            .createStringMap();

    private HoverModel hoverModel;

    private DetailsWidgetHelper detailsWidgetHelper;

    private PopupManagerFactory popupManagerFactory;

    public PopupWithHighlightingViewItemBehavior(HoverModel hoverModel,
            DetailsWidgetHelper detailsWidgetHelper,
            PopupManagerFactory popupManagerFactory) {

        this.hoverModel = hoverModel;
        this.detailsWidgetHelper = detailsWidgetHelper;
        this.popupManagerFactory = popupManagerFactory;
    }

    // for test
    protected PopupManager createPopupManager(final ViewItem viewItem) {
        return popupManagerFactory.createPopupManager(detailsWidgetHelper
                .createDetailsWidget(viewItem));
    }

    @Override
    public void onInteraction(ViewItem viewItem, ViewItemInteraction interaction) {
        assert viewItem != null;
        assert popupManagers.containsKey(viewItem.getViewItemID());
        assert interaction != null;

        PopupManager popupManager = popupManagers.get(viewItem.getViewItemID());

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
    public void onViewItemCreated(ViewItem viewItem) {
        assert viewItem != null;
        assert !highlightingManagers.containsKey(viewItem.getViewItemID());
        assert !popupManagers.containsKey(viewItem.getViewItemID());

        final HighlightingManager highlightingManager = new HighlightingManager(
                hoverModel, viewItem.getResources());

        PopupManager popupManager = createPopupManager(viewItem);
        Popup popup = popupManager.getPopup();

        popup.addDomHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent e) {
                highlightingManager.setHighlighting(true);
            }
        }, MouseOverEvent.getType());
        popup.addDomHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                highlightingManager.setHighlighting(false);
            }
        }, MouseOutEvent.getType());
        popup.addHandler(new PopupOpacityChangedEventHandler() {
            @Override
            public void onOpacityChangeStarted(PopupOpacityChangedEvent event) {
                if (event.getOpacity() == Opacity.TRANSPARENT) {
                    highlightingManager.setHighlighting(false);
                }
            }
        }, PopupOpacityChangedEvent.TYPE);

        highlightingManagers.put(viewItem.getViewItemID(), highlightingManager);
        popupManagers.put(viewItem.getViewItemID(), popupManager);
    }

    @Override
    public void onViewItemRemoved(ViewItem viewItem) {
        assert viewItem != null;
        assert highlightingManagers.containsKey(viewItem.getViewItemID());
        assert popupManagers.containsKey(viewItem.getViewItemID());

        popupManagers.remove(viewItem.getViewItemID());
        HighlightingManager manager = highlightingManagers.remove(viewItem
                .getViewItemID());
        manager.dispose();
    }
}
