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
package org.thechiselgroup.choosel.visualization_component.map.client;

import org.thechiselgroup.choosel.core.client.resources.Resource;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.views.model.Slot;
import org.thechiselgroup.choosel.core.client.views.model.ViewContentDisplayCallback;
import org.thechiselgroup.choosel.core.client.views.model.ViewItem;

import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.geom.LatLng;

public class DefaultMapRenderer implements MapRenderer {

    private MapWidget map;

    private ViewContentDisplayCallback callback;

    @Override
    public void init(MapWidget map, ViewContentDisplayCallback callback) {
        this.map = map;
        this.callback = callback;
    }

    private void initMapItem(ViewItem viewItem) {
        // TODO iterate over path
        // TODO resolve sets
        // TODO separate resolvers for latitude and longitude

        Resource location = (Resource) viewItem.getValue(Map.LOCATION);

        double latitude = toDouble(location.getValue(Map.LATITUDE));
        double longitude = toDouble(location.getValue(Map.LONGITUDE));

        LatLng latLng = LatLng.newInstance(latitude, longitude);

        DefaultMapItem mapItem = new DefaultMapItem(viewItem, latLng);

        map.addOverlay(mapItem.getOverlay());

        viewItem.setDisplayObject(mapItem);
    }

    @Override
    public void onAttach() {
    }

    @Override
    public void onDetach() {
    }

    private void removeOverlay(ViewItem viewItem) {
        map.removeOverlay(((DefaultMapItem) viewItem.getDisplayObject())
                .getOverlay());
    }

    // TODO move to library class
    private double toDouble(Object value) {
        assert value != null;

        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }

        if (value instanceof String) {
            return Double.parseDouble((String) value);
        }

        throw new IllegalArgumentException("" + value
                + " could not be converted to double");
    }

    @Override
    public void update(LightweightCollection<ViewItem> addedViewItems,
            LightweightCollection<ViewItem> updatedViewItems,
            LightweightCollection<ViewItem> removedViewItems,
            LightweightCollection<Slot> changedSlots) {

        // TODO pull up
        if (!map.isAttached()) {
            return;
        }

        for (ViewItem resourceItem : addedViewItems) {
            initMapItem(resourceItem);
        }

        for (ViewItem resourceItem : removedViewItems) {
            removeOverlay(resourceItem);
        }

        // TODO refactor
        if (!changedSlots.isEmpty()) {
            for (ViewItem viewItem : callback.getViewItems()) {
                DefaultMapItem mapItem = (DefaultMapItem) viewItem
                        .getDisplayObject();
                for (Slot slot : changedSlots) {
                    if (slot.equals(Map.BORDER_COLOR)) {
                        mapItem.updateBorderColor();
                    } else if (slot.equals(Map.COLOR)) {
                        mapItem.updateColor();
                    } else if (slot.equals(Map.RADIUS)) {
                        mapItem.updateRadius();
                    }
                }
            }
        }

        updateStatusStyling(updatedViewItems);
    }

    private void updateStatusStyling(LightweightCollection<ViewItem> viewItems) {
        for (ViewItem viewItem : viewItems) {
            ((DefaultMapItem) viewItem.getDisplayObject()).setStatusStyling();
        }
    }

}