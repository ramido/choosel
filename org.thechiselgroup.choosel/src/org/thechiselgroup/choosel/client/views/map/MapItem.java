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
package org.thechiselgroup.choosel.client.views.map;

import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.ui.popup.PopupManager;
import org.thechiselgroup.choosel.client.views.DragEnabler;
import org.thechiselgroup.choosel.client.views.DragEnablerFactory;
import org.thechiselgroup.choosel.client.views.IconResourceItem;
import org.thechiselgroup.choosel.client.views.Layer;
import org.thechiselgroup.choosel.client.views.ViewContentDisplayCallback;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.geom.Point;

public class MapItem extends IconResourceItem {

    private class MarkerEventHandler implements ClickHandler, MouseOutHandler,
            MouseOverHandler {

        @Override
        public void onClick(ClickEvent event) {
            callback.switchSelection(getResourceSet());
        }

        @Override
        public void onMouseOut(MouseOutEvent event) {
            getPopupManager()
                    .onMouseOut(event.getClientX(), event.getClientY());
            setHighlighted(false);
        }

        @Override
        public void onMouseOver(MouseOverEvent event) {
            getPopupManager().onMouseOver(event.getClientX(),
                    event.getClientY());
            setHighlighted(true);
        }
    }

    private static final int Z_INDEX_DEFAULT = 5;

    private static final int Z_INDEX_GRAYED_OUT = 1;

    private static final int Z_INDEX_HIGHLIGHTED = 20;

    private static final int Z_INDEX_SELECTED = 10;

    private final ViewContentDisplayCallback callback;

    private MarkerEventHandler eventHandler;

    private ResourceOverlay overlay;

    private DragEnablerFactory dragEnablerFactory;

    public MapItem(LatLng point, ResourceSet resources, ResourceSet hoverModel,
            PopupManager popupManager, Layer layerModel,
            ViewContentDisplayCallback callback,
            DragEnablerFactory dragEnablerFactory) {

        super(resources, hoverModel, popupManager, layerModel);

        this.callback = callback;
        this.dragEnablerFactory = dragEnablerFactory;
        this.overlay = new ResourceOverlay(point, Point.newInstance(-10, -10),
                getDefaultIconURL()); // -10 = - (width /2)
        this.eventHandler = new MarkerEventHandler();

        initEventHandlers();
    }

    public ResourceOverlay getOverlay() {
        return overlay;
    }

    private void initEventHandlers() {
        overlay.addMouseOverHandler(eventHandler);
        overlay.addMouseOutHandler(eventHandler);
        overlay.addClickHandler(eventHandler);

        final DragEnabler enabler = dragEnablerFactory.createDragEnabler(this);
        overlay.addMouseUpHandler(new MouseUpHandler() {
            @Override
            public void onMouseUp(MouseUpEvent event) {
                enabler.forwardMouseUp(event.getNativeEvent());
            }
        });
        overlay.addMouseOutHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                enabler.forwardMouseOut(event.getNativeEvent());
            }
        });
        overlay.addMouseMoveHandler(new MouseMoveHandler() {
            @Override
            public void onMouseMove(MouseMoveEvent event) {
                enabler.forwardMouseMove(event.getNativeEvent());
            }
        });
        overlay.addMouseDownHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                enabler.forwardMouseDownWithTargetElementPosition(event
                        .getNativeEvent());
                event.stopPropagation(); // to prevent standard map drag
            }
        });
    }

    public void setDefaultStyle() {
        overlay.setIconURL(getDefaultIconURL());
        overlay.setZIndex(Z_INDEX_DEFAULT);
    }

    private void setGrayedOutStyle() {
        overlay.setIconURL(getGrayedOutIconURL());
        overlay.setZIndex(Z_INDEX_GRAYED_OUT);
    }

    private void setHighlightedStyle() {
        overlay.setIconURL(getHighlightIconURL());
        overlay.setZIndex(Z_INDEX_HIGHLIGHTED);
    }

    private void setSelectedStyle() {
        overlay.setIconURL(getSelectedIconURL());
        overlay.setZIndex(Z_INDEX_SELECTED);
    }

    @Override
    protected void setStatusStyling(Status status) {
        switch (status) {
        case HIGHLIGHTED_SELECTED: {
            setHighlightedStyle();
        }
            break;
        case HIGHLIGHTED: {
            setHighlightedStyle();
        }
            break;
        case DEFAULT: {
            setDefaultStyle();
        }
            break;
        case GRAYED_OUT: {
            setGrayedOutStyle();
        }
            break;
        case SELECTED: {
            setSelectedStyle();
        }
            break;
        }
    }
}