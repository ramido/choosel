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
package org.thechiselgroup.choosel.visualization_component.map.client;

import org.thechiselgroup.choosel.core.client.ui.CSS;

import com.google.gwt.maps.client.MapPane;
import com.google.gwt.maps.client.MapPaneType;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.geom.Point;
import com.google.gwt.maps.client.overlay.Overlay;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;

public class LabelOverlay extends Overlay {

    private Element label;

    private LatLng latLng;

    private MapWidget map;

    private MapPane pane;

    private Point locationPoint;

    private EventListener eventListener;

    private String text;

    private int size;

    public LabelOverlay(LatLng latLng, String text, int size, String styleName,
            EventListener eventListener) {

        assert latLng != null;
        assert text != null;
        assert styleName != null;
        assert eventListener != null;
        assert text != null;
        assert size >= 0;

        this.size = size;
        this.text = text;
        this.latLng = latLng;
        this.label = DOM.createDiv();
        this.label.setClassName(styleName);
        this.eventListener = eventListener;

        DOM.sinkEvents(label, Event.MOUSEEVENTS | Event.ONCLICK);
        DOM.setEventListener(label, eventListener);
        CSS.setPosition(label, CSS.ABSOLUTE);

        updateSizeCssProperties();

        label.setInnerText(text);
    }

    @Override
    protected final Overlay copy() {
        return new LabelOverlay(latLng, text, size, label.getClassName(),
                eventListener);
    }

    @Override
    protected final void initialize(MapWidget map) {
        this.map = map;

        pane = map.getPane(MapPaneType.MARKER_PANE);
        pane.getElement().appendChild(label);

        updatePosition(map.convertLatLngToDivPixel(latLng));
    }

    @Override
    protected final void redraw(boolean force) {
        /*
         * We check if the location has changed, because Google Maps allows
         * infinite panning along the east-west-axis (see issue 38) and requires
         * an updated widget location in this case, although it will not force
         * redrawing.
         */
        Point newLocationPoint = map.convertLatLngToDivPixel(latLng);

        if (!force && sameLocation(newLocationPoint)) {
            return;
        }

        updatePosition(newLocationPoint);
    }

    public void registerEventListener() {

    }

    @Override
    protected final void remove() {
        label.removeFromParent();
    }

    private boolean sameLocation(Point newLocationPoint) {
        assert newLocationPoint != null;
        return locationPoint != null
                && locationPoint.getX() == newLocationPoint.getX()
                && locationPoint.getY() == newLocationPoint.getY();
    }

    public void setBackgroundColor(String color) {
        assert color != null;
        CSS.setBackgroundColor(label, color);
    }

    public void setBorderColor(String color) {
        assert color != null;
        CSS.setBorderColor(label, color);
    }

    public void setLabel(String labelText) {
        assert labelText != null;
        this.text = labelText;
        this.label.setInnerText(labelText);
    }

    public void setSize(int size) {
        this.size = size;
        updateSizeCssProperties();
        updatePosition(map.convertLatLngToDivPixel(latLng));
    }

    public void setZIndex(int zIndex) {
        CSS.setZIndex(label, zIndex);
    }

    private void updatePosition(Point newLocationPoint) {
        assert newLocationPoint != null;
        locationPoint = newLocationPoint;
        CSS.setLeft(label, locationPoint.getX() - size / 2);
        CSS.setTop(label, locationPoint.getY() - size / 2);
    }

    public void updateSizeCssProperties() {
        CSS.setWidth(label, size);
        CSS.setHeight(label, size);
        CSS.setBorderRadius(label, size / 2);
    }
}