package org.thechiselgroup.choosel.example.workbench.client;

import org.thechiselgroup.choosel.core.client.views.ViewContentDisplaysConfiguration;
import org.thechiselgroup.choosel.visualization_component.chart.client.barchart.BarChartViewContentDisplayFactory;
import org.thechiselgroup.choosel.visualization_component.chart.client.piechart.PieChartViewContentDisplayFactory;
import org.thechiselgroup.choosel.visualization_component.chart.client.scatterplot.ScatterPlotViewContentDisplayFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class ChooselExampleWorkbenchViewContentDisplaysConfigurationProvider implements
        Provider<ViewContentDisplaysConfiguration> {

    @Inject
    private BarChartViewContentDisplayFactory barChartDisplayFactory;

    @Inject
    private ScatterPlotViewContentDisplayFactory scatterPlotDisplayFactory;

    @Inject
    private PieChartViewContentDisplayFactory pieChartDisplayFactory;

    @Override
    public ViewContentDisplaysConfiguration get() {
        ViewContentDisplaysConfiguration result = new ViewContentDisplaysConfiguration();

        result.register(barChartDisplayFactory);
        result.register(pieChartDisplayFactory);
        result.register(scatterPlotDisplayFactory);

        return result;
    }

}