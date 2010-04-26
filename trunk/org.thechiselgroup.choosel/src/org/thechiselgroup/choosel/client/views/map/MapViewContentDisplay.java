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

import java.util.List;

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
import org.thechiselgroup.choosel.client.views.Layer;
import org.thechiselgroup.choosel.client.views.ResourceItem;
import org.thechiselgroup.choosel.client.views.Slot;
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

    private static final String MEMENTO_CENTER_LATITUDE = "center-latitude";

    private static final String MEMENTO_CENTER_LONGITUDE = "center-longitude";

    private static final String MEMENTO_MAP_TYPE = "type";

    private static final String MEMENTO_MAP_TYPE_HYBRID = "hybrid";

    private static final String MEMENTO_MAP_TYPE_NORMAL = "normal";

    private static final String MEMENTO_MAP_TYPE_PHYSICAL = "physical";

    private static final String MEMENTO_MAP_TYPE_SATELLITE = "satellite";

    private static final String MEMENTO_ZOOM_LEVEL = "zoom-level";

    private MapWidget map;

    private DragEnablerFactory dragEnablerFactory;

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

    @Override
    public ResourceItem createResourceItem(Layer layer, Resource resource) {
	// TODO iterate over path
	// TODO resolve sets
	Resource location = (Resource) layer.getValue(
		SlotResolver.LOCATION_SLOT_ID, resource);

	double latitude = ((Number) location.getValue("latitude"))
		.doubleValue();
	double longitude = ((Number) location.getValue("longitude"))
		.doubleValue();

	LatLng latLng = LatLng.newInstance(latitude, longitude);

	PopupManager popupManager = createPopupManager(layer, resource);

	MapItem mapItem = new MapItem(latLng, resource, hoverModel,
		popupManager, layer, getCallback(), dragEnablerFactory);

	map.addOverlay(mapItem.getOverlay());

	return mapItem;
    }

    @Override
    public Slot[] createSlots() {
	return new Slot[] { new Slot(SlotResolver.DESCRIPTION_SLOT_ID),
		new Slot(SlotResolver.LABEL_SLOT_ID),
		new Slot(SlotResolver.COLOR_SLOT_ID),
		new Slot(SlotResolver.LOCATION_SLOT_ID) };
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
    public void initLayer(Layer layerModel, List<Layer> layers) {
	SlotResolver.createLabelSlotResolver(layerModel);
	SlotResolver.createDescriptionSlotResolver(layerModel);
	SlotResolver.createLocationSlotResolver(layerModel);
	SlotResolver.createColorSlotResolver(layerModel, layers);
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
}
