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
import java.util.Map.Entry;

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
import org.thechiselgroup.choosel.core.client.visualization.model.managed.ManagedSlotMappingConfigurationChangedEvent;
import org.thechiselgroup.choosel.core.client.visualization.model.managed.ManagedSlotMappingState;
import org.thechiselgroup.choosel.core.client.visualization.model.managed.ManagedVisualItemValueResolver;
import org.thechiselgroup.choosel.core.client.visualization.model.managed.VisualItemValueResolverFactoryProvider;
import org.thechiselgroup.choosel.core.client.visualization.resolvers.ui.VisualItemValueResolverUIController;
import org.thechiselgroup.choosel.core.client.visualization.resolvers.ui.VisualItemValueResolverUIControllerFactory;
import org.thechiselgroup.choosel.core.client.visualization.resolvers.ui.VisualItemValueResolverUIControllerFactoryProvider;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Widget;

public class DefaultVisualMappingsControl implements VisualMappingsControl {

    protected final HasResourceCategorizer resourceGrouping;

    protected final ManagedSlotMappingConfiguration slotMappingConfigurationUIModel;

    protected final VisualItemValueResolverFactoryProvider resolverFactoryProvider;

    private ConfigurationPanel visualMappingPanel;

    protected ListBoxControl<String> groupingBox;

    private DataTypeToListMap<SlotControl> slotControlsByDataType;

    private Map<Slot, SlotControl> slotToSlotControls = new HashMap<Slot, SlotControl>();

    private final VisualItemValueResolverUIControllerFactoryProvider uiProvider;

    public static final String GROUP_BY_URI_LABEL = "No Grouping";

    public DefaultVisualMappingsControl(
            ManagedSlotMappingConfiguration slotMappingConfigurationUIModel,
            HasResourceCategorizer resourceGrouping,
            VisualItemValueResolverUIControllerFactoryProvider uiProvider,
            VisualItemValueResolverFactoryProvider resolverFactoryProvider) {

        assert slotMappingConfigurationUIModel != null;
        assert resourceGrouping != null;
        assert uiProvider != null;

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

    private SlotControl createSlotControl(Slot slot,
            ManagedVisualItemValueResolver resolver) {
        SlotControl slotControl;
        VisualItemValueResolverUIController newResolverUI = createUIControllerFromResolver(
                slot, resolver);
        slotControl = initSlotControl(slot, newResolverUI);
        return slotControl;
    }

    // TODO this should take allowableFactories as a parameter
    private VisualItemValueResolverUIController createUIControllerFromResolver(
            Slot slot, ManagedVisualItemValueResolver currentResolver) {
        VisualItemValueResolverUIControllerFactory uiFactory = uiProvider
                .getFactoryById(currentResolver.getResolverId());

        assert uiFactory != null;

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

    protected void initGroupingBox() {
        // TODO include aggregation that does not aggregate...
        // TODO include bin aggregation for numerical slots

        groupingBox = new ListBoxControl<String>(new ExtendedListBox(false),
                new NullTransformer<String>());

        /**
         * This is an event handle which watches the resource grouping box for
         * grouping changes
         */
        groupingBox.setChangeHandler(new ChangeHandler() {
            // XXX This is a bad Hack.
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
            VisualItemValueResolverUIController resolverUI) {

        DefaultSlotControl slotControl = new DefaultSlotControl(slot,
                slotMappingConfigurationUIModel, resolverUI);

        this.slotToSlotControls.put(slot, slotControl);
        addSlotControl(slot, slotControl);

        return slotControl;

    }

    private void initSlotControls() {
        slotControlsByDataType = new DataTypeToListMap<SlotControl>();
    }

    @Override
    public void updateConfigurationForSlotMappingChangedEvent(
            ManagedSlotMappingConfigurationChangedEvent e) {

        for (Entry<Slot, ManagedSlotMappingState> entry : e
                .getSlotConfigurationStates().entrySet()) {
            updateSlotUI(entry.getKey(), entry.getValue(), e.getVisualItems());
        }
    }

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

    private void updateGroupingBox(LightweightCollection<VisualItem> visualItems) {
        ResourceSet resources = new DefaultResourceSet();
        for (VisualItem viewItem : visualItems) {
            resources.addAll(viewItem.getResources());
        }

        DataTypeToListMap<String> propertiesByDataType = ResourceSetUtils
                .getPropertiesByDataType(resources);

        updateGroupingBox(propertiesByDataType);
    }

    private void updateSlotControls(LightweightCollection<VisualItem> viewItems) {
        for (DataType dataType : DataType.values()) {
            for (SlotControl slotControl : slotControlsByDataType.get(dataType)) {
                slotControl.updateOptions(viewItems);
            }
        }
    }

    private void updateSlotUI(Slot slot, ManagedSlotMappingState state,
            LightweightCollection<VisualItem> visualItems) {

        // TODO in the future, errors in these two things would likely be
        // handled, instead of exceptions being thrown
        assert state.isAllowable();
        assert state.isConfigured();

        updateGroupingBox(visualItems);

        ManagedVisualItemValueResolver resolver = state.getResolver();

        SlotControl slotControl = slotToSlotControls.get(slot);
        if (slotControl == null) {
            // The slot Control has not yet been initialized, initialize it and
            // set the resolverUI
            slotControl = createSlotControl(slot, resolver);
        } else if (!resolver.getResolverId().equals(
                slotControl.getCurrentResolverUIId())) {
            // only the factory has changed, and we need to update the
            // resolverUI
            slotControl.setNewUIModel(createUIControllerFromResolver(slot,
                    resolver));
        }

        // update the slotMapping with the new options
        slotControl.updateOptions(visualItems);
    }

}