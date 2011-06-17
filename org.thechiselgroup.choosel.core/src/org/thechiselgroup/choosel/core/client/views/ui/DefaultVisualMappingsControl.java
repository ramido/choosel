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
package org.thechiselgroup.choosel.core.client.views.ui;

import java.util.HashMap;
import java.util.Map;

import org.thechiselgroup.choosel.core.client.resources.DataType;
import org.thechiselgroup.choosel.core.client.resources.DataTypeToListMap;
import org.thechiselgroup.choosel.core.client.resources.HasResourceCategorizer;
import org.thechiselgroup.choosel.core.client.resources.ResourceByPropertyMultiCategorizer;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.resources.ResourceSetUtils;
import org.thechiselgroup.choosel.core.client.ui.ConfigurationPanel;
import org.thechiselgroup.choosel.core.client.ui.widget.listbox.ExtendedListBox;
import org.thechiselgroup.choosel.core.client.ui.widget.listbox.ListBoxControl;
import org.thechiselgroup.choosel.core.client.util.transform.NullTransformer;
import org.thechiselgroup.choosel.core.client.views.model.Slot;
import org.thechiselgroup.choosel.core.client.views.model.SlotMappingConfiguration;
import org.thechiselgroup.choosel.core.client.views.model.ViewContentDisplay;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Widget;

public class DefaultVisualMappingsControl implements VisualMappingsControl {

    private final HasResourceCategorizer resourceGrouping;

    private final SlotMappingConfiguration resolver;

    private ConfigurationPanel visualMappingPanel;

    private final ViewContentDisplay contentDisplay;

    private ListBoxControl<String> groupingBox;

    private DataTypeToListMap<SlotControl> slotControlsByDataType;

    private Map<Slot, SlotControl> slotToSlotControls = new HashMap<Slot, SlotControl>();

    public DefaultVisualMappingsControl(ViewContentDisplay contentDisplay,
            SlotMappingConfiguration resolver,
            HasResourceCategorizer resourceGrouping) {

        this.contentDisplay = contentDisplay;
        this.resolver = resolver;
        this.resourceGrouping = resourceGrouping;
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
        if (visualMappingPanel == null) {
            init();
        }

        return visualMappingPanel;
    }

    private void init() {
        visualMappingPanel = new ConfigurationPanel();

        initGroupingBox();
        initSlotControls();
    }

    private void initGroupingBox() {
        // TODO include aggregation that does not aggregate...
        // TODO include bin aggregation for numerical slots

        groupingBox = new ListBoxControl<String>(new ExtendedListBox(false),
                new NullTransformer<String>());

        groupingBox.setChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                String property = groupingBox.getSelectedValue();
                resourceGrouping
                        .setCategorizer(new ResourceByPropertyMultiCategorizer(
                                property));
            }
        });

        visualMappingPanel.addConfigurationSetting("Grouping",
                groupingBox.asWidget());
    }

    private void initSlotControls() {
        slotControlsByDataType = new DataTypeToListMap<SlotControl>();

        for (Slot slot : contentDisplay.getSlots()) {
            switch (slot.getDataType()) {
            case TEXT:
                addSlotControl(new TextSlotControl(slot, resolver));
                break;
            case NUMBER:
                addSlotControl(new NumberSlotControl(slot, resolver));
                break;
            }
        }
    }

    // TODO link to resource model instead & do updates when resources change
    @Override
    public void updateConfiguration(ResourceSet resources) {
        DataTypeToListMap<String> propertiesByDataType = ResourceSetUtils
                .getPropertiesByDataType(resources);

        // TODO update selection of slots?
        updateGroupingBox(propertiesByDataType);
        updateSlotControls(propertiesByDataType);
    }

    private void updateGroupingBox(
            DataTypeToListMap<String> propertiesByDataType) {

        groupingBox.setValues(propertiesByDataType.get(DataType.TEXT));
        if (groupingBox.getSelectedValue() == null
                && resourceGrouping.getCategorizer() instanceof ResourceByPropertyMultiCategorizer) {
            String property = ((ResourceByPropertyMultiCategorizer) resourceGrouping
                    .getCategorizer()).getProperty();
            groupingBox.setSelectedValue(property);
        }
    }

    private void updateSlotControls(
            DataTypeToListMap<String> propertiesByDataType) {

        for (DataType dataType : DataType.values()) {
            for (SlotControl slotControl : slotControlsByDataType.get(dataType)) {
                slotControl.updateOptions(propertiesByDataType.get(dataType));
            }
        }
    }

}