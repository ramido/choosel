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
import org.thechiselgroup.choosel.core.client.util.collections.Delta;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.visualization.model.Slot;
import org.thechiselgroup.choosel.core.client.visualization.model.ViewContentDisplayCallback;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItem;

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

    private void initMapItem(VisualItem viewItem) {
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

    private void removeOverlay(VisualItem viewItem) {
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
    public void update(Delta<VisualItem> delta,
            LightweightCollection<Slot> updatedSlots) {

        // TODO pull up
        if (!map.isAttached()) {
            return;
        }

        for (VisualItem resourceItem : delta.getAddedElements()) {
            initMapItem(resourceItem);
        }

        for (VisualItem resourceItem : delta.getRemovedElements()) {
            removeOverlay(resourceItem);
        }

        // TODO refactor
        if (!updatedSlots.isEmpty()) {
            for (VisualItem viewItem : callback.getVisualItems()) {
                DefaultMapItem mapItem = (DefaultMapItem) viewItem
                        .getDisplayObject();
                for (Slot slot : updatedSlots) {
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

        updateStatusStyling(delta.getUpdatedElements());
    }

    private void updateStatusStyling(LightweightCollection<VisualItem> viewItems) {
        for (VisualItem viewItem : viewItems) {
            ((DefaultMapItem) viewItem.getDisplayObject()).setStatusStyling();
        }
    }

}