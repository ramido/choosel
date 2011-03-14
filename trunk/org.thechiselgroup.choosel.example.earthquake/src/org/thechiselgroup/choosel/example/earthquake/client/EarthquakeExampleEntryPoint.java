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
package org.thechiselgroup.choosel.example.earthquake.client;

import org.thechiselgroup.choosel.core.client.label.IncrementingSuffixLabelFactory;
import org.thechiselgroup.choosel.core.client.resources.DefaultResourceSetFactory;
import org.thechiselgroup.choosel.core.client.resources.ResourceByPropertyMultiCategorizer;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.test.BenchmarkResourceSetFactory;
import org.thechiselgroup.choosel.core.client.views.CompositeViewItemBehavior;
import org.thechiselgroup.choosel.core.client.views.DefaultSelectionModel;
import org.thechiselgroup.choosel.core.client.views.HighlightingViewItemBehavior;
import org.thechiselgroup.choosel.core.client.views.HoverModel;
import org.thechiselgroup.choosel.core.client.views.SelectionModel;
import org.thechiselgroup.choosel.core.client.views.SwitchSelectionOnClickViewItemBehavior;
import org.thechiselgroup.choosel.core.client.views.VisualizationWidget;
import org.thechiselgroup.choosel.visualization_component.chart.client.barchart.BarChartViewContentDisplay;
import org.thechiselgroup.choosel.visualization_component.map.client.MapViewContentDisplay;
import org.thechiselgroup.choosel.visualization_component.timeline.client.TimeLineViewContentDisplay;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

public class EarthquakeExampleEntryPoint implements EntryPoint {

    public void onModuleLoad() {
        ResourceSet resourceSet = BenchmarkResourceSetFactory
                .createResourceSet(20, new DefaultResourceSetFactory());

        HoverModel hoverModel = new HoverModel();
        SelectionModel selectionModel = new DefaultSelectionModel(
                new IncrementingSuffixLabelFactory("test"),
                new DefaultResourceSetFactory());

        CompositeViewItemBehavior viewItemBehavior = new CompositeViewItemBehavior();
        viewItemBehavior.add(new HighlightingViewItemBehavior(hoverModel));
        viewItemBehavior.add(new SwitchSelectionOnClickViewItemBehavior(
                selectionModel));

        MapViewContentDisplay mapContentDisplay = new MapViewContentDisplay();
        VisualizationWidget map = new VisualizationWidget(mapContentDisplay,
                selectionModel.getSelectionProxy(), hoverModel.getResources(),
                viewItemBehavior);

        map.setContentResourceSet(resourceSet);

        CompositeViewItemBehavior behaviors2 = new CompositeViewItemBehavior();
        behaviors2.add(new HighlightingViewItemBehavior(hoverModel));
        behaviors2.add(new SwitchSelectionOnClickViewItemBehavior(
                selectionModel));

        VisualizationWidget barChart = new VisualizationWidget(
                new BarChartViewContentDisplay(),
                selectionModel.getSelectionProxy(), hoverModel.getResources(),
                behaviors2);

        barChart.setContentResourceSet(resourceSet);
        barChart.setCategorizer(new ResourceByPropertyMultiCategorizer(
                BenchmarkResourceSetFactory.TEXT_2));

        CompositeViewItemBehavior behaviors3 = new CompositeViewItemBehavior();
        behaviors3.add(new HighlightingViewItemBehavior(hoverModel));
        behaviors3.add(new SwitchSelectionOnClickViewItemBehavior(
                selectionModel));

        VisualizationWidget timeline = new VisualizationWidget(
                new TimeLineViewContentDisplay(),
                selectionModel.getSelectionProxy(), hoverModel.getResources(),
                behaviors3);

        mapContentDisplay.setMapType(MapViewContentDisplay.MAP_TYPE_PHYSICAL);
        barChart.setSize("400px", "600px");
        map.setSize("700px", "400px");
        timeline.setSize("700px", "200px");

        RootPanel.get("barchart").add(barChart);
        RootPanel.get("map").add(map);
        RootPanel.get("timeline").add(timeline);

        // waaa
        timeline.setContentResourceSet(resourceSet);
    }

}