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
package org.thechiselgroup.choosel.core.client.views.slots;

import java.util.Arrays;
import java.util.List;

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

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class NumberSlotControl extends SlotControl {

    public class CalculationResourceSetToValueResolverFactory implements
            ResourceSetToValueResolverFactory {

        private Calculation calculation;

        public CalculationResourceSetToValueResolverFactory(
                Calculation calculation) {

            this.calculation = calculation;
        }

        @Override
        public String getDescription() {
            return calculation.getDescription();
        }

        @Override
        public ResourceSetToValueResolver getResolver() {
            return new CalculationResourceSetToValueResolver(
                    propertySelector.getSelectedValue(), calculation);
        }
    }

    private VerticalPanel panel;

    private ListBoxControl<String> propertySelector;

    private ListBoxControl<ResourceSetToValueResolverFactory> resolverFactorySelector;

    private ChangeHandler changeHandler;

    private final SlotMappingConfiguration slotMappingConfiguration;

    public NumberSlotControl(Slot slot,
            final SlotMappingConfiguration slotMappingConfiguration) {

        super(slot);

        this.slotMappingConfiguration = slotMappingConfiguration;

        changeHandler = new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                ResourceSetToValueResolverFactory resolverFactory = resolverFactorySelector
                        .getSelectedValue();

                propertySelector
                        .setVisible(!(resolverFactory instanceof FixedResourceSetToValueResolverFactory));

                slotMappingConfiguration.setMapping(getSlot(),
                        resolverFactory.getResolver());
            }
        };

        ResourceSetToValueResolverFactory[] calculations = new ResourceSetToValueResolverFactory[] {
                new CalculationResourceSetToValueResolverFactory(
                        new SumCalculation()),
                new CalculationResourceSetToValueResolverFactory(
                        new AverageCalculation()),
                new CalculationResourceSetToValueResolverFactory(
                        new MinCalculation()),
                new CalculationResourceSetToValueResolverFactory(
                        new MaxCalculation()),
                new FixedResourceSetToValueResolverFactory(
                        new NumberOfResourcesResolver()),
                new FixedResourceSetToValueResolverFactory(
                        new FixedValuePropertyValueResolver(new Double(1))) };

        resolverFactorySelector = new ListBoxControl<ResourceSetToValueResolverFactory>(
                new ExtendedListBox(false),
                new Converter<ResourceSetToValueResolverFactory, String>() {
                    @Override
                    public String convert(
                            ResourceSetToValueResolverFactory value)
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
            ResourceSetToValueResolver resolver = slotMappingConfiguration
                    .getResolver(getSlot());

            // TODO generic interface for resolvers that require property
            if (resolver instanceof CalculationResourceSetToValueResolver) {
                String property = ((CalculationResourceSetToValueResolver) resolver)
                        .getProperty();
                propertySelector.setSelectedValue(property);
            }
        }
    }
}