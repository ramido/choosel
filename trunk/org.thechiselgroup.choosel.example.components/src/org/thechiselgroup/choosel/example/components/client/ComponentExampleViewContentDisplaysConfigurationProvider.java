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
package org.thechiselgroup.choosel.example.components.client;

import org.thechiselgroup.choosel.core.client.views.ViewContentDisplaysConfiguration;
import org.thechiselgroup.choosel.visualization_component.chart.client.barchart.BarChartViewContentDisplayFactory;
import org.thechiselgroup.choosel.visualization_component.chart.client.piechart.PieChartViewContentDisplayFactory;
import org.thechiselgroup.choosel.visualization_component.chart.client.scatterplot.ScatterPlotViewContentDisplayFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class ComponentExampleViewContentDisplaysConfigurationProvider implements
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