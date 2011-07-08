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
import org.thechiselgroup.choosel.core.client.resources.DefaultResourceSet;
import org.thechiselgroup.choosel.core.client.resources.HasResourceCategorizer;
import org.thechiselgroup.choosel.core.client.resources.ResourceByPropertyMultiCategorizer;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.resources.ResourceSetUtils;
import org.thechiselgroup.choosel.core.client.ui.ConfigurationPanel;
import org.thechiselgroup.choosel.core.client.ui.widget.listbox.ExtendedListBox;
import org.thechiselgroup.choosel.core.client.ui.widget.listbox.ListBoxControl;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.util.transform.NullTransformer;
import org.thechiselgroup.choosel.core.client.views.model.Slot;
import org.thechiselgroup.choosel.core.client.views.model.SlotMappingConfigurationUIModel;
import org.thechiselgroup.choosel.core.client.views.model.ViewItem;
import org.thechiselgroup.choosel.core.client.views.resolvers.ManagedViewItemValueResolver;
import org.thechiselgroup.choosel.core.client.views.resolvers.ViewItemValueResolverFactoryProvider;
import org.thechiselgroup.choosel.core.client.views.resolvers.ViewItemValueResolverUIController;
import org.thechiselgroup.choosel.core.client.views.resolvers.ViewItemValueResolverUIControllerFactory;
import org.thechiselgroup.choosel.core.client.views.resolvers.ViewItemValueResolverUIControllerFactoryProvider;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Widget;

public class DefaultVisualMappingsControl implements VisualMappingsControl {

    private final HasResourceCategorizer resourceGrouping;

    private final SlotMappingConfigurationUIModel slotMappingConfigurationUIModel;

    private final ViewItemValueResolverFactoryProvider resolverFactoryProvider;

    private ConfigurationPanel visualMappingPanel;

    private ListBoxControl<String> groupingBox;

    private DataTypeToListMap<SlotControl> slotControlsByDataType;

    private Map<Slot, SlotControl> slotToSlotControls = new HashMap<Slot, SlotControl>();

    private final ViewItemValueResolverUIControllerFactoryProvider uiProvider;

    public DefaultVisualMappingsControl(
            SlotMappingConfigurationUIModel slotMappingConfigurationUIModel,
            HasResourceCategorizer resourceGrouping,
            ViewItemValueResolverUIControllerFactoryProvider uiProvider,
            ViewItemValueResolverFactoryProvider resolverFactoryProvider) {

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

    private ViewItemValueResolverUIController createUIControllerFromResolver(
            Slot slot, ManagedViewItemValueResolver currentResolver) {
        ViewItemValueResolverUIControllerFactory uiFactory = uiProvider
                .getFactoryById(currentResolver.getResolverId());

        assert uiFactory != null;

        // TODO maybe refactor this, probably some null checks would be great
        ViewItemValueResolverUIController resolverUI = uiFactory.create(
                resolverFactoryProvider.getFactoryById(currentResolver
                        .getResolverId()), slotMappingConfigurationUIModel
                        .getSlotMappingUIModel(slot),
                slotMappingConfigurationUIModel.getViewItems());
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
                resourceGrouping
                        .setCategorizer(new ResourceByPropertyMultiCategorizer(
                                property));
            }
        });

        visualMappingPanel.setConfigurationSetting("Grouping",
                groupingBox.asWidget());
    }

    private SlotControl initSlotControl(Slot slot,
            ViewItemValueResolverUIController resolverUI,
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
            ManagedViewItemValueResolver oldResolver,
            ManagedViewItemValueResolver currentResolver) {

        ViewItemValueResolverUIController resolverUI = createUIControllerFromResolver(
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
            resolverUI.update(slotMappingConfigurationUIModel.getViewItems());
            currentSlotControl.setNewUIModel(resolverUI);

            // this is firing the second event
            currentSlotControl.updateOptions(slotMappingConfigurationUIModel
                    .getViewItems());
        }
    }

    @Override
    public void updateConfigurationForChangedViewItems(
            LightweightCollection<ViewItem> viewItems) {

        ResourceSet resources = new DefaultResourceSet();
        for (ViewItem viewItem : viewItems) {
            resources.addAll(viewItem.getResources());
        }

        DataTypeToListMap<String> propertiesByDataType = ResourceSetUtils
                .getPropertiesByDataType(resources);

        // TODO remove the property selection stuff, and make grouping done the
        // same way as slots
        updateGroupingBox(propertiesByDataType);
        updateSlotControls(viewItems);
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

    private void updateSlotControls(LightweightCollection<ViewItem> viewItems) {
        for (DataType dataType : DataType.values()) {
            for (SlotControl slotControl : slotControlsByDataType.get(dataType)) {
                slotControl.updateOptions(viewItems);
            }
        }
    }

}