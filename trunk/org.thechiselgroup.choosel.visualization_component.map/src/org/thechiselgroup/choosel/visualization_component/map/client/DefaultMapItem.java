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

import org.thechiselgroup.choosel.core.client.ui.Color;
import org.thechiselgroup.choosel.core.client.views.model.ViewItem;
import org.thechiselgroup.choosel.core.client.views.model.ViewItemInteraction;

import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;

public class DefaultMapItem {

    private DefaultOverlay overlay;

    private final ViewItem viewItem;

    public DefaultMapItem(ViewItem viewItem, LatLng point) {
        this.viewItem = viewItem;

        overlay = new DefaultOverlay(point, getRadius(), getColor(),
                getBorderColor(), getZIndex(), new EventListener() {
                    @Override
                    public void onBrowserEvent(Event event) {
                        // prevent standard map drag on item
                        if (event.getTypeInt() == Event.ONMOUSEDOWN) {
                            event.stopPropagation();
                        }

                        // forward
                        DefaultMapItem.this.viewItem
                                .reportInteraction(new ViewItemInteraction(
                                        event));
                    }
                });
    }

    private Color getBorderColor() {
        return viewItem.<Color> getValue(Map.BORDER_COLOR);
    }

    private Color getColor() {
        return viewItem.<Color> getValue(Map.COLOR);
    }

    public DefaultOverlay getOverlay() {
        return overlay;
    }

    private int getRadius() {
        return viewItem.<Number> getValue(Map.RADIUS).intValue();
    }

    private int getZIndex() {
        return viewItem.<Number> getValue(Map.Z_INDEX).intValue();
    }

    protected void setStatusStyling() {
        updateColor();
        updateBorderColor();
        updateZIndex();
        // XXX all properties should be taken into account...
    }

    public void updateZIndex() {
        overlay.setZIndex(getZIndex());
    }

    public void updateBorderColor() {
        overlay.setBorderColor(getBorderColor());
    }

    public void updateColor() {
        overlay.setColor(getColor());
    }

    public void updateRadius() {
        overlay.setRadius(getRadius());
    }
}