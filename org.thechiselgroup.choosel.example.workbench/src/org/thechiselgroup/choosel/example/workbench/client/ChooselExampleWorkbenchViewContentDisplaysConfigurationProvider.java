package org.thechiselgroup.choosel.example.workbench.client;

import org.thechiselgroup.choosel.core.client.views.ViewContentDisplaysConfiguration;
import org.thechiselgroup.choosel.visualization_component.chart.client.barchart.BarChartViewContentDisplayFactory;
import org.thechiselgroup.choosel.visualization_component.chart.client.piechart.PieChartViewContentDisplayFactory;
import org.thechiselgroup.choosel.visualization_component.chart.client.scatterplot.ScatterPlotViewContentDisplayFactory;
import org.thechiselgroup.choosel.visualization_component.graph.client.GraphViewContentDisplayFactory;
import org.thechiselgroup.choosel.visualization_component.map.client.MapViewContentDisplayFactory;
import org.thechiselgroup.choosel.visualization_component.text.client.TextViewContentDisplayFactory;
import org.thechiselgroup.choosel.visualization_component.timeline.client.TimeLineViewContentDisplayFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class ChooselExampleWorkbenchViewContentDisplaysConfigurationProvider
        implements Provider<ViewContentDisplaysConfiguration> {

    @Inject
    private BarChartViewContentDisplayFactory barChartDisplayFactory;

    @Inject
    private ScatterPlotViewContentDisplayFactory scatterPlotDisplayFactory;

    @Inject
    private MapViewContentDisplayFactory mapViewContentDisplayFactory;

    @Inject
    private TextViewContentDisplayFactory textViewContentDisplayFactory;

    @Inject
    private PieChartViewContentDisplayFactory pieChartDisplayFactory;

    @Inject
    private GraphViewContentDisplayFactory graphViewContentDisplayFactory;

    @Inject
    private TimeLineViewContentDisplayFactory timelineViewContentDisplayFactory;

    @Override
    public ViewContentDisplaysConfiguration get() {
        ViewContentDisplaysConfiguration result = new ViewContentDisplaysConfiguration();

        result.register(barChartDisplayFactory);
        result.register(pieChartDisplayFactory);
        result.register(scatterPlotDisplayFactory);
        result.register(mapViewContentDisplayFactory);
        result.register(graphViewContentDisplayFactory);
        result.register(textViewContentDisplayFactory);
        result.register(timelineViewContentDisplayFactory);

        return result;
    }

}