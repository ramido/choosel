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

import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.resources.ui.DetailsWidgetHelper;
import org.thechiselgroup.choosel.core.client.ui.WidgetFactory;
import org.thechiselgroup.choosel.core.client.ui.popup.PopupClosingEvent;
import org.thechiselgroup.choosel.core.client.ui.popup.PopupClosingHandler;
import org.thechiselgroup.choosel.core.client.ui.popup.PopupManager;
import org.thechiselgroup.choosel.core.client.ui.popup.PopupManagerFactory;
import org.thechiselgroup.choosel.core.client.util.collections.CollectionFactory;
import org.thechiselgroup.choosel.core.client.views.slots.SlotMappingConfiguration;

import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;

/**
 * Manages {@link ViewItem} popups in a single view.
 */
public class PopupViewItemBehavior implements ViewItemBehavior {

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

    private SlotMappingConfiguration slotMappingConfiguration;

    public PopupViewItemBehavior(HoverModel hoverModel,
            DetailsWidgetHelper detailsWidgetHelper,
            PopupManagerFactory popupManagerFactory,
            SlotMappingConfiguration slotMappingConfiguration) {

        this.hoverModel = hoverModel;
        this.detailsWidgetHelper = detailsWidgetHelper;
        this.popupManagerFactory = popupManagerFactory;
        this.slotMappingConfiguration = slotMappingConfiguration;
    }

    // for test
    protected PopupManager createPopupManager(final String viewItemID,
            final ResourceSet resources) {

        WidgetFactory widgetFactory = new WidgetFactory() {

            @Override
            public Widget createWidget() {
                return detailsWidgetHelper.createDetailsWidget(viewItemID,
                        resources, slotMappingConfiguration);
            }
        };

        return popupManagerFactory.createPopupManager(widgetFactory);
    }

    @Override
    public void onInteraction(ViewItem viewItem, ViewItemInteraction interaction) {
        assert viewItem != null;
        assert popupManagers.containsKey(viewItem.getViewItemID());
        assert interaction != null;

        PopupManager popupManager = popupManagers.get(viewItem.getViewItemID());

        switch (interaction.getEventType()) {
        case Event.ONMOUSEMOVE: {
            popupManager.onMouseMove(interaction.getClientX(),
                    interaction.getClientY());
        }
            break;
        case Event.ONMOUSEDOWN: {
            if (interaction.hasNativeEvent()) {
                popupManager.onMouseDown(interaction.getNativeEvent());
            }
        }
            break;
        case Event.ONMOUSEOUT: {
            popupManager.onMouseOut(interaction.getClientX(),
                    interaction.getClientY());
        }
            break;
        case Event.ONMOUSEOVER: {
            popupManager.onMouseOver(interaction.getClientX(),
                    interaction.getClientY());
        }
            break;
        }
    }

    @Override
    public void onViewItemCreated(ViewItem viewItem) {
        assert viewItem != null;
        assert !highlightingManagers.containsKey(viewItem.getViewItemID());
        assert !popupManagers.containsKey(viewItem.getViewItemID());

        final HighlightingManager highlightingManager = new HighlightingManager(
                hoverModel, viewItem.getResourceSet());

        PopupManager popupManager = createPopupManager(
                viewItem.getViewItemID(), viewItem.getResourceSet());

        popupManager.addPopupMouseOverHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent e) {
                highlightingManager.setHighlighting(true);
            }
        });
        popupManager.addPopupMouseOutHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                highlightingManager.setHighlighting(false);
            }
        });
        popupManager.addPopupClosingHandler(new PopupClosingHandler() {
            @Override
            public void onPopupClosing(PopupClosingEvent event) {
                highlightingManager.setHighlighting(false);
            }
        });

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
