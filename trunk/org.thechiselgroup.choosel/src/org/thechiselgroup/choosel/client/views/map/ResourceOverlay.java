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

import org.thechiselgroup.choosel.client.ui.CSS;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.maps.client.MapPane;
import com.google.gwt.maps.client.MapPaneType;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.geom.Point;
import com.google.gwt.maps.client.overlay.Overlay;
import com.google.gwt.user.client.ui.Label;

public class ResourceOverlay extends Overlay {

    // TODO use in timeline as well?
    public static final String CSS_RESOURCE_ITEM_ICON = "resourceItemIcon";

    private Label label;

    private LatLng latLng;

    private MapWidget map;

    private Point offset;

    private MapPane pane;

    private String text;

    public ResourceOverlay(LatLng latLng, Point offset, String text) {
        this.latLng = latLng;
        this.offset = offset;
        this.text = text;
        this.label = new Label(text);
        this.label.addStyleName(CSS_RESOURCE_ITEM_ICON);
    }

    public HandlerRegistration addClickHandler(ClickHandler handler) {
        return label.addClickHandler(handler);
    }

    public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
        return label.addMouseDownHandler(handler);
    }

    public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler) {
        return label.addMouseMoveHandler(handler);
    }

    public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
        return label.addMouseOutHandler(handler);
    }

    public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
        return label.addMouseOverHandler(handler);
    }

    public HandlerRegistration addMouseUpHandler(MouseUpHandler handler) {
        return label.addMouseUpHandler(handler);
    }

    @Override
    protected final Overlay copy() {
        return new ResourceOverlay(latLng, offset, text);
    }

    @Override
    protected final void initialize(MapWidget map) {
        this.map = map;

        pane = map.getPane(MapPaneType.MARKER_PANE);
        pane.add(label);

        updatePosition();
    }

    @Override
    protected final void redraw(boolean force) {
        if (!force) {
            return;
        }

        updatePosition();
    }

    @Override
    protected final void remove() {
        label.removeFromParent();
    }

    public void setColor(String color) {
        assert color != null;
        CSS.setBackgroundColor(label, color);
    }

    public void setZIndex(int zIndex) {
        CSS.setZIndex(label.getElement(), zIndex);
    }

    private void updatePosition() {
        Point locationPoint = map.convertLatLngToDivPixel(latLng);
        pane.setWidgetPosition(label, locationPoint.getX() + offset.getX(),
                locationPoint.getY() + offset.getY());
    }

}