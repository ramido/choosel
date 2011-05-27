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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.thechiselgroup.choosel.core.client.label.IncrementingSuffixLabelFactory;
import org.thechiselgroup.choosel.core.client.resources.DataType;
import org.thechiselgroup.choosel.core.client.resources.DefaultResourceSetFactory;
import org.thechiselgroup.choosel.core.client.resources.ResourceByPropertyMultiCategorizer;
import org.thechiselgroup.choosel.core.client.resources.ResourceByUriMultiCategorizer;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.resources.ui.SimpleDetailsWidgetHelper;
import org.thechiselgroup.choosel.core.client.test.BenchmarkResourceSetFactory;
import org.thechiselgroup.choosel.core.client.ui.CSS;
import org.thechiselgroup.choosel.core.client.ui.Color;
import org.thechiselgroup.choosel.core.client.ui.popup.DefaultPopupFactory;
import org.thechiselgroup.choosel.core.client.ui.popup.DefaultPopupManagerFactory;
import org.thechiselgroup.choosel.core.client.util.math.SumCalculation;
import org.thechiselgroup.choosel.core.client.views.VisualizationWidget;
import org.thechiselgroup.choosel.core.client.views.behaviors.CompositeViewItemBehavior;
import org.thechiselgroup.choosel.core.client.views.behaviors.HighlightingViewItemBehavior;
import org.thechiselgroup.choosel.core.client.views.behaviors.PopupWithHighlightingViewItemBehavior;
import org.thechiselgroup.choosel.core.client.views.behaviors.SwitchSelectionOnClickViewItemBehavior;
import org.thechiselgroup.choosel.core.client.views.model.DefaultSelectionModel;
import org.thechiselgroup.choosel.core.client.views.model.HighlightingModel;
import org.thechiselgroup.choosel.core.client.views.model.SelectionModel;
import org.thechiselgroup.choosel.core.client.views.model.ViewItem.Subset;
import org.thechiselgroup.choosel.core.client.views.resolvers.CalculationResolver;
import org.thechiselgroup.choosel.core.client.views.resolvers.FirstResourcePropertyResolver;
import org.thechiselgroup.choosel.core.client.views.resolvers.FixedValueResolver;
import org.thechiselgroup.choosel.core.client.views.resolvers.ResourceCountResolver;
import org.thechiselgroup.choosel.core.client.views.resolvers.SubsetDelegatingValueResolver;
import org.thechiselgroup.choosel.core.client.views.resolvers.ViewItemStatusResolver;
import org.thechiselgroup.choosel.core.client.views.resolvers.ViewItemStatusResolver.StatusRule;
import org.thechiselgroup.choosel.core.client.views.sorting.ViewItemDoubleComparator;
import org.thechiselgroup.choosel.core.client.views.sorting.ViewItemStringSlotComparator;
import org.thechiselgroup.choosel.protovis.client.PVShape;
import org.thechiselgroup.choosel.visualization_component.chart.client.barchart.BarChart;
import org.thechiselgroup.choosel.visualization_component.chart.client.piechart.PieChart;
import org.thechiselgroup.choosel.visualization_component.chart.client.scatterplot.ScatterPlot;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.UmbrellaException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootPanel;

public class ComponentExampleEntryPoint implements EntryPoint {

    private static final int CONTROL_HEIGHT = 25;

    private static final Color COLOR_DEFAULT = new Color(18, 64, 171, 0.6);

    private static final Color COLOR_DEFAULT_BORDER = new Color(6, 38, 111);

    private static final Color COLOR_SELECTION = new Color(255, 19, 0, 0.8);

    private static final Color COLOR_SELECTION_BORDER = new Color(166, 12, 0);

    private static final Color COLOR_HIGHLIGHTED = new Color(255, 250, 0, 0.9);

    private static final Color COLOR_HIGHLIGHTED_BORDER = new Color(166, 163, 0);

    private static final ViewItemStatusResolver COLOR_RESOLVER = new ViewItemStatusResolver(
            COLOR_DEFAULT, DataType.COLOR, StatusRule.fullOrPartial(
                    COLOR_HIGHLIGHTED, Subset.HIGHLIGHTED),
            StatusRule.fullOrPartial(COLOR_SELECTION, Subset.SELECTED));

