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

import java.util.HashMap;

import org.thechiselgroup.choosel.core.client.resources.Resource;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.test.BenchmarkResourceSetFactory;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.views.DefaultView;
import org.thechiselgroup.choosel.core.client.views.slots.FirstResourcePropertyResolver;
import org.thechiselgroup.choosel.core.client.views.slots.ResourceSetToValueResolver;
import org.thechiselgroup.choosel.core.client.windows.WindowContentProducer;
import org.thechiselgroup.choosel.protovis.client.PVShape;
import org.thechiselgroup.choosel.visualization_component.chart.client.barchart.BarChartVisualization;
import org.thechiselgroup.choosel.visualization_component.chart.client.piechart.PieChartVisualization;
import org.thechiselgroup.choosel.visualization_component.chart.client.scatterplot.ScatterPlotVisualization;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

public class ComponentExampleEntryPoint implements EntryPoint {

    public void onModuleLoad() {
        ComponentExampleGinjector injector = GWT
                .create(ComponentExampleGinjector.class);

        WindowContentProducer contentProducer = injector
                .getWindowContentProducer();

        ResourceSet resourceSet = BenchmarkResourceSetFactory
                .createResourceSet(20, injector.getResourceSetFactory());

        // un-comment as needed - only one can be active at a time
        // initBarChartView(contentProducer, resourceSet);
        // initPieChartView(contentProducer, resourceSet);
        initScatterPlotView(contentProducer, resourceSet);
    }

    private void initBarChartView(WindowContentProducer contentProducer,
            ResourceSet resourceSet) {

        DefaultView view = createView(contentProducer, BarChartVisualization.ID);

        // view.getViewContentDisplay().setPropertyValue(
        // BarChartVisualization.LAYOUT_PROPERTY, LayoutType.VERTICAL);

        view.getResourceModel().addResourceSet(resourceSet);

        view.getSlotMappingConfiguration().setMapping(
                BarChartVisualization.BAR_LABEL_SLOT,
                new FirstResourcePropertyResolver(
                        BenchmarkResourceSetFactory.TEXT_2));
        view.getSlotMappingConfiguration().setMapping(
                BarChartVisualization.BAR_LENGTH_SLOT,
                new FirstResourcePropertyResolver(
                        BenchmarkResourceSetFactory.NUMBER_2) {
                    @Override
                    public String toString() {
                        return "my axis label"; // example for axis labeling
                    }
                });
    }

    private void initScatterPlotView(WindowContentProducer contentProducer,
            ResourceSet resourceSet) {

        DefaultView view = createView(contentProducer,
                ScatterPlotVisualization.ID);

        view.getResourceModel().addResourceSet(resourceSet);

        view.getSlotMappingConfiguration().setMapping(
                ScatterPlotVisualization.X_POSITION_SLOT,
                new FirstResourcePropertyResolver(
                        BenchmarkResourceSetFactory.NUMBER_1));
        view.getSlotMappingConfiguration().setMapping(
                ScatterPlotVisualization.Y_POSITION_SLOT,
                new FirstResourcePropertyResolver(
                        BenchmarkResourceSetFactory.NUMBER_2));
        view.getSlotMappingConfiguration().setMapping(
                ScatterPlotVisualization.SHAPE_SLOT,
                new ResourceSetToValueResolver() {
                    public Object resolve(
                            LightweightCollection<Resource> resources,
                            String category) {

                        Resource r = resources.iterator().next();
                        Object value = r
                                .getValue(BenchmarkResourceSetFactory.TEXT_2);

                        if (value.equals("category-0")) {
                            return PVShape.DIAMOND;
                        }
                        if (value.equals("category-1")) {
                            return PVShape.SQUARE;
                        }
                        if (value.equals("category-2")) {
                            return PVShape.CIRCLE;
                        }
                        if (value.equals("category-3")) {
                            return PVShape.CROSS;
                        }

                        return PVShape.TRIANGLE;
                    }
                });

        HashMap<String, String> shapeLegend = new HashMap<String, String>();
        shapeLegend.put(PVShape.DIAMOND, "Description A");
        shapeLegend.put(PVShape.SQUARE, "Description B");
        shapeLegend.put(PVShape.CROSS, "Description C");
        shapeLegend.put(PVShape.CIRCLE, "Test");

        view.getViewContentDisplay().setPropertyValue(
                ScatterPlotVisualization.SHAPE_LEGEND_PROPERTY, shapeLegend);
    }

    private void initPieChartView(WindowContentProducer contentProducer,
            ResourceSet resourceSet) {

        DefaultView view = createView(contentProducer, PieChartVisualization.ID);

        // NOTE: the view is configured BEFORE the resources are added
        view.getSlotMappingConfiguration().setMapping(
                PieChartVisualization.PIE_LABEL_SLOT,
                new FirstResourcePropertyResolver(
                        BenchmarkResourceSetFactory.TEXT_2));
        view.getSlotMappingConfiguration().setMapping(
                PieChartVisualization.PIE_ANGLE_SLOT,
                new FirstResourcePropertyResolver(
                        BenchmarkResourceSetFactory.NUMBER_2));

        view.getResourceModel().addResourceSet(resourceSet);
    }

    private DefaultView createView(WindowContentProducer contentProducer,
            String visualizationTypeId) {

        final DefaultView view = (DefaultView) contentProducer
                .createWindowContent(visualizationTypeId);
        view.init();
        RootPanel.get().add(view.asWidget());

        // Set the size of the window, and listen for
        // changes in size.
        view.asWidget().setPixelSize(Window.getClientWidth(),
                Window.getClientHeight() - 5);

        Window.addResizeHandler(new ResizeHandler() {
            @Override
            public void onResize(ResizeEvent event) {
                view.asWidget().setPixelSize(event.getWidth(),
                        event.getHeight());
            }
        });

        return view;
    }
}
