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
package org.thechiselgroup.choosel.client.views;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.thechiselgroup.choosel.client.calculation.AverageCalculation;
import org.thechiselgroup.choosel.client.calculation.Calculation;
import org.thechiselgroup.choosel.client.calculation.CountCalculation;
import org.thechiselgroup.choosel.client.calculation.MaxCalculation;
import org.thechiselgroup.choosel.client.calculation.MinCalculation;
import org.thechiselgroup.choosel.client.calculation.SumCalculation;
import org.thechiselgroup.choosel.client.resolver.ResourceSetToValueResolver;
import org.thechiselgroup.choosel.client.resources.ResourceByPropertyMultiCategorizer;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ResourceSetUtils;
import org.thechiselgroup.choosel.client.resources.ResourceSplitter;
import org.thechiselgroup.choosel.client.ui.ConfigurationPanel;
import org.thechiselgroup.choosel.client.ui.WidgetAdaptable;
import org.thechiselgroup.choosel.client.util.CollectionUtils;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class VisualMappingsControl implements WidgetAdaptable {

    /*
     * Boolean flag that indicates if the configuration part of the view has
     * been created.
     * 
     * XXX This solution breaks down when there is more than one kind of
     * resource (i.e. with different properties)
     */
    private boolean isConfigurationAvailable = false;

    private final ResourceSplitter splitter;

    private final ResourceItemValueResolver resolver;

    private ConfigurationPanel visualMappingPanel;

    private final ViewContentDisplay contentDisplay;

    private ListBox groupingBox;

    public VisualMappingsControl(ViewContentDisplay contentDisplay,
            ResourceItemValueResolver resolver, ResourceSplitter splitter) {

        this.contentDisplay = contentDisplay;
        this.resolver = resolver;
        this.splitter = splitter;
    }

    @Override
    public Widget asWidget() {
        return visualMappingPanel;
    }

    public void init() {
        visualMappingPanel = new ConfigurationPanel();

        initGroupingBox();
    }

    private void initGroupingBox() {
        // TODO include aggregation that does not aggregate...
        // TODO include bin aggregation for numerical slots

        groupingBox = new ListBox(false);
        groupingBox.setVisibleItemCount(1);

        groupingBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                String property = groupingBox.getValue(groupingBox
                        .getSelectedIndex());

                splitter.setCategorizer(new ResourceByPropertyMultiCategorizer(
                        property));
            }
        });

        visualMappingPanel.addConfigurationSetting("Grouping", groupingBox);
    }

    // TODO link to resource model instead & do updates when resources change
    public void updateConfiguration(ResourceSet resources) {
        /*
         * TODO check if there are changes when adding / adjust each slot -->
         * stable per slot --> initialize early for the slots & map to object
         * that has corresponding update method
         * 
         * XXX for now: just add a flag if a configuration has been created, and
         * if that's the case, don't rebuild the configuration.
         * 
         * XXX this also fails with redo / undo
         */
        if (isConfigurationAvailable) {
            return;
        }
        isConfigurationAvailable = true;

        // TODO do this separately for aggregation & slots (which should be
        // based on resource items)
        // TODO update selection of slots?

        // TODO map [ data type --> list<property> ]
        DataTypeToListMap<String> propertiesByDataType = ResourceSetUtils
                .getPropertiesByDataType(resources);

        for (String property : propertiesByDataType.get(DataType.TEXT)) {
            groupingBox.addItem(property, property);
        }

        /*
         * TODO move
         */
        if (Arrays.asList(contentDisplay.getSlots()).contains(
                SlotResolver.LOCATION_SLOT)) {

            final List<String> propertyNames = propertiesByDataType
                    .get(DataType.LOCATION);

            if (!propertyNames.isEmpty()) {
                resolver.put(SlotResolver.LOCATION_SLOT,
                        new ResourceSetToValueResolver() {
                            @Override
                            public Object resolve(ResourceSet resources,
                                    String category) {

                                return resources.getFirstResource().getValue(
                                        propertyNames.get(0));
                            }
                        });
            }
        }

        /*
         * TODO flexibility TODO move
         */
        if (Arrays.asList(contentDisplay.getSlots()).contains(
                SlotResolver.COLOR_SLOT)) {
            resolver.put(SlotResolver.COLOR_SLOT,
                    new ResourceSetToValueResolver() {
                        @Override
                        public Object resolve(ResourceSet resources,
                                String category) {

                            return "#6495ed";
                        }
                    });
        }

        /*
         * TODO move
         */
        if (Arrays.asList(contentDisplay.getSlots()).contains(
                SlotResolver.DATE_SLOT)) {

            final List<String> propertyNames = propertiesByDataType
                    .get(DataType.DATE);

            if (!propertyNames.isEmpty()) {
                resolver.put(SlotResolver.DATE_SLOT,
                        new ResourceSetToValueResolver() {
                            @Override
                            public Object resolve(ResourceSet resources,
                                    String category) {

                                return resources.getFirstResource().getValue(
                                        propertyNames.get(0));
                            }
                        });
            }
        }

        for (final Slot slot : contentDisplay.getSlots()) {
            if (slot.getDataType() == DataType.TEXT) {
                List<String> propertyNames = propertiesByDataType
                        .get(DataType.TEXT);

                if (!propertyNames.isEmpty()) {
                    resolver.put(slot, new TextResourceSetToValueResolver(
                            propertyNames.get(0)));
                }

                SlotControl control = new TextSlotControl(slot, resolver,
                        contentDisplay);
                control.init();
                visualMappingPanel.addConfigurationSetting(slot.getName(),
                        control.asWidget());

                control.updateOptions(propertyNames);
            } else if (slot.getDataType() == DataType.NUMBER) {
                List<String> propertyNames = propertiesByDataType
                        .get(DataType.NUMBER);

                if (!propertyNames.isEmpty()) {
                    resolver.put(slot,
                            new CalculationResourceSetToValueResolver(
                                    propertyNames.get(0), new SumCalculation()));
                }

                Calculation[] calculations = new Calculation[] {
                        new SumCalculation(), new CountCalculation(),
                        new AverageCalculation(), new MinCalculation(),
                        new MaxCalculation() };

                final Map<String, Calculation> calculationMap = new HashMap<String, Calculation>();
                for (Calculation calculation : calculations) {
                    calculationMap.put(calculation.getID(), calculation);
                }

                final ListBox calculationBox = new ListBox(false);
                calculationBox.setVisibleItemCount(1);
                for (Calculation calculation : calculations) {
                    calculationBox.addItem(calculation.getDescription(),
                            calculation.getID());
                }

                final ListBox slotPropertyMappingBox = new ListBox(false);
                slotPropertyMappingBox.setVisibleItemCount(1);

                ChangeHandler handler = new ChangeHandler() {
                    @Override
                    public void onChange(ChangeEvent event) {
                        String propertyName = slotPropertyMappingBox
                                .getValue(slotPropertyMappingBox
                                        .getSelectedIndex());

                        String calculationString = calculationBox
                                .getValue(calculationBox.getSelectedIndex());
                        Calculation calculation = calculationMap
                                .get(calculationString);

                        resolver.put(slot,
                                new CalculationResourceSetToValueResolver(
                                        propertyName, calculation));

                        contentDisplay.update(
                                Collections.<ResourceItem> emptySet(),
                                Collections.<ResourceItem> emptySet(),
                                Collections.<ResourceItem> emptySet(),
                                CollectionUtils.toSet(slot));
                    }
                };

                slotPropertyMappingBox.addChangeHandler(handler);
                calculationBox.addChangeHandler(handler);

                for (String propertyName : propertyNames) {
                    slotPropertyMappingBox.addItem(propertyName, propertyName);
                }

                VerticalPanel panel = new VerticalPanel();
                panel.add(calculationBox);
                panel.add(slotPropertyMappingBox);
                visualMappingPanel.addConfigurationSetting(slot.getName(),
                        panel);
            }
        }

    }

}