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
import org.thechiselgroup.choosel.core.client.resources.DefaultResourceSetFactory;
import org.thechiselgroup.choosel.core.client.resources.ResourceByPropertyMultiCategorizer;
import org.thechiselgroup.choosel.core.client.resources.ResourceByUriMultiCategorizer;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.resources.ui.DetailsWidgetHelper;
import org.thechiselgroup.choosel.core.client.test.BenchmarkResourceSetFactory;
import org.thechiselgroup.choosel.core.client.ui.CSS;
import org.thechiselgroup.choosel.core.client.ui.Color;
import org.thechiselgroup.choosel.core.client.ui.popup.DefaultPopupFactory;
import org.thechiselgroup.choosel.core.client.ui.popup.DefaultPopupManagerFactory;
import org.thechiselgroup.choosel.core.client.views.VisualizationWidget;
import org.thechiselgroup.choosel.core.client.views.behaviors.CompositeViewItemBehavior;
import org.thechiselgroup.choosel.core.client.views.behaviors.HighlightingViewItemBehavior;
import org.thechiselgroup.choosel.core.client.views.behaviors.PopupWithHighlightingViewItemBehavior;
import org.thechiselgroup.choosel.core.client.views.behaviors.SwitchSelectionOnClickViewItemBehavior;
import org.thechiselgroup.choosel.core.client.views.model.DefaultSelectionModel;
import org.thechiselgroup.choosel.core.client.views.model.HighlightingModel;
import org.thechiselgroup.choosel.core.client.views.model.SelectionModel;
import org.thechiselgroup.choosel.core.client.views.model.ViewItem;
import org.thechiselgroup.choosel.core.client.views.model.ViewItem.Subset;
import org.thechiselgroup.choosel.core.client.views.resolvers.FirstResourcePropertyResolver;
import org.thechiselgroup.choosel.core.client.views.resolvers.FixedValueResolver;
import org.thechiselgroup.choosel.core.client.views.resolvers.ResourceCountResolver;
import org.thechiselgroup.choosel.core.client.views.resolvers.ViewItemStatusResolver;
import org.thechiselgroup.choosel.core.client.views.resolvers.ViewItemStatusResolver.StatusRule;
import org.thechiselgroup.choosel.core.client.views.sorting.ViewItemDoubleComparator;
import org.thechiselgroup.choosel.core.client.views.sorting.ViewItemStringSlotComparator;
import org.thechiselgroup.choosel.visualization_component.chart.client.barchart.BarChart;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.UmbrellaException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class ComponentExampleEntryPoint implements EntryPoint {

    private static final Color COLOR_DEFAULT = new Color(18, 64, 171, 0.6);

    private static final Color COLOR_DEFAULT_BORDER = new Color(6, 38, 111);

    private static final Color COLOR_SELECTION = new Color(255, 19, 0, 0.8);

    private static final Color COLOR_SELECTION_BORDER = new Color(166, 12, 0);

    private static final Color COLOR_HIGHLIGHTED = new Color(255, 250, 0, 0.9);

    private static final Color COLOR_HIGHLIGHTED_BORDER = new Color(166, 163, 0);

    private static final ViewItemStatusResolver COLOR_RESOLVER = new ViewItemStatusResolver(
            COLOR_DEFAULT, StatusRule.fullOrPartial(COLOR_HIGHLIGHTED,
                    Subset.HIGHLIGHTED), StatusRule.fullOrPartial(
                    COLOR_SELECTION, Subset.SELECTED));

    // height of control element in px, also in index.html
    private static final int CONTROL_HEIGHT = 25;

    private VisualizationWidget<BarChart> barChart;

    private void createBarChart(ResourceSet resourceSet, HighlightingModel hoverModel,
            SelectionModel selectionModel) {

        // behaviors: how the view reacts to user interactions
        CompositeViewItemBehavior barChartBehaviors = new CompositeViewItemBehavior();
        barChartBehaviors.add(new HighlightingViewItemBehavior(hoverModel));
        barChartBehaviors.add(new SwitchSelectionOnClickViewItemBehavior(
                selectionModel));
        barChartBehaviors.add(new PopupWithHighlightingViewItemBehavior(
                new DetailsWidgetHelper() {
                    public Widget createDetailsWidget(ViewItem viewItem) {
                        return new HTML(
                                "<b style='white-space: nowrap;'>"
                                        + viewItem.getViewItemID()
                                        + "</b><br/><span style='white-space: nowrap;'>"
                                        + viewItem.getResources().size()
                                        + " items<span>");
                    }
                }, new DefaultPopupManagerFactory(new DefaultPopupFactory()), hoverModel));

        // create visualization
        barChart = new VisualizationWidget<BarChart>(new BarChart(),
                selectionModel.getSelectionProxy(), hoverModel.getResources(),
                barChartBehaviors);

        // configure visual mappings
        barChart.setResolver(
                BarChart.BAR_COLOR,
                new ViewItemStatusResolver(COLOR_DEFAULT, StatusRule
                        .fullOrPartial(COLOR_HIGHLIGHTED, Subset.HIGHLIGHTED),
                        StatusRule.full(COLOR_SELECTION, Subset.SELECTED)));
        barChart.setResolver(
                BarChart.BAR_BORDER_COLOR,
                new ViewItemStatusResolver(COLOR_DEFAULT_BORDER, StatusRule
                        .full(COLOR_SELECTION_BORDER, Subset.SELECTED),
                        StatusRule.fullOrPartial(COLOR_HIGHLIGHTED_BORDER,
                                Subset.HIGHLIGHTED)));
        barChart.setResolver(BarChart.PARTIAL_BAR_LENGTH,
                new ResourceCountResolver(Subset.SELECTED));
        barChart.setResolver(BarChart.PARTIAL_BAR_COLOR, COLOR_RESOLVER);
        barChart.setResolver(BarChart.PARTIAL_BAR_BORDER_COLOR,
                new FixedValueResolver(COLOR_SELECTION_BORDER));

        // default settings
        doNotGroupBarChart();

        // configure properties
        // barChart.setPropertyValue(BarChart.LAYOUT_PROPERTY,
        // LayoutType.VERTICAL);

        // set resources
        barChart.setContentResourceSet(resourceSet);
    }

    private FlowPanel createChartControl() {
        FlowPanel panel = new FlowPanel();
        panel.add(createDoNotGroupBarChartButton());
        panel.add(createGroupBarChartByText2Button());
        return panel;
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
                        BenchmarkResourceSetFactory.TEXT_2));
        barChart.setResolver(BarChart.BAR_LENGTH,
                new FirstResourcePropertyResolver(
                        BenchmarkResourceSetFactory.NUMBER_2) {
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
                        BenchmarkResourceSetFactory.TEXT_2));
        barChart.setResolver(BarChart.BAR_LENGTH, new ResourceCountResolver());
    }

    private void handle(Throwable ex) {
        // TODO use error handler
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

            RootPanel.get("control").add(createChartControl());
            RootPanel.get("chart").add(barChart);

            // Set the size of the window, and listen for
            // changes in size.
            Window.enableScrolling(false);
            barChart.setSize(Window.getClientWidth() + CSS.PX,
                    Window.getClientHeight() - CONTROL_HEIGHT + CSS.PX);
            Window.addResizeHandler(new ResizeHandler() {
                @Override
                public void onResize(ResizeEvent event) {
                    barChart.setSize(event.getWidth() + CSS.PX,
                            event.getHeight() - CONTROL_HEIGHT + CSS.PX);
                }
            });
        } catch (Throwable ex) {
            handle(ex);
        }
    }
}
