package org.thechiselgroup.choosel.visualization_component.chart.client.scatterplot;

import org.thechiselgroup.choosel.core.client.resources.DataType;
import org.thechiselgroup.choosel.core.client.views.slots.Slot;

public final class ScatterPlotVisualization {

    public final static String ID = "org.thechiselgroup.choosel.visualization_component.chart.ScatterPlot";

    public static final Slot Y_POSITION_SLOT = new Slot("y_position", "Y-Axis",
            DataType.NUMBER);

    public static final Slot X_POSITION_SLOT = new Slot("x_position", "X-Axis",
            DataType.NUMBER);

    private ScatterPlotVisualization() {
    }

}