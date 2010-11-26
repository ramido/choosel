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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.thechiselgroup.choosel.client.calculation.SumCalculation;
import org.thechiselgroup.choosel.client.resolver.ResourceSetToValueResolver;
import org.thechiselgroup.choosel.client.resources.ResourceByPropertyMultiCategorizer;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ResourceSetUtils;
import org.thechiselgroup.choosel.client.resources.ResourceSplitter;
import org.thechiselgroup.choosel.client.ui.ConfigurationPanel;
import org.thechiselgroup.choosel.client.ui.WidgetAdaptable;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.ListBox;
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

    private DataTypeToListMap<SlotControl> slotControlsByDataType;

    private Map<Slot, SlotControl> slotToSlotControls = new HashMap<Slot, SlotControl>();

    public VisualMappingsControl(ViewContentDisplay contentDisplay,
            ResourceItemValueResolver resolver, ResourceSplitter splitter) {

        this.contentDisplay = contentDisplay;
        this.resolver = resolver;
        this.splitter = splitter;
    }

    private void addSlotControl(SlotControl control) {
        assert control != null;

        Slot slot = control.getSlot();

        visualMappingPanel.addConfigurationSetting(slot.getName(),
                control.asWidget());

        slotControlsByDataType.get(slot.getDataType()).add(control);
        slotToSlotControls.put(slot, control);
    }

    @Override
    public Widget asWidget() {
        return visualMappingPanel;
    }

    public void init() {
        visualMappingPanel = new ConfigurationPanel();

        initGroupingBox();
        initSlotControls();
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

    private void initSlotControls() {
        slotControlsByDataType = new DataTypeToListMap<SlotControl>();

        // TODO refactor
        for (final Slot slot : contentDisplay.getSlots()) {
            if (slot.getDataType() == DataType.TEXT) {
                addSlotControl(new TextSlotControl(slot, resolver,
                        contentDisplay));
            } else if (slot.getDataType() == DataType.NUMBER) {
                addSlotControl(new NumberSlotControl(slot, resolver,
                        contentDisplay));
            }
        }
    }

    private void setInitialMappings(
            DataTypeToListMap<String> propertiesByDataType) {
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
            } else if (slot.getDataType() == DataType.NUMBER) {
                List<String> propertyNames = propertiesByDataType
                        .get(DataType.NUMBER);

                if (!propertyNames.isEmpty()) {
                    resolver.put(slot,
                            new CalculationResourceSetToValueResolver(
                                    propertyNames.get(0), new SumCalculation()));
                }
            }
        }
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

        DataTypeToListMap<String> propertiesByDataType = ResourceSetUtils
                .getPropertiesByDataType(resources);

        setInitialMappings(propertiesByDataType);

        updateGroupingBox(propertiesByDataType);
        updateSlotControls(propertiesByDataType);
    }

    private void updateGroupingBox(
            DataTypeToListMap<String> propertiesByDataType) {

        for (String property : propertiesByDataType.get(DataType.TEXT)) {
            groupingBox.addItem(property, property);
        }
    }

    protected void updateSlotControls(
            DataTypeToListMap<String> propertiesByDataType) {

        for (DataType dataType : DataType.values()) {
            for (SlotControl slotControl : slotControlsByDataType.get(dataType)) {
                slotControl.updateOptions(propertiesByDataType.get(dataType));
            }
        }
    }

}