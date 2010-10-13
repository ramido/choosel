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

import java.util.Set;

import org.thechiselgroup.choosel.client.persistence.Memento;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.ui.CSS;
import org.thechiselgroup.choosel.client.views.AbstractViewContentDisplay;
import org.thechiselgroup.choosel.client.views.DragEnablerFactory;
import org.thechiselgroup.choosel.client.views.ResourceItem;
import org.thechiselgroup.choosel.client.views.Slot;
import org.thechiselgroup.choosel.client.views.SlotResolver;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.maps.client.MapType;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.control.SmallMapControl;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class MapViewContentDisplay extends AbstractViewContentDisplay {

    public static final String LATITUDE = "latitude";

    public static final String LONGITUDE = "longitude";

    private static final String MEMENTO_CENTER_LATITUDE = "center-latitude";

    private static final String MEMENTO_CENTER_LONGITUDE = "center-longitude";

    private static final String MEMENTO_MAP_TYPE = "type";

    private static final String MEMENTO_MAP_TYPE_HYBRID = "hybrid";

    private static final String MEMENTO_MAP_TYPE_NORMAL = "normal";

    private static final String MEMENTO_MAP_TYPE_PHYSICAL = "physical";

    private static final String MEMENTO_MAP_TYPE_SATELLITE = "satellite";

    private static final String MEMENTO_ZOOM_LEVEL = "zoom-level";

    private DragEnablerFactory dragEnablerFactory;

    private MapWidget map;

    @Inject
    public MapViewContentDisplay(DragEnablerFactory dragEnablerFactory) {
        this.dragEnablerFactory = dragEnablerFactory;
    }

    @Override
    public void checkResize() {
        map.checkResize();
    }

    @Override
    public Widget createWidget() {
        map = new MapWidget();

        DOM.setStyleAttribute(map.getElement(), CSS.OVERFLOW, CSS.HIDDEN);

        map.setWidth("100%");
        map.setHeight("100%");

        map.addControl(new SmallMapControl());

        map.addMapType(MapType.getPhysicalMap());
        map.setCurrentMapType(MapType.getHybridMap());
        map.setScrollWheelZoomEnabled(true);

        return map;
    }

    @Override
    public Widget getConfigurationWidget() {
        FlowPanel panel = new FlowPanel();

        final ListBox layoutBox = new ListBox(false);
        layoutBox.setVisibleItemCount(1);

        layoutBox.addItem("Hybrid", MEMENTO_MAP_TYPE_HYBRID);
        layoutBox.addItem("Map", MEMENTO_MAP_TYPE_NORMAL);
        layoutBox.addItem("Satellite", MEMENTO_MAP_TYPE_SATELLITE);
        layoutBox.addItem("Terrain", MEMENTO_MAP_TYPE_PHYSICAL);

        layoutBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                setMapType(layoutBox.getValue(layoutBox.getSelectedIndex()));
            }
        });
        panel.add(layoutBox);

        return panel;
    }

    public String getMapType() {
        MapType mapType = map.getCurrentMapType();
        if (MapType.getNormalMap().equals(mapType)) {
            return MEMENTO_MAP_TYPE_NORMAL;
        } else if (MapType.getSatelliteMap().equals(mapType)) {
            return MEMENTO_MAP_TYPE_SATELLITE;
        } else if (MapType.getPhysicalMap().equals(mapType)) {
            return MEMENTO_MAP_TYPE_PHYSICAL;
        } else if (MapType.getHybridMap().equals(mapType)) {
            return MEMENTO_MAP_TYPE_HYBRID;
        } else {
            throw new RuntimeException(
                    "map type persistence not supported for type "
                            + mapType.getName(false));
        }
    }

    @Override
    public Slot[] getSlots() {
        return new Slot[] { SlotResolver.DESCRIPTION_SLOT,
                SlotResolver.COLOR_SLOT, SlotResolver.LOCATION_SLOT };
    }

    private void initMapItem(ResourceItem resourceItem) {
        // TODO iterate over path
        // TODO resolve sets
        // TODO separate resolvers for latitude and longitude

        Resource location = (Resource) resourceItem
                .getResourceValue(SlotResolver.LOCATION_SLOT);

        double latitude = toDouble(location.getValue(LATITUDE));
        double longitude = toDouble(location.getValue(LONGITUDE));

        LatLng latLng = LatLng.newInstance(latitude, longitude);

        MapItem mapItem = new MapItem(resourceItem, latLng, getCallback(),
                dragEnablerFactory);

        mapItem.setStatusStyling(resourceItem.getStatus());

        map.addOverlay(mapItem.getOverlay());

        resourceItem.setDisplayObject(mapItem);
    }

    private void removeOverlay(ResourceItem resourceItem) {
        map.removeOverlay(((MapItem) resourceItem.getDisplayObject())
                .getOverlay());
    }

    @Override
    public void restore(Memento state) {
        setMapType((String) state.getValue(MEMENTO_MAP_TYPE));
        restoreCenterPosition(state);
        restoreZoomLevel(state);
    }

    private void restoreCenterPosition(Memento state) {
        double centerLatitude = Double.parseDouble((String) state
                .getValue(MEMENTO_CENTER_LATITUDE));
        double centerLongitude = Double.parseDouble((String) state
                .getValue(MEMENTO_CENTER_LONGITUDE));
        map.setCenter(LatLng.newInstance(centerLatitude, centerLongitude));
    }

    private void restoreZoomLevel(Memento state) {
        int zoomLevel = Integer.parseInt((String) state
                .getValue(MEMENTO_ZOOM_LEVEL));
        map.setZoomLevel(zoomLevel);
    }

    @Override
    public Memento save() {
        Memento state = new Memento();

        saveCenterPosition(state);
        saveZoomLevel(state);
        state.setValue(MEMENTO_MAP_TYPE, getMapType());

        return state;
    }

    private void saveCenterPosition(Memento state) {
        LatLng center = map.getCenter();
        state.setValue(MEMENTO_CENTER_LONGITUDE,
                Double.toString(center.getLongitude()));
        state.setValue(MEMENTO_CENTER_LATITUDE,
                Double.toString(center.getLatitude()));
    }

    private void saveZoomLevel(Memento state) {
        state.setValue(MEMENTO_ZOOM_LEVEL, Integer.toString(map.getZoomLevel()));
    }

    public void setMapType(String mapTypeID) {
        if (MEMENTO_MAP_TYPE_NORMAL.equals(mapTypeID)) {
            map.setCurrentMapType(MapType.getNormalMap());
        } else if (MEMENTO_MAP_TYPE_SATELLITE.equals(mapTypeID)) {
            map.setCurrentMapType(MapType.getSatelliteMap());
        } else if (MEMENTO_MAP_TYPE_PHYSICAL.equals(mapTypeID)) {
            map.setCurrentMapType(MapType.getPhysicalMap());
        } else if (MEMENTO_MAP_TYPE_HYBRID.equals(mapTypeID)) {
            map.setCurrentMapType(MapType.getHybridMap());
        } else {
            throw new RuntimeException(
                    "map type persistence not supported for type " + mapTypeID);
        }
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
    public void update(Set<ResourceItem> addedResourceItems,
            Set<ResourceItem> updatedResourceItems,
            Set<ResourceItem> removedResourceItems, Set<Slot> changedSlots) {

        for (ResourceItem resourceItem : addedResourceItems) {
            initMapItem(resourceItem);
        }

        for (ResourceItem resourceItem : removedResourceItems) {
            removeOverlay(resourceItem);
        }

        updateStatusStyling(updatedResourceItems);
    }

    private void updateStatusStyling(Set<? extends ResourceItem> resourceItems) {
        for (ResourceItem resourceItem : resourceItems) {
            ((MapItem) resourceItem.getDisplayObject())
                    .setStatusStyling(resourceItem.getStatus());
        }
    }
}
