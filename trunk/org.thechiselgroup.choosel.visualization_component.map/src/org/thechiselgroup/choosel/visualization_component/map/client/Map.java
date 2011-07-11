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

import static org.thechiselgroup.choosel.core.client.util.collections.Delta.createAddedDelta;

import org.thechiselgroup.choosel.core.client.persistence.Memento;
import org.thechiselgroup.choosel.core.client.persistence.PersistableRestorationService;
import org.thechiselgroup.choosel.core.client.resources.persistence.ResourceSetAccessor;
import org.thechiselgroup.choosel.core.client.resources.persistence.ResourceSetCollector;
import org.thechiselgroup.choosel.core.client.ui.CSS;
import org.thechiselgroup.choosel.core.client.ui.SidePanelSection;
import org.thechiselgroup.choosel.core.client.util.DataType;
import org.thechiselgroup.choosel.core.client.util.collections.Delta;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollections;
import org.thechiselgroup.choosel.core.client.visualization.model.AbstractViewContentDisplay;
import org.thechiselgroup.choosel.core.client.visualization.model.Slot;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItem;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.maps.client.MapType;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.control.MapTypeControl;
import com.google.gwt.maps.client.control.SmallMapControl;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class Map extends AbstractViewContentDisplay {

    public final static String ID = "org.thechiselgroup.choosel.visualization_component.Map";

    public final static Slot RADIUS = new Slot("radius", "Radius",
            DataType.NUMBER);

    public final static Slot COLOR = new Slot("color", "Color", DataType.COLOR);

    public final static Slot BORDER_COLOR = new Slot("borderColor",
            "Border Color", DataType.COLOR);

    public final static Slot LOCATION = new Slot("location", "Location",
            DataType.LOCATION);

    // TODO should just be a comparator
    // positive integer
    public final static Slot Z_INDEX = new Slot("zIndex", "Z Index",
            DataType.NUMBER);

    public static final Slot[] SLOTS = new Slot[] { LOCATION, COLOR,
            BORDER_COLOR, RADIUS, Z_INDEX };

    public static final String LATITUDE = "latitude";

    public static final String LONGITUDE = "longitude";

    private static final String MEMENTO_CENTER_LATITUDE = "center-latitude";

    private static final String MEMENTO_CENTER_LONGITUDE = "center-longitude";

    private static final String MEMENTO_MAP_TYPE = "type";

    public static final String MAP_TYPE_HYBRID = "hybrid";

    public static final String MAP_TYPE_NORMAL = "normal";

    public static final String MAP_TYPE_PHYSICAL = "physical";

    public static final String MAP_TYPE_SATELLITE = "satellite";

    private static final String MEMENTO_ZOOM_LEVEL = "zoom-level";

    private MapWidget map;

    private MapRenderer renderer;

    public Map(MapRenderer renderer) {
        this.renderer = renderer;
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
        map.addControl(new MapTypeControl());

        map.addMapType(MapType.getPhysicalMap());
        map.setCurrentMapType(MapType.getPhysicalMap());
        map.setScrollWheelZoomEnabled(true);

        // TODO pull up
        map.addAttachHandler(new Handler() {
            @Override
            public void onAttachOrDetach(AttachEvent event) {
                if (event.isAttached()) {
                    onAttach();
                } else {
                    onDetach();
                }
            }
        });

        renderer.init(map, callback);

        return map;
    }

    public LatLng getCenter() {
        return map.getCenter();
    }

    public String getMapType() {
        MapType mapType = map.getCurrentMapType();
        if (MapType.getNormalMap().equals(mapType)) {
            return MAP_TYPE_NORMAL;
        } else if (MapType.getSatelliteMap().equals(mapType)) {
            return MAP_TYPE_SATELLITE;
        } else if (MapType.getPhysicalMap().equals(mapType)) {
            return MAP_TYPE_PHYSICAL;
        } else if (MapType.getHybridMap().equals(mapType)) {
            return MAP_TYPE_HYBRID;
        } else {
            throw new RuntimeException(
                    "map type persistence not supported for type "
                            + mapType.getName(false));
        }
    }

    public MapWidget getMapWidget() {
        return map;
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

        layoutBox.addItem("Hybrid", MAP_TYPE_HYBRID);
        layoutBox.addItem("Map", MAP_TYPE_NORMAL);
        layoutBox.addItem("Satellite", MAP_TYPE_SATELLITE);
        layoutBox.addItem("Terrain", MAP_TYPE_PHYSICAL);

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
        return SLOTS;
    }

    public int getZoomLevel() {
        return map.getZoomLevel();
    }

    // TODO pull up
    protected void onAttach() {
        renderer.onAttach();

        // add all view items
        update(createAddedDelta(callback.getVisualItems()),
                LightweightCollections.<Slot> emptyCollection());
    }

    // TODO pull up
    protected void onDetach() {
        renderer.onDetach();

        // might have been disposed (then callback would be null)
        // XXX broken, TODO reactivate -- what is this for?
        // if (callback != null) {
        // // remove all view items
        // update(LightweightCollections.<ViewItem> emptyCollection(),
        // LightweightCollections.<ViewItem> emptyCollection(),
        // callback.getViewItems(),
        // LightweightCollections.<Slot> emptyCollection());
        // }
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
        setCenter(LatLng.newInstance(centerLatitude, centerLongitude));
    }

    private void restoreZoomLevel(Memento state) {
        setZoomLevel(Integer.parseInt((String) state
                .getValue(MEMENTO_ZOOM_LEVEL)));
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
        LatLng center = getCenter();
        state.setValue(MEMENTO_CENTER_LONGITUDE,
                Double.toString(center.getLongitude()));
        state.setValue(MEMENTO_CENTER_LATITUDE,
                Double.toString(center.getLatitude()));
    }

    private void saveZoomLevel(Memento state) {
        state.setValue(MEMENTO_ZOOM_LEVEL, Integer.toString(getZoomLevel()));
    }

    public void setCenter(LatLng center) {
        map.setCenter(center);
    }

    public void setMapType(String mapTypeID) {
        if (MAP_TYPE_NORMAL.equals(mapTypeID)) {
            map.setCurrentMapType(MapType.getNormalMap());
        } else if (MAP_TYPE_SATELLITE.equals(mapTypeID)) {
            map.setCurrentMapType(MapType.getSatelliteMap());
        } else if (MAP_TYPE_PHYSICAL.equals(mapTypeID)) {
            map.setCurrentMapType(MapType.getPhysicalMap());
        } else if (MAP_TYPE_HYBRID.equals(mapTypeID)) {
            map.setCurrentMapType(MapType.getHybridMap());
        } else {
            throw new RuntimeException(
                    "map type persistence not supported for type " + mapTypeID);
        }
    }

    /**
     * Zoom levels range from 0 (zoomed out) to 21 (max zoomed in). The center
     * position of the map stays the same.
     * 
     * @see <a href=
     *      "http://code.google.com/apis/maps/documentation/staticmaps/#Zoomlevels"
     *      >Google Maps API Zoom Levels</a>
     */
    public void setZoomLevel(int zoomLevel) {
        LatLng center = getCenter();
        map.setZoomLevel(zoomLevel);
        setCenter(center);
    }

    @Override
    public void update(Delta<VisualItem> delta,
            LightweightCollection<Slot> updatedSlots) {

        // TODO pull up
        if (!map.isAttached()) {
            return;
        }

        renderer.update(delta, updatedSlots);
    }

}