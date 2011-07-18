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
package org.thechiselgroup.choosel.core.client.visualization.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.thechiselgroup.choosel.core.client.resources.DataTypeToListMap;
import org.thechiselgroup.choosel.core.client.resources.DefaultResourceSet;
import org.thechiselgroup.choosel.core.client.resources.HasResourceCategorizer;
import org.thechiselgroup.choosel.core.client.resources.ResourceByPropertyMultiCategorizer;
import org.thechiselgroup.choosel.core.client.resources.ResourceByUriMultiCategorizer;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.resources.ResourceSetUtils;
import org.thechiselgroup.choosel.core.client.ui.ConfigurationPanel;
import org.thechiselgroup.choosel.core.client.ui.widget.listbox.ExtendedListBox;
import org.thechiselgroup.choosel.core.client.ui.widget.listbox.ListBoxControl;
import org.thechiselgroup.choosel.core.client.util.DataType;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.util.transform.NullTransformer;
import org.thechiselgroup.choosel.core.client.visualization.model.Slot;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItem;
import org.thechiselgroup.choosel.core.client.visualization.model.managed.ManagedSlotMappingConfiguration;
import org.thechiselgroup.choosel.core.client.visualization.model.managed.ManagedVisualItemValueResolver;
import org.thechiselgroup.choosel.core.client.visualization.model.managed.VisualItemValueResolverFactoryProvider;
import org.thechiselgroup.choosel.core.client.visualization.resolvers.ui.VisualItemValueResolverUIController;
import org.thechiselgroup.choosel.core.client.visualization.resolvers.ui.VisualItemValueResolverUIControllerFactory;
import org.thechiselgroup.choosel.core.client.visualization.resolvers.ui.VisualItemValueResolverUIControllerFactoryProvider;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Widget;

public class DefaultVisualMappingsControl implements VisualMappingsControl {

    private final HasResourceCategorizer resourceGrouping;

    private final ManagedSlotMappingConfiguration slotMappingConfigurationUIModel;

    private final VisualItemValueResolverFactoryProvider resolverFactoryProvider;

    private ConfigurationPanel visualMappingPanel;

    private ListBoxControl<String> groupingBox;

    private DataTypeToListMap<SlotControl> slotControlsByDataType;

    private Map<Slot, SlotControl> slotToSlotControls = new HashMap<Slot, SlotControl>();

    private final VisualItemValueResolverUIControllerFactoryProvider uiProvider;

    private static final String GROUP_BY_URI_LABEL = "No Grouping";

    public DefaultVisualMappingsControl(
            ManagedSlotMappingConfiguration slotMappingConfigurationUIModel,
            HasResourceCategorizer resourceGrouping,
            VisualItemValueResolverUIControllerFactoryProvider uiProvider,
            VisualItemValueResolverFactoryProvider resolverFactoryProvider) {

        assert slotMappingConfigurationUIModel != null;
        assert resourceGrouping != null;
        assert uiProvider != null;
        assert resolverFactoryProvider != null;

        this.slotMappingConfigurationUIModel = slotMappingConfigurationUIModel;

        this.resourceGrouping = resourceGrouping;
        this.uiProvider = uiProvider;
        this.resolverFactoryProvider = resolverFactoryProvider;
    }

    private void addSlotControl(Slot slot, SlotControl slotControl) {
        assert slotControl != null;
        assert slot != null;

        visualMappingPanel.setConfigurationSetting(slot.getName(),
                slotControl.asWidget());

        slotControlsByDataType.get(slot.getDataType()).add(slotControl);
        slotToSlotControls.put(slot, slotControl);
    }

    // TODO must we make this a widget before we add things to it or what?
    @Override
    public Widget asWidget() {
        if (visualMappingPanel == null) {
            init();
        }

        return visualMappingPanel;
    }

    private List<String> calculateGroupingBoxOptions(
            DataTypeToListMap<String> propertiesByDataType) {
        List<String> values = propertiesByDataType.get(DataType.TEXT);
        values.add(GROUP_BY_URI_LABEL);
        return values;
    }

