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
import org.thechiselgroup.choosel.client.util.CollectionUtils;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class NumberSlotControl extends SlotControl {

    private VerticalPanel panel;

    private ListBox slotPropertyMappingBox;

    private ResourceItemValueResolver resolver;

    private ViewContentDisplay contentDisplay;

    public NumberSlotControl(Slot slot, ResourceItemValueResolver resolver,
            ViewContentDisplay contentDisplay) {

        super(slot);
        this.resolver = resolver;
        this.contentDisplay = contentDisplay;
    }

    @Override
    public Widget asWidget() {
        return panel;
    }

    @Override
    public void init() {
        Calculation[] calculations = new Calculation[] { new SumCalculation(),
                new CountCalculation(), new AverageCalculation(),
                new MinCalculation(), new MaxCalculation() };

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

        slotPropertyMappingBox = new ListBox(false);
        slotPropertyMappingBox.setVisibleItemCount(1);

        ChangeHandler handler = new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                String propertyName = slotPropertyMappingBox
                        .getValue(slotPropertyMappingBox.getSelectedIndex());

                String calculationString = calculationBox
                        .getValue(calculationBox.getSelectedIndex());
                Calculation calculation = calculationMap.get(calculationString);

                resolver.put(getSlot(),
                        new CalculationResourceSetToValueResolver(propertyName,
                                calculation));

                contentDisplay.update(Collections.<ResourceItem> emptySet(),
                        Collections.<ResourceItem> emptySet(),
                        Collections.<ResourceItem> emptySet(),
                        CollectionUtils.toSet(getSlot()));
            }
        };

        slotPropertyMappingBox.addChangeHandler(handler);
        calculationBox.addChangeHandler(handler);

        panel = new VerticalPanel();
        panel.add(calculationBox);
        panel.add(slotPropertyMappingBox);
    }

    @Override
    public void updateOptions(List<String> properties) {
        for (String property : properties) {
            slotPropertyMappingBox.addItem(property, property);
        }
    }
}