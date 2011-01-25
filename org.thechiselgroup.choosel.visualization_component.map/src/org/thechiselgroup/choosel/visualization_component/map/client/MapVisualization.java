package org.thechiselgroup.choosel.visualization_component.map.client;

import org.thechiselgroup.choosel.core.client.resources.DataType;
import org.thechiselgroup.choosel.core.client.views.slots.Slot;

public final class MapVisualization {

    public final static String ID = "org.thechiselgroup.choosel.visualization_component.Map";

    public final static Slot LAB_SLOT = new Slot("map.item.label", "Label",
            DataType.TEXT);

    public final static Slot COLOR_SLOT = new Slot("map.item.color", "Color",
            DataType.COLOR);

    public final static Slot LOCATION_SLOT = new Slot("map.item.location",
            "Location", DataType.LOCATION);

    private MapVisualization() {
    }

}
