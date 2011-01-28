package org.thechiselgroup.choosel.visualization_component.graph.client;

import org.thechiselgroup.choosel.core.client.resources.DataType;
import org.thechiselgroup.choosel.core.client.views.slots.Slot;

public final class GraphVisualization {

    public static final Slot NODE_BORDER_COLOR_SLOT = new Slot(
    "nodeBorderColor", "Node Border Color", DataType.COLOR);
    public static final Slot NODE_BACKGROUND_COLOR_SLOT = new Slot(
    "nodeBackgroundColor", "Node Color", DataType.COLOR);
    public static final Slot NODE_LABEL_SLOT = new Slot("nodeLabel",
    "Node Label", DataType.TEXT);

    private GraphVisualization() {
    }

    public final static String ID = "org.thechiselgroup.choosel.visualization_component.graph.Graph";

}