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
package org.thechiselgroup.choosel.core.client.views.model;

import java.util.Map.Entry;

import org.thechiselgroup.choosel.core.client.persistence.Memento;
import org.thechiselgroup.choosel.core.client.persistence.Persistable;
import org.thechiselgroup.choosel.core.client.persistence.PersistableRestorationService;
import org.thechiselgroup.choosel.core.client.resources.persistence.ResourceSetAccessor;
import org.thechiselgroup.choosel.core.client.resources.persistence.ResourceSetCollector;
import org.thechiselgroup.choosel.core.client.util.math.AverageCalculation;
import org.thechiselgroup.choosel.core.client.util.math.Calculation;
import org.thechiselgroup.choosel.core.client.util.math.MaxCalculation;
import org.thechiselgroup.choosel.core.client.util.math.MinCalculation;
import org.thechiselgroup.choosel.core.client.util.math.SumCalculation;
import org.thechiselgroup.choosel.core.client.views.model.ViewItem.Subset;
import org.thechiselgroup.choosel.core.client.views.resolvers.CalculationResolver;
import org.thechiselgroup.choosel.core.client.views.resolvers.FirstResourcePropertyResolver;
import org.thechiselgroup.choosel.core.client.views.resolvers.ViewItemValueResolver;

public class SlotMappingConfigurationPersistableAdapter implements Persistable {

    private static final String MEMENTO_KEY_CALCULATION_TYPE = "calculationType";

    private static final String MEMENTO_VALUE_CALCULATION = "calculation";

    private static final String MEMENTO_VALUE_FIRST_RESOURCE_PROPERTY = "first-resource-property";

    private static final String MEMENTO_KEY_PROPERTY = "property";

    private static final String MEMENTO_KEY_TYPE = "type";

    private SlotMappingConfigurationInterface slotMappingConfiguration;

    public SlotMappingConfigurationPersistableAdapter(
            SlotMappingConfigurationInterface slotMappingConfiguration) {
        this.slotMappingConfiguration = slotMappingConfiguration;
    }

    @Override
    public void restore(Memento memento,
            PersistableRestorationService restorationService,
            ResourceSetAccessor accessor) {

        for (Entry<String, Memento> entry : memento.getChildren().entrySet()) {
            String slotId = entry.getKey();
            Memento child = entry.getValue();

            Slot slot = slotMappingConfiguration.getSlotById(slotId);

            if (child.getFactoryId() == null) {
                String value = (String) child.getValue(MEMENTO_KEY_TYPE);
                if (MEMENTO_VALUE_FIRST_RESOURCE_PROPERTY.equals(value)) {
                    String property = (String) child
                            .getValue(MEMENTO_KEY_PROPERTY);

                    // XXX correct id
                    slotMappingConfiguration.setResolver(
                            slot,
                            new FirstResourcePropertyResolver(property, slot
                                    .getDataType()));
                } else if (MEMENTO_VALUE_CALCULATION.equals(value)) {
                    String property = (String) child
                            .getValue(MEMENTO_KEY_PROPERTY);
                    String calculationType = (String) child
                            .getValue(MEMENTO_KEY_CALCULATION_TYPE);

                    if ("min".equals(calculationType)) {
                        slotMappingConfiguration.setResolver(slot,
                                new CalculationResolver(property, Subset.ALL,
                                        new MinCalculation()));
                    } else if ("max".equals(calculationType)) {
                        slotMappingConfiguration.setResolver(slot,
                                new CalculationResolver(property, Subset.ALL,
                                        new MaxCalculation()));
                    } else if ("sum".equals(calculationType)) {
                        slotMappingConfiguration.setResolver(slot,
                                new CalculationResolver(property, Subset.ALL,
                                        new SumCalculation()));
                    } else if ("average".equals(calculationType)) {
                        slotMappingConfiguration.setResolver(slot,
                                new CalculationResolver(property, Subset.ALL,
                                        new AverageCalculation()));
                    }
                }
            } else {
                slotMappingConfiguration.setResolver(slot,
                        (ViewItemValueResolver) restorationService
                                .restoreFromMemento(child, accessor));
            }
        }
    }

    @Override
    public Memento save(ResourceSetCollector resourceSetCollector) {
        Memento memento = new Memento();

        Slot[] slots = slotMappingConfiguration.getSlots();
        for (Slot slot : slots) {
            if (slotMappingConfiguration.isConfigured(slot)) {
                ViewItemValueResolver resolver = slotMappingConfiguration
                        .getResolver(slot);

                Memento child = new Memento();

                if (resolver instanceof FirstResourcePropertyResolver) {
                    child.setValue(MEMENTO_KEY_TYPE,
                            MEMENTO_VALUE_FIRST_RESOURCE_PROPERTY);
                    child.setValue(MEMENTO_KEY_PROPERTY,
                            ((FirstResourcePropertyResolver) resolver)
                                    .getProperty());
                } else if (resolver instanceof CalculationResolver) {
                    child.setValue(MEMENTO_KEY_TYPE, MEMENTO_VALUE_CALCULATION);

                    Calculation calculation = ((CalculationResolver) resolver)
                            .getCalculation();
                    child.setValue(MEMENTO_KEY_PROPERTY,
                            ((CalculationResolver) resolver).getProperty());

                    if (calculation instanceof SumCalculation) {
                        child.setValue(MEMENTO_KEY_CALCULATION_TYPE, "sum");
                    } else if (calculation instanceof AverageCalculation) {
                        child.setValue(MEMENTO_KEY_CALCULATION_TYPE, "average");
                    } else if (calculation instanceof MinCalculation) {
                        child.setValue(MEMENTO_KEY_CALCULATION_TYPE, "min");
                    } else if (calculation instanceof MaxCalculation) {
                        child.setValue(MEMENTO_KEY_CALCULATION_TYPE, "max");
                    }
                } else if (resolver instanceof Persistable) {
                    child = ((Persistable) resolver).save(resourceSetCollector);
                }

                memento.addChild(slot.getId(), child);
            }
        }

        return memento;
    }

}