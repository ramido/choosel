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

import org.thechiselgroup.choosel.core.client.persistence.Memento;
import org.thechiselgroup.choosel.core.client.persistence.PersistableRestorationService;
import org.thechiselgroup.choosel.core.client.resources.Resource;
import org.thechiselgroup.choosel.core.client.resources.persistence.ResourceSetAccessor;
import org.thechiselgroup.choosel.core.client.resources.persistence.ResourceSetCollector;
import org.thechiselgroup.choosel.core.client.ui.CSS;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.views.AbstractViewContentDisplay;
import org.thechiselgroup.choosel.core.client.views.DragEnablerFactory;
import org.thechiselgroup.choosel.core.client.views.SidePanelSection;
import org.thechiselgroup.choosel.core.client.views.ViewItem;
import org.thechiselgroup.choosel.core.client.views.slots.Slot;

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
    public String getName() {
        return "Map";
    }

    @Override
    public SidePanelSection[] getSidePanelSections() {
        FlowPanel mapSettingsMap = new FlowPanel();

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
        mapSettingsMap.add(layoutBox);

        return new SidePanelSection[] { new SidePanelSection("Map Settings",
                mapSettingsMap), };
    }

    @Override
    public Slot[] getSlots() {
        return new Slot[] { MapVisualization.LAB_SLOT,
                MapVisualization.COLOR_SLOT, MapVisualization.LOCATION_SLOT };
    }

    private void initMapItem(ViewItem resourceItem) {
        // TODO iterate over path
        // TODO resolve sets
        // TODO separate resolvers for latitude and longitude

        Resource location = (Resource) resourceItem
                .getSlotValue(MapVisualization.LOCATION_SLOT);

        double latitude = toDouble(location.getValue(LATITUDE));
        double longitude = toDouble(location.getValue(LONGITUDE));

        LatLng latLng = LatLng.newInstance(latitude, longitude);

        MapItem mapItem = new MapItem(resourceItem, latLng, getCallback(),
                dragEnablerFactory);

        mapItem.setStatusStyling(resourceItem.getStatus());

        map.addOverlay(mapItem.getOverlay());

        resourceItem.setDisplayObject(mapItem);
    }

    private void removeOverlay(ViewItem resourceItem) {
        map.removeOverlay(((MapItem) resourceItem.getDisplayObject())
                .getOverlay());
    }

    @Override
    public void restore(Memento state,
            PersistableRestorationService restorationService,
            ResourceSetAccessor accessor) {

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
    public Memento save(ResourceSetCollector resourceSetCollector) {
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
    public void update(LightweightCollection<ViewItem> addedResourceItems,
            LightweightCollection<ViewItem> updatedResourceItems,
            LightweightCollection<ViewItem> removedResourceItems,
            LightweightCollection<Slot> changedSlots) {

        for (ViewItem resourceItem : addedResourceItems) {
            initMapItem(resourceItem);
        }

        for (ViewItem resourceItem : removedResourceItems) {
            removeOverlay(resourceItem);
        }

        // XXX performance of toList
        if (changedSlots.toList().contains(MapVisualization.LAB_SLOT)) {
            for (ViewItem resourceItem : getCallback().getViewItems()) {
                ((MapItem) resourceItem.getDisplayObject()).updateLabel();
            }
        }

        updateStatusStyling(updatedResourceItems);
    }

    private void updateStatusStyling(
            LightweightCollection<ViewItem> resourceItems) {

        for (ViewItem resourceItem : resourceItems) {
            ((MapItem) resourceItem.getDisplayObject())
                    .setStatusStyling(resourceItem.getStatus());
        }
    }
}
