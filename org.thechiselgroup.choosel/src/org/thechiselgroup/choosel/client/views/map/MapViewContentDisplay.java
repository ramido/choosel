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

import org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants;
import org.thechiselgroup.choosel.client.persistence.Memento;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ui.DetailsWidgetHelper;
import org.thechiselgroup.choosel.client.ui.CSS;
import org.thechiselgroup.choosel.client.ui.popup.PopupManager;
import org.thechiselgroup.choosel.client.ui.popup.PopupManagerFactory;
import org.thechiselgroup.choosel.client.views.AbstractViewContentDisplay;
import org.thechiselgroup.choosel.client.views.DragEnablerFactory;
import org.thechiselgroup.choosel.client.views.ResourceItem;
import org.thechiselgroup.choosel.client.views.ResourceItemValueResolver;
import org.thechiselgroup.choosel.client.views.SlotResolver;

import com.google.gwt.maps.client.MapType;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.control.MenuMapTypeControl;
import com.google.gwt.maps.client.control.SmallMapControl;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.name.Named;

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
    public MapViewContentDisplay(
            PopupManagerFactory popupManagerFactory,
            DetailsWidgetHelper detailsWidgetHelper,
            @Named(ChooselInjectionConstants.HOVER_MODEL) ResourceSet hoverModel,
            DragEnablerFactory dragEnablerFactory) {

        super(popupManagerFactory, detailsWidgetHelper, hoverModel);

        this.dragEnablerFactory = dragEnablerFactory;
    }

    @Override
    public void checkResize() {
        map.checkResize();
    }

    // TODO test
    @Override
    public ResourceItem createResourceItem(ResourceItemValueResolver resolver,
            String category, ResourceSet resources) {
        // TODO iterate over path
        // TODO resolve sets
        // TODO separate resolvers for latitude and longitude

        Resource location = (Resource) resolver.resolve(
                SlotResolver.LOCATION_SLOT, category, resources);

        double latitude = toDouble(location.getValue(LATITUDE));
        double longitude = toDouble(location.getValue(LONGITUDE));

        LatLng latLng = LatLng.newInstance(latitude, longitude);

        PopupManager popupManager = createPopupManager(resolver, resources);

        MapItem mapItem = new MapItem(category, latLng, resources, hoverModel,
                popupManager, resolver, getCallback(), dragEnablerFactory);

        map.addOverlay(mapItem.getOverlay());

        return mapItem;
    }

    @Override
    public Widget createWidget() {
        map = new MapWidget();

        DOM.setStyleAttribute(map.getElement(), CSS.OVERFLOW, CSS.HIDDEN);

        map.setWidth("100%");
        map.setHeight("100%");

        map.addControl(new SmallMapControl());
        map.addControl(new MenuMapTypeControl());

        map.addMapType(MapType.getPhysicalMap());
        map.setCurrentMapType(MapType.getHybridMap());
        map.setScrollWheelZoomEnabled(true);

        return map;
    }

    @Override
    public String[] getSlotIDs() {
        return new String[] { SlotResolver.DESCRIPTION_SLOT,
                SlotResolver.LABEL_SLOT, SlotResolver.COLOR_SLOT,
                SlotResolver.LOCATION_SLOT };
    }

    @Override
    public void removeResourceItem(ResourceItem resourceItem) {
        map.removeOverlay(((MapItem) resourceItem).getOverlay());
    }

    @Override
    public void restore(Memento state) {
        restoreMapType(state);
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

    private void restoreMapType(Memento state) {
        String mapTypeID = (String) state.getValue(MEMENTO_MAP_TYPE);
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
        saveMapType(state);

        return state;
    }

    private void saveCenterPosition(Memento state) {
        LatLng center = map.getCenter();
        state.setValue(MEMENTO_CENTER_LONGITUDE, Double.toString(center
                .getLongitude()));
        state.setValue(MEMENTO_CENTER_LATITUDE, Double.toString(center
                .getLatitude()));
    }

    private void saveMapType(Memento state) {
        String mapTypeID;
        MapType mapType = map.getCurrentMapType();
        if (MapType.getNormalMap().equals(mapType)) {
            mapTypeID = MEMENTO_MAP_TYPE_NORMAL;
        } else if (MapType.getSatelliteMap().equals(mapType)) {
            mapTypeID = MEMENTO_MAP_TYPE_SATELLITE;
        } else if (MapType.getPhysicalMap().equals(mapType)) {
            mapTypeID = MEMENTO_MAP_TYPE_PHYSICAL;
        } else if (MapType.getHybridMap().equals(mapType)) {
            mapTypeID = MEMENTO_MAP_TYPE_HYBRID;
        } else {
            throw new RuntimeException(
                    "map type persistence not supported for type "
                            + mapType.getName(false));
        }
        state.setValue(MEMENTO_MAP_TYPE, mapTypeID);
    }

    private void saveZoomLevel(Memento state) {
        state
                .setValue(MEMENTO_ZOOM_LEVEL, Integer.toString(map
                        .getZoomLevel()));
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
}