    private VisualizationWidget<BarChart> barChart;

    private VisualizationWidget<PieChart> pieChart;

    private FlowPanel chartControl;

    private VisualizationWidget<ScatterPlot> scatterPlot;

    private void createScatterPlot(ResourceSet resourceSet,
            HighlightingModel hoverModel, SelectionModel selectionModel) {

        // behaviors: how the view reacts to user interactions
        CompositeViewItemBehavior barChartBehaviors = new CompositeViewItemBehavior();
        barChartBehaviors.add(new HighlightingViewItemBehavior(hoverModel));
        barChartBehaviors.add(new SwitchSelectionOnClickViewItemBehavior(
                selectionModel));
        barChartBehaviors.add(new PopupWithHighlightingViewItemBehavior(
                new SimpleDetailsWidgetHelper(),
                new DefaultPopupManagerFactory(new DefaultPopupFactory()),
                hoverModel));

        // create visualization
        scatterPlot = new VisualizationWidget<ScatterPlot>(new ScatterPlot(),
                selectionModel.getSelectionProxy(), hoverModel.getResources(),
                barChartBehaviors);

        // configure visual mappings
        scatterPlot.setResolver(
                ScatterPlot.COLOR,
                new ViewItemStatusResolver(COLOR_DEFAULT, DataType.COLOR,
                        StatusRule.fullOrPartial(COLOR_HIGHLIGHTED,
                                Subset.HIGHLIGHTED), StatusRule.full(
                                COLOR_SELECTION, Subset.SELECTED)));
        scatterPlot.setResolver(
                ScatterPlot.BORDER_COLOR,
                new ViewItemStatusResolver(COLOR_DEFAULT_BORDER,
                        DataType.COLOR, StatusRule.full(COLOR_SELECTION_BORDER,
                                Subset.SELECTED), StatusRule.fullOrPartial(
                                COLOR_HIGHLIGHTED_BORDER, Subset.HIGHLIGHTED)));
        scatterPlot.setResolver(ScatterPlot.X_POSITION,
                new CalculationResolver(BenchmarkResourceSetFactory.NUMBER_2,
                        new SumCalculation()));
        scatterPlot.setResolver(ScatterPlot.Y_POSITION,
                new CalculationResolver(BenchmarkResourceSetFactory.NUMBER_1,
                        new SumCalculation()));
        scatterPlot.setResolver(ScatterPlot.SIZE, new FixedValueResolver(10,
                DataType.NUMBER));
        // TODO shape variable
        scatterPlot.setResolver(ScatterPlot.SHAPE, new FixedValueResolver(
                PVShape.CIRCLE, DataType.SHAPE));

        // set resources
        scatterPlot.setContentResourceSet(resourceSet);

        // TODO enable shape legend
    }

