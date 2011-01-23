package org.thechiselgroup.choosel.visualization_component.chart.client.barchart;

import org.thechiselgroup.choosel.core.client.resources.DataType;
import org.thechiselgroup.choosel.core.client.views.slots.Slot;

public final class BarChartVisualization {

    public final static String ID = "org.thechiselgroup.choosel.visualization_component.chart.BarChart";

    public static final Slot BAR_LABEL_SLOT = new Slot("chart-label",
            "Label", DataType.TEXT);

    public static final Slot BAR_LENGTH_SLOT = new Slot("chart-value",
            "Value", DataType.NUMBER);

    private BarChartVisualization() {
    }

}