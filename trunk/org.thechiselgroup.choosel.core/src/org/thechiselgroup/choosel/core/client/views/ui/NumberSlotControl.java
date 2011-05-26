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

import java.util.Arrays;
import java.util.List;

import org.thechiselgroup.choosel.core.client.resources.DataType;
import org.thechiselgroup.choosel.core.client.ui.widget.listbox.ExtendedListBox;
import org.thechiselgroup.choosel.core.client.ui.widget.listbox.ListBoxControl;
import org.thechiselgroup.choosel.core.client.util.ConversionException;
import org.thechiselgroup.choosel.core.client.util.Converter;
import org.thechiselgroup.choosel.core.client.util.NullConverter;
import org.thechiselgroup.choosel.core.client.util.math.AverageCalculation;
import org.thechiselgroup.choosel.core.client.util.math.Calculation;
import org.thechiselgroup.choosel.core.client.util.math.MaxCalculation;
import org.thechiselgroup.choosel.core.client.util.math.MinCalculation;
import org.thechiselgroup.choosel.core.client.util.math.SumCalculation;
import org.thechiselgroup.choosel.core.client.views.model.Slot;
import org.thechiselgroup.choosel.core.client.views.model.SlotMappingConfiguration;
import org.thechiselgroup.choosel.core.client.views.resolvers.CalculationResolver;
import org.thechiselgroup.choosel.core.client.views.resolvers.FixedValueResolver;
import org.thechiselgroup.choosel.core.client.views.resolvers.FixedValueResolverFactory;
import org.thechiselgroup.choosel.core.client.views.resolvers.ResourceCountResolver;
import org.thechiselgroup.choosel.core.client.views.resolvers.ViewItemResolverFactory;
import org.thechiselgroup.choosel.core.client.views.resolvers.ViewItemValueResolver;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class NumberSlotControl extends SlotControl {

    public class CalculationResolverFactory implements ViewItemResolverFactory {

        private Calculation calculation;

        public CalculationResolverFactory(Calculation calculation) {

            this.calculation = calculation;
        }

        @Override
        public String getDescription() {
            return calculation.getDescription();
        }

        @Override
        public ViewItemValueResolver getResolver() {
            return new CalculationResolver(propertySelector.getSelectedValue(),
                    calculation);
        }
    }

    private VerticalPanel panel;

    private ListBoxControl<String> propertySelector;

    private ListBoxControl<ViewItemResolverFactory> resolverFactorySelector;

    private ChangeHandler changeHandler;

    private final SlotMappingConfiguration slotMappingConfiguration;

    public NumberSlotControl(Slot slot,
            final SlotMappingConfiguration slotMappingConfiguration) {

        super(slot);

        this.slotMappingConfiguration = slotMappingConfiguration;

        changeHandler = new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                ViewItemResolverFactory resolverFactory = resolverFactorySelector
                        .getSelectedValue();

                propertySelector
                        .setVisible(!(resolverFactory instanceof FixedValueResolverFactory));

                slotMappingConfiguration.setResolver(getSlot(),
                        resolverFactory.getResolver());
            }
        };

        ViewItemResolverFactory[] calculations = new ViewItemResolverFactory[] {
                new FixedValueResolverFactory(new ResourceCountResolver()),
                new CalculationResolverFactory(new SumCalculation()),
                new CalculationResolverFactory(new AverageCalculation()),
                new CalculationResolverFactory(new MinCalculation()),
                new CalculationResolverFactory(new MaxCalculation()),
                new FixedValueResolverFactory(new FixedValueResolver(
                        new Double(1), DataType.NUMBER)) };

        resolverFactorySelector = new ListBoxControl<ViewItemResolverFactory>(
                new ExtendedListBox(false),
                new Converter<ViewItemResolverFactory, String>() {
                    @Override
                    public String convert(ViewItemResolverFactory value)
                            throws ConversionException {
                        return value.getDescription();
                    }
                });
        resolverFactorySelector.setValues(Arrays.asList(calculations));
        resolverFactorySelector.setSelectedValue(calculations[0]);
        resolverFactorySelector.setChangeHandler(changeHandler);

        propertySelector = new ListBoxControl<String>(
                new ExtendedListBox(false), new NullConverter<String>());
        propertySelector.setChangeHandler(changeHandler);

        panel = new VerticalPanel();
        panel.add(resolverFactorySelector.asWidget());
        panel.add(propertySelector.asWidget());
    }

    @Override
    public Widget asWidget() {
        return panel;
    }

    @Override
    public void updateOptions(List<String> properties) {
        propertySelector.setValues(properties);

        if (propertySelector.getSelectedValue() == null) {
            ViewItemValueResolver resolver = slotMappingConfiguration
                    .getResolver(getSlot());

            // TODO generic interface for resolvers that require property
            if (resolver instanceof CalculationResolver) {
                String property = ((CalculationResolver) resolver)
                        .getProperty();
                propertySelector.setSelectedValue(property);
            }
        }
    }
}