    private void createPieChart(ResourceSet resourceSet,
            HighlightingModel hoverModel, SelectionModel selectionModel) {

        // behaviors: how the view reacts to user interactions
        CompositeViewItemBehavior barChartBehaviors = new CompositeViewItemBehavior();
        barChartBehaviors.add(new HighlightingViewItemBehavior(hoverModel));
        barChartBehaviors.add(new SwitchSelectionOnClickViewItemBehavior(
                selectionModel));
        barChartBehaviors.add(new PopupWithHighlightingViewItemBehavior(
                new SimpleDetailsWidgetHelper(),
                new DefaultPopupManagerFactory(new DefaultPopupFactory()),
                hoverModel));

        // create visualization
        pieChart = new VisualizationWidget<PieChart>(new PieChart(),
                selectionModel.getSelectionProxy(), hoverModel.getResources(),
                barChartBehaviors);

        // configure visual mappings
        pieChart.setResolver(
                PieChart.COLOR,
                new ViewItemStatusResolver(COLOR_DEFAULT, DataType.COLOR,
                        StatusRule.fullOrPartial(COLOR_HIGHLIGHTED,
                                Subset.HIGHLIGHTED), StatusRule.full(
                                COLOR_SELECTION, Subset.SELECTED)));
        pieChart.setResolver(
                PieChart.BORDER_COLOR,
                new ViewItemStatusResolver(COLOR_DEFAULT_BORDER,
                        DataType.COLOR, StatusRule.full(COLOR_SELECTION_BORDER,
                                Subset.SELECTED), StatusRule.fullOrPartial(
                                COLOR_HIGHLIGHTED_BORDER, Subset.HIGHLIGHTED)));
        pieChart.setResolver(PieChart.VALUE, new CalculationResolver(
                BenchmarkResourceSetFactory.NUMBER_2, new SumCalculation()));
        pieChart.setResolver(PieChart.PARTIAL_VALUE,
                new SubsetDelegatingValueResolver(PieChart.VALUE,
                        Subset.SELECTED));
        pieChart.setResolver(PieChart.PARTIAL_COLOR, COLOR_RESOLVER);
        pieChart.setResolver(PieChart.PARTIAL_BORDER_COLOR,
                new FixedValueResolver(COLOR_SELECTION_BORDER, DataType.COLOR));

        // set resources
        pieChart.setContentResourceSet(resourceSet);
    }

    private void createBarChart(ResourceSet resourceSet,
            HighlightingModel hoverModel, SelectionModel selectionModel) {

        // behaviors: how the view reacts to user interactions
        CompositeViewItemBehavior barChartBehaviors = new CompositeViewItemBehavior();
        barChartBehaviors.add(new HighlightingViewItemBehavior(hoverModel));
        barChartBehaviors.add(new SwitchSelectionOnClickViewItemBehavior(
                selectionModel));
        barChartBehaviors.add(new PopupWithHighlightingViewItemBehavior(
                new SimpleDetailsWidgetHelper(),
                new DefaultPopupManagerFactory(new DefaultPopupFactory()),
                hoverModel));

        // create visualization
        barChart = new VisualizationWidget<BarChart>(new BarChart(),
                selectionModel.getSelectionProxy(), hoverModel.getResources(),
                barChartBehaviors);

        // configure visual mappings
        barChart.setResolver(
                BarChart.BAR_COLOR,
                new ViewItemStatusResolver(COLOR_DEFAULT, DataType.COLOR,
                        StatusRule.fullOrPartial(COLOR_HIGHLIGHTED,
                                Subset.HIGHLIGHTED), StatusRule.full(
                                COLOR_SELECTION, Subset.SELECTED)));
        barChart.setResolver(
                BarChart.BAR_BORDER_COLOR,
                new ViewItemStatusResolver(COLOR_DEFAULT_BORDER,
                        DataType.COLOR, StatusRule.full(COLOR_SELECTION_BORDER,
                                Subset.SELECTED), StatusRule.fullOrPartial(
                                COLOR_HIGHLIGHTED_BORDER, Subset.HIGHLIGHTED)));
        barChart.setResolver(BarChart.PARTIAL_BAR_LENGTH,
                new SubsetDelegatingValueResolver(BarChart.BAR_LENGTH,
                        Subset.SELECTED));
        barChart.setResolver(BarChart.PARTIAL_BAR_COLOR, COLOR_RESOLVER);
        barChart.setResolver(BarChart.PARTIAL_BAR_BORDER_COLOR,
                new FixedValueResolver(COLOR_SELECTION_BORDER, DataType.COLOR));

        // default settings
        doNotGroupBarChart();

        // configure properties, e.g.
        // barChart.setPropertyValue(BarChart.LAYOUT_PROPERTY,
        // LayoutType.VERTICAL);

        // set resources
        barChart.setContentResourceSet(resourceSet);
    }

    private void createChartControl() {
        chartControl = new FlowPanel();
        chartControl.add(createDoNotGroupBarChartButton());
        chartControl.add(createGroupBarChartByText2Button());
    }

