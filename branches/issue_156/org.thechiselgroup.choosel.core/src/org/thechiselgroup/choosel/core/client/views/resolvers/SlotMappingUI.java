/*******************************************************************************
 * Copyright (C) 2011 Lars Grammel 
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
package org.thechiselgroup.choosel.core.client.views.resolvers;

import java.util.ArrayList;

import org.thechiselgroup.choosel.core.client.ui.widget.listbox.ExtendedListBox;
import org.thechiselgroup.choosel.core.client.ui.widget.listbox.ListBoxControl;
import org.thechiselgroup.choosel.core.client.util.ConversionException;
import org.thechiselgroup.choosel.core.client.util.transform.Transformer;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * This class combines the information from the SlotMappingUIModel to draw a UI
 * used to configure the slotMapping. It also keeps track of when the uiModel
 * changes and fires events accordingly
 */
public class SlotMappingUI extends FlowPanel {

    private SlotMappingUIModel uiModel;

    ViewItemValueResolverUIControllerFactoryProvider uiProvider;

    private final ListBoxControl<ViewItemValueResolverFactory> resolverFactorySelector = new ListBoxControl<ViewItemValueResolverFactory>(
            new ExtendedListBox(false),
            new Transformer<ViewItemValueResolverFactory, String>() {
                @Override
                public String transform(ViewItemValueResolverFactory value)
                        throws ConversionException {
                    return value.getLabel();
                }
            });

    // This handler knows when the current factory that is set has changed
    private ChangeHandler factoryChangeHandler = new ChangeHandler() {
        @Override
        public void onChange(ChangeEvent event) {
            ViewItemValueResolverFactory factory = resolverFactorySelector
                    .getSelectedValue();

            // if the factory that is selected is null, don't try and make that
            // work
            if (factory == null) {
                return;
            }

            // sanity check to ensure that the factory has changed
            // since the list should only be populated by valid factories, we
            // know that this resolver factory can create a resolver that will
            // work
            if (!uiModel.getCurrentResolver().getResolverId()
                    .equals(factory.getId())) {
                // the set the current resolver to one created by the
                // factory
                uiModel.setCurrentResolverByFactoryID(factory.getId());
            }
        }
    };

    private ViewItemValueResolverUIController resolverUIController;

    public SlotMappingUI(SlotMappingUIModel uiModel,
            ViewItemValueResolverUIControllerFactoryProvider uiProvider) {
        this.uiModel = uiModel;
        this.uiProvider = uiProvider;
        ViewItemValueResolver currentResolver = uiModel.getCurrentResolver();
        this.resolverUIController = uiProvider.getFactoryById(
                currentResolver.getResolverId()).create(currentResolver);
    }

    @Override
    public Widget asWidget() {
        HorizontalPanel panel = new HorizontalPanel();
        // add in the slot label widget
        panel.add(new Label(uiModel.getSlot().getName() + " is "));

        // make sure the change handler knows when this listbox is changed
        resolverFactorySelector.setChangeHandler(factoryChangeHandler);
        ArrayList<ViewItemValueResolverFactory> factories = new ArrayList<ViewItemValueResolverFactory>();
        factories.addAll(uiModel.getResolverFactories());
        resolverFactorySelector.setValues(factories);

        // add in the two widgets
        panel.add(resolverFactorySelector.asWidget());

        Widget resolverUIWidget = resolverUIController.asWidget();
        if (resolverUIWidget != null) {
            panel.add(resolverUIWidget);
        }

        return panel;
    }

    public void updateResolverUIController() {
        this.resolverUIController = uiProvider.getFactoryById(
                uiModel.getCurrentResolver().getResolverId()).create(
                uiModel.getCurrentResolver());
    }
}
