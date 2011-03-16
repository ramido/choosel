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

import org.thechiselgroup.choosel.core.client.views.IconItem;
import org.thechiselgroup.choosel.core.client.views.ViewItem;
import org.thechiselgroup.choosel.core.client.views.ViewItem.Status;
import org.thechiselgroup.choosel.core.client.views.ViewItemInteraction;

import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;

public class MapItem extends IconItem {

    private LabelOverlay overlay;

    public MapItem(ViewItem viewItem, LatLng point) {
        super(viewItem, Map.COLOR_SLOT);

        overlay = new LabelOverlay(point, getLabelValue(), getSize(),
                CSS_RESOURCE_ITEM_ICON, new EventListener() {
                    @Override
                    public void onBrowserEvent(Event event) {
                        // prevent standard map drag on item
                        if (event.getTypeInt() == Event.ONMOUSEDOWN) {
                            event.stopPropagation();
                        }

                        // forward
                        MapItem.this.viewItem
                                .reportInteraction(new ViewItemInteraction(
                                        event));
                    }
                });
    }

    public String getLabelValue() {
        return (String) getSlotValue(Map.LABEL_SLOT);
    }

    public LabelOverlay getOverlay() {
        return overlay;
    }

    public int getSize() {
        return ((Number) getSlotValue(Map.SIZE_SLOT)).intValue();
    }

    public void setDefaultStyle() {
        overlay.setBackgroundColor(getDefaultColor());
        overlay.setBorderColor(calculateBorderColor(getDefaultColor()));
        overlay.setZIndex(Z_INDEX_DEFAULT);
    }

    private void setHighlightedStyle() {
        overlay.setBackgroundColor(getHighlightColor());
        overlay.setBorderColor(calculateBorderColor(getHighlightColor()));
        overlay.setZIndex(Z_INDEX_HIGHLIGHTED);
    }

    private void setSelectedStyle() {
        overlay.setBackgroundColor(getSelectedColor());
        overlay.setBorderColor(calculateBorderColor(getSelectedColor()));
        overlay.setZIndex(Z_INDEX_SELECTED);
    }

    protected void setStatusStyling(Status status) {
        switch (status) {
        case PARTIALLY_HIGHLIGHTED:
        case PARTIALLY_HIGHLIGHTED_SELECTED:
        case HIGHLIGHTED_SELECTED:
        case HIGHLIGHTED: {
            setHighlightedStyle();
        }
            break;
        case DEFAULT: {
            setDefaultStyle();
        }
            break;
        case SELECTED: {
            setSelectedStyle();
        }
            break;
        }
    }

    public void updateColor() {
        overlay.setBackgroundColor(getDefaultColor());
    }

    public void updateLabel() {
        overlay.setLabel(getLabelValue());
    }

    public void updateSize() {
        overlay.setSize(getSize());
    }
}