    private RadioButton createDoNotGroupBarChartButton() {
        RadioButton button = new RadioButton("chartSettings",
                "do not group, show NUMBER_2");
        button.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                if (event.getValue()) {
                    doNotGroupBarChart();
                }
            }
        });
        button.setValue(true);
        return button;
    }

    private RadioButton createGroupBarChartByText2Button() {
        RadioButton button = new RadioButton("chartSettings", "group by TEXT_2");
        button.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                if (event.getValue()) {
                    groupBarChartByText2();
                }
            }
        });
        return button;
    }

    private ResourceSet createResourceSet() {
        return BenchmarkResourceSetFactory.createResourceSet(20,
                new DefaultResourceSetFactory());
    }

    private void doNotGroupBarChart() {
        // grouping
        barChart.setCategorizer(new ResourceByUriMultiCategorizer());
        // sorting by value
        barChart.getContentDisplay().setViewItemComparator(
                new ViewItemDoubleComparator(BarChart.BAR_LENGTH));
        // mappings
        barChart.setResolver(BarChart.BAR_LABEL,
                new FirstResourcePropertyResolver(
                        BenchmarkResourceSetFactory.TEXT_2, DataType.TEXT));
        barChart.setResolver(BarChart.BAR_LENGTH, new CalculationResolver(
                BenchmarkResourceSetFactory.NUMBER_2, new SumCalculation()) {
            @Override
            public String toString() {
                return "my axis label"; // example for axis labeling
            }
        });
    }

    protected void groupBarChartByText2() {
        // grouping
        barChart.setCategorizer(new ResourceByPropertyMultiCategorizer(
                BenchmarkResourceSetFactory.TEXT_2));
        // sorting by label
        barChart.getContentDisplay().setViewItemComparator(
                new ViewItemStringSlotComparator(BarChart.BAR_LABEL));
        // slot mappings
        barChart.setResolver(BarChart.BAR_LABEL,
                new FirstResourcePropertyResolver(
                        BenchmarkResourceSetFactory.TEXT_2, DataType.TEXT));
        barChart.setResolver(BarChart.BAR_LENGTH, new ResourceCountResolver());
    }

    private void handle(Throwable ex) {
        while (ex instanceof UmbrellaException) {
            ex = ex.getCause();
        }
        Logger.getLogger("").log(Level.SEVERE, ex.getMessage(), ex);
    }

    public void onModuleLoad() {
        try {
            ResourceSet resourceSet = createResourceSet();

            // init highlighting and selection models
            HighlightingModel hoverModel = new HighlightingModel();
            SelectionModel selectionModel = new DefaultSelectionModel(
                    new IncrementingSuffixLabelFactory(""),
                    new DefaultResourceSetFactory());

            createBarChart(resourceSet, hoverModel, selectionModel);
            createPieChart(resourceSet, hoverModel, selectionModel);
            createScatterPlot(resourceSet, hoverModel, selectionModel);
            createChartControl();

            RootPanel.get().add(chartControl);
            RootPanel.get().add(barChart);
            RootPanel.get().add(pieChart);
            RootPanel.get().add(scatterPlot);

            // Set the size of the window, and listen for
            // changes in size.
            Window.enableScrolling(false);
            layout();
            Window.addResizeHandler(new ResizeHandler() {
                @Override
                public void onResize(ResizeEvent event) {
                    layout();
                }
            });
        } catch (Throwable ex) {
            handle(ex);
        }
    }

    private void layout() {
        CSS.setHeight(chartControl, CONTROL_HEIGHT);
        CSS.setDisplay(pieChart.getElement(), CSS.INLINE);
        CSS.setDisplay(scatterPlot.getElement(), CSS.INLINE);

        barChart.setSize(Window.getClientWidth() + CSS.PX,
                (Window.getClientHeight() - CONTROL_HEIGHT) / 2 + CSS.PX);

        pieChart.setSize(Window.getClientWidth() / 2 + CSS.PX,
                (Window.getClientHeight() - CONTROL_HEIGHT) / 2 + CSS.PX);
        scatterPlot.setSize(Window.getClientWidth() / 2 + CSS.PX,
                (Window.getClientHeight() - CONTROL_HEIGHT) / 2 + CSS.PX);
    }
}