    private VisualItemValueResolverUIController createUIControllerFromResolver(
            Slot slot, ManagedVisualItemValueResolver currentResolver) {
        VisualItemValueResolverUIControllerFactory uiFactory = uiProvider
                .getFactoryById(currentResolver.getResolverId());

        assert uiFactory != null;

        // TODO maybe refactor this, probably some null checks would be great
        VisualItemValueResolverUIController resolverUI = uiFactory.create(
                resolverFactoryProvider.getFactoryById(currentResolver
                        .getResolverId()), slotMappingConfigurationUIModel
                        .getManagedSlotMapping(slot),
                slotMappingConfigurationUIModel.getVisualItems());
        return resolverUI;
    }

    // TODO uh, shouldnt we just initialize the visualMappingSPanel in the
    // constructor, as well as the other two things.
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

                if (GROUP_BY_URI_LABEL.equals(property)) {
                    resourceGrouping
                            .setCategorizer(new ResourceByUriMultiCategorizer());
                } else {
                    resourceGrouping
                            .setCategorizer(new ResourceByPropertyMultiCategorizer(
                                    property));
                }
            }
        });

        visualMappingPanel.setConfigurationSetting("Grouping",
                groupingBox.asWidget());
    }

    private SlotControl initSlotControl(Slot slot,
            VisualItemValueResolverUIController resolverUI,
            SlotControl currentSlotControl) {

        DefaultSlotControl slotControl = new DefaultSlotControl(slot,
                slotMappingConfigurationUIModel, resolverUI);

        this.slotToSlotControls.put(slot, slotControl);
        addSlotControl(slot, slotControl);

        return slotControl;

    }

    // TODO we may want to create add a control for each slot, with an empty
    // widget
    private void initSlotControls() {
        slotControlsByDataType = new DataTypeToListMap<SlotControl>();
    }

    @Override
    public void updateConfigurationForChangedSlotMapping(Slot slot,
            ManagedVisualItemValueResolver oldResolver,
            ManagedVisualItemValueResolver currentResolver) {

        VisualItemValueResolverUIController resolverUI = createUIControllerFromResolver(
                slot, currentResolver);

        SlotControl currentSlotControl = this.slotToSlotControls.get(slot);
        if (currentSlotControl == null) {
            currentSlotControl = initSlotControl(slot, resolverUI,
                    currentSlotControl);
        }

        /*
         * if the factory has changed, then the uiController must be changed and
         * updated
         */
        if (oldResolver == null
                || !oldResolver.getResolverId().equals(
                        currentResolver.getResolverId())) {
            // if yes, reset the uiController

            // update the resolveUI to ensure that it's in a valid state
            resolverUI.update(slotMappingConfigurationUIModel.getVisualItems());
            currentSlotControl.setNewUIModel(resolverUI);

            // this is firing the second event
            currentSlotControl.updateOptions(slotMappingConfigurationUIModel
                    .getVisualItems());
        }
    }

    @Override
    public void updateConfigurationForChangedViewItems(
            LightweightCollection<VisualItem> viewItems) {

        ResourceSet resources = new DefaultResourceSet();
        for (VisualItem viewItem : viewItems) {
            resources.addAll(viewItem.getResources());
        }

        DataTypeToListMap<String> propertiesByDataType = ResourceSetUtils
                .getPropertiesByDataType(resources);

        // TODO remove the property selection stuff, and make grouping done the
        // same way as slots
        updateGroupingBox(propertiesByDataType);
        updateSlotControls(viewItems);
    }

    // TODO with tag cloud visualization, the property should be set to
    // tagContent automatically
    private void updateGroupingBox(
            DataTypeToListMap<String> propertiesByDataType) {
        groupingBox
                .setValues(calculateGroupingBoxOptions(propertiesByDataType));
        if (groupingBox.getSelectedValue() == null
                && resourceGrouping.getCategorizer() instanceof ResourceByPropertyMultiCategorizer) {

            String property = ((ResourceByPropertyMultiCategorizer) resourceGrouping
                    .getCategorizer()).getProperty();
            groupingBox.setSelectedValue(property);
        } else if (groupingBox.getSelectedValue() == null
                && resourceGrouping.getCategorizer() instanceof ResourceByUriMultiCategorizer) {
            groupingBox.setSelectedValue(GROUP_BY_URI_LABEL);
        }
    }

    private void updateSlotControls(LightweightCollection<VisualItem> viewItems) {
        for (DataType dataType : DataType.values()) {
            for (SlotControl slotControl : slotControlsByDataType.get(dataType)) {
                slotControl.updateOptions(viewItems);
            }
        }
    }

}